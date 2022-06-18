package com.hyfd.exceptionTask;

import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.Bill.TianYinBillDeal;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class TianYinExceptionTask implements BaseTask{
    private static Logger log = Logger.getLogger(TianYinExceptionTask.class);

    @Override
    public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            String defaultParameter = (String)channelMap.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String searchUrl = paramMap.get("searchUrl") + "";// 查询地址
            String platyformId = paramMap.get("platyformId"); // 平台编号
            String orgcode = paramMap.get("orgcode"); // 机构代码
            String requestid =
                platyformId + ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + TianYinBillDeal.getFourSquece();
            String requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
            
            String upids = order.get("providerOrderId") + "";
            String orderId = order.get("orderId") + "";
            map.put("orderId", orderId);
            map.put("providerOrderId", upids);
            map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
            map.put("phone",order.get("phone"));
            map.put("fee",order.get("fee"));

            String ret = TianYinBillDeal.sendSearch(searchUrl, requestid, requesttime, orgcode, orderId);
            if (null != ret)
            {
                Map<String, Object> result = TianYinBillDeal.readXmlToMapFromCreateResponse(ret);

                String msg = result.get("RETURN_MSG").toString();
                String code = result.get("RETURN_CODE").toString();
                // String responseTime = result.get("RESPONSETIME").toString();
                String submitbackmsg = code + ":" + msg;
                map.put("resultCode", submitbackmsg);
                if ("000000".equals(code)) {// 充值成功
                    map.put("status", 1);
                } else if ("999999".equals(code)) {// 充值失败
                    map.put("status", 0);
                } else if ("888888".equals(code)) {// 无此订单信息（订单途中）
                    map.put("status", 2);
                } else {
                    map.put("status", 3);
                    map.put("resultCode", "未知的订单状态，请与上家核实后处理:"+submitbackmsg);
                }
            }else{
                map.put("status", 3);
                map.put("resultCode", "查询订单状态失败！");
            }
        } catch (Exception e) {
            log.error("天音异常订单状态话费查询Task出错" + e);
        }
        return map;
    }
    
    /**
     * Http请求工具类
     */
    public static String post(String streamNo, String url, String data)
    {
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        
        // 设置请求头对象
        StringEntity stringEntity = new StringEntity(data, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("text/xml");
        httpPost.setEntity(stringEntity);
        
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        
        String result = "";
        try
        {
            HttpResponse response = httpClient.execute(httpPost);
            int ret = response.getStatusLine().getStatusCode();
            if (ret == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    String out = EntityUtils.toString(entity, "UTF-8");
                    return out;
                }
            }
            else
            {
                result = "";
            }
        }
        catch (HttpException e)
        {
            result =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
                    + streamNo
                    + "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>HttpException</ResultInfo></soapenv:Body></soapenv:Envelope>";
            
        }
        catch (IOException e)
        {
            result =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
                    + streamNo
                    + "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>IOException</ResultInfo></soapenv:Body></soapenv:Envelope>";
        }
        return result;
    }
    
    /**
     * 将xml字符串转换成Json(给上游发送请求，有数据返回时调用)
     * 
     * @param xml 发送充值请求后，上游返回的xml
     */
    public static Map<String, String> readXmlToMap(String xml)
    {
        try
        {
            Map<String, String> map = new HashMap<String, String>();
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage reqMsg =
                msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(xml.getBytes("UTF-8")));
            reqMsg.saveChanges();
            SOAPMessage msg = reqMsg;
            @SuppressWarnings("unchecked")
            Iterator<SOAPElement> iterator = msg.getSOAPBody().getChildElements();
            while (iterator.hasNext())
            {
                SOAPElement element = (SOAPElement)iterator.next();
                map.put(element.getLocalName(), element.getValue());
            }
            return map;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}
