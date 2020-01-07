package com.hyfd.deal.Flow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class LeMianFlowDeal implements BaseDeal
{
    private static Logger log = Logger.getLogger(LeMianFlowDeal.class);
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            // 获取订单参数
            String phoneNo = (String)order.get("phone");// 手机号
            String spec = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNo + GenerateData.getIntData(9, 4);
            }
            map.put("orderId", curids);
            // 获取物理通道信息
            Map<String, Object> channel = (Map<String, Object>)order.get("channel"); // 获取物理通道
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String userId = paramMap.get("appId").toString();
            String userName = paramMap.get("userName").toString();
            String password = paramMap.get("password").toString();
            String backurl = paramMap.get("callbackUrl").toString();
            String timestamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
            String orderUrl = paramMap.get("linkUrl").toString();
            
            password = getOrderPass(password, timestamp);
            
            // 对URL进行base编码
            backurl = new BASE64Encoder().encode(backurl.getBytes());
            
            String secret = getOrderSign(userId, userName, password, phoneNo, spec, timestamp); // 客户按照规则加密后的密文
            orderUrl += "?UserId=" + userId;
            orderUrl += "&UserName=" + userName;
            orderUrl += "&Password=" + password;
            orderUrl += "&mobile=" + phoneNo;
            orderUrl += "&flow=" + spec;
            orderUrl += "&stamp=" + timestamp;
            orderUrl += "&secret=" + secret;
            
            String orderData = "";// ToolHttp.get(false, orderUrl);
            if (orderData == null)
            {
                String myMsg = "乐免[流量充值]请求超时!手机号[" + phoneNo + "],流量包[" + spec + "MB]";
                log.error(myMsg);
            }
            else
            {
                // 解析json
                JSONObject createJsonObject = JSON.parseObject(orderData.toString());
                String status = createJsonObject.getString("status");
                String submitbackmsg = createJsonObject.getString("description");
                String providerOrderId = createJsonObject.getString("msgid");
                map.put("providerOrderId", providerOrderId);
                map.put("resultCode", status + ":" + submitbackmsg);
                
                // 充值失败处理
                if (!"1".equals(status))
                {
                    flag = 0;
                    // log.error("乐免[流量充值]请求:提交失败!手机号["+phoneNo+"],流量包["+spec+"MB]");
                }
                else
                {
                    flag = 1;
                    // 充值成功处理
                    // log.error("乐免[流量充值]请求:提交成功!手机号["+phoneNo+"],流量包["+spec+"MB]");
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("乐免[流量充值]出错" + e.getMessage() + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    public static String getOrderSign(String userId, String userName, String password, String mobile, String flow,
        String stamp)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append(userId + ",");
            sb.append(userName + ",");
            sb.append(password + ",");
            sb.append(mobile + ",");
            sb.append(flow + ",");
            sb.append(stamp);
            return DigestUtils.md5Hex(sb.toString()).toUpperCase();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getOrderPass(String password, String stamp)
    {
        try
        {
            return DigestUtils.md5Hex(password + stamp).toUpperCase();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
