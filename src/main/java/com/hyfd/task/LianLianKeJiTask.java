package com.hyfd.task;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolDateTime;
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

	@Scheduled(fixedDelay = 60000)
	public void queryManFanOrder() {	
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id="2000000052";															
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);		//获取通道的数据
			String defaultParameter = channel.get("default_parameter")+"";						//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("appKey");												//key
            String requestType = paramMap.get("requestType_query");								//RECHARGE：充值，QUERY：查询，BUSINESS:业务
			String appSecret = paramMap.get("appSecret");										//秘钥
			String timeStamp = ToolDateTime.format(new Date(),"yyyyMMddHHmmss");				//时间戳，格式yyyyMMddHHmmss（年月日时分秒）
			String transactionId = "";															//订单号ID查询时为空
			String queryUrl = paramMap.get("queryUrl");											//查询地址
			Map<String, Object> param = new HashMap<String, Object>();
	        param.put("dispatcherProviderId", id);
	        param.put("status", "1");
	        List<Map<String, Object>> orderList = orderDao.selectByTask(param);
	        for (Map<String, Object> order : orderList){
	        	int flag = 2;
	        	String upids = order.get("providerOrderId") + "";
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                map.put("providerOrderId", upids);
                JSONObject responseJson = new JSONObject();										//请求提交参数 json格式
    		    responseJson.put("rechargeId",upids);
    		    Map<String, Object> parameterMap = new HashMap<String, Object>();
    			parameterMap.put("appKey",appKey);
    			parameterMap.put("requestMsg",responseJson.toJSONString());
    			parameterMap.put("requestType",requestType);
    			parameterMap.put("timeStamp",timeStamp);
    			parameterMap.put("transactionId",transactionId);
    			SortedMap<Object, Object> params = new TreeMap<Object, Object>(parameterMap);
    			String sign = createSign(params,appSecret);
    			JSONObject json = new JSONObject();
    			json.put("appKey",appKey);
    			json.put("requestMsg",responseJson);
    			json.put("requestType",requestType);
    			json.put("sign",sign);
    			json.put("timeStamp",timeStamp);
    			json.put("transactionId",transactionId);
				String result = ToolHttp.post(false,queryUrl,json.toJSONString(),null);			
				log.debug("连连科技查询返回结果成功：" + result);
				if(result != null && !"".equals(result)){
					JSONObject jsonObject = JSONObject.parseObject(result);
					String status = jsonObject.getString("code");						//返回码
					if(status.equals("0")) {
						JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("responseMsg"));
						String order_status = jsonData.getString("status");
						if("0".equals(order_status)) {
							//充值中直接跳过查询，等待充值成功在查
		    				continue;
						}else if("1".equals(order_status)) {
							flag = 1;	// 提交成功
						}else if("2".equals(order_status)) {
							flag = 0;	//充值失败
						}
					}else {
						flag = 0;	// 提交异常
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
	 * 拼接参数并加密
	 * @param params
	 * @param APP_SECRET
	 * @return MD5加密后的字符串
	 */
	public static String createSign(SortedMap<Object, Object> params, String APP_SECRET) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<Object, Object>> es = params.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + APP_SECRET);
        String sign = md5Encode(sb.toString());
        return sign;
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
