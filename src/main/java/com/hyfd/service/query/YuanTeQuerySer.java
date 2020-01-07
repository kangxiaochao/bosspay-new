package com.hyfd.service.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.hyfd.bean.yuante.BodyInfo;
import com.hyfd.bean.yuante.YuanTeBean;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.common.utils.YuanTeSign;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class YuanTeQuerySer extends BaseService
{
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
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap
	 * key:amount 余额
	 * key:status 查询状态 0查询成功 1查询失败
	 */
	public Map<String, String> getChargeAmountInfo(String mobileNumber){
	 
	 return  YuanTeQueryService(mobileNumber.replace(" ",""));
	}
	/**
	 * 远特查询余额
	 * @param phoneNo 查询余额的远特手机号
	 * @return 
	 *     status 状态 0成功 1失败  
	 *     amount 手机余额 单位：元
	 *     phoneownername 机主姓名
	 */
		public Map<String,String> YuanTeQueryService(String phoneNo) {
			
			Map<String,String> returnMap=new HashMap<String,String>();
			
			String id = "2000000021";// 物理通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String desUrl = "http://120.26.134.145:22336/des.aspx"; // 加密地址
			
			String notifyUrl = paramMap.get("chargeAmountQryUrl");
			String privateKey = paramMap.get("privateKey");// 加密秘钥
			String userName =paramMap.get("userName");	//Boss系统分配，定值
			String userPwd = paramMap.get("userPwd");		//Boss系统分配，定值
			String systemId = paramMap.get("systemId");//接入Boss系统的外系统个数，Boss系统分配，定值
		
			String chargeAmountQryUrl = paramMap.get("chargeAmountQryUrl");//充值类型：1 余额查询
		
			String streamNo =  systemId+dateFormat.format(new Date()) +getFiveSquece();//3位系统标识 + yyyymmddhhmiss + 8位流水
			String intfType = "0";// 接口类型：0同步接口,定值
			String serviceKind = paramMap.get("serviceKind"); // 业务类型，定值
			String linkUrl = paramMap.get("linkUrl"); 	//请求地址,定值
			String chargeAmountQryJson=getChargeAmountQryJson(streamNo, phoneNo, systemId, userName, 
					userPwd, intfType, chargeAmountQryUrl,notifyUrl, serviceKind, desUrl, privateKey);
					
			String queryResultJson=ToolHttps.post(true, linkUrl, chargeAmountQryJson,null);
			
			Map<String, String> rltMap = readXmlToMapFromCreateResponseOfQuery(queryResultJson);
			String code = rltMap.get("ResultCode");
			if("0".equals(code)){
				returnMap.put("status","0");
				returnMap.put("amount",(Double.parseDouble(""+rltMap.get("RestFee"))/100)+"");
			}else{
				returnMap.put("status","1");
				returnMap.put("amount","0");
			}
			returnMap.put("phoneownername","未知");
			return returnMap;
		}
	
		
		/**
		 * 获取余额查询请求json
		 * @param url
		 * @param streamNo  3位系统标识 + yyyymmddhhmiss + 8位流水
		 * @param phoneNo   	查询余额的号码
		 * @param systemId    接入boss系统的外系统个数，由boss系统统一分配
		 * @param userName  登陆系统的用户名，boss系统分配
		 * @param userPwd     用户名对应的密码
		 * @param intfType    接口类型：0同步接口
		 * @param notifyUrl   回调地址
		 * @param serviceKind  业务类型 1 余额查询
		 * @param desUrl         加密地址
		 * @param privateKey   加密秘钥
		 * @return
		 */
		public static String getChargeAmountQryJson
		(String streamNo,String phoneNo, String systemId,String userName, String userPwd, String intfType, String chargeAmountQryUrl,
		String notifyUrl,String serviceKind, 
		String desUrl, String privateKey) {

			YuanTeBean bean = new YuanTeBean();
			bean.setStreamNo(streamNo);
			bean.setSystemId(systemId);
			bean.setUserName(userName);
			bean.setUserPwd(userPwd);
			bean.setIntfType(intfType);// 接口类型：0同步接口
			bean.setRechargeType(chargeAmountQryUrl);// 充值类型：1 余额查询
			bean.setNotifyURL(notifyUrl);
			
			List bodyInfos = new ArrayList();
			BodyInfo bodyInfo = new BodyInfo();
			bodyInfo.setServiceId(phoneNo);// 充值号码，业务号码
			bodyInfo.setServiceKind(serviceKind);// 业务类型： 8
			
			bodyInfos.add(bodyInfo);
			bean.setBodys(bodyInfos);

			// 拼接xml
			String xml = createRequestXMLOfQuery(bean, privateKey);

			return xml;
		}
		
		
		
		/**
		 * 将xml字符串转换成map(给上游发送请求，有数据返回时调用)
		 * @param xml
		 *          发送充值请求后，上游返回的xml
		 * @return Map结构:<"StreamNo", string> 消息流水（3位系统标识 + yyyymmddhhmiss +
		 *         8位流水），同请求消息头<br />
		 *         <"ResultCode", string> 接收结果 0 成功 1失败<br />
		 *         <"ResultInfo", string> 失败原因<br />
		 */
		public static Map<String, String> readXmlToMapFromCreateResponseOfQuery(String xml) {
			Document doc = null;
			Map<String, String> map = new HashMap<String, String>();
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			try {
				doc = DocumentHelper.parseText(xml); // 将字符串转为XML
				Element rootElt = doc.getRootElement(); // 获取根节点
				List l = rootElt.elements();
				Element recordEle1 = rootElt.element("Body");
				String streamNo = recordEle1.elementTextTrim("StreamNo");
				String RestFee = recordEle1.elementTextTrim("RestFee");
				String resultCode = recordEle1.elementTextTrim("ResultCode");
				String resultInfo = recordEle1.elementTextTrim("ResultInfo");
				
				String AcctBalanceFee = recordEle1.elementTextTrim("AcctBalanceFee");
				String FreezeFee = recordEle1.elementTextTrim("FreezeFee");
				
				
				map.put("StreamNo", streamNo);
				map.put("RestFee", RestFee);
				map.put("ResultCode", resultCode);
				map.put("ResultInfo", resultInfo);
				map.put("AcctBalanceFee", AcctBalanceFee);
				
				map.put("FreezeFee", FreezeFee);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
		
		/**
		 * 根据参数bean拼成上游所需xml字符串
		 * 
		 * @param bean
		 * @return 包含xml信息的字符串
		 */
		public static String createRequestXMLOfQuery(YuanTeBean bean, String privateKey) {

			StringBuffer encryptInfoXML = new StringBuffer();

			encryptInfoXML.append("<StreamNo>" + bean.getStreamNo() + "</StreamNo>");
			encryptInfoXML.append("<SystemId>" + bean.getSystemId() + "</SystemId>");
			encryptInfoXML.append("<UserName>" + bean.getUserName() + "</UserName>");
			encryptInfoXML.append("<UserPwd>" + bean.getUserPwd() + "</UserPwd>");
			encryptInfoXML.append("<IntfType>" + bean.getIntfType() + "</IntfType>");
			encryptInfoXML.append("<RechargeType>" + bean.getRechargeType() + "</RechargeType>");
			encryptInfoXML.append("<NotifyURL>" + bean.getNotifyURL() + "</NotifyURL>");
			for (BodyInfo bodyInfo : bean.getBodys()) {
				encryptInfoXML.append("<BodyInfo>");
				encryptInfoXML.append("<ServiceId>" + bodyInfo.getServiceId() + "</ServiceId>");
				encryptInfoXML.append("<ServiceKind>" + bodyInfo.getServiceKind() + "</ServiceKind>");
				encryptInfoXML.append("</BodyInfo>");
			}
			String encryptInfo = encryptInfoXML.toString();

			// 得到密文
			String desEncryptInfo = YuanTeSign.encryptToHex(privateKey, encryptInfo);
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
			// String testxml =
			// "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>728</ContentLen><EncryptInfo><StreamNo>1012015033118555510001234</StreamNo><SystemId>101</SystemId><UserName>test</UserName><UserPwd>test</UserPwd><IntfType>1</IntfType><RechargeType>0</RechargeType><NotifyURL>http://127.0.0.1:8080/services/chargeNotify</NotifyURL><BodyInfo><accountId>14508</accountId><cityCode>110</cityCode><dealerId>1111404</dealerId><ifContinue>29</ifContinue><notifyDate>20150402152003</notifyDate><operator>LYBJ_TEST01</operator><payFee>10</payFee><userId>41629</userId><serviceId>17090060696</serviceId><serviceKind>8</serviceKind></BodyInfo></EncryptInfo></soapenv:Body></soapenv:Envelope>";
			return desWen.toString();
		}
		
		public static int getFiveSquece() {
			return (int) ((Math.random() * 9 + 1) * 10000000);
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
			String result = ToolHttps.post(false, desUrl, data, "application/x-www-form-urlencoded");
			return result;
		}
}
