package com.hyfd.task;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
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

/**
 * 连连科技查询
 * @author Administrator
 */
@Component
public class LianLianKeJiTask {
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao; // 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单


	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(LianLianKeJiTask.class);

//	@Scheduled(fixedDelay = 60000)	 连连科技老接口停用，新接口暂无订单查询接口
	public void queryManFanOrder() {	
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id="2000000052";															
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);		//获取通道的数据
			String defaultParameter = channel.get("default_parameter")+"";						//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		    String agentId = paramMap.get("agentId");											//用户编码
			String signKey = paramMap.get("signKey");											//秘钥
			String queryUrl = paramMap.get("queryUrl");										    //查询地址
			String timestamp = String.valueOf(new Date().getTime()/1000);  
			String ts =  Integer.valueOf(timestamp)+"";											//当前时间，格式秒
			Map<String, Object> param = new HashMap<String, Object>();
	        param.put("dispatcherProviderId", id);
	        param.put("status", "1");
	        List<Map<String, Object>> orderList = orderDao.selectByTask(param);
	        log.error("LianLianKeJi查询执行："+orderList+"-商户账号："+agentId+"-查询链接："+queryUrl);
	        for (Map<String, Object> order : orderList){
	        	int flag = 2;
	        	String upids = order.get("providerOrderId") + "";
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                map.put("providerOrderId", upids);
	    		String sign = md5Encode(agentId+ts+signKey);
	    		JSONObject json = new JSONObject();
				json.put("agent_id",agentId);
				json.put("sign",sign);
				json.put("trade_no",orderId);
				json.put("ts",ts);
				String result = ToolHttp.post(false,queryUrl,json.toJSONString(),null);				
				if(result != null && !"".equals(result)){
					JSONObject jsonObject = JSONObject.parseObject(result);
					String status = jsonObject.getString("error");						//返回码
					JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
					String order_status = jsonData.getString("order_status");
					if(status.equals("0")) {
						if("1".equals(order_status)) {
							//充值中直接跳过查询，等待充值成功在查
		    				continue;
						}else if("2".equals(order_status)) {
							flag = 1;	// 充值成功
							log.debug("连连科技充值成功：" + orderId + ":" + orderId + "返回结果："+jsonObject.toString());
						}else if("3".equals(order_status)) {
							flag = 0;	//充值失败
							log.debug("连连科技充值失败：" + orderId + ":" +  orderId + "返回结果："+jsonObject.toString());
						}
					}else {
						flag = 0;	// 提交异常
						log.debug("连连科技充值返回异常：" + orderId + ":" +  orderId + "返回结果："+jsonObject.toString());
					}
				}
	    		map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
	        }
		} catch (Exception e) {
			// TODO: handle exceptionW
			log.error("连连科技Task出错" + e);
		}
	}

	/**
	 * MD5加密 
	 * @param str
	 * @return 加密后小写
	 */
	public static String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
			String s = Integer.toHexString(bytes[i] & 0xff);
			if(s.length()==1){
			buf.append("0");
			}
			buf.append(s);
		}
		}
		catch(Exception ex){	
		}
		return buf.toString();
	}
}
