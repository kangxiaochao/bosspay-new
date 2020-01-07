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
import java.util.Random;

import org.apache.http.HttpResponse;
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
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class JianHangBillDeal implements BaseDeal{

	public static Logger log = Logger.getLogger(JianHangBillDeal.class);
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String resultCode = "";
		try{
			String phone = order.get("phone")+"";//手机号
			String orderId = "JHQ"+DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS")+phone;
			@SuppressWarnings("unchecked")
			Map<String,Object> card = (Map<String, Object>) order.get("card");
			log.error(phone+"获取的卡密数据为"+MapUtils.toString(card));
			if(card != null){
				String cardId = (String) card.get("cardId");//卡号
				String cardPass = (String) card.get("cardPass");//密码
				@SuppressWarnings("unchecked")
				Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
				String defaultParameter = (String) channel.get("default_parameter");//默认参数
				Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
				String verifyUrl = paramMap.get("verifyUrl");
				String chargeUrl = paramMap.get("chargeUrl");
				String userId = genUserId();
				map.put("providerOrderId", userId);
				String url = chargeUrl + "?couponCode="+cardPass+"&mobile="+phone+"&smsCode="+getCode(verifyUrl, userId)+"&user_id="+userId+"&actId=url_old";
				String result = ToolHttp.get(true, url);
				JSONObject json = JSONObject.parseObject(result);
				boolean chargeFlag = json.getBooleanValue("success");
				resultCode = result;
				if(chargeFlag){
					flag = 3;
					card.put("useState", 1);
					card.put("orderId", order.get("id"));
					card.put("resultMsg", "充值成功"+result);
					card.put("useTime", new Date());
				}else{
					flag = 4;
					card.put("useState", 2);//初始化卡密状态
					card.put("orderId", order.get("id"));
					card.put("resultMsg", "充值失败"+result);
					card.put("failCount", (int)card.get("failCount")+1);
					card.put("useTime", new Date());
				}
				map.put("card", card);
			}else{
				flag = 0;
				resultCode = "未获取到卡密信息，提交失败";
			}
			map.put("orderId", orderId);
			map.put("status", flag);
			map.put("resultCode", resultCode);
		}catch(Exception e){
			log.error("建行兑换券充值出现异常"+e.getMessage()+MapUtils.toString(order));
		}
		return map;
	}
	
	public static String genUserId() {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();// 随机用以下三个随机生成器
		Random randdata = new Random();
		int data = 0;
		for (int i = 0; i < 12; i++) {
			int index = rand.nextInt(2);
			// 目的是随机选择生成数字，大小写字母
			switch (index) {
			case 0:
				data = randdata.nextInt(10);// 仅仅会生成0~9
				sb.append(data);
				break;
			case 1:
				data = randdata.nextInt(26) + 97;// 保证只会产生65~90之间的整数
				sb.append((char) data);
				break;
			}
		}
		return sb.toString();
	}
	
	public static String getCode(String verifyUrl, String userId) {
		String code_url = verifyUrl + "?user_id="
				+ userId;
		HttpResponse response = httpGet(code_url, null);
		InputStream in = null;
		String result = "";
		try {
			in = response.getEntity().getContent();
			result = PostPic("feixiaoge", "xiaofei1314", "893496", "1902", "0", "0", "fxg", IOUtils.toByteArray(in));
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jobject = JSONObject.parseObject(result);
		String code = jobject.getString("pic_str");
		return code;
	}

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
			log.error(e.getMessage());
		}
		return null;
	}

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
	
}
