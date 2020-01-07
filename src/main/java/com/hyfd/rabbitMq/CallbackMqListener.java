package com.hyfd.rabbitMq;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.service.mp.AgentCallbackSer;

public class CallbackMqListener implements MessageListener {

	Logger log = Logger.getLogger(getClass());

	@Autowired
	OrderDao orderDao;

	@Autowired
	AgentDao agentdao;

	@Autowired
	AgentCallbackSer agentCallbackSer;

	@Override
	public synchronized void onMessage(Message message) {
		try {
			Map<String, Object> map = SerializeUtil.getObjMapFromMessage(message);
			@SuppressWarnings("unchecked")
			Map<String, Object> order = (Map<String, Object>) map.get("order");
			String status = (String) map.get("status");
			String agentId = order.get("agentId") + "";
			Map<String, Object> agent = agentdao.selectById(agentId);
			String name = agent.get("name") + "";
			boolean flag = false;
			if ("xicheng".equals(name)) {
				flag = agentCallbackSer.xiChengCallback(order, status);
			}else if(order.containsKey("voucher")) {
				flag = agentCallbackSer.kongChongCallback(order, status);
			} else {
				String callbackUrl = (String) order.get("callbackUrl");
				if (callbackUrl.contains("baidu")) {
					flag = true;
				} else {
					flag = agentCallbackSer.callback(order, status);
				}

			}

			if (flag) {
				log.info(MapUtils.toString(order) + "回调成功");
				order.put("callbackStatus", "1");
				// 保存回调时间

			} else {
				log.error("回调失败" + MapUtils.toString(order));
			}
			Timestamp end = new Timestamp(System.currentTimeMillis());// 订单结束时间
			order.put("callbackDate", end);
			// 获取订单提交时间
			String id = order.get("id") + "";
			Map<String, Object> orderData = orderDao.selectById(id);
			Date createDate = DateTimeUtils.parseDate(orderData.get("createDate") + "", "yyyy-MM-dd HH:mm:ss");
			Timestamp start = new Timestamp(createDate.getTime());// 订单开始时间
			order.put("consumedTime", end.getTime() - start.getTime());// 保存订单消耗时间
			orderDao.updateByPrimaryKeySelective(order);
		} catch (Exception e) {
			log.error("向下家回调出错" + ExceptionUtils.getExceptionMessage(e));
			e.printStackTrace();
		}
	}
}
