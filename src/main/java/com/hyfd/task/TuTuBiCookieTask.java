package com.hyfd.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;

//@Component
public class TuTuBiCookieTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	CookiesDao cookiesDao;//登录信息
	
	Logger log = Logger.getLogger(getClass());
//	
//	@Scheduled(fixedDelay = 1800000)
	public void getCookie(){
		String id = "2000000016";
		try{
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String getCodeUrl = paramMap.get("getCodeUrl");
			GetMethod getCodeMethod = new GetMethod(getCodeUrl);
			try {
				HttpClient client = new HttpClient();
				client.executeMethod(getCodeMethod);
				String getCodeResult = getCodeMethod.getResponseBodyAsString();
				JSONObject getCodeJson = JSON.parseObject(getCodeResult);
				boolean getCodeflag = getCodeJson.getBoolean("flag");
				if(getCodeflag){
					String transferCookies = getCodeJson.getString("cookies");
					String msgVerify = getCodeJson.getString("msgVerify");
					Map<String,Object> cookie = new HashMap<String,Object>();
					cookie.put("ids", UUID.randomUUID().toString().replaceAll("-", ""));
					cookie.put("cookies", transferCookies);
					cookie.put("oldcode", msgVerify);
					cookie.put("updatetime", System.currentTimeMillis());
					cookie.put("bz", "TTB");
					int x = cookiesDao.insertSelective(cookie);
					if(x != 1){
						log.error("兔兔币登录信息保存出错，信息为"+MapUtils.toString(cookie));
					}
				}else{
					log.error("兔兔币登陆失败。。。。");
				}
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(Exception e){
			log.error("获取兔兔币的登录信息出错"+e.getMessage());
		}
	}
	
}
