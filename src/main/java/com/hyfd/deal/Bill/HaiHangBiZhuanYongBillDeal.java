package com.hyfd.deal.Bill;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.deal.BaseDeal;
import com.hyfd.deal.interfaces.JavaScriptInterface;


public class HaiHangBiZhuanYongBillDeal implements BaseDeal {
	
	private static Logger log = Logger.getLogger(HaiHangBiZhuanYongBillDeal.class);
	
	private static String timer = "";
	private static String cookie = "";
	private static String ticket = "";

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		String phoneNo = (String) order.get("phone");	// 手机号
		Double fee = (Double) order.get("fee");			//金额，以元为单位
		String spec = fee.intValue() + ""; 				//只取整数部分
		boolean bool = (boolean)order.get("isOk");		//验证该手机号是在可充值号段范围
		Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		// 生成自己的id，供回调时查询数据使用
		String orderId = ToolDateTime.format(new Date(),"yyyyMMddHHmmssSSS") + GenerateData.getStrData(3);
		map.put("orderId", orderId);
		log.info("-=-=-=---:"+order.toString());
		if(!bool) {
			map.put("resultCode","此号段不在充值号段范围内！");
			map.put("status",4);		// 4失败
		}
		if(bool) {			
			try {
				// 海航号段专用通道充值
				String data = recharge(paramMap, phoneNo, spec);
				log.info("号码[" + phoneNo + "]海航号段专用通道[话费充值]请求返回信息[" + data + "]");
				if (!data.equals("")) {
					JSONObject jsonObj = JSONObject.parseObject(data);
					String Result = jsonObj.getString("Result");
					String Msg = jsonObj.getString("Msg");
					
					String nowTime = DateUtils.getNowTime();
					map.put("resultCode", Result + ":" + Msg);
					if (Result.equals("0")) {
						flag = 3;
						map.put("resultCode", Result + ":充值成功");
						timer = nowTime;
						log.debug("海航号段专用通道[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+spec+"]元");
					} else if (Result.equals("100")) {
						flag = 4;
						timer = "";
						log.debug("海航号段专用通道[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["	+ spec + "]元," + Result + "[" + Msg + "]");
					} else {
						timer = nowTime;
						log.debug("海航号段专用通道[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["	+ spec + "]元," + Result + "[" + Msg + "]");
						flag = 4;
					}
				} else{
					log.debug("海航号段专用通道[话费充值]请求:充值请求发生异常,请人工核实处理!手机号["+phoneNo+"],充值金额["+spec+"]元");
					flag = -1;
				}
			} catch (Exception e) {
				flag = -1;
				if (map.get("resultCode") == null) {
					map.put("resultCode", "充值请求发生异常,请人工核实处理");
				}
				log.error("号码[" + phoneNo + "]海航号段专用通道[话费充值]发生异常:" + e.getMessage() + ","+ MapUtils.toString(order));
			}
			map.put("status", flag);
		}
		return map;
	}
	
	/**
	 * <h5>功能:海航币充值</h5>
	 * 
	 * @author zhangpj	@date 2018年9月13日
	 * @param paramMap
	 * @param phoneNo
	 * @param spec
	 * @return 
	 */
	private String recharge(Map<String, String> paramMap, String phoneNo, String spec){
		boolean loginFlag = false;
		String resultStr = "";
		String verifyCodeUrl = paramMap.get("verifyCodeUrl"); // 获取cookies的url
		// 1.验证当前时间是否已经超过指定时间间隔
		boolean timeFlag = validateTimer(1800);
		if (timeFlag) {
			log.info("海航号段专用通道话费充值开始获取cookies！");
			// 2.获取cookies
			int code = verifyCode(verifyCodeUrl);
			if (code == 200) {
				log.info("海航号段专用通道话费充值获取cookies成功"+cookie);
				String loginUrl = paramMap.get("loginUrl"); // 用户登录url
				String empeeCode = paramMap.get("empeeCode"); // 登录账号
				String password = paramMap.get("password"); // 登录密码
				String appId = paramMap.get("appId"); // appid
				String type = paramMap.get("type"); // 类型
				// 3.登录系统,获取ticket
				ticket = login(loginUrl, empeeCode, password, appId, type);
				if (!"".equals(ticket)) {
					String qryStaffOrgUrl = paramMap.get("qryStaffOrgUrl"); // 用户登录url
					// 4.获取登录用户资料信息
					boolean qryStaffOrgFlag = qryStaffOrg(qryStaffOrgUrl);
					if (qryStaffOrgFlag) {
						loginFlag = true;
					}
				}
			}
		}else{
			loginFlag = true;
		}
		if (loginFlag) {
			String agentAirRechargeUrl = paramMap.get("agentAirRechargeUrl"); // 充值url
			String method = paramMap.get("method"); // 充值方法名
			String payPwd = paramMap.get("payPwd"); // 充值密码
			// 5. 海航号段专用通道余额充值
			resultStr = agentAirRecharge(agentAirRechargeUrl, method, payPwd, phoneNo, spec);
		}
		
		return resultStr;
	}
	
	/**
	 * <h5>功能:1.获取cookies</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param verifyCodeUrl 获取验证码地址
	 */
	private int verifyCode(String verifyCodeUrl){
//		String url = "http://agent.10044.cn:8100/SaleWeb/images/VerifyCode.do?type=1";
		List<String> cookies = new ArrayList<String>();
		int code = 0;
		
		OkHttpClient okHttpClient = new OkHttpClient();
		
		// 1.构建一个Request构建请求参数,如url,请求方式(默认get),请求参数,header等
		Request request = new Request.Builder().url(verifyCodeUrl).build();
		
		// 2.生成一个具体请求实例
		Call call = okHttpClient.newCall(request);
		try {
			// 3.Response：结果响应
			// call.execute()非异步方式,会阻塞线程,等待返回结果;call.enqueue(Callback),异步方式.
		    Response response = call.execute();

	        Headers headers = response.headers();
	        cookies = headers.values("Set-Cookie");
//	        for (int i = 0; i < cookies.size(); i++) {
//				System.out.println("cookies"+(i+1)+":"+cookies.get(i));
//			}
	        cookie = cookies.toString();
	        // 保存cookie
	        cookie = cookie.substring(1,cookie.toString().length() -1);
	        // 保存cookie生成时间
	        timer = DateUtils.getNowTime();
	        log.info("海航号段专用通道获取到cookie["+cookie+"]");
	        code = response.code();
		} catch (IOException e) {
			log.error("海航号段专用通道发起verifyCode请求发生异常["+e.getMessage()+"]");
		}
		return code;
	}
	
	/**
	 * <h5>功能:2.登录系统,获取ticket</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param empeeCode 登录账号
	 * @param password 登录密码
	 * @param appId
	 * @param type 类型
	 * @return 
	 */
	private String login(String loginUrl, String empeeCode, String password, String appId, String type){
//		String url = "http://agent.10044.cn:8100/Auth/login.do";
		String ticket = "";
		log.info("海航号段专用通道话费充值开始登陆账号");
		OkHttpClient okHttpClient = new OkHttpClient();
		
		// 1.构建表单数据
		FormBody.Builder params=new FormBody.Builder();
		params.add("empeeCode", Base64Utils.encodeToString(empeeCode.getBytes()));
		params.add("password", rsaEncrypt(password));
		params.add("verifyCode", rsaEncrypt(GenerateData.getStrData(4)));
		params.add("appId", appId);
		params.add("type", type);
//		params.add("serviceUrl", serviceUrl); // 可暂时不用
		log.info("海航号段专用通道充值登陆加密参数"+rsaEncrypt(password)+"---"+rsaEncrypt(GenerateData.getStrData(4)));
		// 2.构建数据请求主体.默认请求方式为application/x-www-form-urlencoded
		RequestBody requestBody = params.build();
		
		// 3.构建一个Request构建请求参数,如url,请求方式(默认get),请求参数,header等
		Builder builder = new Request.Builder().url(loginUrl).post(requestBody);
		builder.addHeader("Cookie", cookie);
		Request request = builder.build();
		
		// 4.生成一个具体请求实例call.
		// call.execute()非异步方式,会阻塞线程,等待返回结果;call.enqueue(Callback),异步方式.
		Call call = okHttpClient.newCall(request);
		try {
			// 5.Response：结果响应
			Response response = call.execute();
			
			// 6.1获取返回信息,获取返回字符串(只能使用一种)
		    String reultContent=response.body().string();
		    log.info("海航号段专用通道登录请求返回数据["+reultContent+"]");
		    ticket = getTicket(reultContent);
		} catch (IOException e) {
			log.error("海航号段专用通道登录请求发生异常["+e.getMessage()+"]");
		}
		return ticket;
	}
	
	/**
	 * <h5>功能:获取登录用户资料信息</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param qryStaffOrgUrl
	 */
	public boolean qryStaffOrg(String qryStaffOrgUrl){
		boolean flag = false;
		log.info("海航号段专用通道话费充值获取登陆用户资料！");
		String url = "http://agent.10044.cn:8100/SaleWeb/sale/qryStaffOrg.do";
		OkHttpClient okHttpClient = new OkHttpClient();
		
		// 2.构建表单数据
		FormBody.Builder params=new FormBody.Builder();
		params.add("randomNum", "917");
		params.add("ticket", ticket);
//		params.add("serviceUrl", "http://agent.10044.cn:8100/SaleWeb/www/html/index.html?ticket="+ticket); // 可暂时不用
	
		// 3.构建数据请求主体.默认请求方式为application/x-www-form-urlencoded
		RequestBody requestBody = params.build();
		
		// 4.构建一个Request构建请求参数,如url,请求方式(默认get),请求参数,header等
		Builder builder = new Request.Builder().url(url).post(requestBody);
		builder.addHeader("Cookie", cookie);
		
		Request request = builder.build();
		
		// 5.生成一个具体请求实例call.
		// call.execute()非异步方式,会阻塞线程,等待返回结果;call.enqueue(Callback),异步方式.
		Call call = okHttpClient.newCall(request);
		try {
			// 6.Response：结果响应
			Response response = call.execute();
			
			// 7.1获取返回信息,获取返回字符串(只能使用一种)
		    String reultContent=response.body().string();
		    log.info("海航号段专用通道发起qryStaffOrg请求返回数据["+reultContent+"]");
		    flag = true;
		} catch (IOException e) {
			log.error("海航号段专用通道发起qryStaffOrg请求发生异常["+e.getMessage()+"]");
		}
		return flag;
	}
	
	/**
	 * <h5>功能:海航币余额充值</h5>
	 * 
	 * @author zhangpj	@date 2018年9月13日
	 * @param agentAirRechargeUrl 请求地址
	 * @param method 充值类型
	 * @param payPwd 充值密码
	 * @param phoneNO 充值号码
	 * @param rechargeAmount 充值金额(元)
	 * @return 
	 */
	public String agentAirRecharge(String agentAirRechargeUrl, String method, String payPwd, String phoneNO, String rechargeAmount){
//		String url = "http://agent.10044.cn:8100/SaleWeb/sale/agentAirRecharge";
		OkHttpClient okHttpClient = new OkHttpClient();
		log.info("海航号段专用通道话费充值开始执行余额充值！！"+agentAirRechargeUrl+"-"+method+"-"+payPwd+"-"+phoneNO+"-"+"rechargeAmount");
		// 2.构建表单数据
		FormBody.Builder params=new FormBody.Builder();
		params.add("method", method);
		params.add("payPwd", rsaEncrypt(payPwd));
		params.add("phoneNO", rsaEncrypt(phoneNO));
		params.add("rechargeAmount", rsaEncrypt(rechargeAmount));
		params.add("ticket", ticket);
//		params.add("serviceUrl", "http://agent.10044.cn:8100/SaleWeb/www/html/index.html?ticket="+ticket); // 可暂时不用
		
		// 3.构建数据请求主体.默认请求方式为application/x-www-form-urlencoded
		RequestBody requestBody = params.build();
		
		// 4.构建一个Request构建请求参数,如url,请求方式(默认get),请求参数,header等
		Builder builder = new Request.Builder().url(agentAirRechargeUrl).post(requestBody);
		builder.addHeader("Cookie", cookie);
		
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
	
	// ============== private method =================
	/**
	 * <h5>功能:验证当前时间是否已经超过指定时间间隔</h5>
	 * 
	 * @author zhangpj	@date 2018年9月13日
	 * @param intervalTime 秒
	 * @return 
	 */
	private boolean validateTimer(int intervalTime){
		boolean flag = false;
		// 当前时间
		String nowTime = DateUtils.getNowTime();
		if (timer.equals("")) {
			flag = true;
		} else {
			// 同步清理,方式多线程操作时发生异常
			synchronized (timer) {
				int second = DateUtils.getDistanceSecond(nowTime, timer);
				// 实际间隔时间大于最小间隔时间,则清理
				if (second > intervalTime) {	
					flag = true;
				}
			}
		}
		return flag;
	}
	
	/**
	 * <h5>功能:获取ticket</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param str
	 * @return 
	 */
	private String getTicket(String str){
		str = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
		JSONObject jsonObj = JSONObject.parseObject(str);
		String ticket = jsonObj.getString("ticket");
		System.out.println(ticket);
		return ticket;
	}
	
	/**
	 * <h5>功能:javaScript的RSA加密</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param value
	 * @return 
	 */
	private String rsaEncrypt(String value){
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取一个JavaScript 引擎实例
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		String str="";
		try {
			// 获取文件路径
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//            System.out.println(path);
            // FileReader的参数为所要执行的js文件的路径
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/BigInt.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/Barrett.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/RSA.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/base64.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/rsaEncrypt.js"));
            
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                // 从脚本引擎中获取JavaScriptInterface接口对象（实例）. 该接口方法由具有相匹配名称的脚本函数实现。
                JavaScriptInterface executeMethod = invocable.getInterface(JavaScriptInterface.class);
                // 调用这个js接口
                str = executeMethod.rsaEncrypt(value);
//                System.out.println(str);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("海航号段专用通道加密参数："+str);
		return str;
	}
	
	public String test() {
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("verifyCodeUrl", "http://agent.10044.cn:8100/SaleWeb/images/VerifyCode.do?type=1");
		paramMap.put("loginUrl", "http://agent.10044.cn:8100/Auth/login.do");
		paramMap.put("qryStaffOrgUrl", "http://agent.10044.cn:8100/SaleWeb/sale/qryStaffOrg.do");
		paramMap.put("agentAirRechargeUrl", "http://agent.10044.cn:8100/SaleWeb/sale/agentAirRecharge");
		paramMap.put("empeeCode", "SU02283");
		paramMap.put("password", "HYFd10044");
		paramMap.put("appId", "708");
		paramMap.put("type", "query");
		paramMap.put("method", "airRecharge_HNA");
		paramMap.put("payPwd", "123Qwe");
		
		String phoneNo = "13280009366", spec="10";
		
		HaiHangBiZhuanYongBillDeal hhb = new HaiHangBiZhuanYongBillDeal();
		return hhb.recharge(paramMap, phoneNo, spec);
		
	}

	/**
	 * <h5>功能:初始化海航币登录时长(设置为未登录状态)</h5>
	 * 
	 * @author zhangpj	@date 2019年1月15日 
	 */
	public static void initTimer() {
		HaiHangBiZhuanYongBillDeal.timer = "";
	}
}
