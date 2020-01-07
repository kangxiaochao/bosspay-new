package com.hyfd.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.OrderSimulateUtil;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.service.mp.OrderOtherSer;
import com.hyfd.service.mp.QianmigonghuoCustomerSer;

/**
 * @功能描述：	千米供货业务处理
 *
 * @作者：zhangpj		@创建时间：2017年12月28日
 */
//@Component
public class QianMiGongHuoBillOrderTask{
	private static Logger log = Logger.getLogger(QianMiGongHuoBillOrderTask.class);
	
	@Autowired
	QianmigonghuoCustomerSer qianmigonghuoCustomerSer;// 物理通道信息
	
	@Autowired
	OrderOtherSer orderOtherSer;
	
	/**
	 * Overriding
	 * @功能描述：	千米供货抓单充值
	 *
	 * @作者：zhangpj		@创建时间：2017年12月28日
	 * @return
	 */
//	@Scheduled(fixedDelay = 60000)
	public void orderExecute() {
        List<Map<String, Object>> customerList = qianmigonghuoCustomerSer.getQianmigonghuoCustomerList();// 获取通道的数据
        if (customerList.size() > 0) {
			for (int i = 0; i < customerList.size(); i++) {
				Map<String, Object> map = customerList.get(i);
				try {
					orderExecuteByCustomer(map);
				} catch (Exception e) {
					log.error("千米供货客户[" + JSONObject.toJSONString(map) + "]抓取订单发生异常," + e.getMessage());
				}
			}
		}
	}
	
