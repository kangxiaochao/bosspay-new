package com.hyfd.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.DixintongKeyDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class DiXinTongBillTask {

	private static Logger log = Logger.getLogger(DiXinTongBillTask.class);
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	DixintongKeyDao keyDao;
	@Autowired
	OrderDao orderDao;//订单
	@Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	
	@Scheduled(fixedDelay = 60000)
	public void queryDiXinTong(){
		String channelId = "2000000004";
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(channelId);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String linkUrl = paramMap.get("linkUrl");
			String appKey = paramMap.get("appId");
			String destType = paramMap.get("destType");
			String queryMethod = paramMap.get("queryMethod");
			String version = paramMap.get("version");
			String qryType="1"; //1按充值订单号查询 2按充值号码查询
			String startTime="";
			String endTime="";
			String status="";
			String destCode = "";// 被充值的业务号码
			Map<String,Object> keyMap = keyDao.selectRecentKey();
			if(keyMap != null){
				String secretKey = (String) keyMap.get("secret_key");//密钥
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("dispatcherProviderId", channelId);
				param.put("status", "1");
				List<Map<String,Object>> orderList = orderDao.selectByTask(param);
				for(Map<String,Object> order : orderList){
					int flag = 2;
					String transactionId = appKey+ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + GenerateData.getIntData(9,8)+GenerateData.getCharAndNumr(1);// 交易流水号，每笔交易均不一样
					String orderId = (String) order.get("orderId");
					map.put("orderId", orderId);
					String providerOrderId = (String) order.get("providerOrderId");
					String reqTime = DateTimeUtils.getDateTime("yyyyMMdd");
					String chargeOrderQueryXml=getChargeOrderQueryAll(appKey, secretKey,queryMethod,version,transactionId, reqTime, qryType, destCode, orderId, destType, startTime, endTime, status);
					String responseData = sslPost(linkUrl, chargeOrderQueryXml);
					if (responseData != null && !responseData.equals("")) {
						Map<String, String> resultMap=readXmlToMapFromQueryResponse(responseData);
						String queryResultStatus=resultMap.get("status");
						String respCode=resultMap.get("respCode");
						String respDesc=resultMap.get("respDesc");
						map.put("resultCode", respCode + ":" + respDesc);
						if("success".equals(queryResultStatus)){
							//成功状态处理
							flag = 1;
						}else if("underway".equals(queryResultStatus) ) {
							//处理中状态
							continue;
						}else{
							flag = 0;
						}
					}
					map.put("status", flag);
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}else{
				log.error("迪信通查询获取密钥为空");
			}
		}catch(Exception e){
			log.error("迪信通查询出错"+e.getMessage());
		}
	}
	
	/**
	 * 进行HttpClient post连接
	 * 
	 * @param isHttps
	 *            是否ssl链接
	 * @param url
	 * @param data
	 * @param contentType
	 * @return
	 */
	public String sslPost(String url, String data) {
		CloseableHttpClient httpClient = createSSLInsecureClient();
		HttpPost httpPost = new HttpPost(url);
		if (null != data) {
			StringEntity stringEntity = new StringEntity(data, "UTF-8");
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/xml");
			httpPost.setEntity(stringEntity);
		}

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
		httpPost.setConfig(requestConfig);

		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String out = EntityUtils.toString(entity, "UTF-8");
				return out;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("dixintong sslPost error|"+e.getMessage());
		} 

		return null;
	}

	/**
	 * HTTPS访问对象，信任所有证书
	 * 
	 * @return
	 */
	public CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				@SuppressWarnings("unused")
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}

				@Override
				public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("dixintong createSSLInsecureClient error|"+e.getMessage());
		} 
		return HttpClients.createDefault();
	}
	
	/**
	 * 获取查询的xml参数0
	 * @author lks 2017年8月31日下午2:37:30
	 * @param appKey
	 * @param secretKey
	 * @param method
	 * @param version
	 * @param transactionId
	 * @param reqTime
	 * @param qryType
	 * @param destCode
	 * @param reqSerial
	 * @param destType
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @return
	 */
	public static String getChargeOrderQueryAll(String appKey, String secretKey,String method,String version,String transactionId,String reqTime,String qryType,String destCode,String reqSerial,String destType,String startTime,String endTime,String status){
		String headXml = "";
		String bodyXml = getChargeOrderQueryBody(qryType, destCode, reqSerial, destType, startTime, endTime, status);
		
		String sign = "";
		try {
			String signSource = transactionId + "" + bodyXml + "" + secretKey;
			sign = MD5.ToMD5(signSource);
			headXml = getChargeOrderQueryHead(appKey,method,transactionId, reqTime, sign,version);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("dixintong sign error|"+e.getMessage());
		}
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<Root>");
		xml.append(headXml);
		xml.append(bodyXml);
		xml.append("</Root>");
		return xml.toString();
	}
	
	/**
	 * 获取xml的head
	 * @author lks 2017年8月31日下午2:43:34
	 * @param appKey
	 * @param method
	 * @param transactionId
	 * @param reqTime
	 * @param sign
	 * @param version
	 * @return
	 */
	public static String getChargeOrderQueryHead(String appKey,String method,String transactionId,String reqTime,String sign,String version){
		StringBuffer xml=new StringBuffer();
		xml.append("<Head>");
		xml.append("<appKey>" + appKey + "</appKey>");
		xml.append("<method>" + method + "</method>");
		xml.append("<transactionId>" + transactionId + "</transactionId>");
		xml.append("<reqTime>" + reqTime + "</reqTime>");
		xml.append("<sign>" + sign + "</sign>");
		xml.append("<version>" + version + "</version>");
		xml.append("</Head>");
		return xml.toString();
	}
	
	/**
	 * 获取xml的body
	 * @author lks 2017年8月31日下午2:44:24
	 * @param qryType
	 * @param destCode
	 * @param reqSerial
	 * @param destType
	 * @param startTime
	 * @param endTime
	 * @param status
	 * @return
	 */
	public static String getChargeOrderQueryBody(String qryType,String destCode,String reqSerial,String destType,String startTime,String endTime,String status){
		StringBuffer signXml = new StringBuffer();
		signXml.append("<Body>");
		signXml.append("<RechargeQueryReq>");
		signXml.append("<qryType>" + qryType + "</qryType>");
		signXml.append("<destCode>" + destCode + "</destCode>");
		signXml.append("<reqSerial>" + reqSerial + "</reqSerial>");
		signXml.append("<destType>" + destType + "</destType>");
		signXml.append("<startTime>" + startTime + "</startTime>");
		signXml.append("<endTime>" + endTime + "</endTime>");
		signXml.append("<status>" + status + "</status>");
		signXml.append("</RechargeQueryReq>");
		signXml.append("</Body>");
		return signXml.toString();
	}
	
	/**
	 * 读取订单查询结果  success 成功 underway 充值中 其他情况都为失败
	 * @param xml
	 * @return
	 */
	public static Map<String, String> readXmlToMapFromQueryResponse(String xml) {
		Document doc = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			
			Element headElement = rootElt.element("Head");
			Element bodyElement = rootElt.element("Body");
			
			Element responseElement = headElement.element("Response");
			String respCode = responseElement.elementTextTrim("respCode");
			String respDesc = responseElement.elementTextTrim("respDesc");
			
			Element rechargeQueryRespElement = bodyElement.element("RechargeQueryResp");
			Element rechargeRecordInfoElement = rechargeQueryRespElement.element("RechargeRecordInfo");
			try{
			String status=rechargeRecordInfoElement.elementTextTrim("status");
			resultMap.put("status", status);
			}catch(Exception e){
				e.printStackTrace();
				//发生异常以后设置为处理中
				resultMap.put("status", "underway");
			}
			resultMap.put("respCode", respCode);
			resultMap.put("respDesc", respDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
}
