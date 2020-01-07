package com.hyfd.deal.Bill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;


public class HaiHangAssistBillDeal implements BaseDeal{
	
	private static Logger log = Logger.getLogger(HaiHangAssistBillDeal.class);

	public static String cookie = null; // 海航工号充值登录成功标识

	private static boolean threadRun = true; // 海航工号充值处理线程控制

	public static HttpClient httpClient = new HttpClient(); // 海航工号充值请求处理类

	private static boolean threadStart = false; // 海航工号充值处理线程控制
	
	private static int flag = -1;
	
	public static RabbitMqProducer mqProducer;
	
	private static Queue<Map<String, Object>> queue = new ConcurrentLinkedQueue<Map<String, Object>>(); // FIFO（先进先出）原则对元素进行排序
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String phone = (String) order.get("phone");//手机号
			double fee = (double) order.get("fee");//金额，以元为单位
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			// 单位为元
			String spec = BigDecimal.valueOf(fee).intValue()+"";
			// 生成自己的id，供回调时查询数据使用
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 4);
			map.put("orderId", curids);
			// 拼接海航工号充值数据
			Map<String, Object> haiHangMap = new HashMap<String, Object>();
			haiHangMap.put("orderIds", curids);
			// 海航工号充值 充值号码
			haiHangMap.put("msisdn", phone);
			// 海航工号充值 充值金额
			haiHangMap.put("money", spec);
			// 海航工号充值 是否查询
			haiHangMap.put("isQuery", "false");
			// 海航工号充值 用户名称
			haiHangMap.put("loginname", paramMap.get("loginname"));
			// 海航工号充值 用户密码
			haiHangMap.put("password", paramMap.get("password"));
			// 海航工号充值验证码获取地址
			haiHangMap.put("catchaUrl", paramMap.get("catchaUrl") + System.currentTimeMillis());
			// 海航工号充值登录地址
			haiHangMap.put("loginUrl", paramMap.get("loginUrl"));
			// 海航工号充值首页地址
			haiHangMap.put("indexUrl", paramMap.get("indexUrl"));
			// 海航工号充值地址
			haiHangMap.put("rechargeUrl", linkUrl);
			// 海航工号充值查询用户信息地址
			haiHangMap.put("queryUrl", paramMap.get("queryUrl"));
			// 超级鹰 验证码识别用户名
			haiHangMap.put("catchaUsername", paramMap.get("catchaUsername"));
			// 超级鹰 验证码识别用户密码
			haiHangMap.put("catchaPassword", paramMap.get("catchaPassword"));
			// 超级鹰 验证码识别软件编号
			haiHangMap.put("catchaSoftid", paramMap.get("catchaSoftid"));
			// 超级鹰 验证码识别模版编号
			haiHangMap.put("catchaCodetype", paramMap.get("catchaCodetype"));

