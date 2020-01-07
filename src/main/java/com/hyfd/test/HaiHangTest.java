package com.hyfd.test;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HaiHangTest {
	
	private static Map<String,String> productIdMap = new HashMap<String,String>();

	
	static{
		productIdMap.put("10", "101");
		productIdMap.put("20", "102");
		productIdMap.put("30", "103");
		productIdMap.put("50", "104");
		productIdMap.put("100", "105");
		productIdMap.put("200", "106");
		productIdMap.put("300", "107");
		productIdMap.put("500", "108");
	}
	
	public static void main(String[] args) {
		testRecharge();
//		testQuery();
	}
	
	
	public static void testRecharge(){
		// 自己封装请求海航云接口
		//参考文档地址https://data.hnagroup.com/indexPage/toDocument.do?uuid=42AA5A64B424DAC435A592BFEAB3EF2D
		// 充值平台appSecret 7oeyvzrp8m320g8spaxfmi0jwb5550o6
		// 充值平台票据appToken E04A8DDD8FA092057564F1ED33E39D2D99CCD87A
		
		try {
			
			String appSecret = "y6dcgiww6qc0mzlqy4e2vxchzyv4x4df";
			String appToken = "509665715AB3B380FDD816825FFAB9E777A82535";
			String method = "capability-package_recharge_recharge"; //	capability-package_recharge_recharge
			String timestamp="";
			String format ="json";
			StringBuffer source = new StringBuffer();
					
			String sign = "";
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			timestamp = sdf.format(now);
			
			JSONObject appJson = new JSONObject();
			JSONObject header = new JSONObject();
			JSONObject body = new JSONObject();
			// 封装应用请求头
			header.put("appId", "1");	// 固定1
			header.put("securityKey", "1"); // 固定1
			header.put("version", "1.0");	// 固定1.0
		
			//封装应用请求包体
			body.put("hpno", "200047");	// 平台定义200047
			body.put("pin", "123456");	// 平台定义123456
			body.put("payMone", "5");	// 固定 5
			body.put("channelCode", "11");	// 固定 11
			body.put("requestAmount", "1"); // 固定 1
			body.put("ofrId", productIdMap.get("10"));	// 商品ID,订购商品ID由商务提供
			body.put("rcgMobile", "17000001008");	// 充值号码
			body.put("orderNumber", sdf2.format(now)+"qxqxqxqx");	// 订单号(30位以内，不能重复)
			body.put("destAttr", "2");	// 被充值用户属性，固定填写2
			body.put("operationCode", "0");	// 请求类型(固定0－普通空充)
			
			appJson.put("body", body);
			appJson.put("header", header);
			System.out.println("---------"+appJson.toString());
			//
			source.append(appSecret).append("AccessToken"+appToken).append("Format"+format).append("Method"+method)
					.append("Parameter"+appJson.toString()).append("Timestamp"+timestamp).append(appSecret);
			sign = md5Encode(source.toString()); // MD5加密大写
			
			// 封装http请求
			String urlString = "https://api.hnagroup.com";
			
			String result="";
			HttpPost httpRequst = new HttpPost(urlString);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("Method", method));
			nvps.add(new BasicNameValuePair("AccessToken", appToken));
			nvps.add(new BasicNameValuePair("Sign", sign));
			nvps.add(new BasicNameValuePair("Timestamp", timestamp));
			nvps.add(new BasicNameValuePair("Format", format));
			nvps.add(new BasicNameValuePair("Parameter", appJson.toString()));
			
			httpRequst.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			System.out.println("url="+httpRequst.getURI());	
			 CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse httpResponse = httpclient.execute(httpRequst);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}
			System.out.println("result="+result);	
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	public static void testQuery(){
		// 自己封装请求海航云接口
		//参考文档地址https://data.hnagroup.com/indexPage/toDocument.do?uuid=42AA5A64B424DAC435A592BFEAB3EF2D
		// 充值平台appSecret 7oeyvzrp8m320g8spaxfmi0jwb5550o6
		// 充值平台票据appToken E04A8DDD8FA092057564F1ED33E39D2D99CCD87A
		
		try {
			
			String appSecret = "y6dcgiww6qc0mzlqy4e2vxchzyv4x4df";
			String appToken = "509665715AB3B380FDD816825FFAB9E777A82535";
			String method = "capability-package_recharge_queryOrderInfo"; //	capability-package_recharge_queryOrderInfo
			String timestamp="";
			String format ="json";
			StringBuffer source = new StringBuffer();
			
			String sign = "";
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			timestamp = sdf.format(now);
			
			JSONObject appJson = new JSONObject();
			
			//封装应用请求包体
			appJson.put("hpno", "200047");	// 平台定义200047
			appJson.put("orderNumber", "hh2017091211334722617161553503");	// 订单号(30位以内，不能重复)
			
			System.out.println(appJson.toString());
			//
			source.append(appSecret).append("AccessToken"+appToken).append("Format"+format).append("Method"+method)
			.append("Parameter"+appJson.toString()).append("Timestamp"+timestamp).append(appSecret);
			sign = md5Encode(source.toString()); // MD5加密大写
			
			// 封装http请求
			String urlString = "https://api.hnagroup.com";
			
			String result="";
			HttpPost httpRequst = new HttpPost(urlString);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("Method", method));
			nvps.add(new BasicNameValuePair("AccessToken", appToken));
			nvps.add(new BasicNameValuePair("Sign", sign));
			nvps.add(new BasicNameValuePair("Timestamp", timestamp));
			nvps.add(new BasicNameValuePair("Format", format));
			nvps.add(new BasicNameValuePair("Parameter", appJson.toString()));
			
			httpRequst.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			System.out.println("url="+httpRequst.getURI());	
			CloseableHttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse httpResponse = httpclient.execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}
			System.out.println("result="+result);	
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	public static String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
			String s = Integer.toHexString(bytes[i] & 0xff);
			if(s.length()==1){
			buf.append("0");
			}
			buf.append(s);
		}
		}
		catch(Exception ex){	
		}
		return buf.toString().toUpperCase();//转换成大写字母
	}
}
