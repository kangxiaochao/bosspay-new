package com.hyfd.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;

@Component
public class YongYouCookiesTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	CookiesDao cookiesDao;//登录信息
	
	private static HttpClient httpClient = new HttpClient();
	
	Logger log = Logger.getLogger(getClass());
	
	@Scheduled(fixedDelay = 600000)
	public void getCookies(){
		String id = "2000000047";
		try{
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String username = paramMap.get("username");
			String password = paramMap.get("password");
			String channelName = paramMap.get("channel");
			String loginCookie = login2(login1(login(),username,password),channelName);
			String crmCookie = payQuery();
			Map<String,Object> cookie = new HashMap<String,Object>();
			cookie.put("ids", UUID.randomUUID().toString().replaceAll("-", ""));
			cookie.put("cookies", crmCookie);
			cookie.put("updatetime", System.currentTimeMillis());
			cookie.put("bz", "YY");
			int x = cookiesDao.insertSelective(cookie);
			if(x > 0){
				payLogin(loginCookie);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String login(){
		try{
			GetMethod loginMethod = new GetMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			String result = stringBuffer.toString();
			int x = result.indexOf("name=\"lt\" value=\"");
			String sss = result.substring(x+17, x+76+17);
			return sss;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String login1(String lt,String username,String password){
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			NameValuePair[] transferParam = {
	                new NameValuePair("username", username),
	                new NameValuePair("password",password),
	                new NameValuePair("strong","true"),
	                new NameValuePair("command",""),
	                new NameValuePair("lt",lt),
	                new NameValuePair("_eventId","submit"),
	        };
			loginMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			String result = stringBuffer.toString();
			int x = result.indexOf("<input type=\"hidden\" name=\"lt\" value=\"");
			String sss = result.substring(x+38, x+76+38);
			return sss;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String login2(String lt,String channel){
		StringBuffer tmpcookies = new StringBuffer();
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			NameValuePair[] transferParam = {
					new NameValuePair("operId","828"),
	                new NameValuePair("operCode", channel),
	                new NameValuePair("_eventId_cancel","%E8%BF%94++%E5%9B%9E"),
	                new NameValuePair("lt",lt),
	                new NameValuePair("_eventId","submit")
	        };
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			loginMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies) {
				tmpcookies.append(c.toString() + ";");
			}
			return tmpcookies.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String payQuery(){
		StringBuffer tmpcookies = new StringBuffer();
		try{
			GetMethod pqMethod = new GetMethod("http://54.222.154.14:9200/MVNO-AR/payDispose/payquery");
			pqMethod.setFollowRedirects(false);
			pqMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			httpClient.executeMethod(pqMethod);
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies) {
				tmpcookies.append(c.toString() + ";");
			}
			return tmpcookies.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static void payLogin(String loginCookie){
		try{
			GetMethod pqMethod = new GetMethod("http://54.222.154.14:9100/MVNO-SSF/login?service=http%3A%2F%2F54.222.154.14%3A9200%2FMVNO-AR&menuUrl=%2FpayDispose%2Fpayquery");
			pqMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			pqMethod.setRequestHeader("Cookie",loginCookie);
			httpClient.executeMethod(pqMethod);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
