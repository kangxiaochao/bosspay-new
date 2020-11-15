package com.hyfd.test;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class haiHangBiTow {
	public static void main(String[] args) {
		String str = "[SESSION=NzMyYjQ3MDQtZTljMi00MWViLTkwZDQtMTY4ZjUyYzc0MmZj; Path=/; SameSite=Lax]";
		System.out.println(str.indexOf("[")+1+"---"+ str.indexOf(";"));
		String sub = str.substring(str.indexOf("[")+1, str.indexOf("; "));
        System.out.println(sub);
		
//		try {
//			testLogin();
//		} catch (IOException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
	}
	
	 public static void testLogin() throws IOException {
	        //时间戳
	        long timestamp = new Date().getTime();
	        //请求地址
	        String url = "https://www.10044.cn/SaleWeb/www/html/index.html?time=" + timestamp;
	        String url2 = "https://www.10044.cn/SaleWeb/sale/qryStaffOrg.do";
	        HttpClient client = new HttpClient();
	        //post请求方式
	        PostMethod postMethod = new PostMethod(url2);
	        //推荐的数据存储方式,类似key-value形式
	        NameValuePair telPair = new NameValuePair();
	        telPair.setName("randomNum");
	        telPair.setValue("944");
	        NameValuePair pwdPair = new NameValuePair("serviceUrl",url);
	        //封装请求参数
	        postMethod.setRequestBody(new NameValuePair[]{telPair,pwdPair});
	        //这里是设置请求内容为json格式,根据站点的格式决定
	        //因为这个网站会将账号密码转为json格式,所以需要这一步
	        postMethod.setRequestHeader("Content_Type","application/json");
	        //执行请求
	        client.executeMethod(postMethod);
	        //通过Post/GetMethod对象获取响应头信息
	        String cookie = postMethod.getResponseHeader("Set-Cookie").getValue();
	        //截取需要的内容
	        String sub = cookie.substring(cookie.indexOf("&"), cookie.lastIndexOf("&"));
	        String[] splitPwd = sub.split("=");
	        String pwd = splitPwd[1];
	        System.out.println(pwd);
	    }
}
