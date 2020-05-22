package com.hyfd.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;

public class KuShangZhongXingTest {
	private static HttpClient httpClient = new HttpClient();
	public static void main(String[] args) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String phone = "17037111115";										//手机号
		String money = "10";												//金额
//		String productCode = getProductCode(money);										//对应金额的产品代码
		String productCode = "001";										//对应金额的产品代码

		String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
		map.put("orderId", orderId);
		if(productCode == null || productCode.equals("")) {
			map.put("resultCode", "该面值暂不支持！");
			map.put("status","4");
			System.out.println(map);
//			return map;
		}
//		Map<String,Object> cookie = (Map<String, Object>) order.get("cookie");
		String cookies = "JSESSIONID=BBAE37B93F0EAC99067E91D6F1BE1802;";
		if(cookies != null){
//			log.error("酷商中兴获取的Cookie为"+MapUtils.toString(cookie));
//			cookies = (String) cookie.get("cookies");
//			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
//			String defaultParameter = (String) channel.get("default_parameter");	//默认参数
//			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryUrl = "http://agent.seecom.com.cn/business/recharge/get170MobileFrom";								//查询地址
			if(queryCustInfo(queryUrl,phone,cookies)) {
				map.put("resultCode", "不支持充值该号码！");
				map.put("status","4");
//				return map;
				System.out.println(map);
			}
			String linkUrl = "http://agent.seecom.com.cn/business/recharge/rechargePhoneCost";								//充值地址
			String str = charge(linkUrl,phone,productCode,cookies);
			if(str.equals("")) {
				flag = 4;
				map.put("resultCode", "酷商中兴充值出现异常");
			}else {
				JSONObject jsonStatus = JSONObject.parseObject(str);
				if("true".equals(jsonStatus.get("status")+"")){
					flag = 3;
					map.put("resultCode", "充值成功！");
				}else {
					flag = 4;
					map.put("resultCode", jsonStatus.get("msg")+"");
				}
			}
		}else{
			flag = 4;
			map.put("resultCode", "cookie未获取到");
		}
		map.put("status", flag);
//		return map;
		System.out.println(map);
	}
	
	
	/**
	 * 验证是否支持充值该号码
	 * @param phone
	 * @param cookies
	 * @return 返回 false 代表验证通过，返回 true 代表不支持该号码的充值
	 */
	public static boolean queryCustInfo(String queryUrl,String phone,String cookies){
		try{
			PostMethod loginMethod = new PostMethod(queryUrl);
			NameValuePair[] transferParam = {
	                new NameValuePair("msisdn", phone),
	        };
			loginMethod.setRequestHeader("Cookie",cookies);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			JSONObject jsonStatus = JSONObject.parseObject(stringBuffer.toString());
			String status = jsonStatus.get("status")+"";
			if("true".equals(status)) {
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 充值下单地址
	 * @param mobile
	 * @param productCode
	 * @param phoneBillOptional
	 * @param cookies
	 * @return
	 */
	public static String charge(String linkUer,String mobile,String productCode,String cookies){
		try{
			PostMethod loginMethod = new PostMethod(linkUer);
			NameValuePair[] transferParam = {
	                new NameValuePair("mobile",mobile),
	                new NameValuePair("productCode",productCode),
	                new NameValuePair("phoneBillOptional",""),
	        };
			loginMethod.setRequestHeader("cookie",cookies);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			return stringBuffer.toString();
		}catch(Exception e){
//			log.error("酷商中兴充值方法报错   --HttpException"+e);
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取对应金额的产品代码
	 * @param fee
	 * @param cookies
	 * @param operator
	 * @return
	 */
	public static String getProductCode(String fee) {
		Map<String,String> map = new HashMap<>();
		// 10-001 , 20-002 , 30-003 , 50-004 , 100-005 , 200-006
		map.put("10","001");
		map.put("20","002");
		map.put("30","003");
		map.put("50","004");
		map.put("100","005");
		map.put("200","006");
		return map.get(fee);
	}
}
