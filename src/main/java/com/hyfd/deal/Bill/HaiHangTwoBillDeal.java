package com.hyfd.deal.Bill;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.RSAEncrypt;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 *  海航任意充
 * @author Administrator
 *
 */
public class HaiHangTwoBillDeal implements BaseDeal{
	static Logger log = Logger.getLogger(HaiHangTwoBillDeal.class);
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
	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMhZIxG+wUfLb/TTvbEerukKDHUZaU5JNCSlRR3LWYaSoJjWev7DM2VqGUbVhKJ10ByC2oOPrn+uwS2fKmj3W6GMWZ2I1ceIaphNUtDuuNz7SeNVm/Ryc5Zt0a7bycjPw49htG1qacwaTZOwxRzbuGEgh+27zXDMmckB/ZXcQwnvAgMBAAECgYAZS//8lpxrB0nsnOu/uIMXU7h89KdyRhX6Zo/SkkGMl2kFw1cmYdlUfdARPJaRuNR9NRyILhU3dAZaT1WYiVsdUpmPLEWN9uxdQMzlNmHUNBpi2Zz0ZlDY9fGeeQOybkrHhPGZtq/HYP01Ip2GqsDkaz0kJZtxQJOdO9hYPudAOQJBAOe0/QvyYqu5tt41b6XMQvi0pOGVRpGLFBI1tixpsoMDL4vILHT6Ucrj73ZGKc5Dfw6FQwPWNBjk3PfIIDLbj5UCQQDdWnqgcVrDELdysuVgGCxPqJyX205GtnnrKvptyNr+qT1ZlcvyosyrpbdqJg9Z0lZLgQ/Lzw4Mj4aNw5w7YOJzAkBCYuexeZPtsbsENXk0nq59hXxMKbHbV2a7xOnaFbqQsOP5f7PFHEc9A0uDrpyhvVL0/RdzTPcGPuRUleEr0R51AkEA07iAREhIFP4/SXE9nl1gvMy9Ay8bvt043iwcnreYaC6+ZJq4GZuIAsIeHmTLGBHswolu73Vbrb32aFBqdg0kawJBAMR6ZnAnzk6Zvgkb0vhIMnyauJp3RmPHv/+obth0B8RReHxcZ6bDwsKAaZ21HulzK+2DGBTYLSo8PzMNRSmaRFY=";
	private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/wEdz/b3WOVOLJSWv47+Svj2YUQNy5AL7/r6278zWwBWJa6hp1JAExNQNacT8KDC69bJvzndqqMXZzQpJptGFZtVveX1A/dd/c/rkb7oFQY/o73WdqTXNUSUx8ZsJaqq4DzuKZ5Pc5FQRcDn3fHYyzuiSiYzRCPVcj1iONlDd/wIDAQAB";
	
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try {
			String DestinationId =  order.get("phone")+"";								//手机号
			String RechargeAmount =  order.get("fee")+"";				//充值金额，以元为单位，可以充值小数
			String RequestId  = "R12345611"+ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS")+(int)((Math.random()*9+1)*1000);	//请求流水
			map.put("orderId", RequestId);
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");	//获取通道参数
			String defaultParameter = channel.get("default_parameter")+"";				//默认参数
			String url = channel.get("link_url").toString();							//获取充值地址
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
//			String publicKey = paramMap.get("publicKey");								//公钥
			//提交充值申请
			int i = 0;
			String resultStr = recharge(i,paramMap,DestinationId,RechargeAmount,RequestId,url);
			log.info("number:["+DestinationId+"]requestHaiHang：submit successfully！[" + resultStr + "]");
			if(resultStr != null && !resultStr.equals("")) {
				 Map<String,String> returnXml = XmlUtils.readXmlToMap(resultStr.trim());
				 String bodyStr = returnXml.get("Body");
				 //取出返回的json数据body，并使其按照json字符串的顺序排序
//				 LinkedHashMap<String, Object> json = JSON.parseObject(bodyStr,LinkedHashMap.class, Feature.OrderedField);
//				 JSONObject bodyJson=new JSONObject(true);
//				 bodyJson.putAll(json);
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
			log.error("hai Hang Error in recharging"+e.getMessage()+"|||"+MapUtils.toString(order));
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
	public String recharge(int i ,Map<String,String> paramMap,String DestinationId,String rechargeAmount,String RequestId,String url) {
		i++;
		String ret = "";
		try {
			String  PayAcct =  paramMap.get("payAcct");									//代理商账号
			//String  Pwd =  MD5Util.MD5Encode(paramMap.get("pwd"),"UTF-8");				//代理商密码  MD5加密码后小写转16进制
			String  Pwd =  paramMap.get("pwd");											//代理商密码  MD5加密码后小写转16进制
			String  RequestSource = paramMap.get("requestSource");						//自服务平台标识或IP地址
			String  RequestUser = paramMap.get("requestUser");							//用户如果登录填写登录用户名
			String  TransactionID = paramMap.get("transactionID");						//交易编码
			String  SYS_ID = paramMap.get("sys_Id");									//用于区分哪个系统
			String  DestinationAttr = paramMap.get("destinationAttr");					//被充值用户属性
			String  DestinationAttrDetail  = paramMap.get("destinationAttrDetail");		//被充值用户属性明细
			String  ObjType= paramMap.get("objType");									//号码类型，1：帐户；2：客户；3：用户；
			String  BalanceType = paramMap.get("balanceType");							//账本类型，1表示可退，2表示不可退，８０表示充值卡，不传默认1   
			String  RechargeUnit = paramMap.get("rechargeUnit");						//充值金额单位：0－元；1－条;
			//充值类型，0快充，1卡充，2表示支付宝快充，3表示业务办理快充，4表示业务办理支付宝快充，5表示业务办理POS刷卡，10表示号码销售一次费  默认值 0
			String  RechargeType = paramMap.get("rechargeType");
			String  ServiceBus = paramMap.get("serviceBus");
			String  StaffId = paramMap.get("staffId");									//代理商工号，  传代理商账号
			String  DiscountFee = paramMap.get("discountFee");							//减免金额     默认值 0
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
			return recharge(i,paramMap,DestinationId,rechargeAmount,RequestId,url);
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
}
