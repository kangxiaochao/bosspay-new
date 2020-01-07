package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.DixintongKeyDao;
import com.hyfd.deal.BaseDeal;

public class DiXinTongBillDeal implements BaseDeal{

	private static int OUT_TIME = 30000;//超时时间
	
	Logger log = Logger.getLogger(getClass());
	
	// 响应码
	static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("0000", "成功");
		rltMap.put("9001", "发起方组件认证失败");
		rltMap.put("9002", "校验发起方消息报文控制头格式错误");
		rltMap.put("9003", "校验发起方消息报文业务包体格式错误");
		rltMap.put("9004", "校验落地方应答的消息报文控制头格式错误");
		rltMap.put("9005", "校验落地方应答的消息报文业务包体格式错误");
		rltMap.put("9006", "校验落地方消息报文格式错误");
		rltMap.put("9007", "落地方机构检查请求报文格式出错");
		rltMap.put("9008", "落地方交换节点发现报文 Message Header格式错误");
		rltMap.put("9009", "落地方交换节点发现报文 Message Body格式错误");
		rltMap.put("9010", "落地方机构尚未在枢纽登记此业务或者不存");
		rltMap.put("9011", "落地方机构未开通");
		rltMap.put("9012", "落地方机构未签到");
		rltMap.put("9013", "落地方机构网络不通");
		rltMap.put("9014", "落地方机构系统僵死，稍后再访问");
		rltMap.put("9015", "落地方机构系统流量过大，请稍后访问");
		rltMap.put("9016", "等待落地方应答超时");
		rltMap.put("9017", "请求超过流量控制");
		rltMap.put("9018", "交易流水号重复");
		rltMap.put("9019", "消息文件格式枢纽检验不通");
		rltMap.put("9020", "发起方尚未开通此功能");
		rltMap.put("9021", "落地方尚未开通此功能");
		rltMap.put("9022", "发起方请求报文过大");
		rltMap.put("9023", "请求报文名称重复");
		rltMap.put("9024", "报文不是 xml文件");
		rltMap.put("9025", "报文名称不符合规范");
		rltMap.put("9026", "SecretKey已过期");
		rltMap.put("9027", "数字签名不正确");
		rltMap.put("9030", "没找到跟落地方应答报文对");
		rltMap.put("9031", "落地方应答报文名与目录或");
		rltMap.put("9032", "没有找到对应的发起方流水");
		rltMap.put("9033", "落地方应答报文不是 xml 文");
		rltMap.put("9034", "落地方应答报文过大");
		rltMap.put("9035", "落地方报文名称不符合规范");
		rltMap.put("9036", "发起方报文名与目录或节点");
		rltMap.put("9040", "标准业务对象操作 (SOO)");
		rltMap.put("9986", "落地方系统暂时故障，稍后");
		rltMap.put("9987", "规定时间内请求数量超过阀");
		rltMap.put("9988", "规定时间内请求流量超过阀");
		rltMap.put("9989", "枢纽系统正在维护中 ...");
		rltMap.put("9990", "落地方超时或服务不存在");
		rltMap.put("9991", "消息流内部错误");
		rltMap.put("9999", "枢纽内部系统错误");
		rltMap.put("X999", "对于没有定义的错误代码暂时使用 X999X999 ，其中 X表示业分类代码");
		rltMap.put("1999", "系统平台内部错误（所有的级在这里体现 )");
		rltMap.put("1990", "校验业务体格式错误（节点名称）（可在 校验业务体格式错误（节点名称）（可在 respDesc详细描述）");
		rltMap.put("1991", "所填参数主据中没有定义（哪个节点的值可在respDesc详细描述）");
		rltMap.put("1992", "选节点为空或未填值（名称在 respDesc详细描述）");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phone = (String) order.get("phone");//手机号
			String fee = order.get("fee")+"";//金额，以元为单位
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String appKey = paramMap.get("appId");// 分配给第三方系统的AppKey
//			String secretKey = paramMap.get("merchantKey");// 密钥
			String secretKey = (String) order.get("secretKey");
			String method = paramMap.get("method");// API接口名称
			String version = paramMap.get("version");// 接口版本
			String destType = paramMap.get("destType");// 被充值的用户属性-
			String unitTye = paramMap.get("unitTye");// 充值单位类型
			String curids = appKey+DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + GenerateData.getIntData(9,9);// 交易流水号，每笔交易均不一样
			map.put("orderId", curids);
			String transactionId = curids;	// 交易流水号
			String reqSerial = curids;		// 充值请求流水号
			String reqTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");// 请求时间
			String feeAmount = new Double(fee).intValue()*100+"";// 金额 单位：分
			String destCode = phone;// 被充值的业务号码
			String bonusUnitTye = "0";
			String bonusAmount = "0";
			String xml = createRequestXML(appKey, secretKey, method, transactionId, reqTime, version, reqSerial, destCode, destType, unitTye, feeAmount, bonusUnitTye, bonusAmount);
			
