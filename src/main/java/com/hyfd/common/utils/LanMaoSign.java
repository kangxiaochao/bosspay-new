package com.hyfd.common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @名称: LanMaoSign
 * @描述: 签名工具类，包括RSA和DSA签名验签方法
 * @作者:
 * @时间: 2014年7月15日
 * @版本: 1.0
 *
 */
public class LanMaoSign {

	private static final String CHAR_ENCODING = "UTF-8";

	public static String encryptToHex(String key, String data) {
		key = addZeroForNum(key, 24);
		try {
			byte[] keyByte = key.getBytes(CHAR_ENCODING);
			byte[] dataByte = data.getBytes(CHAR_ENCODING);
			byte[] valueByte = des3Encryption(keyTo24Byte(keyByte), dataByte);
			return toHex(valueByte);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}
		return output.toString();
	}

	private static byte[] des3Encryption(byte[] key, byte[] data)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, IllegalStateException {

		final String Algorithm = "DESede";
		if (key.length != 24) {
			throw new RuntimeException("Invalid DESede key length (must be 24 bytes)");
		}
		SecretKey deskey = new SecretKeySpec(key, Algorithm);

		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.ENCRYPT_MODE, deskey);
		return c1.doFinal(data);
	}

	private static byte[] keyTo24Byte(byte[] keyByte) {
		if (keyByte == null || keyByte.length == 0) {
			throw new RuntimeException("keyByte is null");
		}
		byte[] toByte = null;
		if (keyByte.length == 24) {
			return keyByte;
		} else if (keyByte.length > 24) {
			toByte = new byte[24];
			System.arraycopy(keyByte, 0, toByte, 0, 24);
		} else {
			toByte = new byte[24];
			System.arraycopy(keyByte, 0, toByte, 0, keyByte.length);
		}
		return toByte;
	}

	public static String decryptFromHex(String key, String value) {
		try {
			byte[] keyByte = key.getBytes(CHAR_ENCODING);
			byte[] valueByte = fromHex(value);
			byte[] dataByte = des3Decryption(keyTo24Byte(keyByte), valueByte);
			String data = new String(dataByte, CHAR_ENCODING);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] des3Decryption(byte[] key, byte[] data)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, IllegalStateException {

		final String Algorithm = "DESede";
		if (key.length != 24) {
			throw new RuntimeException("Invalid DESede key length (must be 24 bytes)");
		}
		SecretKey deskey = new SecretKeySpec(key, Algorithm);
		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.DECRYPT_MODE, deskey);
		return c1.doFinal(data);
	}

	private static byte[] fromHex(String input) {
		if (input == null)
			return null;
		byte output[] = new byte[input.length() / 2];
		for (int i = 0; i < output.length; i++)
			output[i] = (byte) Integer.parseInt(input.substring(i * 2, (i + 1) * 2), 16);
		return output;
	}

	// 密钥长度不足后面补零
	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		StringBuffer sb = null;
		while (strLen < strLength) {
			sb = new StringBuffer();
			sb.append(str).append("0");// 右(后)补0
			str = sb.toString();
			strLen = str.length();
		}
		return str;
	}

	public static void main(String[] args) {
		String value = "1234567890123456789";
		String key = "NEUSOFT2";
		String result = encryptToHex(key, value);
		System.out.println("encrypt: " + result);
		System.out.println("3c45aac44b04660fe688ead959d6ed48232fa26d4f836f40".equals(result));

		System.out.println(decryptFromHex(key, result));
	}
}
