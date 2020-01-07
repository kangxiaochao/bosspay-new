package com.hyfd.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.hyfd.common.utils.Des3Utils;
import com.hyfd.common.utils.LanMaoSign;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;

public class BeiWeiTest {

	public static void main(String[] args) throws Exception {
		String phone = "16226650024";
		String fee = "1";
		String linkUrl = "http://119.18.192.105:10022";
		String apiKey = "NEUSOFT2";
		String userName = "ENTERF0122G5ihy7Q5";
		String userPwd = "YCYuLe1hhg761";
		String dealerId = "C100301868";
		String notifyURL = "http://120.26.134.145:40001/rcmp/jf/orderDeal/statusBackBeiWei";
		String systemId = "100";
		String serviceKind = "8";
		//
		// 生成自己的id，供回调时查询数据使用
		String curids = systemId + ToolDateTime.format(new Date(), "yyyyMMddHHmmss")
				+ (RandomUtils.nextInt(9999999) + 10000000);
		Map<String, String> sendmap = charge(linkUrl, apiKey, curids, systemId, userName, userPwd, dealerId, phone,
				serviceKind, fee + "", notifyURL);

		if (null != sendmap && !"".equals(sendmap)) {// 返回值不为空
			String code = sendmap.get("ResultCode");// 获取状态码
			String resultMsg = sendmap.get("ResultInfo");// 获取状态信息
			System.out.println(code+"||"+resultMsg);
		}

	}

	// 充值接口
	public static Map<String, String> charge(String apiUrl, String apiKey, String streamNo, String systemId,
			String userName, String userPwd, String dealerId, String serviceId, String serviceKind, String payFee,
			String notifyURL) {
		// 受理地市,填写0
		String cityCode = "0";
		// 帐户标识，填写0
		String accountId = "0";
		// 用户标识，填写0
		String userId = "0";
		// 缴费方式30代理商缴费
		String ifContinue = "30";
		// 缴费时间YYYYMMDD hh24miss如：20150331185555
		String notifyDate = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
		// 接口类型：1异步接口 0同步接口
		String intfType = "1";
		// 充值类型：0 代理商充值
		String rechargeType = "0";

		String encryptInfo = "<EncryptInfo><StreamNo>" + streamNo + "</StreamNo><SystemId>" + systemId
				+ "</SystemId><UserName>" + userName + "</UserName><UserPwd>" + userPwd + "</UserPwd>" + "<IntfType>"
				+ intfType + "</IntfType><RechargeType>" + rechargeType + "</RechargeType>" + "<NotifyURL>" + notifyURL
				+ "</NotifyURL><BodyInfo><accountId>" + accountId + "</accountId><cityCode>" + cityCode
				+ "</cityCode><dealerId>" + dealerId + "</dealerId>" + "<ifContinue>" + ifContinue
				+ "</ifContinue><notifyDate>" + notifyDate + "</notifyDate>" + "<operator>" + userName
				+ "</operator><payFee>" + payFee + "</payFee><userId>" + userId + "</userId><serviceId>" + serviceId
				+ "</serviceId><serviceKind>" + serviceKind + "</serviceKind></BodyInfo></EncryptInfo>";

		// readXmlToMap
		String sign = LanMaoSign.encryptToHex(apiKey, encryptInfo);
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>"
				+ sign.length() + "</ContentLen><EncryptInfo>" + sign
				+ "</EncryptInfo></soapenv:Body></soapenv:Envelope>";
		System.err.println("请求内容:" + envelope);
		String result = post(streamNo, apiUrl, envelope);
		Map<String, String> map = readXmlToMap(result);
		System.err.println("返回内容:" + map);
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

				result = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><StreamNo>"
						+ streamNo + "</StreamNo><ResultCode>-1</ResultCode><ResultInfo>HttpCode:" + ret
						+ "</ResultInfo></soapenv:Body></soapenv:Envelope>";

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
	@SuppressWarnings("unchecked")
	public static Map<String, String> readXmlToMap(String xml) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			MessageFactory msgFactory = MessageFactory.newInstance();
			SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(),
					new ByteArrayInputStream(xml.getBytes("UTF-8")));
			reqMsg.saveChanges();
			SOAPMessage msg = reqMsg;
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
