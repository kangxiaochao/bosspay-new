package com.hyfd.deal.Bill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.SHA;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 民生话费新平台处理
 * 
 * @param obj
 */
public class NewMinShengBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phonenum = (String) order.get("phone");// 手机号
			double fee = (double) order.get("fee");// 金额，以元为单位
			String recharge = String.valueOf((int) (fee) * 100);// 充值金额，以分为单位
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = (String) channel.get("link_url");// 充值地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String label = paramMap.get("label");
			String service = "agent.synrecharge";
			String payChannel = paramMap.get("payChannel");
			String appKey = paramMap.get("appKey");
			String orderCode = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + phonenum
					+ ((int) (Math.random() * 9000) + 1000);
			map.put("orderId", orderCode);

			// 拼装充值请求参数
			String param = JointUrl(recharge, phonenum, orderCode, label, appKey, service, payChannel);

			String result = HttpUtils.doPost(linkUrl, param);
			if (!StringUtil.empty(result)) {
				JSONObject resultJson = JSONObject.parseObject(result);
				String ReCode = resultJson.getString("code");
				JSONObject data = JSONObject.parseObject(resultJson.getString("data"));
				map.put("resultCode", ReCode + ":" + new String(resultJson.getString("data").getBytes(),"UTF-8"));
				if ("10200".equals(ReCode)) {
					flag = 3;
					map.put("providerOrderId", data.getString("orderCode"));
				} else {
					flag = 4;
				}
				log.error("民生充值完成，返回信息为" + result);
			} else {// 未拿到返回数据
				map.put("resultCode", "未拿到返回数据");
			}
		} catch (Exception e) {
			log.error("民生充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 拼接信息想
	 * 
	 * @param recharge
	 *            充值金额/分
	 * @param phonenum
	 *            手机号
	 * @param orderCode
	 *            订单号
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
	public String JointUrl(String recharge, String phonenum, String orderCode, String label, String appKey,
			String service, String payChannel) {
		// 拼接签名信息
		List<String> list = new ArrayList<String>();
		list.add(recharge);
		list.add(phonenum);
		list.add(orderCode);
		list.add(label);
		list.add(appKey);
		list.add(service);
		list.add(payChannel);

		// 拼接需要提交的信息
		JSONObject param = new JSONObject();
		param.put("phonenum", phonenum);
		param.put("orderCode", orderCode);
		param.put("recharge", recharge);
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
