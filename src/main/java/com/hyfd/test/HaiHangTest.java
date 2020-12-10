package com.hyfd.test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.RSAEncrypt;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;

public class HaiHangTest {
		static Logger log = Logger.getLogger(HaiHangTest.class);
		private static Map<String,String> productIdMap = new HashMap<String,String>();
		static{
			productIdMap.put("0", "成功");
			productIdMap.put("100", "输入参数缺失");
			productIdMap.put("101", "输入参数格式错误");
			productIdMap.put("201", "查询用户资料失败");
			productIdMap.put("403", "流水号已充值");
			productIdMap.put("203", "查询代理商无记录");
			productIdMap.put("209", "代理商账号密码错误");
			productIdMap.put("501", "代理商余额不足");
			productIdMap.put("507", "充值超时");
			productIdMap.put("700", "系统异常");
		}
		private static String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAO0bdgTszL4erwUJ6koI6ZXUu7WcVuUNMMJPiIAuWqeMhmXRaVvEy8ji/nLG8Fyihuf9jhnphj+QCIMtDXNwd1hHkzBZaNHUm4UUAs7JeDLIb7KblYymeDUuU7Z6HbgQzQQp8GQxut9zWoFeMJrDHkoT6PUHdLgCpzQjeXINz+wFAgMBAAECgYAUdKgL6LoJNPB4iNBzrq0oRgAjvOcqP6cTPHVUSat8hP0bT6O9TQ8ft6WoFU2rUEI/RXq69ZClbzGsz3g3m15QTuPxtPET9lOYZNkAqFqkV8Cou+M1LiKeoETEHrBvAY7tmyrX3+azSfOolGFJ3VEfBxVkoLcpnsyM3ZTJhT4oCQJBAP6N3K8mfjlFc9m7D7muscBqxmhbEpI5A4Kxxu0r6l2GELTDow/8gTThXzyrZaTatKWGatGac9x/wDZHDPlCBJ8CQQDudDrnQgOVMs8C+m84kZmN0I9D+9rSbBM0IIO5SqbxZq7nKeZoK273nbGRokPCwiodI0E3w4WIIJX4K3Z3LwjbAkEA4jQyTbsJI9+iCXP/j2O8NV8tHQMBR8s+YV+VhKEnzSAbrGbFeqlKMDGuLf2pf4uSSB1gww5bszAbIKCW2NFfxQJBAM2XZwXm0+H6maOOuwBCAOvVywApkORmDDSgaBIE/GUcTGbAqIheEmHqE6RmDBhKgjb3I47pxCK3PbZn+s8NvYsCQQD82wyPbJR46wBZVIZE9st8kMNEV4QboLSdBEhRTwX/2MnHqww+i8afrqLgZBZ0TSYedk/IpRh+wEEF1RYgABmm";
		private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvmqzP9+NU6vBtyV/+KWw4o7z9gUblWEnJ9ZAOYE53owknrN1R7oIVcr7cqrXyWaLiIFnHsNEMz5LXvJDkgJm9iqM9lUFwJ30wrolfv5315w0lkGrIOVNaKUALj3gWimgFE+LBxqpRdkdHoN7gSXnS2PWDi0E7P7iHZgll/NDX9wIDAQAB";
		
