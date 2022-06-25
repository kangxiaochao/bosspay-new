package com.hyfd.rabbitMq;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.hyfd.service.mp.*;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;

public class ResultMqListener implements MessageListener{

	Logger log = Logger.getLogger(getClass());
	
	@Autowired
	OrderDao orderDao;//订单dao
	@Autowired
	chargeOrderSer chargeOrderSer;//话费充值Service
	@Autowired
	ProviderAccountSer providerAccountSer;//上家余额Service
	@Autowired
	ExceptionOrderDao exceptionOrderDao;//异常订单
	@Autowired
	AgentBillDiscountSer agentBillDiscountSer; // 代理商话费折扣Service
	@Autowired
	AgentAccountSer agentAccountService;
	@Autowired
	RabbitMqProducer rabbitMqProducer;//消息队列生产者
	
	@Override
	public synchronized void onMessage(Message message) {
		Map<String,Object> resultMap = SerializeUtil.getObjMapFromMessage(message);
		String orderId = (String) resultMap.get("orderId");//平台订单号
		int status = Integer.parseInt(resultMap.get("status")+"");//订单状态
		Map<String,Object> order = null;
		if(!"".equals(orderId)&&orderId != null){
			order = orderDao.selectByOrderId(orderId);
		}
		if(order==null){
			log.error("结果处理监听器查询不到订单，订单号为"+orderId);
//			rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(resultMap));
		}else{	
			String orderStatus = order.get("status")+"";
			if(orderStatus.equals("1")){//状态为处理中
				if(resultMap.containsKey("providerOrderId")){
					String providerOrderId = resultMap.get("providerOrderId")+"";//上家订单号
					order.put("providerOrderId", providerOrderId);
				}
				if(resultMap.containsKey("resultCode")){
					order.put("resultCode", order.get("resultCode")+"|"+resultMap.get("resultCode"));
				}
				if(resultMap.containsKey("voucher")){
					order.put("voucher",resultMap.get("voucher"));
				}
				if(status == 1){//成功
					Map<String,Object> orderPathRecord = new HashMap<String,Object>();
					orderPathRecord.putAll(order);
					orderPathRecord.put("status", "3");
					//充值流水
					chargeOrderSer.saveOrderPathRecord(orderPathRecord);
					boolean flag = providerAccountSer.Charge(order);//扣除上家余额
					if(!flag){
						log.error("扣除上家余额出错"+MapUtils.toString(order));
						orderDao.deleteByPrimaryKey((String)order.get("id"));
						order.put("resultCode", order.get("resultCode")+"|该笔订单扣除上家余额出现异常");
						exceptionOrderDao.insertSelective(order);//保存异常订单
					}else{
						order.put("status", 3);
						order.put("endDate", new Timestamp(System.currentTimeMillis()));
						int n = orderDao.updateOrder(order);
						if(n<1){
							log.error("更新数据库出错"+MapUtils.toString(order));
							orderDao.deleteByPrimaryKey((String)order.get("id"));
							order.put("resultCode", order.get("resultCode")+"|该笔订单更新订单状态出现异常");
							exceptionOrderDao.insertSelective(order);//保存异常订单
						}else{
							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);
							//根据订单状态新增或扣除上级代理商的利润，并生成利润变更明细
							agentAccountService.addAllParentAgentProfit(order, false);
							//处理回调下家及上家余额扣除
							chargeOrderSer.orderCallback(order,AgentCallbackSer.CallbackStatus_Success);
						}
					}
				}else if(status == 0){//失败
					//充值流水
					Map<String,Object> orderPathRecord = new HashMap<String,Object>();
					orderPathRecord.putAll(order);
					orderPathRecord.put("status", "4");
					chargeOrderSer.saveOrderPathRecord(orderPathRecord);
					//修改订单ID
					order.put("orderId", "");
					orderDao.updateByPrimaryKeySelective(order);
					//TODO 复充
					chargeOrderSer.ReCharge(order);
				}else if(status == -1){
					Map<String,Object> orderPathRecord = new HashMap<String,Object>();
					orderPathRecord.putAll(order);
					orderPathRecord.put("resultCode", "订单接受充值状态出现异常，进入异常订单列表");
					//充值流水
					chargeOrderSer.saveOrderPathRecord(orderPathRecord);
					//TODO 异常订单
					orderDao.deleteByPrimaryKey((String)order.get("id"));
					order.put("resultCode", order.get("resultCode")+"|该笔订单获取充值状态出现异常");
					exceptionOrderDao.insertSelective(order);//保存异常订单
				}
			}else{//状态不为处理中
				System.out.println("无用消息，丢弃之");
			}
		}
	}
}
