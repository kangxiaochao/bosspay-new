package com.hyfd.task;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.yuanteUtils.sdk.Key;
import com.hyfd.yuanteUtils.sdk.TencentConstants;
import com.hyfd.yuanteUtils.sdk.TencentSignature;
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

		log.info("远特task回调=============");
		try {
			String id = "2000000021";// 物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String dealerId = paramMap.get("dealerId");
			String url = paramMap.get("queryUrl");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);
				String providerOrderId = order.get("providerOrderId") + "";
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("systemId","0");
				jsonObject.put("dealerId",dealerId);
				jsonObject.put("flowNumber",providerOrderId);

				String resultStr = charge(url, jsonObject.toString());
				log.info("远特回调参数：============="+resultStr);
				if (resultStr == null) {
					log.error("远特查询返回值为空 providerOrderId=" + providerOrderId);
					continue;
				}
				JSONObject resultJson = JSONObject.parseObject(resultStr);
				String result = resultJson.getString("result");
				if("0000".equals(result)){
					String payStatus = resultJson.getString("payStatus");
					if("2".equals(payStatus)){
						flag = 1;
						map.put("status", flag);
					}else {
						flag = 0;
						map.put("status", flag);
					}
				}else {
					flag = 0;
					map.put("status", flag);
				}
				if(map.containsKey("status")){
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		} catch (Exception e) {
			log.error("远特查询Task出错" + e);
		}
	}

	public String charge(String linkUrl,String str){
		TencentSignature signature = new TencentSignature();
		String resultStr = "";
		try
		{
			String sign = signature.rsa256Sign(str, Key.privateKey, TencentConstants.CHARSET_GBK);
			String signTemp = URLEncoder.encode(sign, "GBK");
			System.out.println(signTemp);
			String url = linkUrl+"?1=1&sign="+signTemp;
			resultStr = ToolHttp.post(false, url, str, null);
			return resultStr;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultStr;
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
