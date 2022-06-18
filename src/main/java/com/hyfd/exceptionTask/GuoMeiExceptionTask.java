package com.hyfd.exceptionTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GuoMeiExceptionTask implements BaseTask {

    private static Logger log = Logger.getLogger(GuoMeiExceptionTask.class);

    @Override
    public Map<String, Object> task(Map<String, Object> order,Map<String,Object> channelMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String defaultParameter = (String)channelMap.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryUrl = paramMap.get("queryUrl"); // 取出查询接口地址
            String secretKey = paramMap.get("secretKey");
            String applyChlId = paramMap.get("applyChlId");
            String provinceCode = paramMap.get("provinceCode");
            String cityCode = paramMap.get("cityCode");
            String fromType = paramMap.get("fromType");
            String orgType = paramMap.get("orgType");
            String operId = paramMap.get("operId");

            String orderId = order.get("orderId") + "";
            map.put("orderId", orderId);
            map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
            map.put("providerOrderId",order.get("providerOrderId") != null ? order.get("providerOrderId") : "");
            map.put("phone",order.get("phone") + "");
            map.put("fee",order.get("fee"));

            String reqDateTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
            // 请求流水号
            String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
            String timestamp = format1.format(new Date());
            String queryResult =
                sendChargeQuery(queryUrl,
                    secretKey,
                    serialNumber,
                    timestamp,
                    provinceCode,
                    cityCode,
                    fromType,
                    orgType,
                    applyChlId,
                    operId,
                    orderId);
            if (!"".equals(queryResult) && null != queryResult)
            {
                Map<String, String> queryResultMap = getQueryResultCode(queryResult);
                String resultCode = queryResultMap.get("resultCode");
                String resultMessage = queryResultMap.get("resultMessage");

                // 处理中 继续查询
                if ("103125".equals(resultCode)) {
                    map.put("status",2);	// 充值订单处理中
                    map.put("resultCode", "充值中！" );
                } else if ("000000".equals(resultCode)) {
                    String applyId = queryResultMap.get("applyId");
                    map.put("providerOrderId", applyId);
                    map.put("status",1);	// 充值成功
                    map.put("resultCode", "充值成功！" );
                } else {
                    map.put("status",0);	// 充值失败 或 无充值订单信息
                    map.put("resultCode", "充值失败:"+resultCode + "-" + resultMessage);
                }
            }else {
                map.put("status",3);				// 查询订单状态失败
                map.put("resultCode","查询接口返回信息为空！");	// 查询结果
            }
        } catch (Exception e) {
            log.error("国美查询Task出错" + e);
        }
        return map;
    }
    
    public String sendChargeQuery(String url, String secretKey, String serialNumber, String timestamp,
        String provinceCode, String cityCode, String fromType, String orgType, String applyChlId, String operId,
        String payOrgSerial)
    {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        JSONObject requestHeader = new JSONObject();
        requestHeader.put("provinceCode", provinceCode);
        requestHeader.put("cityCode", cityCode);
        requestHeader.put("fromType", fromType);
        requestHeader.put("orgType", orgType);
        requestHeader.put("applyChlId", applyChlId);
        requestHeader.put("operId", operId);
        
        JSONObject chargeQueryRequest = new JSONObject();
        chargeQueryRequest.put("requestHeader", requestHeader);
        chargeQueryRequest.put("payOrgSerial", payOrgSerial);
        
        JSONObject data = new JSONObject();
        data.put("chargeQueryRequest", chargeQueryRequest);
        
        String sign = MD5.ToMD5(data.toString() + secretKey);
        
        Map json = new LinkedHashMap();
        json.put("serial_number", serialNumber);
        json.put("timestamp", format1.format(new Date()));
        json.put("sign", sign);
        json.put("data", data);
        
        log.error(JSONObject.toJSONString(json));
        
        String queryString = "";
        try
        {
            queryString = URIUtil.encodeQuery(JSONObject.toJSONString(json));
        }
        catch (Exception e)
        {
            log.error("guo mei job send charge query exception|" + e.getMessage());
        }
        String ret = ToolHttp.post(false, url, queryString, null);
        return ret;
        
    }
    
    /**
     * 获取6位随机数
     * 
     * @return
     */
    public int getSixSquece()
    {
        return (int)((Math.random() * 9 + 1) * 100000);
    }
    
    /**
     * 获取查询结果中返回的结果信息
     * 
     * @param responseStr 查询结果返回值
     * @return queryResultMap此map中包括返回结果代码resultCode与返回结果消息resultMessage
     */
    public static Map<String, String> getQueryResultCode(String responseStr)
    {
        Map<String, String> queryResultMap = new HashMap<String, String>();
        try
        {
            JSONObject resultJson = JSON.parseObject(responseStr);
            String resultCode = resultJson.getJSONObject("responseHeader").getString("resultCode");
            String resultMessage = resultJson.getJSONObject("responseHeader").getString("resultMessage");
            if(null!=resultJson.getJSONObject("payApplyLog")&&!"".equals(resultJson.getJSONObject("payApplyLog"))){
                String applyId = resultJson.getJSONObject("payApplyLog").getString("applyId");
                queryResultMap.put("applyId", applyId);
            }
            queryResultMap.put("resultCode", resultCode);
            queryResultMap.put("resultMessage", resultMessage);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("guo mei job getResultcode exception|" + e.getMessage());
        }
        
        return queryResultMap;
    }
}
