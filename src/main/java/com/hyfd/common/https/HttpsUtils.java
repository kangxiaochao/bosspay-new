package com.hyfd.common.https;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons
 * -io-2.4.jar
 * 
 * @author 费振龙
 * 
 */
@SuppressWarnings("deprecation")
public class HttpsUtils {

	public static final int connTimeout = 10000;
	public static final int readTimeout = 10000;
	public static final String mimeType = "text/plain";
	public static final String charset = "UTF-8";
	private static HttpClient client = null;
	/**
	 * 初始化日志输出
	 */
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(HttpsUtils.class);
	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(128);
		cm.setDefaultMaxPerRoute(128);
		client = HttpClients.custom().setConnectionManager(cm).build();
	}

	public static String postParameters(String url, String parameterStr)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		return post(url, parameterStr, "application/x-www-form-urlencoded",
				charset, connTimeout, readTimeout);
	}

	public static String postParameters(String url, String parameterStr,
			String charset, Integer connTimeout, Integer readTimeout)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		return post(url, parameterStr, "application/x-www-form-urlencoded",
				charset, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		return postForm(url, params, null, connTimeout, readTimeout);
	}

	public static String postParameters(String url, Map<String, String> params,
			Integer connTimeout, Integer readTimeout)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		return postForm(url, params, null, connTimeout, readTimeout);
	}

	public static String get(String url) throws Exception {
		return get(url, charset, null, null);
	}

	public static String get(String url, String charset) throws Exception {
		return get(url, charset, connTimeout, readTimeout);
	}

	/**
	 * 发送一个 Post 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/xml "application/x-www-form-urlencoded"
	 *            a=1&b=2&c=3
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String url, String body, String mimeType,
			String charset, Integer connTimeout, Integer readTimeout)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		String result = "";
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.create(
						mimeType, charset));
				post.setEntity(entity);
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());

			HttpResponse res;
			if (url.startsWith("https")) {
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else {
				// 执行 Http 请求.
				client = HttpsUtils.client;
				res = client.execute(post);
			}
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 发送一个 Post 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/xml "application/x-www-form-urlencoded"
	 *            a=1&b=2&c=3
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String post(String url, String body, String token,
			String signatrue, String mimeType, String charset,
			Integer connTimeout, Integer readTimeout)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		String result = "";
		try {
			if (StringUtils.isNotBlank(body)) {
				HttpEntity entity = new StringEntity(body, ContentType.create(
						mimeType, charset));
				post.setEntity(entity);
			}
			post.addHeader("4GGOGO-Auth-Token", token);
			post.addHeader("HTTP-X-4GGOGO-Signature", signatrue);
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());

			HttpResponse res;
			if (url.startsWith("https")) {
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else {
				// 执行 Http 请求.
				client = HttpsUtils.client;
				res = client.execute(post);
			}
			System.out.println("状态码：" + JSONObject.toJSONString(res.getStatusLine()));
			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 发送一个 Get 请求, 使用指定的字符集编码.
	 * 
	 * @param url
	 * @param body
	 *            RequestBody
	 * @param mimeType
	 *            例如 application/xml "application/x-www-form-urlencoded"
	 *            a=1&b=2&c=3
	 * @param charset
	 *            编码
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return ResponseBody, 使用指定的字符集编码.
	 * @throws ConnectTimeoutException
	 *             建立链接超时异常
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String doGet(String url, String body, String token,
			String signatrue, String mimeType, String charset)
			throws ConnectTimeoutException, SocketTimeoutException, Exception {
		HttpClient client = null;
		HttpGet method = new HttpGet(url);
		String result = "";
		try {
			method.addHeader("Content-Type", mimeType);
			method.addHeader("4GGOGO-Auth-Token", token);
			method.addHeader("HTTP-X-4GGOGO-Signature", signatrue);

			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			customReqConf.setConnectTimeout(connTimeout);
			customReqConf.setSocketTimeout(readTimeout);
			method.setConfig(customReqConf.build());
			System.out.println("请求内容：" + JSONObject.toJSONString(method));
			HttpResponse res;
			if (url.startsWith("https")) {
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(method);
			} else {
				// 执行 Http 请求.
				client = HttpsUtils.client;
				res = client.execute(method);
			}
			System.out.println("状态码：" + JSONObject.toJSONString(res.getStatusLine()));

			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			method.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 提交form表单
	 * 
	 * @param url
	 * @param params
	 * @param connTimeout
	 * @param readTimeout
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public static String postForm(String url, Map<String, String> params,
			Map<String, String> headers, Integer connTimeout,
			Integer readTimeout) throws ConnectTimeoutException,
			SocketTimeoutException, Exception {

		HttpClient client = null;
		HttpPost post = new HttpPost(url);
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();
				Set<Entry<String, String>> entrySet = params.entrySet();
				for (Entry<String, String> entry : entrySet) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, Consts.UTF_8);
				post.setEntity(entity);
			}

			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			post.setConfig(customReqConf.build());
			HttpResponse res = null;
			if (url.startsWith("https")) {
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(post);
			} else {
				// 执行 Http 请求.
				client = HttpsUtils.client;
				res = client.execute(post);
			}
			return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
		} finally {
			post.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
	}

	/**
	 * 发送一个 GET 请求
	 * 
	 * @param url
	 * @param charset
	 * @param connTimeout
	 *            建立链接超时时间,毫秒.
	 * @param readTimeout
	 *            响应超时时间,毫秒.
	 * @return
	 * @throws ConnectTimeoutException
	 *             建立链接超时
	 * @throws SocketTimeoutException
	 *             响应超时
	 * @throws Exception
	 */
	public static String get(String url, String charset, Integer connTimeout,
			Integer readTimeout) throws ConnectTimeoutException,
			SocketTimeoutException, Exception {

		HttpClient client = null;
		HttpGet get = new HttpGet(url);
		String result = "";
		try {
			// 设置参数
			Builder customReqConf = RequestConfig.custom();
			if (connTimeout != null) {
				customReqConf.setConnectTimeout(connTimeout);
			}
			if (readTimeout != null) {
				customReqConf.setSocketTimeout(readTimeout);
			}
			get.setConfig(customReqConf.build());

			HttpResponse res = null;

			if (url.startsWith("https")) {
				// 执行 Https 请求.
				client = createSSLInsecureClient();
				res = client.execute(get);
			} else {
				// 执行 Http 请求.
				client = HttpsUtils.client;
				res = client.execute(get);
			}

			result = IOUtils.toString(res.getEntity().getContent(), charset);
		} finally {
			get.releaseConnection();
			if (url.startsWith("https") && client != null
					&& client instanceof CloseableHttpClient) {
				((CloseableHttpClient) client).close();
			}
		}
		return result;
	}

	/**
	 * 从 response 里获取 charset
	 * 
	 * @param ressponse
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getCharsetFromResponse(HttpResponse ressponse) {
		// Content-Type:text/html; charset=GBK
		if (ressponse.getEntity() != null
				&& ressponse.getEntity().getContentType() != null
				&& ressponse.getEntity().getContentType().getValue() != null) {
			String contentType = ressponse.getEntity().getContentType()
					.getValue();
			if (contentType.contains("charset=")) {
				return contentType
						.substring(contentType.indexOf("charset=") + 8);
			}
		}
		return null;
	}

	/**
	 * 创建 SSL连接
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static CloseableHttpClient createSSLInsecureClient()
			throws GeneralSecurityException {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, new X509HostnameVerifier() {

						@Override
						public boolean verify(String arg0, SSLSession arg1) {
							return true;
						}

						@Override
						public void verify(String host, SSLSocket ssl)
								throws IOException {
						}

						@Override
						public void verify(String host, X509Certificate cert)
								throws SSLException {
						}

						@Override
						public void verify(String host, String[] cns,
								String[] subjectAlts) throws SSLException {
						}

					});

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} catch (GeneralSecurityException e) {
			throw e;
		}
	}

	/**
	 * 通过post请求服务器获取一个json数据
	 * 
	 * @param url
	 *            请求地址
	 * @param map
	 *            请求参数
	 * @return 请求结果
	 */
	public static JSONObject getUrlToJson(String url, Map<String, String> map) {
		String ret = "";

		System.out.println("请求时间："
				+ HttpsDateUtils.getDate2LStr(HttpsDateUtils.getCurrentDate())
				+ "毫秒数：" + HttpsDateUtils.getCurTimeMillis() + "\n请求地址：" + url
				+ "\n请求参数：" + map);
		JSONObject jo;
		try {
			ret = HttpsUtils.postForm(url, map, null, connTimeout, readTimeout);
			System.out.println("成功：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容：" + ret);
			jo = JSONObject.parseObject(ret);
			// 记录请求成功

		} catch (ConnectTimeoutException e) {
			jo = null;
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		} catch (SocketTimeoutException e) {
			jo = null;
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		} catch (Exception e) {
			jo = null;
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		}
		return jo;
	}

	/**
	 * 通过post请求服务器获取一个字符串
	 * 
	 * @param url
	 *            请求地址
	 * @param map
	 *            请求参数
	 * @return 请求结果
	 */
	public static String getUrlToString(String url, Map<String, String> map) {
		String ret = "";
		System.out.println("请求时间："
				+ HttpsDateUtils.getDate2LStr(HttpsDateUtils.getCurrentDate())
				+ "毫秒数：" + HttpsDateUtils.getCurTimeMillis() + "\n请求地址：" + url
				+ "\n请求参数：" + map);
		try {
			ret = HttpsUtils.postForm(url, map, null, connTimeout, readTimeout);
			System.out.println("成功：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容：" + ret);

		} catch (ConnectTimeoutException e) {
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		} catch (SocketTimeoutException e) {
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		} catch (Exception e) {
			// 记录请求失败
			System.out.println("异常：响应时间："
					+ HttpsDateUtils.getDate2LStr(HttpsDateUtils
							.getCurrentDate()) + "毫秒数："
					+ HttpsDateUtils.getCurTimeMillis() + "\n响应内容："
					+ e.getMessage());
		}
		return ret;
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, String> sortMap = new TreeMap<String, String>(
				new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						// TODO Auto-generated method stub
						return o1.compareTo(o2);
					}
				});

		sortMap.putAll(map);

		return sortMap;
	}
}