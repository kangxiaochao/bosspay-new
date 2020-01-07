package com.hyfd.deal.Bill;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class HaiKouYuShuiBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(HaiKouYuShuiBillDeal.class);

	/*
	 * 海口雨水文化 业务处理 HaiKouYuShui
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;

		try {

			String phone = (String) order.get("phone"); // 手机号码
			Double fee = Double.parseDouble(order.get("fee") + ""); // 话充 以分为单位
																	// 流量以M为单位
																	// 充值的金额 只能为
																	// 10 20 50
																	// 100
			String rechargeAmount = fee.intValue() * 100 + ""; // 话充只取整数部分

			Map<String, Object> channel = (Map<String, Object>) order.get("channel"); // 获取参数通道
			String url = channel.get("link_url").toString(); // 接口地址
																// "http://47.99.174.242/core/api/buyProduct.do"
			String defaultParameter = (String) channel.get("default_parameter"); // 用于获取默认的参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String partnerId = paramMap.get("partnerId") + ""; // 商户编码
			String productType = paramMap.get("productType") + ""; // 商品编码 1.
																	// 手机话费充值
																	// 4.国内流量
			String notifyUrl = paramMap.get("notifyUrl") + ""; // 回调地址
			String sign = paramMap.get("sign"); // 签名

			String partnerRequestTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss"); // 系统时间
			String partnerOrderId = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + "17705305254"
					+ randomNumber(); // 商户流水号

			map.put("orderId", partnerOrderId); // return 返回平台订单号

			// 按照key值对json进行排序
			JSONObject json = new JSONObject(new TreeMap<String, Object>());
			json.put("partnerId", partnerId);
			json.put("partnerOrderId", partnerOrderId);
			json.put("partnerRequestTime", partnerRequestTime);
			json.put("productType", "1");
			json.put("phone", phone);
			json.put("rechargeAmount", rechargeAmount);
			json.put("notifyUrl", notifyUrl);
			String string1 = "";
			for (Entry<String, Object> entry : json.entrySet()) {
				string1 = string1 + entry.getValue() + "|";
			}
			String sign2 = MD5.MD5(string1 + sign).toLowerCase(); // 生成加密后sign
			json.put("sign", sign2);

			String result = sendJson(url, json);
			if (null == result || result.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "海口雨水文化话费充值,号码[" + phone + "],金额[" + fee + "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			} else {

				// {"resultCode":"opSuccess","bizData":"{\"orderId\":\"20190318102226609400024\",\"partnerOrderId\":\"2019031810222656517705305254IcjD\"}","resultMessage":""}"
				JSONObject response = JSONObject.parseObject(result);
				String resultCode = response.getString("resultCode"); // 返回结果码
				String resultMessage = response.getString("resultMessage"); // 返回结果码的说明
				if (resultCode.equals("opSuccess")) {
					map.put("resultCode", resultCode + ":" + resultMessage); // 执行结果说明
					JSONObject bizData = response.getJSONObject("bizData");
					map.put("providerOrderId", bizData.getString("orderId")); // 上家订单号（非必须）
					flag = 1; // 提交成功
				} else {
					map.put("resultCode", resultCode);
					flag = 0; // 提交失败
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("海口雨水文化话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status", flag); // return 返回执行的结果
		return map;
	}

	/*
	 * 用于生成4位字符 添加到商品流水号后
	 */
	public static String randomNumber() {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			String str = random.nextInt(2) % 2 == 0 ? "num" : "char";
			if ("char".equalsIgnoreCase(str)) { // 产生字母
				int nextInt = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (nextInt + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(str)) { // 产生数字
				val += String.valueOf(random.nextInt(10));
			}
		}

		return val;
	}

	/*
	 * json提交
	 */
	public static String sendJson(String url, JSONObject json) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		String result = "";
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");// 发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(res.getEntity());// 返回json格式：
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
