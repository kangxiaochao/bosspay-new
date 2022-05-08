package com.hyfd.test;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.hyfd.common.utils.Base64;
import org.apache.commons.codec.binary.Hex;

public class HaoBaiSign {

	/**
	 * 充值订单下单接口签名生成
	 */
	public static String sign(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp, String privateKeyStr) throws Exception {
		byte[] keyByteArray = Base64.decode(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		String md5 = md5Param(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName, timeStamp);

		byte[] rsa = encodeBytePrivate(md5.getBytes(), privateKey);

		return Hex.encodeHexString(rsa);
	}

	/**
	 * 查询余额密钥生成
	 */
	public static String sign(String terminalName, String privateKeyStr) throws Exception {
		byte[] keyByteArray = Base64.decode(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		String md5 = md5Param(terminalName);
		byte[] rsa = encodeBytePrivate(md5.getBytes(), privateKey);

		return Hex.encodeHexString(rsa);
	}

	/**
	 * 生成签名
	 * @param terminalName
	 * @param customerOrderId
	 * @param orderType
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static String sign(String terminalName, String customerOrderId, String orderType, String privateKeyStr)
			throws Exception {
		byte[] keyByteArray = Base64.decode(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		String md5 = md5Param(terminalName, customerOrderId, orderType);

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

	public static String md5Param(String terminalName) {
		String content = "terminalName=" + terminalName;
		System.err.println("生成签名的参数:" + content);
		return md5(content);
	}

	public static String md5Param(String terminalName, String customerOrderId, String orderType) {
		String content = getCustSignature(terminalName, customerOrderId, orderType);
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
			// 获得MD5摘要算法 生成MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
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

	public static String getCustSignature(String terminalName, String customerOrderId, String orderType) {
		StringBuilder sb = new StringBuilder();
		sb.append("customerOrderId=" + customerOrderId);
		sb.append("&orderType=" + orderType);
		sb.append("&terminalName=" + terminalName);
		return sb.toString();
	}

	/**
	 * 验签
	 * @param callbackUrl
	 * @param customerOrderId
	 * @param orderType
	 * @param phoneNo
	 * @param scope
	 * @param spec
	 * @param terminalName
	 * @param timeStamp
	 * @param signature
	 * @param publicKeyStr
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String callbackUrl, String customerOrderId, String orderType, String phoneNo,
			String scope, String spec, String terminalName, String timeStamp, String signature, String publicKeyStr)
					throws Exception {
		byte[] keyByteArray = Base64.decode(publicKeyStr);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		String md5 = md5Param(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName, timeStamp);
		byte[] rsa = decodeBytePublic(Hex.decodeHex(signature.toCharArray()), publicKey);
		return new String(rsa).equals(md5);
	}

	/**
	 * 验签
	 * @param terminalName
	 * @param customerOrderId
	 * @param orderType
	 * @param signature
	 * @param publicKeyStr
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(String terminalName, String customerOrderId, String orderType, String signature,
			String publicKeyStr) throws Exception {
		byte[] keyByteArray = Base64.decode(publicKeyStr);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

		String md5 = md5Param(terminalName, customerOrderId, orderType);
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
