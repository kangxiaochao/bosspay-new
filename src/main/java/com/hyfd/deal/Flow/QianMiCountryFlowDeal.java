package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.domain.elife.Item;
import com.qianmi.open.api.domain.elife.OrderDetailInfo;
import com.qianmi.open.api.request.MobileFlowCreateBillRequest;
import com.qianmi.open.api.request.MobileFlowItemsList2Request;
import com.qianmi.open.api.request.RechargeBasePayBillRequest;
import com.qianmi.open.api.response.MobileFlowCreateBillResponse;
import com.qianmi.open.api.response.MobileFlowItemsList2Response;
import com.qianmi.open.api.response.RechargeBasePayBillResponse;

public class QianMiCountryFlowDeal implements BaseDeal
{
    
    private static Logger log = Logger.getLogger(QianMiCountryFlowDeal.class);
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phoneNo = (String)order.get("phone");// 手机号
            String spec = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            String dataType = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            String scope = dataType.equals("1") ? "p" : "c";
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNo + GenerateData.getIntData(9, 2);
            }
            map.put("orderId", curids);
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String token = paramMap.get("tokenC");
            String appKey = paramMap.get("appKeyC");//
            String appSecret = paramMap.get("appSecretC");// 密钥
            String pid = getFlowItemList2(linkUrl, appKey, appSecret, spec, phoneNo, token, scope);
            // String token = qianmiTokenDao.selectCurrentToken("1");//获取国内流量的最新token
            if (pid != null && !pid.equals(""))
            {// 产品不为空
             // 有产品编号时处理
                OpenClient client = new DefaultOpenClient(linkUrl, appKey, appSecret);
                MobileFlowCreateBillRequest req = new MobileFlowCreateBillRequest();
                req.setItemId(pid);
                req.setMobileNo(phoneNo);
                req.setOuterTid(curids);
                MobileFlowCreateBillResponse response = client.execute(req, token);
                String errorCode = response.getErrorCode();// 错误代码
                String msg = response.getMsg();// 提示信息
                if (response.isSuccess())
                {
                    map.put("resultCode", errorCode + ":" + msg);
                    OrderDetailInfo orderDetailInfo = response.getOrderDetailInfo();
                    String billId = orderDetailInfo.getBillId();
                    map.put("providerOrderId", billId);
                    OpenClient payClient = new DefaultOpenClient(linkUrl, appKey, appSecret);
                    RechargeBasePayBillRequest payReq = new RechargeBasePayBillRequest();
                    payReq.setBillId(billId);
                    RechargeBasePayBillResponse payResponse = payClient.execute(payReq, token);
                    String payErrorCode = payResponse.getErrorCode();
                    String payMsg = payResponse.getMsg();
                    if (payResponse.isSuccess())
                    {// 支付请求成功
                        map.put("resultCode", map.get("resultCode") + "|" + payErrorCode + ":" + payMsg);
                        if (payErrorCode != null)
                        {
                            flag = 0;
                        }
                        else
                        {
                            flag = 1;
                        }
                    }
                    else
                    {
                        map.put("resultCode", payErrorCode + ":" + payMsg);
                    }
                }
                else
                {
                    map.put("resultCode", errorCode + ":" + msg);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("千米流量充值出错" + e.getLocalizedMessage() + "|||" + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
    
    /**
     * 根据手机号与流量包获取产品id如果没有返回null
     * 
     * @param serverUrl 请求url地址
     * @param appKey
     * @param appSecret
     * @param flowPkgSize 流量包大小单位M(比如10M就传入10)
     * @param phoneNum 手机号
     * @param accessToken 访问令牌
     * @return
     */
    public static String getFlowItemList2(String serverUrl, String appKey, String appSecret, String flowPkgSize,
        String phoneNum, String accessToken, String scope)
    {
        // 产品编号
        String pid = null;
        try
        {
            OpenClient client = new DefaultOpenClient(serverUrl, appKey, appSecret);
            MobileFlowItemsList2Request req = new MobileFlowItemsList2Request();
            req.setFlow(flowPkgSize + "M"); // 流量包大小
            req.setMobileNo(phoneNum); // 手机号
            
            // 这里可以选省网跟国内
            req.setUseScope(scope); // 使用范围 p:省内通用；c:全国通用
            
            MobileFlowItemsList2Response response = client.execute(req, accessToken);
            
            if (!response.isSuccess())
            {
                log.error("未找到对应产品编号|" + flowPkgSize + "|" + phoneNum);
                return null;
            }
            List<Item> items = response.getItems();
            if (items != null && items.size() > 0)
            {
                String itemId = "";
                for (Item item : items)
                {
                    itemId = item.getItemId();
                }
                pid = itemId;
            }
            
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return pid;
    }
    
}
