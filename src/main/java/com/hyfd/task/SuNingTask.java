package com.hyfd.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.SocketTool;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeGetRequest;
import com.suning.api.entity.operasale.AgentrechargeGetResponse;
import com.suning.api.exception.SuningApiException;

/**
 * @功能描述：	可查询充值记录
 *
 * @作者：zhangpj		@创建时间：2018年5月7日
 */
@Component
public class SuNingTask {
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(SuNingTask.class);
    
    @Scheduled(fixedDelay = 60000)
	public void querySuNingOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000035";// 中邮物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String linkUrl = channel.get("link_url").toString(); // 查询地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
			String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
			String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
			String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				String phone = order.get("phone") + "";
				
				map.put("orderId", orderId);
				// 话费充值结果查询请求
				String resultStr = rechargeGetRequest(linkUrl, appKey, appSecret, key, channelId, orderId, phone);
				if (null != resultStr && !resultStr.equals("")) {
					// 验证充值查询请求结果
					JSONObject resultJson = valiResult(resultStr);
					boolean state = resultJson.getBoolean("state");
					if (state) {
						// 充值订单状态编码， 0-充值失败 1-充值成功 2-充值订单处理中 3-无充值订单信息
						String code = resultJson.getString("respCode");
						String respMsg = resultJson.getString("respMsg");
						map.put("resultCode", code + ":" + respMsg);
						
						if ("1".equals(code)) {
							flag = 1;	// 充值成功
						} else if ("0".equals(code) || "3".equals(code)) {
							flag = 0;	// 充值失败 或 无充值订单信息
						} if ("2".equals(code)) {
							continue;	// 充值订单处理中
						}
						map.put("status", flag);
						mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,SerializeUtil.getStrFromObj(map));
					}
				}
			}
		} catch (Exception e) {
			log.error("中邮查询Task出错" + e);
		}
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
	 * @param result
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
	
	public static void main(String[] args) {
		String serverUrl = "https://openpre.cnsuning.com/api/http/sopRequest";	//测试地址
		String appKey = "9d2770a883c62dfadd02df257a64f45c";						// 平台appKey
		String appSecret = "44dfed1476af6316fa2fa286e2f28937";					// 平台appSecret
		String key = "530aQaLJBlQB1PLV";	// 接口key
		String channelId = "10138380";		// 代理商编码
		String reqSerial = "101383802018111216545215";							// 充值请求流水号,格式:代理商编码+YYYYMMDD+8位流水号
		String serialNumber = "17092586666";// 待查询的用户号码
		
		SuNingTask snt = new SuNingTask();
//		String resultStr = snt.rechargeGetRequest(serverUrl, appKey, appSecret, key, channelId, reqSerial, serialNumber);
		String resultStr = "{\"sn_responseContent\":{\"sn_body\":{\"getAgentrecharge\":{\"reqSerial\":\"101383802018111216545215\",\"respCode\":\"2\",\"respMsg\":\"充值订单处理中\"}}}}";
//		String resultStr = "{\"sn_responseContent\":{\"sn_body\":{\"getAgentrecharge\":{\"reqSerial\":\"100903752018010100000000\",\"respCode\":\"1\",\"respMsg\":\"充值成功\"}}}}";
//		String resultStr = "{\"sn_responseContent\":{\"sn_error\":{\"error_code\":\"API异常码\",\"error_msg\":\"异常码中文描述\"}}}";
		snt.valiResult(resultStr);
	}
}
