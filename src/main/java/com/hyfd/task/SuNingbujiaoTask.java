package com.hyfd.task;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeGetRequest;
import com.suning.api.entity.operasale.AgentrechargeGetResponse;
import com.suning.api.exception.SuningApiException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SuNingbujiaoTask {

    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

    @Autowired
    OrderDao orderDao;// 订单

    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者

    private static Logger log = Logger.getLogger(SuNingbujiaoTask.class);

    @Scheduled(fixedDelay = 60000)
    public void querySuNingBuJiaoOrder() {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            String id = "2000000070";// 苏宁补交物理通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String linkUrl = channel.get("link_url").toString(); // 查询地址
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
            String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
            String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList) {
                int flag = 2;
                String orderId = order.get("orderId") + "";
                String phone = order.get("phone") + "";

                map.put("orderId", orderId);
                // 话费充值结果查询请求
                String resultStr = rechargeGetRequest(linkUrl, appKey, appSecret, key, channelId, orderId, phone);
                log.info(orderId+"苏宁查单接口返回结果:"+resultStr);
                if (null != resultStr && !resultStr.equals("")) {
                    // 验证充值查询请求结果
                    JSONObject resultJson = valiResult(resultStr);
                    boolean state = resultJson.getBoolean("state");
                    if (state) {
                        // 充值订单状态编码， 0-充值失败 1-充值成功 2-充值订单处理中 3-无充值订单信息
                        String code = resultJson.getString("respCode");
                        String respMsg = resultJson.getString("respMsg");
                        map.put("resultCode", code + ":" + respMsg);

                        if ("1".equals(code)) {
                            flag = 1;	// 充值成功
                        } else if ("0".equals(code) || "3".equals(code)) {
                            flag = 0;	// 充值失败 或 无充值订单信息
                        } if ("2".equals(code)) {
                            continue;	// 充值订单处理中
                        }
                        map.put("status", flag);
                        mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
                    }
                }
            }
        } catch (Exception e) {
            log.error("苏宁补交Task出错" + e);
        }

    }

    public String rechargeGetRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber) {
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
