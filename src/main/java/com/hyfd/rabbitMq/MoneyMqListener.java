package com.hyfd.rabbitMq;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

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
	chargeOrderSer rechargeFlowSer;
	@Autowired
	RabbitMqProducer rabbitMqProducer;
	
	@Override
	public void onMessage(Message message) {
		try {
			//接收扣款队列中的消息
			Map<String,Object> msgMap = SerializeUtil.getObjMapFromMessage(message);
			@SuppressWarnings("unchecked")
			Map<String,Object> order = (Map<String, Object>) msgMap.get("order");
			List<Map<String, Object>> moneyList = (List<Map<String, Object>>)msgMap.get("moneyList");// 获取扣款的list
			boolean chargeFlag = agentAccountService.Charge(order,moneyList);
			if(chargeFlag){//扣款成功进入充值队列
				rechargeFlowSer.dealOrder(order);
			}else{//扣款失败将订单置为提交失败
				//TODO 人工处理异常的订单
				log.error("为代理商扣款失败"+MapUtils.toString(order));
				//TODO 异常订单
				order.put("status", "2");
				order.put("resultCode", "该笔订单为代理商扣款失败");
				orderDao.updateByPrimaryKeySelective(order);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("扣款出现异常"+ExceptionUtils.getExceptionMessage(e));
			e.printStackTrace();
		}
	}
}
