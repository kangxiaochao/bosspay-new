package com.hyfd.exceptionTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.ToolMD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.rabbitMq.RabbitMqProducer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class HuaJiTongXinExceptionTask implements BaseTask {

	RabbitMqProducer mqProducer;// 消息队列生产者
	private static Logger log = Logger.getLogger(HuaJiTongXinExceptionTask.class);

	@Override
	public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			String defaultParameter = (String) channelMap.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String appKey = paramMap.get("appKey");
			String password = paramMap.get("passWord");
			String linkUrl = paramMap.get("linkUrl");
			String method = paramMap.get("chargeOrderQryUrl");
			String timestamp = format.format(new Date());// 格式 ：yyyymmddhhmiss调用时间
			String vserion = paramMap.get("vserion");// 版本号 1.0 目前1.0是固定值。如有变动，上游会通知
			
			String orderId = order.get("orderId") + "";
			map.put("orderId", orderId);
			map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
			map.put("providerOrderId",order.get("providerOrderId") != null ? order.get("providerOrderId") : "");
			map.put("phone",order.get("phone"));
			map.put("fee",order.get("fee"));
			String serial = format.format(new Date()) + getFiveSquece();// 流水号，自定义流水

			String chargeOrderQryJson = getChargeOrderQryJson(password, appKey, method, timestamp, vserion, serial, orderId);
			String data = ToolHttp.post(false, linkUrl, chargeOrderQryJson, "json/html");

			if (data == null) {
				map.put("status", 3);
				map.put("resultCode", "查询接口返回信息为空！");
				return map;
			}

			JSONObject result = null;
			int status = 1;
			String success = "";
			String message = "";
			try {
				result = JSON.parseObject(data);
				status = result.getInteger("status");
				success = result.getString("success");
				message = result.getString("message");
			} catch (Exception e) {
				log.error("话机通信返回数据解析异常:"+e.getMessage());
			}

			//判断返回结果,成功0, 1:失败
			if(status == 0 && "true".equals(success)){
				map.put("status", 1);
				map.put("resultCode", "充值成功！");
			}else if(status == 1 || "false".equals(success)){
				map.put("status", 0);
				map.put("resultCode", "充值失败："+message);
			}else{
				map.put("status", 3);
				map.put("resultCode", "未知的订单状态，请与上家核实后处理："+message);
			}
		} catch (Exception e) {
			log.error("话机通信查询Task出错" + e.getMessage());
		}
		return map;
	}
	
	/**
	 * 获取5位序列码
	 * 
	 * @return
	 */
	private static int getFiveSquece() {
		return (int) ((Math.random() * 9 + 1) * 10000);
	}
	
	/**
	 * @功能描述：	组合请求参数
	 *
	 * @作者：zhangpj		@创建时间：2017年8月26日
	 * @param password
	 * @param appkey
	 * @param method
	 * @param timestamp
	 * @param vserion
	 * @param serial
	 * @param esReqId
	 * @return
	 */
	private static String getChargeOrderQryJson(String password,String appkey,String method,String timestamp,String vserion,String serial,String esReqId){
		JSONObject requestJson = new JSONObject();
		requestJson.put("esReqId", esReqId);

		String signSrc = serial + requestJson.toString() + password;
		String sign = new String();
		try {
			sign = ToolMD5.encodeMD5Hex(signSrc).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		JSONObject json = new JSONObject();
		json.put("appkey", appkey);
		json.put("method", method);
		json.put("timestamp", timestamp);
		json.put("vserion", vserion);
		json.put("sign", sign);
		json.put("serial", serial);
		json.put("request", requestJson);

		log.info(json.toString());
		return json.toString();
	}
}
