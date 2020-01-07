package com.hyfd.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.bean.yuante.BodyInfo;
import com.hyfd.bean.yuante.YuanTeBean;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.common.utils.YuanTeSign;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class YuanTeTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(YuanTeTask.class);

	@Scheduled(fixedDelay = 60000)
	public void queryYuanTeOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000021";// 物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String desUrl = paramMap.get("desUrl"); // 加密地址
			String privateKey = paramMap.get("privateKey");// 加密秘钥
			String userName = paramMap.get("userName"); // Boss系统分配，定值
			String userPwd = paramMap.get("userPwd"); // Boss系统分配，定值
			String systemId = paramMap.get("systemId");
			String intfType = paramMap.get("ddanType");// 接口类型：1异步接口,定值
			String userId = paramMap.get("userId");// 用户标识, 填写0,定值
			String dealerId = paramMap.get("dealerId");
			String serviceKind = paramMap.get("serviceKind"); // 业务类型，定值
			String url = paramMap.get("linkUrl"); // 请求地址,定值

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);

				String data = sendPost(url, orderId, systemId, userName, userPwd, intfType, userId, dealerId,
						serviceKind, desUrl, privateKey);
				if (data == null) {
					log.error("远特查询返回值为空 orderId=" + orderId);
					continue;
				}

				Map<String, String> rltMap = readXmlToMapFromCreateResponse(data);
				String code = rltMap.get("ResultCode").toString();
				String ResultInfo = rltMap.get("ResultInfo").toString();
				map.put("resultCode", code + ":" + ResultInfo);
				// 判断返回结果,成功0, 1:失败
				if ("0".equals(code)) {
					flag = 1;
					map.put("status", flag);
				} else if ("1".equals(code)) {
					flag = 0;
					map.put("status", flag);
				} else if("2".equals(code)){
					continue;
				} else {
					continue;
				}
				if(map.containsKey("status")){
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("远特查询Task出错" + e);
		}
	}

	private String sendPost(String url, String orderId, String systemId, String userName, String userPwd,
			String intfType, String userId, String dealerId, String serviceKind, String desUrl, String privateKey) {
		YuanTeBean bean = new YuanTeBean();
		bean.setStreamNo(orderId);
		bean.setSystemId(systemId);
		bean.setUserName(userName);
		bean.setUserPwd(userPwd);
		bean.setIntfType(intfType);// 接口类型：1异步接口
		bean.setRechargeType("2");// 充值类型：0 代理商充值
		// bean.setNotifyURL(notifyUrl);
		List bodyInfos = new ArrayList();
		BodyInfo bodyInfo = new BodyInfo();
		// bodyInfo.setCityCode(cityCode);
		// bodyInfo.setAccountId(accountId);// 帐户标识
		bodyInfo.setUserId(userId);// 用户标识
		bodyInfo.setDealerId(dealerId);// 营业厅代码
		bodyInfo.setNotifyDate(ToolDateTime.format(new Date(), "yyyyMMddHHMMdd"));// 缴费时间
		bodyInfo.setOperator(userName);
		// bodyInfo.setServiceId(phoneNo);// 充值号码，业务号码
		bodyInfo.setServiceKind(serviceKind);// 业务类型： 8
		bodyInfo.setIfContinue("30");// 缴费方式30代理商缴费

		bodyInfos.add(bodyInfo);
		bean.setBodys(bodyInfos);

		// 拼接xml
		String xml = createRequestXML(bean, desUrl, privateKey);
		log.error(xml);
		String ret = "";
		try {
			ret = ToolHttp.post(true, url, xml, null);
		} catch (Exception e) {
			log.error("远特定时任务查询请求发生异常：" + e.getMessage());
		}
		log.error(ret);
		return ret;
	}

	/**
	 * 根据参数bean拼成上游所需xml字符串
	 * 
	 * @param bean
	 * @return 包含xml信息的字符串
	 */
	public static String createRequestXML(YuanTeBean bean, String desUrl, String privateKey) {

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
			encryptInfoXML.append("<accountId>" + bodyInfo.getAccountId() + "</accountId>");
			encryptInfoXML.append("<cityCode>" + bodyInfo.getCityCode() + "</cityCode>");
			encryptInfoXML.append("<dealerId>" + bodyInfo.getDealerId() + "</dealerId>");
			encryptInfoXML.append("<ifContinue>" + bodyInfo.getIfContinue() + "</ifContinue>");
			encryptInfoXML.append("<notifyDate>" + bodyInfo.getNotifyDate() + "</notifyDate>");
			encryptInfoXML.append("<operator>" + bodyInfo.getOperator() + "</operator>");
			encryptInfoXML.append("<payFee>" + bodyInfo.getPayFee() + "</payFee>");
			encryptInfoXML.append("<userId>" + bodyInfo.getUserId() + "</userId>");
			encryptInfoXML.append("<serviceId>" + bodyInfo.getServiceId() + "</serviceId>");
			encryptInfoXML.append("<serviceKind>" + bodyInfo.getServiceKind() + "</serviceKind>");
			encryptInfoXML.append("</BodyInfo>");
		}
		String encryptInfo = encryptInfoXML.toString();
		// encryptInfo =
		// "<StreamNo>1012015033118555510001234</StreamNo><SystemId>101</SystemId><UserName>test</UserName><UserPwd>test</UserPwd><IntfType>1</IntfType><RechargeType>0</RechargeType><NotifyURL>http://127.0.0.1:8080/services/chargeNotify</NotifyURL><BodyInfo><accountId>14508</accountId><cityCode>110</cityCode><dealerId>1111404</dealerId><ifContinue>29</ifContinue><notifyDate>20150402152003</notifyDate><operator>LYBJ_TEST01</operator><payFee>10</payFee><userId>41629</userId><serviceId>17090060696</serviceId><serviceKind>8</serviceKind></BodyInfo>";
		// key = "NEUSOFT2";//远特给的key

		// 得到密文
		// String desEncryptInfo = DES.encrypt(privateKey,
		// encryptInfo.toString());
