package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

//弃用
public class HaiHangBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	private static Map<String,String> productIdMap = new HashMap<String,String>();
	static{
		productIdMap.put("1", "111");
		productIdMap.put("2", "112");
		productIdMap.put("3", "113");
		productIdMap.put("4", "114");
		productIdMap.put("5", "100");
		productIdMap.put("6", "116");
		productIdMap.put("7", "117");
		productIdMap.put("8", "118");
		productIdMap.put("9", "119");
		
		productIdMap.put("10", "101");
		productIdMap.put("20", "102");
		productIdMap.put("30", "103");
		productIdMap.put("50", "104");
		productIdMap.put("100", "105");
		productIdMap.put("200", "106");
		productIdMap.put("300", "107");
		productIdMap.put("500", "108");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phone = (String) order.get("phone");//手机号
			Double fee = (Double) order.get("fee");//金额，以元为单位
			String curids = "hh"+DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phone;
			map.put("orderId", curids);
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数

			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());

			// 提交充值申请
			String resultStr = recharge(paramMap, phone, fee.intValue()+"", curids);
			log.info("号码["+phone+"]进行海航话费请求返回数据[" + resultStr + "]");
			if (null != resultStr) {
				JSONObject jsonObj = JSONObject.parseObject(resultStr).getJSONObject("msgResponse");
				String result = jsonObj.getJSONObject("responseInfo").getString("result");
				String resultCode = "";
				String resultDesc = "";
				String transactionId = "";
				if ("1".equals(result)) {
					resultCode = jsonObj.getJSONObject("data").getString("ResultCode");
					resultDesc = jsonObj.getJSONObject("data").getString("ResultDesc");
					transactionId = jsonObj.getJSONObject("data").getString("TransactionId");
					
					map.put("providerOrderId", transactionId);
				}
				// result=0或-1 或者 result=1并且ResultCode != 0 代表订单提交失败
				if (result.equals("0") || result.equals("-1") || (result.equals("1") && !resultCode.equals("0"))) {
					log.info("号码["+phone+"]进行海航新话费请求：提交失败!result="+result);
					flag = 0;
				} else if(result.equals("1") && resultCode.equals("0")){ // result=1并且ResultCode=0代表订单提交成功
					log.debug("号码["+phone+"]进行海航新话费请求：提交成功！");
					// 订单状态置为提交成功
					flag = 1;
				}
				map.put("resultCode", result+","+resultCode+":" + resultDesc);
			}
		}catch(Exception e){
			log.error("海航话费充值出错"+e.getMessage()+"|||"+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	public String recharge(Map<String, String> map,String phoneNo,String spec,String curids){
		String result="";
		try {
			String appSecret = map.get("appSecret");
			String appToken = map.get("appToken");
			String method = map.get("method");
			String timestamp="";
			String format ="json";
			StringBuffer source = new StringBuffer();
					
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			timestamp = sdf.format(now);
			
			JSONObject appJson = new JSONObject();
			JSONObject header = new JSONObject();
			JSONObject body = new JSONObject();
			// 封装应用请求头
			header.put("appId", map.get("appId"));	// 固定1
			header.put("securityKey", map.get("securityKey")); // 固定1
			header.put("version", map.get("version"));	// 固定1.0
		
			//封装应用请求包体
			body.put("hpno", map.get("hpno"));	// 平台定义200047
			body.put("pin", map.get("pin"));	// 平台定义123456
			body.put("payMone", map.get("payMone"));	// 固定 5
			body.put("channelCode", map.get("channelCode"));	// 固定 11
			body.put("requestAmount", map.get("requestAmount")); // 固定 1
			body.put("ofrId", productIdMap.get(spec));	// 商品ID,订购商品ID由商务提供
			body.put("rcgMobile", phoneNo);	// 充值号码
			body.put("orderNumber", curids);	// 订单号(30位以内，不能重复)
			body.put("destAttr", map.get("destAttr"));	// 被充值用户属性，固定填写2
			body.put("operationCode", map.get("operationCode"));	// 请求类型(固定0－普通空充)
			
			appJson.put("body", body);
			appJson.put("header", header);
			System.out.println(appJson.toString());
			log.info("海航话费提交数据["+appJson.toString()+"]");
			source.append(appSecret).append("AccessToken"+appToken).append("Format"+format).append("Method"+method)
					.append("Parameter"+appJson.toString()).append("Timestamp"+timestamp).append(appSecret);
			String sign = md5Encode(source.toString()); // MD5加密大写
			
			// 封装http请求
			String linkUrl = map.get("linkUrl");
			
			HttpPost httpRequst = new HttpPost(linkUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("Method", method));
			nvps.add(new BasicNameValuePair("AccessToken", appToken));
			nvps.add(new BasicNameValuePair("Sign", sign));
			nvps.add(new BasicNameValuePair("Timestamp", timestamp));
			nvps.add(new BasicNameValuePair("Format", format));
			nvps.add(new BasicNameValuePair("Parameter", appJson.toString()));
			
			httpRequst.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			System.out.println("url="+httpRequst.getURI());	
			CloseableHttpClient httpclient = createSSLInsecureClient();
			HttpResponse httpResponse = httpclient.execute(httpRequst);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}else{
				httpclient.close();
			}
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	public String md5Encode(String str)
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
	
	/**
	 * HTTPS访问对象，信任所有证书
	 * 
	 * @return
	 */
	public CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("haiHang createSSLInsecureClient error|" + e.getMessage());
		}
		return HttpClients.createDefault();
	}
}
