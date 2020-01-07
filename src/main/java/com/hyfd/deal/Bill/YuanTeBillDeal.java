package com.hyfd.deal.Bill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class YuanTeBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phone = (String) order.get("phone");//手机号
			String fee = order.get("fee")+"";//金额，以元为单位
			String spec = Double.parseDouble(fee)+"";//充值金额，以元为单位
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String privateKey = paramMap.get("privateKey");	//加密秘钥
			String userName = paramMap.get("userName");		//账号
			String userPwd = paramMap.get("userPwd");		//密码
			String systemId = paramMap.get("systemId");		//接入Boss系统的外系统个数	
			String intfType = paramMap.get("intfType");		//接口类型
			String userId = paramMap.get("userId");			//用户标示
			String accountId = paramMap.get("accountId");	//账户标示
			String cityCode = paramMap.get("cityCode");		//受理地市
			String dealerId = paramMap.get("dealerId");		//营业厅代码
			String serviceKind = paramMap.get("serviceKind");//业务类型
			String desUrl = paramMap.get("desUrl");			//des加密地址
			String noticeUrl = paramMap.get("noticeUrl");	//回调地址
			String curids = systemId + DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + GenerateData.getIntData(9,8);//订单号
			map.put("orderId", curids);
			String result = sendPost(linkUrl, curids, phone, systemId, userName, userPwd, intfType,
					noticeUrl, cityCode, accountId, userId, dealerId, spec, serviceKind, desUrl, privateKey);
			if(result!=null&&!result.equals("")){//有返回值
				Map<String,String> resultMap = readXmlToMapFromCreateResponse(result);
				String code = resultMap.get("ResultCode");
				String submitbackmsg = resultMap.get("ResultInfo");
				map.put("resultCode", code+":"+submitbackmsg);
				if("0".equals(code)){//提交成功
					flag = 1;
				}else{//提交失败
					flag = 0;
				}
			}
		}catch(Exception e){
			log.error("远特充值方法报错"+e.getMessage()+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 
	 * @param url
	 * @param orderId
	 *          消息流水（3位系统标识 + yyyymmddhhmiss + 8位流水）
	 * @param phoneNo
	 *          业务号码
	 * @param systemId
	 *          接入boss系统的外系统个数，由boss系统统一分配
	 * @param userName
	 *          登陆系统的用户名，boss系统分配
	 * @param userPwd
	 *          用户名对应的密码
	 * @param intfType
	 *          接口类型：1异步接口
	 * @param notifyUrl
	 *          回调地址
	 * @param cityCode
	 *          城市代码
	 * @param accountId
	 *          帐户标识
	 * @param userId
	 *          用户标识
	 * @param dealerId
	 *          营业厅代码
	 * @param payFee
	 *          充值金额
	 * @param serviceKind
	 *          业务类型
	 * @param privateKey
	 *          加密秘钥
	 * @return
	 */
	public static String sendPost(String url, String orderId, String phoneNo, String systemId, String userName, String userPwd, String intfType, String notifyUrl, String cityCode, String accountId, String userId, String dealerId, String payFee, String serviceKind, String desUrl, String privateKey) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("streamNo", orderId);
		map.put("systemId", systemId);
		map.put("userName", userName);
		map.put("userPwd", userPwd);
		map.put("intfType", intfType);//接口类型：1 异步接口
		map.put("rechargeType", "0");//充值类型：0 代理商充值
		map.put("notifyUrl", notifyUrl);
		Map<String,Object> bodyInfo = new HashMap<String,Object>();
		bodyInfo.put("cityCode", cityCode);
		bodyInfo.put("accountId", accountId);
		bodyInfo.put("userId", userId);
		bodyInfo.put("dealerId", dealerId);
		bodyInfo.put("notifyDate", DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		bodyInfo.put("operator", userName);
		bodyInfo.put("payFee", payFee);
		bodyInfo.put("serviceId", phoneNo);
		bodyInfo.put("serviceKind", serviceKind);
		bodyInfo.put("ifContinue", "30");
		map.put("bodys", bodyInfo);
		// 拼接xml
		String xml = createRequestXML(map, desUrl, privateKey);
		String ret = ToolHttp.post(true, url, xml, null);
		return ret;
	}
	
	/**
	 * 生成请求的xml
	 * @author lks 2017年2月6日下午2:30:49
	 * @param bean
	 * @param desUrl
	 * @param privateKey
	 * @return
	 */
	public static String createRequestXML(Map<String,Object> map, String desUrl, String privateKey) {

		StringBuffer encryptInfoXML = new StringBuffer();
		encryptInfoXML.append("<StreamNo>" + map.get("streamNo") + "</StreamNo>");
		encryptInfoXML.append("<SystemId>" + map.get("systemId") + "</SystemId>");
		encryptInfoXML.append("<UserName>" + map.get("userName") + "</UserName>");
		encryptInfoXML.append("<UserPwd>" + map.get("userPwd") + "</UserPwd>");
		encryptInfoXML.append("<IntfType>" + map.get("intfType") + "</IntfType>");
		encryptInfoXML.append("<RechargeType>" + map.get("rechargeType") + "</RechargeType>");
		encryptInfoXML.append("<NotifyURL>" + map.get("notifyUrl") + "</NotifyURL>");
		@SuppressWarnings("unchecked")
		Map<String,Object> bodyInfo = (Map<String, Object>) map.get("bodys");
		encryptInfoXML.append("<BodyInfo>");
		encryptInfoXML.append("<accountId>" + bodyInfo.get("accountId") + "</accountId>");
		encryptInfoXML.append("<cityCode>" + bodyInfo.get("cityCode") + "</cityCode>");
		encryptInfoXML.append("<dealerId>" + bodyInfo.get("dealerId") + "</dealerId>");
		encryptInfoXML.append("<ifContinue>" + bodyInfo.get("ifContinue") + "</ifContinue>");
		encryptInfoXML.append("<notifyDate>" + bodyInfo.get("notifyDate") + "</notifyDate>");
		encryptInfoXML.append("<operator>" + bodyInfo.get("operator") + "</operator>");
		encryptInfoXML.append("<payFee>" + bodyInfo.get("payFee") + "</payFee>");
		encryptInfoXML.append("<userId>" + bodyInfo.get("userId") + "</userId>");
		encryptInfoXML.append("<serviceId>" + bodyInfo.get("serviceId") + "</serviceId>");
		encryptInfoXML.append("<serviceKind>" + bodyInfo.get("serviceKind") + "</serviceKind>");
		encryptInfoXML.append("</BodyInfo>");
		String encryptInfo = encryptInfoXML.toString();

		String desEncryptInfo = getDes(desUrl, "1", encryptInfo.toString(), privateKey);
		desEncryptInfo = desEncryptInfo.trim();

		StringBuffer xml = new StringBuffer();
		xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">").append("<soapenv:Header/>").append("<soapenv:Body>").append("<ContentLen>" + desEncryptInfo.length() + "</ContentLen>").append("<EncryptInfo>");
		String ming = xml.toString();
		xml.append(desEncryptInfo);
		StringBuffer lastXml = new StringBuffer();
		lastXml.append("</EncryptInfo>");
		lastXml.append("</soapenv:Body>");
		lastXml.append("</soapenv:Envelope>");
		String mingwen = ming + encryptInfoXML + lastXml;
		String desWen = xml.toString() + lastXml;
		return desWen.toString();
	}
	
	/**
	 * @功能描述： 对时局进行加密
	 *
	 * @作者：zhangpj @创建时间：2016年2月19日
	 * @param desUrl
	 *          加密地址
	 * @param desType
	 *          1:加密 0：解密
	 * @param srcCode
	 * @return
	 */
	public static String getDes(String desUrl, String desType, String srcCode, String desKey) {
		String data = "desType=" + desType;
		data += "&srcCode=" + srcCode;
		data += "&desKey=" + desKey;
		String result = ToolHttp.post(false, desUrl, data, "application/x-www-form-urlencoded");
		return result;
	}
	
	/**
	 * 将xml字符串转换成map(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 *          发送充值请求后，上游返回的xml
	 * @return Map结构:<"StreamNo", string> 消息流水（3位系统标识 + yyyymmddhhmiss +
	 *         8位流水），同请求消息头<br />
	 *         <"ResultCode", string> 接收结果 0 成功 1失败<br />
	 *         <"ResultInfo", string> 失败原因<br />
	 */
	public static Map<String, String> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List l = rootElt.elements();
			Element recordEle1 = rootElt.element("Body");
			String streamNo = recordEle1.elementTextTrim("StreamNo");
			String flag = recordEle1.elementTextTrim("flag");
			String resultCode = recordEle1.elementTextTrim("ResultCode");
			String resultInfo = recordEle1.elementTextTrim("ResultInfo");
			map.put("StreamNo", streamNo);
			map.put("flag", flag);
			map.put("ResultCode", resultCode);
			map.put("ResultInfo", resultInfo);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
		String xml= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>1182018010411233058188528</StreamNo><ResultCode>0</ResultCode><ResultInfo>接收成功</ResultInfo></soapenv:Body></soapenv:Envelope>";
		Map<String, String> resultMap = readXmlToMapFromCreateResponse(xml);
		String code = resultMap.get("ResultCode");
		String submitbackmsg = resultMap.get("ResultInfo");
		if("0".equals(code)){//提交成功
			System.out.println(1);
		}else{//提交失败
			System.out.println(0);
		}
	}
}
