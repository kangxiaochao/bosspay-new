package com.hyfd.deal.Bill;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolString;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 民生话费处理
 * 
 * @param obj
 */
public class MinShengBillDeal implements BaseDeal
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
            String phone = (String)order.get("phone");// 手机号
            double fee = (double)order.get("fee");// 金额，以元为单位
            String spec = String.valueOf((int)(fee));// 充值金额，以分为单位
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            
            String key = paramMap.get("key");
            String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
            String customerOrderId = timeStamp + GenerateData.getIntData(9, 6) + "01";// 32位订单号
            map.put("orderId", customerOrderId);
            
            // 拼装充值请求参数
            JSONObject json = new JSONObject();
            json.put("interFace", "cr-Recharge-Payment");
            json.put("key", key);
            json.put("tel", phone);
            json.put("money", spec);
            json.put("orderNumber", customerOrderId);
            json.put("time", DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String sign = DigestUtils.md5Hex(json.toJSONString());
            
            String result = post(linkUrl, json.toJSONString(), null, sign);
            if (!StringUtil.empty(result))
            {
                JSONObject resultJson = JSONObject.parseObject(result);
                String ReCode = resultJson.getString("ReCode");
                String msg = resultJson.getString("msg");
                String streamNo = resultJson.getString("streamNo");
                map.put("resultCode", ReCode + ":" + msg);
                map.put("providerOrderId", streamNo);
                if ("1".equals(ReCode))
                {
                    flag = 3;
                }
                else
                {
                    flag = 4;
                }
            }
            else
            {// 未拿到返回数据
                map.put("resultCode", "未拿到返回数据");
            }
        }
        catch (Exception e)
        {
            log.error("民生充值方法出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
    
    /**
     * 获取5位序列码
     * 
     * @return
     */
    public static int getSixSquece()
    {
        return (int)((Math.random() * 9 + 1) * 100000);
    }
    
    /**
     * 进行HttpClient post连接
     * 
     * @param isHttps 是否ssl链接
     * @param url
     * @param data
     * @param contentType
     * @return
     */
    public String post(String url, String data, String contentType, String sign)
    {
        CloseableHttpClient httpClient = null;
        try
        {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("sign", sign);
            // (name,
            // value);.addRequestHeader("Content-Type","text/html;charset=UTF-8");
            // httpPost.getParams().setParameter(HttpMethod.HTTP_CONTENT_CHARSET,
            // "UTF-8");
            if (null != data)
            {
                StringEntity stringEntity = new StringEntity(data, ToolString.encoding);
                stringEntity.setContentEncoding(ToolString.encoding);
                if (null != contentType)
                {
                    stringEntity.setContentType(contentType);
                }
                else
                {
                    stringEntity.setContentType("application/json");
                }
                httpPost.setEntity(stringEntity);
            }
            
            RequestConfig requestConfig =
                RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    String out = EntityUtils.toString(entity, ToolString.encoding);
                    return out;
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("ToolHttp.java  post()-------UnsupportedEncodingException Exception:" + e.toString());
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            log.error("ToolHttp.java  post()-------ClientProtocolException Exception:" + e.toString());
            e.printStackTrace();
            log.error("连接超时：" + url);
        }
        catch (IOException e)
        {
            log.error("ToolHttp.java  post()-------IOException Exception:" + e.toString());
            e.printStackTrace();
            log.error("IO异常:" + url);
        }
        finally
        {
            try
            {
                if (null != httpClient)
                {
                    httpClient.close();
                }
            }
            catch (IOException e)
            {
                log.error("ToolHttp.java  post()-------httpClient.close() Exception:" + e.toString());
                
            }
        }
        return null;
    }
    
}
