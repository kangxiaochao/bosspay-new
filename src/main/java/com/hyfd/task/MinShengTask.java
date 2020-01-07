package com.hyfd.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolString;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class MinShengTask
{
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(MinShengTask.class);
    
    @Scheduled(fixedDelay = 60000)
    public void queryMinShengOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        
        try
        {
            String id = "2000000013";// 民生物流通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryOrderUrl = paramMap.get("url");// 查询地址
            String appkey = paramMap.get("key");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList)
            {
            	int flag = 2;
                String phone = order.get("phone") + "";
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("interFace", "cr-Query-Recharge");
                jsonObject.put("key", appkey);
                jsonObject.put("tel", phone);
                jsonObject.put("orderNumber", orderId);
                String sign = DigestUtils.md5Hex(jsonObject.toString());
                // 查询结果
                String searchResult = post(queryOrderUrl, jsonObject.toString(), null, appkey, sign);
                JSONObject resultObject = JSONObject.parseObject(searchResult);
                String searchResultCode = resultObject.getString("ReCode");
                if ("1".equals(searchResultCode))
                {
                    String list = resultObject.getString("list");
                    JSONArray jsonList = JSON.parseArray(list);
                    int type = (int)jsonList.getJSONObject(0).get("type");
                    String resultType = String.valueOf(type);
                    if ("1".equals(resultType))
                    {// 充值成功
                        flag = 1;
                    }
                    else if ("0".equals(resultType))
                    {// 充值失败
                        flag = 0;
                    }
                    else
                    {
                        continue;
                    }
                }
                else
                {
                    continue;
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("民生查询Task出错" + e);
        }
    }
    
    public static String post(String url, String data, String contentType, String appkey, String sign)
    {
        CloseableHttpClient httpClient = null;
        try
        {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("sign", sign);
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