		public static Map<String, Object> deal() {
			// TODO Auto-generated method stub
			Map<String,Object> map = new HashMap<String,Object>();
			int flag = -1;
			try {
				String DestinationId =  "17705305254";								//手机号
				String RechargeAmount =  "10";				//充值金额，以元为单位，可以充值小数
				String RequestId  = "R12345611"+ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS")+(int)((Math.random()*9+1)*1000);	//请求流水
				map.put("orderId", RequestId);
			
				String url = "http://www.10044.cn:8020/esb/Recharge002?token=bdb01bcd-281d-4506-a5f8-bcb92ae85a12";							//获取充值地址
//				String publicKey = paramMap.get("publicKey");								//公钥
				//提交充值申请
				int i = 0;
				String resultStr = recharge(i,null,DestinationId,RechargeAmount,RequestId,url);
				log.info("number:["+DestinationId+"]requestHaiHang：submit successfully！[" + resultStr + "]");
				if(resultStr != null && !resultStr.equals("")) {
					 Map<String,String> returnXml = XmlUtils.readXmlToMap(resultStr.trim());
					 String bodyStr = returnXml.get("Body");
					 //取出返回的json数据body，并使其按照json字符串的顺序排序
//					 LinkedHashMap<String, Object> json = JSON.parseObject(bodyStr,LinkedHashMap.class, Feature.OrderedField);
//					 JSONObject bodyJson=new JSONObject(true);
//					 bodyJson.putAll(json);
					 JSONObject bodyJson = JSONObject.parseObject(bodyStr);
					 //验签
					 //boolean verifySignature = verifySignature(bodyJson,publicKey);
					 String  resultJson = bodyJson.getJSONObject("SvcCont").getJSONArray("SOO").get(0)+"";
					 JSONObject  jsonObject = JSONObject.parseObject(resultJson);
					 String Result = jsonObject.getJSONObject("RESP").getString("Result");
					 String ResponseId = jsonObject.getJSONObject("RESP").getString("ResponseId");
					 String resultCode = "";
					 map.put("providerOrderId", ResponseId);
					// if(verifySignature) {
						 if("0".equals(Result)) {   //为0时表示充值成功
							 resultCode = productIdMap.get(Result);
							 flag = 3;				
							 log.debug("number:["+DestinationId+"]requestHaiHang：submit successfully！");
						 }else {
							 resultCode = productIdMap.get(Result);
							 flag = 4;
							 log.info("number["+DestinationId+"]requestHaiHang：submit failure!result="+Result+":"+resultCode);
						 }
						 map.put("resultCode", Result+":"+resultCode);
					/* }else {
						 flag = 4;
						 map.put("resultCode","签名验证不匹配");
						 log.debug("number:["+DestinationId+"] Signature verification does not match");
					 }*/
				}
			} catch (Exception e) {
				log.error("hai Hang Error in recharging"+e.getMessage()+"|||");
			}
			map.put("status", flag);
			return map;
		}
		/**
		 * 下单处理
		 * @param paramMap
		 * @param DestinationId	被充值的手机号
		 * @param rechargeAmount
		 * @param RequestId
		 * @param url
		 * @return
		 */
		public static String recharge(int i ,Map<String,String> paramMap,String DestinationId,String rechargeAmount,String RequestId,String url) {
			i++;
			String ret = "";
			try {
				String  PayAcct =  "A108851422211";									//代理商账号
				//String  Pwd =  MD5Util.MD5Encode(paramMap.get("pwd"),"UTF-8");			//代理商密码  MD5加密码后小写转16进制
				String  Pwd =  "7946e795e44db67be4c74219def41e2e";											//代理商密码  MD5加密码后小写转16进制
				String  RequestSource = "1";						//自服务平台标识或IP地址
				String  RequestUser = "SU081";							//用户如果登录填写登录用户名
				String  TransactionID = "bdb01bcd-281d-4506-a5f8-bcb92ae85a12";						//交易编码
				String  SYS_ID = "126";									//用于区分哪个系统
				String  DestinationAttr = "0";					//被充值用户属性
				String  DestinationAttrDetail  = "0";		//被充值用户属性明细
				String  ObjType= "3";									//号码类型，1：帐户；2：客户；3：用户；
				String  BalanceType = "1";							//账本类型，1表示可退，2表示不可退，８０表示充值卡，不传默认1   
				String  RechargeUnit = "0";						//充值金额单位：0－元；1－条;
				//充值类型，0快充，1卡充，2表示支付宝快充，3表示业务办理快充，4表示业务办理支付宝快充，5表示业务办理POS刷卡，10表示号码销售一次费  默认值 0
				String  RechargeType = "0";
				String  ServiceBus = "/ServiceBus/custView/cust/Recharge002";
				String  StaffId = "227268";									//代理商工号，  传代理商账号
				String  DiscountFee = "0";							//减免金额     默认值 0
				String  RequestTime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");		//请求时间，YYYYMMDDHHMMSS
				//生成签名
				 String S = "{\"SOO\": [{\"RechargeReq\": {"
						 	+"\"PayAcct\":\""+PayAcct+"\",\"Pwd\": \""+Pwd+"\",\"RequestSource\": \""+RequestSource+"\",\"RequestUser\": \""+RequestUser+"\","
						 	+"\"RequestId\":\""+RequestId+"\",\"RequestTime\": \""+RequestTime+"\",\"DestinationId\": \""+DestinationId+"\","
						 	+"\"DestinationAttr\":\""+DestinationAttr+"\",\"DestinationAttrDetail\": \""+DestinationAttrDetail+"\",\"ObjType\": \""+ObjType+"\","
						 	+"\"BalanceType\":\""+BalanceType+"\",\"RechargeUnit\": \""+RechargeUnit+"\",\"RechargeAmount\": \""+rechargeAmount+"\","
						 	+"\"RechargeType\": \""+RechargeType+"\",\"StaffId\": \""+StaffId+"\",\"DiscountFee\": \""+DiscountFee+"\""
						 	+"}}]}";
				 //将签名转成json格式
				 JSONObject json = JSONObject.parseObject(S);
				 String sign = generateSignature(json.toJSONString(),privateKey).toUpperCase();
				 if(sign != null  && !sign.equals("")) {
					 String xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" "
							 	+"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"http://www.tydic.com/\">"
							 	+"<SOAP-ENV:Header/><SOAP-ENV:Body><Business>{"
							 	+"\"TcpCont\":{\"TransactionID\":\""+TransactionID+"\",\"ReqTime\":\""+RequestTime+"\"," 
							 	+"\"SignatureInfo\":\""+sign+"\",\"ServiceCode\":\""+ServiceBus+"\",\"SYS_ID\":\""+SYS_ID+"\"},"
							 	+"\"SvcCont\":"+json.toJSONString()+"}</Business></SOAP-ENV:Body></SOAP-ENV:Envelope>";
					 ret = ToolHttp.post(false,url, xml.toString(), "text/xml");
				 }
				 log.error("ret:"+ret);
			}catch (Exception e) {
				// TODO: handle exception
			}
			if((ret!= null && !ret.equals("")) || i > 2){
				return ret;
			}else{
				return recharge(i,null,DestinationId,rechargeAmount,RequestId,url);
			}
		}
		
