package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;

@Component
public class OrderTask {

	@Autowired
	OrderDao orderDao;//订单
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;
	
	Logger log = Logger.getLogger(getClass());
	
//	@Scheduled(fixedDelay = 60000)
	public void queryInprocessOrder(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("status", 1);
		List<Map<String,Object>> orderList = orderDao.selectByTask(param);
		for(int i = 0 , size = orderList.size() ; i < size ; i++){
			Map<String,Object> order = orderList.get(i);//订单对象
			String dispatcherProviderId = (String) order.get("dispatcherProviderId");//物理通道Id
			String orderId = (String) order.get("orderId");//订单号
			String phone = (String) order.get("phone");//手机号
			String dataType = (String) order.get("dataType");//
			String providerOrderId = (String) order.get("providerOrderId");//
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(dispatcherProviderId);
			String providerMark = channel.get("providerMark")+"";//通道备注
			
		}
	}
	
}
