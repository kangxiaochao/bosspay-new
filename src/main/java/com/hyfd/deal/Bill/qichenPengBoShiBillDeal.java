package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class qichenPengBoShiBillDeal implements BaseDeal {

    Logger log = Logger.getLogger(getClass());

    public static Map<String, String> rltMap = new HashMap<String, String>();
    static {
        rltMap.put("1001", "账户不存在或未开通接口权限");
        rltMap.put("1002", "签名验证失败");
        rltMap.put("1003", "不支持当前手机号[无法确定归属地");
        rltMap.put("1004", "暂不支持当前充值金额");
        rltMap.put("1006", "余额不足");
        rltMap.put("1007", "系统繁忙，创建订单失败");
        rltMap.put("1008", "系统繁忙，获取账户余额失败");
        rltMap.put("1009", "订单创建失败");
        rltMap.put("1010", "外部订单编号重复");
        rltMap.put("1011", "充值请求参数缺失");
        rltMap.put("1012", "用户开户未完成");
        rltMap.put("2001", "订单创建成功");
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phonenum = order.get("phone") + "";// 手机号
            String fee = order.get("fee") + "";// 金额，以元为单位

            Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
            String linkUrl = (String) channel.get("link_url");// 充值地址
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = paramMap.get("appKey");
            String partnerId = paramMap.get("partnerId");


            String nonce_str = DateUtils.getNowTimeStamp().toString() + ((int)(Math.random()*9000)+1000);//随机字符串
            String order_id = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + phonenum + ((int)(Math.random()*9000)+1000);//订单号
            String ts = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
            String params = jointUrl(fee, nonce_str, order_id, partnerId, phonenum, appKey,ts);

            String result = HttpUtils.doGet(linkUrl + "?" + params);
            log.error("启辰鹏博士充值提交完成，返回信息为" + result);
            if (!StringUtil.empty(result)) {
                JSONObject resultJson = JSONObject.parseObject(result);

                String retcode = resultJson.getString("retcode");

                map.put("resultCode", retcode + ":" + rltMap.get(retcode));
                if ("2001".equals(retcode)) {
                    flag = 1;
                    map.put("providerOrderId", resultJson.getString("drpeng_order_id"));
                } else{
                    flag = 0;
                }
            }
            map.put("orderId", order_id);
        }catch(Exception e) {
            log.error("启辰鹏博士充值方法出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }

    /**
     * 拼接信息
     * @param fee 充值金额
     * @param nonce_str 随机字符串
     * @param order_id 订单号
     * @param partner_id 合作伙伴Id
     * @param phonenum 充值手机号
     * @param appKey 密钥
     * @return
     */
    public String jointUrl(String fee,String nonce_str,String order_id,String partnerId,String phonenum,String appKey,String ts) {
        StringBuffer suBuffer = new StringBuffer();
        suBuffer.append("money" + fee);
        suBuffer.append("nonce_str" + nonce_str);
        suBuffer.append("order_id" + order_id);
        suBuffer.append("partner_id" + partnerId);
        suBuffer.append("phone_number" + phonenum);
        suBuffer.append("ts" + ts);
        suBuffer.append(appKey);
        log.error("代加密串"+suBuffer);
        String sign = DigestUtils.md5Hex(suBuffer + "");//签名

        StringBuffer url = new StringBuffer();
        url.append("partner_id=" + partnerId);
        url.append("&phone_number=" + phonenum);
        url.append("&money=" + fee);
        url.append("&nonce_str=" + nonce_str);
        url.append("&order_id=" + order_id);
        url.append("&ts=" + ts);
        url.append("&sign=" + sign);

        return url + "";
    }
}
