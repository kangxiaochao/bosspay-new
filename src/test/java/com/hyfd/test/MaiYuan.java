package com.hyfd.test;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.ToolHttp;

public class MaiYuan {
	
	public static void main(String[] args) {
		chargeTest();		// 充值
//		getPackageTest();	// 话费包查询
//		getBalanceTest();	// 账户余额查询
		queryReportTest();	// 充值订单查询
	}
	
	/**
	 * @功能描述：	充值(正式环境,没有测试环境)
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static void chargeTest(){
		String url = "http://47.97.10.2:8080/telapi.aspx";
		String action = "charge";
		String v = "H1.1";
		String outTradeNo = GenerateData.getCharAndNumr(32);
		String account = "haobai";
		String mobile = "132802009366";
		String myPackage = "100";
		String apiKey = "bbfe4b84f1574a02b23737212aea72f1";
		
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		map.put("mobile", mobile);
		map.put("package", myPackage);
		
		// 计算签名
		String sign = creatSign(map,apiKey);
		System.out.println(sign);
		map.put("action", action);
		map.put("v", v);
		map.put("outTradeNo", outTradeNo);
		map.put("sign", sign);
		
		// 生成请求参数
		url += creatParam(map, apiKey);
		System.out.println(url);
		String resultStr = ToolHttp.get(false, url);
		System.out.println(resultStr);
	}
	
	/**
	 * @功能描述：	获取话费包定义
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static void getPackageTest(){
		String url = "http://47.97.10.2:8080/telapi.aspx";
		String action = "getPackage";
		String v = "H1.1";
		String account = "haobai";
		String type = "0";
		String apiKey = "bbfe4b84f1574a02b23737212aea72f1";
		
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		map.put("type", type);
		
		// 计算签名
		String sign = creatSign(map,apiKey);
		System.out.println(sign);
		try {
			map.put("action", action);
			map.put("v", v);
			map.put("sign", sign);
			
			// 生成请求参数
			url += creatParam(map, apiKey);
			System.out.println(url);
			String resultStr = ToolHttp.get(false, url);
			System.out.println(resultStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @功能描述：	账户余额查询
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static void getBalanceTest(){
		String url = "http://47.97.10.2:8080/telapi.aspx";
		String action = "getBalance";
		String v = "H1.1";
		String account = "haobai";
		String apiKey = "bbfe4b84f1574a02b23737212aea72f1";
		
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		
		String sign = creatSign(map,apiKey);
		System.out.println(sign);
		try {
			map.put("action", action);
			map.put("v", v);
			map.put("sign", sign);
			
			// 生成请求参数
			url += creatParam(map, apiKey);
			System.out.println(url);
			String resultStr = ToolHttp.get(false, url);
			System.out.println(resultStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @功能描述：	查询订单
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static void queryReportTest(){
		String url = "http://47.97.10.2:8080/telapi.aspx";
		String action = "queryReport";
		String v = "H1.1";
		String account = "haobai";
		String taskID = "";
		String outTradeNo = "1111";
		String sendTime = DateUtils.getNowTime("yyyy-MM-dd");
		String apiKey = "bbfe4b84f1574a02b23737212aea72f1";
		
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		
		String sign = creatSign(map,apiKey);
		System.out.println(sign);
		try {
			map.put("action", action);
			map.put("v", v);
			map.put("taskID",taskID);
			map.put("outTradeNo",outTradeNo);
			map.put("sendTime",sendTime);
			map.put("sign", sign);
			
			// 生成请求参数
			url += creatParam(map, apiKey);
			System.out.println(url);
			String resultStr = ToolHttp.get(false, url);
			System.out.println(resultStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @功能描述：	计算签名
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static String creatSign(Map<String, String> map,String apiKey){
		// 拼接请求参数字符串
        StringBuilder paramUrl = new StringBuilder();
        for (String key : map.keySet()) {
            paramUrl.append(key).append("=").append(map.get(key)).append("&");
        }
        paramUrl.append("key=" + apiKey);
		System.out.println(paramUrl.toString());
		String sign = com.hyfd.common.utils.MD5.ToMD5(paramUrl.toString());
		return sign;
	}
	
	/**
	 * @功能描述：	生成请求参数
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static String creatParam(Map<String, String> map, String apiKey){
		// 拼接请求参数字符串
		StringBuilder paramUrl = new StringBuilder("?");
		for (String key : map.keySet()) {
			paramUrl.append(key).append("=").append(map.get(key)).append("&");
		}
		paramUrl.append("key=" + apiKey);
		
		return paramUrl.toString();
	}
}
