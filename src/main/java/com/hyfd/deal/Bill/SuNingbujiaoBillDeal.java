package com.hyfd.deal.Bill;

import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeAddRequest;
import com.suning.api.entity.operasale.AgentrechargeAddResponse;
import com.suning.api.exception.SuningApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SuNingbujiaoBillDeal implements BaseDeal {

    Logger log = Logger.getLogger(SuNingBillDeal.class);

    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phoneNo = (String) order.get("phone");	// 手机号
            String fee = (String)order.get("fee");//金额，以分为单位
            Double ibillsize = Double.parseDouble(fee)*100;
            String spec = ibillsize.intValue() + "";

            Map<String, Object> channel = (Map<String, Object>) order.get("channel");	// 获取通道参数
            String linkUrl = channel.get("link_url").toString(); 						// 充值地址
            String defaultParameter = (String) channel.get("default_parameter");//默认参数
            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
            String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
            String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID

            // 生成自己的id，供回调时查询数据使用,上游要求格式：代理商编码+YYYYMMDD+8位流水号（我感觉后续还要和提交过来的唯一标识做绑定，插入数据库）
            String curids = channelId + DateUtils.getNowTimeToMS().substring(0, 16);
            map.put("orderId", curids);

            String resultStr = rechargeAddRequest(linkUrl, appKey, appSecret, key, channelId, curids, phoneNo, spec);
        }catch (Exception e){
            log.error("苏宁视通补交话费充值出错" + e.getMessage() + MapUtils.toString(order));
        }

        map.put("status", flag);
        return map;

    }


    public String rechargeAddRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber, String feeAmount) {
        String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
        String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
        byte[] encrypt = AESUtils.encrypt(serialNumber, key);
        String Number = Base64.encodeBase64String(encrypt);

        AgentrechargeAddRequest request = new AgentrechargeAddRequest();
        request.setChannelId(channelId);
        request.setFeeAmount(feeAmount);
        request.setReqSerial(reqSerial);
        request.setReqSign(sign);
        request.setReqTime(reqTime);
        request.setSerialNumber(Number);
//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
//        request.setCheckParam(true);
        DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
        try {
            AgentrechargeAddResponse response = client.excute(request);
//            System.out.println("返回json/xml格式数据 :" + response.getBody());
        } catch (SuningApiException e) {
            e.printStackTrace();
        }



        return "";
    }


}
