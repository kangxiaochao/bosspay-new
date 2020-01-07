package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.ToolMD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class HuaJiShiJieBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;  //-1异常;1成功;0失败;3实时成功;4实时失败
		try{
			//获取订单中的信息
			String phone = (String) order.get("phone");
//			double fee = (double) order.get("fee");
			double fee =  Double.parseDouble(order.get("fee")+"");
			String curids = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);//订单号
			map.put("orderId", curids);	// 自己平台订单号
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String appKey = paramMap.get("appKey");		//调用账户
			String passWord = paramMap.get("passWord");	//密码
			String method = paramMap.get("method");		//调用方法
			String vserion = paramMap.get("vserion");	//版本号
			String paySrc = paramMap.get("paySrc");		//渠道标识
			String noticeUrl = paramMap.get("noticeUrl");//回调地址
			String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");// 格式 ：yyyymmddhhmiss调用时间
			String payNote = "号码["+phone+"]"+timestamp+"充值话费"+fee+"元!";
			String json = createRequestJson(passWord, appKey, method, timestamp, vserion, curids, fee*100+"", phone, noticeUrl, payNote, paySrc);
			String result = ToolHttp.post(false, linkUrl, json, "json/html");
			if(result!=null&&!result.equals("")){
				JSONObject resultJson = JSONObject.parseObject(result);
				String status = resultJson.getString("status");	// 返回0是成功，返回1是失败
				
				String message = resultJson.getString("message");
				String iscallback = "";
				String esPaymentId = "";
				if ("0".equals(status)) {
					iscallback = resultJson.getString("iscallback");	// 返回1表示会回调，返回0表示充值直接成功不需要回调
					esPaymentId = resultJson.getString("esPaymentId");	// 响应的流水
				}
				
				map.put("providerOrderId", esPaymentId);	// 上家平台订单号
				map.put("resultCode", status+":"+message);	// 充值结果
				if("0".equals(status) && "0".equals(iscallback)){
					flag = 3;
				}else if("0".equals(status) && "1".equals(iscallback)){
					flag = 1;
				}else{
					flag = 0;
				}
			}
		}catch(Exception e){
			log.error("话机世界充值出错"+e.getMessage()+"||"+MapUtils.toString(order));
		}
		map.put("status", flag);	// 充值状态
		return map;
	}

	/**
	 * @param password
	 *          密码
	 * @param appkey
	 *          调用账户 ，由话机世界统一分配
	 * @param method
	 *          调用方法com.method.Recharge 充值时为这个字符串，固定的
	 * @param timestamp
	 *          格式 ：yyyymmddhhmiss调用时间
	 * @param vserion
	 *          版本号 1.0
	 * @param serial
	 *          流水号，自定义流水
	 * @param payAmount
	 *          充值金额(分) 付款金额单位分最多每次充1000
	 * @param accNbr
	 *          充值号码
	 * @param noticeUrl
	 *          回调的url 充值回调地址（1709号码是实时的不需要回调）
	 * @param payNote
	 *          充值描述
	 * @param paySrc
	 *          充值渠道
	 * @return
	 */
	public static String createRequestJson(String password, String appkey, String method, String timestamp, String vserion, String serial, String payAmount, String accNbr, String noticeUrl, String payNote, String paySrc) {
		JSONObject requestJson = new JSONObject();
		requestJson.put("payAmount", payAmount);
		requestJson.put("accNbr", accNbr);
		requestJson.put("noticeUrl", noticeUrl);
		requestJson.put("payNote", payNote);
		requestJson.put("paySrc", paySrc);

		String signSrc = serial + requestJson.toString() + password;
		String sign = new String();
		try {
			sign = ToolMD5.encodeMD5Hex(signSrc).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		json.put("appkey", appkey);
		json.put("method", method);
		json.put("timestamp", timestamp);
		json.put("vserion", vserion);
		json.put("sign", sign);
		json.put("serial", serial);
		json.put("request", requestJson);
		return json.toString();
	}

	
}
