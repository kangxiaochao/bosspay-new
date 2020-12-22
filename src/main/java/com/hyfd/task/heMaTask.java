package com.hyfd.task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class heMaTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	@Autowired
	OrderDao orderDao;// 订单
	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者
	private static Logger log = Logger.getLogger(heMaTask.class);
	
	@Scheduled(fixedDelay = 60000)
	public void queryHeMaOrder() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id = "2000000062";														//河马物理通道ID ~~~~~
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");			// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String username = paramMap.get("username");										//账号
	        String password = paramMap.get("password");										//密码
	 		Map<String, Object> param = new HashMap<String, Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			log.info("heMa定时任务执行--------------------");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList) {
				//查询token url
		        String queryTokenUrl = paramMap.get("queryTokenUrl") + "?username=" + username + "&password=" + password;
		        //销毁token url
		        String destroyTokenUrl = paramMap.get("destroyTokenUrl") + username;
				// 查单地址
				String queryUrl = paramMap.get("queryUrl")+"?orderNo=";
				int flag = 2;
				String orderId = order.get("orderId") + "";
				map.put("orderId", orderId);
				queryUrl = queryUrl + orderId;
				//获取token
				String X_AUTH_TOKEN = ToolHttp.post(false, queryTokenUrl, null, "application/text");
				Map<String, String> headerMap = new HashMap<>();
				headerMap.put("X-AUTH-TOKEN",X_AUTH_TOKEN);
				String result = ToolHttp.post(false,headerMap ,queryUrl, null, "application/text");
				log.info("查询河马订单充值结果：" + "["+orderId+"] "+result);
				if(result != null && !(result.equals(""))) {
					//销毁token
					httpDelete(destroyTokenUrl, null, "application/text");
					//解析返回参数
					JSONObject response = JSONObject.parseObject(result);
					String respCode = response.get("respCode")+"";
					//没有查询到该订单号对应记录
					if("-1".equals(respCode)) {
						continue;
					}
					if("0".equals(respCode)) {
						// 状态（"待充值_1","充值成功_2","充值失败_3"）
						JSONObject data = response.getJSONObject("data");
						log.info("查询河马订单详情-------=-" + data);
						if("3".equals(data.get("status")+"")) {
							flag = 0;
		    				log.debug("河马充值失败：" + orderId + ":" + response.toString());
						}else if("2".equals(data.get("status")+"")) {
							flag = 1;
							log.debug("河马充值成功：" + orderId + ":" + response.toString());
						}else if("1".equals(data.get("status")+"")) {
							//充值中直接跳过查询，等待充值成功在查
		    				continue;
						}else {
							flag = 0;
							log.debug("河马充值异常返回未知状态码：" + orderId + ":" + response.toString());
						}
					}
					map.put("status", flag);
	                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
	    		}
			}
		}catch (Exception e) {
			// TODO: handle exceptionW
			log.error("河马查询Task出错" + e);
		}
	}
	
	// 销毁token
	public static void httpDelete(String url, Map<String, String> headers, String encode) {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		// 创建Delete请求
		HttpDelete httpDelete = new HttpDelete(url);
		// 响应模型
		CloseableHttpResponse response = null;
		try {
			// 配置信息
			RequestConfig requestConfig = RequestConfig.custom()
					// 设置连接超时时间(单位毫秒)
					.setConnectTimeout(5000)
					// 设置请求超时时间(单位毫秒)
					.setConnectionRequestTimeout(5000)
					// socket读写超时时间(单位毫秒)
					.setSocketTimeout(5000)
					// 设置是否允许重定向(默认为true)
					.setRedirectsEnabled(true).build();

			// 将上面的配置信息 运用到这个Delete请求里
			httpDelete.setConfig(requestConfig);

			// 由客户端执行(发送)Delete请求
			response = httpClient.execute(httpDelete);

			// 从响应模型中获取响应实体
			HttpEntity responseEntity = response.getEntity();
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，防止相应出现乱码
				System.out.println("响应内容为:" + EntityUtils.toString(responseEntity, StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
