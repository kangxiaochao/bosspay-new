package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;

public class LianLianKeJiTest {

	public static void mainss(String[] args) {
		
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String agent_id = "36";												//商户ID
			String ts =  new Date().getTime()+"";								//当前时间，格式秒
			String sign_key = "a875315e13040b75b808130938c3e9a6";				//加密字符串
			String linkUrl = "http://manage.win10030.com/aapi/charge";			//充值地址
			String mobile = "17006838616";										//充值手机号
			String amount = "10";												//充值金额
			String reply_url = "http://j926e6.natappfree.cc/bosspaybill/status/lianLianKeJiBack";		//回调地址
			//商户订单号
			String trade_no = agent_id + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);
			String sign = md5Encode(agent_id+ts+sign_key);
			JSONObject json = new JSONObject();
			json.put("agent_id",agent_id);
			json.put("ts",ts);
			json.put("sign",sign);
			json.put("trade_no",trade_no);
			json.put("amount",amount);
			json.put("mobile",mobile);
			json.put("reply_url",reply_url);
			
			String result = ToolHttp.post(false, linkUrl,json.toJSONString(),null);
			
//			String result = "{\"error\":0,\"msg\":\"\",\"data\":{\"order_no\":\"test\",\"trade_no\":\"12323\",\" mobile\":\"17006838683\"}}";
			
			if(result == null || result.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "连连科技话费充值,号码[" + mobile + "],金额[" +amount+ "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
//				log.error(msg);
			}else{
				JSONObject jsonObject = JSONObject.parseObject(result);
				String status = jsonObject.getString("error");					//返回码
				String message = jsonObject.getString("msg");					//返回码说明
				JSONObject jsonObject2 = JSONObject.parseObject(jsonObject.getString("data"));
				if(status.equals("0")) {
					map.put("resultCode", status+":"+message);						//执行结果说明
					map.put("providerOrderId",jsonObject2.getString("order_no"));	//的是上家订单号（非必须）
					flag = 1;	// 提交成功
				}else {
					map.put("resultCode", status);
					flag = 0;	// 提交异常
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
//			log.error("连连科技话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status",flag);													
//		return map;
	
	}
	
	
	public static String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
			String s = Integer.toHexString(bytes[i] & 0xff);
			if(s.length()==1){
			buf.append("0");
			}
			buf.append(s);
		}
		}
		catch(Exception ex){	
		}
		return buf.toString();
	}
	
	
	public static void mains(String[] args) {
		
		
		String result = "{\"agent_id\":1,\"ts\":123213,\"sign\":\"66823bd8b590c56668f63f1a7a7b4124\",\"error\":\"0\",\"msg\":\"充值成功\"" + 
				",\"data\":{\"order_no\":\"test\",\"trade_no\":\"106550279510588\",\"mobile\":\"17156682509\"}}";
		JSONObject jsonObject = JSONObject.parseObject(result);
		//取出返回的签名
		String sign = jsonObject.getString("sign");
		String agent_id = jsonObject.getString("agent_id");
		String ts = jsonObject.getString("ts");
		String sign_key = "a875315e13040b75b808130938c3e9a6";				//加密字符串
		String signStr = md5Encode(agent_id+ts+sign_key);
		JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
		
//		log.error("连连科技回调开始：回调信息[" + jsonObject +"]");
		
		System.out.println(signStr);
		
		
		
	}

}
