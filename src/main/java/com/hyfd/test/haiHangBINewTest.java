package com.hyfd.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.deal.interfaces.JavaScriptInterface;

public class haiHangBINewTest {
	private static Logger log = Logger.getLogger(haiHangBINewTest.class);

	/**
	 * 登录地址
	 */
	private static String loginUrl = "https://www.10044.cn/gateway/sso/login/sso/v2";

	/**
	 * 获取验证码地址
	 */
	private static String sendCode = "https://www.10044.cn/gateway/sso/sms/sendCode";

	/**
	 * 下单地址
	 */
	private static String agentAirRechargeUrl = "https://www.10044.cn/SaleWeb/sale/agentAirRecharge?method=airRecharge_HNA";
	private static String cookie = "JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2;";

	// 获取验证时携带的固定参数
	private static String bussCode = "SSO_LOG";
	private static String channelCode = "10002";
	private static String extSystem = "102";

	private static String loginNbr = "HNADL2460"; // 账号
	private static String pwd = "Wn123120"; // 密码
	private static String smsCode = "993821"; // 验证码-=---------
	private static String payPwd = "123456"; // 操作密码
	private static String rechargeAmount = "10"; // 充值金额
	private static String phoneNO = "16793636999"; // 充值号码

	public static void main(String[] args) throws Exception {
//		sendCode();
//		login();
//		charge(login());
		charge("JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2;SESSION=M2EzZTkzMjctYWM3NC00NDUzLWJhNTUtNmU2NzBhMWE0ZDJi; Path=/; SameSite=Lax");
		
	}

	// 获取验证码
	public static String sendCode() throws ClientProtocolException, IOException {

		// 获取验证码的参数
		JSONObject codeJson = new JSONObject();
		codeJson.put("bussCode", bussCode);
		codeJson.put("channelCode", channelCode);
		codeJson.put("extSystem", extSystem);
		codeJson.put("staffCode", loginNbr);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		// 第一步：创建HttpClient对象
		httpClient = HttpClients.createDefault();

		// 第二步：创建httpPost对象
		HttpPost httpPost = new HttpPost(sendCode);
		httpPost.setHeader("Cookie", cookie);
		httpPost.setHeader("appId", "2");
		// 第三步：给httpPost设置JSON格式的参数
		StringEntity requestEntity = new StringEntity(codeJson.toJSONString(), "utf-8");
		requestEntity.setContentEncoding("UTF-8");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setEntity(requestEntity);

		// 第四步：发送HttpPost请求，获取返回值
		String response = httpClient.execute(httpPost, responseHandler); // 调接口获取返回值时，必须用此方法
		System.out.print(response);
		return "";
	}

	// 登录
	public static String login() throws ClientProtocolException, IOException {

		// 获取验证码的参数
		// 登录的参数
		JSONObject loginJson = new JSONObject();
		loginJson.put("bussCode", bussCode);
		loginJson.put("channelCode", channelCode);
		loginJson.put("extSystem", extSystem);
		loginJson.put("loginNbr", loginNbr);
		loginJson.put("loginType", "50");
		loginJson.put("pwd", pwd);
		loginJson.put("smsCode", smsCode);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 第一步：创建HttpClient对象
		httpClient = HttpClients.createDefault();

		// 第二步：创建httpPost对象
		HttpPost httpPost = new HttpPost(loginUrl);
		httpPost.setHeader("Cookie", cookie);
		httpPost.setHeader("appId", "2");
		// 第三步：给httpPost设置JSON格式的参数
		StringEntity requestEntity = new StringEntity(loginJson.toJSONString(), "utf-8");
		requestEntity.setContentEncoding("UTF-8");
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setEntity(requestEntity);

		// 第四步：发送HttpPost请求，获取返回值
		CloseableHttpResponse response = httpClient.execute(httpPost); // 调接口获取返回值时，必须用此方法
		String cookies = "";
		Header[] headers = response.getHeaders("Set-Cookie");
		for(Header h : headers) {
			System.out.println(h);
			cookies += h;
		}
		//{"Result":"0","Msg":"未知异常"}
		System.out.println(EntityUtils.toString(response.getEntity()));
		return cookies;
	}

	
	// 下单
		public static String charge(String cookies) throws ClientProtocolException, IOException {
			HttpClient httpClient = new HttpClient();
			String serviceUrl =  "https://www.10044.cn/SaleWeb/www/html/index.html?time="+new Date().getTime();  //请求地址
			JSONObject rechargeJson = new JSONObject();
			rechargeJson.put("payPwd", rsaEncrypt(payPwd));
			rechargeJson.put("phoneNO", rsaEncrypt(phoneNO));
			rechargeJson.put("rechargeAmount", rsaEncrypt(rechargeAmount));
			rechargeJson.put("serviceUrl", serviceUrl);
			PostMethod transferMethod = new PostMethod(agentAirRechargeUrl);
			NameValuePair[] transferParam = {
	                new NameValuePair("serviceUrl", serviceUrl),
	                new NameValuePair("payPwd", rsaEncrypt(payPwd)),
	                new NameValuePair("phoneNO", rsaEncrypt(phoneNO)),
	                new NameValuePair("rechargeAmount", rsaEncrypt(rechargeAmount)),
	        };
			transferMethod.setRequestBody(transferParam);
			transferMethod.setRequestHeader("cookie",cookies);
			httpClient.executeMethod(transferMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(transferMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}  
			String transferResult = stringBuffer.toString();
			System.out.printf(transferResult);
			return "";
		}
	
		
		public static String rsaEncrypt(String value){
			ScriptEngineManager manager = new ScriptEngineManager();
			// 获取一个JavaScript 引擎实例
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			String str="";
			try {
				// 获取文件路径
	            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	            /**
	             * 指向本地的js文件
	             */
	            engine.eval(new FileReader("E:\\eclipse_bospaybill\\bosspaybill\\src\\main\\webapp\\js\\project\\haihangbi\\BigInt.js"));
	            engine.eval(new FileReader("E:\\eclipse_bospaybill\\bosspaybill\\src\\main\\webapp\\js\\project\\haihangbi\\Barrett.js"));
	            engine.eval(new FileReader("E:\\eclipse_bospaybill\\bosspaybill\\src\\main\\webapp\\js\\project\\haihangbi\\RSA.js"));
	            engine.eval(new FileReader("E:\\eclipse_bospaybill\\bosspaybill\\src\\main\\webapp\\js\\project\\haihangbi\\base64.js"));
	            engine.eval(new FileReader("E:\\eclipse_bospaybill\\bosspaybill\\src\\main\\webapp\\js\\project\\haihangbi\\rsaEncrypt.js"));
	            
	            if (engine instanceof Invocable) {
	                Invocable invocable = (Invocable) engine;
	                // 从脚本引擎中获取JavaScriptInterface接口对象（实例）. 该接口方法由具有相匹配名称的脚本函数实现。
	                JavaScriptInterface executeMethod = invocable.getInterface(JavaScriptInterface.class);
	                // 调用这个js接口
	                str = executeMethod.rsaEncrypt(value);
	            }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return str;
		}
		
	
}
