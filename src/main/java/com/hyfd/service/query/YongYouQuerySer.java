package com.hyfd.service.query;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;
//{"RES_DATA":{"custId":15030,"freezeTotalBalance":0,"acctId":185463,"certType":"居民身份证(临时居民身份证)","myOID":"1555901437265","currentFee":0,"state":"正常","custState":"1","integralValue":0,"stateCode":"1","acctCount":6,"stateFlag":"1","custVipLevel":"","CITYCODE":"760","certNum":"500102********0495","employeeFlag":"0","acctName":"谭超-17012491255","owningFee":0,"custTypeId":"1","custMainNum":"17012491255","creditFee":0,"custName":"谭超","PRIVENCECODE":"76","custType":"个人客户"},"RES_RESULT":"SUCCESS"}
//{"RES_MSG":"success","RES_DATA":{"custId":15030,"could_fee":"-10.84","should_fee":"10.84","this_fee":"10.90","acctId":185463,"used_limit_fee":"42314.820","acct_name":"谭超-17012491255","acct_type_value":"1","limit_fee":"50000.000"},"RES_RESULT":"SUCCESS"}
@Service
public class YongYouQuerySer extends BaseService {
    @Autowired
	CookiesDao cookiesDao;	//登录信息
   	 
	
    static HttpClient httpClient = new HttpClient();
	/**
	 * 获取余额信息，传入手机号
	 * @param mobileNumber
	 * @return
	 * key:amount 余额
	 * key:status 查询状态 0查询成功 1查询失败
	 */
	public Map<String,String> getChargeAmountInfo(String mobileNumber){
		return YongYouQuerySer(mobileNumber.trim());	//删除手机号中的空格
	}

	/**
	 * 用友查询余额
	 * @param phone  查询余额的用友手机号
	 * @return
	 *  status 状态  0： 成功    1：失败
	 *  amount 手机余额  单位 元
	 *  phoneownername 机主姓名
 	 */
	public  Map<String,String> YongYouQuerySer(String phone){
		Map<String,String> returnMap=new HashMap<String,String>();
		String oid = System.currentTimeMillis()+"";
		Map<String, Object> cookies = cookiesDao.selectFirstYYCookie();
		String cookie = cookies.get("cookies")+"";
		String resultStr = queryCustInfo(phone,oid,cookie);
		String resultStr2 = queryCustInfo2(oid,cookie);
		if(resultStr != null && resultStr2 != null && !"".equals(resultStr) && !"".equals(resultStr2)) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			JSONObject jsonObject2 = JSONObject.parseObject(resultStr2);
			String result = jsonObject.getString("RES_RESULT");
			if(result.equals("SUCCESS")) {
//				String custMainNum = phone;		//手机号
//				String custName = jsonObject.getJSONObject("RES_DATA").getString("custName");			//姓名
				String couldFee = jsonObject2.getJSONObject("RES_DATA").getString("could_fee");			//欠费金额
				returnMap.put("status","0");
				returnMap.put("amount",couldFee);
				returnMap.put("phoneownername",phone);
			}else {
				returnMap.put("status","1");
				returnMap.put("amount","0");
				returnMap.put("phoneownername","未知");
			}
		}
		return returnMap;
	}
	/**
	 * 查询当前手机号的信息
	 * @param oid
	 * @param cookies
	 * @return
	 */
	public static String queryCustInfo(String phone,String oid,String cookies){
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/custComprehensive/queryCustInfo");
			NameValuePair[] transferParam = {
	                new NameValuePair("zjType", "num"),
	                new NameValuePair("zjNum",phone),
	                new NameValuePair("zjPwd",""),
	                new NameValuePair("busiOperCode",""),
	                new NameValuePair("myOID",oid),
	                new NameValuePair("pay_HideCustName","no"),
	                new NameValuePair("newAcctId","-1")
	        };
			loginMethod.setRequestHeader("Cookie",cookies);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 查询当前手机号欠费的金额
	 * @param oid
	 * @param c
	 * @return
	 */
	public static String queryCustInfo2(String oid,String c){
		try{
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/pay/queryCustInfo");
			NameValuePair[] transferParam = {
	                new NameValuePair("oid",oid)
	        };
			loginMethod.setRequestHeader("Cookie",c);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	
	
}
