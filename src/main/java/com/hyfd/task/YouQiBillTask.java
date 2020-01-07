package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

/**
 * 优启查询
 * 
 * @author Administrator
 *
 */
@Component
public class YouQiBillTask {

	private static Logger log = Logger.getLogger(TuTuBiOrderTask.class);

	@Autowired
	OrderDao orderDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	
	@Scheduled(fixedDelay = 60000)
	public void queryYouQiOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000032";// 优启物理通道ID ~~~~~
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

					Map<String, String> queryResultJson = orderQuery(paramMap, orderId);

					JSONObject dataJson = (JSONObject) JSONObject.toJSON(queryResultJson.get("data"));
					log.info("优启查询返回"+dataJson);
					String statCode = dataJson.getString("status"); // 交易状态码(0：充值成功，1正在充值，2充值失败)
					if ("0".equals(statCode)) {
						flag = 1;
					} else if ("2".equals(statCode)) {
						flag = 0;
					}
					map.put("providerOrderId", dataJson.getString("oid"));
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("优启查询Task出错" + e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private	Map<String, String> orderQuery(Map<String, String> map,String curids){
		Map<String, String> result = new HashMap<>();
		try {
			String appId = map.get("appId");
			String appKey = map.get("appKey");
			String queryUrl = map.get("queryUrl");
			String timestamp = DateUtils.getNowTimeToSec();
			String parm = md5Sign(appId, appKey, curids, timestamp);
			result = JSONObject.toJavaObject(JSONObject.parseObject(HttpUtils.doGet(queryUrl + "?" + parm)), Map.class);
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String md5Sign(String appid,String appKey,String order_id,String timestamp) {
		StringBuilder sb = new StringBuilder();
		sb.append("appid=").append(appid);
		sb.append("&appkey=").append(appKey);
		sb.append("&order_id=").append(order_id);
		sb.append("&timestamp=").append(timestamp);
		String md5 = MD5.ToMD5(sb.toString());
		StringBuilder parm = new StringBuilder();
		parm.append("appid=").append(appid);
		parm.append("&timestamp=").append(timestamp);
		parm.append("&order_id=").append(order_id);
		parm.append("&sign=").append(md5);
		return parm.toString();
	}
}
