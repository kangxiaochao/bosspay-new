package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.Bill.XiangjiaofeiBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeOrderInfoRequest;
import com.qianmi.open.api.response.RechargeOrderInfoResponse;

@Component
public class XiangJiaoFeiTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	
	@Autowired
	OrderDao orderDao;//订单
	@Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	private static Logger log = Logger.getLogger(XiangJiaoFeiTask.class);
	
	
	@Scheduled(fixedDelay = 60000)
	public void queryXiangJiaoFeiOrder(){
		Map<String,Object> map = new HashMap<String,Object>();

		try{
			String id = "2000000018";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String url = paramMap.get("queryUrl");//查询地址
			String key = paramMap.get("key");
			String agentID = paramMap.get("agentID");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			for(Map<String,Object> order : orderList){
				int flag = 2;
				String orderId = order.get("orderId")+"";
				map.put("orderId", orderId);
				
				// 获取订单号
				// 根据我方平台订单编号发起话费订单查询
				String queryData = XiangjiaofeiBillDeal.query(agentID, key, url, null, orderId);
				
				if (null != queryData) {
					String[] backDataArray = queryData.split("\\|");
					// 5.查询成功
					if (backDataArray[0].equalsIgnoreCase("SUCCESS")) {
						// stateKey为1充值成功,2充值失败,则回调下游客户,3订单不存在
						String stateKey = backDataArray[1];
						if ("1".equals(stateKey)) {
							// 充值成功,设置充值结果为1
							flag = 1;
						}else if ("2".equals(stateKey)) {
							// 充值失败,设置充值结果为0
							flag = 0;
						}else if ("3".equals(stateKey)){
							flag = 0;
						}else {
							continue;
						}
					}
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				
				}else {
					log.error("享缴费查询出错"+orderId);
				}
			}
		}catch(Exception e){
			log.error("享缴费查询Task出错"+e);
		}
	}	
}
