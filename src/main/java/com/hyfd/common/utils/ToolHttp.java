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
 * HTTP????????????
 * 
 * @author
 */
public abstract class ToolHttp {

	private static Logger log = Logger.getLogger(ToolHttp.class);

	public final static int OUT_TIME = 30000; // ?????????????????????????????????

	/**
	 * HTTP????????????GET
	 */
	public static final String http_method_get = "GET";

	/**
	 * HTTP????????????POST
	 */
	public static final String http_method_post = "POST";

	/**
	 * ??????
	 */
	public static final String encoding = "UTF-8";

	/**
	 * ??????HttpClient get??????
	 * 
	 * @param isHttps
	 *            ??????ssl??????
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String get(boolean isHttps, String url) {
		log.error("???????????????????????? get()-------:isHttps=" + isHttps + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// ?????????????????????????????????
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			log.debug("???????????????url---" + url + "?????????????????????????????????---" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// ???????????????
				// System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("get response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("???????????????url---" + url + "???????????????"+e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("???????????????url---" + url + "httpClient.close()??????");
			}
		}
		return null;
	}

	/**
	 * ??????HttpClient get??????
	 * 
	 * @param isHttps
	 *            ??????ssl??????
	 * @param url
	 * @return
	 */
	public static String get1(boolean isHttps, String url) {
		log.error("???????????????????????? get()-------:isHttps=" + isHttps + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try {
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// ?????????????????????????????????
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// ???????????????
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
				log.error("httpClient.close()??????");
			}
		}
		return null;
	}

	/**
	 * <h5>??????:</h5>??????HttpClient get??????
	 * 
	 * @author zhangpj @date 2016???8???24???
	 * @param isHttps
	 *            ??????ssl??????
	 * @param headerMap
	 *            headerMap ?????????Header
	 * @param url
	 *            ????????????
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String get(boolean isHttps, Map<String, String> headerMap, String url){
		log.error("???????????????????????? get()-------:isHttps=" + isHttps + "---headerMap=" + headerMap + "----url=" + url);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpGet httpget = new HttpGet(url);
			// ????????????header??????
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// ??????header
					httpget.setHeader(key, headerMap.get(key));
				}
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(OUT_TIME).setConnectTimeout(OUT_TIME)
					.build();// ?????????????????????????????????
			httpget.setConfig(requestConfig);
			// httpget.addHeader(new BasicHeader("", ""));
			// httpget.addHeader("", "");
			CloseableHttpResponse response = httpClient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// ???????????????
				// System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String out = EntityUtils.toString(entity, encoding);
					log.error("get response content|" + out);
					return out;
				}
			}
		}catch(Exception e){
			log.error("???????????????url---" + url + "???????????????"+e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				if (null != httpClient) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error("httpClient.close()??????");
			}
		}
		return null;
	}

	/**
	 * ??????HttpClient post??????
	 * 
	 * @param isHttps
	 *            ??????ssl??????
	 * @param url
	 *            ????????????
	 * @param data
	 *            ????????????
	 * @param contentType
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String post(boolean isHttps, String url, String data, String contentType){
		log.error("???????????????????????? post()-------:isHttps=" + isHttps + "----url=" + url + "---data=" + data
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
					.build();// ?????????????????????????????????
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
			log.error("???????????????url---" + url + "???????????????"+e.getMessage());
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
	 * <h5>??????:</h5>??????HttpClient post??????
	 * 
	 * @author zhangpj @date 2016???8???24???
	 * @param isHttps
	 *            ??????ssl??????
	 * @param headerMap
	 *            ?????????Header
	 * @param url
	 *            ????????????
	 * @param data
	 *            ????????????
	 * @param contentType
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String post(boolean isHttps, Map<String, String> headerMap, String url, String data,
			String contentType) {
		log.error("???????????????????????? post()-------:isHttps=" + isHttps + "----headerMap=" + headerMap + "----url=" + url
				+ "---data=" + data + "----contentType=" + contentType);
		CloseableHttpClient httpClient = null;
		try{
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient();
			}
			HttpPost httpPost = new HttpPost(url);
			// ????????????header??????
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// ??????header
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
					.build();// ?????????????????????????????????
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
			log.error("???????????????url---" + url + "???????????????"+e.getMessage());
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
	 * <h5>??????:</h5>??????HttpClient post??????
	 * 
	 * @author zhangpj @date 2016???8???24???
	 * @param isHttps
	 *            ??????ssl??????
	 * @param headerMap
	 *            ?????????Header
	 * @param url
	 *            ????????????
	 * @param data
	 *            ????????????
	 * @param contentType
	 * @return
	 */
	public static String post4sdyd(boolean isHttps, Map<String, String> headerMap, String url, String data,
			String contentType) {
		log.error("???????????????????????? post()-------:isHttps=" + isHttps + "----headerMap=" + headerMap + "----url=" + url
				+ "---data=" + data + "----contentType=" + contentType);
		CloseableHttpClient httpClient = null;
		try {
			if (!isHttps) {
				httpClient = HttpClients.createDefault();
			} else {
				httpClient = createSSLInsecureClient4Sdyd();
			}
			HttpPost httpPost = new HttpPost(url);
			// ????????????header??????
			if (null != headerMap) {
				for (String key : headerMap.keySet()) {
					// ??????header
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
					.build();// ?????????????????????????????????
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
			log.error("???????????????" + url);
			System.out.println("????????????" + e.toString());
		} catch (Exception e) {
			log.error("ToolHttp.java  post()-------Exception Exception:" + e.toString());
			e.printStackTrace();
			log.error("IO??????:" + url);
			System.out.println("IO??????" + e.toString());
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
	 * @throws Exception @Title:????????????post @Description:
	 * TODO(?????????????????????????????????????????????) @author CXJ @date 2017???4???10???
	 * ??????9:53:11 @param @param url @param @param map @param @param
	 * charset @param @return @return String ???????????? @throws
	 */
	public static String post4sddx(String url, Map<String, String> map, String charset) throws Exception {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		httpClient = new SSLClient();
		httpPost = new HttpPost(url);
		// ????????????
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
	 * HTTPS?????????????????????????????????
	 * 
	 * @return
	 */
	public static CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// ????????????
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
	 * HTTPS?????????????????????????????????
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
	 * ???????????????????????????
	 * 
	 * @param keyStorePath
	 *            ???????????????
	 * @param keyStorepass
	 *            ???????????????
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
			// ???????????????CA???????????????????????????
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
	 * ??????????????????
	 * 
	 * @param isHttps
	 *            ??????https
	 * @param requestUrl
	 *            ????????????
	 * @param requestMethod
	 *            ???????????????GET???POST???
	 * @param outputStr
	 *            ???????????????
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
				// ??????SSLContext?????????????????????????????????????????????????????????
				TrustManager[] tm = { new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// ?????????????????????
					}

					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
						// ????????????????????????
					}

					public X509Certificate[] getAcceptedIssuers() {
						// ??????????????????X509????????????
						return null;
					}
				} };
				SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new java.security.SecureRandom());
				SSLSocketFactory ssf = sslContext.getSocketFactory();// ?????????SSLContext???????????????SSLSocketFactory??????
				httpsConn.setSSLSocketFactory(ssf);
				conn = httpsConn;
			}

