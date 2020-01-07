package com.hyfd.deal.Bill;

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

public class YongYouBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			Map<String,Object> cookie = (Map<String, Object>) order.get("cookie");
			if(cookie != null){
				String cookies = (String) cookie.get("cookies");
				String phone = (String) order.get("phone");//手机号
				String money = order.get("fee")+"";//金额
				String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
				map.put("orderId", orderId);
				String oid = System.currentTimeMillis()+"";
				String phoneInfo = queryCustInfo(phone,oid,cookies);
				log.info("用友查询号码信息为："+phoneInfo);
				if(!"".equals(phoneInfo)){
					JSONObject infoJson = JSONObject.parseObject(phoneInfo);
					JSONObject dataJson = infoJson.getJSONObject("RES_DATA");
					String custId = dataJson.getString("custId");
					String acctId = dataJson.getString("acctId");
					String chargeResult = charge(acctId,custId,money,cookies);
					log.info("用友充值信息为："+chargeResult);
					if(!"".equals(chargeResult)){
						JSONObject chargeJson = JSONObject.parseObject(chargeResult);
						JSONObject cDataJson = chargeJson.getJSONObject("RES_DATA");
						String RES_RESULT = chargeJson.getString("RES_RESULT");
						String RES_MSG = chargeJson.getString("RES_MSG");
						map.put("resultCode", RES_RESULT+":"+RES_MSG);
						if(RES_RESULT.equals("SUCCESS")){
							flag = 3;
						}else{
							flag = 4;
						}
						map.put("providerOrderId", cDataJson.getString("applyId"));
					}
				}
			}else{
				flag = 4;
				map.put("resultCode", "cookie未获取到");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		map.put("status", flag);
		return map;
	}

	
	public static String queryCustInfo(String phone,String oid ,String cookies){
		HttpClient httpClient = new HttpClient();
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/custComprehensive/queryCustInfo");
			NameValuePair[] transferParam = {
	                new NameValuePair("zjType", "num"),
	                new NameValuePair("zjNum",phone),
	                new NameValuePair("zjPwd",""),
	                new NameValuePair("busiOperCode",""),
	                new NameValuePair("myOID",oid),
	                new NameValuePair("pay_HideCustName","no"),
	                new NameValuePair("newAcctId","-1")
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
	
	public static String charge(String acctId,String custId,String fee,String cookies){
		HttpClient httpClient = new HttpClient();
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/pay/submit");
			NameValuePair[] transferParam = {
	                new NameValuePair("acct_type_value","1"),
	                new NameValuePair("acctID",acctId),
	                new NameValuePair("collect_fee",fee),
	                new NameValuePair("custId",custId),
	                new NameValuePair("has_query","1"),
	                new NameValuePair("old_pay_fee",""),
	                new NameValuePair("payAmount",fee),
	                new NameValuePair("payStyle","1"),
	                new NameValuePair("should_fee_old","0.00"),
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
	
}
