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
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeOrderInfoRequest;
import com.qianmi.open.api.response.RechargeOrderInfoResponse;

@Component
public class QianMiTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	OrderDao orderDao;//订单
	@Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	private static Logger log = Logger.getLogger(QianMiTask.class);
	
	
	@Scheduled(fixedDelay = 60000)
	public void queryQianMiOrder(){
		Map<String,Object> map = new HashMap<String,Object>();

		try{
			String id = "2000000026";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String url = paramMap.get("url");//查询地址
			String appKey = paramMap.get("appKey");
			String appSecret = paramMap.get("appSecret");
			String accessToken = paramMap.get("AccessToken");
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			for(Map<String,Object> order : orderList){
				int flag = 2;
//				String providerOrderId = order.get("orderids")+"";//上家订单号
				String orderId = order.get("orderId")+"";
				map.put("orderId", orderId);
				
//				String agentids=order.get("agentids")+"";
//				String terminalids=order.get("terminalids")+"";
//				String agentkk=order.get("agentdisprice")+"";
//				String terminalkk=order.get("terminaldisprice")+"";
				String upids = order.get("providerOrderId")+"";
				
				OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
				RechargeOrderInfoRequest req = new RechargeOrderInfoRequest();
				req.setBillId(upids);
				
				RechargeOrderInfoResponse response = client.execute(req, accessToken);
				if(response.isSuccess()){
					// 返回的充值状态
					String status = response.getOrderDetailInfo().getRechargeState();
					log.equals("千米查询订单号为"+orderId+"的单子返回状态为"+status);
					if("1".equals(status)){
						// 充值成功
						flag = 1;
					}else if("0".equals(status)){
						// 充值中
						continue;
					}else if ("9".equals(status)) {
						// 充值失败
						flag = 0;
					}else {
						continue;
					}
				}else {
					log.error("千米查询出错 ");
				}
				map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		}catch(Exception e){
			log.error("千米查询Task出错"+e);
		}
	}	
}
