package com.hyfd.deal;

import java.util.Map;

public interface BaseDeal {

	/**
	 * 业务处理类需要实现的接口
	 * @author lks 2016年12月10日下午3:35:07
	 * @param order
	 * @return Map 中 status = 0是失败，1是成功，-1是异常状态，
	 * 				 orderId是平台订单号
	 * 				 providerOrderId是上家订单号（非必须）
	 * 				 resultCode是上家状态返回码
	 */
	public Map<String,Object> deal(Map<String,Object> order);
	
}
