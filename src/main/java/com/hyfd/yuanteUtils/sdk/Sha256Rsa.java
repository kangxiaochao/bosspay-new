package com.hyfd.yuanteUtils.sdk;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * RSA签名验签类
 */
public class Sha256Rsa{	
	 /** 
     * 以行为单位读取文件，常用于读面向行的格式化文件 
     *  
     * @param fileName 
     *            文件名 
     */  
    public static String readFileByLines(String fileName) {  
        File file = new File(fileName);  
        String content = new String();
        BufferedReader reader = null;  
        try {  
            System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
            	content +=tempString;
            }  	
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        
        return content;
    }  
    
	public static void main(String args[])
	{
		/*
		 * 私钥格式的证书为pkcs8
		 * linux 下生成证书步骤
		 * 1、私钥
		 * openssl genrsa -out private.pem 1024
		 * 2、公钥
		 * openssl rsa -in private.pem  -pubout -out public.pem
		 * 3、转换成pkcs8格式证书步骤
		 * 
		 */
		String content = "12345667890";
		//从文件读取公钥和私钥
/*		String privateKey = readFileByLines("X:/private.pkcs8");
		privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----","");
		privateKey = privateKey.replace("-----END PRIVATE KEY-----","");
		String publickey = readFileByLines("X:/public.pem");
		publickey = publickey.replace("-----BEGIN PUBLIC KEY-----","");
		publickey = publickey.replace("-----END PUBLIC KEY-----","");*/

		//该用例可用
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKhAkP+z3QKhxrvn8HRmUA80o+gz7mcMOmZiVBZWw1CIr98Xeq5T0KyF43BHwlKqU247o7XEoY2RiVczWmDempA/48XRBVSfaV39h+M7hqcrYHGwwkTlXP/zOwd7dIXtZ91JshVed6zsDznB2CKU4S/e6lEmFejyTeDM363kYEPpAgMBAAECgYAbnm2u69GNywyab9py4RCnTlbRmSS7WbCEegOfnyJ0p9tm3a3PzAZnK5tIJR9q/navk66t+dcjLA7Fd7KQUDpCy10XSPF/W5gEwrOY2Q3h85i/2mcF7VdM9q0l4P0mVVITIRsGwkdQrZK4BTnQngJOQUnIN84iN4pLTRZ+6VcBGQJBANssTSoMIofRVQR/eXgjSyQy2UMixfAAnpQ6mi6O0d4rbwULE2UJ+RM8UzttbG3dGbkfYuILS/ArvUzvJzL/EHsCQQDEhel+Q2KcS+0dMdROrIhliY6k88Y+Snpz8JDA5z4RCOzJ3Cv3qnSTTjjoswIdPM97gYHcI4DvwCAu2McKtnnrAkBaWvcs4HfT2j7cbdkb6CDwOW5MOSe0++xkW4x4qTRoDSvlvl0uiGAAyBvgIgpTeJVENmZDnjieOMn/z5Ave+vFAkEAtYJBYB74ywbNX7OQNYHhEHb2Sp/kr8+2PbMo3yI1DyLBIMe7zCmhNhR/N11uTHbH43h/6kTYZ5d5ogsR+3ECNQJBAKRH0KUcpTMFr3BK6m8/JYeL6u9Khw9IYPYr8h6nUnWdEGuwQ8bSUqLAJs4DzZdSvM+BPE2kmn22dj4PdWhWAuw=";
		String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoQJD/s90Coca75/B0ZlAPNKPoM+5nDDpmYlQWVsNQiK/fF3quU9CsheNwR8JSqlNuO6O1xKGNkYlXM1pg3pqQP+PF0QVUn2ld/YfjO4anK2BxsMJE5Vz/8zsHe3SF7WfdSbIVXnes7A85wdgilOEv3upRJhXo8k3gzN+t5GBD6QIDAQAB";
		System.out.println(privateKey);
		System.out.println(publickey);
		TencentSignature signature = new TencentSignature();
		try
		{
			String sign = signature.rsa256Sign(content, privateKey, TencentConstants.CHARSET_GBK);
			System.out.println(sign);
			boolean result = signature.rsa256CheckContent(content,sign,publickey, TencentConstants.CHARSET_GBK);
			System.out.println(result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
