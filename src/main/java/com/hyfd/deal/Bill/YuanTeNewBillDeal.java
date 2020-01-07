package com.hyfd.deal.Bill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.hyfd.common.utils.LanMaoSign;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.common.utils.YuanTeSign;
import com.hyfd.deal.BaseDeal;

public class YuanTeNewBillDeal implements BaseDeal
{
    
    private static Logger log = Logger.getLogger(XiangjiaofeiBillDeal.class);
    
    public static Map<String, String> rltMap = new HashMap<String, String>();
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phone = (String)order.get("phone");// 手机号
//            Double fee = (double)order.get("fee");// 金额，以元为单位
            String feeStr = order.get("fee")+"";
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            // String linkUrl = "http://120.133.2.20:10022/";//充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            
            // String apiUrl = paramMap.get("apiUrl");
            String apiKey = paramMap.get("apiKey");
            String userName = paramMap.get("userName");
            String userPwd = paramMap.get("userPwd");
            String dealerId = paramMap.get("dealerId");
            String notifyURL = paramMap.get("notifyURL");
            String systemId = paramMap.get("systemId");
            String serviceKind = paramMap.get("serviceKind");
            // String apiKey = "NEUSOFT2";
            // String userName = "TJTJZY001";
            // String userPwd ="lmyd2016";
            // String dealerId = "A100304003";
            // String notifyURL = "http://120.26.134.145:40001/rcmp/jf/orderDeal/statusBackLanMao1";
            // String systemId = "102";
            // String serviceKind = "8";
            //
            // 生成自己的id，供回调时查询数据使用
            String curids =
                systemId + ToolDateTime.format(new Date(), "yyyyMMddHHmmss")
                    + (RandomUtils.nextInt(9999999) + 10000000);
            map.put("orderId", curids);
            Map<String, String> sendmap =
                charge(linkUrl,
                    apiKey,
                    curids,
                    systemId,
                    userName,
                    userPwd,
                    dealerId,
                    phone,
                    serviceKind,
                    feeStr,
                    notifyURL);
            
            if (null != sendmap && !"".equals(sendmap))
            {// 返回值不为空
            
                String code = sendmap.get("ResultCode");// 获取状态码
                String resultMsg = sendmap.get("ResultInfo");// 获取状态信息
                map.put("resultCode", code + ":" + resultMsg);
                
                if (code.equals("0"))
                {// 提交成功
                    flag = 1;
                } else if (code.equals("-1")) {
                	flag = -1;
                }else {// 提交失败
                    flag = 0;
                }
            }
        }
        catch (Exception e)
        {
            log.error("蓝猫充值逻辑出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
        
    }
    
    // 充值接口
    public static Map<String, String> charge(String apiUrl, String apiKey, String streamNo, String systemId,
        String userName, String userPwd, String dealerId, String serviceId, String serviceKind, String payFee,
        String notifyURL)
    {
        // 受理地市,填写0
        String cityCode = "0";
        // 帐户标识，填写0
        String accountId = "0";
        // 用户标识，填写0
        String userId = "0";
        // 缴费方式30代理商缴费
        String ifContinue = "30";
        // 缴费时间YYYYMMDD hh24miss如：20150331185555
        String notifyDate = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
        // 接口类型：1异步接口 0同步接口
        String intfType = "1";
        // 充值类型：0 代理商充值
        String rechargeType = "0";
        
        String encryptInfo =
            "<EncryptInfo><StreamNo>" + streamNo + "</StreamNo><SystemId>" + systemId + "</SystemId><UserName>"
                + userName + "</UserName><UserPwd>" + userPwd + "</UserPwd>" + "<IntfType>" + intfType
                + "</IntfType><RechargeType>" + rechargeType + "</RechargeType>" + "<NotifyURL>" + notifyURL
                + "</NotifyURL><BodyInfo><accountId>" + accountId + "</accountId><cityCode>" + cityCode
                + "</cityCode><dealerId>" + dealerId + "</dealerId>" + "<ifContinue>" + ifContinue
                + "</ifContinue><notifyDate>" + notifyDate + "</notifyDate>" + "<operator>" + userName
                + "</operator><payFee>" + payFee + "</payFee><userId>" + userId + "</userId><serviceId>" + serviceId
                + "</serviceId><serviceKind>" + serviceKind + "</serviceKind></BodyInfo></EncryptInfo>";
        
        // readXmlToMap
        String sign = YuanTeSign.encryptToHex(apiKey, encryptInfo);
        String envelope =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>"
                + sign.length()
                + "</ContentLen><EncryptInfo>"
                + sign
                + "</EncryptInfo></soapenv:Body></soapenv:Envelope>";
        log.error("请求内容:" + envelope);
        String result = post(streamNo, apiUrl, envelope);
        Map<String, String> map = readXmlToMap(result);
        log.error("返回内容:" + map);
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
                
                result =
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
                        + streamNo + "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>HttpCode:" + ret
                        + "</ResultInfo></soapenv:Body></soapenv:Envelope>";
                
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
    @SuppressWarnings("unchecked")
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
    
    public static void main(String[] args)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fee", 11.01);
        map.put("phone", "12563526352");
        YuanTeNewBillDeal xingMeiBillDeal = new YuanTeNewBillDeal();
        Map<String, Object> map1 = xingMeiBillDeal.deal(map);
        System.out.println("map=" + map1.toString());
    }
}
