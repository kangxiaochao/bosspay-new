package com.hyfd.rabbitMq;

public interface MqProducer {

	/**
	 * 发送消息到指定的队列
	 * @param queueKey 队列的key
 	 * @param object 消息
	 */
	public void sendDataToQueue(String queueKey,Object object);
	
}
