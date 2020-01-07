package com.hyfd.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class FXSignUtils {

	/**
	 * CryptTool 封装了一些加密工具方法, 包括 3DES, MD5 等.
	 * 
	 * @author hxq
	 * @version 1.0 2006-01-10
	 */

	private final static String DES = "DES";

	public FXSignUtils() {
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * MD5 摘要计算(byte[]).
	 * 
	 * @param src
	 *            byte[]
	 * @throws Exception
	 * @return byte[] 16 bit digest
	 */
	public static byte[] md5Digest(byte[] src) throws Exception {
		java.security.MessageDigest alg = java.security.MessageDigest.getInstance("MD5"); // MD5
																							// is
																							// 16
																							// bit
																							// message
																							// digest

		return alg.digest(src);
	}

	/**
	 * MD5 摘要计算(String).
	 * 
	 * @param src
	 *            String
	 * @throws Exception
	 * @return String
	 */
	public static String md5Digest(String src) {
		try {
			return byteArrayToHexString(md5Digest(src.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * MD5 摘要计算(String)，此函数会在源字符串后面添加"&KEY=serectKey".
	 * 
	 * @param src
	 *            String
	 * @param serectKey
	 *            签名秘钥
	 * @throws Exception
	 * @return String
	 */
	public static String md5Digest(String src, String serectKey) {
		try {
			return byteArrayToHexString(md5Digest((src + "&KEY=" + serectKey).getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取所有参数的签名字符串
	 * 
	 * @param parameters
	 *            要签名的参数
	 * @param 业务参数
	 * @param secretKey
	 *            签名秘钥
	 * @return
	 * @throws Exception
	 */
	public static String md5Digest(Map<String, String> parameters, String data, String secretKey) {
		// 第一步：检查参数是否已经排序
		TreeMap<String, Object> sortedParas = new TreeMap<String, Object>();
		sortedParas.putAll(parameters);
		sortedParas.remove("sign");

		// 第二步：把所有参数名和参数值串在一起
		Set<Entry<String, Object>> paramSet = sortedParas.entrySet();
		StringBuilder query = new StringBuilder(secretKey);
		for (Entry<String, Object> param : paramSet) {
			if (areNotEmpty(param.getKey(), (String) param.getValue())) {
				query.append(param.getKey().toUpperCase()).append(param.getValue());
			}
		}
		query.append(data);
		query.append(secretKey);
		return md5Digest(query.toString());
	}

	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}

	/**
	 * 检查指定的字符串是否为空。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty(" ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value
	 *            待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取所有参数的签名字符串
	 * 
	 * @param parameters
	 *            要签名的参数
	 * @param secretKey
	 *            签名秘钥
	 * @return
	 * @throws Exception
	 */
	public static String md5Digest(Map<String, String> parameters, String secretKey) {
		// 第一步：检查参数是否已经排序
		TreeMap<String, Object> sortedParas = new TreeMap<String, Object>();
		sortedParas.putAll(parameters);
		sortedParas.remove("check_sum_sign");

		// 第二步：把所有参数名和参数值串在一起
		Set<Entry<String, Object>> paramSet = sortedParas.entrySet();
		StringBuilder query = new StringBuilder(secretKey);
		for (Entry<String, Object> param : paramSet) {
			if (areNotEmpty(param.getKey(), (String) param.getValue())) {
				query.append(param.getKey().toUpperCase()).append(param.getValue());
			}
		}
		query.append(secretKey);
		return md5Digest(query.toString());
	}

	/**
	 * SHA1 摘要计算(byte[]).
	 * 
	 * @param src
	 *            byte[]
	 * @throws Exception
	 * @return byte[] 16 bit digest
	 */
	public static byte[] sha1Digest(byte[] src) throws Exception {
		java.security.MessageDigest alg = java.security.MessageDigest.getInstance("SHA-1"); // MD5
																							// is
																							// 16
																							// bit
																							// message
																							// digest

		return alg.digest(src);
	}

	/**
	 * SHA1 摘要计算(String).
	 * 
	 * @param src
	 *            String
	 * @throws Exception
	 * @return String
	 */
	public static String sha1Digest(String src) {
		try {
			return byteArrayToHexString(sha1Digest(src.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取所有参数的SHA1签名字符串
	 * 
	 * @param parameters
	 *            要签名的参数
	 * @param secretKey
	 *            签名秘钥
	 * @param urlStyle
	 *            是否使用URL字符串风格，使用则为name1=value1&name2=value2的样式连接字符串，
	 *            否则为name1value1name2value2的样式连接字符串
	 * @return
	 * @throws Exception
	 */
	public static String sha1Digest4Weixin(Map<String, String> parameters, boolean urlStyle) {
		// 第一步：检查参数是否已经排序
		TreeMap<String, Object> sortedParas = new TreeMap<String, Object>();
		sortedParas.putAll(parameters);
		sortedParas.remove("mac");
		sortedParas.remove("sign");

		// 第二步：把所有参数名和参数值串在一起
		Set<Entry<String, Object>> paramSet = sortedParas.entrySet();
		StringBuilder query = new StringBuilder();
		for (Entry<String, Object> param : paramSet) {
			if (areNotEmpty(param.getKey(), (String) param.getValue())) {
				if (urlStyle) {
					query.append("&").append(param.getKey()).append("=").append(param.getValue());
				} else {
					query.append(param.getKey()).append(param.getValue());
				}
			}
		}
		return sha1Digest(urlStyle ? query.substring(1) : query.toString());
	}

	/**
	 * 获取所有参数的字符串
	 * 
	 * @param parameters
	 *            要签名的参数
	 * @param urlStyle
	 *            是否使用URL字符串风格，使用则为name1=value1&name2=value2的样式连接字符串，
	 *            否则为name1value1name2value2的样式连接字符串
	 * @return
	 * @throws Exception
	 */
	public static String sortParameters4Weixin(Map<String, String> parameters, boolean urlStyle) {
		// 第一步：检查参数是否已经排序
		TreeMap<String, Object> sortedParas = new TreeMap<String, Object>();
		sortedParas.putAll(parameters);
		sortedParas.remove("mac");
		sortedParas.remove("sign");

		// 第二步：把所有参数名和参数值串在一起
		Set<Entry<String, Object>> paramSet = sortedParas.entrySet();
		StringBuilder query = new StringBuilder();
		for (Entry<String, Object> param : paramSet) {
			if (areNotEmpty(param.getKey(), (String) param.getValue())) {
				if (urlStyle) {
					query.append("&").append(param.getKey()).append("=").append(param.getValue());
				} else {
					query.append(param.getKey()).append(param.getValue());
				}
			}
		}
		return urlStyle ? query.substring(1) : query.toString();
	}

	/**
	 * SHA1 摘要计算(String)，此函数会在源字符串后面添加"&KEY=serectKey".
	 * 
	 * @param src
	 *            String
	 * @param serectKey
	 *            签名秘钥
	 * @throws Exception
	 * @return String
	 */
	public static String sha1Digest(String src, String serectKey) {
		try {
			return byteArrayToHexString(sha1Digest((src + "&KEY=" + serectKey).getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/** Test crypt */
	public static void main(String[] args) {
		try {
			// 使用key值生成 SIGN
			String keyStr = "123456";// 使用固定key

			Map<String, String> paras = new HashMap<String, String>();
			paras.put("MERCHANTID", "123456789");
			paras.put("UPTRANSEQ", "20080101000001");
			paras.put("Method", "2006050112564931556");
			paras.put("Charge_channel_code", "10000");
			paras.put("Msisdn", "Msisdn");
			paras.put("Amount", "Amount");
			paras.put("reqid", "reqid2014051317560000000000000000001");
			paras.put("aaa", "aaatest");
			String SIGN = md5Digest(paras, keyStr);
			System.out.println("加密后字符串1 == " + SIGN);
			System.out.println("加密后字符串2 == " + SIGN);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
