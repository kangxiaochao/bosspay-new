package com.hyfd.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.OrderSimulateUtil;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.service.mp.JuhegonghuoCustomerSer;
import com.hyfd.service.mp.OrderOtherSer;

@Component
public class JuHeBillOrderTask {
	private static Logger log = LogManager.getLogger(JuHeBillOrderTask.class);
	
	@Autowired
	JuhegonghuoCustomerSer juhegonghuoCustomerSer;// 物理通道信息
	
	@Autowired
	OrderOtherSer orderOtherSer;

	/**
	 * Overriding
	 * @功能描述：	千米供货抓单充值
	 *
	 * @作者：zhangpj		@创建时间：2017年12月28日
	 * @return
	 */
	@Scheduled(fixedDelay = 15000)
	public void orderExecute() {
        List<Map<String, Object>> customerList = juhegonghuoCustomerSer.getJuhegonghuoCustomerList();// 获取聚合供货的客户列表
        if (customerList.size() > 0) {
			for (int i = 0; i < customerList.size(); i++) {
				Map<String, Object> map = customerList.get(i);
				try {
					orderExecuteByCustomer(map);
				} catch (Exception e) {
					log.error("聚合供货客户[" + JSONObject.toJSONString(map) + "]抓取订单发生异常," + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * @功能描述：	抓取聚合供货平台话费充值订单并且发起充值
	 *
	 * @param customerMap 
	 *
	 * @作者：zhangpj		@创建时间：2018年3月6日
	 */
	private void orderExecuteByCustomer(Map<String, Object> customerMap){
		String templateId = customerMap.get("id").toString();	// 模板编号
		String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数
		
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim()); 
		String ptId = paramMap.get("ptId").toString();	// 订单开始和结尾标识(自定义)
		String terminalName = paramMap.get("agentName").toString();	// 我方平台分配的客户名称(自定义)
		
		String getOrderUrl = paramMap.get("getOrderUrl").toString();	// 订单获取地址
		String updateOrderStatusUrl = paramMap.get("updateOrderStatusUrl").toString();	// 回调聚合供货平台地址
		String supplierId = paramMap.get("supplierId").toString();	// 模板编号,定值,供货商提供
		String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String num = paramMap.get("num").toString(); // 每次抓取订单的数量
		String callbackUrl = paramMap.get("callbackUrl").toString();	// 回调地址
		String privateKey = paramMap.get("privateKey").toString();	// 信息加密私钥(自定义)
		
		String category = "virtual_operator"; // 商品类型 PHONE("手机话费"), PHONE_FLOW("手机流量"), FIXED("固话宽带"), LIFE("生活缴费"), GAME("游戏”),VIRTUAL_OPERATOR("虚拟运营商")
		String sign = DigestUtils.md5Hex(supplierId + num + apiKey);
		
		// 1 获取订单信息
		String resultStr = getOrder(getOrderUrl, supplierId, num, category, sign);
//		String resultStr = "{\"msg\":null,\"success\":true,\"detail\":[{\"tradeId\":3874677,\"supplierId\":\"S100055\",\"productId\":\"15100000\",\"productName\":\"蜗牛移动(蜗牛数字)面值：1元\",\"productCategory\":\"virtual_operator\",\"faceValue\":1.0000,\"costPrice\":0.9550,\"account\":\"17183611111\",\"amount\":1,\"createTime\":1520473920601,\"province\":null,\"city\":null,\"operatorType\":6,\"operatorBrand\":\"蜗牛移动\",\"operatorCompany\":\"蜗牛数字\",\"foreignCode\":\"\"}]}";
		
		System.out.println(resultStr);
		if (null !=  resultStr && !resultStr.equals("")) {
			JSONArray orderArray = getOrderDetail(resultStr);
			if (null != orderArray &&  orderArray.size() > 0) {
				JSONObject orderJson = null;
				String tradeId = ""; // 聚合平台订单编号
				String account = ""; // 充值号码
				int faceValue; // 充值金额
				int amount; // 充值数量
				for (int i = 0; i < orderArray.size(); i++) {
					orderJson = orderArray.getJSONObject(i);
					tradeId = orderJson.getString("tradeId");	// 聚合平台订单编号
					account = orderJson.getString("account");
					faceValue = orderJson.getInteger("faceValue");
					amount = orderJson.getInteger("amount");
					
					String spec = String.valueOf(amount * faceValue * 100);	//充值总金额 = 充值数量 * 人民币面值 * 100,单位:分
					String timeStamp = DateUtils.getNowTimeToMS(); //格式:yyyyMMddHHmmssSSS 精确到毫秒
					String agentOrderId = ptId + timeStamp + StringUtil.fillstr(tradeId, 11, 'k') + ptId;
					String oldAgentOrderId =  tradeId+"_"+ templateId;	// 聚合供货平台提交上来的订单唯一标示
					String orderType = "2";	//话费充值
					String orderSource = ptId+"_"+templateId;	//订单来源
					
					Map<String, Object> orderOtherMap = orderOtherSer.getOrderOther(oldAgentOrderId, orderType, orderSource); // 获取全类型(话费和流量)订单的其他信息
					// 2.验证抓取到的此订单是否已经在rcmp平台的充值队列中,不存在则进行充值请求
					if (null == orderOtherMap) {
						// 3.组合请求参数
						Map<String, String> param = new HashMap<String, String>();
						param.put("terminalName", terminalName);
						param.put("customerOrderId", agentOrderId);
						param.put("phoneNo", account);
						param.put("orderType", orderType);	// 话费
						param.put("spec", spec);	// 话费单位:分
						param.put("scope", "nation");
						param.put("callbackUrl", callbackUrl);
						param.put("timeStamp", timeStamp);
						
						// 4.发送模拟充值请求
						String data = OrderSimulateUtil.quotaOrder(param, privateKey);
//						String data = "{\"code\": 0}";
						if (null == data) {
							log.error("聚合供货平台话费充值请求超时,phoneNo["+account+"],customerOrderId["+agentOrderId+"]");
						}else{
							try {
								JSONObject jsonData = JSONObject.parseObject(data);
								String code = jsonData.getString("code");
								if ("0".equals(code) == false) {
									log.error("聚合供货平台话费充值请求失败,phoneNo["+account+"],返回数据["+data+"]");
									
									String orderState = "3"; //设置订单状态 2:充值成功 3:冲值失败
									String outOrderNo = "error99999";
									String backSign = DigestUtils.md5Hex(supplierId + outOrderNo + apiKey);
									// 5 请求失败,返回充值结果
									String result = JuHeBillOrderCallback.updateOrderStatus(updateOrderStatusUrl, supplierId, tradeId, orderState, backSign, outOrderNo);
							    	if (null != result && !result.equals("")) {
										JSONObject jsonObj = JSONObject.parseObject(result);
										String success = jsonObj.getString("success");
										if (!"true".equals(success)) {
											log.info("phoneNo["+account+"]回调聚合供货平台订单状态失败,返回信息["+result+"]");
										}
									}
								}else{
									// 6 请求成功
									String orderinfo = orderJson.toJSONString();
									
									// 7 保存订单信息到mp_order_other表
									orderOtherMap = new HashMap<String, Object>();
									orderOtherMap.put("id", UUID.randomUUID().toString().replace("-", ""));
									orderOtherMap.put("agentorderid", agentOrderId);
									orderOtherMap.put("oldagentorderid", oldAgentOrderId);
									orderOtherMap.put("ordertype", orderType);
									orderOtherMap.put("ordersource", orderSource);
									orderOtherMap.put("orderinfo", orderinfo);
									orderOtherMap.put("createtime", DateUtils.getNowTimeToMS());
									orderOtherSer.saveOrderOther(orderOtherMap);
								}
							} catch (Exception e) {
								log.error("聚合供货平台话费充值请求失败,phoneNo["+account+"],返回数据["+data+"]");
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @功能描述：	
	 *
	 * @param url 供货地址
	 * @param supplierId 供货商编号
	 * @param num 取单数量
	 * @param category 商品类型
	 * @param sign 签名
	 *
	 * @作者：zhangpj		@创建时间：2018年3月5日
	 */
	private static String getOrder(String url,String supplierId,String num,String category,String sign){
		StringBuffer sbf = new StringBuffer();
		sbf.append(url);
		sbf.append("?supplierId=" + supplierId);
		sbf.append("&num=" + num);
		sbf.append("&category=" + category);
		sbf.append("&sign=" + sign);
		
		String retrunStr = "";
		try {
			retrunStr = ToolHttp.post(false, sbf.toString(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retrunStr;
	}
	
	/**
	 * @功能描述：	
	 *
	 * @param resultStr
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年3月5日
	 */
	private static JSONArray getOrderDetail(String resultStr){
		JSONObject jsonObj = JSONObject.parseObject(resultStr);
		String success = jsonObj.getString("success");
		JSONArray orderArray = null; 
		if (success.equals("true")) {
			orderArray = jsonObj.getJSONArray("detail");
		}else {
			log.info("聚合抓取订信息失败,返回信息["+jsonObj.toJSONString()+"]");
		}
		
		return orderArray;
	}
	
	public static void main(String[] args) {
//		String url = "http://39.108.114.236/api/supply/getOrder";
//		String supplierId = "S100055";
//		int num = 1;
//		String category = "PHONE";
//		String apiKey = "4dd5e7dfc84941dc93100efe68e3c13b";
//		
//		
//		String sign = createSign(supplierId, num, apiKey);
//		String resutlStr = getOrder(url, supplierId, num, category, sign);
//		System.out.println(resutlStr);
		
		getOrderDetail(null);
	}
}
