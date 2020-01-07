package com.hyfd.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.Bill.MaiYuanBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

/**
 * @功能描述：	可查询充值记录
 *
 * @作者：zhangpj		@创建时间：2018年5月7日
 */
@Component
public class MaiYuanTask {
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(MaiYuanTask.class);
    
    @Scheduled(fixedDelay = 60000)
	public void queryMaiYuanOrder() {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String id = "2000000029";// 麦远物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String url = paramMap.get("url") + ""; // 麦远提供 请求地址
			String action = paramMap.get("queryReportAction") + ""; // 麦远提供查询方法名称
			String v = paramMap.get("v") + ""; // 麦远提供 版本号
			String account = paramMap.get("account") + ""; // 麦远提供 帐号
			String apiKey = paramMap.get("apiKey") + ""; // 麦远提供 密钥

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);
				
				String resultStr = queryReport(url, action, v, account, "", orderId, apiKey);
				JSONObject jsonObject = JSONObject.parseObject(resultStr);
				
				String code = String.valueOf(jsonObject.get("Code"));

				if ("0".equals(code)) {
					JSONObject reportsJsonObject = jsonObject.getJSONObject("Reports");
					String status = String.valueOf(reportsJsonObject.get("Status"));
					if ("3".equals(status) || "5".equals(status)) {
						flag = 0;	// 充值失败
					} else if ("4".equals(status)) {
						flag = 1;	// 充值成功
					}
				}
				map.put("status", flag);
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			log.error("麦远查询Task出错" + e);
		}
	}
	
	// ---------------------------- private ----------------------------
	
	/**
	 * @功能描述：	查询订单
	 *
	 * @param url 请求地址
	 * @param action 请求方法
	 * @param v 版本号
	 * @param account 账号
	 * @param taskID 上家流水id
	 * @param outTradeNo 我方平台流水
	 * @param apiKey 密钥
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月22日
	 */
	private static String queryReport(String url, String action, String v, String account, String taskID , String outTradeNo, String apiKey){
		String sendTime = DateUtils.getNowTime("yyyy-MM-dd");
		
		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		
		String sign = MaiYuanBillDeal.creatSign(map,apiKey);
		
		String resultStr = "";
		try {
			map.put("action", action);
			map.put("v", v);
			map.put("taskID",taskID);
			map.put("outTradeNo",outTradeNo);
			map.put("sendTime",sendTime);
			map.put("sign", sign);
			
			// 生成请求参数
			url += MaiYuanBillDeal.creatParam(map, apiKey);
			resultStr = ToolHttp.get(false, url);
		} catch (Exception e) {
			log.error("订单号为["+outTradeNo+"]的麦远定时任务查询发生异常");
			e.printStackTrace();
		}
		return resultStr;
	}
}
