package com.hyfd.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

/**
 * 星博海查询
 * @author Administrator
 */
@Component
public class XingBoHaiFenShengTest {

	
	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "等待处理");
		rltMap.put("1", "暂停处理");
		rltMap.put("2", "正在处理");
		rltMap.put("6", "正在缴费");
		rltMap.put("11", "处理成功");
		rltMap.put("16", "缴费成功");
		rltMap.put("20", "取消处理");
		rltMap.put("21", "处理失败");
		rltMap.put("26", "缴费失败");
		rltMap.put("99", "冻结");
	}
	
	
	public static void main(String[] args) {
		XingBoHaiFenShengTest xbh = new XingBoHaiFenShengTest();
		xbh.queryXingBoHaiFenShengOrder();
	}

	public void queryXingBoHaiFenShengOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

			String link_url = "http://219.139.153.17:50080/API";//充值连接
			String agentAccount = "jnhb111";// 商户账号
			String action = "CX";//充值交易指令码
			String appkey = "DC01A985222ED12A";//密钥
			int flag = 2;
			String orderId = "7532020041721224013640616";
			map.put("orderId", orderId);
			String params = jointUrl(action, orderId, agentAccount,appkey);
			String result = HttpUtils.doPost(link_url, params);
			JSONObject resultJson = JSONObject.parseObject(result);
			if(resultJson.containsKey("orderStatuInt")) {
				String code = resultJson.getString("orderStatuInt");
				if(code.equals("0") || code.equals("1") || code.equals("2") || code.equals("6") || code.equals("11")) {
	
				}else if(code.equals("16")){
					flag = 1;
				}else if(code.equals("20") || code.equals("21") || code.equals("26") ){
					flag = 0;
				}else {
					flag = -1;
				}
	
				if(resultJson.containsKey("orderStatuText")){
					String orderStatuText = resultJson.getString("orderStatuText");
					String regEx="[^0-9]";  
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(orderStatuText);
					map.put("voucher", m.replaceAll("").trim());
				}
				map.put("resultCode", code + ":" + rltMap.get(code));
				map.put("providerOrderId", resultJson.getString("chargeId"));
				map.put("status", flag);
			}
	}
	
	/**
	 * 
	 * @param action 交易指令码
	 * @param orderId 订单号
	 * @param agentAccount 商户名称
	 * @return
	 */
	public String jointUrl(String action,String orderId,String agentAccount,String appkey) {
		
		StringBuffer suBuffer = new StringBuffer();
		suBuffer.append("{\"action\":\"" + action + "\",");
		suBuffer.append("\"orderId\":\"" + orderId + "\"}");
		
		String sign = DigestUtils.md5Hex(suBuffer + appkey);
		
		StringBuffer params = new StringBuffer();
		params.append("{\"sign\":\"" + sign + "\",");
		params.append("\"agentAccount\":\"" + agentAccount + "\",");
		params.append("\"busiBody\":" + suBuffer + "}");
		
		return params.toString();
	}
}
