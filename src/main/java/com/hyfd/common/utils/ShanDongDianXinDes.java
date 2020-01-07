package com.hyfd.common.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ShanDongDianXinDes {

	/**
	 * 认证码加密 @ srcStr 加密的内容 @ key 加密key
	 * 
	 * @return
	 */
	public static String encrypt(String srcStr, String key) {
		String strEncrypt = null;
		byte[] byteFina = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(1, generateKey(key));
			byteFina = cipher.doFinal(srcStr.getBytes("UTF-8"));
		} catch (Exception e) {
			System.out.println("-9999" + "字节加密异常" + e.getMessage());
		} finally {
			cipher = null;
		}
		strEncrypt = Base64.encodeBase64String(byteFina);
		return strEncrypt;
	}

	/**
	 * 生成秘钥 @ strKey
	 * 
	 * @return
	 */
	public static Key generateKey(String strKey) {
		try {
			DESKeySpec desKeySpec = new DESKeySpec(strKey.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			return keyFactory.generateSecret(desKeySpec);
		} catch (Exception e) {
			System.out.println("-9999" + "生成密钥异常" + e.getMessage());
		}
		return null;
	}

}
