package com.hyfd.task;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class SaiNonegBillTask
{
    
    Logger log = Logger.getLogger(getClass());
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Scheduled(fixedDelay = 60000)
    public void querySaiNongOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            String providerId = "2000000007";//
            Map<String, Object> channel = providerPhysicalChannelDao.selectByProviderId(providerId);// 获取通道
            String defaultParameter = (String)channel.get("default_parameter");// 参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String appId = paramMap.get("appId");
            String appKey = paramMap.get("merchantKey");
            String queryAction = paramMap.get("queryAction");
            // 查询订单中属于指通的折扣
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", providerId);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);// 查询出所有尚通的充值中的订单
            for (int i = 0, size = orderList.size(); i < size; i++)
            {
            	 int flag = 2;
                Map<String, Object> order = orderList.get(i);
                String orderId = order.get("orderId") + "";// 平台订单号
                map.put("orderId", orderId);
                Map<String, String> result = search(linkUrl, appId, appKey, orderId);
                // 判断是否成功
                if (result != null)
                {
                    String errorCode = result.get("errorCode");
                    String errorMsg = result.get("errorMsg");
                    map.put("resultCode", errorCode + ":" + errorMsg);
                    if ("0000".equals(errorCode))
                    {
                        String status = result.get("status");
                        if ("16".equals(status))
                        {
                            flag = 1;
                        }
                        
                        else if ("6".equals(status))
                        {
                            continue;
                        }
                        else
                        {
                            flag = 0;
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("赛浓话费查询出错" + e);
        }
    }
    
    // 话费充值
    public static Map<String, String> search(String apiUrl, String customId, String customKey, String orderId)
    {
        // 接口标识
        String act = "search";
        String sign = md5(act + customId + orderId + customKey, 32);
        // 拼接参数
        Map<String, String> pMap = new TreeMap<String, String>();
        pMap.put("act", act);
        pMap.put("customId", customId);
        pMap.put("orderId", orderId);
        pMap.put("sign", sign);
        
        // 请求接口获取数据
        String res = "";
        try
        {
            res = ToolHttp.get(false, getUrl(apiUrl, pMap));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            res = "errorCode=9999&chargeMoney=0.00&bid=&errorMsg=" + e.getMessage();
        }
        return splitResult(res);
    }
    
    // 将返回的数据进行分割
    public static Map<String, String> splitResult(String result)
    {
        Map<String, String> map = new HashMap<String, String>();
        String[] s = result.split("&");
        for (String str : s)
        {
            String[] sp = str.split("=");
            if(sp.length==2){
            	map.put(sp[0], sp[1]);
            }
        }
        return map;
    }
    
    /**
     * md5加密
     * 
     * @param strSrc 加密原串
     * @return 加密后的值
     * @throws Exception
     */
    public static String md5(String strSrc, int len)
    {
        String result = "";
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            System.out.println(strSrc);
            md5.update(strSrc.getBytes("GB2312"));
            byte[] temp;
            temp = md5.digest();
            for (int i = 0; i < temp.length; i++)
            {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("NoSuchAlgorithmException");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.substring(0, len);
    }
    
    /**
     * 拼接Get请求参数
     * 
     * @param url
     * @param params
     * @return
     */
    public static String getUrl(String url, Map<String, String> params)
    {
        // 添加url参数
        if (params != null)
        {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext())
            {
                String key = it.next();
                String value = "";
                if (params.get(key) != null)
                {
                    value = params.get(key).toString();
                }
                
                if (sb == null)
                {
                    sb = new StringBuffer();
                    sb.append("?");
                }
                else
                {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        
        System.out.println(url);
        return url;
    }
}
