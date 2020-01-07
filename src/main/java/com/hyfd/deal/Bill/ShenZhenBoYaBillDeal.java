package com.hyfd.deal.Bill;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ShenZhenBoYaBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("200", "成功");
		rltMap.put("1400", "通信失败");
		rltMap.put("1401", "参数错误");
		rltMap.put("1402", "产品不存在");
		rltMap.put("1403", "余额不足");
		rltMap.put("1404", "订单异常");
		rltMap.put("1405", "签名错误");
		rltMap.put("1406", "充值号码错误");
		rltMap.put("1407", "用户鉴权失败");
		rltMap.put("1408", "数字签名错误");
		rltMap.put("1409", "系统异常");
		rltMap.put("1410", "充值失败");
		rltMap.put("1411", "扩展字段");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = (String) channel.get("link_url");// 充值地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String secretKey = paramMap.get("appKey");
			String appId = paramMap.get("account");
			String orderUrl = paramMap.get("orderUrl");
			String noticeUrl = paramMap.get("noticeUrl");
			String customerOrderId = order.get("order_id")+"";
			String phone = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			int spec=Integer.parseInt(fee)*100;
			String data = order(orderUrl, appId, secretKey, customerOrderId, phone, spec,noticeUrl);
			if (data == null) {
				log.error("博亚话费充值方法出错");
			} else {
				JSONObject jsonObject = JSON.parseObject(data);
				String code = jsonObject.getString("code");
				String msg = jsonObject.getString("msg");		

				if (!"200".equals(code) && rltMap.containsKey(code)) {
					log.debug("请求：提交失败！" + code);
					flag = 0;
				} else if ("200".equals(code)) {
					flag = 1;
				}
			}
			
		} catch (Exception e) {
			log.error("博亚充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 充值
	 */
	public static String order(String url, String appId, String secretKey,
			String orderid, String phone, int rechFee, String notifyUrl) {
		String result=null;
		String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		String stringA = "appId=" + appId + "&notifyUrl=" + notifyUrl
				+ "&outTradeNo=" + orderid + "&phoneNumber=" + phone
				+ "&rechFee=" + rechFee + "&timestamp=" + timestamp;
		String stringSignTtemp = stringA + "&secretKey=" + secretKey;
		String sign = MD5(stringSignTtemp);

		String param = "appId=" + appId + "&phoneNumber=" + phone + "&rechFee="
				+ rechFee + "&notifyUrl=" + notifyUrl + "&timestamp="
				+ timestamp + "&sign=" + sign + "&outTradeNo=" + orderid;
		result = ToolHttp.post(false, url, param, null);
		return result;
	}

	/**
	 * 获取3位序列码
	 * 
	 * @return
	 */
	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100);
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
