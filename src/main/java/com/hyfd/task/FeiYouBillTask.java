package com.hyfd.task;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class FeiYouBillTask {

	private static Logger log = Logger.getLogger(FeiYouBillTask.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("A00", "操作成功");
		rltMap.put("A11", "参数错误");
		rltMap.put("A20", "请求超速");
		rltMap.put("A20", "用户状态正常 ");
		rltMap.put("A21", "用户不存在");
		rltMap.put("A22", "用户状态暂停");
		rltMap.put("A23", "用户状态测试");
		rltMap.put("A24", "用户状态不可用");
		rltMap.put("A30", "签名正确");
		rltMap.put("A31", "签名错误");
		rltMap.put("A40", "订单号不存在错误");
		rltMap.put("A41", "订单号重复");
		rltMap.put("A52", "余额不足");
	}
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	
	@Scheduled(fixedDelay = 60000)
	public void queryFeiYouOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000041";// 飞游全国移动物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appkey = paramMap.get("appKey");
			String secret = paramMap.get("secret");
			String queryUrl = paramMap.get("queryUrl");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				if(!orderId.equals("") && orderId != null) {
					map.put("orderId", orderId);
					
					String timeStamp = DateUtils.getNowTimeToSec();
					String params = jointUrl(appkey, orderId, timeStamp, secret);
					JSONObject queryResultJson = JSONObject.parseObject(HttpUtils.doPost(queryUrl, params));
					log.error("飞游全国移动查询返回：" + queryResultJson);
					String code = queryResultJson.getString("code");
					if("A00".equals(code)) {
						String voucher = "";
						String status = queryResultJson.getString("status");
						if(status.equals("2")) {//2成功1充值中其余失败
							flag = 1;
						}else if(status.equals("1")) {
							continue;
						}else {
							flag = 0;
						}
						if(queryResultJson.containsKey("srcOrderId")){
							voucher = queryResultJson.getString("srcOrderId");
							map.put("voucher",queryResultJson.getString("srcOrderId"));
						}
						map.put("providerOrderId", queryResultJson.getString("orderID"));
						map.put("resultCode", code + ":" + rltMap.get(code)+"流水号"+voucher);
					}else {
						flag = 0;
						map.put("resultCode", code + ":" + rltMap.get(code)+"流水号");
					}
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("飞游全国移动查询Task出错" + e);
		}
	}
	
	public String jointUrl(String appkey,String outOrderID,String timeStamp,String secret){
		StringBuffer url = new StringBuffer();
		try {
			StringBuffer sing = new StringBuffer();
			sing.append("appKey="+appkey);
			sing.append("&outOrderID="+outOrderID);
			sing.append("&timeStamp="+timeStamp);
			sing.append(secret);
			
			url.append("appKey="+appkey);
			url.append("&outOrderID="+outOrderID);
			url.append("&timeStamp="+timeStamp);
			url.append("&sign="+MD5Util.MD5Encode(sing + "", "UTF-8"));
			log.error("飞游全国移动查询提交信息为：" + url);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url + "";
	}
}
