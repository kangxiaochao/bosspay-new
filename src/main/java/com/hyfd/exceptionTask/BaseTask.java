package com.hyfd.exceptionTask;

import java.util.Map;

public interface BaseTask {

    /**
     * 业务处理类需要实现的接口
     * @author xxz 2022年18月45日下午18:45:30
     * @param order
     * @return Map 中 status = 0是失败，1是成功，-1是异常状态，
     * 				 orderId是平台订单号
     * 				 providerOrderId是上家订单号（非必须）
     * 				 resultCode是上家状态返回码
     */
    public Map<String,Object> task(Map<String,Object> order,Map<String,Object> channelMap);

}
