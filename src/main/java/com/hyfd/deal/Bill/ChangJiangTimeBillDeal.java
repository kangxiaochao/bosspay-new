package com.hyfd.deal.Bill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjtc.http.HttpClientConnectionManager;
import com.cjtc.sign.RequestSignHandler;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ChangJiangTimeBillDeal implements BaseDeal{
	private static Logger log = Logger.getLogger(ChangJiangTimeBillDeal.class);
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phoneNo = (String) order.get("phone");//手机号
			String fee = order.get("fee")+"";//金额，以元为单位
			String spec = Double.parseDouble(fee)*100+"";//充值金额，以分为单位

			//获取channel信息
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String Url = channel.get("link_url").toString();
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("appKey");
			String secretKey = paramMap.get("secretKey");
			String method  = paramMap.get("chongzhiMethod");
			String tpAccount = paramMap.get("tpAccount");
			
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNo+GenerateData.getIntData(9, 2);
			map.put("orderId", curids);
			//开始调用接口充值
			String resultStr = SendPost(Url,appKey,method,tpAccount,secretKey,phoneNo,spec,curids);
			
			//解析返回值
			JSONObject resultJson = JSON.parseObject(resultStr);
			String rpListStr = resultJson.getString("rpList");
			JSONArray rpList = JSONArray.parseArray(rpListStr);
			JSONObject rp = rpList.getJSONObject(0);
			int status = rp.getInteger("status");
			String phone = rp.getString("phone");
			String amount = rp.getString("amount");
			String orderNo = rp.getString("orderNo");
			map.put("providerOrderId", orderNo);
			//判断充值是否成功
			if(status==1){
				flag = 3;
//				log.debug("长江时代[话费充值]长江时代充值成功     ,手机号["+phone+"],充值金额["+amount+"]");
			}else{
				flag = 4;
				log.error("长江时代[话费充值]失败 ,手机号["+phone+"],充值金额["+amount+"]");
			}
		}catch(Exception e){
			log.error("长江时代[话费充值]方法出错"+e+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	public static String SendPost(String Url,String appKey,String method,String tpAccount,String secretKey,String phoneNo,String price,String curids){		
		HttpPost post = null;
		String jsonStr = "";
		try{
			String url = Url;
			HttpClient client = HttpClientConnectionManager.getInstance();
			Map<String, String> paramMap = new HashMap<String, String>();
			//公钥
			paramMap.put("appKey", appKey);
			//服务名
			paramMap.put("method", method);
			//时间戳
			paramMap.put("timestrap", System.currentTimeMillis() + "");
			paramMap.put("tpAccount", tpAccount);
			//公钥，私钥，编码
			RequestSignHandler rsh = new RequestSignHandler(appKey, secretKey, "UTF-8");
			List<Map<String,String>> rpList = new ArrayList<Map<String,String>>();
			Map<String,String> rp = new HashMap<String,String>();
			rp.put("phone", phoneNo);
			rp.put("amount", price);
			rpList.add(rp);
			paramMap.put("tpAmount", price);
			paramMap.put("tpSerialNo", curids);
			paramMap.put("rpList", JSONArray.toJSONString(rpList).toString());
			//生成签名
			String sign = rsh.createSign(paramMap);
			paramMap.put("sign", sign);
			//POST方式调用接口
			log.debug("长江时代请求参数         ||||"+JSONObject.toJSON(paramMap).toString());
			post = HttpClientConnectionManager.getPostMethod(url, JSONObject.toJSON(paramMap).toString());
			HttpResponse response = client.execute(post);
			//服务端返回结果（JSON字符串）
			jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			log.debug("长江时代返回值        ||||"+jsonStr);
		} catch (Exception e) {
			log.error("长江时代充值出现异常---"+e);
			e.printStackTrace();
		}finally{
			if(post != null){
				post.releaseConnection();
			}
		}
		return jsonStr;
	}
	
}
