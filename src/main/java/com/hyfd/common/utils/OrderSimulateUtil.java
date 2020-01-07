package com.hyfd.common.utils;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

/**
 * @功能描述：	警告,此类禁止删除,千米供货平台应用此类进行了模拟发送请求
 *
 * @作者：zhangpj		@创建时间：2018年1月15日
 */
public class OrderSimulateUtil {
	private static Logger log = Logger.getLogger(OrderSimulateUtil.class);
	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception{
//    	verifySignTest();	// 签名验证
    	quotaOrderTest();	// 话费/流量充值测试
    }
    
    /**
     * <h5>功能:</h5>签名验证
     * @throws Exception 
     *
     * @author zhangpj	@date 2016年9月9日
     */
    private static void verifySignTest() throws Exception{
    	Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("terminalName", "7d1bce1e829e4331b87bfe2b1ef1416e");
		paramMap.put("customerOrderId", "9477b6340c68c4b79dbeea6eb1ca83d1");
		paramMap.put("phoneNo", "13911277252");
//		paramMap.put("orderType", "2");	// 话费
//		paramMap.put("spec", "1000");	// 话费单位:分
		paramMap.put("orderType", "1");	// 流量
		paramMap.put("spec", "10");		// 流量单位:MB
		paramMap.put("scope", "nation");
		paramMap.put("callbackUrl", "http://139.129.110.162/flowpro/ifgw/sdjnhbaa/sync.do");
		paramMap.put("timeStamp", "20170714094705212");
		  
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIgFZ2sabNZqylPNZ3c4TmpPAbe0hMBRJjg1HsSOIEDfwipJ7D5lvWRzOzYuBcuHBeJ+la97sBdgu8oAqGC/AMvol4W3L4tHryLStLTImh/e3l7/SoloMfgXuASOAGl0lvJPg72Qz7/uXAORgAew7D8bXcGiX3eyP5Yg7eHZFEMHAgMBAAECgYAW4EHKY4FWdlNc0LjL9i5J9sulJ7kD1yIEZfqst/J4tqkjD5epztaRZEfbDtZuqmC0/PETuEufQueSS5YF31m0spunmB5XjNy0DbsJVUFX2Y1QmemwhhBdluNe8kw+H9EMP43m3aRojSbWsBN5NxMOJDRRk2MyehDbK7Ka0w3jEQJBAMdoDLzVxnIg6x1bhFrITy1rMVOLXDCk7AStuEx7z/utx1bBfAhyy81fxCud3vdqp9UnbOu0eEgDZLn6kaUdh5MCQQCuoBGgU7zc459hYiE7cwa+PmedeqYjgl8CbzJX1YKR4APkIWGMj4k0jlg6lf5M/J2QA7/LIrWGrfkWu2szKFc9AkAj2gJIRUs7o82B5r1iMReDO/2PXsQBfvVsVeuH3M7lGjZ306vAPRuFXk1/5g97fnKUcVU0/6qMkAehJHrhK/D/AkBsgqLxUZeGWxtq1CN6YZvLE9QnE1OxtQ184cQSuGYOZ3mDhQqs4XSM5/cDULlY9ZCjMPE5NTQ/oFOsQIH4pBSxAkEAgAARS/llN6ErfiaKftJ1J0TCucXmg4XWVAcSz0osxwtViPpxncORPxBdPLRmdOXqiVlKVXoL+WiZF8CphQvIkw==";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIBWdrGmzWaspTzWd3OE5qTwG3tITAUSY4NR7EjiBA38IqSew+Zb1kczs2LgXLhwXifpWve7AXYLvKAKhgvwDL6JeFty+LR68i0rS0yJof3t5e/0qJaDH4F7gEjgBpdJbyT4O9kM+/7lwDkYAHsOw/G13Bol93sj+WIO3h2RRDBwIDAQAB";

		String signature = sign(paramMap, privateKey);
		System.err.println("\r签名signature[" + signature + "]\r");
		System.out.println("签名验证结果[" + verify(paramMap, signature, publicKey)+ "]");
    }
    
