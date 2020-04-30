package com.hyfd.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.SHA;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class NewMinShengTast {


	public static void main(String [] args) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {

			String appKey = "ZicpeJGgcQxBuuVulPtgHAFWf3MIFWkQ8HqnnTWSAk4";
			String service = "agent.synrechargequery";
			String label = "YFD001";
			String payChannel = "yfdpay";
			String queryOrderUrl = "http://api.ms170.cn:6060/mstx/pr/gateway.do";

			int flag = 2;
			String phonenum = "16729177660";
			String orderCode = "20200216012352167291776606181";
			map.put("orderId", orderCode);

			String params = JointUrl(phonenum, orderCode, label, appKey, service, payChannel);
			System.out.println(params);
			// 查询结果
			String searchResult = HttpUtils.doPost(queryOrderUrl, params);
			System.out.println(searchResult);
			JSONObject resultObject = JSONObject.parseObject(searchResult);
			if (resultObject.containsKey("code")) {
				JSONObject data = JSONObject.parseObject(resultObject.getString("data"));
				String status = data.getString("status");
				System.out.println(resultObject.getString("code"));
				if ("10200".equals(resultObject.getString("code")) && "2".equals(status)) {// 充值成功
					flag = 1;
					System.out.println("充值成功");
				} else if("1".equals(status)){
					System.out.println("处理中");
				} else if("3".equals(status)){// 充值失败
					flag = 0;
				}
				map.put("providerOrderId", data.getString("orderCode"));
			}
			map.put("status", flag);
			System.out.println(map.toString());
		} catch (Exception e) {
		}
	}

	/**
	 * 拼接信息想
	 * 
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
	public static String JointUrl(String phonenum, String orderCode, String label, String appKey, String service,
			String payChannel) {
		// 拼接签名信息
		List<String> list = new ArrayList<String>();
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
		param.put("payChannel", payChannel);
		param.put("service", service);
		param.put("label", label);
		param.put("sign",  getSign(list));

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