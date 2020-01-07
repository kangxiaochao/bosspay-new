package com.hyfd.deal.Bill;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class SaiNongBillDeal implements BaseDeal
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
            String mobile = (String)order.get("phone");// 手机号
            double fee = (double) order.get("fee");//金额，以元为单位
            String chargeCash =String.valueOf((int)(fee*1));
            String chargeType = "0";
            String orderId =
                ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + mobile + GenerateData.getIntData(9, 2);
            map.put("orderId", orderId);
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充 值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String appId = paramMap.get("appId");
            String merchantKey = paramMap.get("merchantKey");
            String noticeUrl = paramMap.get("noticeUrl");
            Map<String, String> result =
                charge(linkUrl, appId, merchantKey, mobile, orderId, chargeCash, chargeType, noticeUrl);
            if (result != null)
            {
                String errorCode = result.get("errorCode");// 错误码
                String errorMsg = result.get("errorMsg");// 错误信息
                map.put("resultCode", errorCode + ":" + errorMsg);
                if ("0000".equals(errorCode))
                {
                    String bid = result.get("bid");
                    map.put("providerOrderId", bid);
                    flag = 1;
                }
                else
                {
                    flag = 0;
                }
            }
        }
        catch (Exception e)
        {
            log.error("赛浓充值方法出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
    
    // 话费充值
    public Map<String, String> charge(String apiUrl, String customId, String customKey, String mobile, String orderId,
        String chargeCash, String chargeType, String returnUrl)
    {
        // 接口标识
        String act = "charge";
        String sign = md5(act + customId + orderId + mobile + chargeCash + chargeType + customKey, 32);
        // 拼接参数
        Map<String, String> pMap = new TreeMap<String, String>();
        pMap.put("act", act);
        pMap.put("customId", customId);
        pMap.put("orderId", orderId);
        pMap.put("mobile", mobile);
        pMap.put("chargeCash", chargeCash);
        pMap.put("chargeType", chargeType);
        pMap.put("returnUrl", returnUrl);
        pMap.put("sign", sign);
        
        // 请求接口获取数据
        String res = "";
        try
        {
            res = ToolHttp.get(false, getUrl(apiUrl, pMap));
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            res = "{\"responseHeader\":{\"resultCode\":\"999999\",\"resultMessage\":\"系统异常\"}}";
        }
        return splitResult(res);
    }
    
    // 将返回的数据进行分割
    public Map<String, String> splitResult(String result)
    {
        Map<String, String> map = new HashMap<String, String>();
        String[] s = result.split("&");
        for (String str : s)
        {
            String[] sp = str.split("=");
            if(sp.length==2){
            	map.put(sp[0], sp[1]);
            }
            
        }
        return map;
    }
    
    /**
     * md5加密
     * 
     * @param strSrc 加密原串
     * @return 加密后的值
     * @throws Exception
     */
    public String md5(String strSrc, int len)
    {
        String result = "";
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            System.out.println(strSrc);
            md5.update(strSrc.getBytes("GB2312"));
            byte[] temp;
            temp = md5.digest();
            for (int i = 0; i < temp.length; i++)
            {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("NoSuchAlgorithmException");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.substring(0, len);
    }
    
    /**
     * 拼接Get请求参数
     * 
     * @param url
     * @param params
     * @return
     */
    public String getUrl(String url, Map<String, String> params)
    {
        // 添加url参数
        if (params != null)
        {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext())
            {
                String key = it.next();
                String value = "";
                if (params.get(key) != null)
                {
                    value = params.get(key).toString();
                }
                
                if (sb == null)
                {
                    sb = new StringBuffer();
                    sb.append("?");
                }
                else
                {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        
        System.out.println(url);
        return url;
    }
}
