package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.SocketTool;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 中邮话费业务处理
 * 
 * @author Administrator
 *
 */
public class ZhongYouBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(ZhongYouBillDeal.class);

	/**
	 * 中邮业务处理
	 * 
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			double fee = new Double(order.get("fee") + "");// 金额，以元为单位
			String spec = new Double(fee * 100).intValue() + "";// 充值金额，以分为单位
			
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = (String)channel.get("link_url");// 充值地址
			
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String port = paramMap.get("port");
            String key = paramMap.get("key");
            String busiCode = paramMap.get("BusiCode");
            String interfaceId = paramMap.get("InterfaceId");
            String interfaceType = paramMap.get("InterfaceType");
            String opId = paramMap.get("OpId");
            String countyCode = paramMap.get("CountyCode");
            String orgId = paramMap.get("OrgId");
            String clientIP = paramMap.get("ClientIP");
            String regionCode = paramMap.get("RegionCode");
            String agentAcctId = paramMap.get("AgentAcctId");
            String agentNode = paramMap.get("AgentNode");
            String isCheckPWD = paramMap.get("IsCheckPWD");
            String agentPWD = paramMap.get("AgentPWD");
			
			String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
			String orderId = timeStamp + GenerateData.getIntData(9, 6) + "01";//32位订单号
			map.put("orderId", orderId);
			
			// 组合请求参数
			Map<String, Object> paymentMap= new HashMap<String, Object>();
			paymentMap.put("host", linkUrl);	// ip地址(上游提供)
			paymentMap.put("port", port);	// 端口号(上游提供)
			paymentMap.put("key", key); 	// 密钥(上游提供)
			paymentMap.put("BusiCode", busiCode);// 接口方法名称(上游提供)
			paymentMap.put("InterfaceId", interfaceId);	// (上游提供,暂时为空)
			paymentMap.put("TransactionId", orderId);	// 交易流水
			paymentMap.put("InterfaceType", interfaceType);	// 接口类型(上游提供)
			paymentMap.put("OpId", opId);	// 操作员Id(上游提供)
			paymentMap.put("CountyCode", countyCode);	// 操作员登录县市标识
			paymentMap.put("OrgId", orgId);	// 操作员组织Id
			paymentMap.put("ClientIP", clientIP);	// 请求端IP
			paymentMap.put("TransactionTime", timeStamp);	//交易时间YYYYMMDDH24MISS(上游提供)
			paymentMap.put("RegionCode", regionCode);	// 操作员登录地区标识
			paymentMap.put("AgentAcctId", agentAcctId);	// 代理商帐户编号(上游提供)
			paymentMap.put("AgentNode", agentNode);		// 代理网点(上游提供)
			paymentMap.put("ServiceNum", phoneNo);	// 服务号码,即充值号码
			paymentMap.put("IsCheckPWD", isCheckPWD);	// 是否检查密码,0:不检查密码 1:检查密码
			paymentMap.put("AgentPWD", agentPWD);		// 代理商密码
			paymentMap.put("AuditDate", "");	// 清算日期 YYYYMMDD	不指定清算日期指当天
			paymentMap.put("Amount", spec);	// 充值金额(分)
			paymentMap.put("OppDoneCode", orderId);	// 对端交易流水
			
			// 进行话费充值
			String resultStr = agentCashPayment(paymentMap);
			if (null != resultStr && !resultStr.equals("")) {
				// 检测充值返回数据
				Map<String, Object> resultMap = valiResult(resultStr);
				log.info("中邮话费充值,号码[" + phoneNo + "],金额[" + spec + "(分)],返回数据["+JSONObject.toJSONString(resultMap)+"]");
				map.put("resultCode", resultMap.get("code").toString()+":"+resultMap.get("message").toString());
				if ("0000".equals(resultMap.get("code").toString())) {
					flag = 3;
					String providerOrderId = resultMap.get("paymentId").toString();
					map.put("providerOrderId", providerOrderId);
				} else {
					flag = -1;
				}
			} else {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "中邮话费充值,号码[" + phoneNo + "],金额[" + spec + "(分)],请求超时,接收到返回数据为null";
				map.put("resultCode", msg);
				log.error(msg);
			}
		} catch (Exception e) {
			log.error("中邮[话费充  值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * @功能描述：	进行话费充值
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月22日
	 */
	private static String agentCashPayment(Map<String,Object> map){
		String host = map.get("host").toString();
		int port = Integer.valueOf(map.get("port").toString());
		String key = map.get("key").toString();
		int timeOut = 20000;

		Map<String, Object> pubInfoMap= new LinkedHashMap<String, Object>();
		pubInfoMap.put("InterfaceId", map.get("InterfaceId").toString());	// 接口标识
		pubInfoMap.put("TransactionId", map.get("TransactionId").toString());	// 交易流水
		pubInfoMap.put("InterfaceType", map.get("InterfaceType").toString());	// 接口类型
		pubInfoMap.put("OpId", map.get("OpId").toString());	// 操作员Id
		pubInfoMap.put("CountyCode", map.get("CountyCode").toString());	// 操作员登录县市标识
		pubInfoMap.put("OrgId", map.get("OrgId").toString());	// 操作员组织Id
		pubInfoMap.put("ClientIP", map.get("ClientIP").toString());	// 客户端IP
		pubInfoMap.put("TransactionTime", map.get("TransactionTime").toString());	// 交易时间YYYYMMDDH24MISS
		pubInfoMap.put("RegionCode", map.get("RegionCode").toString());	// 操作员登录地区标识
		
		Map<String, Object> busiParamsMap= new LinkedHashMap<String, Object>();
		busiParamsMap.put("AgentAcctId", map.get("AgentAcctId").toString()); // v1 代理商帐户编号
		busiParamsMap.put("AgentNode", "");	//v2 代理网点	
		busiParamsMap.put("ServiceNum", map.get("ServiceNum").toString()); //v3 服务号码,即电话号码
		busiParamsMap.put("IsCheckPWD", map.get("IsCheckPWD").toString()); //v4 是否检查密码,0:不检查密码 1:检查密码
		busiParamsMap.put("AgentPWD", ""); //v5 代理商密码
		busiParamsMap.put("AuditDate", ""); //v6 清算日期 YYYYMMDD	不指定清算日期指当天
		busiParamsMap.put("Amount", Integer.valueOf(map.get("Amount").toString())); //v7 充值金额
		busiParamsMap.put("OppDoneCode", map.get("OppDoneCode").toString()); // v8 对端交易流水
		
		Map<String, Object> signParammap = new LinkedHashMap<String, Object>();
		signParammap.put("AgentAcctId",(map.get("AgentAcctId")));
		signParammap.put("AgentNode",(map.get("AgentNode")));  
		signParammap.put("ServiceNum",(map.get("ServiceNum"))); 
		signParammap.put("IsCheckPWD",(map.get("IsCheckPWD"))); 
		signParammap.put("AgentPWD",(map.get("AgentPWD")));   
		signParammap.put("AuditDate",(map.get("AuditDate")));  
		signParammap.put("Amount",(map.get("Amount")));     
		signParammap.put("OppDoneCode",(map.get("OppDoneCode")));
		
		String sign = signCreate(key,signParammap);
		busiParamsMap.put("Sign", sign); // 签名:md5(V1V2Vn…md5(key))
		
		Map<String, Object> requestMap= new HashMap<String, Object>();
		requestMap.put("BusiParams", busiParamsMap);
		requestMap.put("BusiCode", map.get("BusiCode").toString());
		
		Map<String, Object> paramMap= new HashMap<String, Object>();
		paramMap.put("Request", requestMap);
		paramMap.put("PubInfo", pubInfoMap);
		
		String jsonString = JSONObject.toJSONString(paramMap);
		System.out.println(jsonString);
		
        SocketTool socket=new SocketTool(host, port, timeOut);
        
        String retMsg = "";
        try {
			retMsg = socket.sendJson(jsonString);
			System.out.println("返回信息["+retMsg+"]");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return retMsg;
	}
	

	
	/**
	 * @功能描述：	生成签名:md5(V1V2Vn…md5(key))
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月21日
	 */
	private static String signCreate(String key,Map<String, Object> map){
		StringBuilder sbd = new StringBuilder();
		for (String mapKey : map.keySet()) {
			sbd.append(map.get(mapKey).toString());
		}

		String sign = MD5.ToMD5(sbd.toString() + MD5.ToMD5(key));
		return sign;
	}
	
	/**
	 * @功能描述：检测充值返回数据	
	 *
	 * @param resultStr
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月21日
	 */
	public static Map<String,Object> valiResult(String resultStr){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap.put("state", false);
		
		JSONObject jsonObject = JSONObject.parseObject(resultStr);
		JSONObject errorInfo = jsonObject.getJSONObject("Response").getJSONObject("ErrorInfo");
		String code = errorInfo.getString("Code");
		String message = errorInfo.getString("Message");
		String hint = errorInfo.getString("Hint");
		resultMap.put("code", code);	// 返回状态码
		resultMap.put("message", message);	// 错误信息
		resultMap.put("hint", hint);	// 错误信息提示
		if ("0000".equals(code)) {
			// 200 是成功
			String paymentId = jsonObject.getJSONObject("Response").getJSONObject("RetInfo").getString("PaymentId");
			resultMap.put("state", true);
			resultMap.put("paymentId", paymentId);	// 上家boss充值流水号
		}
		
		return resultMap;
	}
	
	public static void chongZhiTest(){
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("host", "103.249.55.151");
		map.put("port", "43001");
		map.put("key", "yfdcz123!");
		map.put("BusiCode", "OI_AgentCashPayment");
		map.put("InterfaceId", "100000000035");	// 固定
		map.put("TransactionId", "WEB20160120144842569307");
		map.put("InterfaceType", "35");	// 固定
		map.put("OpId", "1000005661");
		map.put("CountyCode", "0400");
		map.put("OrgId", "100018016");
		map.put("ClientIP", "112.230.203.114");
		map.put("TransactionTime", "20180420150547");
		map.put("RegionCode", "400");
		
		map.put("AgentAcctId", "10248");
		map.put("AgentNode", "");
		map.put("ServiceNum", "17056690992");
		map.put("IsCheckPWD", "0");
		map.put("AgentPWD", "");
		map.put("AuditDate", "");
		map.put("Amount", "1000");
		map.put("OppDoneCode", "WEB20160120144842569307");
		
		String resultStr = agentCashPayment(map);
//		String resultStr="{\"Response\":{\"ErrorInfo\":{\"Message\":\"成功\",\"Hint\":\"成功\",\"Code\":\"0000\"},\"RetInfo\":{\"Sign\":\"b1fe114afd173b47745890f24a483b16\",\"PaymentId\":\"20180423140745A3968013110\"}}}";
		Map<String, Object> resultMap = valiResult(resultStr);
		for (String key : resultMap.keySet()) {
			System.out.println(key +"=" + resultMap.get(key));
		}
	}
	
	public static void main(String[] args) {
		chongZhiTest();
	}
}
