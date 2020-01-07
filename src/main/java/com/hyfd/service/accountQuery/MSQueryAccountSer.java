package com.hyfd.service.accountQuery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolString;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseAccountQueryInterface;

@Service
public class MSQueryAccountSer implements BaseAccountQueryInterface {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	public static Logger log = Logger.getLogger(MSQueryAccountSer.class);

	public String query() {
		String id = "2000000013";// 民生物流通道ID ~~~~~
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
		String defaultParameter = (String) channel.get("default_parameter");// 默认参数
		Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		String url = paramMap.get("url");// 查询地址
		String appkey = paramMap.get("key");
		JSONObject paramJson = new JSONObject();
		paramJson.put("interFace", "cr-Query-Pool-Balance");
		paramJson.put("key", appkey);
		String sign = DigestUtils.md5Hex(paramJson.toString());
		String searchResult = post(url, paramJson.toJSONString(), null, appkey, sign);
		System.out.println(searchResult);
		return null;
	}
	
	public static void main(String[] args){
		String url = "http://boss.ms170.cn/msService/";// 查询地址
		String appkey = "kAiCBue1xZvdmKRTyhSPl/o";
		JSONObject paramJson = new JSONObject();
		paramJson.put("interFace", "cr-Query-Pool-Balance");
		paramJson.put("key", appkey);
		String sign = DigestUtils.md5Hex(paramJson.toString());
		String searchResult = post(url, paramJson.toJSONString(), null, appkey, sign);
		System.out.println(searchResult);
	}

	public static String post(String url, String data, String contentType, String appkey, String sign) {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("sign", sign);
			if (null != data) {
				StringEntity stringEntity = new StringEntity(data, ToolString.encoding);
				stringEntity.setContentEncoding(ToolString.encoding);
				if (null != contentType) {
					stringEntity.setContentType(contentType);
				} else {
					stringEntity.setContentType("application/json");
				}
				httpPost.setEntity(stringEntity);
			}

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000)
					.build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);

			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, ToolString.encoding);
					return out;
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error("ToolHttp.java  post()-------UnsupportedEncodingException Exception:" + e.toString());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			log.error("ToolHttp.java  post()-------ClientProtocolException Exception:" + e.toString());
			e.printStackTrace();
			log.error("连接超时：" + url);
		} catch (IOException e) {
			log.error("ToolHttp.java  post()-------IOException Exception:" + e.toString());
			e.printStackTrace();
			log.error("IO异常:" + url);
		} finally {
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("ToolHttp.java  post()-------httpClient.close() Exception:" + e.toString());

			}
		}
		return null;
	}

}
