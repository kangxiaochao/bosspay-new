package com.hyfd.rabbitMq;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.service.mp.AgentAccountSer;
import com.hyfd.service.mp.chargeOrderSer;
import com.rabbitmq.client.Channel;

@Component
public class MoneyMqListener implements MessageListener{
	
	Logger log = Logger.getLogger(getClass());
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	chargeOrderSer cOSer;
	@Autowired
	ExceptionOrderDao exceptionOrderDao;
	@Autowired
	AgentAccountSer agentAccountService;
	@Autowired
	RabbitMqProducer rabbitMqProducer;
	
	@Override
	public synchronized void onMessage(Message message) {
		try {
			//接收扣款队列中的消息
			Map<String,Object> msgMap = SerializeUtil.getObjMapFromMessage(message);
			boolean chargeFlag = agentAccountService.Charge(msgMap);//
			@SuppressWarnings("unchecked")
			Map<String,Object> order = (Map<String, Object>) msgMap.get("order");
			if(chargeFlag){//扣款成功进入充值队列
				rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Order_QueueKey, SerializeUtil.getStrFromObj(order));
			}else{//扣款失败将订单置为提交失败
				//TODO 人工处理异常的订单
				log.error("为代理商扣款失败"+MapUtils.toString(order));
				//TODO 异常订单
//				orderDao.deleteByPrimaryKey((String)order.get("id"));
				order.put("status", "2");
				order.put("resultCode", "该笔订单为代理商扣款失败");
				orderDao.updateByPrimaryKeySelective(order);
//				exceptionOrderDao.insertSelective(order);//保存异常订单
//				cOSer.dealOrderFail(order, "2", "该笔订单为代理商扣款失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("扣款出现异常"+ExceptionUtils.getExceptionMessage(e));
			e.printStackTrace();
		}
		
	}
}
