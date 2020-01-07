package com.hyfd.common.utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

public class SignatureUtil {
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception{
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("terminalName", "zhenzhen");
        paramMap.put("customerOrderId", "terminal100000000000000000012342");
        paramMap.put("phoneNo", "17070299999");
        paramMap.put("orderType", "2");
        paramMap.put("spec", "3000");
        paramMap.put("scope", "nation");
        paramMap.put("callbackUrl", "http://www.baidu.com");
        paramMap.put("timeStamp", "20160712123051000");
   
        String signature = sign(paramMap);
        System.out.println("signature="+signature);
        System.out.println(verify(paramMap,signature));
    }

    
    /**
     * @功能描述：	校验签名
     *
     * @作者：zhangjun	@创建时间：2018年1月9日
     * @param paramMap
     * @param signature
     * @return
     */
    public static boolean verify(Map<String, String> paramMap,String signature) {
        String md5 = md5Param(paramMap);
        boolean flag = false;
        if (md5.equals(signature)) {
        	flag = true;
		}
        return flag;
    }
    
    /**
     * @功能描述：	签名
     *
     * @作者：zhangjun	@创建时间：2018年1月9日
     * @param paramMap
     * @return
     */
    public static String sign(Map<String, String> paramMap){
        String md5 = md5Param(paramMap);
        return md5;
    }
    
    /**
     * @功能描述：	签名
     *
     * @作者：zhangjun	@创建时间：2018年1月9日
     * @param paramStr
     * @return
     */
    public static String sign(String paramStr){
        String md5 = md5(paramStr);
        return md5;
    }
    
    public static String md5Param(Map<String, String> paramMap) {
        // 转换
        List<String> paramNames = new ArrayList<String>();
        for (String paramKey : paramMap.keySet()) {
            paramNames.add(paramKey);
        }

        // 排序
        Collections.sort(paramNames);

        // 拼接请求参数字符串
        StringBuilder paramUrl = new StringBuilder();
        for (String paramName : paramNames) {
            paramUrl.append(paramName).append("=").append(paramMap.get(paramName)).append("&");
        }
        paramUrl.deleteCharAt(paramUrl.length() - 1);

        String content = paramUrl.toString();
        System.err.println(content);
        return md5(content);
    }
    
    /**
     * MD5加密
     *
     * @param s
     * @return
     */
    public final static String md5(String s) {
        try {
            byte[] btInput = s.getBytes("UTF-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return Hex.encodeHexString(md);
        } catch (Exception e) {
            return null;
        }
    }
}
