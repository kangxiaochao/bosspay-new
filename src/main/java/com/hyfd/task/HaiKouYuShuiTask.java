package com.hyfd.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class HaiKouYuShuiTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;										// 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单

	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	
	private static Logger log = Logger.getLogger(HaiKouYuShuiTask.class);
	
	@Scheduled(fixedDelay = 60000)																//每隔60秒调用一次
	public void queryHaiKouYuShuiOrder() {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String id="2000000042";															
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);		//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");				//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String queryUrl = paramMap.get("queryUrl");											//订单查询接口地址
			String partnerId = paramMap.get("partnerId");										//商户编码
			String partnerRequestTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");	//系统时间
			String sign = paramMap.get("sign");													//签名
			
			//用于查询 orderId:流量话充平台订单号     partnerOrderId:上家订单号
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			
			// 查询出 id是‘2000000042’ 且 状态为1（正常） 保存到list集合当中
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			for(Map<String,Object> order : orderList){
				int flag = 2;
				String orderId = order.get("providerOrderId")+"";
				String partnerOrderId = order.get("orderId")+"";
				String result = sendJson(queryUrl,partnerId,orderId,partnerOrderId,partnerRequestTime,sign);
				
				map.put("orderId", partnerOrderId);
				
				if(result == null || result.equals("")) {
					log.error("海口雨水文化查询job返回值为空 orderids=" + partnerOrderId);
				}else {
					JSONObject response = JSONObject.parseObject(result);
					map.put("resultCode",response.getString("resultCode")+":"+response.getString("resultMessage"));
					if(response.containsKey("bizData")){
						JSONObject bizData = response.getJSONObject("bizData");						//获取  订单查询接口操作后的业务数据   bizData（json类型的）
						String rechargeStatus = bizData.getString("rechargeStatus");				//通过获取的 bizData，来查询充值是否成功
						if(rechargeStatus.equals("SUCCESS")) {
							flag = 1;
						}else if(rechargeStatus.equals("PROCESS") || rechargeStatus.equals("TIMEOUT")) {
							// PROCESS : 正在处理充值结果无法确认，需后续主动查询充值结果或等待我方异步通知            TIMEOUT : 充值超时：原因待分析。     	
							continue;
						}else {																		//充值失败，已退款 
							flag = 0;
						}	
						
						map.put("status", flag);
						mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
					}
					
				}
				
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			log.error("海口雨水文化查询Task出错"+e);
		}
		
		
	}
	
	
	/*
	 *json提交
	 */
	public static String sendJson(String queryUrl,String partnerId,String orderId,String partnerOrderId,String partnerRequestTime,String sign){
		
		JSONObject json = new JSONObject(new TreeMap<String, Object>());
		json.put("partnerId",partnerId);								//商户编码
		json.put("orderId",orderId);									//流量话充平台订单号
		json.put("partnerOrderId",partnerOrderId);						//商户流水号
		json.put("partnerRequestTime",partnerRequestTime);				//系统时间
		String string1="";
		for(Entry<String, Object> entry : json.entrySet()){
			string1=string1+entry.getValue()+"|";
		}
		String sign2 = MD5.MD5(string1+sign).toLowerCase();				//生成加密后sign	
		
		json.put("sign",sign2);
		
		
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(queryUrl);
        String result = "";
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                result = EntityUtils.toString(res.getEntity());// 返回json格式：
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
	
	
}
