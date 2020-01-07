package com.hyfd.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
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
 * 云流查询
 * @author Administrator
 */
@Component
public class YunLiuBillTask {

	private static Logger log = Logger.getLogger(YunLiuBillTask.class);
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	
	@Scheduled(fixedDelay = 60000)
	public void queryYunLiuOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000036";// 云流全国物理通道ID ~~~~~
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

					JSONArray reportsArr = queryResultJson.getJSONArray("Reports");
					log.info("云流全国查询返回"+queryResultJson);
					JSONObject yunliu = reportsArr.getJSONObject(0);
					String statCode = yunliu.getString("Status"); // 2.充值中(该状态时号码可能为空)3.提交失败 4.充值成功 5.充值失败 7.未知(待处理)
					if ("4".equals(statCode)) {
						flag = 1;
					} else if("2".equals(statCode)||"7".equals(statCode)){
						continue;
					} else {
						flag = 0;
					}
					if(yunliu.containsKey("ReportCode")){
						String voucher = yunliu.get("ReportCode")+"";
						Pattern pattern = Pattern.compile("0034[0-9]+");
						Matcher matcher = pattern.matcher(voucher);
						if(matcher.find()) {
							map.put("voucher", matcher.group(0));
						}
					}
					map.put("providerOrderId", yunliu.get("TaskID")+"");
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("云流全国查询Task出错" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询订单信息
	 * @param paramMap
	 * @param orderId
	 * @return
	 */
	public JSONObject orderQuery(Map<String, String> map,String outTradeNo) {
		String account = map.get("account");
		String url = map.get("queryUrl");
		String apiKey = map.get("apiKey");//密钥
		String action = map.get("actions");
		String v = map.get("v");
		Map<String, String> maps = new HashMap<>();
		maps.put("account", account);
		String sign = getSign(maps,apiKey);
		
		url += "?v="+ v +"&action=" + action + "&account=" + account + "&outTradeNo=" 
				+ outTradeNo + "&sendTime=" + DateUtils.getNowTime("yyyy-MM-dd") + "&sign=" + sign;
		log.error("云流全国查询请求信息" + url);
		
		return JSONObject.parseObject(HttpUtils.doGet(url));
	}
	
	public static String getSign(Map<String, String> map,String apiKey) {
		String result = "";
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			
			// 构造签名键值对的格式
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> item : infoIds) {
				if (item.getKey() != null || item.getKey() != "") {
					String key = item.getKey();
					String val = item.getValue();
					if (!(val == "" || val == null)) {
						sb.append(key + "=" + val +"&");
					}
				}
 
			}
			sb.append("key="+apiKey);
			result = sb.toString();
			//进行MD5加密
			result = MD5.ToMD5(result).toLowerCase();
		} catch (Exception e) {
			return null;
		}
		return result;
	}
}