			String resultData = sslPost(linkUrl,xml);//发送请求
			log.error("迪信通充值返回：" + resultData);
			if(resultData!=null&&!resultData.equals("")){//提交不为空
				Map<String,String> resultMap = readXmlToMap(resultData);//将xml格式的数据解析成map
				String code = resultMap.get("respCode");//获取状态码
				map.put("resultCode", code+":"+rltMap.get(code));
				transactionId = resultMap.get("transactionId");
				map.put("providerOrderId", transactionId);
				if(code.equals("0000")){//提交成功
					flag = 3;
				}else{//提交失败
					flag = 4;
				}
			}
		}catch(Exception e){
			log.error("迪信通充值逻辑出错"+e.getMessage()+"||"+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	
	
	/**
	 * * 根据参数拼成上游所需xml字符串
	 * 
	 * @param appKey
	 *            分配给第三方系统的AppKey
	 * @param method
	 *            API接口名称 recharge.charge
	 * @param transactionId
	 *            交易流水号，每笔交易均不一样
	 * @param reqTime
	 *            请求时间
	 * @param sign
	 *            签名字符串
	 * @param version
	 *            接口版本
	 * @param reqSerial
	 *            充值请求流水号
	 * @param destCode
	 *            被充值的业务号码
	 * @param destType
	 *            被充值的用户属性
	 * @param unitTye
	 *            充值单位类型
	 * @param feeAmount
	 *            充值金额
	 * @param bounsUnitTye
	 *            赠送 充值单位类型
	 * @param bonusAmount
	 *            赠送 金额
	 * @return
	 */
	public String createRequestXML(String appKey, String secretKey, String method, String transactionId, String reqTime,
			String version, String reqSerial, String destCode, String destType, String unitTye, String feeAmount, 
			String bonusUnitTye, String bonusAmount) {

		StringBuffer signXml = new StringBuffer();
		signXml.append("<Body>");
		signXml.append("<RechargeBalanceReq>");
		signXml.append("<reqSerial>" + reqSerial + "</reqSerial>");
		signXml.append("<destCode>" + destCode + "</destCode>");
		signXml.append("<destType>" + destType + "</destType>");
		signXml.append("<unitTye>" + unitTye + "</unitTye>");
		signXml.append("<feeAmount>" + feeAmount + "</feeAmount>");
		signXml.append("<Bonus>");
		signXml.append("<unitTye>" + bonusUnitTye + "</unitTye>");
		signXml.append("<bonusAmount>" + bonusAmount + "</bonusAmount>");
		signXml.append("</Bonus>");
		signXml.append("</RechargeBalanceReq>");
		signXml.append("</Body>");
		String mingwen = transactionId + "" + signXml + "" + secretKey;
		String sign = null;
		try {
			sign = MD5.ToMD5(mingwen);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("dixintong create request md5 error|"+e.getMessage());
		}

		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<Root>");
		xml.append("<Head>");
		xml.append("<appKey>" + appKey + "</appKey>");
		xml.append("<method>" + method + "</method>");
		xml.append("<transactionId>" + transactionId + "</transactionId>");
		xml.append("<reqTime>" + reqTime + "</reqTime>");
		xml.append("<sign>" + sign + "</sign>");
		xml.append("<version>"+version+"</version>");
		xml.append("</Head>");

		String str = xml.toString() + signXml.toString() + "</Root>";
		log.error("迪信通请求报文：" + str);
		return str;
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

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME)
				.setConnectTimeout(OUT_TIME).build();// 设置请求和传输超时时间
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
			log.error("dixintong sslPost error|" + e.getMessage());
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
	 * 解析返回值
	 * @author lks 2018年1月10日下午2:41:04
	 * @param xml
	 * @return
	 */
	public static Map<String, String> readXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element headElt = rootElt.element("Head");
			List<Element> l = headElt.elements();
			for (Iterator<Element> iterator = l.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				if("Response".equals(element.getName())){
					List<Element> rl = element.elements();
					for (Iterator<Element> it = rl.iterator(); it.hasNext();) {
						Element e = (Element) it.next();
						map.put(e.getName(), e.getStringValue());
					}
				}else{
					map.put(element.getName(), element.getStringValue());
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
