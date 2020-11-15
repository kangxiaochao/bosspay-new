package com.hyfd.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.deal.interfaces.JavaScriptInterface;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request.Builder;

public class haiHangBINewTest {
	private static Logger log = Logger.getLogger(haiHangBINewTest.class);
	private static HttpClient httpClient = new HttpClient();
	
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
	
	
	/**
	 * 每次请求cookie中都包含这一段
	 */
	private static String cookie = "JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2;";

	/**
	 * 之前手动复制的cookie做下单测试用的
	 * 	  第一个JSESSIONID的值有两种格式
	 */
	//JSESSIONID="sso:sessions:F09A38DC1DC93B4C547F218639CD98DC"; JSESSIONID=91D13B6300A5ED2CD7449ADCC0147D74-n1.jvm2; SESSION=ODA2ZDQ5OTUtYzQzZS00N2QxLTljODctODMwNDQ0MWNjNzU2
	//JSESSIONID="sso:sessions:F09A38DC1DC93B4C547F218639CD98DC"; JSESSIONID=91D13B6300A5ED2CD7449ADCC0147D74-n1.jvm2; SESSION=ODA2ZDQ5OTUtYzQzZS00N2QxLTljODctODMwNDQ0MWNjNzU2
	//JSESSIONID="sso:sessions:B10667302D90EDBF10299039D54EBB04"; JSESSIONID=91D13B6300A5ED2CD7449ADCC0147D74-n1.jvm2; SESSION=ODA2ZDQ5OTUtYzQzZS00N2QxLTljODctODMwNDQ0MWNjNzU2

	//JSESSIONID=939BD2C3B0BBB551621E587BB357DFA6; JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2; SESSION=ZTcwZDkyZTUtNjhkMC00MGM3LTkxZTctOTRmNzg1OTU2NTUy
	//JSESSIONID=939BD2C3B0BBB551621E587BB357DFA6; JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2; SESSION=ZTcwZDkyZTUtNjhkMC00MGM3LTkxZTctOTRmNzg1OTU2NTUy
	//JSESSIONID=06151B4203421B7567177DD231CEFB58; JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2; SESSION=MmM2MTk0NDMtYmNjZS00MGY5LTljYzctNTg1MmJkMjk1MzJk
	//JSESSIONID=A312DB6891317387B064B2C03B0C2E94; JSESSIONID=5E25A61D3CBD200C6AF33852E0CDD940-n1.jvm2; SESSION=MmM2MTk0NDMtYmNjZS00MGY5LTljYzctNTg1MmJkMjk1MzJk
	
	
	public static void main(String[] args) {
		//获取验证时携带的固定参数
		String bussCode =  "SSO_LOG"; 
		String channelCode =  "10002"; 
		String extSystem =  "102"; 
		
		String loginNbr =  "HNADL2460"; 	//账号
		String pwd =  "Wn123120"; 	 		//密码
		String smsCode =  "265036";  		//验证码-=---------
		String payPwd =  "123456";  		//操作密码
		String rechargeAmount =  "10";  	//充值金额
		String phoneNO =  "17705305254";  	//充值号码
		
		String loginType =  "50";           //登录时的固定参数
		//下单时formdata中包含的参数
		String serviceUrl =  "https://www.10044.cn/SaleWeb/www/html/index.html?time="+new Date().getTime();  //请求地址
		//获取验证码的参数
		JSONObject codeJson = new JSONObject();
		codeJson.put("bussCode",bussCode);
		codeJson.put("channelCode",channelCode);
		codeJson.put("extSystem",extSystem);
		codeJson.put("staffCode",loginNbr);
		//登录的参数
		JSONObject loginJson = new JSONObject();
		loginJson.put("bussCode",bussCode);
		loginJson.put("channelCode",channelCode);
		loginJson.put("extSystem",extSystem);
		loginJson.put("loginNbr",loginNbr);
		loginJson.put("loginType",loginType);
		loginJson.put("pwd",pwd);
		loginJson.put("smsCode",smsCode);
		/**
		 * 下单参数
         * 下单时 手机号、操作密码、充值金额会进行加密
         */
		JSONObject rechargeJson = new JSONObject();
		rechargeJson.put("payPwd",rsaEncrypt(payPwd));
		rechargeJson.put("phoneNO",rsaEncrypt(phoneNO));
		rechargeJson.put("rechargeAmount",rsaEncrypt(rechargeAmount));
		rechargeJson.put("serviceUrl",serviceUrl);
		
		//获取验证码
		System.out.println(getCode(codeJson.toString()));
		//登录
		System.out.println(login(loginJson.toString()));
//		{"Result":"0","Msg":"æœªçŸ¥å¼‚å¸¸"}  充值成功
		//下单
		System.out.println(agentAirRecharge(agentAirRechargeUrl,serviceUrl,payPwd,phoneNO,rechargeAmount));
	}
	
