package com.hyfd.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.YunpuOrderDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class YunPuTask {
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao; // 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单

	@Autowired
	YunpuOrderDao yunpuOrderDao;

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(HaiKouYuShuiTask.class);

	// queryUrl
	@Scheduled(fixedDelay = 60000) // 每隔60秒调用一次
	public void StatusReportRetrieval() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id="2000000043";															
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);		//获取通道的数据
			String defaultParameter = channel.get("default_parameter")+"";						//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryUrl = paramMap.get("queryUrl");											//状态报告获取URL
			String account = paramMap.get("account");											//用户名
			String password = paramMap.get("password")+"";										//密码
			String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");			//系统时间
			JSONObject json = new JSONObject(new TreeMap<String, Object>());
			json.put("account",account);
			json.put("timestamp",timestamp);
			String sign = MD5.MD5(account+password+timestamp).toLowerCase();					//校验码
			json.put("sign",sign);		
			//用于查询  orderId:流量话充平台订单号     partnerOrderId:上家订单号
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			for(Map<String,Object> order : orderList){
				int flag = 2;
				String data = order.get("providerOrderId")+"";
				String productId = order.get("orderId")+"";
				map.put("orderId",productId);
				String str = ToolHttp.post(false,queryUrl,json.toJSONString(), null);
				Map<String,Object> map2 = new HashMap<>();	//存放查询的订单信息，用于添加到mp_yunpu_order数据库中
				if(str == null || str.equals("")) {
					log.error("云普文化查询job返回值为空 orderids=" + productId);
				}else {
					try {
						JSONArray jsonArray = JSONArray.parseArray(str);
						for (int i = 0; i < jsonArray.size(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							map2.put("id", object.getString("id"));
							map2.put("outOrderId", object.getString("outOrderId"));
							map2.put("dest", object.getString("dest"));
							map2.put("reportStatus", object.getString("reportStatus"));
							map2.put("reportDetail", object.getString("reportDetail"));
							int a = yunpuOrderDao.insert(map2);
							if (a <= 0) {
								log.error("云普 mp_yunpu_order表数据添加失败." + object);
							}
							// 根据外部订单号查询出对应的的订单信息
							Map<String, Object> map3 = orderDao.selectByOrderId(object.getString("outOrderId"));
							if (map3.get("status").equals("1") && object.getString("reportStatus").equals("1")) { // 判读是否充值成功
								flag = 1; // 1成功 0失败
							} else {
								flag = 0;
							}
							map.put("status", flag);
							map.put("resultCode",
									object.getString("reportStatus") + ":" + object.getString("reportDetail"));
							mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,
									SerializeUtil.getStrFromObj(map));
						}
					} catch (Exception e) {
						log.error("云普查询返回值为jsonObject格式");
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