		/**
		 * 获取签名  采用私钥加密
		 * @param svcCont
		 * @return
		 */
		public static String generateSignature(String  svcCont,String privateKey) {
			 String signature = "";
			 try {
					//对报文摘要用MD5进行加密
					String contMd5 = MD5(svcCont);
					//对MD5加密后的摘要用 密钥 进行加密得到数据签名
					byte[] signatureBytes = RSAUtils.encryptByPrivateKey(contMd5.getBytes(),privateKey);
					signature = byte2hex(signatureBytes);
					return signature;
				} catch (Exception e) {
			    	e.printStackTrace();                                                                              
			        log.error("privateKeyError" + ExceptionUtils.getExceptionMessage(e));
				}
			 return signature;
		 }
		
		public static boolean verifySignature(JSONObject reqJsonObj,String publicKey) {
			try {
				RSAEncrypt rsaPriEncrypt = new RSAEncrypt();
				rsaPriEncrypt.loadPublicKey(publicKey);
				JSONObject svcContObj = reqJsonObj.getJSONObject("SvcCont");
				//对返回的参数进行MD5加密 与 返回的SignatureInfo进行对比， 避免信息被篡改
				String contMd5 = rsaPriEncrypt.MD5(svcContObj.toString());
				JSONObject tcpContObj = reqJsonObj.getJSONObject("TcpCont");
				String signatureInfo = tcpContObj.getString("SignatureInfo");
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
				cipher.init(Cipher.DECRYPT_MODE,rsaPriEncrypt.getPublicKey());
				byte[] output = cipher.doFinal(rsaPriEncrypt.hexStringToByte(signatureInfo));
				//使用公钥进行解密后获的是上家对参数进行MD5加密的结果
				String contMd51 = new String(output);
				//检证两者是否相等 
				if (contMd5.equals(contMd51)) {  
					return true;
				}
			} catch (Exception e) {
				log.error("publicKeyError" + ExceptionUtils.getExceptionMessage(e));
			}
			return false;
		}
		
		/**
		 * 使用MD5加密，转十六进制字符串
		 * @param val
		 * @return
		 */
		 public static String MD5(String val) {
				MessageDigest md5 = null;
				try {
					md5 = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				md5.update(val.getBytes());
				String code = byte2hex(md5.digest());
				return code;
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
		 /**
		  * 数据库中pwd存放的是加密后的密码
		  * @param args
		  */
		 public static void main(String[] args) {
//			 Map<String, Object> order = new HashMap<>();
//			System.out.println(deal());
//			
			 try {
				System.out.println(MD5Util.MD5Encode("Wn123120","UTF-8"));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

