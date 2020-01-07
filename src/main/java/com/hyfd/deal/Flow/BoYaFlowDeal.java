package com.hyfd.deal.Flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class BoYaFlowDeal implements BaseDeal
{
    
    private static Logger log = Logger.getLogger(BoYaFlowDeal.class);
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phone = order.get("phone") + "";// 手机号
            String spec = new Double(Double.parseDouble(order.get("value") + "")).intValue() + "";// 充值大小
            String providerId = order.get("providerId") + "";
            // 平台订单号设置
            String orderNo = order.get("orderId") + "";
            if (null == orderNo || "".equals(orderNo) || "null".equals(orderNo))
            {
                orderNo = ToolDateTime.format(new Date(), "yyyyMMddHHmmssS") + GenerateData.getIntData(9, 7);
            }
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryUrl = paramMap.get("queryUrl");
            String key = paramMap.get("key");
            String appId = paramMap.get("appId");
            String productNo = queryProductNo(queryUrl, appId, key, spec, phone, providerId);
            String timestamp = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
            String notifyUrl = paramMap.get("notifyUrl") + "";// 回调地址
            if (!productNo.equals("") && productNo != null)
            {// 产品号不为空
                String paramStr =
                    "appId=" + appId + "&notifyUrl=" + notifyUrl + "&outTradeNo=" + orderNo + "&phone=" + phone
                        + "&productNo=" + productNo + "&timestamp=" + timestamp;
                String sign = MD5.ToMD5(paramStr + "&secretKey=" + key).toUpperCase();
                paramStr = paramStr + "&sign=" + sign;
                String result = "";// ToolHttp.post(false, linkUrl, paramStr, null);
                if (!result.equals("") && result != null)
                {
                    JSONObject json = JSONObject.parseObject(result);
                    String code = json.getString("code");
                    String msg = json.getString("msg");
                    map.put("resultCode", code + ":" + msg);
                    if (code.equals("200"))
                    {// 请求成功
                        flag = 1;
                        JSONObject data = json.getJSONObject("data");
                        String outTrNo = data.getString("outTradeNo");
                        String sysOrderId = data.getString("sysOrderId");
                        map.put("orderId", outTrNo);
                        map.put("providerOrderId", sysOrderId);
                    }
                    else
                    {
                        flag = 0;
                    }
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("深圳博亚流量充值接口出错" + e);
        }
        map.put("status", 1);
        return map;
    }
    
    public static String queryProductNo(String url, String appId, String key, String orderSize, String phone,
        String providerId)
    {
        String signStr = "appId=" + appId + "&secretKey=" + key;
        String sign = MD5.ToMD5(signStr).toUpperCase();
        String paramStr = "appId=" + appId + "&sign=" + sign;
        String result = "";
        try
        {
            result = ToolHttp.post(false, url, paramStr, null);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getProductCodeByOrderSize(result, orderSize, phone, providerId);
    }
    
    private static String getProductCodeByOrderSize(String json, String orderSize, String phone, String providerId)
    {
        String productNo = "";
        List<Map<String, String>> productList = getProductList(json);
        for (Map<String, String> map : productList)
        {
            String productName = (String)map.get("productName");
            productName = productName.trim();
            String Osize = "";
            String pkg = "";
            if (productName != null && !"".equals(productName))
            {
                for (int i = 0; i < productName.length(); i++)
                {
                    if (productName.charAt(i) >= 48 && productName.charAt(i) <= 57)
                    {
                        Osize += productName.charAt(i);
                    }
                    String[] split = productName.split("_");
                    pkg = split[0];
                }
                
            }
            String carrierstype = "";
            if (providerId.equals("0000000001"))
            {
                carrierstype = "移动";
            }
            else if (providerId.equals("0000000002"))
            {
                carrierstype = "联通";
            }
            else if (providerId.equals("0000000003"))
            {
                carrierstype = "电信";
            }
            
            if (Osize.equals(orderSize) && carrierstype.equals(pkg))
            {
                productNo = (String)map.get("productNo");
                break;
            }
        }
        return productNo;
    }
    
    private static List<Map<String, String>> getProductList(String json)
    {
        List<Map<String, String>> productList = new ArrayList<Map<String, String>>();
        try
        {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String code = jsonObject.get("code").toString();
            if (code.equals("200"))
            {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++)
                {
                    Map<String, String> map = new HashMap<String, String>();
                    JSONObject object = (JSONObject)JSONObject.toJSON(jsonArray.get(i));
                    map.put("productName", object.getString("productName"));
                    map.put("price", object.getString("price"));
                    map.put("productNo", object.getString("productNo"));
                    productList.add(map);
                }
            }
            return productList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("深圳博亚流量充值解析产品列表出错");
        }
        return null;
    }
    
}
