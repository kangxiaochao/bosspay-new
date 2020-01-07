package com.hyfd.common.utils;

/** 
 * @ClassName: XiChengAESUtils 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author CXJ 
 * @date 2017年6月20日 上午9:37:07 
 * @version 1.0  
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class XiChengAESUtils
{
    static Logger log = Logger.getLogger(XiChengAESUtils.class);
    
    public final static String DEFAULT_CHARSET = "UTF-8";
    
    /**
     * AES加密
     *
     * @param content
     * @param password
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String password)
        throws Exception
    {
        String secretMd5 = XiChengMD5Utils.strToMd5(password, DEFAULT_CHARSET);
        password = XiChengMD5Utils.strToMd5(password + secretMd5, DEFAULT_CHARSET);
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        byte[] byteContent = content.getBytes(DEFAULT_CHARSET);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(byteContent);
        System.out.println(byteContent.length);
        if ((result != null) && (result.length > 0))
        {
            return new String(Base64.encodeBase64(result));
        }
        return null;
    }
    
    /**
     * AES解密
     * 
     * @param content
     * @param password
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String password)
        throws Exception
    {
        String secretMd5 = XiChengMD5Utils.strToMd5(password, DEFAULT_CHARSET);
        password = XiChengMD5Utils.strToMd5(password + secretMd5, DEFAULT_CHARSET);
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        System.out.println(Base64.decodeBase64(content).length);
        byte[] result = cipher.doFinal(Base64.decodeBase64(content));
        if ((result != null) && (result.length > 0))
        {
            log.debug("[AESUtils][decrypt][result.length]:" + result.length);
            log.debug("[AESUtils][decrypt][result]:" + new String(result, DEFAULT_CHARSET));
            return new String(result, DEFAULT_CHARSET);
        }
        else
        {
            log.debug("[AESUtils][decrypt][result] is null");
            return null;
        }
    }
    
}