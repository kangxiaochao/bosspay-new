package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.deal.BaseDeal;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.omg.CORBA.FloatSeqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.util.*;


public class SanWangBillDeal implements BaseDeal {

    private static Logger log = Logger.getLogger(SanWangBillDeal.class);


//    public static Map<String, String> ydMap = new HashMap<String, String>();
//    public static Map<String, String> ltMap = new HashMap<String, String>();
//    public static Map<String, String> dxMap = new HashMap<String, String>();

//    static {
//        ydMap.put("100", "H10100");
//        ydMap.put("50", "H10050");
//        ydMap.put("30", "H10030");
//        ltMap.put("100", "H20100");
//        ltMap.put("50", "H20050");
//        ltMap.put("30", "H20030");
//        dxMap.put("100", "H30100");
//        dxMap.put("50", "H30050");
//        dxMap.put("30", "H30030");
//    }

    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        log.info(order+"order");
        Map<String,Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            log.info("发起三网话费充值-------------------------");
            String phone = (String) order.get("phone")+"";// 手机号
            log.info(phone+"phone");
            String fee1 = order.get("fee") + "";//充值金额
            String[] split = fee1.split("\\.");
            String fee = split[0];
            log.info(fee+"fee");
            String providerId = order.get("providerId")+"";
            log.info(providerId+"providerId");
            Map<String, Object> channel = (Map<String, Object>) order
                    .get("channel");// 获取通道参数
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            log.info("defaultParameter"+defaultParameter);
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
//            String accessToken = paramMap.get("accessToken");
//            log.info(accessToken+"accessToken");
            String secret = paramMap.get("secret");
            String callback = paramMap.get("callback");
            String accountName = paramMap.get("accountName");
            log.info(secret);
            String url = paramMap.get("url");
            log.info(url+"url");

             String carrier = "";
            if (providerId.equals("0000000001")) {
                carrier = "中国移动";
           }else if (providerId.equals("0000000002")){
                carrier = "中国联通";
            }else if (providerId.equals("0000000003")){
                carrier = "中国电信";
           }
//            log.info("手机号为"+phone+"充值编码为"+gearCode);
            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
            map.put("orderId", curids);//订单号


            Map maps = new HashMap();
            maps.put("mobile",phone);//手机号
            maps.put("price",fee);//充值面值
            maps.put("orderId",curids);
            maps.put("successNotifyUrl",callback);
            maps.put("failNotifyUrl",callback);
            maps.put("accountName",accountName);
            maps.put("carrier",carrier);
//            maps.put("timestamp",String.valueOf(System.currentTimeMillis()));//提单时间
            String sign = getSign(maps, secret);
            log.info(sign);
            maps.put("sign",sign);
            String parameter = JSONObject.toJSONString(maps);

            //发送post请求.
            String data = ToolHttp.post(true, url, parameter, "application/json");
            if (!StringUtil.empty(data)) {
                log.info("三网接受的响应为:" + data);
                JSONObject response = JSONObject.parseObject(data);
                boolean status = (Boolean) response.get("status");
                String message = (String) response.get("message");
                if (status == true) {
                    flag = 1;
                    map.put("resultCode", "提交成功");
                } else {
                    flag = 0;
                    map.put("resultCode", message);
                }
            }
        }catch (Exception e){
            log.error("三网话费充值逻辑出错" + e + MapUtils.toString(order));
        }

        map.put("status", flag);
        return map;
    }


    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }
    /**
     * 加密方式
     *
     * @param map
     * @return
     */
    public static String getSign(Map map,String sign) {
        log.info(sign);
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        String buf = "";
        for (Map.Entry<String, Object> stringStringEntry : infoIds) {
            if (stringStringEntry.getValue() != null) {
                buf += stringStringEntry.getKey() + stringStringEntry.getValue();
            }
        }
       log.info(buf);
        String sign2 = encrypt32(sign + encrypt32(buf) + sign);
        return sign2;
    }




}
