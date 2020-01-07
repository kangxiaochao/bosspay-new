package com.hyfd.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hyfd.common.GenerateData;

/**
 *  时间工具
 * @author
 */
public final class DateUtils {
    /**
     *  获得当前时间
     *  格式为：yyyy-MM-dd HH:mm:ss
    */
    public static String getNowTime() {
        Date nowday = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        String time = sdf.format(nowday);
        return time;
    }
    
    /**
     * <h5>功能:</h5>获得当前时间,精确到毫秒,格式:yyyyMMddHHmmss
     * @return 
     *
     * @author zhangpj	@date 2016年9月9日
     */
    public static String getNowTimeToSec() {
    	Date nowday = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");// 精确到毫秒
    	String time = sdf.format(nowday);
    	return time;
    }
    
    /**
     * <h5>功能:</h5>获得当前时间,精确到毫秒,格式:yyyyMMddHHmmssSSS
     * @return 
     *
     * @author zhangpj	@date 2016年9月9日
     */
    public static String getNowTimeToMS() {
    	Date nowday = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");// 精确到毫秒
    	String time = sdf.format(nowday);
    	return time;
    }

    /**
     * 获取当前系统时间戳
     * @return
     */
    public static Long getNowTimeStamp() {
        return System.currentTimeMillis();
    }

    public static Long getNowDateTime() {
        return new Date().getTime()/1000;
//        return new Date().getTime()/1000;
    }

    /**
     * 自定义日期格式
     * @param format
     * @return
     */
    public static String getNowTime(String format) {
        Date nowday = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);// 精确到秒
        String time = sdf.format(nowday);
        return time;
    }

    /**
     * 将时间字符转成Unix时间戳
     * @param timeStr
     * @return
     * @throws java.text.ParseException
     */
    public static Long getTime(String timeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        Date date = sdf.parse(timeStr);
        return date.getTime()/1000;
    }

    /**
     * 将Unix时间戳转成时间字符
     * @param timestamp
     * @return
     */
    public static String getTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
        Date date = new Date(timestamp*1000);
        return sdf.format(date);
    }

    /**
     * 获取半年后的时间
     * 时间字符格式为：yyyy-MM-dd HH:mm:ss
     * @return 时间字符串
     */
    public static String getHalfYearLaterTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒

        Calendar calendar = Calendar.getInstance();
        int currMonth = calendar.get(Calendar.MONTH) + 1;

        if (currMonth >= 1 && currMonth <= 6) {
            calendar.add(Calendar.MONTH, 6);
        } else {
            calendar.add(Calendar.YEAR, 1);
            calendar.set(Calendar.MONTH, currMonth - 6 - 1);
        }

        return sdf.format(calendar.getTime());
    }
    
	/**
	 * <h5>功能:获取两个时间相差的秒数</h5>
	 * 
	 * @author zhangpj	@date 2018年9月5日
	 * @param str1 格式：yyyy-MM-dd HH:mm:ss
	 * @param str2 格式：yyyy-MM-dd HH:mm:ss
	 * @return 
	 */
	public static int getDistanceSecond(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long diff;
		long min = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			diff = time1 - time2;
			min = diff / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) Math.abs(min);
	}
    
    public static void main(String[] args) {
    	System.out.println("getNowTime()\t"+getNowTime());
		System.out.println("getNowTime()\t"+getNowTime("yyyy-MM-dd"));
		System.out.println("getNowTimeToSec()\t"+getNowTimeToSec());
		System.out.println("getNowTimeToMS()\t"+getNowTimeToMS());
		System.out.println("getNowTimeStamp()\t"+getNowTimeStamp());
		System.out.println("getNowDateTime()\t"+getNowDateTime());
		System.out.println("getNowTime(String format)\t"+getNowTime("yyyymmddhhmmssSSS").length());
		System.out.println("getHalfYearLaterTime()\t"+getHalfYearLaterTime());
		System.out.println(getNowTimeToMS()+ "13280009366" + GenerateData.getIntData(9,4));
		System.out.println("20160922092954983132800093661014".length());
		
		System.out.println(getDistanceSecond("2018-04-04 10:10:49", "2018-04-04 10:10:53"));
	}
}