	private void orderExecuteByCustomer(Map<String, Object> customerMap){
		String templateId = customerMap.get("id").toString();	// 模板编号
		String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数
		
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String ptId = paramMap.get("ptId").toString();	// 订单开始和结尾标识(自定义)
		String terminalName = paramMap.get("rcmpTerminalName").toString();	// rcmp平台分配的客户名称(自定义)
		String partner = paramMap.get("partner").toString();	// 合作商代码,定值,供货商提供
		String tplId = paramMap.get("tplId").toString();		// 模板编号,定值,供货商提供
		String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String supplyUrl = paramMap.get("supplyUrl").toString();	// 订单获取地址
		String checkOrderUrl = paramMap.get("checkOrderUrl").toString();	// 漏单检查地址
		String confirmRechargeUrl = paramMap.get("confirmRechargeUrl").toString();	// 订单充值前确认地址
		String callbackUrl = paramMap.get("callbackUrl").toString();	// 回调地址
		String privateKey = paramMap.get("privateKey").toString();	// 信息加密私钥(自定义)
		
		boolean isOk = true;	// 数据是否正常
		List<Map<String, Object>> phoneInfoList = null;
		
		// 1.订单获取接口
		JSONArray dataListArray = supply(supplyUrl, partner, tplId, apiKey);
		if (null == dataListArray) {
			isOk = false;	//没有获取到订单信息
		}else{
			// 获取订单信息列表
			phoneInfoList = getSupplyDataList(dataListArray);
			if (null == phoneInfoList) {
				isOk = false;	//数据异常
			}
		}
		// 2.如果未返回数据或返回数据无法正常解析,可发起一次漏单确认,千米系统核实之前发送的待充值订单记录,并将检查结果返回给合作商系统
		if (isOk == false) {
			JSONObject jsonData = checkOrder(checkOrderUrl, partner, tplId, apiKey, null);
			if (null == jsonData) {
				isOk = false;	//数据异常
			}else{
				// 是否有漏单
				String leakDetecting = jsonData.getString("leakDetecting");
				if (leakDetecting.equals("true")) {
					dataListArray = jsonData.getJSONArray("dataList");
					// 获取漏单信息列表
					phoneInfoList = getSupplyDataList(dataListArray);
					if (null == phoneInfoList) {
						isOk = false;	//数据异常
					}
				}
			}
		}
		// 3.订单获取或漏单检查未发生异常
		if (isOk) {
			for (int i = 0; i < phoneInfoList.size(); i++) {
				Map<String, Object> map = phoneInfoList.get(i);
				String order_id = map.get("order_id").toString(); //订单编号
				String id = map.get("id").toString(); //id
				
				// 4.订单充值前确认接口
				boolean flag = confirmRecharge(confirmRechargeUrl, order_id, id);
				if (flag) {
					String recharge_account = map.get("recharge_account").toString(); //充值号码
					int order_num = Integer.valueOf(map.get("order_num").toString()); //购买数量
					int product_par_value = Integer.valueOf(map.get("product_par_value").toString()); //人民币面值
					String spec = String.valueOf(order_num*product_par_value*100);	//充值总金额=购买数量*人民币面值*100,单位:分
					String timeStamp = DateUtils.getNowTimeToMS(); //格式:yyyyMMddHHmmssSSS 精确到毫秒
					String agentOrderId = ptId + timeStamp + recharge_account + ptId;
					String oldAgentOrderId = order_id +"_"+ id;	// 千米供货平台提交上来的订单唯一标示
					String orderType = "2";	//话费充值
					String orderSource = ptId+"_"+templateId;	//订单来源
					
					Map<String, Object> orderOtherMap = orderOtherSer.getOrderOther(oldAgentOrderId, orderType, orderSource); // 获取全类型(话费和流量)订单的其他信息
					// 5.验证抓取到的此订单是否已经在rcmp平台的充值队列中,不存在则进行充值请求
					if (null == orderOtherMap) {
						// 6.组合请求参数
						Map<String, String> param = new HashMap<String, String>();
						param.put("terminalName", terminalName);
						param.put("customerOrderId", agentOrderId);
						param.put("phoneNo", recharge_account);
						param.put("orderType", orderType);	// 话费
						param.put("spec", spec);	// 话费单位:分
						param.put("scope", "nation");
						param.put("callbackUrl", callbackUrl);
						param.put("timeStamp", timeStamp);
						
						// 6.发送模拟充值请求
						String data = OrderSimulateUtil.quotaOrder(param, privateKey);
						if (null == data) {
							log.error("千米供货平台话费充值请求超时,phoneNo["+recharge_account+"],customerOrderId["+agentOrderId+"]");
						}else{
							try {
								JSONObject jsonData = JSONObject.parseObject(data);
								String code = jsonData.getString("code");
								if ("0".equals(code) == false) {
									log.error("千米供货平台话费充值请求失败,返回数据["+data+"]");
								}else{
									// 7.请求成功
									String orderinfo = JSONObject.toJSONString(map);
									
									// 8.保存订单信息到mp_order_other表
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
								log.error("千米供货平台话费充值请求失败,返回数据["+data+"]");
							}
						}
					}
				}else{
					log.error("千米供货平台订单充值前确认,验证结果为["+flag+"],order_id["+order_id+"],id["+id+"]");
				}
			}
		}
	}
	
	/**
	 * <h5>功能:</h5>订单获取接口
	 * @param supplyUrl 订单获取地址
	 * @param partner 合作商代码
	 * @param tplid 模板编号
	 * @param apikey 接口密钥
	 * @return 订单信息,如果请求超时或解析数据发生异常则null
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	public static JSONArray supply(String supplyUrl,String partner,String tplid,String apikey){
//		supplyUrl = "http://supply.api.17sup.com/supply.do"; // 订单获取地址
//		partner = "S016149";			// 合作商代码,定值,供货商提供
//		tplid = "MB2016090609233480";	// 模板编号,定值,供货商提供
//		apikey = "y8t26fkznhsznqghrjvuyt88we7gqdvvnof3xoxfed";	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String reqid = "supply" + DateUtils.getNowTimeToMS();
		String format = "json";	// 指定数据返回格式
		JSONArray dataListArray = null; //返回订单信息
		
		// 组合加密参数,生成签名
		List<String> list = new ArrayList<String>();
		list.add(partner);
		list.add(tplid);
		list.add(apikey);
		String sign = getSing(list);// 计算签名
		
		// 组合请求参数,生成请求字符串
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		paramMap.put("partner", partner);
		paramMap.put("tplid", tplid);
		paramMap.put("sign", sign);
		paramMap.put("reqid", reqid);
		paramMap.put("format", format);
		String param = getParam(paramMap);// 组合请求参数
//		System.out.println("param=["+param+"]");
		
		// 订单获取
		String data = ToolHttps.post(false, supplyUrl,param, "application/x-www-form-urlencoded");
//		System.err.println("订单获取返回数据["+data+"]");
		if (null != data) {
			try {
				JSONObject jsonObject = JSONObject.parseObject(data);
//				System.out.println("操作状态["+jsonObject.getString("status")+"]");
//				System.out.println("状态描述["+jsonObject.getString("msg")+"]");
				dataListArray = jsonObject.getJSONObject("data").getJSONArray("dataList");
				// 如果没有获取到订单信息,则返回null
				if (dataListArray.isEmpty()) {
					dataListArray = null;
				}
			} catch (Exception e) {
				log.error("千米供货平台获取订单解析返回数据[" + data + "]发生异常");
			}
		}
		return dataListArray;
	}
	
	/**
	 * <h5>功能:</h5>漏单检查
	 * @param checkOrderUrl 漏单检查地址
	 * @param partner 合作商代码
	 * @param tplid 模板编号
	 * @param apikey 接口密钥
	 * @param orderids 需要验证的订单编号,多个订单号之间用英文逗号隔开
	 * @return 请求结果 请求超时或解析数据发生异常返回null
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	public static JSONObject checkOrder(String checkOrderUrl,String partner,String tplid,String apikey,String orderids){
//		checkOrderUrl = "http://supply.api.17sup.com/checkOrder.do";	// 漏单检查地址
//		partner = "S016149";			// 合作商代码,定值,供货商提供
//		tplid = "MB2016090609233480";	// 模板编号,定值,供货商提供
//		apikey = "y8t26fkznhsznqghrjvuyt88we7gqdvvnof3xoxfed";	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		orderids = ""; // 需要验证的订单编号,需要验证的订单编号,多个订单号之间用英文逗号隔开
		String reqid = "checkOrder" + DateUtils.getNowTimeToMS();
		String format = "json";
		JSONObject jsonData = null; //返回漏单信息
		
		// 组合加密参数,生成签名
		List<String> list = new ArrayList<String>();
		list.add(partner);
		list.add(tplid);
		list.add(reqid);
		list.add(apikey);
		String sign = getSing(list);// 计算签名
		
		// 组合请求参数,生成请求字符串
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		paramMap.put("partner", partner);
		paramMap.put("tplid", tplid);
		paramMap.put("sign", sign);
		paramMap.put("orderids", orderids);
		paramMap.put("reqid", reqid);
		paramMap.put("format", format);
		String param = getParam(paramMap);// 组合请求参数
		System.out.println("param=["+param+"]");
		
		// 漏单检查
		String data = ToolHttps.post(false, checkOrderUrl, param, "application/x-www-form-urlencoded");
//		data = "{\"status\":\"0000\",\"data\":{\"leakDetecting\":\"true\",\"reqId\":null,\"dataList\":[{\"item\":{\"order_id\":\"156854125698558\",\"product_id\":\"10000\",\"recharge_account\":\"15122665896\",\"order_num\":\"10\",\"product_par_value\":\"10\"}},{\"item\":{\"order_id\":\"156854125698666\",\"product_id\":\"10000\",\"recharge_account\":\"15122665777\",\"order_num\":\"30\",\"product_par_value\":\"30\"}},{\"item\":{\"order_id\":\"156854125698999\",\"product_id\":\"10000\",\"recharge_account\":\"15122665666\",\"order_num\":\"20\",\"product_par_value\":\"20\"}}],\"fields\":null},\"msg\":null}";
		System.err.println("漏单检查返回数据["+data+"]");
		if (null != data) {
			try {
				JSONObject jsonObject = JSONObject.parseObject(data);
				System.out.println("操作状态["+jsonObject.getString("status")+"]");
				System.out.println("状态描述["+jsonObject.getString("msg")+"]");
				
				jsonData = JSONObject.parseObject(jsonObject.getString("data"));
				System.out.println("是否有漏单["+jsonData.getString("leakDetecting")+"]");
			} catch (Exception e) {
				log.error("千米供货平台[漏单检查]解析返回数据[" + data +"]发生异常");
				jsonData = null;
			}
		}
		return jsonData;
	}
	
	/**
	 * <h5>功能:</h5>订单充值前确认接口
	 * @param confirmRechargeUrl 订单充值前确认地址
	 * @param orderid 订单编号
	 * @param rechargeId 充值单号,捞单时返回的订单信息中id节点的值
	 * @return 是否可以充值:true可以充值,false不可用充值
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	public static boolean confirmRecharge(String confirmRechargeUrl,String orderid,String rechargeId){
//		confirmRechargeUrl = "http://supply.api.17sup.com/confirmRecharge.do";	// 漏单检查地址
//		orderid = "15010914892174"; // 需要验证的订单编号,需要验证的订单编号,多个订单号之间用英文逗号隔开
//		rechargeId = "344463510";
		String format = "json";
		boolean flag = false;
		
		// 组合请求参数,生成请求字符串
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		paramMap.put("orderid", orderid);
		paramMap.put("id", rechargeId);
		paramMap.put("format", format);
		String param = getParam(paramMap);// 组合请求参数
		System.out.println("param=["+param+"]");
		
		// 漏单检查
		String data = ToolHttps.post(false, confirmRechargeUrl, param, "application/x-www-form-urlencoded");
		System.err.println("订单充值前确认返回数据["+data+"]");
		
		if (null != data) {
			try {
				JSONObject jsonObject = JSONObject.parseObject(data);
				String status = jsonObject.getString("status");
				
				JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
				String canRechaege = jsonData.getString("canRechaege");
				
				if ("0000".equals(status) && "true".equals(canRechaege)) {
					flag = true;
				}
				System.out.println("操作状态["+status+"]");
				System.out.println("状态描述["+jsonObject.getString("msg")+"]");
				System.out.println("是否可以充值["+canRechaege+"]");
			} catch (Exception e) {
				log.error("千米供货平台[订单充值前确认]解析返回数据[" + data + "]发生异常");
			}
		}
		return flag;
	}
	
	/**
	 * <h5>功能:</h5>返回充值结果接口
	 * @param setOrdersUrl 订单充值前确认地址
	 * @param partner 合作商代码
	 * @param tplid 模板编号
	 * @param apikey 接口密钥
	 * @param orderid 订单编号
	 * @param id 充值单号,捞单时返回的订单信息中id节点的值
	 * @param orderstate 订单状态(4:充值成功 5:冲值失败 6:可疑订单)
	 * @param version 版本号
	 * @return 操作是否成功(true:操作成功,false操作失败,为false的时候可以再次发送请求)
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	public static String setOrders(String setOrdersUrl,String partner,String tplid,String apikey,String orderid,String id,String orderstate,String version){
//		setOrdersUrl = "http://supply.api.17sup.com/setOrders.do";	// 返回充值结果地址
//		partner = "S016149";			// 合作商代码,定值,供货商提供
//		tplid = "MB2016090609233480";	// 模板编号,定值,供货商提供
//		apikey = "y8t26fkznhsznqghrjvuyt88we7gqdvvnof3xoxfed";	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
//		orderid = "15010914892174"; // 需要验证的订单编号,需要验证的订单编号,多个订单号之间用英文逗号隔开
//		id = "344463510";
//		orderstate = "5";
//		version = "2.4";
		String format = "json";
		String isok = "";	//
		
		// 组合加密参数,生成签名
		List<String> list = new ArrayList<String>();
		list.add(partner);
		list.add(id);
		list.add(orderid);
		list.add(orderstate);
		list.add(apikey);
		String sign = getSing(list);// 计算签名
		
		// 组合请求参数,生成请求字符串
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		paramMap.put("id", id);
		paramMap.put("orderid", orderid);
		paramMap.put("partner", partner);
		paramMap.put("tplid", tplid);
		paramMap.put("version", version);
		paramMap.put("sign", sign);
		paramMap.put("orderstate", orderstate);
		paramMap.put("format", format);
		String param = getParam(paramMap);// 组合请求参数
		log.error("千米供货平台,返回充值结果接口,请求url[" + setOrdersUrl+"?"+param+"]");
		
		// 返回充值结果
		String data = ToolHttps.post(false, setOrdersUrl, param, "application/x-www-form-urlencoded");
		log.error("千米供货平台,返回充值结果接口,返回数据["+data+"]");
		
		if (null != data) {
			JSONObject jsonObject = JSONObject.parseObject(data);
//			System.out.println("操作状态["+jsonObject.getString("status")+"]");
//			System.out.println("状态描述["+jsonObject.getString("msg")+"]");
			
			JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
			isok = jsonData.getString("isok");
//			System.out.println("操作是否成功["+isok+"]");
		}
		return isok;
	}
	
	/**
	 * <h5>功能:</h5>获取订单信息列表,如果返回null,则表示解释数据时发生异常,数据已不准确,建议不再进行[漏单检查]之后的操作
	 * @param dataListArray
	 * @return 
	 *
	 * @author zhangpj	@date 2016年9月8日
	 */
	private static List<Map<String,Object>> getSupplyDataList(JSONArray dataListArray){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			for (int i = 0; i < dataListArray.size(); i++) {
				Map<String,Object> map = new HashMap<String, Object>();
				// 获取dataListArray数组中的item JSON对象
				JSONObject jsonObj = JSONObject.parseObject(dataListArray.get(i).toString());
				System.out.println(jsonObj);
				String id = jsonObj.getString("id"); //id
				String order_id = jsonObj.getString("order_id"); //订单编号
				int order_num = jsonObj.getIntValue("order_num"); //购买数量
				String recharge_account = jsonObj.getString("recharge_account"); //充值号码
				String product_par_value = jsonObj.getString("product_par_value"); //人民币面值
				
				map.put("id", id);
				map.put("order_id", order_id);
				map.put("order_num", order_num);
				map.put("recharge_account", recharge_account);
				map.put("product_par_value", product_par_value);
				
				list.add(map);
			}
		} catch (Exception e) {
			list = null;
			log.error("千米供货平台获取订单返回数据解析[" + dataListArray.toJSONString() + "]发生异常");
		}
		
		return list;
	}
	
	/**
	 * <h5>功能:</h5>计算签名
	 * @param list
	 * @return 
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	private static String getSing(List<String> list){
		StringBuffer sbf = new StringBuffer();
		for (String str : list) {
			sbf.append(str);
		}
		System.out.println(sbf.toString());
		// 进行md5加密,并转大写
		return DigestUtils.md5Hex(sbf.toString()).toUpperCase();
	}
	
	/**
	 * <h5>功能:</h5>组合参数
	 * @param paramMap
	 * @return 
	 *
	 * @author zhangpj	@date 2016年9月6日
	 */
	private static String getParam(Map<String, Object> paramMap){
		StringBuffer sbf = new StringBuffer();
		for (String paramKey : paramMap.keySet()) {
			sbf.append(paramKey).append("=").append(paramMap.get(paramKey)).append("&");
		}
		if (sbf.length()>0) {
			sbf.deleteCharAt(sbf.length()-1); //移除指定位置的字符,下表从0开始
		}
		return sbf.toString();
	}
}
