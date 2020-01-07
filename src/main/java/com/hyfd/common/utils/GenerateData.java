package com.hyfd.common.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class GenerateData {
	
	static Random r=new Random();
	static DecimalFormat floatFormat = new DecimalFormat("0.00");
	static String dateFormat="yyyy-MM-dd";
	static String timeFormat="HH:mm:ss";
	static String longDateFormat="yyyyMMDDHHmmssSSS";
	static DateFormat df = new SimpleDateFormat(dateFormat+" "+timeFormat);
	static DateFormat dfl=new SimpleDateFormat(longDateFormat);
	static String letterLow="abcdefghijklmnopqrstuvwxyz";
	static String letterUpper=letterLow.toUpperCase();
	
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
	
	public static String getStringLow(int inLentgh){
		StringBuilder sb=new StringBuilder("");
		for(int i=0;i<inLentgh;i++) sb.append(letterLow.charAt(getIntData(26)));
		return sb.toString();
	}
	
	public static String getStringUpper(int inLentgh){
		return getStringLow(inLentgh).toUpperCase();
	}
	
	public static String getSystemDateTime()  {
		return df.format(Calendar.getInstance().getTime());
	}
	
	public static String getLongDate(){
		return dfl.format(Calendar.getInstance().getTime());
	}
	
    /**
     * java生成随机数字和字母组合
     * @param length[生成随机数的长度]
     * @return
     */
    public static String getCharAndNumr(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; 
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; 
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }
    
    /**
     * java生成随机数字和字母(全小写)组合
     * @param length[生成随机数的长度]
     * @return
     */
    public static String getCharAndNumrToLower(int length){
    	return getCharAndNumr(length).toLowerCase();
    }
    
    /**
     * java生成随机数字和字母(全大写)组合
     * @param length[生成随机数的长度]
     * @return
     */
    public static String getCharAndNumrToUpper(int length){
    	return getCharAndNumr(length).toUpperCase();
    }
	
	public static void main(String[] args) throws Exception {
		System.out.println(GenerateData.getIntData(26));
		//六位随机数,如果是0开头则自动舍去
		System.out.println(GenerateData.getIntData(9,6));
		// 生成指定长度的随机整数字符串
		System.out.println(GenerateData.getStrData(10));
		System.out.println(GenerateData.getFloatData(10));
		System.out.println(GenerateData.getCharLow());
		System.out.println(GenerateData.getChineseChar0());
		System.out.println(GenerateData.getChineseString0(15));
		System.out.println(GenerateData.getChineseChar1());
		System.out.println(GenerateData.getChineseString1(15));
		System.out.println(GenerateData.getCharUpper());
		System.out.println(GenerateData.getStringLow(5)+"------");
		System.out.println(GenerateData.getStringUpper(5));
		//当前系统时间 2015-10-31 16:20:15
		System.out.println(GenerateData.getSystemDateTime());
		//当前系统时间 201510304162015773
		System.out.println(GenerateData.getLongDate()+"----");
		int i = 1;
		do {
			// 大小写字母加数字组合
			System.out.println(GenerateData.getCharAndNumr(6));
			i++;
		} while (i <= 55);
		System.out.println(getCharAndNumrToLower(23));
		System.out.println(getCharAndNumrToUpper(23));
	}
}
