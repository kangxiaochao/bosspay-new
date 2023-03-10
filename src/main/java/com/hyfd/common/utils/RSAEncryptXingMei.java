package com.hyfd.common.utils;                                                                                          
                                                                                                                            
import java.io.BufferedReader;                                                                                              
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;                                                                                                 
import java.io.InputStream;                                                                                                 
import java.io.InputStreamReader;                                                                                           
import java.security.InvalidKeyException;                                                                                   
import java.security.KeyFactory;                                                                                                                                                                            
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;                                                                                                                                                                      
import java.security.interfaces.RSAPrivateKey;                                                                              
import java.security.interfaces.RSAPublicKey;                                                                               
import java.security.spec.InvalidKeySpecException;                                                                          
import java.security.spec.PKCS8EncodedKeySpec;                                                                              
import java.security.spec.X509EncodedKeySpec;                                                                               
import javax.crypto.BadPaddingException;                                                                                    
import javax.crypto.Cipher;                                                                                                 
import javax.crypto.IllegalBlockSizeException;                                                                              
import javax.crypto.NoSuchPaddingException;                                                                                 
import org.bouncycastle.jce.provider.BouncyCastleProvider;                                                                  
import sun.misc.BASE64Decoder;                                                                                              
                                                                                                                            
public class RSAEncryptXingMei {
	
	private static final String privatePath = "c:\\rsa_private_key.pem";
	
	private static final String publicPath = "c:\\rsa_public_key.pem";
	
//	private static final String privatePath = "\\pem\\2\\rsa_private_key.pem";
//	
//	private static final String publicPath = "\\pem\\1\\rsa_public_key.pem";
                                                                                                                                                                                                                                                                                                                                                 
    /**                                                                                                                     
     * ??????                                                                                                                 
     */                                                                                                                     
    private RSAPrivateKey privateKey;                                                                                       
                                                                                                                            
    /**                                                                                                                     
     * ??????                                                                                                                 
     */                                                                                                                     
    private RSAPublicKey publicKey;                                                                                         
                                                                                                                            
    /**                                                                                                                     
     * ????????????????????????????????????                                                                                             
     */                                                                                                                     
    private static final char[] HEX_CHAR= {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}; 
                                                                                                                            
                                                                                                                            
    /**                                                                                                                     
     * ????????????                                                                                                             
     * @return ?????????????????????                                                                                               
     */                                                                                                                     
    public RSAPrivateKey getPrivateKey() {                                                                                  
        return privateKey;                                                                                                  
    }                                                                                                                       
                                                                                                                            
    /**                                                                                                                     
     * ????????????                                                                                                             
     * @return ?????????????????????                                                                                               
     */                                                                                                                     
    public RSAPublicKey getPublicKey() {                                                                                    
        return publicKey;                                                                                                   
    }                                                                                                                                                                                                                                                                                                                                                                     
                                                                                                                            
