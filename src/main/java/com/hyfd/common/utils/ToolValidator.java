package com.hyfd.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 文件读取
 * @author 2015-9-7 下午3:12:21
 */
public abstract class ToolValidator {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ToolValidator.class);

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
//		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
//		p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$"); // 验证手机号
//		p = Pattern.compile("^((13[0-9])|(15[0-9])|(16[0-9])|(19[0-9])|(14[0-9])|(17[0-9])|(18[0-9]))\\d{8}$"); // 验证手机号
		p = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	
	/**
	 * 物联网卡验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isWLWMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
//		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
//		p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$"); // 验证手机号
		p = Pattern.compile("^((10648)|(10649))\\d{8}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}
	
	/**
	 * 是否为正整数
	 * @param str
	 * @return
	 */
	public static boolean isNumber1(String str){
		Pattern pattern = Pattern.compile("\\d+");
		return pattern.matcher(str).matches();
	}
	
	/**
	 * 是否为正整数或负整数
	 * @param str
	 * @return
	 */
	public static boolean isNumber2(String str){
		Pattern pattern = Pattern.compile("-?\\d+");
		return pattern.matcher(str).matches();
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}
}
