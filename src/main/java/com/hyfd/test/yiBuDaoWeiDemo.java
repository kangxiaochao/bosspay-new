package com.hyfd.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.LanMaoSign;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;

public class yiBuDaoWeiDemo {

	public static void main(String[] args) {
		String userName = "sdhb001";
		String password = "123456";

		// 获取tokenURL POST请求 返回token
		String queryTokenUrl = "http://jf.ybdw.com/rest/tokens" + "?username=" + userName + "&password="
				+ password;
		// 销毁tokenURL POST请求 返回token
		String destroyTokenUrl = "http://jf.ybdw.com/rest/tokens/" + userName;
		// 下单地址
		String payUrl = "http://jf.ybdw.com/rest/payRechargeSubmit";
		// 查单地址
		String queryUrl = "http://jf.ybdw.com/rest/selectPayOrderStatus";
		// token
		String X_AUTH_TOKEN = ToolHttp.post(false, queryTokenUrl, null, "application/text");
		String phone = "16725621234";	//手机号
		Integer money = 10;		//充值金额   整数 （元）
		String time = DateTimeUtils.formatDate(new Date(),"yyyyMMddHHmmss"); //时间戳
		String orderNo = userName + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);//商户流水订单号
		String key = "KpvJcv6HYIgUUaNFNjEug4xsbCI1fjwq";
		String sign = md5Encode(""+phone+money+time+orderNo+key);
		System.out.println(X_AUTH_TOKEN);
		System.out.println(sign);
		//拼接下单参数
		payUrl = payUrl+"?phone="+phone+"&money="+money+"&time="+time+"&sign="+sign+"&orderNo="+orderNo;
		System.out.println("充值地址： "+payUrl);
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("X-AUTH-TOKEN",X_AUTH_TOKEN);
//		String result = ToolHttp.post(false,headerMap , payUrl, null, "application/text");
		//充值结果：  {"respCode":"0","ok":true,"data":{"createDate":"2020-11-10 14:33:11","money":10,"phone":"16725621234","rechargeNo":"JF-ORDER-20111014331110482","orderNo":"jf_0012020111014314111341220","status":1},"message":"成功"}
		String orderNo2 = "sdhb0012020112223082815236652";
		//拼接查单接口链接
		queryUrl = queryUrl + "?orderNo=" + orderNo2;
		//查单结果： {"respCode":"0","ok":true,"data":{"createDate":"2020-11-10 14:33:12","money":10.00,"phone":"16725621234","rechargeNo":"JF-ORDER-20111014331110482","orderNo":"jf_0012020111014314111341220","status":4},"message":"成功"}
		String result = ToolHttp.post(false,headerMap , queryUrl, null, "application/text");
		System.out.println(result);
		
		// 拼接销毁toKen
//		httpDelete(destroyTokenUrl, null, "application/text");
		
		
	}

	
	
	
	
	
	
	
	
	// 销毁token
	public static void httpDelete(String url, Map<String, String> headers, String encode) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		// 创建Delete请求
		HttpDelete httpDelete = new HttpDelete(url);
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			// 配置信息
			RequestConfig requestConfig = RequestConfig.custom()
					// 设置连接超时时间(单位毫秒)
					.setConnectTimeout(5000)
					// 设置请求超时时间(单位毫秒)
					.setConnectionRequestTimeout(5000)
					// socket读写超时时间(单位毫秒)
					.setSocketTimeout(5000)
					// 设置是否允许重定向(默认为true)
					.setRedirectsEnabled(true).build();

			// 将上面的配置信息 运用到这个Delete请求里
			httpDelete.setConfig(requestConfig);

			// 由客户端执行(发送)Delete请求
			response = httpClient.execute(httpDelete);

			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，防止相应出现乱码
				System.out.println("响应内容为:" + EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * MD5加密 
	 * @param str
	 * @return 加密后小写
	 */
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
		return buf.toString();
	}

}