    /**
     * <h5>功能:</h5> 话费/流量充值测试
     *
     * @author zhangpj	@date 2016年9月9日
     */
    private static void quotaOrderTest(){
    	String quotaOrderUrl = "http://118.31.229.23:8081/bosspaybill/order/quotaOrder"; // 充值地址
    	// 加密密钥
//    	String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJjzsyQzfaNB9mcEY/FxfA6B0i+/jLapzZBG7MtT7PMefCiVMK1PxquUTnDoCQ6TvL6b2aB3mTji2zwLTeMRHRfpJj25lonjeagYpWYKRn6dyzK3fVR75erOqIhlzLbxuTOguv1FSR+h9TH2AueRCsqDMnTcDAL6vWy1PxRmBYJLAgMBAAECgYAk2BegMdMi/6HGgRU+NRMoP3eNTvwriE3fYmZqOGZMzlwkPoQCS8RU0p3ursZ3v3jpBYvwDr9XJ7aeCS8S8q1qFKDnSIJzB7gqPW3rJc/ntnILbRLQJ/GThh1koQ5/RPQ8UWdwksQ27r4Hk8U0YMcMPiqkVe7pRVsPaIFBlLjwQQJBANFLJMkIGqEKYUiWecL3C6+Gm5ctNyOC6wtY026eVBtvEJRGeAXQKX0pI8iMuNwf6exF+osHsUStMtC/jDQBZuECQQC7Fci6sQv3i+5DsO0omLx2yxY9lCcYTlD1+Jlwnqz8JT3LYoFJtUoYEXhpOH6vBjNiYtyblvS9wrOuS0AqMQqrAkBHgYvs30bx1HEOctLhCLTGYJASI5GeIWGau9tpBNEpmlWowzbIjalPKNof7+xM0N6EKhiCnbs0ApTE+1y0e/IhAkAerczT6JKYzoaM7SeG370QEe+cu33Ju5YFL/YhhLPDSWwfvRCMgMEcv0iqoIwNsYudl66riQ6rS5FhhI+KG66vAkEAgJY+tWzGzNv6PEEVkaLS8CpKpFTwsHiGhX4qqq+aAQ9wEvjodCFdlWwA60iUrmjX6niLBzvsq6a0oXNlsnSHeg==";
    	String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJp1kaDAm4e/Og5UvWaW+G4LeLWzSvInZTfrP3f4uWfcXBXLAeXi+tmxRMNDAuLIFvU07nXlNVtIqkSDk2BpjGUQAe9ZgQIGtG7w3oxW93eHkjFWC0lFRRu0m0uYXGjb7KBfDeiUyIdaMq7Xn4Fof/bVgDtiaiVK9XWKnyZ9I9tFAgMBAAECgYBThNEK2DsBKuCMkTuctnN8pY1OyrTyvWSY2QaHq6EtTNUqH4xWLuavQZBJ+PkdgU4Qpt9uNlMkmeUfiPnuvwPfZi9ZYXJ4gzvQdO8iumqxcj3Bg0UN/igz1NBNg8hxvAiRQqsBdxdmE3l87O8KjtoNGbtSfvsYK6DB3l+mXaRv/QJBANWnxxaiHCmqYjHL6Or4XCpd5BDPbUf5OWgJval67I2Di2uQfKX6IK3edDePrW3714qo6sa5WfhWA4DsYPefnicCQQC5Elw2CrKlk9vVekcO0wxUy0Pvzg0iJSQrD/HXdepISEK8Vt2eG2E2cTk+DYjR57TlxeVdSN/n+zsrhIvoR0qzAkEAua8X4r6+1SNENdzur49rolJ5THbJlfyufJHCqNGxVofVmoU3H5TT4+fqfwM6idSPDe4iJNWfsKkoYFqPJ4YsjwJATMeeM9lqViEqgb1R/4d1RTRzAej1L+ZCxA7AYYgM42H/Nc2/8ZkgUorUmbbZvWOoe+8HAhV6tu5WlO5PcNUq2wJAOhUUgKrCo19Gmm71ACpMCh9GIAR9qHsfSj0Vwbm+r4AXRPQcJdwai8CZnNxTwOXq+MTS3jz+eMNWjEm6hwj+yA==";
    	
    	// 组合请求参数
    	Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("terminalName", "newweixin");
		paramMap.put("customerOrderId", "XPTHF20171229093838511157140");
		paramMap.put("phoneNo", "17091452586");
		paramMap.put("orderType", "2");	// 话费
		paramMap.put("spec", "3000");	// 话费单位:分
//		paramMap.put("orderType", "1");	// 流量
//		paramMap.put("spec", "10");		// 流量单位:MB
		paramMap.put("scope", "nation");
		paramMap.put("callbackUrl", "http://120.26.134.145/rcmp/jf/orderDeal/statusBackBossBillPay");
		paramMap.put("timeStamp", "20171229102301685");
		
		String data = quotaOrder(quotaOrderUrl, paramMap, privateKey);
		System.err.println("请求返回数据["+data+"]");
    }
    
