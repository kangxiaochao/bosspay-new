package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class QuanGuoKongChongTask {

	private static Logger log = Logger.getLogger(QuanGuoKongChongTask.class);

	@Autowired
	OrderDao orderDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	
	@Scheduled(fixedDelay = 60000)
	public void queryQuanGuoKongChongOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000033";// 全国空充物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				if(!orderId.equals("") && orderId != null) {
					map.put("orderId", orderId);

					JSONObject queryResultJson = orderQuery(paramMap, orderId);

					JSONObject dataJson = queryResultJson.getJSONObject("data");
					
					log.info("全国空充查询返回"+dataJson);
					String statCode = dataJson.getString("status"); // 交易状态码(2：充值成功，其余都是失败)
					if ("2".equals(statCode)) {
						flag = 1;
					} else if("3".equals(statCode)){
						flag = 0;
					}else {
						continue;
					}
					if(dataJson.containsKey("outOrderNo")){
						map.put("voucher",dataJson.getString("outOrderNo").replaceAll("[^(0-9)]", ""));
					}
					map.put("providerOrderId", dataJson.getString("id"));
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("全国空充查询Task出错" + e);
		}
	}
	
	
	public JSONObject orderQuery(Map<String, String> map,String serialno){
		JSONObject result;
		try {
			String userId = map.get("userId");
			String appKey = map.get("appKey");
			String queryUrl = map.get("queryUrl");
			String sign = MD5.ToMD5(serialno + userId + appKey);
			String parm = "userId=" + userId + "&serialno=" + serialno + "&sign=" + sign;
			result = JSONObject.parseObject(HttpUtils.doGets(queryUrl + "?" + parm));
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}
}
