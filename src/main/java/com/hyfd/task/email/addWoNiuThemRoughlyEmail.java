package com.hyfd.task.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.service.mp.PhoneSectionSer;

@Component
public class addWoNiuThemRoughlyEmail {
	private static Logger log = Logger.getLogger(addWoNiuThemRoughlyEmail.class);
	@Autowired 
	PhoneSectionDao phoneSectionDao;
	/**
	 * 定时查询蜗牛号段，如果数据库中没有则添加进数据库
	 */
	@Scheduled(fixedDelay = 1800000)
	public void addThemRoughly() {
		try {
			String result = allSegmentQueries();
			if(result.equals("")) {
				log.error("获取蜗牛手机号段出错，错误的类位置： com.hyfd.task.addWoNiuThemRoughly");
			}else {
				JSONObject resultJson = JSONObject.parseObject(result);						//获取接口返回的数据
				JSONArray jsonArray = JSONArray.parseArray(resultJson.getString("data"));	//获取返回数据中的号段信息
				List<Map<String,String>> listMap = new ArrayList<>();						//用于存储数据库中没有的号段的信息
				Map<String,String> themRoughlyMap = new HashMap<>();						
				Map<String,String> theUnknownMap = new HashMap<>();							//存放添加数据时的固定信息
				theUnknownMap.put("id",UUID.randomUUID().toString().replaceAll("-",""));
				theUnknownMap.put("providerId","0000000028");								//蜗牛的ID
				theUnknownMap.put("providerType","1");					
				theUnknownMap.put("carrierType","4");
				Set set = new HashSet();
				for (int i=0; i<jsonArray.size();i++) {
					JSONObject resultJson2 = jsonArray.getJSONObject(i);
					String MOBILEFRAGMENT = resultJson2.getString("MOBILEFRAGMENT").substring(0, 7);  //手机号段   获取的号段存在大于7位的 截取前7位
					String PROVINCENAME = resultJson2.getString("PROVINCENAME");					  //省份
					String CITYNAME = resultJson2.getString("CITYNAME");				 	  	 	  //城市
					if(set.contains(MOBILEFRAGMENT)) {
					}else {
						set.add(MOBILEFRAGMENT);
						//判断手机号段、省份、城市，是否为null或者“”
						if(!(MOBILEFRAGMENT == null || MOBILEFRAGMENT.equals("") || PROVINCENAME == null || PROVINCENAME.equals("") || CITYNAME == null || CITYNAME.equals(""))) {
							//查询号段是否存在， 存在返回号段信息，不存在返回null	
							Map<String, Object> map = phoneSectionDao.selectBySection(MOBILEFRAGMENT);
							if(map == null || map.equals("")) {
									//存放数据库中没有的号段，通过日志打印出来
									themRoughlyMap.put("MOBILEFRAGMENT",MOBILEFRAGMENT);
									themRoughlyMap.put("PROVINCENAME",PROVINCENAME);
									themRoughlyMap.put("CITYNAME",CITYNAME);
									listMap.add(themRoughlyMap);
									//将没有的号段添加到数据库中
									theUnknownMap.put("section",MOBILEFRAGMENT);
									theUnknownMap.put("provinceCode",PROVINCENAME);
									theUnknownMap.put("cityCode",CITYNAME);
									try {
										int j = phoneSectionDao.insertSelective(theUnknownMap);
									} catch (Exception e) {
										log.error("蜗牛号段添加失败："+theUnknownMap);
									}
							}
						}else {
							log.error("蜗牛号段存在空值空值的信息："+resultJson2);
						}
					
					}
				}
				log.error("添加的号段："+listMap);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	 
	public String allSegmentQueries(){
		try {
			String url = "http://business.api.sandbox.wn/fbi/sms/city/query.do";
			String accessId = "16";												//访问id
			String accessPasswd = "2O3I4J21L2K3J";								//访问密码
			String accessType = "1";											//访问类型						
			String returnType = "json";											//返回类型
			String accesskey = "A223L1J2H3S0DF98SKLJ";							//sgin
			String dataInfo = "{}";												//数据信息
			StringBuffer inputBuffer = new StringBuffer();
			inputBuffer.append(accessId).append(accessPasswd).append(accessType).append(dataInfo).append(returnType).append(accesskey);
			String verifyStr = MD5.ToMD5(inputBuffer.toString()).toUpperCase();
			System.out.println("蜗牛官方转账接口加密后密文："+verifyStr);
			String parameter = "?securityInfo=%7B%22accessId%22:%22"+accessId+"%22,%22accessPasswd%22:%22"+
						accessPasswd+"%22,%22accessType%22:%22"+accessType+"%22,%22returnType%22:%22"+
						returnType+"%22,%22verifyStr%22:%22"+verifyStr+"%22%7D&dataInfo=%7B%7D";
			return ToolHttp.get(false,url+parameter);
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}
}
