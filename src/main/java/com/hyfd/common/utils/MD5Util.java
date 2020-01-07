package com.hyfd.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	static public boolean verify(String src, String code) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = null;
		md5 = MessageDigest.getInstance("MD5");

		byte[] byteArray = null;
		byteArray = src.getBytes("UTF-8");
	
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer(32);
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().equals(code);
	}
	
	static public String MD5Encode(String src, String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md5 = null;
		md5 = MessageDigest.getInstance("MD5");

		byte[] byteArray = null;
		byteArray = src.getBytes(charset);
	
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer(32);
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}