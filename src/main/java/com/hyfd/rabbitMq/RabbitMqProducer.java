package com.hyfd.rabbitMq;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.ExceptionUtils;

@Service
public class RabbitMqProducer implements MqProducer{

	@Autowired
	private AmqpTemplate mqTemplate;
	
	Logger log = Logger.getLogger(getClass());
	
	/**
	 * 金钱处理队列
	 */
	public static final String Money_QueueKey = "bill_money_queue";//金钱处理队列
	public static final String Order_QueueKey = "bill_order_queue";//订单处理队列
	public static final String Channel_QueueKey = "bill_channel_queue";//通道处理队列
	public static final String Batch_Channel_QueueKey = "bill_batch_channel_queue";//通道处理队列
	public static final String Status_QueueKey = "bill_status_queue";//订单提交状态队列
	public static final String Result_QueueKey = "bill_result_queue";//订单处理结果队列
	public static final String Callback_QueueKey = "bill_callback_queue";//回调下降队列
	
	@Override
	public void sendDataToQueue(String queueKey, Object object) {
		try{
			mqTemplate.convertAndSend(queueKey, object);
		}catch(Exception e){
			log.error("MqProducer-->"+queueKey+"队列的"+object+ExceptionUtils.getExceptionMessage(e));
		}
	}

}
