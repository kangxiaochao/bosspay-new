package com.hyfd.common.utils;

import com.hyfd.common.utils.zx.AESUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class MD5 {
	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public MD5() {
	}

	public static String byteArrayToString(byte b[]) {
		StringBuffer bths = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			bths.append(byteToHexString(b[i]));

		return bths.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return (new StringBuilder(String.valueOf(hexDigits[d1]))).append(
				hexDigits[d2]).toString();
	}

	public static String ToMD5(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToString(md.digest(resultString.getBytes()));
		} catch (Exception exception) {
		}
		return resultString;
	}

	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes("UTF-8");
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 签名字符串
	 *
	 * @param text          需要签名的字符串
	 * @param key           密钥
	 * @param charset 编码格式
	 * @return 签名结果
	 */


	public static String signAES(String text, String key, String charset) {
		String aesText = AESUtil.encrypt(text, key);
		return DigestUtils.md5Hex(getContentBytes(aesText, charset));
	}



	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}
}