package com.hyfd.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GuoMeiTest {
    public static void main(String[] args) {
        testCheck();


    }
    // 测试充值请求 我方->上游
    public static void testCheck() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        String applyChlId = "1000160";// XX—根据不同外围系统提供不同编码 TODO
        String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();

        String checkUrl = "http://121.36.88.50:26810/MVNO-UIP-GOMESKYPAY/orderTrade/chargeCheck";
        String secretKey = "JNHB123456789"; // TODO 秘钥
        String serviceNum = "16562655766";// 服务号码

        //用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
        String checkResult = sendCheck(checkUrl, secretKey, serialNumber, serviceNum);
        System.out.println("check:" + checkResult);

        //处理返回值
        JSONObject check = JSON.parseObject(checkResult);
        String checkResultCode = check.getJSONObject("responseHeader").getString("resultCode");
        String checkResultMessage = check.getJSONObject("responseHeader").getString("resultMessage");
        System.out.println(checkResultCode + ":" + checkResultMessage);

    }
    // 充值
    public static  void charge(){
                Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phoneNo = "16562655766";// 手机号
            String fee = "10";// 金额，以元为单  位
            String spec = Double.parseDouble(fee) + "";// 充值金额，以分为单位


            String url = "http://121.36.88.50:26810/MVNO-UIP-GOMESKYPAY/orderTrade/charge";
            String secretKey = "JNHB123456789";
            String applyChlId = "1000161";
            String cityCode = "000";// 定值
            String fromType = "8";// XX—根据不同外围系统提供不同编码
            String orgType = "2";// X-不同外围系统分配不同参数
            String operId = "1000161";// 操作员IDXXXXXXX-不同外围系统分配不同参数
            String payChannel = "8";// 操作渠道 X-不同外围系统分配不同参数
            String payStyle = "2";// 付款方式 X-不同外围系统分配不同参数
            String provinceCode = "00";// 定值

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数 与上面的curids是否需要统一
            String curids = applyChlId + format.format(new Date()) + getSixSquece();
            map.put("orderId", curids);
            String timestamp = format1.format(new Date());// 2013-12-30 17:36:15
            String reqSerial = curids;// 充值请求流水号 订单号
            String payAmount = spec;// 单位元
            String serviceNum = phoneNo;// 服务号码
            // 检查可以充值
            String orderResult = sendOrder(url, secretKey, curids,timestamp, provinceCode, cityCode, fromType, orgType,
                    applyChlId, operId, reqSerial, payChannel, payStyle,payAmount, serviceNum);
            // 处理返回值
            JSONObject orderResultJson = null;
            String orderResultCode = "";
            String orderResultMessage = "";
            try {
                orderResultJson = JSON.parseObject(orderResult);
                orderResultCode = orderResultJson.getJSONObject("responseHeader").getString("resultCode");
                orderResultMessage = orderResultJson.getJSONObject("responseHeader").getString("resultMessage");
                System.out.println(orderResultCode + ":" + orderResultMessage);
            } catch (Exception e) {
                orderResultJson = null;
            }

        } catch (Exception e) {
            map.put("resultCode", "充值过程发生异常");
            System.out.println("国美[话费充值]方法出错");
        }
        map.put("status", flag);
        System.out.println(map);
    }

    //  充值查询
    public static void ss(){
        String url = "http://121.36.88.50:26810/MVNO-UIP-GOMESKYPAY/orderTrade/chargeQuery";

        String secretKey = "JNHB123456789"; // TODO 秘钥
        String applyChlId = "1000161";// XX—根据不同外围系统提供不同编码 TODO
        String provinceCode="00";
        String cityCode="00";
        String fromType="8";
        String orgType="2";
        String operId="1000161";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp=format1.format(new Date());


        String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
        String payOrgSerial="100016120220831143942979807811";

        String result=sendChargeQuery(url, secretKey, serialNumber, timestamp, provinceCode, cityCode, fromType, orgType, applyChlId, operId, payOrgSerial);
        System.out.println(result);
        System.out.println(getQueryResultCode(result));
    }
    public static String getQueryResultCode(String responseStr){
        String resultCode="";
        try{
            JSONObject resultJson= JSON.parseObject(responseStr);
            resultCode= resultJson.getJSONObject("responseHeader").getString("resultCode");
            System.out.println();
        }catch(Exception e){
            e.printStackTrace();
        }

        return resultCode;
    }

    // 用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
    public static String sendCheck(String checkUrl, String secretKey, String serialNumber, String serviceNum) {

        // http://10.128.22.128:16810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck?
        // serialNumber=200000120140902162250011935001
        // &timestamp=2014-09-02 16:22:50
        // &sign=812d196060596c510675d8975106d6d9
        // &data={"serviceNum":"17090181818"}

        StringBuffer paramBuffer = new StringBuffer();
        JSONObject data = new JSONObject();
        data.put("serviceNum", serviceNum);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        paramBuffer.append("serialNumber=").append(serialNumber);

        paramBuffer.append("&timestamp=").append(format.format(new Date()));
        System.out.println(data);
        String sign = MD5.ToMD5(data.toString() + secretKey);
        paramBuffer.append("&sign=").append(sign);

        paramBuffer.append("&data=").append(data);

        String queryString = "";
        System.out.println(paramBuffer);
        try {
            queryString = URIUtil.encodeQuery(paramBuffer.toString());
            // queryString = paramBuffer.toString();
        } catch (URIException e) {
            e.printStackTrace();
        }
        // queryString = paramBuffer.toString();
        String url = checkUrl + "?" + queryString;
        System.out.println(url);
        String ret = ToolHttp.get(false, url);
        return ret;

    }

    public static String sendChargeQuery(String url, String secretKey, String serialNumber, String timestamp, String provinceCode, String cityCode, String fromType, String orgType, String applyChlId, String operId, String payOrgSerial) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject requestHeader=new JSONObject();
        requestHeader.put("provinceCode", provinceCode);
        requestHeader.put("cityCode", cityCode);
        requestHeader.put("fromType", fromType);
        requestHeader.put("orgType", orgType);
        requestHeader.put("applyChlId", applyChlId);
        requestHeader.put("operId", operId);

        JSONObject chargeQueryRequest=new JSONObject();
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

        System.out.println(JSONObject.toJSONString(json));

        String queryString = "";
        try {
            queryString = URIUtil.encodeQuery(JSONObject.toJSONString(json));
        } catch (URIException e) {
            e.printStackTrace();
        }
        String ret = ToolHttp.post(false, url, queryString, null);
        return ret;

    }

    /**
     * @param url
     *            链接地址
     * @param serialNumber
     *            业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数
     * @param timestamp
     *            时间戳
     * @param provinceCode
     *            省份代码 请求报文头
     * @param cityCode
     *            地市代码 请求报文头
     * @param fromType
     *            来源类型 请求报文头
     * @param orgType
     *            组织类型 请求报文头
     * @param applyChlId
     *            渠道id 请求报文头
     * @param operId
     *            操作员ID 请求报文头
     * @param reqSerial
     *            充值请求流水号 缴费信息
     * @param payChannel
     *            操作渠道 缴费信息
     * @param payStyle
     *            付款方式 缴费信息
     * @param payAmount
     *            缴费金额 缴费信息 位是元
     * @param serviceNum
     *            服务号码 缴费信息
     * @return
     */
    public static String sendOrder(String url, String secretKey,
                                   String serialNumber, String timestamp, String provinceCode,
                                   String cityCode, String fromType, String orgType,
                                   String applyChlId, String operId, String reqSerial,
                                   String payChannel, String payStyle, String payAmount,
                                   String serviceNum) {
        JSONObject data = new JSONObject();

        JSONObject paymentRequest = new JSONObject();
        data.put("paymentRequest", paymentRequest);

        JSONObject requestHeader = new JSONObject();
        JSONObject paymentInfo = new JSONObject();
        paymentRequest.put("requestHeader", requestHeader);
        paymentRequest.put("paymentInfo", paymentInfo);

        requestHeader.put("provinceCode", provinceCode);
        requestHeader.put("cityCode", cityCode);
        requestHeader.put("fromType", fromType);
        requestHeader.put("orgType", orgType);
        requestHeader.put("applyChlId", applyChlId);
        requestHeader.put("operId", operId);

        paymentInfo.put("reqSerial", reqSerial);
        paymentInfo.put("payChannel", payChannel);
        paymentInfo.put("payStyle", payStyle);
        paymentInfo.put("payAmount", payAmount);
        paymentInfo.put("serviceNum", serviceNum);

        System.out.println("明文:" + data.toJSONString() + secretKey);
        String sign = MD5.ToMD5(data.toJSONString() + secretKey);

        JSONObject json = new JSONObject();
        json.put("serial_number", serialNumber);
        json.put("timestamp", timestamp);
        json.put("sign", sign);
        json.put("data", data);
        System.out.println("all:" + json.toJSONString());
        String queryString = "";
        try {
            queryString = URIUtil.encodeQuery(json.toJSONString());
            // queryString = paramBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        String ret = ToolHttp.post(false, url, queryString, null);
        return ret;

    }

    /**
     * 获取5位序列码
     *
     * @return
     */
    public static int getSixSquece() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static int getThrSquece() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }
}
