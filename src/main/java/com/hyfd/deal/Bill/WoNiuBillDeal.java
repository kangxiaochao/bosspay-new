package com.hyfd.deal.Bill;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class WoNiuBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			String spec = Double.parseDouble(fee) * 100 + "";// 充值金额，以分为单位
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = (String) channel.get("link_url");// 充值地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String timeStamp = DateTimeUtils.formatDate(new Date(),"yyyyMMddhhmmss");
			String customerOrderId = timeStamp + GenerateData.getIntData(9, 6)+ "01";// 32位订单号
			String sAgentPlatyformId = paramMap.get("sAgentPlatyformId");//"106";// 直充代理商ID
			String secretKey = paramMap.get("secretKey");//"123456789";
			String sAgentPass = paramMap.get("password");//"25F9E794323B453885F5181F1B624D0B";
			String sAgentOrderId = customerOrderId;// 代理商方的订单号，代理商方需保证其值唯一。（每笔订单由唯一的数字或字符串表示一个订单号，发送给游戏蜗牛）
			
			//String getCardTypeIdUrl = "http://222.92.116.36/webservice/imprest/36_106.xml";
			String sCardTypeId = paramMap.get("sCardTypeId");// 通过getCartTypeId方法获取，因为变动不大，也可以写成定值 3011
			String sUserName = StrToHex(phone);// 充值帐号 必须经过StrToHex转码
														// 发送时需进行StrToHex转换（见转换说明）
			String sAreaID = "0";// 免卡话费产品ID,固定传0
			String amount = spec;
			String sGameID = "36";//免卡话费产品ID,固定 36
			String sImprestType = "0";
			String sPlayformIp = paramMap.get("ipAddress");//用户ip TODO 如何获取
			String createResult = sendCreate(linkUrl, sAgentPlatyformId, sAgentPass, sAgentOrderId, sCardTypeId, amount, sUserName, sImprestType, sAreaID, sPlayformIp, secretKey);
			System.out.println("充值结果：" + createResult);
			
			Document doc = DocumentHelper.parseText(createResult);
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element returnEle = rootElt.element("return");
			String result = returnEle.attributeValue("value");
			
			if ("1".equals(result)) {
				flag = 1;
			} else {
				flag = 0;
			}
			
		} catch (Exception e) {
			log.error("蜗牛充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 获取3位序列码
	 * 
	 * @return
	 */
	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100);
	}
	
	/**
	 * 
	 * @param checkUrl
	 * @param sAgentPlatyformId 直充代理商ID 
	 * @param sAgentPass 直充代理商密码（MD5后转大写）
	 * @param sAgentOrderId代理商方的订单号，代理商方需保证其值唯一。（每笔订单由唯一的数字或字符串表示一个订单号，发送给游戏蜗牛）
	 * @param sCardTypeId 卡类型ID  [数字型]（用户所选择的包含在xml里面的卡类型ID，对应字段为Card_Type_ID）
	 * @param amount 此值传用户需要充入的金额，正整数且>=1,比如用户需要充30元，此值传30
	 * @param totalValue充移动话费时和amount一致
	 * @param sUsername充值手机号 发送时需进行StrToHex转换（见转换说明）（用户在游戏蜗牛的通行证帐号，即登录蜗牛游戏的帐号）
	 * @param sImprestType 免卡话费默认使用0
	 * @param sAreaID 免卡话费默认使用0
	 * @param sImprestAccountIP 充值用户ip
	 * @param sVerifyStr
	 * @return
	 */
	public static String sendCreate(String url, String sAgentPlatyformId, String sAgentPass, String sAgentOrderId, String sCardTypeId, String amount, String sUsername, String sImprestType, String sAreaID, String sImprestAccountIP, String secretKey) {
//		System.out.println("正式的：");
//		System.out.println(sAgentPlatyformId);
//		System.out.println(sAgentPass);
//		System.out.println(sAgentOrderId);
//		System.out.println(sCardTypeId);
//		System.out.println(amount);
//		System.out.println(sUsername);
//		System.out.println(sImprestType);
//		System.out.println(sAreaID);
//		System.out.println(sImprestAccountIP);
//		System.out.println(secretKey);
		try {

			String md5Src = sAgentPlatyformId + sAgentPass + sAgentOrderId + sCardTypeId + amount + amount + sUsername + sImprestType + sAreaID + sImprestAccountIP + secretKey;
			String sVerifyStr = DigestUtils.md5Hex(md5Src).toUpperCase();
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);
			call.setOperationName("completeImprestAmount");// WSDL里面描述的接口名称
			// 设置参数名 // 参数名 参数类型:String 参数模式：'IN' or 'OUT'
			call.addParameter("sAgentPlatyformId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentPass", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentOrderId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sCardTypeId", XMLType.XSD_INTEGER, ParameterMode.IN);
			call.addParameter("Amount", XMLType.XSD_INTEGER, ParameterMode.IN);
			call.addParameter("totalValue", XMLType.XSD_INTEGER, ParameterMode.IN);
			call.addParameter("sUserName", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sImprestType", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAreaID", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sImprestAccountIP", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sVerifyStr", XMLType.XSD_STRING, ParameterMode.IN);
			// 设置返回值类型

			call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
			String result = (String) call.invoke(new Object[] { sAgentPlatyformId, sAgentPass, sAgentOrderId, sCardTypeId, amount, amount, sUsername, sImprestType, sAreaID, sImprestAccountIP, sVerifyStr });// 远程调用
			return result;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	}
	
	
	public static String sendSearch(String url, String sAgentPlatyformId, String sAgentPass, String sAgentOrderId, String sImprestAccountIP, String secretKey) {
		try {
			String md5Src = sAgentPlatyformId + sAgentPass + sAgentOrderId + sImprestAccountIP + secretKey;
			String sVerifyStr = DigestUtils.md5Hex(md5Src);
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);
			call.setOperationName("querySnailOrderID");// WSDL里面描述的接口名称
			// 设置参数名 // 参数名 参数类型:String 参数模式：'IN' or 'OUT'
			call.addParameter("sAgentPlatyformId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentPass", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentOrderId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sImprestAccountIP", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sVerifyStr", XMLType.XSD_STRING, ParameterMode.IN);
			// 设置返回值类型
			call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
			String result = (String) call.invoke(new Object[] { sAgentPlatyformId, sAgentPass, sAgentOrderId, sImprestAccountIP, sVerifyStr });// 远程调用

			return result;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	}
	
	public static String sendCheck(String checkUrl, String sAgentPlatyformId, String sAgentPass, String sAgentOrderId, String sCardTypeId, String sUsername, String sGameID, String sAreaID, String secretKey) {
		try {
			String md5CheckSrc = sAgentPlatyformId + sAgentPass + sAgentOrderId + sCardTypeId + sUsername + sGameID + sAreaID + secretKey;
			String sVerifyStr = DigestUtils.md5Hex(md5CheckSrc).toUpperCase();
			System.out.println(md5CheckSrc);
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(checkUrl);
			call.setOperationName("authImprestAndQueryPresents");// WSDL里面描述的接口名称
			// 设置参数名 // 参数名 参数类型:String 参数模式：'IN' or 'OUT'
			call.addParameter("sAgentPlatyformId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentPass", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAgentOrderId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sCardTypeId", XMLType.XSD_INTEGER, ParameterMode.IN);
			call.addParameter("sUsername", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sGameID", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sAreaID", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("sVerifyStr", XMLType.XSD_STRING, ParameterMode.IN);
			// 设置返回值类型
			call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String

			String result = (String) call.invoke(new Object[] { sAgentPlatyformId, sAgentPass, sAgentOrderId, sCardTypeId, sUsername, sGameID, sAreaID, sVerifyStr });// 远程调用

			return result;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail";
	}

	/**
	 * 获取5位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	public static String StrToHex(String HexStr) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			byte[] b = HexStr.getBytes("GB2312");
			for (int i = 0; i < b.length; i++) {
				String stmp = Integer.toHexString(b[i] & 0xFF);
				if (stmp.length() == 1) {
					stringBuffer.append("0");
					stringBuffer.append(stmp);
				} else {
					stringBuffer.append(stmp);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString().toUpperCase();
	}
public static String getCartTypeId (String url) throws ClientProtocolException, IOException {
		
		String xml = ToolHttp.get(false, url);
		
		Document doc = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			String cardTypeId = rootElt.element("UnitPrice").element("GameCardType").element("Card_Type_ID").getStringValue();
			System.out.println(cardTypeId);
			return cardTypeId;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getGameId (String url) throws ClientProtocolException, IOException {
		String xml = ToolHttp.get(false, url);
		
		Document doc = null;
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			String gameId = rootElt.element("GameInfo").element("Game_ID").getStringValue();
			System.out.println(gameId);
			return gameId;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
