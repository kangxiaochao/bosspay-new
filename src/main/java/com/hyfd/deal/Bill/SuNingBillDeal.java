package com.hyfd.deal.Bill;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeAddRequest;
import com.suning.api.entity.operasale.AgentrechargeAddResponse;
import com.suning.api.exception.SuningApiException;

public class SuNingBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(SuNingBillDeal.class);

	@Override
	@SuppressWarnings("unchecked")
	public synchronized Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");	// 手机号
			Double fee = (Double) order.get("fee");//金额，以元为单位
			String spec = fee.intValue() + ""; //只取整数部分

			Map<String, Object> channel = (Map<String, Object>) order.get("channel");	// 获取通道参数
			String linkUrl = channel.get("link_url").toString(); 						// 充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
			String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
			String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
			String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID
//			Thread.sleep(50);//线程休眠，防止批量提单导致生成的订单号一致。
			// 生成自己的id，供回调时查询数据使用,上游要求格式：代理商编码+YYYYMMDD+8位流水号
			String curids = channelId + DateUtils.getNowTimeToMS().substring(0, 16);
			map.put("orderId", curids);

			String resultStr = rechargeAddRequest(linkUrl, appKey, appSecret, key, channelId, curids, phoneNo, spec);
			if (null == resultStr || resultStr.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "苏宁话费充值,号码[" + phoneNo + "],金额[" + spec + "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			} else {
				JSONObject resultJson = valiResult(resultStr);
				boolean state = resultJson.getBoolean("state");
				if (state) {
					String status = resultJson.getString("respCode");
					String respMsg = resultJson.getString("respMsg");
					if (status.equals("0000")) {
						map.put("resultCode", respMsg);
						flag = 1;	// 提交成功
					} else {
						map.put("resultCode", respMsg);
						flag = -1;	// 提交异常
					}
				} else {
					String errorCode = resultJson.getString("error_code");
					String errorMsg = resultJson.getString("error_msg");
					
					map.put("resultCode", errorCode+":" + errorMsg);
					flag = 0;	// 提交失败
				}
			}
		} catch (Exception e) {
			log.error("苏宁视通话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		
		map.put("status", flag);
		return map;
	}

	/**
	 * <h5>功能:充值请求</h5>
	 * 
	 * @author zhangpj	@date 2018年11月13日
	 * @param serverUrl 请求地址
	 * @param appKey 平台appKey
	 * @param appSecret 平台appSecret
	 * @param key 接口key
	 * @param channelId 代理商编码ID
	 * @param reqSerial 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
	 * @param serialNumber 待充值的用户号码
	 * @param feeAmount 充值金额,单位:元
	 * @return 
	 */
	public String rechargeAddRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber, String feeAmount) {
		String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
		String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
		
		AgentrechargeAddRequest request = new AgentrechargeAddRequest();
		request.setChannelId(channelId);
		request.setFeeAmount(feeAmount);
		request.setReqSerial(reqSerial);
		request.setReqTime(reqTime);
		request.setReqSign(sign);
		request.setSerialNumber(serialNumber);
		// api入参校验逻辑开关，当测试稳定之后建议设置为 false,或者删除该行
//		request.setCheckParam(true);

		DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,	appSecret, "json");
		String resultStr = "";
		try {
			AgentrechargeAddResponse response = client.excute(request);
			resultStr = response.getBody();
//			System.out.println("返回json/xml格式数据 :" + resultStr);
		} catch (SuningApiException e) {
			e.printStackTrace();
		}
		return resultStr;
	}
	
	/**
	 * <h5>功能:处理充值请求返回结果</h5>
	 * 
	 * @author zhangpj	@date 2018年11月13日
	 * @param
	 * @return 成功state返回true,失败state返回false
	 */
	public JSONObject valiResult(String resultStr){
		JSONObject jsonObj = JSONObject.parseObject(resultStr);
		JSONObject resultJson = null;
		try {
			JSONObject snBodyJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body");
			if (null != snBodyJson) {
				resultJson = snBodyJson.getJSONObject("addAgentrecharge");
				resultJson.put("state", true);
			} else {
				resultJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_error");
				resultJson.put("state", false);
			}
		} catch (Exception e) {
			log.error("苏宁充值返回信息解析发生异常,返回数据为[" + resultStr + "]");
		}
		return resultJson;
	}
}
