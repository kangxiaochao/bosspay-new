package com.hyfd.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * Title: �ַ����
 * </p>
 * <p>
 * Description: �ַ����
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: SI-TECH
 * </p>
 * 
 * @author zhangli
 * @version 1.0
 * 
 */

public class StringUtil
{
    
    public static boolean empty(String param)
    {
        return param == null || param.trim().length() < 1;
    }
    
    public static String objToString(Object param)
    {
        return param == null ? "" : String.valueOf(param);
    }
    
    /*
     * 填充字符串，根据参数的长度，如果原字符串长度不够，左边填充默认字符，返回固定长度的字符串
     */
    public static String fillstr(String src, int len, char defaultchar)
    {
        int srclen = src.length();
        StringBuffer targetstr = new StringBuffer(src);
        for (int i = srclen; i < len; i++)
        {
            targetstr.insert(0, defaultchar);
        }
        return targetstr.toString();
    }
    
    public static String parseObject(Object obj)
    {
        if (obj == null)
        {
            return "";
        }
        else
        {
            return obj.toString();
        }
    }
    
    public static Long parseLong(String s)
    {
        if (empty(s))
        {
            return 0l;
        }
        else
        {
            return new Long(s);
        }
    }
    
    public static Long parseLong2(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        else
        {
            return new Long(obj.toString());
        }
    }
    
    public static Date parseDate(Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        else
        {
            return (Date)obj;
        }
    }
    
    /**
     * 字符串按字节截取 （中文2字节）
     * 
     * @param str 字符串
     * @param n 字节长度
     * @return 按照长度截取字符串长度
     */
    public static List<String> splitString(String str, int n)
    {
        List<String> strList = new ArrayList<String>();
        Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]$");
        int end = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++)
        {
            String c = str.substring(i, i + 1);
            
            Matcher m = p.matcher(c);
            int tmp = m.find() ? 2 : 1;
            
            // int tmp = 0;
            // try {
            // tmp = c.getBytes("GBK").length;
            // } catch (UnsupportedEncodingException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            if (n == (end + tmp))
            { // 如果当前长度等于所需截取长度，则截取到当前字符
                sb.append(c);
                strList.add(sb.toString());
                sb = new StringBuffer();
                end = 0;
            }
            else if (n < (end + tmp))
            { // 如果当前长度大于所需截取长度，则只截取到前一个字符
                strList.add(sb.toString());
                sb = new StringBuffer();
                sb.append(c);
                end = tmp;
            }
            else
            { // 如果当前长度小于所需截取长度，则把字符添加进sb
                sb.append(c);
                end = end + tmp;
            }
        }
        
        if (!StringUtil.empty(sb.toString()))
        { // 判断是否还有未加入List的字符串
            strList.add(sb.toString());
        }
        
        return strList;
    }
    
    public static int getZhLength(Object obj)
    {
        return parseObject(obj).replaceAll("[\u4e00-\u9fa5]", "**").length();
    }
    
    /**
     * 分转换为元
     * 
     * @param fen
     * @return
     */
    public static String fen2Yuan(String fen)
    {
        float yuan = Float.parseFloat(fen) / 100;
        String str = String.valueOf(yuan);
        return str.replaceAll("(\\.(0)+)$", "");
    }
    
    /**
     * 元转换为分
     * 
     * @param yuan
     * @return
     */
    public static String yuan2Fen(String yuan)
    {
        float fen = Float.parseFloat(yuan) * 100;
        String str = String.valueOf(fen);
        return str.replaceAll("(\\.(0)+)$", "");
    }
    
    /**
     * 判断一个字符串是否由阿拉伯数字构成
     * 
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)
    {
        boolean ret = false;
        if (str != null)
        {
            Pattern pattern = Pattern.compile("^[0-9]+$");
            ret = pattern.matcher(str.toLowerCase()).matches();
        }
        return ret;
    }
    
    public static String getSourceFromRequestUrl(String sourceUrl)
    {
        String sourceSign = "";
        int start = sourceUrl.indexOf("?");
        int end = sourceUrl.lastIndexOf("&");
        sourceSign = sourceUrl.substring(start, end);
        return sourceSign;
    }
    
    public static String sortOrginReqStr(String orginReqStr)
    {
        String[] strArray = orginReqStr.split("&");
        Arrays.sort(strArray);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strArray.length; i++)
        {
            sb.append(strArray[i]);
            if (i < strArray.length - 1)
            {
                sb.append("&");
            }
        }
        return sb.toString();
    }
    
    public static boolean isEmpty(String str)
    {
        if (str == null || "".equals(str))
        {
            return true;
        }
        return false;
    }
    
    public static String getNewColumnValueForSql(String s)
    {
        if (null != s && !"".equals(s))
        {
            s = s.replace("'", "\\'");
        }
        return s;
    }
    
    /**
     * <h5>功能描述:</h5> String[]转List<String>
     *
     * @param str
     * @return
     *
     * @作者：zhangpj @创建时间：2017年3月8日
     */
    public static List<String> arrayToList(String[] str)
    {
        int strLength = str.length;
        List<String> list = null;
        if (null != str && strLength > 0)
        {
            list = new ArrayList<String>();
            for (int i = 0; i < str.length; i++)
            {
                list.add(str[i]);
            }
        }
        return list;
    }
    
    public static String replaceBlank(String str)
    {
        String dest = "";
        if (str != null)
        {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
