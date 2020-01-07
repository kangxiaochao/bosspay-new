package com.hyfd.deal.Flow;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XiChengAESUtils;
import com.hyfd.common.utils.XiChengMD5Utils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * Create Date: 2017年6月20日 下午2:45:42
 * 
 * @version: V3.0.0
 * @author: CXJ
 */
public class XiChengFlowDeal implements BaseDeal
{
    Logger log = Logger.getLogger(getClass());
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        
        try
        {
            // 手机号
            String phoneNumber = (String)order.get("phone");
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids =
                    ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNumber + GenerateData.getIntData(9, 2);
            }
            map.put("orderId", curids);
            // 账号
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String protocolId = paramMap.get("protocolId");// 协议ID
            String partyId = paramMap.get("partyId");// 身份ID
            String securitKey = paramMap.get("securitKey");// 加密key
            String notifyUrl = paramMap.get("notifyUrl");// 订购结果回调地址
            String orderTime = ToolDateTime.format(new Date(), "yyyy-MM-dd HH:mm:ss");// 订单时间
            String time = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS");// 发起请求的时间
            // 套餐
            String spec = new Double(order.get("value") + "").intValue() + "";
            String providerId = order.get("providerId") + "";
            // 产品ID;
            String prodId = getProdId(spec, providerId);
            
            // 组装包体
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("protocolId", protocolId);
            jsonBody.put("orderId", curids);
            jsonBody.put("orderTime", orderTime);
            jsonBody.put("prodId", prodId);
            jsonBody.put("phoneNumber", phoneNumber);
            jsonBody.put("notifyUrl", notifyUrl);
            String data = XiChengAESUtils.encrypt(jsonBody.toString(), securitKey);
            JSONObject json = new JSONObject();
            String sign = XiChengMD5Utils.getSignAndMD5(partyId, data, time);
            json.put("partyId", partyId);
            json.put("time", time);
            json.put("data", data);
            json.put("sign", sign);
            
            String contentType = "application/x-www-form-urlencoded";
            // post请求
            String result = "";// ToolHttp.post(false, linkUrl, json.toString(), contentType);
            // 判断是否成功
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                
                String status = jObject.getString("status");
                String resultCode = jObject.getString("resultCode");
                String resultDesc = jObject.getString("resultDesc");
                map.put("resultCode", resultCode + ":" + resultDesc);
                // code=0为成功
                if ("1".equals(status))
                {
                    JSONObject dataJson = jObject.getJSONObject("data");
                    String channelOrderId = dataJson.getString("channelOrderId");
                    map.put("providerOrderId", channelOrderId);
                    flag = 1;
                }
                else if ("0".equals(status))
                {
                    flag = 0;
                }
            }
            else
            {
                flag = 0;
            }
        }
        catch (ConnectTimeoutException | SocketTimeoutException e)
        {// 请求、响应超时
            flag = -1;
        }
        catch (Exception e)
        {
            log.error("西城流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    public static String getProdId(String spec, String providerId)
    {
        if ("0000000001".equals(providerId))
        {
            if (spec.length() == 2)
            {
                spec = "14000" + spec;
            }
            else if (spec.length() == 3)
            {
                spec = "1400" + spec;
            }
            else if (spec.length() == 4)
            {
                spec = "140" + spec;
            }
            else if (spec.length() == 5)
            {
                spec = "14" + spec;
            }
        }
        else if ("0000000002".equals(providerId))
        {
            if (spec.length() == 2)
            {
                spec = "24000" + spec;
            }
            else if (spec.length() == 3)
            {
                spec = "2400" + spec;
            }
            else if (spec.length() == 4)
            {
                spec = "240" + spec;
            }
            else if (spec.length() == 5)
            {
                spec = "24" + spec;
            }
        }
        else if ("0000000003".equals(providerId))
        {
            if (spec.length() == 2)
            {
                spec = "34000" + spec;
            }
            else if (spec.length() == 3)
            {
                spec = "3400" + spec;
            }
            else if (spec.length() == 4)
            {
                spec = "340" + spec;
            }
            else if (spec.length() == 5)
            {
                spec = "34" + spec;
            }
        }
        return spec;
    }
}
