package com.hyfd.service.mp;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.stereotype.Service;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.DixintongKeyDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;

@Service
public class DiXinTongKeyService {
	
	private static int OUT_TIME = 120000;// 超时时间
	private static Logger log = Logger.getLogger(DiXinTongKeyService.class);// 日志

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	DixintongKeyDao dixintongKeyDao;// 迪信通密钥key
	
	/**
	 * <h5>功能:自动更新迪信通密钥到数据库</h5>
	 * 
	 * @author zhangpj	@date 2019年3月11日
	 * @param updateType 更新类型:0自动更新;1手动更新 
	 * @return 
	 */
	public String updateDixintongKey(int updateType) {
		String id = "2000000004";
		String result = "更新迪信通卡密失败";
		try {
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);
			String linkUrl = (String) channel.get("link_url");// 更新密钥地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String appKey = paramMap.get("appId");// 分配给第三方系统的AppKey
			String version = paramMap.get("version");// 接口版本
			Map<String, Object> key = dixintongKeyDao.selectRecentKey();
			String expiredTime = (String) key.get("expired");// 获取密钥有效期
			boolean flag = validateExpired(expiredTime); // 验证密钥有效期是否已不足24小时
			if (flag || updateType == 1) {
				String secretKey = (String) key.get("secret_key");// 获取最新密钥
				String reqTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");// 请求时间
				String curids = appKey + DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + GenerateData.getIntData(9, 9);// 交易流水号，每笔交易均不一样
				String xml = createMiyaoXML(appKey, secretKey, curids, reqTime, version);// 获取提交参数
				String resultData = sslPost(linkUrl, xml);// 结果
				log.info("迪信通获取密钥返回数据信息["+resultData+"]");
//				String resultData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Root><Head><actionCode>1</actionCode><transactionId>JNHBXX000120190311153637509136356247</transactionId><respTime>20190311153645</respTime><Response><respType>0</respType><respCode>0000</respCode><respDesc>成功</respDesc></Response></Head><Body><secretKey>nSwLVJiEeRFS</secretKey><expired>20190510153645</expired></Body></Root>";// 结果
				if (resultData != null && !resultData.equals("")) {
					Map<String, String> resultMap = readResultXmlToMap(resultData.trim());
					int ids = Integer.parseInt(key.get("ids").toString());
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("ids", ids);
					map.put("expired", resultMap.get("expired"));
					map.put("secretKey", resultMap.get("secretKey"));
//					dixintongKeyDao.insertSelective(map);
					int updateCount = dixintongKeyDao.updateByPrimaryKeySelective(map);
					if (updateCount == 1) {
						result = "更新迪信通卡密成功";
					}
				}
			}
		} catch (ParseException e) {
			log.error("迪信通验证密钥有效期发生错误" + e.getMessage());
			result = "迪信通验证密钥有效期发生错误";
		} catch (Exception e) {
			log.error("迪信通获取密钥的调度错误" + e.getMessage());
			result = "迪信通获取密钥的调度错误";
		}
		return result;
	}
	
	public static String createMiyaoXML(String appKey, String secretKey,
			String transactionId, String reqTime, String version) {
		StringBuffer signXml = new StringBuffer();
		signXml.append("<Body>");
		signXml.append("</Body>");
		String signStr = transactionId + "" + signXml + "" + secretKey;
		String sign = null;
		try {
			sign = MD5.ToMD5(signStr);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("|" + e.getMessage());
		}

		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<Root>");
		xml.append("<Head>");
		xml.append("<appKey>" + appKey + "</appKey>");
		xml.append("<method>sys.getSecretKey</method>");
		xml.append("<transactionId>" + transactionId + "</transactionId>");
		xml.append("<reqTime>" + reqTime + "</reqTime>");
		xml.append("<sign>" + sign + "</sign>");
		xml.append("<version>V1.0</version>");
		xml.append("</Head>");
		String str = xml.toString() + signXml.toString() + "</Root>";
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
	public static String sslPost(String url, String data) {
		CloseableHttpClient httpClient = createSSLInsecureClient();
		HttpPost httpPost = new HttpPost(url);
		if (null != data) {
			StringEntity stringEntity = new StringEntity(data, "UTF-8");
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/xml");
			httpPost.setEntity(stringEntity);
		}

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME).build();// 设置请求和传输超时时间
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
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						@SuppressWarnings("unused")
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}

						@Override
						public boolean isTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws java.security.cert.CertificateException {
							// TODO Auto-generated method stub
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("dixintong createSSLInsecureClient error|"
					+ e.getMessage());
		}
		return HttpClients.createDefault();
	}

	/**
	 * @功能描述： 验证密钥有效期是否已不足24小时
	 *
	 * @param expiredTime
	 * @return
	 * @throws ParseException
	 *
	 * @作者：zhangpj @创建时间：2018年2月23日
	 */
	private boolean validateExpired(String expiredTime) throws ParseException {
		boolean flag = false;

		String nowTime = DateUtils.getNowTimeToSec();
		Date nowDate = DateTimeUtils.parseDate(nowTime, "yyyyMMddHHmmss");
		Date expiredDate = DateTimeUtils.parseDate(expiredTime, "yyyyMMddHHmmss");
		
		// 返回两个日期之间隔了多少小时
		int hour = ToolDateTime.getDateHourSpace(nowDate, expiredDate);
		if (hour < 24) {
			flag = true;
		}

		return flag;
	}
	
	// 获取更新秘钥返回的信息
	private static Map<String, String> readResultXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element responseElt = rootElt.element("Head").element("Response");
			String respType = responseElt.elementTextTrim("respType");
			if (respType.equals("0")) {
				Element bodyElt = rootElt.element("Body");
				String secretKey = bodyElt.elementTextTrim("secretKey");
				String expired = bodyElt.elementTextTrim("expired");
				map.put("secretKey", secretKey);
				map.put("expired", expired);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return map;
	}

	public static void main(String[] args) {
		String linkUrl = "https://124.202.131.85:9443/oip/HttpAPIService";
		String appKey = "JNHBXX0001";
		String secretKey = "mXElkCGAG6SQ";// 获取最新密钥
		String reqTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");// 请求时间
		String curids = appKey + DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + GenerateData.getIntData(9, 9);// 交易流水号，每笔交易均不一样
		String xml = createMiyaoXML(appKey, secretKey, curids, reqTime, "V1.0");// 获取提交参数
		System.out.println(xml);
		String resultData = sslPost(linkUrl, xml);// 结果
		System.out.println(resultData);
	}
}
