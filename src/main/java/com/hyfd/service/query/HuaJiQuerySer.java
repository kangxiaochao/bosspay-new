package com.hyfd.service.query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.ToolMD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class HuaJiQuerySer extends BaseService
{

	Logger log = Logger.getLogger(getClass());
	
	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    PhoneSectionDao phoneSectionDao;
    
    @Autowired
	ProviderDao providerDao;
    
    /**
	 * 获取余额信息 传入手机号
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap
	 * key:amount 余额 单位元
	 * key:status 查询状态 0查询成功 1查询失败
	 * key:phoneownername 机主姓名
	 */
	public Map<String, String> getChargeAmountInfo(String mobileNumber){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Map<String, String> amountInfoMap=new HashMap<String, String>();
		
		//Map<String, String> channelParameterMap = ChannelInterfaceService.service.cacheGet("100007");
		String id = "2000000010";
        Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
        String defaultParameter = (String)channel.get("default_parameter");// 默认参数
        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
        
		String appKey = paramMap.get("appKey");
		String passWord = paramMap.get("passWord");
		String linkUrl = paramMap.get("linkUrl");
		String timestamp = format.format(new Date());// 格式 ：yyyymmddhhmiss调用时间
		String vserion = paramMap.get("vserion");// 版本号 1.0 目前1.0是固定值。如有变动，上游会通知
		String chargeAmountQryUrl=paramMap.get("chargeAmountQryUrl"); //取出查询接口方法
		String serial = format.format(new Date()) + getFiveSquece();// 流水号，自定义流水
		
		String chargeAmountQryJson=getChargeAmountQryJson(passWord, appKey, chargeAmountQryUrl, timestamp, vserion, serial, mobileNumber);
		
		String queryResultJson=ToolHttps.post(false, linkUrl, chargeAmountQryJson, "json/html");
		
		if(queryResultJson!=null && (!"".equals(queryResultJson)) ){
			Map<String, String> chargeAmountQryResult=getChargeAmountQryResult(queryResultJson);
			amountInfoMap.put("amount", chargeAmountQryResult.get("amount"));
			amountInfoMap.put("phoneownername", chargeAmountQryResult.get("custName"));
			amountInfoMap.put("status", "0"); //成功
		}else{
			amountInfoMap.put("status", "1"); //失败
		}
		
		return amountInfoMap;		
	}
	
	/**
	 * 获取余额查询请求json
	 * @param password
	 * @param appkey
	 * @param method
	 * @param timestamp
	 * @param vserion
	 * @param serial
	 * @param accNbr
	 * @return String
	 */
	public String getChargeAmountQryJson(String password,String appkey,String method,String timestamp,String vserion,String serial,String accNbr){
		JSONObject requestJson = new JSONObject();
		requestJson.put("accNbr", accNbr);

		String signSrc = serial + requestJson.toString() + password;
		String sign = new String();
		try {
			sign = ToolMD5.encodeMD5Hex(signSrc).toUpperCase();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		JSONObject json = new JSONObject();
		json.put("appkey", appkey);
		json.put("method", method);
		json.put("timestamp", timestamp);
		json.put("vserion", vserion);
		json.put("sign", sign);
		json.put("serial", serial);
		json.put("request", requestJson);
		
		log.error(json.toString());
		
		return json.toString();
	}
	
	/**
	 * 获取5位序列码
	 * 
	 * @return
	 */
	public static int getFiveSquece() {
		return (int) ((Math.random() * 9 + 1) * 10000);
	}
	
	/**
	 * 解析余额查询返回的结果为map
	 * @param resultJson
	 * @return
	 */
	public Map<String, String> getChargeAmountQryResult(String resultJson){
		Map<String, String> chargeAmountQryResultMap=new HashMap<String, String>();
		try{
		log.error(resultJson);
		JSONObject result = JSON.parseObject(resultJson);
		String status = result.getString("status");
		String message = result.getString("message");
		String amount=result.getString("amount");
		String serial=result.getString("serial");
		String custName=result.getString("custName");
		
		chargeAmountQryResultMap.put("status", status);
		chargeAmountQryResultMap.put("message", message);
		chargeAmountQryResultMap.put("amount", Double.parseDouble(amount)/100+"");
		chargeAmountQryResultMap.put("serial", serial);
		chargeAmountQryResultMap.put("custName", custName);
		}catch(Exception e){
			log.error(e.getMessage());
		}
		
		return chargeAmountQryResultMap;
	}
}
