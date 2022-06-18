package com.hyfd.exceptionTask;

import com.hyfd.common.utils.LanMaoSign;
import com.hyfd.common.utils.XmlUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class LanMaoExceptionTask implements BaseTask {

	private static Logger log = Logger.getLogger(LanMaoExceptionTask.class);

	@Override
	public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String defaultParameter = (String) channelMap.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryUrl = paramMap.get("apiUrl") + "";// 查询地址
			String userName = paramMap.get("userName"); // 用户编号
			String userPwd = paramMap.get("userPwd"); // 加密密码
			String apiKey = paramMap.get("apiKey");// 加密秘钥
			String systemId = paramMap.get("systemId");

			String upids = order.get("providerOrderId") + "";
			String streamNo = order.get("orderId") + "";
			map.put("orderId", streamNo);
			map.put("providerOrderId",upids);
			map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
			map.put("phone",order.get("phone"));
			map.put("fee",order.get("fee"));

			// 接口类型：1异步接口 0同步接口
			String intfType = "0";
			// 充值类型：1 余额查询
			String rechargeType = "2";

			String encryptInfo = "<EncryptInfo><StreamNo>" + streamNo + "</StreamNo><SystemId>" + systemId
					+ "</SystemId><UserName>" + userName + "</UserName><UserPwd>" + userPwd + "</UserPwd>"
					+ "<IntfType>" + intfType + "</IntfType><RechargeType>" + rechargeType + "</RechargeType>"
					+ "<NotifyURL></NotifyURL><BodyInfo></BodyInfo></EncryptInfo>";

			// readXmlToMap
			String sign = LanMaoSign.encryptToHex(apiKey, encryptInfo);

			String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>"
					+ sign.length() + "</ContentLen><EncryptInfo>" + sign
					+ "</EncryptInfo></soapenv:Body></soapenv:Envelope>";

			String result = post(streamNo, queryUrl, envelope);
			Map<String, String> xmlMap = readXmlToMap(result);

			if (map == null || "-1".equals(xmlMap.get("ResultCode"))) {// 接口异常
				map.put("status", 3);
				map.put("resultCode", "接口查询异常，请稍后重试！");
			} else if ("1".equals(xmlMap.get("ResultCode"))) {// 充值失败
				map.put("status", 0);
				map.put("resultCode", "充值失败！");
			} else if ("2".equals(xmlMap.get("ResultCode"))) {// 充值中
				map.put("status", 2);
				map.put("resultCode", "充值中！" );
			} else if ("0".equals(xmlMap.get("ResultCode"))) {// 充值成功
				map.put("status", 1);
				map.put("resultCode", "充值成功！");
			}else {
				map.put("status", 3);
				map.put("resultCode", "未知的订单状态，请与上家核实后处理!");
			}
		} catch (Exception e) {
			log.error("爱施德话费查询Task出错" + e);
		}
		return map;
	}

	/**
	 * Http请求工具类
	 */
	public static String post(String streamNo, String url, String data) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		// 设置请求头对象
		StringEntity stringEntity = new StringEntity(data, "UTF-8");
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("text/xml");
		httpPost.setEntity(stringEntity);

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
		httpPost.setConfig(requestConfig);

		String result = "";
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int ret = response.getStatusLine().getStatusCode();
			if (ret == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, "UTF-8");
					return out;
				}
			} else {
				result = "";
			}
		} catch (HttpException e) {
			result = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
					+ streamNo
					+ "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>HttpException</ResultInfo></soapenv:Body></soapenv:Envelope>";

		} catch (IOException e) {
			result = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
					+ streamNo
					+ "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>IOException</ResultInfo></soapenv:Body></soapenv:Envelope>";
		}
		return result;
	}

	/**
	 * 将xml字符串转换成Json(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 *            发送充值请求后，上游返回的xml
	 */
	public static Map<String, String> readXmlToMap(String xml) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			MessageFactory msgFactory = MessageFactory.newInstance();
			SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(),
					new ByteArrayInputStream(xml.getBytes("UTF-8")));
			reqMsg.saveChanges();
			SOAPMessage msg = reqMsg;
			@SuppressWarnings("unchecked")
			Iterator<SOAPElement> iterator = msg.getSOAPBody().getChildElements();
			while (iterator.hasNext()) {
				SOAPElement element = (SOAPElement) iterator.next();
				map.put(element.getLocalName(), element.getValue());
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
