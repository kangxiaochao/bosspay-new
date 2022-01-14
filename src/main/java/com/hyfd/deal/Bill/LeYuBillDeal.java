package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class LeYuBillDeal implements BaseDeal {

    public static Map<String, String> lyMap = new HashMap<String, String>();

    static {
        lyMap.put("10001", "appKey不能为空");
        lyMap.put("10002", "请求类型不能为空");
        lyMap.put("10003", "交易流水不能为空");
        lyMap.put("10004", "时间戳不能为空");
        lyMap.put("10005", "请求内容不能为空");
        lyMap.put("10006", "签名不能为空");
        lyMap.put("10007", "appKey不存在");
        lyMap.put("10008", "签名验证失败");
        lyMap.put("10009", "充值号码不能为空");
        lyMap.put("10010", "充值金额或实付金额不能小于0");
        lyMap.put("10011", "用户非在网或非本网，请检查充值号码");
        lyMap.put("10012", "交易流水已存在");
        lyMap.put("10013", "渠道账户扣费异常");
        lyMap.put("10014", "查询号码不能为空");
        lyMap.put("10015", "号码格式错误");
        lyMap.put("10016", "缴费单号不能为空");
    }


    private static Logger log = Logger.getLogger(LeYuBillDeal.class);

    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        log.info("乐语order" + order);
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phoneNo = (String) order.get("phone");    // 手机号
            Double fee = (Double) order.get("fee");//金额，以厘为单位
            Double ibillsize = Double.parseDouble(fee + "") * 1000;
            String spec = ibillsize.intValue() + "";

            Map<String, Object> channel = (Map<String, Object>) order.get("channel");    // 获取通道参数
            String linkUrl = channel.get("link_url").toString();                        // 充值地址
            String defaultParameter = (String) channel.get("default_parameter");//默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = paramMap.get("AppKey") + "";        // 乐语提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + "";    // 乐语提供 平台appSecret


            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNo + GenerateData.getIntData(9, 2);
            map.put("orderId", curids);
            String result = rechargeOrder(linkUrl, appKey, appSecret, phoneNo, spec, curids);
//            {"msg":"success","code":0,"responseMsg":{"rechargeId":"LYTXRC202201121530035860917"}}

            if (null == result || result.equals("")) {
                // 请求超时,未获取到返回数据
                String msg = "乐语话费充值,号码[" + phoneNo + "],金额[" + spec + "(元)],请求超时,未接收到返回数据";
                map.put("resultCode", msg);
                log.error(msg);
            }else {
                JSONObject jsonObj = JSONObject.parseObject(result);
                String responseMsg = jsonObj.getString("responseMsg");
                JSONObject response = JSONObject.parseObject(responseMsg);

                String code = jsonObj.getString("code");
                String msgs = jsonObj.getString("msg");

                if (code.equals("0")){
                    String rechargeId = response.getString("rechargeId");
                    map.put("providerOrderId", rechargeId);
                    flag = 1;	// 提交成功
                    map.put("resultCode",msgs);
                }else {
                    flag = 0;	// 提交失败
                    map.put("resultCode",lyMap.get(code));
                }

            }

        } catch (Exception e) {
            log.error("乐语话费充值出错" + e.getMessage() + MapUtils.toString(order));
        }


        map.put("status", flag);
        return map;
    }


    /**
     * <h5>功能:充值请求</h5>
     *
     * @param linkUrl   请求地址
     * @param appkey    平台appKey
     * @param appsecret 平台appSecret
     * @param phoneNo   待充值的用户号码
     * @param spec      充值金额,单位:厘
     * @param curids    订单号
     * @return
     * @author zhangpj    @date 2018年11月13日
     */
    public String rechargeOrder(String linkUrl, String appkey, String appsecret, String phoneNo, String spec, String curids) {
        String nowTime = DateUtils.getNowTimeToSec();

        Map mapMsg = new HashMap();
        mapMsg.put("serviceNum", phoneNo);
        mapMsg.put("amount", spec);
        String redultjson = JSONObject.toJSONString(mapMsg);

        SortedMap map = new TreeMap();
        map.put("requestType", "RECHARGE");
        map.put("transactionId", curids);
        map.put("timeStamp", nowTime);
        map.put("appKey", appkey);
        map.put("requestMsg", redultjson);

        String sign = createSign(map, appsecret);
        map.put("sign", sign);
        String redultjsons = JSONObject.toJSONString(map);
        log.info("乐语充值请求参数:" + redultjsons);
        String data = ToolHttp.post(false, linkUrl, redultjsons, "application/json");
        log.info("乐语充值请求响应结果:" + data);
        return redultjsons;
    }


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
            sign = MD5Util.MD5Encode(sb.toString(), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }
}