//			// 将订单数据写入系统
//			haiHangMap.put("orderStatus", orderStatus);
//
//			// 将代理信息写入系统
//			haiHangMap.put("ids", order.getPKValue());
//			haiHangMap.put("agentids", agentids);
//			haiHangMap.put("terminalids", terminalids);
//			haiHangMap.put("agentkk", agentkk);
//			haiHangMap.put("terminalkk", terminalkk);
			
			threadQueueAdd(haiHangMap);
			
			if (!threadStart) {
				log.error("启动海航充值充值处理外挂");
				// 首次加载开启处理进程
				threadStart = true;
				startMoneyDealThread();
			}
			
		} catch (Exception e) {
			log.error("蓝猫充值逻辑出错"+e+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
		
	}
	
	/**
	 * 控制队列线程是否继续
	 * 
	 * @param threadRun
	 */
	public static void setThreadRun(boolean isRun) {
		threadRun = isRun;
	}

	/**
	 * 向队列中增加订单对象，基于ConcurrentLinkedQueue
	 * 
	 * @param order
	 */
	public static void threadQueueAdd(Map<String, Object> map) {
		log.error("海航工号充值:" + map.toString() + "  当前队列总数:" + queue.size());
		queue.offer(map); // 将指定元素插入此队列的尾部
	}

	/**
	 * 获取订单对象，基于ConcurrentLinkedQueue
	 * 
	 * @return order
	 */
	public static Map<String, Object> getThreadQueueObj() {
		return queue.poll(); // 获取并移除此队列的头，如果此队列为空，则返回 null
	}

	/**
	 * 获取海航工号充值cookie
	 * 
	 * @return
	 */
	public static String getCookie() {
		return cookie;
	}

	/**
	 * 设置海航工号充值cookie
	 */
	public static void setCookie(String cookie) {
		HaiHangAssistBillDeal.cookie = cookie;
	}

	/**
	 * 启动订单处理线程
	 */
	public static void startMoneyDealThread() {
		try {
			Thread moneyThread = new Thread(new Runnable() {
				public void run() {
					while (threadRun) {
						try {
							// 取队列数据
							Map<String, Object> map = getThreadQueueObj();
							if (null == map) {
								// 队列为空，等待5秒
								Thread.sleep(30000);
							} else {
								log.error("海航工号充值队列正在处理:" + queue.size());
								threadRecharge(map);
							}
						} catch (Exception e) {
							log.error("订单充值业务异常：" + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			});
			moneyThread.start();
		} catch (Exception e) {
			throw new RuntimeException("Thread moneyThread new Thread Exception");
		}
	}
	
	// 海航模拟充值
		public static int threadRecharge(Map<String, Object> map) {
//			int flag = -1;
			// 请求响应码 200:成功 500:失败 400:未解析到结果 300 系统异常 100:登录异常
			String code = "";
			String msg = "";
			String result = null;
			try {
				if (cookie == null || cookie.equals("")) {
					// 调用一次登录代码
					result = hhLogin(map);
					if (!result.equals("登录成功")) {
						// 登录不成功 结束
						code = "100";
						msg = "登录异常,请联系管理员设置Cookie";
						log.error("code:" + code + " msg:" + msg);
					}
				}

				NameValuePair[] qData = { new NameValuePair("msisdn", map.get("msisdn").toString()) };
				result = httpPost(map.get("queryUrl").toString(), qData, "application/x-www-form-urlencoded");
				JSONObject jObject = JSONObject.parseObject(result).getJSONObject("data");

				NameValuePair[] rechargeData = { new NameValuePair("RECHARGEHNA_ID", ""),
						new NameValuePair("msisdn", map.get("msisdn").toString()), new NameValuePair("isQuery", "false"),
						new NameValuePair("userName", jObject.getString("userName")),
						new NameValuePair("balance", jObject.getString("balance")),
						new NameValuePair("money", map.get("money").toString()) };

				result = httpPost(map.get("rechargeUrl").toString(), rechargeData, "application/x-www-form-urlencoded");
				// 将返回的Html接卸成文本
				//result = Jsoup.parse(result).body().text().replace(" 返回", "");
				result = "";
				if (result.indexOf("充值成功") != -1 && result.indexOf(map.get("msisdn").toString()) != -1) {
					flag = 1;
				} else if (result.indexOf("充值失败") != -1 && result.indexOf(map.get("msisdn").toString()) != -1) {
					flag = 0;
				} else if (result.indexOf("系统错误") != -1) {
					flag = 0;
					cookie=null;
				} else {
					flag = 0;
					cookie=null;
				}
				 Map<String,Object> resultMap = new HashMap<String,Object>();
		    	 resultMap.put("orderId", map.get("orderIds"));
		    	 resultMap.put("status", flag);
		    	 mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(resultMap));
			} catch (Exception e) {
				
				cookie=null;
			}
			return flag;
		}
		/**
		 * 海航工号充登录处理
		 * 
		 * @param obj
		 */
		public static String Login(Map<String, String> map) {

			String code = null;
			String result = "";
			HttpResponse response = httpGet(map.get("catchaUrl").toString());
			if (response != null) {
				int ret = response.getStatusLine().getStatusCode();
				if (ret == HttpStatus.SC_OK) {

					try {
						InputStream in = response.getEntity().getContent();
						result = PostPic(map.get("catchaUsername"), map.get("catchaPassword"),
								map.get("catchaSoftid"), map.get("catchaCodetype"), "0", "0", "fxg",
								IOUtils.toByteArray(in));
						System.out.println("result:" + result);
						JSONObject jobject = JSONObject.parseObject(result);
						if (jobject.getString("err_no").equals("0")) {
							code = jobject.getString("pic_str").toUpperCase();
							cookie = response.getFirstHeader("Set-Cookie").getValue();
							cookie = cookie.substring(0, cookie.indexOf(";"));

							log.error("获取验证码成功:Cookie:" + cookie + " code:" + code);

							NameValuePair[] data = { new NameValuePair("tm", System.currentTimeMillis() + ""), // 订单号
									new NameValuePair("KEYDATA", String.format("%s,%s,%s", map.get("loginname"),
											map.get("password"), code)) };
							result = httpPost(map.get("loginUrl"), data, "application/json;charset=UTF-8");

							if (result.indexOf("success") != -1) {
								// ** 海航登录成功以后必须进入首页，cookie才生效
								httpPost(map.get("indexUrl").toString(), null, "text/html;charset=UTF-8");
								result = "登录成功";
							} else {
								result = "登录失败";
								cookie = null;
							}
						} else {
							result = "0000";
							cookie = null;
						}
					} catch (Exception e) {
						e.getStackTrace();
						result = "0000";
						cookie = null;
					}
				}
			}
			return result;
		}
		/**
		 * 海航工号充登录处理
		 * 
		 * @param obj
		 */
		public static String hhLogin(Map<String, Object> map) {

			String code = null;
			String result = "";
			HttpResponse response = httpGet(map.get("catchaUrl").toString());
			if (response != null) {
				int ret = response.getStatusLine().getStatusCode();
				if (ret == HttpStatus.SC_OK) {

					try {
						InputStream in = response.getEntity().getContent();
						result = PostPic(map.get("catchaUsername").toString(), map.get("catchaPassword").toString(),
								map.get("catchaSoftid").toString(), map.get("catchaCodetype").toString(), "0", "0", "fxg",
								IOUtils.toByteArray(in));
						System.out.println("result:" + result);
						JSONObject jobject = JSONObject.parseObject(result);
						if (jobject.getString("err_no").equals("0")) {
							code = jobject.getString("pic_str").toUpperCase();
							cookie = response.getFirstHeader("Set-Cookie").getValue();
							cookie = cookie.substring(0, cookie.indexOf(";"));

							log.error("获取验证码成功:Cookie:" + cookie + " code:" + code);

							NameValuePair[] data = { new NameValuePair("tm", System.currentTimeMillis() + ""), // 订单号
									new NameValuePair("KEYDATA", String.format("%s,%s,%s", map.get("loginname").toString(),
											map.get("password").toString(), code)) };
							result = httpPost(map.get("loginUrl").toString(), data, "application/json;charset=UTF-8");

							if (result.indexOf("success") != -1) {
								// ** 海航登录成功以后必须进入首页，cookie才生效
								httpPost(map.get("indexUrl").toString(), null, "text/html;charset=UTF-8");
								result = "登录成功";
							} else {
								result = "登录失败";
								cookie = null;
							}
						} else {
							result = "0000";
							cookie = null;
						}
					} catch (Exception e) {
						e.getStackTrace();
						result = "0000";
						cookie = null;
					}
				}
			}
			return result;
		}

		/**
		 * 海航工号充余额查询
		 * 
		 * @param phone
		 *            手机号码
		 */
		public static void querAccInfo(String queryUrl, String phone) {

			if (queryUrl == null || queryUrl.equals("")) {
				queryUrl = "http://171.8.199.108:8282/recharge/rechargeAccInfo";
			}
			NameValuePair[] qData = { new NameValuePair("msisdn", phone) };
			String result = httpPost(queryUrl, qData, "application/x-www-form-urlencoded");
			System.out.println(result);
		}

		/**
		 * Http请求工具类
		 */
		public static HttpResponse httpGet(String url) {

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			try {
				HttpResponse response = httpClient.execute(httpget);
				return response;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return null;
		}

		/**
		 * Http请求工具类
		 */
		public static String httpPost(String url, NameValuePair[] data, String type) {

			PostMethod method = new PostMethod(url);
			httpClient.getParams().setContentCharset("UTF-8");
			method.setRequestHeader("cookie", cookie);
			method.setRequestHeader("ContentType", type);
			if (data != null) {
				method.setRequestBody(data);
			}
			String result = "";
			try {
				int ret = httpClient.executeMethod(method);
				if (ret == 200) {
					result = method.getResponseBodyAsString();
					log.error("URL:" + url + " data:" + data.toString() + " result:" + result);
				} else {
					result = "{\"result\":\"error\"}";
				}
			} catch (IOException e) {
				e.printStackTrace();
				result = "{\"result\":\"error\"}";
			}
			return result;
		}

		/**
		 * 字符串MD5加密
		 * 
		 * @param s
		 *            原始字符串
		 * @return 加密后字符串
		 */
		public static String MD5(String s) {
			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
			try {
				byte[] btInput = s.getBytes();
				MessageDigest mdInst = MessageDigest.getInstance("MD5");
				mdInst.update(btInput);
				byte[] md = mdInst.digest();
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					str[k++] = hexDigits[byte0 & 0xf];
				}
				return new String(str);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * 验证码核心上传函数
		 * 
		 * @param url
		 *            请求URL
		 * @param param
		 *            请求参数，如：username=test&password=1
		 * @param data
		 *            图片二进制流
		 * @return response
		 * @throws IOException
		 */
		public static String httpPostImage(String url, String param, byte[] data) throws IOException {
			long time = (new Date()).getTime();
			URL u = null;
			HttpURLConnection con = null;
			String boundary = "----------" + MD5(String.valueOf(time));
			String boundarybytesString = "\r\n--" + boundary + "\r\n";
			OutputStream out = null;

			u = new URL(url);

			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			// con.setReadTimeout(60000);
			con.setConnectTimeout(60000);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(true);
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			out = con.getOutputStream();

			for (String paramValue : param.split("[&]")) {
				out.write(boundarybytesString.getBytes("UTF-8"));
				String paramString = "Content-Disposition: form-data; name=\"" + paramValue.split("[=]")[0] + "\"\r\n\r\n"
						+ paramValue.split("[=]")[1];
				out.write(paramString.getBytes("UTF-8"));
			}
			out.write(boundarybytesString.getBytes("UTF-8"));

			String paramString = "Content-Disposition: form-data; name=\"userfile\"; filename=\"" + "chaojiying_java.gif"
					+ "\"\r\nContent-Type: application/octet-stream\r\n\r\n";
			out.write(paramString.getBytes("UTF-8"));

			out.write(data);

			String tailer = "\r\n--" + boundary + "--\r\n";
			out.write(tailer.getBytes("UTF-8"));

			out.flush();
			out.close();

			StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}

			return buffer.toString();
		}

		/**
		 * 验证码_识别图片_按图片二进制流
		 * 
		 * @param username
		 *            用户名
		 * @param password
		 *            密码
		 * @param softid
		 *            软件ID
		 * @param codetype
		 *            图片类型
		 * 
		 * @param len_min
		 *            最小位数
		 * @param time_add
		 *            附加时间
		 * @param str_debug
		 *            开发者自定义信息
		 * @param byteArr
		 *            图片二进制数据流
		 * @return
		 * @throws IOException
		 */
		public static String PostPic(String username, String password, String softid, String codetype, String len_min,
				String time_add, String str_debug, byte[] byteArr) {
			String result = "";
			String param = String.format("user=%s&pass=%s&softid=%s&codetype=%s&len_min=%s&time_add=%s&str_debug=%s",
					username, password, softid, codetype, len_min, time_add, str_debug);
			try {
				result = httpPostImage("http://upload.chaojiying.net/Upload/Processing.php", param, byteArr);
			} catch (Exception e) {
				result = "未知问题";
			}

			return result;
		}
}
