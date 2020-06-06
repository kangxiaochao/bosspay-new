package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;

public class LianLianKeJiTask {
	
	public static void main(String[] args) {
			String linkUrl = "http://manage.win10030.com/aapi/order_info";
			String timestamp = String.valueOf(new Date().getTime()/1000);  
			String agent_id = "36";
			String sign_key = "a875315e13040b75b808130938c3e9a6";
			String ts =  Integer.valueOf(timestamp)+"";								//当前时间，格式秒
			String sign = md5Encode(agent_id+ts+sign_key);
			JSONObject json = new JSONObject();
			json.put("agent_id",agent_id);
			json.put("sign",sign);
			json.put("trade_no","362020060616304914599018");
			json.put("ts",ts);
			
			String result = ToolHttp.post(false, linkUrl,json.toJSONString(),null);
			JSONObject jsonObject = JSONObject.parseObject(result);
			System.out.println(jsonObject.toString());
			
	}
	
	/**
	 * MD5加密 
	 * @param str
	 * @return 加密后小写
	 */
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
}
