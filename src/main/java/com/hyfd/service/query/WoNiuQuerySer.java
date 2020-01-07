package com.hyfd.service.query;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.dao.mp.CookiesDao;

public class WoNiuQuerySer {

	@Autowired
	CookiesDao cookiesDao;//登录信息
	
	HttpClient httpClient = new HttpClient();
	
	public String queryWoNiuPhoneInfo(String phone){
		//http://10040.snail.com/platform/web/agent/order/user/info/17184104094?_eq=1555290097382&_=1555289274757
		//{"code":0,"msg":"OK","value":{"userName":"张**","idCardNo":null,"cardType":"40","cardName":"畅玩12免卡","userState":"正常在用","reCallStatus":null,"tutuMoney":"0.0","province":"浙江","city":"杭州","activityStatus":"0","activityConfig":null,"balance":{"freeBlance":0.0,"freezeFreeBlance":0.0,"amounts":"0","giveAmounts":"0","coupons":"0","callPkgs":"0","freeBlances":"0","freezeFreeBlances":"0"},"voiceRemainSize":"0","flowRemainSize":"0","msgRemainSize":"0"}}
		String result = "";
		try{
			String queryUrl = "http://10040.snail.com/platform/web/agent/order/user/info/"+phone+"?_eq="+System.currentTimeMillis()+"&_="+System.currentTimeMillis();
			Map<String, Object> cookies = cookiesDao.selectFirstCookie();
			String cookie = (String) cookies.get("cookies");
			GetMethod queryMethod = new GetMethod(queryUrl);
			queryMethod.setRequestHeader("cookie",cookie);
			httpClient.executeMethod(queryMethod);
			result = queryMethod.getResponseBodyAsString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
}
