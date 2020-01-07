package com.hyfd.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

	 /**
     * 缺省的日期显示格式： yyyyMMdd
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
     
    /**
     * 获取当前日期时间
     * 
     * @param format
     * @return
     */
    public static String getDateTime(String format){
        Date dt = new Date();
        return formatDate(dt, format);
    }
     
    /**
     * 格式化日期时间为指定格式的字符串
     * 
     * @param format
     * @return
     */
    public static String formatDate(Date dt, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }
    
    /**
     * <h5>功能描述:</h5>	将字符串日期格式化yyyyMMddHHmmss
     *
     * @param dateStr 
     * @return
     *
     * @作者：zhangpj		@创建时间：2017年3月23日
     */
    public static String formatDate(String dateStr){
    	 SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
         try {  
             Date date = df.parse(dateStr);   
             return df.format(date);
         } catch (Exception ex) {  
             System.out.println(ex.getMessage());  
         }
         return null;
    }
    
    /**
     * 解析字符串为Date
     * 
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr, String format) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateStr);
    }
     
    /**
     * 日期时间比较，若d1大于d2，则返回true
     * 
     * @param d1
     * @param d2
     * @return
     */
    public static Boolean dateCompare(Date d1, Date d2){
        if(d1.getTime() > d2.getTime()){
            return true;
        } 
        return false;
    }
     
    /**
     * 日期时间比较，若d1大于d2，则返回true
     * 
     * @param d1
     * @param d2
     * @param format
     * @return
     * @throws ParseException 
     */
    public static Boolean dateCompare(String d1, String d2, String format) throws ParseException{
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Date date1 = sf.parse(d1);
        Date date2 = sf.parse(d2);
        return dateCompare(date1, date2);
    }
     
    /**
     * 计算当前时间后延一段时间之后的时间点
     * 
     * @param minute
     *          单位为分钟
     * @param format
     * @return
     */
    public static String calDateStr(int minute, String format){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(new Date(date.getTime()+minute*60*1000));//初始化时间为当前系统时间+1
    } 
     
    /**
     * 检查日期字符串是否符合格式定义
     * 
     * @param dateStr
     * @param format
     * @return
     */
    public static Boolean checkPattern(String dateStr, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            formatter.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
     
    /**
     * 取得指定日期以后若干天的日期。如果要得到以前的日期，参数用负数。
     * 
     * @param date 基准日期
     * @param days 增加的日期数
     * @return 增加以后的日期
     */
    public static Date addDays(Date date, int days) {
        return add(date, days, Calendar.DATE);
    }
    
    public static String addDays(int days) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
         
        //过去七天
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        Date d = c.getTime();
        String day = format.format(d);
        System.out.println("过去七天："+day);
        return day;
    }
 
    /**
     * 取得指定日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 注意，可能不是同一日子，例如2003-1-31加上一个月是2003-2-28
     * 
     * @param date 基准日期
     * @param months 增加的月份数
     * @return 增加以后的日期
     */
    public static Date addMonths(Date date, int months) {
        return add(date, months, Calendar.MONTH);
    }
 
    /**
     * @param date 基准日期
     * @param years 增加的年数
     * @return 增加以后的日期
     */
    public static Date addYears(Date date, int years){
        return add(date, years, Calendar.YEAR);
    }
     
    /**
     * 内部方法。为指定日期增加相应的天数或月数
     * 
     * @param date 基准日期
     * @param amount 增加的数量
     * @param field 增加的单位，年，月或者日
     * @return 增加以后的日期
     */
    private static Date add(Date date, int amount, int field) {
        Calendar calendar = Calendar.getInstance();
 
        calendar.setTime(date);
        calendar.add(field, amount);
 
        return calendar.getTime();
    }
 
    /**
     * 计算两个日期相差天数。 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数
     * 
     * @param one 第一个日期数，作为基准
     * @param two 第二个日期数，作为比较
     * @return 两个日期相差天数
     */
    public static Long diffDays(Date one, Date two) {
        return (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000);
    }
 
    /**
     * 计算两个日期相差月份数 如果前一个日期小于后一个日期，则返回负数
     * 
     * @param one 第一个日期数，作为基准
     * @param two 第二个日期数，作为比较
     * @return 两个日期相差月份数
     */
    public static Integer diffMonths(Date one, Date two) {
 
        Calendar calendar = Calendar.getInstance();
 
        // 得到第一个日期的年分和月份数
        calendar.setTime(one);
        int yearOne = calendar.get(Calendar.YEAR);
        int monthOne = calendar.get(Calendar.MONDAY);
 
        // 得到第二个日期的年份和月份
        calendar.setTime(two);
        int yearTwo = calendar.get(Calendar.YEAR);
        int monthTwo = calendar.get(Calendar.MONDAY);
 
        return (yearOne - yearTwo) * 12 + (monthOne - monthTwo);
    }
 
    /**
     * 将一个字符串用给定的格式转换为日期类型。<br>
     * 注意：如果返回null，则表示解析失败
     * 
     * @param datestr 需要解析的日期字符串
     * @param pattern 日期字符串的格式，默认为“yyyyMMdd”的形式
     * @return 解析后的日期
     */
    public static Date parse(String datestr, String pattern) {
        Date date = null;
 
        if (null == pattern || "".equals(pattern)) {
            pattern = DEFAULT_DATE_FORMAT;
        }
 
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            //
        }
 
        return date;
    }
 
    /**
     * 返回给定日期中的月份中的最后一天
     * 
     * @param date 基准日期
     * @return 该月最后一天的日期
     */
    public static Date getMonthLastDay(Date date) {
 
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
 
        // 将日期设置为下一月第一天
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
 
        // 减去1天，得到的即本月的最后一天
        calendar.add(Calendar.DATE, -1);
 
        return calendar.getTime();
    }
     
    /**
     * 此方法计算时间毫秒 
     * 
     * @param inVal
     * @param inVal2
     * @return
     * @throws Exception
     */
    public static Long getDateDiff(Long inVal,Long inVal2) throws Exception { 
        Date date = null; //定义时间类型 
        Date date1 = null; //定义时间类型
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd"); 
        try { 
            date = inputFormat.parse(inVal.toString()); //将字符型转换成日期型 
            date1=inputFormat.parse(inVal2.toString()); //将字符型转换成日期型
            return (Long)(date.getTime()-date1.getTime())/1000/3600/24; //返回天数 
        } catch (Exception e) { 
            throw e;
        }
    }
     
    /**
     * 计算两个时间的相差毫秒数
     * 
     * @param d1
     * @param d2
     * @return
     * @throws ParseException
     */
	  public static long secondDiff(long d1, long d2) throws ParseException{
	      SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
	      Date date1 = sdf.parse(String.valueOf(d1));
	      Date date2 = sdf.parse(String.valueOf(d2));
	      
	      return date1.getTime()-date2.getTime()/1000;
	      
	  }
     
	  /**
	   * 获取订单的timestamp
		 * @author lks 2016年12月8日上午10:19:58
		 * @param date
		 * @return
		 */
	public static Timestamp getOrderTimestamp(String date){
		  Timestamp timeStamp = null;
		  try {
			  timeStamp = new Timestamp(parseDate(date,"yyyyMMddHHmmssSSS").getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return timeStamp;
	  }
	  
	
	public static void main(String[] args) {
			String ss = formatDate("2013-01-12 12:05:36");
			System.out.println(ss);
			System.out.println(addDays(-7));
	}
}