    /**
     * 校验
     * @param paramMap
     * @param signature
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static boolean verify(Map<String, String> paramMap,String signature,String publicKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(publicKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        
        // 设置请求参数排列顺序
        String content = setParamStr(paramMap);
        // MD5加密
        String md5 = md5Hex(content);
        
        byte[] rsa = decodeBytePublic(Hex.decodeHex(signature.toCharArray()),publicKey);
        return new String(rsa).equals(md5);
    }
    
    private static String rsaDecrypt(String signature,String publicKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(publicKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        
        
        byte[] rsa = decodeBytePublic(Hex.decodeHex(signature.toCharArray()),publicKey);
        return new String(rsa);
    }
    /**
     * 签名
     * @param paramMap
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    private static String sign(Map<String, String> paramMap,String privateKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        
        // 设置请求参数排列顺序
        String content = setParamStr(paramMap);
        System.out.println("加密参数顺序如下["+content+"]");
        // MD5加密
        String md5 = md5Hex(content);
        
        byte[] rsa = encodeBytePrivate(md5.getBytes(),privateKey);
        return Hex.encodeHexString(rsa);
    }
    
    /**
     * 签名
     * @param String paramStr
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    private static String sign(String paramStr, String privateKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        
        String md5 = md5Hex(paramStr);
        
        byte[] rsa = encodeBytePrivate(md5.getBytes(),privateKey);
        return Hex.encodeHexString(rsa);
    }
    
    private static String rsaEncrypt(String md5,String privateKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        
        
        byte[] rsa = encodeBytePrivate(md5.getBytes(),privateKey);
        return Hex.encodeHexString(rsa);
    }
    
    /**
     * <h5>功能:</h5>设置请求参数排列顺序
     * @param paramMap
     * @return 
     *
     * @author zhangpj	@date 2016年9月9日
     */
    private static String setParamStr(Map<String, String> paramMap) {
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
        return content;
    }
    
    /**
     * MD5加密
     *
     * @param s
     * @return
     */
    private final static String md5Hex(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
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
    
    
    /**
     * RSA
     * 
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    private static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    /**
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    private static byte[] decodeBytePublic(byte[] content, PublicKey publicKey) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }
    
    /**
     * @功能描述：	测试充值
     *
     * @param quotaOrderUrl 测试充值地址
     * @param paramMap 充值请求参数组合
     * @param privateKey 加密私钥
     * @return
     *
     * @作者：zhangpj		@创建时间：2016年11月22日
     */
    private static String quotaOrder(String quotaOrderUrl,Map<String, String> paramMap,String privateKey){
    	String data = null;
    	try {
    		String param = setParamStr(paramMap);	// 组合请求参数
    		String signature = sign(paramMap,privateKey);	// 计算加密密文
    		StringBuffer quotaOrderRequestStr = new StringBuffer();	// 组合请求报文
    		quotaOrderRequestStr.append(quotaOrderUrl);
    		quotaOrderRequestStr.append("?");
    		quotaOrderRequestStr.append(param);
    		quotaOrderRequestStr.append("&signature=");
    		quotaOrderRequestStr.append(signature);
    		
    		System.out.println("签名signature["+signature+"]\r");
    		data = ToolHttp.get(false, quotaOrderRequestStr.toString());
//	        System.err.println("请求返回数据["+data+"]");
    	} catch (Exception e) {
    		data = null;
    		log.error("客户订单号["+paramMap.get("customerOrderId").toString()+"]发起充值请求发生异常");
    	}
    	return data;
    }
    
    /**
     * @功能描述：	生产环境充值请求(仅用于系统自动抓取订单充值使用,严禁进行测试充值等其他用途)
     *
     * @作者：zhangpj		@创建时间：2016年11月22日
     * @param paramMap 充值请求参数组合
     * @param privateKey 加密私钥
     * @return
     */
    public static String quotaOrder(Map<String, String> paramMap,String privateKey){
//    	String quotaOrderUrl = "http://www.sdhyfd.com:40001/rcmp/jf/api/app/quotaOrder"; // 充值地址
    	String quotaOrderUrl = "http://127.0.0.1:8080/bosspaybill/order/quotaOrder"; // 充值地址
    	String data = null;
		try {
			String param = setParamStr(paramMap);	// 组合请求参数
			String signature = sign(paramMap,privateKey);	// 计算加密密文
			StringBuffer quotaOrderRequestStr = new StringBuffer();	// 组合请求报文
			quotaOrderRequestStr.append(quotaOrderUrl);
			quotaOrderRequestStr.append("?");
			quotaOrderRequestStr.append(param);
			quotaOrderRequestStr.append("&signature=");
			quotaOrderRequestStr.append(signature);
			
			System.out.println("签名signature["+signature+"]\r");
	        data = ToolHttp.get(false, quotaOrderRequestStr.toString());
	        System.err.println("请求返回数据["+data+"]");
		} catch (Exception e) {
			data = null;
			log.error("客户订单号["+paramMap.get("customerOrderId").toString()+"]发起充值请求发生异常");
		}
    	return data;
    }
}
