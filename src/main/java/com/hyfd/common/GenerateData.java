package com.hyfd.common;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class GenerateData {
	
	static Random r=new Random();
	static DecimalFormat floatFormat = new DecimalFormat("0.00");
	static String dateFormat="yyyy-MM-dd";
	static String timeFormat="HH:mm:ss";
	static String longDateFormat="yyyyMMDDHHmmssSSS";
	static DateFormat df = new SimpleDateFormat(dateFormat+" "+timeFormat);
	static DateFormat dfl=new SimpleDateFormat(longDateFormat);
	static String letterLow="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static String letterUpper=letterLow.toUpperCase();
	static String letterLowExt="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static int getIntData(int inD){
		return r.nextInt(inD);
	}
	
	public static int getIntData(int inMax,int inCount){
		String c="";
		int x=0;
		while(c.length()<inCount){
			x=getIntData(inMax);
			c+=""+x+"";
		}
		return new Integer(c);
	}
	
	/**
	 * <h5>功能:</h5>生成指定长度的随机整数字符串
	 * @param strLength 指定生成的长度
	 * @return 
	 *
	 * @author zhangpj	@date 2016年9月11日
	 */
	public static String getStrData(int strLength){
		StringBuffer sbf = new StringBuffer();
		while(sbf.length() < strLength){
			sbf.append(getIntData(9));
		}
		return sbf.toString();
	}
	
	public static float getFloatData(int inD){
		return new Float(floatFormat.format(r.nextFloat()*inD));
	}
	
	public static char getCharLow(){
		return letterLow.charAt(getIntData(26));
	}
	
	public static char getChineseChar0(){
		return (char)(0x4e00 + r.nextInt(0x9fa5 - 0x4e00 + 1));
	}
	public static char getChineseChar1() throws Exception {
		byte[] b = new byte[2];
		b[0] = (new Integer((176 + Math.abs(r.nextInt(39))))).byteValue();
		b[1] = (new Integer(161 + Math.abs(r.nextInt(93)))).byteValue();
		return new String(b, "GB2312").charAt(0);
	}
	
	public static String getChineseString0(int inLength){
		String s="";
		for(int i=0;i<inLength;i++) s+=getChineseChar0();
		return s;		
	}
	
	public static String getChineseString1(int inLength) throws Exception {
		String s="";
		for(int i=0;i<inLength;i++) s+=getChineseChar1();
		return s;		
	}
	
	public static char getCharUpper(){
		return letterUpper.charAt(getIntData(26));
	}
	
	/**
	 * @功能描述：	随机生成指定长度的字母
	 *
	 * @param inLentgh
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月3日
	 */
	public static String getStringRandom(int inLentgh){
		StringBuilder sb=new StringBuilder("");
		for(int i=0;i<inLentgh;i++) sb.append(letterLow.charAt(getIntData(52)));
		return sb.toString();
	}
	
	public static String getStringRandomExt(int inLentgh){
		StringBuilder sb=new StringBuilder("");
		for(int i=0;i<inLentgh;i++) sb.append(letterLowExt.charAt(getIntData(62)));
		return sb.toString();
	}
	
	/**
	 * @功能描述：	随机生成指定长度的小写字母
	 *
	 * @param inLentgh
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月3日
	 */
	public static String getStringRandomLow(int inLentgh){
		return getStringRandom(inLentgh).toLowerCase();
	}
	
	/**
	 * @功能描述：	随机生成指定长度的大写字母
	 *
	 * @param inLentgh
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月3日
	 */
	public static String getStringRandomUpper(int inLentgh){
		return getStringRandom(inLentgh).toUpperCase();
	}
	
	public static String getSystemDateTime()  {
		return df.format(Calendar.getInstance().getTime());
	}
	
	public static String getLongDate(){
		return dfl.format(Calendar.getInstance().getTime());
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println(GenerateData.getIntData(26));
//		System.out.println(GenerateData.getIntData(9,6));
//		System.out.println(GenerateData.getFloatData(10));
//		System.out.println(GenerateData.getCharLow());
//		System.out.println(GenerateData.getChineseChar0());
//		System.out.println(GenerateData.getChineseString0(15));
//		System.out.println(GenerateData.getChineseChar1());
//		System.out.println(GenerateData.getChineseString1(15));
//		System.out.println(GenerateData.getCharUpper());
//		System.out.println(GenerateData.getStringRandom(5));
//		System.out.println(GenerateData.getStringRandomLow(5));
//		System.out.println(GenerateData.getStringRandomUpper(5));
//		System.out.println(GenerateData.getSystemDateTime());
//		System.out.println(GenerateData.getLongDate());
//		System.out.println(GenerateData.getUUID());
		for (int i = 0; i < 10; i++) {
//			System.out.println(GenerateData.getStringRandom(6));
			System.out.println(GenerateData.getStringRandomExt(10));
		}
	}

}
