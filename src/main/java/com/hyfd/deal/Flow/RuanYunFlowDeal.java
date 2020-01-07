package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class RuanYunFlowDeal implements BaseDeal
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
            String phoneNo = (String)order.get("phone");// 手机号
            String spec = (String)order.get("value");// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            String dataType = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            String scope = dataType.equals("1") ? "province" : "nation";
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String callbackUrl = (String)channel.get("callback_url");// 回调地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appId = paramMap.get("appId");// 账号
            String secretKey = paramMap.get("secretKey");// 密钥
            String openmonth = "1"; // 生效时间 1：即时生效 2：下月生效
            String validmonth = "1"; // 有效期（月） 取值范围1~12
            String whecontinue = "1"; // 到账方式 1：一次性 2：月返
            String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids =
                    DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phoneNo + GenerateData.getIntData(9, 4);// 订单号
            }
            map.put("orderId", curids);
            callbackUrl = new String(new Base64().encode(callbackUrl.getBytes()));// 对回调地址进行base64编码
            String sign =
                Sign(secretKey,
                    appId,
                    callbackUrl,
                    spec,
                    openmonth,
                    phoneNo,
                    scope,
                    timestamp,
                    curids,
                    validmonth,
                    whecontinue);
            linkUrl = linkUrl + "?";
            linkUrl += "appkey=" + appId;
            linkUrl += "&phoneno=" + phoneNo;
            linkUrl += "&flownumber=" + spec;
            linkUrl += "&scope=" + scope;
            linkUrl += "&openmonth=" + openmonth;
            linkUrl += "&validmonth=" + validmonth;
            linkUrl += "&whecontinue=" + whecontinue;
            linkUrl += "&transno=" + curids;
            linkUrl += "&backurl=" + callbackUrl;
            linkUrl += "&timestamp=" + timestamp;
            linkUrl += "&sign=" + sign;
            String orderData = "";// ToolHttp.get(false, linkUrl);
            if (orderData != null && !orderData.equals(""))
            {
                JSONObject createJsonObject = JSONObject.parseObject(orderData.toString());
                String resultCode = createJsonObject.getString("code");
                String submitbackmsg = createJsonObject.getString("msg");
                map.put("resultCode", resultCode + ":" + submitbackmsg);
                if (resultCode.equals("0000"))
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;
                    log.debug("软云[流量充值]请求:提交失败!手机号[" + phoneNo + "],流量包[" + spec + "MB]");
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("软云[流量充值]出错" + e.getMessage() + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    public String Sign(String appsecret, String appkey, String backurl, String flownumber, String openmonth,
        String phoneno, String scope, String timestamp, String transno, String validmonth, String whecontinue)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append(appsecret);
            sb.append("appkey" + appkey);
            sb.append("backurl" + backurl);
            sb.append("flownumber" + flownumber);
            sb.append("openmonth" + openmonth);
            sb.append("phoneno" + phoneno);
            sb.append("scope" + scope);
            sb.append("timestamp" + timestamp);
            sb.append("transno" + transno);
            sb.append("validmonth" + validmonth);
            sb.append("whecontinue" + whecontinue);
            sb.append(appsecret);
            return MD5.ToMD5(sb.toString()).toUpperCase();
        }
        catch (Exception e)
        {
            log.error("ruan yun |getOrderSign" + e.getMessage());
            return "";
        }
    }
    
}
