package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JieXunBillDeal implements BaseDeal {

    private static Logger log = Logger.getLogger(JieXunBillDeal.class);


    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        log.info(order + "order");
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            log.info("发起捷讯话费充值-------------------------");
            String phone = (String) order.get("phone") + "";// 手机号
            log.info(phone + "phone");
            String subfee = order.get("fee") + "";// 金额，以元为单位
            String fee = subfee.substring(0, subfee.lastIndexOf("."));
            log.info(fee + "fee");
            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
            map.put("orderId", curids);//订单号
            Map<String, Object> channel = (Map<String, Object>) order
                    .get("channel");// 获取通道参数
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            String linkUrl = (String) channel.get("link_url");// 充值地址
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());

            String name = paramMap.get("companyCode");
            String token = paramMap.get("appKey");
            log.info(name + "name" + token + "token");

            String data = phone + "-" + fee;

            String sign = ToolMD5.encodeMD5Hex(data + token);

            //拼接字符串
            StringBuilder sb = new StringBuilder();
            sb.append("name=" + name);
            sb.append("&data=" + data);
            sb.append("&sign=" + sign);
            String orderurl = linkUrl + "?" + sb;

            //发送httppost请求
            String result = ToolHttp.post(false, orderurl, null, "application/x-www-form-urlencoded");
            log.info("result" + result);
            String[] split = result.split("\\|");
            String unSplit = split[0];
            log.info("unSplit = " + unSplit);
           String resuldcode = unSplit.split("-")[unSplit.split("-").length - 1];


            if (resuldcode.equals("success")) {
                flag = 3;//直接返回充值成功结果
                map.put("resultCode","充值成功");
            } else if (resuldcode.equals("fail")){
                flag = 4;
                map.put("resultCode","充值失败");
            }
        } catch (Exception e) {
            log.error("捷讯话费充值逻辑出错" + e + MapUtils.toString(order));
        }

        map.put("status", flag);
        return map;
    }
}
