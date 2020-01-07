package com.hyfd.task;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class ZiTengBilliTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(ZiTengBilliTask.class);

	public static Map<String, String> stateMap = new HashMap<String, String>();

	static {
		stateMap.put("0000", "处理中");
		stateMap.put("0001", "充值成功");
		stateMap.put("0002", "充值失败");
		stateMap.put("0003", "找不到相应订单");
		stateMap.put("0004", "其他错误");
	}

	@Scheduled(fixedDelay = 60000)
	public void queryZiTengOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000024";// 通道ID ~~~~~
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryUrl = paramMap.get("queryUrl"); // 取出查询接口地址
			String secretKey = paramMap.get("secretKey");// 分配给第三方系统的AppKey
			String cpid = paramMap.get("appId");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);
				String mobile = order.get("phone") + "";
				// 秘钥算法错误-修复-2016-12-07 23:00:00
				String sign = getQueryOrderSign(cpid, orderId, mobile, secretKey);
				String queryOrderUrl = "";
				queryOrderUrl += queryUrl;
				queryOrderUrl += "?cpid=" + cpid;
				queryOrderUrl += "&cp_order_no=" + orderId;
				queryOrderUrl += "&mobile_num=" + mobile;
				queryOrderUrl += "&sign=" + sign;

				log.error(queryOrderUrl);
				String responseStr = ToolHttps.get(false, queryOrderUrl);
				log.error("紫藤定时查询返回数据[" + responseStr + "]");
				if (!"".equals(responseStr) && null != responseStr) {
					Map<String, String> resultMap = treateResponseStr(responseStr);
					String retResult = resultMap.get("ret_result");
					if ("0000".equals(retResult)||"0004".equals(retResult)) {// 处理中状态
						continue;
					} else if ("0001".equals(retResult)) {
						String providerOrderId = resultMap.get("zt_order_no");
						map.put("providerOrderId", providerOrderId);
						flag = 1;
					} else {
						flag = 0;
					}
				}
				map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			log.error("紫藤查询Task出错" + e);
		}
	}

	/**
	 * 处理紫藤的返回的字符串
	 * 
	 * @param responseStr
	 * @return 0000表示处理中，0001表示充值成功，0002表示充值失败, 0003找不到相应订单 0004其他错误
	 */
	public static Map<String, String> treateResponseStr(String responseStr) {

		String[] strings = responseStr.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < strings.length; i++) {
			String[] aaString = strings[i].split("=");
			map.put(aaString[0], aaString[1]);
		}

		return map;
	}

	public static void main(String[] args) {
		String ssString = "cpid=8802&cp_order_no=20170831144900373253&mobile_num=15169129659&zt_order_no=201708311016876984&ret_result=0001&amount=9.955";
		String[] strings = ssString.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < strings.length; i++) {
			String[] aaString = strings[i].split("=");
			map.put(aaString[0], aaString[1]);
		}
		String ret_result = map.get("ret_result");
		System.out.println(ret_result);
	}

	public static String getQueryOrderSign(String cpid, String cp_order_no, String mobile_num, String secretKey) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		sb.append("cpid=" + cpid);
		sb.append("&cp_order_no=" + cp_order_no);
		sb.append("&mobile_num=" + mobile_num);
		sb.append("" + secretKey);
		result = sb.toString();
		return MD5.ToMD5(result);

	}
}
