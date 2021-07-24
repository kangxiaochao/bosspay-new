package com.hyfd.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyfd.common.utils.DateTimeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class PengBoShiTask {

	private static Logger log = Logger.getLogger(YunLiuBillTask.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("3001", "缴费中");
		rltMap.put("3002", "缴费成功");
		rltMap.put("3003", "缴费失败");
		rltMap.put("3004", "未找到订单");
	}

	@Autowired
	OrderDao orderDao;

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	@Scheduled(fixedDelay = 60000)
	public void queryPengBoShiOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000039";// 鹏博士物理通道ID
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);

			String appKey = paramMap.get("appKey");
			String partnerId = paramMap.get("partnerId");
			String queryUrl = paramMap.get("queryUrl");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				String nonce_str = DateUtils.getNowTimeStamp().toString() + ((int) (Math.random() * 9000) + 1000);// 随机字符串
				if (!orderId.equals("") && orderId != null) {
					map.put("orderId", orderId);
					String ts = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
					String parms = joinUrl(nonce_str, orderId, partnerId, appKey, ts);
					String result = HttpUtils.doGet(queryUrl + "?" + parms);
					JSONObject resultJson = JSONObject.parseObject(result);
					String retcode = resultJson.getString("retcode");
					if (retcode.equals("3002")) {
						flag = 1;
					} else if (retcode.equals("3001")) {
						continue;
					} else {
						flag = 0;
					}
					log.error("鹏博士查询返回：" + result);
					map.put("resultCode", retcode + ":" + rltMap.get(retcode));
					map.put("providerOrderId", resultJson.getString("drpeng_order_id"));
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 拼接请求信息
	 * 
	 * @param nonce_str
	 * @param order_id
	 * @param partner_id
	 * @param appKey
	 * @return
	 */
	public String joinUrl(String nonce_str, String order_id, String partner_id, String appKey, String ts) {
		StringBuffer suBuffer = new StringBuffer();
		suBuffer.append("nonce_str" + nonce_str);
		suBuffer.append("order_id" + order_id);
		suBuffer.append("partner_id" + partner_id);
		suBuffer.append("ts" + ts);
		suBuffer.append(appKey);
		String sign = DigestUtils.md5Hex(suBuffer + "");// 签名

		StringBuffer url = new StringBuffer();
		url.append("partner_id=" + partner_id);
		url.append("&nonce_str=" + nonce_str);
		url.append("&order_id=" + order_id);
		url.append("&ts=" + ts);
		url.append("&sign=" + sign);


		return url.toString() + "";
	}
}
