package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.dao.mp.OrderDao;
import com.hyfd.deal.Bill.HyfdBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class HyfdTask {

	@Autowired
	OrderDao orderDao;
	@Autowired
	RabbitMqProducer mqProducer;
	
	//@Scheduled(fixedDelay = 60000)
	public void queryOrder(){
		Map<String,Object> param = new HashMap<String,Object>();
		//param.put("dispatcherProviderId", "");
		param.put("status", 1);
		List<Map<String,Object>> orderIdList = orderDao.selectAll(param);
		for(int i = 0 , size = orderIdList.size() ; i < size ; i ++){
			String orderId = (String) orderIdList.get(i).get("order_id");
			Map<String,Object> queryMap = new HyfdBillDeal().query(orderId);
			int flag = (int) queryMap.get("flag");
			String providerOrderId = (String) queryMap.get("providerOrderId");//运营商ID
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("orderId", orderId);
			map.put("providerOrderId", providerOrderId);
			if(flag == 2){
				continue;
			}else {
				map.put("status", flag);
			}
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
	}
	
}
