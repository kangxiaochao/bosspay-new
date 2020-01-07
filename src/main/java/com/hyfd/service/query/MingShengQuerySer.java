package com.hyfd.service.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.SHA;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class MingShengQuerySer extends BaseService {

	Logger log = Logger.getLogger(getClass());

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

	/**
	 * 查询手机号余额
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public Map<String, String> getChargeAmountInfo(String mobileNumber) {
		Map<String, String> result = new HashMap<String, String>();
		
		String channelId = "2000000034";//通道ID
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(channelId);
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		String balanceQnquiryUrl = paramMap.get("balanceQnquiryUrl");// 余额查询地址
		String appKey = paramMap.get("appKey");
		String service = "agent.synqryfeeforagent";
		String label = paramMap.get("label");
		String payChannel = paramMap.get("payChannel");
		
		String param = JointUrl(mobileNumber, label, appKey, service, payChannel);
		String searchResult = HttpUtils.doPost(balanceQnquiryUrl, param);
		JSONObject resultObject = JSONObject.parseObject(searchResult);
		log.error("民生余额查询返回信息："+resultObject);
		result.put("phoneownername","未知");
		//10200为成功其余为失败
		if("10200".equals(resultObject.getString("code"))) {
			String amount = resultObject.getJSONObject("data").getString("leaverealFee");
			result.put("amount", Double.parseDouble(amount)/100 + "");
			result.put("status", "0"); //成功
		}else {
			result.put("status", "1"); //成功
		}
		return result;
	}

	/**
	 * 拼接信息想
	 * 
	 * @param phonenum
	 *            手机号
	 * @param label
	 *            标志
	 * @param appKey
	 *            私钥
	 * @param service
	 *            接口标识
	 * @param payChannel
	 *            充值方式
	 * @return
	 */
	public String JointUrl(String phonenum, String label, String appKey, String service, String payChannel) {
		// 拼接签名信息
		List<String> list = new ArrayList<String>();
		list.add(phonenum);
		list.add(label);
		list.add(appKey);
		list.add(service);
		list.add(payChannel);

		// 拼接需要提交的信息
		JSONObject param = new JSONObject();
		param.put("phonenum", phonenum);
		param.put("payChannel", payChannel);
		param.put("service", service);
		param.put("label", label);
		param.put("sign", getSign(list));

		return param.toJSONString();
	}

	public static String getSign(List<String> params) {
		String[] paramArray = params.toArray(new String[0]);
		Arrays.sort(paramArray);
		StringBuilder sb = new StringBuilder();
		for (String s : paramArray) {
			if (s != null && !"null".equals(s)) {
				sb.append(s);
			}
		}
		return SHA.getSHA(sb.toString());
	}
}
