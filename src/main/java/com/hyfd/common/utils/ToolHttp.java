package com.hyfd.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * HTTP请求相关
 * 
 * @author
 */
public abstract class ToolHttp {

	private static Logger log = Logger.getLogger(ToolHttp.class);

	public final static int OUT_TIME = 30000; // 设置请求和传输超时时间

	/**
	 * HTTP请求方法GET
	 */
	public static final String http_method_get = "GET";

	/**
	 * HTTP请求方法POST
	 */
	public static final String http_method_post = "POST";

	/**
	 * 编码
	 */
	public static final String encoding = "UTF-8";

	/**
	 * 进行HttpClient get连接
	 * 
	 * @param isHttps
	 *            是否ssl链接
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String get(boolean isHttps, String url) {
		log.error("准备执行网络请求 get()-------:isHttps=" + isHttps + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			log.debug("执行请求的url---" + url + "向下家回调返回的状态码---" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取状态行
				// System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("get response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("执行请求的url---" + url + "出现异常："+e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("执行请求的url---" + url + "httpClient.close()异常");
			}
		}
		return null;
	}

	/**
	 * 进行HttpClient get连接
	 * 
	 * @param isHttps
	 *            是否ssl链接
	 * @param url
	 * @return
	 */
	public static String get1(boolean isHttps, String url) {
		log.error("准备执行网络请求 get()-------:isHttps=" + isHttps + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try {
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取状态行
				// System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, ToolString.encoding);
					log.error("get response content|" + out);
					return out;
				}
			}
		} catch (ClientProtocolException e) {
			log.error("ToolHttp.java get()-------ClientProtocolException Exception:" + e.toString());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			log.error("ToolHttp.java get()-------IOException Exception:" + e.toString());
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("httpClient.close()异常");
			}
		}
		return null;
	}

	/**
	 * <h5>功能:</h5>进行HttpClient get连接
	 * 
	 * @author zhangpj @date 2016年8月24日
	 * @param isHttps
	 *            是否ssl链接
	 * @param headerMap
	 *            headerMap 请求头Header
	 * @param url
	 *            请求地址
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String get(boolean isHttps, Map<String, String> headerMap, String url){
		log.error("准备执行网络请求 get()-------:isHttps=" + isHttps + "---headerMap=" + headerMap + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			// 设置请求header信息
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// 设置header
					httpget.setHeader(key, headerMap.get(key));
				}
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取状态行
				// System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("get response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("执行请求的url---" + url + "出现异常："+e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("httpClient.close()异常");
			}
		}
		return null;
	}

	/**
	 * 进行HttpClient post连接
	 * 
	 * @param isHttps
	 *            是否ssl链接
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @param contentType
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String post(boolean isHttps, String url, String data, String contentType){
		log.error("准备执行网络请求 post()-------:isHttps=" + isHttps + "----url=" + url + "---data=" + data
				+ "----contentType=" + contentType);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpPost httpPost = new HttpPost(url);
			// (name,
			// value);.addRequestHeader("Content-Type","text/html;charset=UTF-8");
			// httpPost.getParams().setParameter(HttpMethod.HTTP_CONTENT_CHARSET,
			// "UTF-8");

			if (null != data) {
				StringEntity stringEntity = new StringEntity(data, encoding);
				stringEntity.setContentEncoding(encoding);
				if (null != contentType) {
					stringEntity.setContentType(contentType);
				} else {
					stringEntity.setContentType("application/json");
				}
				httpPost.setEntity(stringEntity);
			}

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);

			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("post response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("执行请求的url---" + url + "出现异常："+e.getMessage());
			e.printStackTrace();
		}finally{
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

	/**
	 * <h5>功能:</h5>进行HttpClient post连接
	 * 
	 * @author zhangpj @date 2016年8月24日
	 * @param isHttps
	 *            是否ssl链接
	 * @param headerMap
	 *            请求头Header
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @param contentType
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String post(boolean isHttps, Map<String, String> headerMap, String url, String data,
			String contentType) {
		log.error("准备执行网络请求 post()-------:isHttps=" + isHttps + "----headerMap=" + headerMap + "----url=" + url
				+ "---data=" + data + "----contentType=" + contentType);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpPost httpPost = new HttpPost(url);
			// 设置请求header信息
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// 设置header
					httpPost.setHeader(key, headerMap.get(key));
				}
			}
			if (null != data) {
				StringEntity stringEntity = new StringEntity(data, encoding);
				stringEntity.setContentEncoding(encoding);
				if (null != contentType) {
					stringEntity.setContentType(contentType);
				} else {
					stringEntity.setContentType("application/json");
				}
				httpPost.setEntity(stringEntity);
			}

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);

			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("post response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("执行请求的url---" + url + "出现异常："+e.getMessage());
			e.printStackTrace();
		}finally{
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

	/**
	 * <h5>功能:</h5>进行HttpClient post连接
	 * 
	 * @author zhangpj @date 2016年8月24日
	 * @param isHttps
	 *            是否ssl链接
	 * @param headerMap
	 *            请求头Header
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据
	 * @param contentType
	 * @return
	 */
	public static String post4sdyd(boolean isHttps, Map<String, String> headerMap, String url, String data,
			String contentType) {
		log.error("准备执行网络请求 post()-------:isHttps=" + isHttps + "----headerMap=" + headerMap + "----url=" + url
				+ "---data=" + data + "----contentType=" + contentType);
		CloseableHttpClient httpClient = null;
		try {
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient4Sdyd();
			}
			HttpPost httpPost = new HttpPost(url);
			// 设置请求header信息
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// 设置header
					httpPost.setHeader(key, headerMap.get(key));
				}
			}
			if (null != data) {
				StringEntity stringEntity = new StringEntity(data, encoding);
				stringEntity.setContentEncoding(encoding);
				if (null != contentType) {
					stringEntity.setContentType(contentType);
				} else {
					stringEntity.setContentType("application/json");
				}
				httpPost.setEntity(stringEntity);
			}

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// 设置请求和传输超时时间
			httpPost.setConfig(requestConfig);

			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("post response content|" + out);
					return out;
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error("ToolHttp.java  post()-------UnsupportedEncodingException Exception:" + e.toString());
			e.printStackTrace();
			System.out.println(e.toString());
		} catch (ClientProtocolException e) {
			log.error("ToolHttp.java  post()-------ClientProtocolException Exception:" + e.toString());
			e.printStackTrace();
			log.error("连接超时：" + url);
			System.out.println("连接超时" + e.toString());
		} catch (Exception e) {
			log.error("ToolHttp.java  post()-------Exception Exception:" + e.toString());
			e.printStackTrace();
			log.error("IO异常:" + url);
			System.out.println("IO异常" + e.toString());
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

	/**
	 * @throws Exception @Title:山东电信post @Description:
	 * TODO(这里用一句话描述这个方法的作用) @author CXJ @date 2017年4月10日
	 * 上午9:53:11 @param @param url @param @param map @param @param
	 * charset @param @return @return String 返回类型 @throws
	 */
	public static String post4sddx(String url, Map<String, String> map, String charset) throws Exception {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		httpClient = new SSLClient();
		httpPost = new HttpPost(url);
		// 设置参数
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> elem = (Entry<String, String>) iterator.next();
			list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
		}
		if (list.size() > 0) {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
			httpPost.setEntity(entity);
		}
		HttpResponse response = httpClient.execute(httpPost);
		if (response != null) {
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				result = EntityUtils.toString(resEntity, charset);
			}
		}
		return result;
	}

	/**
	 * HTTPS访问对象，信任所有证书
	 * 
	 * @return
	 */
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	/**
	 * HTTPS访问对象，信任所有证书
	 * 
	 * @return
	 */
	public static CloseableHttpClient createSSLInsecureClient4Sdyd() {

		SSLContext sslContext = custom("D:\\soft\\JDK8\\jre\\lib\\security\\pdata.keystore", "changeit");
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();

		// return HttpClients.createDefault();
	}

	/**
	 * 设置信任自签名证书
	 * 
	 * @param keyStorePath
	 *            密钥库路径
	 * @param keyStorepass
	 *            密钥库密码
	 * @return
	 */
	public static SSLContext custom(String keyStorePath, String keyStorepass) {
		SSLContext sc = null;
		FileInputStream instream = null;
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			instream = new FileInputStream(new File(keyStorePath));
			trustStore.load(instream, keyStorepass.toCharArray());
			// 相信自己的CA和所有自签名的证书
			sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| KeyManagementException e) {
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
			}
		}
		return sc;
	}

	/**
	 * 原生方式请求
	 * 
	 * @param isHttps
	 *            是否https
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return
	 */
	public static String httpRequest(boolean isHttps, String requestUrl, String requestMethod, String outputStr) {
		HttpURLConnection conn = null;

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		PrintWriter printWriter = null;

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (isHttps) {
				HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
				// 创建SSLContext对象，并使用我们指定的信任管理器初始化
				TrustManager[] tm = { new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// 检查客户端证书
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// 检查服务器端证书
					}

					public X509Certificate[] getAcceptedIssuers() {
						// 返回受信任的X509证书数组
						return null;
					}
				} };
				SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new java.security.SecureRandom());
				SSLSocketFactory ssf = sslContext.getSocketFactory();// 从上述SSLContext对象中得到SSLSocketFactory对象
				httpsConn.setSSLSocketFactory(ssf);
				conn = httpsConn;
			}

			// 超时设置，防止 网络异常的情况下，可能会导致程序僵死而不继续往下执行
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);

			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			conn.setDoOutput(true);

			// 设置是否从httpUrlConnection读入，默认情况下是true;
			conn.setDoInput(true);

			// Post 请求不能使用缓存
			conn.setUseCaches(false);

			// 设定传送的内容类型是可序列化的java对象
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

			// 设置请求方式（GET/POST），默认是GET
			conn.setRequestMethod(requestMethod);

			// 连接，上面对urlConn的所有配置必须要在connect之前完成，
			conn.connect();

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				outputStream = conn.getOutputStream();
				outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				printWriter = new PrintWriter(outputStreamWriter);
				printWriter.write(outputStr);
				printWriter.flush();
				printWriter.close();
			}

			// 从输入流读取返回内容
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuilder buffer = new StringBuilder();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str).append("\n");
			}

			return buffer.toString();
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
			return null;

		} catch (Exception e) {
			log.error("https请求异常：{}", e);
			return null;

		} finally { // 释放资源
			if (null != outputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("outputStream.close()异常", e);
				}
				outputStream = null;
			}

			if (null != outputStreamWriter) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					log.error("outputStreamWriter.close()异常", e);
				}
				outputStreamWriter = null;
			}

			if (null != printWriter) {
				printWriter.close();
				printWriter = null;
			}

			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.error("bufferedReader.close()异常", e);
				}
				bufferedReader = null;
			}

			if (null != inputStreamReader) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					log.error("inputStreamReader.close()异常", e);
				}
				inputStreamReader = null;
			}

			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("inputStream.close()异常", e);
				}
				inputStream = null;
			}

			if (null != conn) {
				conn.disconnect();
				conn = null;
			}
		}
	}

}
