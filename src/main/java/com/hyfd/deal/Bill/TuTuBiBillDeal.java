package com.hyfd.deal.Bill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class TuTuBiBillDeal implements BaseDeal{

	private static Logger log = Logger.getLogger(TuTuBiBillDeal.class);
	private static HttpClient httpClient = new HttpClient();
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String phone = (String) order.get("phone");//手机号
		String money = order.get("fee")+"";//金额
		String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
		map.put("orderId", orderId);
		Map<String,Object> cookie = (Map<String, Object>) order.get("cookie");
		String cookies = "";
		if(cookie != null){
			log.error("兔兔币获取的Cookie为"+MapUtils.toString(cookie));
			cookies = (String) cookie.get("cookies");
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String transferUrl = paramMap.get("transferUrl");
			String rechargeNum = paramMap.get("rechargeNum");
			int sum = Integer.parseInt(rechargeNum.trim());
			int x = recursionCharge(1,sum,transferUrl,phone,money,cookies);
			if(x == 1){
				flag = 3;
				map.put("resultCode", "充值成功");
			}else if(x == 0){
				flag = 4;
				map.put("resultCode", "充值失败");
			}else{
				flag = -1;
				map.put("resultCode", "兔兔币充值出现异常，网络连接问题，或者兔兔币返回{code:8888,msg:服务器淘气了，我们的工程师正在修理它...}");
			}
		}else{
			flag = 4;
			map.put("resultCode", "cookie未获取到");
		}
		map.put("status", flag);
		return map;
	}
	
	public static int recursionCharge(int num ,int sum,String transferUrl,String phone,String money,String cookie){
		if(num > sum){
			return 0;
		}
		int flag = transferX(transferUrl,phone,money,cookie);
		if(flag == 0){//如果充值失败，递归充值
			num++;
			return recursionCharge(num,sum,transferUrl,phone,money,cookie);
		}
		if(flag == 1){
			return flag;
		}
		if(flag == -1){
			return flag;
		}
		return 0;
	}

	/**
	 * 兔兔币转账充值方法
	 * @author lks 2017年12月27日上午9:50:38
	 * @param transferUrl
	 * @param phone
	 * @param money
	 * @param transferCookies
	 * @return
	 * @throws IOException
	 */
	public static int transferX(String transferUrl,String phone,String money,String transferCookies){
		int flag = 0;
		PostMethod transferMethod = new PostMethod(transferUrl);
		NameValuePair[] transferParam = {
                new NameValuePair("accountDest", phone),
                new NameValuePair("money",money),
        };
		transferMethod.setRequestBody(transferParam);
		transferMethod.setRequestHeader("cookie",transferCookies);
		try {
			httpClient.executeMethod(transferMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(transferMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}  
			String transferResult = stringBuffer.toString();  
			log.debug("兔兔币充值返回-->"+transferResult);
			JSONObject transferJson = JSON.parseObject(transferResult);
			int transferCode = transferJson.getInteger("code");
			String msg = transferJson.getString("msg");
			System.out.println(phone + "|"+transferCode+msg);
			if(transferCode == 0){
				flag = 1;
				log.debug("兔兔币充值成功  ---- "+phone + "|"+transferCode+msg);
			}else if(transferCode == 8888){
				flag = -1;
			}else{
				log.error("兔兔币充值方法   ---- "+phone + "|"+transferCode+msg);
			}
		} catch (HttpException e) {
			flag = -1;
			log.error("兔兔币充值方法报错   --HttpException"+e);
			e.printStackTrace();
		} catch (IOException e) {
			flag = -1;
			log.error("兔兔币充值方法报错   --IOException"+e);
			e.printStackTrace();
		}finally{
			transferMethod.releaseConnection();
		}
		return flag;
	}

	/**
	 * md5加密方法 by lks
	 */
	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
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
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
