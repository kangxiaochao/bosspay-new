package com.hyfd.service.query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.RSAEncrypt;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class HaiHangQuerySer extends BaseService {

	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	@Autowired
	OrderDao orderDao;

	@Autowired
	PhoneSectionDao phoneSectionDao;

	@Autowired
	ProviderDao providerDao;

	/**
	 * 获取余额信息 传入手机号
	 * 
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap key:amount 余额 key:status 查询状态
	 *         0查询成功 1查询失败
	 */
	public Map<String, String> getChargeAmountInfo(String mobileNumber) {

		return HaiHService(mobileNumber.replace(" ", ""));
	}

	public Map<String, String> HaiHService(String phoneDh) {

		Map<String,String> returnMap = new HashMap<>();
		String id = "2000000008";// 海航物理通道ID ~~~~~
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
		String defaultParameter = (String)channel.get("default_parameter");// 默认参数
		Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String url = paramMap.get("yekUrl");
		String transactionID = dateFormat.format(new Date()) + getSixSquece();// 事务标识，由发起端生成
		String serviceCode = "/ServiceBus/custView/cust/AccountQuery";// 服务编码，唯一标识服务，必填字段
		String reqTime = dateFormat.format(new Date());// 请求时间，格式：yyyymmddhh24miss
		String sysId = "126";// 海航提供
		String requestSource = "2";
		String requestUser = "ceshi";
		String requestId = transactionID;
		String requestTime = dateFormat.format(new Date());
		;
		String destinationId = phoneDh;
		String destinationAttr = "8";
		String objType = "1";
		String balanceType = "1";
		String balanceQueryFlag = "0";
		String responseXml = sendPost(url, transactionID, serviceCode, reqTime,
				sysId, requestSource, requestUser, requestId, requestTime,
				destinationId, destinationAttr, objType, balanceType,
				balanceQueryFlag);

		// JSONObject responseJson = readXmlToJson(responseXml);
		Map<String, String> rltMap = readXmlToJson(responseXml);
		String code = rltMap.get("Result");
		if ("0".equals(code)) {
			returnMap.put("status", "0");
			returnMap.put("amount", rltMap.get("Balance"));
		} else {
			returnMap.put("status", "1");
			returnMap.put("amount", "0");
		}
		returnMap.put("phoneownername", "未知");

		return returnMap;

	}

	/**
	 * 
	 * @param url
	 * @param transactionID
	 *            事务标识，由发起端生成
	 * @param serviceCode
	 *            服务编码，唯一标识服务，必填字段
	 * @param reqTime
	 *            请求时间，格式：yyyymmddhh24miss
	 * @param sysId
	 *            发送请求的系统标识，与SvcCont（报文体）中可能出现的EXT_SYSTEM、SYSTEM_ID、
	 *            SystemID等字段含义保持一致，取值也应保持一致，具体取值参考附录。
	 *            PUB_RES：操作资源，一个包体中可能包含多个操作，可合为一个请求发送
	 * @param type
	 *            操作类型，标记具体的接口操作方法
	 * @param payAcct
	 *            代理商账号
	 * @param pwd
	 *            代理商密码 ，需要加密
	 * @param requestSource
	 *            自服务平台标识或IP地址
	 * @param requestUser
	 *            用户如果登录填写登录用户名，
	 * @param requestId
	 *            请求流水
	 * @param requestTime
	 *            请求时间，YYYYMMDDHHMMSS
	 * @param destinationId
	 *            被充值用户号码
	 * @param destinationAttr
	 *            被充值用户属性（取值参见省间互联分册7.5.53）
	 * @param destinationAttrDetail
	 *            被充值用户属性明细（取值参见省间互联分册7.5.79）
	 * @param objType
	 *            号码类型，1：帐户；2：客户；3：用户；
	 * @param balanceType
	 *            余额类型，根据各地情况分别定义
	 * @param rechargeUnit
	 *            充值金额单位：0－分；1－条;
	 * @param rechargeAmount
	 *            充值金额
	 * @param rechargeType
	 *            充值类型，0快充，1卡充，默认值 0
	 * @param staffId
	 *            员工标识
	 */

	public static String sendPost(String url, String transactionID,
			String serviceCode, String reqTime, String sysId,
			String requestSource, String requestUser, String requestId,
			String requestTime, String destinationId, String destinationAttr,
			String objType, String balanceType, String balanceQueryFlag) {
		StringBuffer xml = new StringBuffer();
		xml.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"http://www.tydic.com/\"><SOAP-ENV:Header/><SOAP-ENV:Body><Business>");
		JSONObject jsonObject = new JSONObject(true);

		JSONObject tcpCont = new JSONObject();
		JSONObject svcCont = new JSONObject();

		JSONArray SOOArray = new JSONArray();

		JSONObject SOO = new JSONObject();

		JSONObject PUBREQ = new JSONObject();

		SOO.put("PUB_REQ", PUBREQ);
		svcCont.put("SOO", SOOArray);

		JSONObject ACCOUNTQUERYREQ = new JSONObject(true);
		ACCOUNTQUERYREQ.put("BalanceQueryFlag", balanceQueryFlag);
		ACCOUNTQUERYREQ.put("BalanceType", balanceType);
		ACCOUNTQUERYREQ.put("DestinationAttr", destinationAttr);
		ACCOUNTQUERYREQ.put("DestinationId", destinationId);
		ACCOUNTQUERYREQ.put("ObjType", objType);
		ACCOUNTQUERYREQ.put("RequestId", requestId);
		ACCOUNTQUERYREQ.put("RequestSource", requestSource);
		ACCOUNTQUERYREQ.put("RequestTime", requestTime);
		ACCOUNTQUERYREQ.put("RequestUser", requestUser);
		SOO.put("ACCOUNTQUERY_REQ", ACCOUNTQUERYREQ);

		SOOArray.add(SOO);
		System.out.println(svcCont);
		RSAEncrypt rsaEncrypt = new RSAEncrypt();
		// 先进行MD5加密需要签名的字符串
		System.out.println("需要签名的字符串：" + JSONObject.toJSONString(svcCont));
		String code = rsaEncrypt.MD5(JSONObject.toJSONString(svcCont));
		// 把MD5加密内容通过私钥签名
		String signatureInfo = rsaEncrypt.enc(code);

		tcpCont.put("TransactionID", transactionID);
		tcpCont.put("ReqTime", reqTime);
		tcpCont.put("SignatureInfo", signatureInfo);
		tcpCont.put("ServiceCode", serviceCode);
		tcpCont.put("SYS_ID", sysId);
		jsonObject.put("TcpCont", tcpCont);
		jsonObject.put("SvcCont", svcCont);
		xml.append(jsonObject);
		xml.append("</Business></SOAP-ENV:Body></SOAP-ENV:Envelope>");

		System.out.println(xml);
		String ret = ToolHttps.post(true, url, xml.toString(), null);
		System.out.println("响应|" + ret);
		return ret;
	}

	/**
	 * 获取6位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	/**
	 * 将xml字符串转换成Json(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 *            发送查询请求后，上游返回的xml
	 */
	public static Map<String, String> readXmlToJson(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List l = rootElt.elements();
			Element body = rootElt.element("Body");
			String jsonStr = body.elementTextTrim("Business");
			String responseId = body.elementTextTrim("ResponseId");
			String responseTime = body.elementTextTrim("ResponseTime");
			String result = body.elementTextTrim("Result");
			String balance = body.elementTextTrim("Balance");
			String balanceUnit = body.elementTextTrim("BalanceUnit");
			String balanceType = body.elementTextTrim("BalanceType");
			//JSONObject json = JSONObject.parseObject(jsonStr);

			map.put("ResponseId", responseId);
			map.put("Result", result);
			map.put("Balance", balance);
			map.put("BalanceUnit", balanceUnit);
			map.put("BalanceType", balanceType);
			map.put("ResponseTime", responseTime);
			return map;

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析出錯");
		return null;
	}
}
