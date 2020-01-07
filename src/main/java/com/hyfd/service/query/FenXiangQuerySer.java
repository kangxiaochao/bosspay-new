package com.hyfd.service.query;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.FXSignUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class FenXiangQuerySer extends BaseService
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
    
    public Map<String, String> getChargeAmountInfo(String mobileNumber) {
		Map<String, String> amountInfoMap = new HashMap<String, String>();

		String id = "2000000027";// 分享物理通道ID ~~~~~
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
		String defaultParameter = (String)channel.get("default_parameter");// 默认参数
		Map<String, String> paramMap1 = XmlUtils.readXmlToMap(defaultParameter);
		String url = paramMap1.get("url");
		String appKey = paramMap1.get("appKey");
		String Method = paramMap1.get("Method");
		String Charge_channel_code = paramMap1.get("account");
		String Req_id = ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + Charge_channel_code
				+ ToolDateTime.format(new Date(), "HHmmssSSS");
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("Method", Method);
		paramMap.put("Charge_channel_code", Charge_channel_code);
		paramMap.put("Req_id", Req_id);
		paramMap.put("Msisdn", mobileNumber);
		String Check_sum_sign = FXSignUtils.md5Digest(paramMap, appKey);
		paramMap.put("Check_sum_sign", Check_sum_sign);
		JSONObject paramJson = getParamJson(paramMap);
		// 获取返回的json
		JSONObject resultJson = postJson(url, paramJson);
		if (resultJson != null) {
			String Resp_code = resultJson.getString("Resp_code");// 返回状态码
			String Resp_description = resultJson.getString("Resp_description");// 返回信息
			String custName = resultJson.getString("CustName");// 用户名
			if (Resp_code.equals("0")) {
				String Bal = resultJson.getString("Balance");// 用户余额
				String Balance = ((Double.valueOf(Bal))/100)+"";
				amountInfoMap.put("status", "0");// 0为成功
				amountInfoMap.put("amount", Balance);
				if (custName != null) {
					amountInfoMap.put("phoneownername", custName);
				} else {
					amountInfoMap.put("phoneownername", "未知");
				}
			} else {
				amountInfoMap.put("status", "1");
				amountInfoMap.put("amount", Resp_description.equals("null") ? "未知" : Resp_description);
			}
		} else {
			log.error("分享的返回值为空或者请求超时");
		}
		return amountInfoMap;

	}
    
    /**
	 * 将请求参数转化为json格式的数据
	 * by lks
	 * */
	public static JSONObject getParamJson(Map<String,String> map){
		JSONObject json = new JSONObject();
		String Method = map.get("Method");
		String Charge_channel_code = map.get("Charge_channel_code");
		String Req_id = map.get("Req_id");
		String ChargeTransfer_req_id = map.get("ChargeTransfer_req_id");
		String Msisdn = map.get("Msisdn");
		String Amount = map.get("Amount");
		String Check_sum_sign = map.get("Check_sum_sign");

		json.put("Method", Method);
		json.put("Charge_channel_code", Charge_channel_code);
		json.put("Req_id", Req_id);
		json.put("Msisdn", Msisdn);
		json.put("Amount", Amount);
		json.put("ChargeTransfer_req_id", ChargeTransfer_req_id);
		json.put("Check_sum_sign", Check_sum_sign);
		return json;
	}
	
	/**
	 * 分享发送请求的方法 
	 * 请求与接收格式都为json格式数据
	 * by lks
	 * */
	public JSONObject postJson(String url,JSONObject json){
		JSONObject result = null;
		try {
			HttpClient  client = new HttpClient ();
			PostMethod method = new PostMethod(url);
			RequestEntity entity = new StringRequestEntity(json.toString(),"application/json","utf-8");//解决中文乱码问题
			method.setRequestEntity(entity);
			 //使用系统提供的默认的恢复策略
	        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
	        //设置超时的时间
	        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			client.executeMethod(method);
			result = JSONObject.parseObject(method.getResponseBodyAsString());
		} catch (HttpException e) {
			log.error("分享请求出错");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			log.error("分享请求出错");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
}