//		String desEncryptInfo = getDes(desUrl, "1", encryptInfo.toString(), privateKey);
		String desEncryptInfo = YuanTeSign.encryptToHex(privateKey, encryptInfo);
		desEncryptInfo = desEncryptInfo.trim();

		StringBuffer xml = new StringBuffer();
		xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
				.append("<soapenv:Header/>").append("<soapenv:Body>")
				.append("<ContentLen>" + desEncryptInfo.length() + "</ContentLen>").append("<EncryptInfo>");
		String ming = xml.toString();
		xml.append(desEncryptInfo);
		StringBuffer lastXml = new StringBuffer();
		lastXml.append("</EncryptInfo>");
		lastXml.append("</soapenv:Body>");
		lastXml.append("</soapenv:Envelope>");
		String mingwen = ming + encryptInfoXML + lastXml;
		String desWen = xml.toString() + lastXml;
		// System.out.println("明文：" + mingwen);
		// System.out.println("密文：" + desWen);
		// String testxml =
		// "<soapenv:Envelope
		// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>728</ContentLen><EncryptInfo><StreamNo>1012015033118555510001234</StreamNo><SystemId>101</SystemId><UserName>test</UserName><UserPwd>test</UserPwd><IntfType>1</IntfType><RechargeType>0</RechargeType><NotifyURL>http://127.0.0.1:8080/services/chargeNotify</NotifyURL><BodyInfo><accountId>14508</accountId><cityCode>110</cityCode><dealerId>1111404</dealerId><ifContinue>29</ifContinue><notifyDate>20150402152003</notifyDate><operator>LYBJ_TEST01</operator><payFee>10</payFee><userId>41629</userId><serviceId>17090060696</serviceId><serviceKind>8</serviceKind></BodyInfo></EncryptInfo></soapenv:Body></soapenv:Envelope>";
		return desWen.toString();
	}

	/**
	 * @功能描述： 对时局进行加密
	 *
	 * @作者：zhangpj @创建时间：2016年2月19日
	 * @param desUrl
	 *            加密地址
	 * @param desType
	 *            1:加密 0：解密
	 * @param srcCode
	 * @return
	 */
	public static String getDes(String desUrl, String desType, String srcCode, String desKey) {
		String data = "desType=" + desType;
		data += "&srcCode=" + srcCode;
		data += "&desKey=" + desKey;
		String result = "";
		try {
			result = ToolHttp.post(false, desUrl, data, "application/x-www-form-urlencoded");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(result.trim());
		return result;
	}

	/**
	 * 将xml字符串转换成map(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 *            发送充值请求后，上游返回的xml
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
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return map;
	}
}
