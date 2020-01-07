package com.hyfd.common.utils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class PublicRSA
{
    
    private static String strPubKey =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRZ53/G+iZ69Ye6pPM0HSR2D3DdQwzOFnYcYe2Xug6DdWQlOBx/PDvLGzJYqx/Uz6x/E7A9gZXZHFVD3t0gmT+6JttPiK6NrvbxOJsIuJfRHLmngSe2WJq3evBy2wOasmJjPIL/EjSOTujt9vadnnA/CuFSsz8ymTLsMFb3XfTUQIDAQAB";
    
    private static RSAPublicKey publicKey = getPublicKey(strPubKey);
    
    private static PublicRSA instance = null;
    
    private PublicRSA()
    {
        
    }
    
    public static PublicRSA getInstance()
    {
        if (instance == null)
        {
            instance = new PublicRSA();
        }
        return instance;
    }
    
    /**
     * 公钥加密
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public String encryptByPublicKey(String data)
        throws Exception
    {
        
        data = java.net.URLEncoder.encode(data, "UTF-8");
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //
        int key_len = publicKey.getModulus().bitLength() / 8;
        //
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //
        for (String s : datas)
        {
            mi += (new BASE64Encoder()).encode(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }
    
    /**
     * 公钥解密
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public String decryptByPublicKey(String data)
        throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        //
        int key_len = publicKey.getModulus().bitLength() / 8;
        byte[] base64 = (new BASE64Decoder()).decodeBuffer(data);
        String ming = "";
        byte[][] arrays = splitArray(base64, key_len);
        for (byte[] arr : arrays)
        {
            ming += new String(cipher.doFinal(arr));
        }
        ming = java.net.URLDecoder.decode(ming, "UTF-8");
        return ming;
    }
    
    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len)
    {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0)
        {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++)
        {
            if (i == x + z - 1 && y != 0)
            {
                str = string.substring(i * len, i * len + y);
            }
            else
            {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }
    
    /**
     * 拆分byte数组
     */
    public static byte[][] splitArray(byte[] data, int len)
    {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0)
        {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++)
        {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0)
            {
                System.arraycopy(data, i * len, arr, 0, y);
            }
            else
            {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }
    
    public static RSAPublicKey getPublicKey(String key)
    {
        try
        {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
            return publicKey;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static RSAPrivateKey getPrivateKey(String key)
    {
        try
        {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(key);
            
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
            return privateKey;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
