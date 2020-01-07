package com.hyfd.service.query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.LanMaoSign;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class LanMaoQuerySer extends BaseService
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
	 
	 return  lanMaoQueryService(mobileNumber.replace(" ",""));
 }
	/**
	 * 远特查询余额
	 * @param phoneNo 查询余额的远特手机号
	 * @return 
	 *     status 状态 0成功 1失败  
	 *     amount 手机余额 单位：元
	 *     phoneownername 机主姓名
	 */
		public Map<String,String> lanMaoQueryService(String phoneNo) {
			
			Map<String,String> returnMap=new HashMap<String,String>();
			
			String id = "2000000011";
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			String notifyUrl = "1";
			String privateKey = paramMap.get("apiKey");// 加密秘钥
			String userName =paramMap.get("userName");	//Boss系统分配，定值
			String userPwd = paramMap.get("userPwd");		//Boss系统分配，定值
			String systemId = paramMap.get("systemId");//接入Boss系统的外系统个数，Boss系统分配，定值
		
			String streamNo =  systemId+dateFormat.format(new Date()) +getFiveSquece();//3位系统标识 + yyyymmddhhmiss + 8位流水
			
			String serviceKind = paramMap.get("serviceKind"); // 业务类型，定值
			String linkUrl = paramMap.get("apiUrl"); 	//请求地址,定值
			Map<String, String> map1 = query(linkUrl, privateKey, streamNo, systemId, userName, userPwd, phoneNo,
					serviceKind, notifyUrl);
			String code = map1.get("ResultCode");
			if("0".equals(code)){
				returnMap.put("status","0");
				returnMap.put("amount",(Double.parseDouble(""+map1.get("RestFee"))/100)+"");
				returnMap.put("phoneownername",map1.get("CustName")+"");
			}else{
				returnMap.put("status","1");
				returnMap.put("amount","0");
				returnMap.put("phoneownername","未知");
			}
			return returnMap;
		}
	
		public static int getFiveSquece() {
			return (int) ((Math.random() * 9 + 1) * 10000000);
		}
		
		// 余额查询接口
		public Map<String, String> query(String apiUrl, String apiKey, String streamNo, String systemId,
				String userName, String userPwd,  String serviceId, String serviceKind,
				String notifyURL) {
			
			// 接口类型：1异步接口 0同步接口
			String intfType = "0";
			// 充值类型：0 代理商充值
			String rechargeType = "1";

			String encryptInfo = "<EncryptInfo><StreamNo>" + streamNo + "</StreamNo><SystemId>" + systemId
					+ "</SystemId><UserName>" + userName + "</UserName><UserPwd>" + userPwd + "</UserPwd>" + "<IntfType>"
					+ intfType + "</IntfType><RechargeType>" + rechargeType + "</RechargeType>" + "<NotifyURL>" + notifyURL
					+ "</NotifyURL><BodyInfo><ServiceId>" +serviceId+ "</ServiceId><ServiceKind>" + serviceKind + "</ServiceKind></BodyInfo></EncryptInfo>";

			// readXmlToMap
			String sign = LanMaoSign.encryptToHex(apiKey, encryptInfo);
			String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>"
					+ sign.length() + "</ContentLen><EncryptInfo>" + sign
					+ "</EncryptInfo></soapenv:Body></soapenv:Envelope>";
			log.error("请求内容:"+envelope);
			String result = post(streamNo, apiUrl, envelope);
			Map<String, String> map = readXmlToMap(result);
			log.error("返回内容:" + map);
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
			}catch (IOException e) {
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
