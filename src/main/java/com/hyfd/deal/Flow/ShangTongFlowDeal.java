package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ShangTongFlowDeal implements BaseDeal
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
            // 获取订单参数
            String phone = (String)order.get("phone");// 手机号
            String value = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");// 获取产品包
            String dataType = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            String timeStamp = System.currentTimeMillis() / 1000 + "";// 时间戳
            int range = 1;
            if (dataType.equals("2"))
            {
                range = 0;
            }
            // 平台订单号
            String customerOrderId = order.get("orderId") + "";
            if (null == customerOrderId || "".equals(customerOrderId) || "null".equals(customerOrderId))
            {
                customerOrderId =
                    DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + GenerateData.getIntData(9, 7);// 订单号
            }
            map.put("orderId", customerOrderId);
            // 获取通道参数
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取物理通道
            String linkUrl = (String)channel.get("link_url");// 订单提交地址
            String defaultParameter = (String)channel.get("default_parameter");// 参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String action = "Charge";// 操作（固定值Charge）
            String account = paramMap.get("account");// 平台申请的独立API账号
            String apiKey = paramMap.get("apiKey");// 平台的appKey
            String beforeSign =
                apiKey + "account=" + account + "&action=" + action + "&phone=" + phone + "&range=" + range + "&size="
                    + value + "&timeStamp=" + timeStamp + apiKey;// 加密前参数
            String sign = MD5.ToMD5(beforeSign);// 签名
            String param =
                "account=" + account + "&action=" + action + "&orderNo=" + customerOrderId + "&phone=" + phone
                    + "&range=" + range + "&sign=" + sign + "&size=" + value + "&timeStamp=" + timeStamp;
            String contentType = "application/x-www-form-urlencoded";
            String result = "";// ToolHttp.post(false, linkUrl, param, contentType);
            if (result != null && !result.equals(""))
            {
                JSONObject resultJson = JSONObject.parseObject(result);// 返回信息
                String respCode = resultJson.getString("respCode");// 返回状态码
                String respMsg = resultJson.getString("respMsg");// 返回状态信息
                String orderID = resultJson.getString("orderID");
                map.put("providerOrderId", orderID);
                map.put("resultCode", respCode + ":" + respMsg);
                if (respCode.equals("0000"))
                {// 提交成功
                    flag = 1;
                }
                else
                {// 提交失败
                    flag = 0;
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("尚通流量出错" + e + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
}
