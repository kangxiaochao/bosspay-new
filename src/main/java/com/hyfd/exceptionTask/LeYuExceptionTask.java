package com.hyfd.exceptionTask;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class LeYuExceptionTask implements BaseTask{

    private static Logger log = Logger.getLogger(LeYuExceptionTask.class);

    @Override
    public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            String defaultParameter = (String) channelMap.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String ServerUrl = paramMap.get("ServerUrl") + ""; 		// 乐语提供 查单url
            String appKey = paramMap.get("AppKey") + ""; 		// 乐语提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + ""; 	// 乐语提供 平台appSecret

            String orderId = order.get("orderId") + "";
            String providerOrderId = order.get("providerOrderId") + "";
            String nowTime = DateUtils.getNowTimeToSec();
            map.put("orderId", orderId);
            map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
            map.put("providerOrderId",order.get("providerOrderId") != null ? order.get("providerOrderId") : "");
            map.put("phone",order.get("phone"));
            map.put("fee",order.get("fee"));

            Map mapParm  = new HashMap();
            mapParm.put("rechargeId",providerOrderId);
            String redultjson = JSONObject.toJSONString(mapParm);
            SortedMap sortedMap = new TreeMap();
            sortedMap.put("appKey",appKey);
            sortedMap.put("requestType","QUERY");
            sortedMap.put("timeStamp", nowTime);
            sortedMap.put("requestMsg",redultjson);
            String sign = createSign(sortedMap, appSecret);
            sortedMap.put("sign",sign);
            sortedMap.put("transactionId","");
            String redultjsons = JSONObject.toJSONString(sortedMap);
            log.info("乐语异常订单查单请求参数:"+redultjsons);
            String data = ToolHttp.post(false, ServerUrl, redultjsons,"application/json");
            log.info("乐语异常订单查单响应结果:" + data);
//                {"msg":"success","code":0,"responseMsg":{"serviceNum":"17001000000","remark":"充值成功","orderPay":1000,"transactionId":"0a7b0039b6af45ab888c9e7f7066ded9","rechargeId":"LYTXRC202201121530035860917","status":1,"createDate":"2022-01-12 15:30:04","completeDate":"2022-01-12 15:30:12"}}
            if (null != data && !data.equals("")) {
                JSONObject jsonObj = JSONObject.parseObject(data);
                String code = jsonObj.getString("code");
                if (code.equals("0")){
                    String responseMsg = jsonObj.getString("responseMsg");
                    JSONObject resp = JSONObject.parseObject(responseMsg);
                    String status = resp.getString("status");
                    String remark = resp.getString("remark");
                    if (status.equals("1")){
                        map.put("status", 1);	  //充值成功
                        map.put("resultCode", "充值成功！");
                    }else if(status.equals("2")) {
                        map.put("status", 0);     //充值失败
                        map.put("resultCode", "充值失败："+code + "-" + remark);
                    }else if (status.equals("0")){
                        map.put("status", 2);    //充值订单处理中
                        map.put("resultCode", "充值中！" );
                    }else{
                        map.put("status", 3);
                        map.put("resultCode", "未知的订单状态，请联系上家核实："+code + "-" + remark);
                    }
                }else {
                    //查单请求出现异常
                    log.error("乐语请求订单出现异常："+order);
                }
            }else{
                map.put("status", 3);
                map.put("resultCode", "查询接口返回信息为空！");
            }
        }catch (Exception e){
            log.error("乐语Task出错" + e);
        }
        return map;
    }


    /**
     * @param params 请求参数
     * @return
     * @author
     * @date 2016-4-22
     * @Description：sign签名
     */
    public static String createSign(SortedMap<Object, Object> params, String APP_SECRET) {
        StringBuffer sb = new StringBuffer();
        Set es = params.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + APP_SECRET);
        System.out.println("sb = " + sb);
        String sign = null;
        try {
            sign = MD5Util.MD5Encode(sb.toString(),"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }
}
