package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GongXiangTongXinBillDeal implements BaseDeal {

    Logger log = Logger.getLogger(getClass());

    public static Map<String, String> rltMap = new HashMap<String, String>();
   


    @SuppressWarnings("unchecked")
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phonenum = order.get("phone") + "";// 手机号
            double fee = new Double(order.get("fee") + "");// 金额，以元为单位
            String spec = new Double(fee * 100).intValue() + "";// 充值金额，以分为单位

            Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
            String linkUrl = (String) channel.get("link_url");// 充值地址
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String companyCode = paramMap.get("companyCode");
            String companyType = paramMap.get("companyType");
            String appKey = paramMap.get("appKey");
            String orderTitle = "haobaihfcz";


            String order_id = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + phonenum + ((int)(Math.random()*9000)+1000);//订单号
            map.put("orderId", order_id);//订单号
            String  sign = signs(companyCode,companyType,phonenum,order_id,orderTitle,spec,appKey);

            Map<String,Object> map1 = new HashMap<String,Object>();
            Map<String,Object> map2 = new HashMap<String,Object>();
            Map<String,Object> map3 = new HashMap<String,Object>();
            map1.put("companyCode",companyCode);//必填
            map1.put("companyType",companyType);// 必填
            map1.put("sign",sign);
            map3.put("publicParams",map1);
            map2.put("orderTitle",orderTitle);
            map2.put("customerId",order_id);
            map2.put("phoneNum",phonenum); //必填
            map2.put("amount",spec);//
            map3.put("businessParams",map2);

            String parameter = JSONObject.toJSONString(map3);
            log.error("发起共享通信,请求参数" + parameter);
            //发送httppost请求
            String data = ToolHttp.post(false, linkUrl, parameter,"text/plain");
            log.error("共享通信充值提交完成，返回信息为" + data);
            if (null != data && !data.equals("")){
                JSONObject resultJson = JSONObject.parseObject(data);
                String status = resultJson.get("status") + ""; //status 状态	0为接受订单成功，具体是否充值成功，异步通知里体现
                String message = resultJson.get("message") + "";
                String orderCode = resultJson.get("orderCode") + "";
                String customerorderId = resultJson.get("customerId") + "";
                map.put("resultCode",message);

                if ("0".equals(status)) {
                    flag = 1;
                    map.put("providerOrderId",orderCode); //上家订单号
                }else {
                    flag = -1;
                    map.put("providerOrderId",orderCode); //上家订单号
                }

            }else {
                // 请求超时,未获取到返回数据
                flag = -1;
                String msg = "共享通信充值,号码[" + phonenum + "],金额[" + spec + "(分)],请求超时,接收到返回数据为null";
                map.put("resultCode", msg);
                log.error(msg);
            }
        }catch(Exception e) {
            log.error("共享通信充值方法出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        log.error(map);
        return map;
    }

    /**
     * 拼接信息
     * @return
     */
    public String signs(String companyCode,String companyType,String phoneNum,String customerId,String orderTitle,String amount,String md5Str) throws  Exception {
        StringBuffer str = new StringBuffer(md5Str);
        str.append("companyCode="+companyCode);
        str.append(":");
        str.append("companyType="+companyType);
        str.append(":");
        str.append("phoneNum="+phoneNum);
        str.append(":");
        str.append("customerId="+customerId);
        str.append(":");
        str.append("orderTitle="+orderTitle);
        str.append(":");
        str.append("amount="+amount);
        str.append(md5Str);
        String sign = ToolMD5.encodeMD5Hex(str.toString()).toUpperCase();
        log.error("加密sign成功，返回信息为" + sign);
        return sign;
    }
}
