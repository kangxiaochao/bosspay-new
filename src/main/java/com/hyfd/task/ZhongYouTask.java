package com.hyfd.task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.SocketTool;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

/**
 * @功能描述：	可查询充值记录
 *
 * @作者：zhangpj		@创建时间：2018年5月7日
 */
@Component
public class ZhongYouTask {
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(ZhongYouTask.class);
    
//    @Scheduled(fixedDelay = 60000)
	public void queryMinShengOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000028";// 中邮物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String url = paramMap.get("url");
			String port = paramMap.get("port");
            String key = paramMap.get("key");
            String queryBusiCode = paramMap.get("QueryBusiCode");
            String interfaceId = paramMap.get("InterfaceId");
            String interfaceType = paramMap.get("InterfaceType");
            String opId = paramMap.get("OpId");
            String countyCode = paramMap.get("CountyCode");
            String orgId = paramMap.get("OrgId");
            String clientIP = paramMap.get("ClientIP");
            String regionCode = paramMap.get("RegionCode");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				String phone = order.get("phone") + "";
				
				Map<String, Object> queryMap= new HashMap<String, Object>();
				queryMap.put("host", url);
				queryMap.put("port", port);
				queryMap.put("key", key);
				queryMap.put("BusiCode", queryBusiCode);
				queryMap.put("InterfaceId", interfaceId);	// 固定
				queryMap.put("TransactionId", orderId);
				queryMap.put("InterfaceType", interfaceType);	// 固定
				queryMap.put("OpId", opId);
				queryMap.put("CountyCode", countyCode);
				queryMap.put("OrgId", orgId);
				queryMap.put("ClientIP", clientIP);
				queryMap.put("TransactionTime", DateTimeUtils.getDateTime("yyyyMMddHHmmss"));
				queryMap.put("RegionCode", regionCode);
				
				// 查询
				queryMap.put("ServiceNum", phone);	// v1 服务号码 手机号
				queryMap.put("QueryType", "1"); // v2 结束索引
				queryMap.put("StartDate", DateTimeUtils.getDateTime("yyyyMMdd")); //v3 起始日期
				queryMap.put("EndDate", DateTimeUtils.getDateTime("yyyyMMdd")); //v4结束日期
				
				String resultStr = getChannelPaymentRecord(queryMap);
				Map<String, Object> resutlMap = valiResult(resultStr);
				String code = String.valueOf(resutlMap.get("code"));

				if ("0000".equals(code)) {
					flag = 1;
				}
				map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			log.error("中邮查询Task出错" + e);
		}
	}
	
	/**
	 * @功能描述：	验证充值查询结果
	 *
	 * @param resultStr
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月28日
	 */
	private static Map<String,Object> valiResult(String resultStr){
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
			resultMap.put("state", true);
		}
		
		return resultMap;
	}
	
	
	/**
	 * @功能描述：	订单查询接口
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月23日
	 */
	private static String getChannelPaymentRecord(Map<String,Object> map){
		String host = map.get("host").toString();
		int port = Integer.valueOf(map.get("port").toString());
//		String key = map.get("key").toString();
		int timeOut = 20000;
		
		Map<String, Object> pubInfoMap= new HashMap<String, Object>();
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
		busiParamsMap.put("ServiceNum", map.get("ServiceNum").toString()); //v1 服务号码,即电话号码
		busiParamsMap.put("QueryType", map.get("QueryType").toString()); //v2 起始日期
		busiParamsMap.put("StartDate", map.get("StartDate").toString()); //v3 起始日期
		busiParamsMap.put("EndDate", map.get("EndDate").toString()); //v4 结束日期
		
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
	
	private static void chaXunTest(){
//		Map<String, Object> map= new HashMap<String, Object>();
//		map.put("host", "103.249.55.151");
//		map.put("port", "43001");
//		map.put("key", "yfdcz123!");
//		map.put("BusiCode", "OI_GetPaymentRecord");
//		map.put("InterfaceId", "100000000035");	// 固定
//		map.put("TransactionId", "WEB2016012014484256930");
//		map.put("InterfaceType", "35");	// 固定
//		map.put("OpId", "1000005661");
//		map.put("CountyCode", "0400");
//		map.put("OrgId", "100018016");
//		map.put("ClientIP", "112.230.203.114");
//		map.put("TransactionTime", "20180420150547");
//		map.put("RegionCode", "400");
//		
//		// 查询
//		map.put("ServiceNum", "17056690992");	// v1 服务号码 手机号
//		map.put("QueryType", "1"); // v2 结束索引
//		map.put("StartDate", "20180428"); //v3 起始日期
//		map.put("EndDate", "20180428"); //v4结束日期
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("host", "kzcz.170139.com");
		map.put("port", "43025");
		map.put("key", "yfdcz123!");
		map.put("BusiCode", "OI_GetPaymentRecord");
		map.put("InterfaceId", "100000000035");	// 固定
		map.put("TransactionId", "WEB2016012014484256931");
		map.put("InterfaceType", "35");	// 固定
		map.put("OpId", "1000005020");
		map.put("CountyCode", "0400");
		map.put("OrgId", "100018066");
		map.put("ClientIP", "112.230.203.114");
		map.put("TransactionTime", "20180815150547");
		map.put("RegionCode", "400");
		
		// 查询
		map.put("ServiceNum", "17065870712");	// v1 服务号码 手机号
		map.put("QueryType", "1"); // v2 结束索引
		map.put("StartDate", "20180816"); //v3 起始日期
		map.put("EndDate", "20180817"); //v4结束日期
		
		String resultStr = getChannelPaymentRecord(map);
//		String resultStr="{\"Response\":{\"ErrorInfo\":{\"Message\":\"成功\",\"Hint\":\"成功\",\"Code\":\"0000\"},\"RetInfo\":{\"Sign\":\"b1fe114afd173b47745890f24a483b16\",\"PaymentId\":\"20180423140745A3968013110\"}}}";
		Map<String, Object> resultMap = valiResult(resultStr);
		for (String key : resultMap.keySet()) {
			System.out.println(key +"=" + resultMap.get(key));
		}
	}
	
	public static void main(String[] args) {
		chaXunTest();
	}
}
