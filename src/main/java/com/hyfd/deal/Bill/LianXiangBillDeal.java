package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpKit;
import com.hyfd.common.utils.LenovoMd5Util;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class LianXiangBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			String orderId = order.get("order_id")+"";
			Map<String, Object> channel = (Map<String, Object>) order
					.get("channel");// 获取通道参数
//			String linkUrl = (String) channel.get("link_url");// 充值地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String applyChlId = paramMap.get("applyChlId");
			String operId = paramMap.get("operId");
			String cityCode = paramMap.get("cityCode");
			String provinceCode = paramMap.get("provinceCode");
			String fromType_pay = paramMap.get("fromType_pay");
			String fromType = paramMap.get("fromType");
			String checkNumIsValid = paramMap.get("checkNumIsValid");
			String payment = paramMap.get("payment");
			
			Map<String, String> hMap = new TreeMap<String, String>();
			hMap.put("applyChlId", applyChlId);
			hMap.put("operId", operId);
			hMap.put("cityCode", cityCode);
			hMap.put("provinceCode", provinceCode);
			// 验证号码是否可以充值
			JSONObject retMsg = checkNumIsValid(checkNumIsValid, fromType, orderId, phone, hMap);
			String code = retMsg.getJSONObject("responseHeader").getString("resultCode");
			String resultMessage = retMsg.getJSONObject("responseHeader").getString("resultMessage");
			
			// 号码确认成功
			if (code.equals("000000")) {

				retMsg = payment(payment, fromType_pay, orderId, phone, fee + "", hMap);
				code = retMsg.getJSONObject("responseHeader").getString("resultCode");
				resultMessage = retMsg.getJSONObject("responseHeader").getString("resultMessage");
				if (code.equals("000000")) {
					String appyId = retMsg.getString("appyId");
					// 订单状态置为充值成功
					flag = 3;
					String submitbackmsg = code + ":" + resultMessage;
					map.put("resultCode",submitbackmsg);
				}
				if (code.equals("555555")) {
					String submitbackmsg = code + ":" + resultMessage;
					flag = 0;
					map.put("resultCode",submitbackmsg);
				} else {
					log.debug("请求：提交失败！" + code);

					// 订单状态置为充值失败
					String submitbackmsg = code + ":" + resultMessage;
					flag = 4;
					map.put("resultCode",submitbackmsg);
				}
			} else if (code.equals("555555")) {
				String submitbackmsg = code + ":" + resultMessage;
				flag = 1;
				map.put("resultCode",submitbackmsg);
			} else {
				// 订单状态置为提交失败
				log.debug("请求：提交失败！" + code);
				String submitbackmsg = code + ":" + resultMessage;
				map.put("resultCode",submitbackmsg);
			}
			
		} catch (Exception e) {
			log.error("联想充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 2.2判断号码是否准许缴费
	 * 
	 * @param baseUrl
	 * @param phone
	 * @param pMap
	 */
	public JSONObject checkNumIsValid(String baseUrl, String fromType, String serialNumber, String phone,
			Map<String, String> hMap) {

		// 请求时间戳
		String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS");
		hMap.put("serialNumber", serialNumber);
		hMap.put("timestamp", timestamp);
		hMap.put("fromType", fromType);

		Map<String, Object> pMap = new TreeMap<String, Object>();
		pMap.put("requestHeader", hMap);
		pMap.put("serviceNum", phone);
		// 请求接口获取数据
		String url = baseUrl + "?data=" + BaseJson.mapToJson(pMap);
		log.info("请求地址:" + url);
		// 请求接口获取数据
		String res = "";
		try {
			res = HttpKit.get(url);
		} catch (Exception e) {
			log.info(e.getMessage());
			res = "{\"responseHeader\":{\"resultCode\":\"555555\",\"resultMessage\":\"系统异常或网络超时\"}}";
		}
		log.info("返回内容:" + res);
		return JSONObject.parseObject(res);
	}

	/**
	 * 2.8用户充值接口
	 * 
	 * @param baseUrl
	 * @param phone
	 * @param pMap
	 */
	public JSONObject payment(String baseUrl, String fromType, String serialNumber, String phone, String money,
			Map<String, String> hMap) {
		// 请求时间戳
		String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS");
		hMap.put("serialNumber", serialNumber);
		hMap.put("timestamp", timestamp);
		hMap.put("fromType", fromType);

		Map<String, Object> paymentInfo = new TreeMap<String, Object>();
		String sid = LenovoMd5Util.encodeByUppeCaseTo16(serialNumber);

		log.info("请求编号:" + serialNumber + " 订单编号:" + sid);
		// 流水号 最大18位
		paymentInfo.put("payOrgSerial", sid);
		// 操作渠道
		paymentInfo.put("payChannel", "37");
		// 付款方式
		paymentInfo.put("payStyle", "39");
		// 缴费金额
		paymentInfo.put("payAmount", money);
		// 服务号码
		paymentInfo.put("serviceNum", phone);

		Map<String, Object> pMap = new TreeMap<String, Object>();
		pMap.put("requestHeader", hMap);
		pMap.put("paymentInfo", paymentInfo);

		String url = baseUrl + "?data=" + BaseJson.mapToJson(pMap);
		log.info("请求地址:" + url);
		// 请求接口获取数据
		String res = "";
		try {
			res = HttpKit.get(url);
		} catch (Exception e) {
			log.info(e.getMessage());
			res = "{\"responseHeader\":{\"resultCode\":\"555555\",\"resultMessage\":\"系统异常或网络超时\"}}";
		}
		log.info("返回内容:" + res);
		return JSONObject.parseObject(res);
	}
}
