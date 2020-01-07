package com.hyfd.task;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class ShenZhenBoYaBillTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	OrderDao orderDao;// 订单
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	private static Logger log = Logger.getLogger(ShenZhenBoYaBillTask.class);

//	@Scheduled(fixedDelay = 60000)
	public void queryBoYaBillOrder() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id = "0000000067";//博亚话费物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryOrderUrl = paramMap.get("queryOrderUrl");// 查询地址
			String appId = paramMap.get("account");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");

			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);
				String stringA="appId=" + appId + "&sysOrderId=" + orderId;
				String sign = MD5(stringA);
				String par=stringA + "&sign=" + sign;
				String result = ToolHttp.post(false, queryOrderUrl, par, null);
				if(result==null){
					log.error("博亚话费查询Task出错");
				}else{
					JSONObject jsonObject = JSON.parseObject(result);
					String code = jsonObject.getString("code");
					String msg = jsonObject.getString("msg");
					JSONObject data = jsonObject.getJSONObject("data");
					if("200".equals(code)){
						String Status = data.getString("orderStatus");
						if("1".equals(Status)){
							continue;
						}else if("2".equals(Status)){
							flag=0;
						}else if("3".equals(Status)){
							flag=1;
						}else{
							flag=-1;
						}
					}
				}
                map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			log.error("博亚话费查询Task出错" + e);
		}
	} 
	
	/**
	 * md5加密方法
	 */
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
