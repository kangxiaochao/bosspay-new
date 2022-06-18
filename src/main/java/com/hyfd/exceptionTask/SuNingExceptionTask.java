package com.hyfd.exceptionTask;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeGetRequest;
import com.suning.api.entity.operasale.AgentrechargeGetResponse;
import com.suning.api.exception.SuningApiException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @功能描述：	可查询充值记录
 *
 * @作者：zhangpj		@创建时间：2018年5月7日
 */
@Component
public class SuNingExceptionTask implements BaseTask {
    
    private static Logger log = Logger.getLogger(SuNingExceptionTask.class);

	@Override
	public Map<String, Object> task(Map<String, Object> order,Map<String,Object> channelMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String linkUrl = channelMap.get("link_url").toString(); // 查询地址
			String defaultParameter = (String) channelMap.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
			String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
			String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
			String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID
			//获取订单信息
			String phone = order.get("phone") + "";
			String orderId = order.get("orderId") != null ? order.get("orderId")+"" : "";
			//获取返回值信息
			map.put("orderId", orderId);
			map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
			map.put("providerOrderId",order.get("providerOrderId") != null ? order.get("providerOrderId") : "");
			map.put("phone",phone);
			map.put("fee",order.get("fee"));
			// 话费充值结果查询请求
			String resultStr = rechargeGetRequest(linkUrl, appKey, appSecret, key, channelId, orderId, phone);
			log.info(orderId+"苏宁查单接口返回结果:"+resultStr);
			if (null != resultStr && !resultStr.equals("")) {
				// 验证充值查询请求结果
				JSONObject resultJson = valiResult(resultStr);
				boolean state = resultJson.getBoolean("state");
				if (state) {
					// 充值订单状态编码， 0-充值失败 1-充值成功 2-充值订单处理中 3-无充值订单信息
					String code = resultJson.getString("respCode");
					String respMsg = resultJson.getString("respMsg");
					if ("1".equals(code)) {
						map.put("status",1);	// 充值成功
						map.put("resultCode", "充值成功！" );
					} else if ("0".equals(code) || "3".equals(code)) {
						map.put("status",0);	// 充值失败 或 无充值订单信息
						map.put("resultCode", "充值失败："+code + "-" + respMsg);
					} else if ("2".equals(code)) {
						map.put("status",2);	// 充值订单处理中
						map.put("resultCode", "充值中！" );
					} else {
						map.put("status",3);	// 查询订单状态失败
						map.put("resultCode", "未知的订单状态，请与上家核实后处理！");
					}
				}else{
					map.put("status",3);				// 查询订单状态失败
					map.put("resultCode",resultStr);	// 查询结果
				}
			}
		}catch (Exception e){
			log.error("苏宁Task出错" + e);
		}
		return map;
	}


	/**
	 * <h5>功能:话费充值结果查询请求</h5>
	 * 
	 * @author zhangpj	@date 2018年11月13日
	 * @param serverUrl 请求地址
	 * @param appKey 平台appKey
	 * @param appSecret 平台appSecret
	 * @param key 接口key
	 * @param channelId 代理商编码ID
	 * @param reqSerial 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
	 * @param serialNumber 待充值的用户号码
	 * @return 
	 */
	public String rechargeGetRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber) {
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
	 * <h5>功能:验证充值查询请求结果</h5>
	 * 
	 * @author zhangpj	@date 2018年11月13日
	 * @param resultStr
	 * @return 成功state返回true,失败state返回false
	 */
	private JSONObject valiResult(String resultStr){
		JSONObject jsonObj = JSONObject.parseObject(resultStr);
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
