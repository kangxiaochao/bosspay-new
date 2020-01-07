package com.hyfd.deal.Flow;

import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * zhitong单号码流量充值接口
 * 
 * @author cxj
 *
 */
public class YinTongFlowDeal implements BaseDeal
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
            // 版本号
            String Version = "1.0";
            // 签名方法
            int EncryptMode = 1;
            // 手机号
            String ChargeAccount = (String)order.get("phone");
            // 时间戳
            int Timestamp = Integer.parseInt(ToolDateTime.format(new Date(), "yyyyMMddHHmmss"));
            // int Timestamp = Integer.parseInt(TimestampStr);
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "".equals(curids))
            {
                curids =
                    ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + ChargeAccount
                        + GenerateData.getIntData(9, 4);
            }
            map.put("orderId", curids);
            
            // 账号
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String MerchantId = paramMap.get("MerchantId");// 账户名
            String Key = paramMap.get("Key");// 平台的appKey
            String NotifyUrl = paramMap.get("NotifyUrl");// 平台回调地址
            String providerId = order.get("providerId") + "";// 获取运营商
            if ("0000000001".equals(providerId))
            {
                providerId = "001";
            }
            else if ("0000000002".equals(providerId))
            {
                providerId = "002";
            }
            else if ("0000000003".equals(providerId))
            {
                providerId = "003";
            }
            // 产品代码
            String ProductCode = "200000000" + providerId + "3001";// 后项1+归属地4+流量区域4+运营商3+时间单位1+过去时间3
            // 套餐
            String spec = new Double(order.get("value") + "").intValue() + "";
            // 购买数量(单位MB)
            int Amount = Integer.parseInt(spec);
            // 计算签名
            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("MerchantId", MerchantId);
            paraMap.put("MerOrderNo", curids);
            paraMap.put("ProductCode", ProductCode);
            paraMap.put("ChargeAccount", ChargeAccount);
            paraMap.put("Amount", String.valueOf(Amount));
            paraMap.put("NotifyUrl", NotifyUrl);
            paraMap.put("Version", Version);
            paraMap.put("Timestamp", String.valueOf(Timestamp));
            paraMap.put("EncryptMode", String.valueOf(EncryptMode));
            String md5Str = formatUrlMap(paraMap, true, true);
            md5Str += "&Key=" + Key;
            String sign = MD5.ToMD5(md5Str);
            JSONObject json = new JSONObject();
            json.put("MerchantId", MerchantId);
            json.put("MerOrderNo", curids);
            json.put("ProductCode", MerchantId);
            json.put("ChargeAccount", ChargeAccount);
            json.put("Amount", Amount);
            json.put("NotifyUrl", NotifyUrl);
            json.put("Version", Version);
            json.put("Timestamp", Timestamp);
            json.put("EncryptMode", EncryptMode);
            json.put("Sign", sign);
            String contentType = "application/x-www-form-urlencoded";
            // post请求
            String result = ToolHttp.post(false, linkUrl, json.toString(), contentType);
            // 判断是否成功
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                String Code = jObject.getString("Code");
                String CodeDesc = jObject.getString("CodeDesc");
                map.put("resultCode", Code + ":" + CodeDesc);
                if ("0000".equals(Code))
                {
                    String OrderId = jObject.getString("OrderId ");
                    // String MerOrderNo = jObject.getString("MerOrderNo");
                    int OrderStatus = jObject.getIntValue("OrderStatus");
                    if (1 == OrderStatus)
                    {
                        map.put("providerOrderId", OrderId);
                        flag = 3;
                    }
                    else if (2 == OrderStatus)
                    {
                        map.put("providerOrderId", OrderId);
                        flag = 4;
                    }
                    else if (0 == OrderStatus)
                    {
                        map.put("providerOrderId", OrderId);
                        flag = 1;
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("银通指通流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
    
    /**
     * 
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     * 
     * @param paraMap 要排序的Map对象
     * @param urlEncode 是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower)
    {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try
        {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()
            {
                
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)
                {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds)
            {
                if (StringUtils.isNotBlank(item.getKey()))
                {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode)
                    {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower)
                    {
                        buf.append(key + "=" + val);
                    }
                    else
                    {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
                
            }
            buff = buf.toString();
            if (buff.isEmpty() == false)
            {
                buff = buff.substring(0, buff.length() - 1);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return buff;
    }
    
    public static void main(String[] args)
    {
        String a = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS");
        // try
        // {
        // Thread.sleep(358);
        // }
        // catch (InterruptedException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        String b = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS");
        System.out.println(Long.parseLong(b) - Long.parseLong(a));
    }
}
