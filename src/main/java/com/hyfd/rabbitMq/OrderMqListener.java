package com.hyfd.rabbitMq;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.service.mp.chargeOrderSer;

public class OrderMqListener implements MessageListener{
	
	@Autowired
	chargeOrderSer rechargeFlowSer;
	
	Logger log = Logger.getLogger(getClass());
	
	@Override
	public void onMessage(Message message) {
		//获取订单对象
		try{
			Map<String,Object> order = SerializeUtil.getObjMapFromMessage(message);
			rechargeFlowSer.dealOrder(order);
		}catch(Exception e){
			log.error("order消息监听器出现异常"+ExceptionUtils.getExceptionMessage(e));
		}
	}

}
