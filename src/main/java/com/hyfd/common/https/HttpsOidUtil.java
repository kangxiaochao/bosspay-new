package com.hyfd.common.https;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 随机流水号
 * 
 * @author Administrator
 */

public class HttpsOidUtil {

	private static java.security.SecureRandom sr = new java.security.SecureRandom();

	/**
	 * Oid 构造子注解。
	 */
	public HttpsOidUtil() {
		super();
	}

	/**
	 * 取得OID。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getOid() {
		String sOid = System.currentTimeMillis() + "" + sr.nextInt()
				+ sr.nextInt();
		if (sOid.length() < 32) {
			for (int i = sOid.length() + 1; i <= 32; i++) {
				sOid += "0";
			}
		} else if (sOid.length() > 32) {
			sOid = sOid.substring(0, 32);
		}

		sOid = sOid.replace('-', '0');

		return sOid;
	}

	/**
	 * 取得RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getRiid() {
		Date dte = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
		return sdf.format(dte) + (int) ((Math.random() * 9 + 1) * 100000);

	}

	/**
	 * 取得16位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getRiidBy16() {
		Date dte = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddSSS");
		return sdf.format(dte) + (int) ((Math.random() * 9 + 1) * 100000);

	}

	/**
	 * 取得16位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getRiidByDL16() {
		Date dte = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddSSS");
		return "DL" + sdf.format(dte) + (int) ((Math.random() * 9 + 1) * 1000);

	}

	/**
	 * 取得16位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getRiidByM() {
		Date dte = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
		Random random = new Random();

		int x = random.nextInt(89999999);
		x = x + 10000000;
		String sOid = sdf.format(dte) + "" + x;
		// if (sOid.length() > 20) {
		// sOid = sOid.substring(0, 20);
		// }
		return "M" + sOid;

	}

	/**
	 * 取得32位RIID,根据时间+随机数获取。 创建日期：(2001-6-20 20:06:41)
	 * 
	 * @return java.lang.String
	 */
	public static String getRiidBy32() {
		Date dte = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
		return sdf.format(dte) + "" + dte.getTime() + ""
				+ (int) ((Math.random() * 9 + 1) * 1000);

	}
}
