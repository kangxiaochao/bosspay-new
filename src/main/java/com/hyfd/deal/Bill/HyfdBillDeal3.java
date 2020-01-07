package com.hyfd.deal.Bill;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.deal.BaseDeal;

public class HyfdBillDeal3 implements com.hyfd.deal.BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	@Override
	public Map<String,Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try {
			String submitUrl = "http://192.168.1.173:8080/haobai/api/order/quotaOrder";//提交地址
			//私钥
			String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPh/YRI7kTKy2hPIgla2aQHduDw4QbVH9WBfED4TQHGdhpL/LHPRc9+YpHO5h6Ln8OTo2HaSeVGSfMj3VI2pBbnp2QW8d3RrL2KWpUkQEmNmOsKkBBJ0CAJjSPGQdBRf+ECvVyk/4SBuL+UA5PgYttSEFEy40Ca+JKNcz4v0OWhFAgMBAAECgYBpkaPpnQjIYxcmhG5q7D+cHVehrbysiQ+Di59Y39Dm86cyV9nIsljpyu1ChLVMzgCXHxym8v87WmJM9lYjgIT/ajwPX7wUD6A2O8wos7XSeMheOMddwHF9r7G03xMFjWzPM81H0uAW83AvrMlDJMTcrDvX4gPSdDUf6TqgQdNWQQJBAP34FJXV87JGnyquuwMaF0kEfrWNyHyKRkF87viDUWLQbcsvzQ3LQHsWZvTZ1AaN5dlt+9y6b9/J2JmXRki04Y0CQQD6fBkAt3+fJJ9Xax8I2Il5aLw5lj620oJcGN97Fp6vFsCpYGOtwWnbT7/fWTR0p2bjEFk6bPMhcYCAqdBEcseZAkA5CAWvZB1Wkm9ZyWUrnCdQeVbu9EjqVq7SM/kCC5MyKnYNY82bZGI1geFa6LOpSSItgnpFpgRLb+tZClR5LU1dAkBi2HHtbHw6SvNuReF7Vif59zC/8OBSuQOkYFRgeG6qXWot7NGpEbg6SWBIPSWMGztow1zSx4eyXN3+6AQ93qWRAkEArdP7yCrjaOHcBhRLPeIeSfyZgj25kBN1QRZ4n9zpmB88vI4WHiJsdww6zx+yklc/JPgmyrlHyR+ME4DwU2bXAw==";
			String callbackUrl = "http://192.168.1.145:8080/bosspay/status/HyfdBill";//回调地址
			log.debug("进入好亚飞达话费处理");
			String terminalName = "lks";//客户名
			String phoneNo = (String) order.get("phone");//手机号
			String spec = order.get("value")+"";//获取消费金额,以分为单位
			String orderType = "1";//业务类型
			String scope = "nation";//适用范围（默认nation）
			String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS");
			String customerOrderId = timeStamp+phoneNo+((int)(Math.random()*9000)+1000);//32位订单号
			map.put("orderId", customerOrderId);
			String Signature = "";// 数据私钥签名
			Signature = sign(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName, timeStamp,
					privateKey);
			String param = "terminalName=" + terminalName + "&customerOrderId=" + customerOrderId + "&phoneNo=" + phoneNo
					+ "&orderType=" + orderType + "&scope=" + scope + "&spec=" + spec + "&callbackUrl=" + callbackUrl
					+ "&timeStamp=" + timeStamp + "&signature=" + Signature;
			String result = get(submitUrl+"?"+param);
			System.out.println("----"+result);
			JSONObject resultJson = JSONObject.parseObject(result);
			String code = resultJson.getString("code");
			String message = resultJson.getString("message");
			map.put("resultCode", code+":"+message);
			if(code.equals("0")){
				flag = 1;
			}else{
				flag = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("status", flag);
		return map;
	}
	
	/**
	 * @author lks 2016年12月16日下午4:39:34
	 * @param orderId
	 * @return -1=异常，0=失败，1=成功，2=处理中
	 */
	public static Map<String,Object> query(String orderId){
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String url = "http://192.168.1.173:8080/haobai/api/order/queryOrder";
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPh/YRI7kTKy2hPIgla2aQHduDw4QbVH9WBfED4TQHGdhpL/LHPRc9+YpHO5h6Ln8OTo2HaSeVGSfMj3VI2pBbnp2QW8d3RrL2KWpUkQEmNmOsKkBBJ0CAJjSPGQdBRf+ECvVyk/4SBuL+UA5PgYttSEFEy40Ca+JKNcz4v0OWhFAgMBAAECgYBpkaPpnQjIYxcmhG5q7D+cHVehrbysiQ+Di59Y39Dm86cyV9nIsljpyu1ChLVMzgCXHxym8v87WmJM9lYjgIT/ajwPX7wUD6A2O8wos7XSeMheOMddwHF9r7G03xMFjWzPM81H0uAW83AvrMlDJMTcrDvX4gPSdDUf6TqgQdNWQQJBAP34FJXV87JGnyquuwMaF0kEfrWNyHyKRkF87viDUWLQbcsvzQ3LQHsWZvTZ1AaN5dlt+9y6b9/J2JmXRki04Y0CQQD6fBkAt3+fJJ9Xax8I2Il5aLw5lj620oJcGN97Fp6vFsCpYGOtwWnbT7/fWTR0p2bjEFk6bPMhcYCAqdBEcseZAkA5CAWvZB1Wkm9ZyWUrnCdQeVbu9EjqVq7SM/kCC5MyKnYNY82bZGI1geFa6LOpSSItgnpFpgRLb+tZClR5LU1dAkBi2HHtbHw6SvNuReF7Vif59zC/8OBSuQOkYFRgeG6qXWot7NGpEbg6SWBIPSWMGztow1zSx4eyXN3+6AQ93qWRAkEArdP7yCrjaOHcBhRLPeIeSfyZgj25kBN1QRZ4n9zpmB88vI4WHiJsdww6zx+yklc/JPgmyrlHyR+ME4DwU2bXAw==";
		String terminalName = "lks";
		String customerOrderId = orderId;
		String orderType = "2";
		String Signature = "";
		try {
			Signature = sign(terminalName, customerOrderId, orderType, privateKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String paramStr = "terminalName=" + terminalName + "&customerOrderId=" + customerOrderId + "&orderType="
				+ orderType + "&signature=" + Signature;
		String result = get(url + "?" + paramStr);
		JSONObject json = JSONObject.parseObject(result);
		String code = json.getString("code");
		if(code.equals("0")){
			JSONObject data = json.getJSONObject("data");
			String status = data.getString("status");
			String providerOrderId = data.getString("orderId");
			map.put("providerOrderId", providerOrderId);
			if(status.equals("0")){
				flag = 1;
			}else if(status.equals("1")){
				flag = 2;
			}else if(status.equals("2")){
				flag = 0;
			}
		}
		map.put("flag", flag);
		return map;
	}
	
	public static void main(String[] args){
		System.out.println("==="+DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS")+"18764166237"+((int)(Math.random()*9000)+1000));
	}
	
	public static String get(String url) {
		System.out.println(url);
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取状态行
				//System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, "UTF-8");
					return out;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(null != httpClient){
					httpClient.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	} 
	
	/**
	 * 生成签名
	 * 
	 * @param paramMap
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static String sign(String terminalName, String customerOrderId, String orderType, String privateKeyStr)
			throws Exception {
		byte[] keyByteArray = Base64.decodeBase64(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		String md5 = md5Param(terminalName, customerOrderId, orderType);

		System.out.println("----测试中，生成的md5:[" + md5 + "]");

		byte[] rsa = encodeBytePrivate(md5.getBytes(), privateKey);

		return Hex.encodeHexString(rsa);
	}
	
	public static String md5Param(String terminalName, String customerOrderId, String orderType) {
		String content = getCustSignature(terminalName, customerOrderId, orderType);
		System.err.println("生成签名的参数:" + content);
		return md5(content);
	}
	public static String getCustSignature(String terminalName, String customerOrderId, String orderType) {
		StringBuilder sb = new StringBuilder();
		sb.append("customerOrderId=" + customerOrderId);
		sb.append("&orderType=" + orderType);
		sb.append("&terminalName=" + terminalName);
		return sb.toString();
	}
	/**
	 * 生成签名
	 * 
	 * @param paramMap
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static String sign(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp, String privateKeyStr) throws Exception {
		byte[] keyByteArray = Base64.decodeBase64(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		String md5 = md5Param(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName, timeStamp);

		System.out.println("----测试中，生成的md5:[" + md5 + "]");

		byte[] rsa = encodeBytePrivate(md5.getBytes(), privateKey);

		return Hex.encodeHexString(rsa);
	}

	public static String md5Param(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp) {
		String content = getCustSignature(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName,
				timeStamp);
		System.err.println("生成签名的参数:" + content);
		return md5(content);
	}

	/**
	 * RSA 加密返回byte[]
	 * 
	 * @param content
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(content);
	}

	/**
	 * MD5摘要
	 *
	 * @param s
	 * @return
	 */
	public final static String md5(String s) {
		try {
			byte[] btInput = s.getBytes("UTF-8");
			// 获得MD5摘要算法�?MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘�?
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			return Hex.encodeHexString(md);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getCustSignature(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp) {
		StringBuilder sb = new StringBuilder();
		sb.append("callbackUrl=" + callbackUrl);
		sb.append("&customerOrderId=" + customerOrderId);
		sb.append("&orderType=" + orderType);
		sb.append("&phoneNo=" + phoneNo);
		sb.append("&scope=" + scope);
		sb.append("&spec=" + spec);
		sb.append("&terminalName=" + terminalName);
		sb.append("&timeStamp=" + timeStamp);
		return sb.toString();
	}

	/**
	 * 验签
	 * 
	 * @param paramMap
	 * @param signature
	 * @param publicKeyStr
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp, String signature, String publicKeyStr)
			throws Exception {
		byte[] keyByteArray = Base64.decodeBase64(publicKeyStr);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		String md5 = md5Param(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName, timeStamp);
		byte[] rsa = decodeBytePublic(Hex.decodeHex(signature.toCharArray()), publicKey);
		return new String(rsa).equals(md5);
	}

	/**
	 * 解密返回byte[]
	 * 
	 * @param content
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] decodeBytePublic(byte[] content, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(content);
	}
	
}
