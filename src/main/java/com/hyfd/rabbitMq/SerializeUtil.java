package com.hyfd.rabbitMq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Message;

/**
 * @作者 杰哥
 * @创建日期 2012-10-12
 * @创建时间 上午10:48:41
 * @版本号 V 1.0
 */
public class SerializeUtil {
	
	/**
	 * @功能描述：	将字符串反序列化成对象
	 *
	 * @作者：zhangpj		@创建时间：2016年11月9日
	 * @param serStr
	 * @return
	 */
	public static Object getObjFromStr(String serStr){
		try {
			String redStr = URLDecoder.decode(serStr, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					redStr.getBytes("ISO-8859-1"));
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			Object result = objectInputStream.readObject();
			objectInputStream.close();
			byteArrayInputStream.close();
			
			return result;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * @功能描述：	将对象序列化成字符串
	 *
	 * @作者：zhangpj		@创建时间：2016年11月9日
	 * @param obj
	 * @return
	 */
	public static String getStrFromObj(Object obj){
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream;
			String serStr = "";
			objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			serStr = byteArrayOutputStream.toString("ISO-8859-1");
			serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
			
			objectOutputStream.close();
			byteArrayOutputStream.close();
			
			return serStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 反序列化消息为map<String,Object>对象
	 * 
	 * @param message
	 * @return Map<String,Object>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getObjMapFromMessage(Message message){
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String body = new String(message.getBody(),"UTF-8");
			map = (Map<String,Object>)getObjFromStr(body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 反序列化消息为map<String,String>对象
	 * 
	 * @param message
	 * @return Map<String,String>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getStrMapFromMessage(Message message){
		Map<String,String> map = new HashMap<String,String>();
		try {
			String body = new String(message.getBody(),"UTF-8");
			map = (Map<String,String>)getObjFromStr(body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
