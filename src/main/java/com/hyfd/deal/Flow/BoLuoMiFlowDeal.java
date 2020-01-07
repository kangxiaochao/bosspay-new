package com.hyfd.deal.Flow;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.Base64Utils;
import com.hyfd.common.utils.JsonHelper;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.PublicRSA;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 菠萝蜜单号码流量充值接口
 * 
 * @author cxj
 *
 */
public class BoLuoMiFlowDeal implements BaseDeal
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
            // 手机号
            String phone = (String)order.get("phone");
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 4);
            }
            String xyOrderId = curids;// 平台订单号
            map.put("orderId", curids);
            // 套餐
            String spec = new Double(order.get("value") + "").intValue() + "";
            // 判断生成产品编号;
            int val = Integer.parseInt(spec);
            String str = "";
            if (val > 1000)
            {
                int i = (val / 1024);
                str = i + "";
                if (str.length() > 1)
                {
                    str = "0" + str;
                }
                else
                {
                    str = "00" + str;
                }
                spec = str;
            }
            else
            {
                if (val < 10)
                {
                    str = "00" + val + "";
                }
                else if (val > 99)
                {
                    str = val + "";
                }
                else
                {
                    str = "0" + val + "";
                }
                spec = str;
            }
            String ISPProductId = "";
            String providerId = order.get("providerId") + "";// 获取运营商
            // 组织产品代码
            if ("0000000001".equals(providerId))
            {
                ISPProductId = "20000" + spec;
            }
            else if ("0000000002".equals(providerId))
            {
                ISPProductId = "10000" + spec;
            }
            else if ("0000000003".equals(providerId))
            {
                ISPProductId = "30000" + spec;
            }
            // 账号
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String userKey = paramMap.get("userKey");// 账户名
            String returnUrl = paramMap.get("returnUrl");// 平台回调地址
            HashMap<String, Object> hashMmap = new HashMap<String, Object>();
            
            hashMmap.put("xyOrderId", xyOrderId);
            
            hashMmap.put("phone", phone);
            
            hashMmap.put("ISPProductId", ISPProductId);
            
            String json = JsonHelper.hashMapToJson(hashMmap);
            String rsaKey = PublicRSA.getInstance().encryptByPublicKey(json);
            String data = Base64Utils.encode(rsaKey.getBytes());
            linkUrl = linkUrl + "?userKey=" + userKey + "&data=" + data + "&returnUrl=" + returnUrl;
            // post请求
            String result = "";// ToolHttp.get(false, linkUrl);
            // 判断是否成功
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                String resultCode = jObject.getString("resultCode");
                String resultMsg = jObject.getString("resultMsg");
                String transCode = jObject.getString("transCode");
                map.put("resultCode", resultCode + ":" + resultMsg);
                if ("0".equals(resultCode))
                {
                    map.put("providerOrderId", transCode);
                    flag = 1;
                }
                else if ("1".equals(resultCode))
                {
                    // map.put("providerOrderId", transCode);
                    flag = 0;
                }
            }
        }
        catch (ConnectTimeoutException | SocketTimeoutException e)
        {// 请求、响应超时
            flag = -1;
        }
        catch (Exception e)
        {
            log.error("菠萝蜜流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
}
