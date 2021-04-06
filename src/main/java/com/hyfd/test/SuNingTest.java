package com.hyfd.test;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeAddRequest;
import com.suning.api.entity.operasale.AgentrechargeAddResponse;
import com.suning.api.entity.operasale.AgentrechargeGetRequest;
import com.suning.api.entity.operasale.AgentrechargeGetResponse;
import com.suning.api.exception.SuningApiException;

public class SuNingTest {
	public static void main(String[] args) {
		String serverUrl = "https://open.suning.com/api/http/sopRequest";	//测试地址
		String appKey = "598ad28ab8e529ac5f6ef6b3f1323665";						// 平台appKey
		String appSecret = "b5653d3b70ef8328b7bf8dcd4ee00c26";					// 平台appSecret
		String key = "jSLFU6iXjkJtR8Yp";	// 接口key
		String channelId = "10160383";		// 代理商编码
		String reqSerial = "101603832021040309470400";							// 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
		String serialNumber = "16523616999";// 待查询的用户号码
		String resultStr = rechargeGetRequest(serverUrl, appKey, appSecret, key, channelId, reqSerial, serialNumber);

		System.out.println("resultStr = " + resultStr);
	}


	public static String rechargeGetRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber) {
		String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
		String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名

		AgentrechargeGetRequest request = new AgentrechargeGetRequest();
		request.setChannelId(channelId);
		request.setReqSerial(reqSerial);
		request.setReqTime(reqTime);
		request.setReqSign(sign);
		request.setSerialNumber(serialNumber);

		//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
//		request.setCheckParam(true);

		DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
		String resultStr = "";
		try {
			AgentrechargeGetResponse response = client.excute(request);
			resultStr = response.getBody();
			System.out.println("返回json/xml格式数据 :" + resultStr);
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
		return resultStr;
	}






	/**
	 * <h5>功能:订单提交</h5>
	 * 
	 * @author zhangpj	@date 2018年11月12日 
	 */
	public static void rechargeAddRequestTest() {
		// String serverUrl = "https://open.suning.com/api/http/sopRequest";	//生产地址
		String serverUrl = "https://openpre.cnsuning.com/api/http/sopRequest";	//测试地址
		String appKey = "9d2770a883c62dfadd02df257a64f45c";						// 平台appKey
		String appSecret = "44dfed1476af6316fa2fa286e2f28937";					// 平台appSecret
		String key = "530aQaLJBlQB1PLV";	// 接口key
		String channelId = "10138380";		// 代理商编码
		String reqSerial = channelId + DateUtils.getNowTimeToMS().substring(0, 16);	// 充值请求流水号，格式：代理商编码+YYYYMMDD+8位流水号
		String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
		String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
		String serialNumber = "17092586666";// 待充值的用户号码
		String feeAmount = "10";			// 充值金额，单位：元
		
		AgentrechargeAddRequest request = new AgentrechargeAddRequest();
		request.setChannelId(channelId);
		request.setFeeAmount(feeAmount);
		request.setReqSerial(reqSerial);
		request.setReqTime(reqTime);
		request.setReqSign(sign);
		request.setSerialNumber(serialNumber);
		// api入参校验逻辑开关，当测试稳定之后建议设置为 false,或者删除该行
		request.setCheckParam(true);

		DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,	appSecret, "json");
		try {
			AgentrechargeAddResponse response = client.excute(request);
			String result = response.getBody();
			System.out.println("返回json/xml格式数据 :" + result);
			getResult(result);
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <h5>功能:订单查询</h5>
	 * 
	 * @author zhangpj	@date 2018年11月12日 
	 */
	public static void rechargeGetRequestTest() {
		String serverUrl = "https://openpre.cnsuning.com/api/http/sopRequest";	//测试地址
		String appKey = "9d2770a883c62dfadd02df257a64f45c";						// 平台appKey
		String appSecret = "44dfed1476af6316fa2fa286e2f28937";					// 平台appSecret
		String key = "530aQaLJBlQB1PLV";	// 接口key
		String channelId = "10138380";		// 代理商编码
		String reqSerial = "101383802018111216545215";								// 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
		String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
		String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
		String serialNumber = "17092586666";// 待查询的用户号码
		
		AgentrechargeGetRequest request = new AgentrechargeGetRequest();
		request.setChannelId(channelId);
		request.setReqSerial(reqSerial);
		request.setReqTime(reqTime);
		request.setReqSign(sign);
		request.setSerialNumber(serialNumber);
		
		//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
		request.setCheckParam(true);
		
		DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
		try {
			AgentrechargeGetResponse response = client.excute(request);
			String result = response.getBody();
			System.out.println("返回json/xml格式数据 :" + result);
			getResult(result);
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getResult(String result){
		JSONObject jsonObj = JSONObject.parseObject(result);
		JSONObject resultJson = null;
		try {
			JSONObject snBodyJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body");
			if (null != snBodyJson) {
				resultJson = snBodyJson.getJSONObject("getAgentrecharge");
				resultJson.put("state", true);
			} else {
				resultJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_error");
				resultJson.put("state", false);
			}
		} catch (Exception e) {
			System.out.println("解析出错了");
		}
		System.out.println(resultJson.toJSONString());
		return resultJson;
	}
}
