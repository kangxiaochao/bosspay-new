package com.hyfd.deal.Bill;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * @功能描述：	麦远话费业务处理
 *
 * @作者：zhangpj		@创建时间：2018年5月21日
 */
public class MaiYuanBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(MaiYuanBillDeal.class);

	/**
	 * Overriding
	 * @功能描述：	麦远业务处理
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 * @param order
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			String spec = new Double(fee).intValue() * 100 + ""; //充值金额，以分为单位,取整数,不要小数

			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = channel.get("link_url").toString(); // 充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String action = paramMap.get("chargeAction") + ""; // 麦远提供 充值方法名称
			String v = paramMap.get("v") + ""; // 麦远提供 版本号
			String account = paramMap.get("account") + ""; // 麦远提供 帐号
			String apiKey = paramMap.get("apiKey") + ""; // 麦远提供 密钥

			// 生成自己的id，供回调时查询数据使用
			String orderId = ToolDateTime.format(new Date(),"yyyyMMddHHmmssSSS") + GenerateData.getStrData(3);
			map.put("orderId", orderId);

			String data = charge(linkUrl, action, v, orderId, account, phoneNo, fee, apiKey);
			log.error("麦远[话费充值]请求返回信息[" + data + "]");
			if (null != data && !data.equals("")) {
				JSONObject jsonObject = JSONObject.parseObject(data);
				String code = jsonObject.get("Code").toString();
				String message = jsonObject.get("Message").toString();
				
				map.put("resultCode", code + ":" + message);
				if (code.equals("0")) {
					String taskID = "";
					if (null != jsonObject.get("TaskID")) {
						jsonObject.get("TaskID").toString();
					}
					map.put("providerOrderId", taskID);
					flag = 1;
					log.debug("麦远[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+spec+"]分");
				} else if ("0000".equals(code)) {
					flag = -1;
					log.debug("麦远[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+spec+"]分");
				} else {
					log.debug("麦远[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["	+ spec + "]分," + code + "[" + message + "]");
					flag = 0;
				}
			}
		} catch (Exception e) {
			log.error("麦远[话费充值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		
		return map;
	}
	
	/**
	 * @功能描述：	计算签名
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	public static String creatSign(Map<String, String> map,String apiKey){
		// 拼接请求参数字符串
        StringBuilder paramUrl = new StringBuilder();
        for (String key : map.keySet()) {
            paramUrl.append(key).append("=").append(map.get(key)).append("&");
        }
        paramUrl.append("key=" + apiKey);
		System.out.println(paramUrl.toString());
		String sign = com.hyfd.common.utils.MD5.ToMD5(paramUrl.toString());
		return sign;
	}
	
	/**
	 * @功能描述：	生成请求参数
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	public static String creatParam(Map<String, String> map, String apiKey){
		// 拼接请求参数字符串
		StringBuilder paramUrl = new StringBuilder("?");
		for (String key : map.keySet()) {
			paramUrl.append(key).append("=").append(map.get(key)).append("&");
		}
		paramUrl.append("key=" + apiKey);
		
		return paramUrl.toString();
	}
	
	// ---------------------------- private ----------------------------
	
	/**
	 * @功能描述：	充值请求
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月21日
	 */
	private static String charge(String url, String action, String v, String outTradeNo, String account, String mobile, String myPackage, String apiKey){
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		map.put("mobile", mobile);
		map.put("package", myPackage);
		
		// 计算签名
		String sign = creatSign(map,apiKey);
		
		String resultStr = "";
		map.put("action", action);
		map.put("v", v);
		map.put("outTradeNo", outTradeNo);
		map.put("sign", sign);
		// 生成请求参数
		url += creatParam(map, apiKey);
		resultStr = ToolHttp.get(false, url);
		return resultStr;
	}
}
