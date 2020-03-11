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
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.deal.BaseDeal;

import jxl.common.Logger;

public class KuShangTest{

//	Map<String>
	
	public static void main(String[] args) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
				String cookies = "JSESSIONID=FEEBFDB154816F916F37D01AC3B542CE;";				
				String mobile = "17037111115";//手机号
				// 10-001 , 20-002 , 30-003 , 50-004 , 100-005 , 200-006
				String fee = "200";																//金额 充值金额是产品代码替代
				String productCode = getProductCode(fee);										//获取对应金额的产品代码
				String phoneBillOptional = "";
				String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + mobile + GenerateData.getIntData(9, 2);
				map.put("orderId", orderId);
				if(productCode == null || productCode.equals("")) {
					map.put("resultCode", "该面值暂不支持！");
					map.put("status","4");
				}
				String phoneInfo = queryCustInfo(mobile,cookies);								//查询是否支持该号段
				JSONObject infoJson = JSONObject.parseObject(phoneInfo);					
				//{"result":"湖北武汉-移动-中兴视通","msg":"湖北武汉-移动-中兴视通","balance":"19.27","userName":"*如靖","status":true}
				if("true".equals(infoJson.get("status")+"")){
					String chargeResult = charge(mobile,productCode,phoneBillOptional,cookies);
					//失败{"status":false,"msg":"网络访问异常，请稍后再试!"}      充值成功{"status":true} 
					/**
					 * 目前用代码提交，返回的结果是：网络访问异常，请稍后再试!，在页面上提示是余额不足（ 账户余额只有90，提交订单金额是200元 ）
					 */
					System.out.println("用友充值信息为："+chargeResult);
					JSONObject jsonStatus = JSONObject.parseObject(chargeResult);
					if("true".equals(""+jsonStatus.get("status"))){

						map.put("resultCode","充值成功!");
						flag = 3;
					}else {
						map.put("resultCode",jsonStatus.get("status")+" : "+jsonStatus.get("msg"));
						flag = 4;
					}
//					map.put("providerOrderId","");
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		map.put("status",flag);
	}

	
	public static String queryCustInfo(String phone,String cookies){
		HttpClient httpClient = new HttpClient();
		try{
			PostMethod loginMethod = new PostMethod("http://agent.seecom.com.cn/business/recharge/get170MobileFrom");
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
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String charge(String mobile,String productCode,String phoneBillOptional,String cookies){
		HttpClient httpClient = new HttpClient();
		try{
			PostMethod loginMethod = new PostMethod("http://agent.seecom.com.cn/business/recharge/rechargePhoneCost");
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
