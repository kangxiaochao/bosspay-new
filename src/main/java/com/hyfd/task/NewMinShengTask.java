package com.hyfd.task;

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
public class NewMinShengTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(MinShengTask.class);

	@Scheduled(fixedDelay = 60000)
	public void queryMinShengOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000034";// 民生物流通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryOrderUrl = paramMap.get("queryOrderUrl");// 查询地址
			String appKey = paramMap.get("appKey");
			String service = "agent.synrechargequery";
			String label = paramMap.get("label");
			String payChannel = paramMap.get("payChannel");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");

			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String phonenum = order.get("phone") + "";
				String orderCode = order.get("orderId") + "";
				map.put("orderId", orderCode);
				String params = JointUrl(phonenum, orderCode, label, appKey, service, payChannel);
				// 查询结果
				String searchResult = HttpUtils.doPost(queryOrderUrl, params);
				JSONObject resultObject = JSONObject.parseObject(searchResult);
				log.error("民生查询返回信息:" + resultObject);
				if (resultObject.containsKey("code")) {
					JSONObject data = JSONObject.parseObject(resultObject.getString("data"));
					String status = data.getString("status");
					if ("10200".equals(resultObject.getString("code")) && "2".equals(status)) {// 充值成功
						flag = 1;
					} else if("1".equals(status)){
						//充值中直接跳过查询，等待充值成功在查
	    				continue;
					} else if("3".equals(status)){// 充值失败
						flag = 0;
					}
					map.put("providerOrderId", data.getString("orderCode"));
				}
				map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			log.error("民生查询Task出错" + e);
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
	public String JointUrl(String phonenum, String orderCode, String label, String appKey, String service,
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