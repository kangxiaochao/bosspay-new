package com.hyfd.exceptionTask;

import com.hyfd.common.utils.XmlUtils;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.BmOrderCustomGetRequest;
import com.qianmi.open.api.response.BmOrderCustomGetResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class YunMiYouExceptionTask implements BaseTask{

    private static Logger log = Logger.getLogger(YunMiYouExceptionTask.class);

    @Override
    public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            String defaultParameter = channelMap.get("default_parameter")+"";				//默认参数
            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String url = paramMap.get("url");												//查询地址
            String appKey = paramMap.get("appKey");
            String appSecret = paramMap.get("appSecret");
            String accessToken = paramMap.get("accessToken");								//接入码
            String orderId = order.get("orderId")+"";									    //上家订单号
            String upids = order.get("providerOrderId")+"";								    //外部订单号
            map.put("orderId", orderId);
            map.put("providerOrderId", upids);
            map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
            map.put("phone",order.get("phone"));
            map.put("fee",order.get("fee"));

            OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
            BmOrderCustomGetRequest req = new BmOrderCustomGetRequest();
            req.setOuterTid(orderId);

            BmOrderCustomGetResponse response = client.execute(req, accessToken);
            if(response.isSuccess()){
                // 返回的充值状态
                String status = response.getOrderDetailInfo().getRechargeState();
                log.error("云米优异常订单查询订单号为"+orderId+"的单子返回状态为"+status);
                if("1".equals(status)){
                    /**
                     * 需要修改  判断输入状态
                     */
                    // 充值成功
                    map.put("status", 1);
                    map.put("resultCode", "充值成功！");
                }else if("0".equals(status)){
                    // 充值中
                    map.put("status", 2);
                    map.put("resultCode", "充值中！");
                }else if ("9".equals(status)) {
                    // 充值失败
                    map.put("status", 0);
                    map.put("resultCode", "充值失败！");
                }else {
                    map.put("status", 3);
                    map.put("resultCode", "未知的订单状态，请与上家核实后处理！");
                }
            }else {
                map.put("status", 3);
                map.put("resultCode", "查询订单状态失败！");
            }
        }catch (Exception e){
            log.error("云米优异常订单状态查询Task出错"+e);
        }
        return map;
    }

}

