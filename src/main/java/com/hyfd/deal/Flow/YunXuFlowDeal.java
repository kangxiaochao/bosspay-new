package com.hyfd.deal.Flow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class YunXuFlowDeal implements BaseDeal
{
    
    static Logger log = Logger.getLogger(YunXuFlowDeal.class);
    
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phone = order.get("phone") + "";// 手机号
            String spec = new Double(Double.parseDouble(order.get("value") + "")).intValue() + "";// 充值额
            // 平台订单号
            String orderNo = order.get("orderId") + "";
            if (null == orderNo || "".equals(orderNo) || "null".equals(orderNo))
            {
                orderNo = ToolDateTime.format(new Date(), "yyyyMMddHHmmssS") + GenerateData.getIntData(9, 7);// 平台订单号
            }
            map.put("orderId", orderNo);
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String username = paramMap.get("username");// 登录账户
            String password = paramMap.get("password");// 登录密码
            String key = paramMap.get("key");// 接口密钥
            String callbackUrl = paramMap.get("callbackUrl");// 回调地址
            String result = "";// rechargeFlow(linkUrl, username, password, key, md5(orderNo, 16), orderNo, phone,
                               // spec);
            JSONObject jsonObject = JSON.parseObject(result);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            map.put("resultCode", code + ":" + msg);
            if (code.equals("000000"))
            {// 提交成功
                flag = 1;
                // JSONObject data = jsonObject.getJSONObject("data");
                // String outTrNo = data.getString("outTradeNo");
                // String sysOrderId = data.getString("sysOrderId");
                // map.put("orderId", outTrNo);
                // map.put("providerOrderId", sysOrderId);
            }
            else if (code.equals("-1"))
            {// 异常
            
            }
            else
            {
                flag = 0;
            }
        }
        catch (Exception e)
        {
            log.error("云旭流量充值出错" + e.getMessage() + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    // 充值接口
    public static String rechargeFlow(String linkUrl, String username, String password, String key, String echostr,
        String orderid, String phone, String product)
    {
        String timestamp = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
        String type = "1";
        String sign = md5(username + password + echostr + orderid + timestamp + phone + type + product + key, 32);
        // 设置请求参数
        Map<String, String> pMap = new HashMap<String, String>();
        // 用户名称
        pMap.put("username", username);
        // 用户密码
        pMap.put("password", password);
        // 用户签名
        pMap.put("echostr", echostr);
        // 订单编号
        pMap.put("orderid", orderid);
        // 时间戳
        pMap.put("timestamp", timestamp);
        // 充值号码
        pMap.put("phone", phone);
        // 充值类型
        pMap.put("type", type);
        // 产品编号
        pMap.put("product", product);
        // 签名
        pMap.put("sign", sign);
        log.error("请求参数:" + pMap);
        NameValuePair[] data =
            {new NameValuePair("username", username), new NameValuePair("password", password),
                new NameValuePair("echostr", echostr), new NameValuePair("orderid", orderid),
                new NameValuePair("timestamp", timestamp), new NameValuePair("phone", phone),
                new NameValuePair("type", type), new NameValuePair("product", product), new NameValuePair("sign", sign)// 签名认证
            };
        return post(linkUrl, data);
    }
    
    /**
     * Http请求工具类
     */
    public static String post(String url, NameValuePair[] data)
    {
        
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(url);
        httpClient.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");
        method.setRequestBody(data);
        String result = "";
        try
        {
            int ret = httpClient.executeMethod(method);
            if (ret == 200)
            {
                result = method.getResponseBodyAsString();
            }
            else
            {
                result = "{\"code\":\"-1\",\"msg\":\"请求超时," + ret + "。\"}";
            }
            
        }
        catch (HttpException e)
        {
            result = "{\"code\":\"-1\",\"msg\":\"请求超时,HttpException。\"}";
        }
        catch (IOException e)
        {
            result = "{\"code\":\"-1\",\"msg\":\"数据错误，IOException。\"}";
        }
        return result;
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
            md5.update(strSrc.getBytes("utf-8"));
            byte[] temp;
            temp = md5.digest();
            for (int i = 0; i < temp.length; i++)
            {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.substring(0, len);
    }
}
