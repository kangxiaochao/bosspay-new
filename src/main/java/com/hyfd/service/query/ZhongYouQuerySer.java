package com.hyfd.service.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.SocketTool;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class ZhongYouQuerySer extends BaseService {

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
	 * 
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap key:amount 余额 单位元 key:status
	 *         查询状态 0查询成功 1查询失败 key:phoneownername 机主姓名
	 */
	public Map<String, String> getChargeAmountInfo(String mobileNumber) {
		Map<String, String> amountInfoMap = new HashMap<String, String>();
		
		String id = "2000000028";// 中邮物理通道ID ~~~~~
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
		String linkUrl = (String)channel.get("link_url");
		
		String defaultParameter = (String)channel.get("default_parameter");// 默认参数
		Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		String port = paramMap.get("port");
        String queryBalanceBusiCode = paramMap.get("QueryBalanceBusiCode");
        String interfaceId = paramMap.get("InterfaceId");
        String interfaceType = paramMap.get("InterfaceType");
        String opId = paramMap.get("OpId");
        String countyCode = paramMap.get("CountyCode");
        String orgId = paramMap.get("OrgId");
        String clientIP = paramMap.get("ClientIP");
        String regionCode = paramMap.get("RegionCode");
        
        String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
		String orderId = timeStamp + GenerateData.getIntData(9, 6) + "01";//32位订单号

        // 组合请求参数
		Map<String, Object> paymentMap= new HashMap<String, Object>();
		paymentMap.put("host", linkUrl);	// ip地址(上游提供)
		paymentMap.put("port", port);	// 端口号(上游提供)
		paymentMap.put("BusiCode", queryBalanceBusiCode);// 接口方法名称(上游提供)
		paymentMap.put("InterfaceId", interfaceId);	// (上游提供,暂时为空)
		paymentMap.put("TransactionId", orderId);	// 交易流水
		paymentMap.put("InterfaceType", interfaceType);	// 接口类型(上游提供)
		paymentMap.put("OpId", opId);	// 操作员Id(上游提供)
		paymentMap.put("CountyCode", countyCode);	// 操作员登录县市标识
		paymentMap.put("OrgId", orgId);	// 操作员组织Id
		paymentMap.put("ClientIP", clientIP);	// 请求端IP
		paymentMap.put("TransactionTime", timeStamp);	//交易时间YYYYMMDDH24MISS(上游提供)
		paymentMap.put("RegionCode", regionCode);	// 操作员登录地区标识
		paymentMap.put("ServiceNum", mobileNumber);	// 服务号码,即充值号码
		
		String resultStr = agentCashPayment(paymentMap);
		if (resultStr != null && !"".equals(resultStr)) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			JSONObject errorInfo = jsonObject.getJSONObject("Response").getJSONObject("ErrorInfo");
			String code = errorInfo.getString("Code");
			if ("0000".equals(code)) {
				String RealBalance = jsonObject.getJSONObject("Response").getJSONObject("RetInfo").getString("RealBalance");
				amountInfoMap.put("status", "0");// 0为成功
				amountInfoMap.put("amount", Double.parseDouble(RealBalance)/100 + "");
				amountInfoMap.put("phoneownername", "未知");
			} else {
				amountInfoMap.put("status", "1");
				amountInfoMap.put("amount", "0");
			}
		} else {
			log.error("分享的返回值为空或者请求超时");
		}
			
		return amountInfoMap;
	}
	
	private static String agentCashPayment(Map<String,Object> map){
		String host = map.get("host").toString();
		int port = Integer.valueOf(map.get("port").toString());
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
		
		Map<String, Object> busiParamsMap= new HashMap<String, Object>();
		busiParamsMap.put("ServiceNum", map.get("ServiceNum").toString()); //v3 服务号码,即电话号码
		
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
	
	public static void main(String[] args) {
		Map<String, Object> map= new HashMap<String, Object>();
		map.put("host", "103.249.55.151");
		map.put("port", "43001");
		map.put("key", "yfdcz123!");
		map.put("BusiCode", "OI_GetAccountBalance");
		map.put("InterfaceId", "100000000035");	// 固定
		map.put("TransactionId", "WEB20160120144842569307");
		map.put("InterfaceType", "35");	// 固定
		map.put("OpId", "1000005661");
		map.put("CountyCode", "0400");
		map.put("OrgId", "100018016");
		map.put("ClientIP", "112.230.203.114");
		map.put("TransactionTime", "20180420150547");
		map.put("RegionCode", "400");
		
		map.put("ServiceNum", "17056690992");
		
		agentCashPayment(map);
	}

}
