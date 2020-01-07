package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.HttpsUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.sitech.miso.util.MD5;

public class WoNiuTransferBillDeal implements BaseDeal{

	private static Logger log = Logger.getLogger(WoNiuTransferBillDeal.class);
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phone = (String) order.get("phone");//手机号
			Double fee = new Double(order.get("fee")+"");//金额
			String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
			map.put("orderId", orderId);
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			String linkUrl = channel.get("link_url").toString();
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String accessId = paramMap.get("accessId");
			String accessKey = paramMap.get("accessKey");
			String accessType = paramMap.get("accessType");
			String accessPasswd = paramMap.get("accessPasswd");
			String passportSrc = paramMap.get("passportSrc");
			String appIdSrc = paramMap.get("appIdSrc");
			String areaIdSrc = paramMap.get("areaIdSrc");
			String passportDest = phone;
			String appIdDest = paramMap.get("appIdDest");
			String areaIdDest = paramMap.get("areaIdDest");
			String businessCode = paramMap.get("businessCode");
			String payTypeId = paramMap.get("payTypeId");
			String src_equal0 = paramMap.get("src_equal0");
			JSONArray jarr = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("payTypeId", payTypeId);
			json.put("amount", fee);
			json.put("src_equal0", src_equal0);
			jarr.add(json);
			StringBuffer inputBuffer = new StringBuffer();
			inputBuffer.append(accessId).append(accessPasswd).append(accessType).append("").append(appIdDest)
					.append(appIdSrc).append(areaIdDest).append(areaIdSrc).append(businessCode).append(orderId)
					.append(passportDest).append(passportSrc).append("").append(jarr.toJSONString()).append("")
					.append("json").append(accessKey);
			log.error("蜗牛官方转账接口加密前明文："+inputBuffer.toString());
			String verifyStr = MD5.ToMD5(inputBuffer.toString()).toUpperCase();
			log.error("蜗牛官方转账接口加密后密文：verifyStr");
			String param = "accessId=" + accessId + "&accessType=" + accessType + "&accessPasswd=" + accessPasswd
					+ "&passportSrc=" + passportSrc + "&appIdSrc=" + appIdSrc + "&areaIdSrc=" + areaIdSrc
					+ "&passportDest=" + passportDest + "&appIdDest=" + appIdDest + "&areaIdDest=" + areaIdDest
					+ "&orderId=" + orderId + "&payTypeId=&amount=&businessCode=" + businessCode + "&transferInfo="
					+ jarr.toJSONString() + "&transferAll=&returnType=json&verifyStr="
					+ verifyStr;
			log.error("蜗牛官方转账接口请求参数："+linkUrl+"?"+param);
			String result = HttpsUtils.postParameters(linkUrl, param);
			log.error("蜗牛官方转账接口请求返回："+result);
			//{"message":"操作成功","transfer":[{"amount":"0.01","payTypeId":"o"}],"msgcode":"1","data":{"STATE":"0","settleTime":"2019-03-26 16:28:31.369","balanceInfoDest":[{"amount":"191.27","endTime":"","payTypeId":"o","payTypeName":"","totalAmt":""}],"resultCode":0,"balanceInfoSrc":[{"amount":"988728.59","endTime":"","payTypeId":"o","payTypeName":"","totalAmt":""}],"failReason":"","orderId":"1234566666667"}}
			JSONObject resultJson = JSONObject.parseObject(result);
			String msgcode = resultJson.getString("msgcode");
			String message = resultJson.getString("message");
			map.put("resultCode", msgcode+":"+message);
			if(msgcode.equals("1")){
				flag = 3;
			}else{
				flag = 4;
			}
			map.put("status", flag);
		}catch(Exception e){
			e.printStackTrace();
			log.error("蜗牛官方转账接口出错："+e.getMessage());
		}
		return map;
	}

}