	/**
	 * 登录
	 */
	public static String  login(String payload) {
		log.info("海航币充值开始登录--------------------------------------------");
		StringBuffer jsonString;
		try {
		    URL url = new URL(loginUrl);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("appId", "2");
		    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		    connection.setRequestProperty("Host", "www.10044.cn");
		    connection.setRequestProperty("Origin", "https://www.10044.cn");
		    connection.setRequestProperty("Referer", "https://www.10044.cn/ssoportal/index.html");
		    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");
		    connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		    writer.write(payload);
		    writer.close();
		    //获取header中包含的参数
		    Map<String, List<String>> map = connection.getHeaderFields();
		    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                System.out.println("Key : " + entry.getKey() + 
                        " ,Value : " + entry.getValue());
            }
		    /**
		     * 输入正确的验证码登录后在header中会包含Set-Cookie，通过截取可以获取出session
		     */
		    if(map.get("Set-Cookie") != null && !"".equals(map.get("Set-Cookie"))) {
		    	String cookies = map.get("Set-Cookie").toString();
		    	//截取需要的内容
		    	//[SESSION=NzMyYjQ3MDQtZTljMi00MWViLTkwZDQtMTY4ZjUyYzc0MmZj; Path=/; SameSite=Lax]
		    	cookie = cookie+ " "+ cookies.substring(cookies.indexOf("[")+1, cookies.indexOf(";")+1);
		    	//截取后的 SESSION=NzMyYjQ3MDQtZTljMi00MWViLTkwZDQtMTY4ZjUyYzc0MmZj;
		    }
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    jsonString = new StringBuffer();
		    String line;
		    while ((line = br.readLine()) != null) {
		        jsonString.append(line);
		    }
		    br.close();
		    connection.disconnect();
		    log.info("海航币用户登录信息：" + jsonString.toString());
		} catch (Exception e) {
		    throw new RuntimeException(e.getMessage());
		}
		return jsonString.toString();
	}
	
	/**
	 * 获取验证码
	 */
	public static String getCode(String payload) {
		log.info("海航币充值开始获取验证码--------------------------------------------");
		StringBuffer jsonString;
		try {
		    URL url = new URL(sendCode);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setDoInput(true);
		    connection.setDoOutput(true);
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("appId", "2");
		    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		    connection.setRequestProperty("Host", "www.10044.cn");
		    connection.setRequestProperty("Origin", "https://www.10044.cn");
		    connection.setRequestProperty("Referer", "https://www.10044.cn/ssoportal/index.html");
		    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36");
		    connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		    writer.write(payload);
		    writer.close();
		    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    jsonString = new StringBuffer();
		    String line;
		    while ((line = br.readLine()) != null) {
		        jsonString.append(line);
		    }
		    br.close();
		    connection.disconnect();
		    log.info("海航币获取验证码结果：" + jsonString.toString());
		} catch (Exception e) {
		    throw new RuntimeException(e.getMessage());
		}
		return jsonString.toString();
	}

	/**
	 * <h5>功能:javaScript的RSA加密</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param value
	 * @return 
	 */
	public static String rsaEncrypt(String value){
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取一个JavaScript 引擎实例
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		String str="";
		try {
			// 获取文件路径
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            // FileReader的参数为所要执行的js文件的路径
//            engine.eval(new FileReader(path+ "../../js/project/haihangbi/BigInt.js"));
//            engine.eval(new FileReader(path+ "../../js/project/haihangbi/Barrett.js"));
//            engine.eval(new FileReader(path+ "../../js/project/haihangbi/RSA.js"));
//            engine.eval(new FileReader(path+ "../../js/project/haihangbi/base64.js"));
//            engine.eval(new FileReader(path+ "../../js/project/haihangbi/rsaEncrypt.js"));
            /**
             * 指向本地的js文件
             */
            engine.eval(new FileReader("E:/eclipse_bospaybill/bosspaybill/src/main/webapp/js/project/haihangbi/BigInt.js"));
            engine.eval(new FileReader("E:/eclipse_bospaybill/bosspaybill/src/main/webapp/js/project/haihangbi/Barrett.js"));
            engine.eval(new FileReader("E:/eclipse_bospaybill/bosspaybill/src/main/webapp/js/project/haihangbi/RSA.js"));
            engine.eval(new FileReader("E:/eclipse_bospaybill/bosspaybill/src/main/webapp/js/project/haihangbi/base64.js"));
            engine.eval(new FileReader("E:/eclipse_bospaybill/bosspaybill/src/main/webapp/js/project/haihangbi/rsaEncrypt.js"));
            
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
	
	
	/**
	 * 海航币余额充值
	 * @param agentAirRechargeUrl 请求地址
	 * @param serviceUrl 固定参数   一个网址最后包含当前时间的时间戳（毫秒）
	 * @param payPwd 充值密码
	 * @param phoneNO 充值号码
	 * @param rechargeAmount 充值金额(元)
	 * @return 
	 */
	public static String agentAirRecharge(String agentAirRechargeUrl,String serviceUrl, String payPwd, String phoneNO, String rechargeAmount){
		OkHttpClient okHttpClient = new OkHttpClient();
		// 2.构建表单数据
		FormBody.Builder params=new FormBody.Builder();
		params.add("serviceUrl", serviceUrl);
		params.add("payPwd", rsaEncrypt(payPwd));
		params.add("phoneNO", rsaEncrypt(phoneNO));
		params.add("rechargeAmount", rsaEncrypt(rechargeAmount));
		// 3.构建数据请求主体.默认请求方式为application/x-www-form-urlencoded
		RequestBody requestBody = params.build();
		// 4.构建一个Request构建请求参数,如url,请求方式(默认get),请求参数,header等
		Builder builder = new Request.Builder().url(agentAirRechargeUrl).post(requestBody);
		builder.addHeader("Cookie", "");
		Request request = builder.build();
		// 5.生成一个具体请求实例call.
		// call.execute()非异步方式,会阻塞线程,等待返回结果;call.enqueue(Callback),异步方式.
		Call call = okHttpClient.newCall(request);
		String reultContent = "";
		try {
			// 6.Response：结果响应
			Response response = call.execute();
			// 7.1获取返回信息,获取返回字符串(只能使用一种)
			reultContent=response.body().string();
			log.info("手机号[" + phoneNO + "],充值金额[" + rechargeAmount + "]元,海航币发起充值请求,返回数据["+reultContent+"]");
		} catch (IOException e) {
			log.error("手机号[" + phoneNO + "],充值金额[" + rechargeAmount + "]元,海航币发起充值请求,发生异常["+e.getMessage()+"]");
		}
		return reultContent;
	}
	
}
