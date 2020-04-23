package com.hyfd.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

public class TestTo {

	public static void main(String [] args) {


		try {

			String str = "50.0";
			str = new Double(str).intValue()+"";
			System.out.println(str);
			
			String link_url = "http://219.139.153.17:50080/API";//充值连接
			String agentAccount = "jnhb111";// 商户账号
			String action = "CX";//充值交易指令码
			String appkey = "DC01A985222ED12A";//密钥
			
				int flag = 2;
				String orderId = "202004170814451393254";
				String params = jointUrl(action, orderId, agentAccount,appkey);
				String result = HttpUtils.doPost(link_url, params);
				JSONObject resultJson = JSONObject.parseObject(result);
				String code = resultJson.getString("orderStatuInt");
				
				System.out.println(code+"----"+resultJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param action 交易指令码
	 * @param orderId 订单号
	 * @param agentAccount 商户名称
	 * @return
	 */
	public static String jointUrl(String action,String orderId,String agentAccount,String appkey) {
		
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
