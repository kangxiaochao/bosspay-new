package com.hyfd.deal.Bill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.yuanteUtils.sdk.Key;
import com.hyfd.yuanteUtils.sdk.TencentConstants;
import com.hyfd.yuanteUtils.sdk.TencentSignature;
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
        log.info("================order");
        log.info(order);
        System.out.println("==============");
        try
        {
            log.info("================ordertry");
            String phone = (String)order.get("phone");// 手机号
            double fee = new Double(order.get("fee") + "");// 金额，以元为单位
            String feeStr = new Double(fee * 100).intValue() + "";// 充值金额，以分为单位

            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址

            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String dealerId = paramMap.get("dealerId");
            // 生成自己的id，供回调时查询数据使用
            String curids =  ToolDateTime.format(new Date(), "yyyyMMddHHmmss")  + (RandomUtils.nextInt(9999999) + 10000000);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serviceId",phone);
            jsonObject.put("systemId","0");
            jsonObject.put("dealerId",dealerId);
            jsonObject.put("paymentFlowNumber",curids);
            jsonObject.put("payFee",feeStr);
            jsonObject.put("operator","haobai");
            jsonObject.put("reUrl","http://114.55.26.121:9001/bosspaybill/status/YuanTe");
            String resultStr = charge(linkUrl, jsonObject.toString());
            String order_id = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + phone + ((int)(Math.random()*9000)+1000);//订单号
            map.put("orderId", order_id);//订单号
            if (null == resultStr || resultStr.equals("")) {
                // 请求超时,未获取到返回数据
                flag = -1;
                String msg = "远特话费充值,号码[" + phone + "],金额[" + fee + "(元)],请求超时,未接收到返回数据";
                map.put("resultCode", msg);
                log.error(msg);
            } else {
                JSONObject resultJson = JSONObject.parseObject(resultStr);
                String result = resultJson.getString("result");
                if ("0000".equals(result)) {
                    String info = resultJson.getString("info");
                    String flowNumber = resultJson.getString("flowNumber");
                    map.put("resultCode", info);
                    map.put("providerOrderId", flowNumber);    // 上家平台订单号
                    flag = 3;    // 提交成功  3充值成功
                }else if("E001".equals(result)||"E002".equals(result)||"E003".equals(result)){
                    String info = resultJson.getString("info");
                    String flowNumber = resultJson.getString("flowNumber");
                    map.put("resultCode", info);
                    map.put("providerOrderId", flowNumber);    // 上家平台订单号
                    flag = 1;    // 提交成功  3充值成功
                } else {
                    String errorCode = resultJson.getString("result");
                    String errorMsg = resultJson.getString("info");
                    log.info("远特订单返回异常：：："+errorCode);
                    map.put("resultCode", errorCode+":" + errorMsg);
                    flag = 0;	// 提交失败
                }
            }
        }
        catch (Exception e)

        {
            log.error("远特充值逻辑出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
        
    }

    public String charge(String linkUrl,String str){
        TencentSignature signature = new TencentSignature();
        String resultStr = "";
        try
        {
            String sign = signature.rsa256Sign(str, Key.privateKey, TencentConstants.CHARSET_GBK);
            String signTemp = URLEncoder.encode(sign, "GBK");
            System.out.println(signTemp);
            String url = linkUrl+"?1=1&sign="+signTemp;
            resultStr = ToolHttp.post(false, url, str, null);
           return resultStr;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return resultStr;
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