   /**                                                                                                                      
    * ????????????????????????????????????                                                                                              
    * @param in ???????????????                                                                                                  
    * @throws Exception ??????????????????????????????                                                                                
    */                                                                                                                      
   public void loadPublicKey(InputStream in) throws Exception{                                                              
       try {                                                                                                                
           BufferedReader br= new BufferedReader(new InputStreamReader(in));                                                
           String readLine= null;                                                                                           
           StringBuilder sb= new StringBuilder();                                                                           
           while((readLine= br.readLine())!=null){                                                                          
               if(readLine.charAt(0)=='-'){                                                                                 
                   continue;                                                                                                
               }else{                                                                                                       
                   sb.append(readLine);                                                                                     
                   sb.append('\r');                                                                                         
               }                                                                                                            
           }                                                                                                                
           loadPublicKey(sb.toString());                                                                                    
       } catch (IOException e) {                                                                                            
           throw new Exception("???????????????????????????");                                                                       
       } catch (NullPointerException e) {                                                                                   
           throw new Exception("?????????????????????");                                                                           
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
                                                                                                                            
   /**                                                                                                                      
    * ???????????????????????????                                                                                                    
    * @param publicKeyStr ?????????????????????                                                                                    
    * @throws Exception ??????????????????????????????                                                                                
    */                                                                                                                      
   public void loadPublicKey(String publicKeyStr) throws Exception{                                                         
       try {                                                                                                                
           BASE64Decoder base64Decoder= new BASE64Decoder();                                                                
           byte[] buffer= base64Decoder.decodeBuffer(publicKeyStr);                                                         
           KeyFactory keyFactory= KeyFactory.getInstance("RSA");                                                            
           X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);                                                      
           this.publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);                                               
       } catch (NoSuchAlgorithmException e) {                                                                               
           throw new Exception("????????????");                                                                                 
       } catch (InvalidKeySpecException e) {                                                                                
           throw new Exception("????????????");                                                                                 
       } catch (IOException e) {                                                                                            
           throw new Exception("??????????????????????????????");                                                                     
       } catch (NullPointerException e) {                                                                                   
           throw new Exception("??????????????????");                                                                             
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
   /**                                                                                                                      
    * ????????????????????????                                                                                                      
    * @param keyFileName ???????????????                                                                                         
    * @return ????????????                                                                                                      
    * @throws Exception                                                                                                     
    */                                                                                                                      
   public void loadPrivateKey(InputStream in) throws Exception{                                                             
       try {                                                                                                                
           BufferedReader br= new BufferedReader(new InputStreamReader(in));                                                
           String readLine= null;                                                                                           
           StringBuilder sb= new StringBuilder();                                                                           
           while((readLine= br.readLine())!=null){                                                                          
               if(readLine.charAt(0)=='-'){                                                                                 
                   continue;                                                                                                
               }else{                                                                                                       
                   sb.append(readLine);                                                                                     
                   sb.append('\r');                                                                                         
               }                                                                                                            
           }                                                                                                                
           loadPrivateKey(sb.toString());                                                                                   
       } catch (IOException e) {                                                                                            
           throw new Exception("????????????????????????");                                                                         
       } catch (NullPointerException e) {                                                                                   
           throw new Exception("?????????????????????");                                                                           
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
   public void loadPrivateKey(String privateKeyStr) throws Exception{                                                       
       try {                                                                                                                
           BASE64Decoder base64Decoder= new BASE64Decoder();                                                                
           byte[] buffer= base64Decoder.decodeBuffer(privateKeyStr);                                                        
           PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);                                                    
           KeyFactory keyFactory= KeyFactory.getInstance("RSA");                                                            
           this.privateKey= (RSAPrivateKey) keyFactory.generatePrivate(keySpec);                                            
       } catch (NoSuchAlgorithmException e) {                                                                               
           throw new Exception("????????????");                                                                                 
       } catch (InvalidKeySpecException e) {                                                                                
           throw new Exception("????????????");                                                                                 
       } catch (IOException e) {                                                                                            
           throw new Exception("??????????????????????????????");                                                                     
       } catch (NullPointerException e) {                                                                                   
           throw new Exception("??????????????????");                                                                             
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
   /**                                                                                                                      
    * ????????????                                                                                                              
    * @param privateKey ??????                                                                                                 
    * @param plainTextData ????????????                                                                                         
    * @return                                                                                                               
    * @throws Exception ??????????????????????????????                                                                                
    */                                                                                                                      
   public byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws Exception{                                    
       if(privateKey== null){                                                                                                
           throw new Exception("??????????????????, ?????????");                                                                     
       }                                                                                                                    
       Cipher cipher= null;                                                                                                 
       try {                                                                                                                
           cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());                                                   
           cipher.init(Cipher.ENCRYPT_MODE, privateKey);                                                                     
           byte[] output= cipher.doFinal(plainTextData);                                                                    
           return output;                                                                                                   
       } catch (NoSuchAlgorithmException e) {                                                                               
           throw new Exception("??????????????????");                                                                             
       } catch (NoSuchPaddingException e) {                                                                                 
           e.printStackTrace();                                                                                             
           return null;                                                                                                     
       }catch (InvalidKeyException e) {                                                                                     
           throw new Exception("??????????????????,?????????");                                                                      
       } catch (IllegalBlockSizeException e) {                                                                              
           throw new Exception("??????????????????");                                                                             
       } catch (BadPaddingException e) {                                                                                    
           throw new Exception("?????????????????????");                                                                           
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
   /**                                                                                                                      
    * ????????????                                                                                                              
    * @param publicKey ??????                                                                                                
    * @param cipherData ????????????                                                                                            
    * @return ??????                                                                                                          
    * @throws Exception ??????????????????????????????                                                                                
    */                                                                                                                      
   public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception{                                     
       if (publicKey== null){                                                                                              
           throw new Exception("??????????????????, ?????????");                                                                     
       }                                                                                                                    
       Cipher cipher= null;                                                                                                 
       try {                                                                                                                
           cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());                                                   
           cipher.init(Cipher.DECRYPT_MODE, publicKey);                                                                    
           byte[] output= cipher.doFinal(cipherData);                                                                       
           return output;                                                                                                   
       } catch (NoSuchAlgorithmException e) {                                                                               
           throw new Exception("??????????????????");                                                                             
       } catch (NoSuchPaddingException e) {                                                                                 
           e.printStackTrace();                                                                                             
           return null;                                                                                                     
       }catch (InvalidKeyException e) {                                                                                     
           throw new Exception("??????????????????,?????????");                                                                      
       } catch (IllegalBlockSizeException e) {                                                                              
           throw new Exception("??????????????????");                                                                             
       } catch (BadPaddingException e) {                                                                                    
           throw new Exception("?????????????????????");                                                                           
       }                                                                                                                    
   }                                                                                                                        
                                                                                                                            
                                                                                                                            
   /**                                                                                                                      
    * ????????????????????????????????????                                                                                              
    * @param data ????????????                                                                                                  
    * @return ??????????????????                                                                                                  
    */                                                                                                                      
   public static String byteArrayToString(byte[] data){                                                                     
       StringBuilder stringBuilder= new StringBuilder();                                                                    
       for (int i=0; i<data.length; i++){                                                                                   
           //???????????????????????? ???????????????????????????????????????????????? ?????????????????????                                               
           stringBuilder.append(HEX_CHAR[(data[i] & 0xf0)>>> 4]);                                                           
           //???????????????????????? ????????????????????????????????????????????????                                                              
           stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);                                                                
           if (i<data.length-1){                                                                                            
               stringBuilder.append(' ');                                                                                   
           }                                                                                                                
       }                                                                                                                    
       return stringBuilder.toString();                                                                                     
   }
   
   public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
   
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2; 
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));  
		}
		return result;
	}
	
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	/**
	 * ????????????
	 */
	public String enc(String code){
		RSAEncryptXingMei rsaEncrypt= new RSAEncryptXingMei();
		String encryptValue = null;
		try {
//			String relativelyPath=System.getProperty("user.dir"); 
			File file = new File(privatePath);
			FileInputStream fis = new FileInputStream(file);
			rsaEncrypt.loadPrivateKey(fis);
			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), code.getBytes());
			encryptValue = byte2hex(cipher);
		} catch (Exception e) {
			System.err.println(e.getMessage());                                                                              
	        System.err.println("??????????????????"); 
		}
		return encryptValue;
	}
    
	/**
	 * ????????????
	 */
	public String dec(String signatureInfo){
		RSAEncryptXingMei rsaEncrypt= new RSAEncryptXingMei();
		String Text = null;
		try {
			String relativelyPath=System.getProperty("user.dir"); 
			File file = new File(relativelyPath+publicPath);
			FileInputStream fis = new FileInputStream(file);
			rsaEncrypt.loadPublicKey(fis);
			byte[] decCipher = hexStringToByte(signatureInfo);
			byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), decCipher);
			Text =  new String(RSAEncryptXingMei.byte2hex(plainText));
			//Text =  new String(plainText);
		} catch (Exception e) {
			System.err.println(e.getMessage());                                                                              
	        System.err.println("??????????????????"); 
		}
		return Text;
	}
	
	/** 
	 * MD5??????
	 * @param val ??????
	 * @return ??????
	 */
	public String MD5(String val){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		md5.update(val.getBytes());
 		String code = RSAEncryptXingMei.byte2hex(md5.digest());
 		return code;
	}
	
   public static void main(String[] args){                                                                                  
		RSAEncryptXingMei rsaEncrypt = new RSAEncryptXingMei();
		//????????????  
		try {
			File file = new File("D:/work/SaleWeb/pem/2/rsa_private_key.pem");
			FileInputStream fis = new FileInputStream(file);
			rsaEncrypt.loadPrivateKey(fis);// RSAEncrypt.class.getResourceAsStream("/configuration.properties"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       //????????????                                                                                                           
       try {
    	   File file = new File("D:/work/SaleWeb/pem/2/rsa_public_key.pem");
		   FileInputStream fis = new FileInputStream(file);
           rsaEncrypt.loadPublicKey(fis);                                                         
           System.out.println("??????????????????");                                                                              
       } catch (Exception e) {                                                                                              
           System.err.println(e.getMessage());                                                                              
           System.err.println("??????????????????");                                                                              
       }                                                                                                                    
                                                                                                                                                                                                                                                
       //???????????????                                                                                                         
        String encryptStr= "{\"SOO\":[{\"CUST_LOGIN\":{\"EXT_SYSTEM\":\"111111\",\"LOGIN_NBR\":\"18108611556\",\"LOGIN_TYPE\":\"1\",\"PWD\":\"123456\",\"SEL_IN_ORG_ID\":\"01\"},\"PUB_REQ\":{\"TYPE\":\"Example\"}}]}";                                                                        
                                                                                                                            
        try { 
        	String code = rsaEncrypt.MD5(encryptStr);
    		System.out.println("MD5?????????"+code);
            //??????                                                                                                          
            byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), code.getBytes()); 
            System.out.println("???????????????"+RSAEncryptXingMei.byte2hex(cipher));
            
            //??????
    		//String dec =  "C3C1B2C330325EE1FBE7C938B7D563BB1E2BCC2287658C9B79BDC656188DD4836FB84CA7157837E6726B7D3375AAF9A04FD391FB790F7A33ED5F738709BECAD388BE8E60EA3F3A454B78C3D8C92B499A9711E0F14F8FD85BD08F7335986C04F779B62B19B28B744B2D676F66D7A3BBB720F1FFA1E4AB968CFADD5A293FA48483";
    		String dec =  "1BC4A127E12A10E6563408330C119E2138E419311229086100147CAD8B9171477BBB11010F86352318F09EF55750DEB3EA8886DE090DE3A81A7D1B2F786814A04AFD81EA2B1C8588F44F3ED2CC06BA02079FDB497FCC29CB05838EC568666F6CE692102D8B17E4722EC0289203B2D7B169F35D51DE83F0F689E6B42CC1F054A3";
    		byte[] decCipher = hexStringToByte(dec);
            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), decCipher);                                                                                                     
            System.out.println("???????????????"+new String(plainText));   
        } catch (Exception e) {                                                                                             
            System.err.println(e.getMessage());                                                                             
        }                                                                                                                   
    }
}