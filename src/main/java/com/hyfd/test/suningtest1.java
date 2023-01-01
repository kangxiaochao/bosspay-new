package com.hyfd.test;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeGetRequest;
import com.suning.api.entity.operasale.AgentrechargeGetResponse;
import com.suning.api.exception.SuningApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class suningtest1 {
    public static void main(String[] args) {
        querySuNingOrder();
    }
    public static void querySuNingOrder() {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            String id = "2000000035";// 苏宁物理通道ID ~~~~~
            String linkUrl = "https://open.suning.com/api/http/sopRequest"; // 查询地址
            String appKey = "598ad28ab8e529ac5f6ef6b3f1323665"; 		// 苏宁提供 平台appKay
            String appSecret = "b5653d3b70ef8328b7bf8dcd4ee00c26"; 	// 苏宁提供 平台appSecret
            String key = "jSLFU6iXjkJtR8Yp"; 				// 苏宁提供 接口签名使用的key
            String channelId =  "10160383"; 	// 苏宁提供 代理商充值渠道ID


                String orderId = "1016038320220902d5aadfcb";
                String phone = "17001607984";
                map.put("orderId", orderId);
                // 话费充值结果查询请求
                String resultStr = rechargeGetRequest(linkUrl, appKey, appSecret, key, channelId, orderId, phone);
                System.out.println(orderId+"苏宁查单接口返回结果:"+resultStr);
        } catch (Exception e) {
            System.out.println("苏宁Task出错" + e);
        }
    }

    /**
     * <h5>功能:话费充值结果查询请求</h5>
     *
     * @author zhangpj	@date 2018年11月13日
     * @param serverUrl 请求地址
     * @param appKey 平台appKey
     * @param appSecret 平台appSecret
     * @param key 接口key
     * @param channelId 代理商编码ID
     * @param reqSerial 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
     * @param serialNumber 待充值的用户号码
     * @return
     */
    public static String rechargeGetRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber) {
        String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
        String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名

        AgentrechargeGetRequest request = new AgentrechargeGetRequest();
        request.setChannelId(channelId);
        request.setReqSerial(reqSerial);
        request.setReqTime(reqTime);
        request.setReqSign(sign);
        request.setSerialNumber(serialNumber);

        //api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
//		request.setCheckParam(true);

        DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
        String resultStr = "";
        try {
            AgentrechargeGetResponse response = client.excute(request);
            resultStr = response.getBody();
            System.out.println("返回json/xml格式数据 :" + resultStr);
        } catch (SuningApiException e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    /**
     * <h5>功能:验证充值查询请求结果</h5>
     *
     * @author zhangpj	@date 2018年11月13日
     * @return 成功state返回true,失败state返回false
     */
    private JSONObject valiResult(String resultStr){
        JSONObject jsonObj = JSONObject.parseObject(resultStr);
        JSONObject resultJson = null;
        try {
            JSONObject snBodyJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body");
            if (null != snBodyJson) {
                resultJson = snBodyJson.getJSONObject("getAgentrecharge");
                resultJson.put("state", true);
            } else {
                resultJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_error");
                resultJson.put("state", false);
            }
        } catch (Exception e) {
            System.out.println("解析出错了");
        }
        System.out.println(resultJson.toJSONString());
        return resultJson;
    }
}