			// ????????????????????? ??????????????????????????????????????????????????????????????????????????????
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);

			// ???????????????httpUrlConnection????????????????????????post????????????????????????
			// http??????????????????????????????true, ??????????????????false;
			conn.setDoOutput(true);

			// ???????????????httpUrlConnection???????????????????????????true;
			conn.setDoInput(true);

			// Post ????????????????????????
			conn.setUseCaches(false);

			// ?????????????????????????????????????????????java??????
			// (??????????????????,???????????????????????????,???WEB?????????????????????????????????????????????java.io.EOFException)
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

			// ?????????????????????GET/POST???????????????GET
			conn.setRequestMethod(requestMethod);

			// ??????????????????urlConn???????????????????????????connect???????????????
			conn.connect();

			// ???outputStr??????null????????????????????????
			if (null != outputStr) {
				outputStream = conn.getOutputStream();
				outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
				printWriter = new PrintWriter(outputStreamWriter);
				printWriter.write(outputStr);
				printWriter.flush();
				printWriter.close();
			}

			// ??????????????????????????????
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
			log.error("???????????????{}", ce);
			return null;

		} catch (Exception e) {
			log.error("https???????????????{}", e);
			return null;

		} finally { // ????????????
			if (null != outputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("outputStream.close()??????", e);
				}
				outputStream = null;
			}

			if (null != outputStreamWriter) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					log.error("outputStreamWriter.close()??????", e);
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
					log.error("bufferedReader.close()??????", e);
				}
				bufferedReader = null;
			}

			if (null != inputStreamReader) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					log.error("inputStreamReader.close()??????", e);
				}
				inputStreamReader = null;
			}

			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("inputStream.close()??????", e);
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
