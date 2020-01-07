package com.hyfd.rabbitMq;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.service.mp.AgentBillDiscountSer;
import com.hyfd.service.mp.AgentCallbackSer;
import com.hyfd.service.mp.ProviderAccountSer;
import com.hyfd.service.mp.chargeOrderSer;

public class StatusMqListener implements MessageListener{

	Logger log = Logger.getLogger(getClass());
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	chargeOrderSer chargeOrderSer;//话费充值方法
	@Autowired
	ExceptionOrderDao exceptionOrderDao;//异常订单
	@Autowired
	ProviderAccountSer providerAccountSer;//上家余额Service
	
	@Autowired
	RabbitMqProducer rabbitMqProducer;//消息队列生产者
	@Autowired
	AgentBillDiscountSer agentBillDiscountSer; // 代理商话费折扣Service
	
	@Override
	public synchronized void onMessage(Message message) {
		try{
			Map<String,Object> result = SerializeUtil.getObjMapFromMessage(message);//获取订单对象
			@SuppressWarnings("unchecked")
			Map<String,Object> order = (Map<String, Object>) result.get("order");
			int flag = (int) result.get("status");//获取充值的状态
			String orderId = (String) result.get("orderId");//平台订单号
			if(result.containsKey("providerOrderId")){//上家订单号
				order.put("providerOrderId", (String) result.get("providerOrderId"));
			}
			if(result.containsKey("resultCode")){
				order.put("resultCode", result.get("resultCode")+"");
			}
			if(flag == 1){//提交成功
				order.put("status", "1");
				order.put("orderId", orderId);
				orderDao.updateByPrimaryKeySelective(order);//更新订单状态
			}else if(flag == 0){//提交失败
				order.put("orderId", orderId);
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "2");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				chargeOrderSer.saveOrderPathRecord(orderPathRecord);
				//TODO 复充
				orderDao.updateByPrimaryKeySelective(order);//更新订单状态
				chargeOrderSer.ReCharge(order);
			}else if(flag == -1){//异常情况
				order.put("orderId", orderId);
				order.put("resultCode", order.get("resultCode")+"|该笔订单获取提交状态出现异常");
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("resultCode", "订单接受提交状态出现异常，进入异常订单列表");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				chargeOrderSer.saveOrderPathRecord(orderPathRecord);
				//TODO 异常订单
				orderDao.deleteByPrimaryKey((String)order.get("id"));
				exceptionOrderDao.insertSelective(order);//保存异常订单
			}else if(flag == 3){//实时接口成功状态
				order.put("status", 3);
				order.put("orderId", orderId);
				order.put("endDate", new Timestamp(System.currentTimeMillis()));
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "3");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				chargeOrderSer.saveOrderPathRecord(orderPathRecord);
				boolean ChargeFlag = providerAccountSer.Charge(order);//扣除上家余额
				if(!ChargeFlag){
					log.error("扣除上家余额出错"+MapUtils.toString(order));
					orderDao.deleteByPrimaryKey((String)order.get("id"));
					order.put("resultCode", order.get("resultCode")+"|该笔订单扣除上家余额出现异常");
					exceptionOrderDao.insertSelective(order);//保存异常订单
				}else{
					int n = orderDao.updateByPrimaryKeySelective(order);
					if(n<1){
						log.error("更新数据库出错"+MapUtils.toString(order));
						orderDao.deleteByPrimaryKey((String)order.get("id"));
						order.put("resultCode", order.get("resultCode")+"|该笔订单更新订单状态出现异常");
						exceptionOrderDao.insertSelective(order);//保存异常订单
					}
					
					// 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
					
					Map<String,Object> callbackMap = new HashMap<String,Object>();
					callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
					callbackMap.put("order", order);
					rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey, SerializeUtil.getStrFromObj(callbackMap));
				}
			}else if(flag == 4){//实时接口失败状态
				order.put("orderId", orderId);
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "4");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				chargeOrderSer.saveOrderPathRecord(orderPathRecord);
				orderDao.updateByPrimaryKeySelective(order);//更新订单状态
				chargeOrderSer.ReCharge(order);
			}else if(flag == 9){//请求、响应超时
				order.put("status", 9);
				order.put("resultCode", "请求或响应超时");
				orderDao.updateByPrimaryKeySelective(order);
			}
			//流水
			//chargeOrderSer.saveOrderPathRecord(order);
		}catch(Exception e){
			//TODO 异常订单
			log.error("订单状态监听器出现异常"+ExceptionUtils.getExceptionMessage(e));
		}
	}

}
