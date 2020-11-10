package com.hyfd.deal.Bill;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class heMaBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(heMaBillDeal.class);

	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO 自动生成的方法存根
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = order.get("phone")+"";											//手机号码
			String fee = order.get("fee")+"";												//充值金额
			Integer money = new Double(fee).intValue();		
			//金额取整
			//获取通道参数
			Map<String, Object> channel = (Map<String, Object>)order.get("channel");		//获取通道参数
//	        String linkUrl = (String)channel.get("link_url");								//充值地址
	        String defaultParameter = (String)channel.get("default_parameter");				//默认参数
	        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
	        String username = paramMap.get("username");										//账号
	        String password = paramMap.get("password");										//密码
	        String key = paramMap.get("key");												//key : 秘钥
	        //查询token url
	        String queryTokenUrl = paramMap.get("queryTokenUrl") + "?username=" + username + "&password=" + password;
	        //销毁token url
	        String destroyTokenUrl = paramMap.get("destroyTokenUrl") + username;
	        //下单地址
	        String payUrl = paramMap.get("payUrl");
	        String time = DateTimeUtils.formatDate(new Date(),"yyyyMMddHHmmss"); 			//时间戳
	        //商户流水订单号
			String orderNo = username + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);
			map.put("orderId",orderNo);
			//获取token
			String X_AUTH_TOKEN = ToolHttp.post(false, queryTokenUrl, null, "application/text");
			//获取加密后的秘钥
			String sign = md5Encode(""+phone+money+time+orderNo+key);
			//拼接下单接口
			payUrl = payUrl+"?phone="+phone+"&money="+money+"&time="+time+"&sign="+sign+"&orderNo="+orderNo;
			log.error("河马[话费充值]请求[" + payUrl + "]");
			//header中存放token
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put("X-AUTH-TOKEN",X_AUTH_TOKEN);
			String result = ToolHttp.post(false,headerMap , payUrl, null, "application/text");
			if(result == null || "".equals(result)) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "河马[话费充值],号码[" + phone + "],金额[" + fee + "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			}else {
				log.error("河马[话费充值]请求结果： " + result);
				JSONObject response = JSONObject.parseObject(result);
				String  resultCode = response.get("respCode")+" : "+response.get("message");
				if("true".equals(response.get("ok")+"")) {
					// 状态（"待充值_1","充值成功_2","充值失败_3"）
					JSONObject data = response.getJSONObject("data");
					if("3".equals(data.get("status")+"")) {
						map.put("resultCode", resultCode);
						flag = 0; // 提交失败
					}else {
						log.error("河马[话费充值]提交成功 ： " + data.toString());
						map.put("resultCode", resultCode); // 执行结果说明
						map.put("providerOrderId", data.getString("rechargeNo")); // 上家订单号（非必须）
						flag = 1; // 提交成功
					}
				}
			}
			//销毁token
			httpDelete(destroyTokenUrl, null, "application/text");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("河马[话费充值]出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status",flag);	
		return map;
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
