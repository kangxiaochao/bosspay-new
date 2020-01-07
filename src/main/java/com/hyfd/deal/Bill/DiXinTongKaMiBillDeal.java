package com.hyfd.deal.Bill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.deal.BaseDeal;

public class DiXinTongKaMiBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String resultCode = "";
		try{
			String phone = (String) order.get("phone");
			//平台订单号
			String orderId = "DKM"+DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS")+phone;
			//获取卡密数据
			Map<String,Object> card = (Map<String, Object>) order.get("card");
			log.error(phone+"获取的卡密数据为"+MapUtils.toString(card));
			boolean checkCodeFlag = false;
			if(card != null){
				String cardId = (String) card.get("cardId");//卡号
				String cardPass = (String) card.get("cardPass");//密码
				String result = cardRecharge(cardPass, phone);//执行卡密充值请求，并返回结果
				if(result != null && !result.equals("")){
					JSONObject resultJson = JSONObject.parseObject(result);
					if(resultJson.getBooleanValue("success")){
						card.put("useState", 1);
						card.put("orderId", order.get("id"));
						card.put("resultMsg", "提交成功");
						card.put("useTime", new Date());
						flag = 1;
					}else{
						String errorMsg = resultJson.getString("errorMsg");
						if(errorMsg.equals("验证码错误")){
							card.put("useState", 0);//初始化卡密状态
							card.put("orderId", order.get("id"));
							card.put("resultMsg", "验证码错误");
							card.put("failCount", (int)card.get("failCount")+1);
							card.put("useTime", new Date());
							flag = 4;
							checkCodeFlag = true;
						}
					}
					if(!checkCodeFlag){
						Thread.sleep(1000);
						String data = rechargeResult(cardPass);
						if(data != null && !data.equals("")){
							boolean resultFlag = valiRechargeCardResutl(cardId, phone, data);
							if(resultFlag){
								flag = 3;
								card.put("useState", 1);
								card.put("orderId", order.get("id"));
								card.put("resultMsg", "充值成功");
								card.put("useTime", new Date());
							}else{
								flag = 4;
								card.put("useState", 2);//初始化卡密状态
								card.put("orderId", order.get("id"));
								card.put("resultMsg", "充值失败");
								card.put("failCount", (int)card.get("failCount")+1);
								card.put("useTime", new Date());
							}
						}else{
							flag = 1;
							card.put("orderId", orderId);
							card.put("resultMsg", "提交后未查到充值状态");
							card.put("useTime", new Date());
						}
					}
				}
				map.put("card", card);
			}else{//如果卡密数据为空，该笔订单置为失败
				flag = 0;
				resultCode = "未获取到卡密信息，提交失败";
			}
			map.put("orderId", orderId);
			map.put("status", flag);
			map.put("resultCode", resultCode);
		}catch(Exception e){
			log.error("迪信通卡密充值出现异常"+MapUtils.toString(order));
		}
		return map;
	}
	
	/**
	 * @Title:cardRecharge @Description: 迪信通卡密充值(这里用一句话描述这个方法的作用) @author
	 *                     CXJ @date 2017年5月27日 上午10:23:19 @param @param
	 *                     password @param @param
	 *                     phone @param @return @param @throws
	 *                     UnsupportedOperationException @param @throws
	 *                     IOException @return String 返回类型 @throws
	 */
	public static String cardRecharge(String password, String phone) throws UnsupportedOperationException, IOException {

		String cookie = "";
		String code = "";
		String resultStr = "";
		String code_url = "http://www.10026.cn/createVerifyImageServlet?VerifyImageName=CardPayVerify&datetime="
				+ System.currentTimeMillis();

		HttpResponse response = httpGet(code_url, null);
		InputStream in = response.getEntity().getContent();
		String result = PostPic("feixiaoge", "xiaofei1314", "893496", "1902", "0", "0", "fxg", IOUtils.toByteArray(in));
		JSONObject jobject = JSONObject.parseObject(result);
		code = jobject.getString("pic_str");
		Header[] h = response.getHeaders("Set-Cookie");
		for (Header header : h) {
			cookie = cookie + header.getValue() + ";";
		}
		System.out.println("cookie:" + cookie + "   code:" + code);
		if (code != null) {
			String rUrl = "http://www.10026.cn/busicenter/cardPay/HomeCardPayMenuAction/initBusi.menu";
			String name = "CardPayVerify";
			NameValuePair[] data = { new NameValuePair("_menuId", "110"), new NameValuePair("password", password), // 订单号
					new NameValuePair("phone", phone), // 订单号
					new NameValuePair("yanzhengma", code), // 订单号
					new NameValuePair("name", name) };
			resultStr = httpPost(rUrl, data, null, cookie);
		}
		return resultStr;
	}
	
	/**
	 * 卡密充值 Http请求工具类--请求首页
	 */
	public static HttpResponse httpGet(String url, String cookie) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
		httpget.setConfig(requestConfig);
		try {
			// 执行getMethod
			HttpResponse response = httpClient.execute(httpget);
			return response;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 卡密充值 Http请求工具类-充值专用
	 */
	public static String httpPost(String url, NameValuePair[] data, String type, String cookie) {

		HttpClient httpClient = new HttpClient();
		PostMethod method = new PostMethod(url);
		httpClient.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("cookie", cookie);
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		method.setRequestHeader("User-Agent",
				"Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
		if (data != null) {
			method.setRequestBody(data);
		}
		String result = "";
		try {
			int ret = httpClient.executeMethod(method);
			if (ret == 200) {
				result = method.getResponseBodyAsString();
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
	 * 卡密充值验证码_识别图片_按图片二进制流
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
	
	/**
	 * 卡密充值 验证码核心上传函数
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
		String boundary = "----------" + MD5.ToMD5(String.valueOf(time));
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
	
	// 根据充值卡卡密发起查询请求(只需要一个参数即可,其余参数固定)
	public static String rechargeResult(String queryNum) {
		String rechargeResultQuery = "http://www.10026.cn/busicenter/cardPay/HomeCardPayMenuAction/initBusiQueryCardpayRecord.menu?_menuId=1050116&_menuId=1050116&QueryType=CardPwd&EndDate=&StartDate=&QueryNum=";
		String data = ToolHttp.post(false, rechargeResultQuery + queryNum, null, null);
		return data;
	}
	
	// 验证充值是否成功,充值卡卡号,充值号码,查询返回的数据,充值成功返回true,否则返回false
	public static boolean valiRechargeCardResutl(String cardId, String phoneNo, String data) {
		boolean result = false;
		if (null != data && !data.equals("")) {
			int startIndex = data.indexOf("<table");
			int endIndex = data.indexOf("</table");
			data = data.substring(startIndex, endIndex);
			if (data.contains(phoneNo) && data.contains("充值成功")) {
				result = true;
			}
		}
		return result;
	}
	
}
