package com.hyfd.task;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

public class WoNiuTask
{
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(WoNiuTask.class);
    
    static Map<String, String> rltMap = new HashMap<String, String>();
    static
    {
        rltMap.put("1", "操作成功充值成功");
        rltMap.put("1004", "代理商的身份没有足够权限访问");
        rltMap.put("1009", "参数类型错误");
        rltMap.put("1005", "数字签名信息未通过验证");
        rltMap.put("1010", "未查询到此订单或此订单处理失败");
        rltMap.put("1000", "平台ID：查询订单失败!");
        rltMap.put("15007", "未查到代理商订单号");
        rltMap.put("53041", "未充值代理商订单号");
        rltMap.put("53042", "未查到充值任务");
        rltMap.put("53043", "未查到充值任务记录");
        rltMap.put("53044", "正在处理");
    }
    
    @Scheduled(fixedDelay = 60000)
    public void queryMinShengOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            String id = "0000000067";// 民生物流通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryOrderUrl = paramMap.get("queryOrderUrl");// 查询地址
            String secretKey = paramMap.get("secretKey");// "123456789";
            String sAgentPlatyformId = paramMap.get("sAgentPlatyformId");// "106";// 直充代理商ID
            String sAgentPass = paramMap.get("password");// "25F9E794323B453885F5181F1B624D0B";
            String sCardTypeId = paramMap.get("sCardTypeId");// 通过getCartTypeId方法获取，因为变动不大，也可以写成定值 3011
            String sPlayformIp = paramMap.get("ipAddress");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList)
            {
            	int flag = 2;
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                String searchResult =
                    sendSearch(queryOrderUrl, sAgentPlatyformId, sAgentPass, orderId, sPlayformIp, secretKey);
                if (null == searchResult)
                {
                    // 超时
                    flag = -1;
                    continue;
                }
                else
                {
                    Document doc = DocumentHelper.parseText(searchResult);
                    Element rootElt = doc.getRootElement(); // 获取根节点
                    Element returnEle = rootElt.element("return");
                    String result = returnEle.attributeValue("value");
                    String msg = returnEle.attributeValue("message");
                    
                    if ("1".equals(result))
                    {
                        // 成功状态处理
                        flag = 1;
                    }
                    else if ("53044".equals(result))
                    {
                        // 处理中状态 正在处理
                        continue;
                    }
                    else if (("1010".equals(result) || "1000".equals(result) || "15007".equals(result)
                        || "53041".equals(result) || "53042".equals(result) || "53043".equals(result)))
                    {
                        // 未查到代理商订单号 未充值代理商订单号
                        flag = -1;
                    }
                    else if (!rltMap.containsKey(result))
                    {
                        // 不在已知状态内的转为人工处理
                        flag = -1;
                    }
                    else
                    {
                        flag = 0;
                    }
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("蜗牛查询Task出错" + e);
        }
    }
    
    public static String sendSearch(String url, String sAgentPlatyformId, String sAgentPass, String sAgentOrderId,
        String sImprestAccountIP, String secretKey)
    {
        try
        {
            String md5Src = sAgentPlatyformId + sAgentPass + sAgentOrderId + sImprestAccountIP + secretKey;
            String sVerifyStr = DigestUtils.md5Hex(md5Src).toUpperCase();
            Service service = new Service();
            Call call = (Call)service.createCall();
            call.setTargetEndpointAddress(url);
            call.setOperationName("querySnailOrderID");// WSDL里面描述的接口名称
            // 设置参数名 // 参数名 参数类型:String 参数模式：'IN' or 'OUT'
            call.addParameter("sAgentPlatyformId", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sAgentPass", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sAgentOrderId", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sImprestAccountIP", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sVerifyStr", XMLType.XSD_STRING, ParameterMode.IN);
            // 设置返回值类型
            call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
            String result =
                (String)call.invoke(new Object[] {sAgentPlatyformId, sAgentPass, sAgentOrderId, sImprestAccountIP,
                    sVerifyStr});// 远程调用
            
            return result;
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        return "fail";
    }
}
