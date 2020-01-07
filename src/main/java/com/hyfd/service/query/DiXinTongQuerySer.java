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
import com.asiainfo.crm.inter.common.util.DecryptUtil;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class DiXinTongQuerySer extends BaseService
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
		
		boolean userBalanceFlag=false;
		boolean userSimpleInfoFlag=false;
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Map<String, String> amountInfoMap=new HashMap<String, String>();
		String channelId = "2000000004";
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(channelId);
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		
		String chargeAmountQryUrl=paramMap.get("chargeAmountQryUrl"); //取出查询接口方法
		String httpPubKey = paramMap.get("httpPubKey");		
		String httpPriKey = paramMap.get("httpPriKey");
		String httpOrgId = paramMap.get("httpOrgId");
		String httpOpId = paramMap.get("httpOpId");
		String httpGetAccountBalanceBusiCode = paramMap.get("httpGetAccountBalanceBusiCode");
		String httpGetUserSimpleInfoBusiCode = paramMap.get("httpGetUserSimpleInfoBusiCode");
		String InterfaceId="";
		String httpInterfaceType = paramMap.get("httpInterfaceType");
		String httpInfoType = paramMap.get("httpInfoType");
		String httpRegionCode = paramMap.get("httpRegionCode");
		String httpCountryCode = paramMap.get("httpCountryCode");
		String httpClientIP = paramMap.get("httpClientIP");
		
		
		String TransactionId=GenerateData.getCharAndNumr(14);
		String TransactionTime=format.format(new Date());
		
		Map<String, String> userSimpleInfoParMap=new HashMap<String, String>();
		userSimpleInfoParMap.put("BusiCode", httpGetUserSimpleInfoBusiCode);
		userSimpleInfoParMap.put("ServiceNum", mobileNumber);
		userSimpleInfoParMap.put("InfoType", httpInfoType);
		
		userSimpleInfoParMap.put("TransactionId", TransactionId);
		userSimpleInfoParMap.put("TransactionTime", TransactionTime);
		userSimpleInfoParMap.put("InterfaceId", InterfaceId);
		userSimpleInfoParMap.put("InterfaceType", httpInterfaceType);
		userSimpleInfoParMap.put("OpId", httpOpId);
		userSimpleInfoParMap.put("OrgId", httpOrgId);
		userSimpleInfoParMap.put("RegionCode", httpRegionCode);
		userSimpleInfoParMap.put("CountryCode", httpCountryCode);
		userSimpleInfoParMap.put("ClientIP", httpClientIP);
		
		String userSimpleInfoJson=getUserSimpleInfoJson(userSimpleInfoParMap);
		
		try {
			userSimpleInfoJson=DecryptUtil.encryptDES3(httpPubKey, httpPriKey, userSimpleInfoJson);
			log.error("has encode|"+userSimpleInfoJson);
			String userSimpleResponseStr=ToolHttps.post(false, chargeAmountQryUrl, userSimpleInfoJson, "text/json; charset=UTF-8");
			log.error(userSimpleResponseStr);
			
			if(userSimpleResponseStr!=null && (!"".equals(userSimpleResponseStr))){
				String userSimpleInfoResponse=DecryptUtil.decryptDES3(httpPubKey, httpPriKey, userSimpleResponseStr);
				log.error(userSimpleInfoResponse);
				Map<String, String> userSimpleInfoMap=getUserSimpleInfoMap(userSimpleInfoResponse);
				log.error(userSimpleInfoMap);
				amountInfoMap.put("phoneownername", userSimpleInfoMap.get("CustName")==null?"未知":userSimpleInfoMap.get("CustName"));
				userSimpleInfoFlag=true;
			}else{
				userSimpleInfoFlag=false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			userSimpleInfoFlag=false;
		}
		

		Map<String, String> accountBalanceParMap=new HashMap<String, String>();
		accountBalanceParMap.put("BusiCode", httpGetAccountBalanceBusiCode);
		accountBalanceParMap.put("ServiceNum", mobileNumber);
		accountBalanceParMap.put("AccId", "");
		
		accountBalanceParMap.put("TransactionId", TransactionId);
		accountBalanceParMap.put("TransactionTime", TransactionTime);
		accountBalanceParMap.put("InterfaceId", InterfaceId);
		accountBalanceParMap.put("InterfaceType", httpInterfaceType);
		accountBalanceParMap.put("OpId", httpOpId);
		accountBalanceParMap.put("OrgId", httpOrgId);
		accountBalanceParMap.put("RegionCode", httpRegionCode);
		accountBalanceParMap.put("CountryCode", httpCountryCode);
		accountBalanceParMap.put("ClientIP", httpClientIP);
		String accountBalanceJson=getAccountBalanceJson(accountBalanceParMap);
		
		log.error("no encode|"+accountBalanceJson);
		
		try {
			accountBalanceJson=DecryptUtil.encryptDES3(httpPubKey, httpPriKey, accountBalanceJson);
			log.error("has encode|"+accountBalanceJson);
			String accountBalanceResponseStr=ToolHttps.post(false, chargeAmountQryUrl, accountBalanceJson, "text/json; charset=UTF-8");
			log.error(accountBalanceResponseStr);
			if(accountBalanceJson!=null && !"".equals(accountBalanceResponseStr)){
			String myNewContent=DecryptUtil.decryptDES3(httpPubKey, httpPriKey, accountBalanceResponseStr);
			log.error(myNewContent);
			Map<String, String> accountBalanceMap=getAccountBalanceMap(myNewContent);
			log.error(accountBalanceMap);
			amountInfoMap.put("amount", accountBalanceMap.get("RealBalance"));
			userBalanceFlag=true;
			}else{
				userBalanceFlag=false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			userBalanceFlag=false;
		}
		
		if(userBalanceFlag==true || userSimpleInfoFlag==true){
			amountInfoMap.put("status", "0"); //成功
		}else{
			amountInfoMap.put("status", "1"); //失败
		}
		
		return amountInfoMap;		
	}
	
	public static String getAccountBalanceJson(Map<String, String> parMap){
		
		JSONObject accountBalaceJson = new JSONObject();

		//------------------------------------------start requestJson
		//构造requestJson
		JSONObject requestJson = new JSONObject();
		
		//构造requestJson子元素busiParamsJson
		JSONObject busiParamsJson = new JSONObject();
		busiParamsJson.put("ServiceNum", parMap.get("ServiceNum"));
		busiParamsJson.put("AccId", parMap.get("AccId"));
		
		requestJson.put("BusiCode", parMap.get("BusiCode"));
		requestJson.put("BusiParams", busiParamsJson);
		
		System.out.println(requestJson);
		
		//------------------------------------------end requestJson
		//------------------------------------------start pubInfoJson
		//构造requestJson
		JSONObject pubInfoJson = new JSONObject();
		pubInfoJson.put("TransactionId", parMap.get("TransactionId"));
		pubInfoJson.put("TransactionTime", parMap.get("TransactionTime"));
		pubInfoJson.put("InterfaceId", parMap.get("InterfaceId"));
		pubInfoJson.put("InterfaceType", parMap.get("InterfaceType"));
		pubInfoJson.put("OpId", parMap.get("OpId"));
		pubInfoJson.put("OrgId", parMap.get("OrgId"));
		pubInfoJson.put("RegionCode", parMap.get("RegionCode"));
		pubInfoJson.put("CountryCode", parMap.get("CountryCode"));
		pubInfoJson.put("ClientIP", parMap.get("ClientIP"));
		//------------------------------------------start pubInfoJson
		
		accountBalaceJson.put("Request", requestJson);
		accountBalaceJson.put("PubInfo", pubInfoJson);
		return accountBalaceJson.toString();
	}
	
	public static Map<String, String> getAccountBalanceMap(String accountBalanceJson){
		Map<String, String> resultMap=new HashMap<String, String>();
		String RealBalance="0";
		try{
			JSONObject resultJson= JSON.parseObject(accountBalanceJson);
			RealBalance= resultJson.getJSONObject("Response").getJSONObject("RetInfo").getString("RealBalance");
			resultMap.put("RealBalance", RealBalance);
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("RealBalance", "获取迪信通余额信息失败");
		}
		return resultMap;
	}
	
	public String getUserSimpleInfoJson(Map<String, String> parMap){
		JSONObject userSimpleInfoJson = new JSONObject();
		//------------------------------------------start requestJson
		//构造requestJson
		JSONObject requestJson = new JSONObject();
		
		//构造requestJson子元素busiParamsJson
		JSONObject busiParamsJson = new JSONObject();
		busiParamsJson.put("ServiceNum", parMap.get("ServiceNum"));
		busiParamsJson.put("InfoType", parMap.get("InfoType"));
		
		requestJson.put("BusiCode", parMap.get("BusiCode"));
		requestJson.put("BusiParams", busiParamsJson);
		
		log.error(requestJson);
		
		//------------------------------------------end requestJson
		
		//------------------------------------------start pubInfoJson
		//构造requestJson
		JSONObject pubInfoJson = new JSONObject();
		pubInfoJson.put("TransactionId", parMap.get("TransactionId"));
		pubInfoJson.put("TransactionTime", parMap.get("TransactionTime"));
		pubInfoJson.put("InterfaceId", parMap.get("InterfaceId"));
		pubInfoJson.put("InterfaceType", parMap.get("InterfaceType"));
		pubInfoJson.put("OpId", parMap.get("OpId"));
		pubInfoJson.put("OrgId", parMap.get("OrgId"));
		pubInfoJson.put("RegionCode", parMap.get("RegionCode"));
		pubInfoJson.put("CountryCode", parMap.get("CountryCode"));
		pubInfoJson.put("ClientIP", parMap.get("ClientIP"));
		//------------------------------------------start pubInfoJson
		
		userSimpleInfoJson.put("Request", requestJson);
		userSimpleInfoJson.put("PubInfo", pubInfoJson);
		log.error(userSimpleInfoJson.toString());
		return userSimpleInfoJson.toString();
	}
	
	public static Map<String, String> getUserSimpleInfoMap(String userSimpleInfoJson){
		Map<String, String> resultMap=new HashMap<String, String>();
		String CustName="";
		String AcctId="";
		try{
			JSONObject resultJson= JSON.parseObject(userSimpleInfoJson);
			CustName= resultJson.getJSONObject("Response").getJSONObject("RetInfo").getString("CustName");
			AcctId=resultJson.getJSONObject("Response").getJSONObject("RetInfo").getString("AcctId");
			resultMap.put("CustName", CustName);
			resultMap.put("AcctId", AcctId);
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("CustName", "获取迪信通用户信息失败");
			resultMap.put("AcctId", "");
		}
		return resultMap;
	}
}
