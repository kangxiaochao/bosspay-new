package com.hyfd.task;

import java.security.MessageDigest;
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
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

public class ManFanTask {
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao; // 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单


	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者

	private static Logger log = Logger.getLogger(ManFanTask.class);

	@Scheduled(fixedDelay = 60000)
	public void queryYuanTeOrder() {
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
			String id="2000000054";															
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);		//获取通道的数据
			String defaultParameter = channel.get("default_parameter")+"";						//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		    String userId = paramMap.get("userId");												//用户编码
			String signKey = paramMap.get("apiKey");											//秘钥
			String queryUrl  = paramMap.get("queryUrl ");										//查询地址
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
	    		String sign = md5Encode(orderId+userId+signKey);
	    		String querysURL = queryUrl+"?sign="+sign+"&userId="+userId+"&serialno="+orderId;
	    		String result = ToolHttp.get(false,querysURL);
	    		if(result != null && !(result.equals(""))) {
	    			Map<String,String> utilsMap = readXmlToMap(result);
	    			String status =  utilsMap.get("status"); // 0或者1处理中 2成功3失败 4充值锁定中 (也是处理中) 9未确认
	    			String statusDesc = utilsMap.get("statusDesc");
	    			if(status.equals("2")) {
	    				flag = 1;
	    				log.debug("满帆充值成功：" + orderId + ":" + utilsMap.get("serialno")+"--"+status+statusDesc);
	    			}else if(status.equals("1") || status.equals("0") || status.equals("4")){
	    				//充值中直接跳过查询，等待充值成功在查
	    				continue;
	    			}else if(status.equals("3")){
	    				flag = 0;
	    				log.debug("满帆充值失败：" + orderId + ":" + utilsMap.get("serialno")+"--"+status+statusDesc);
	    			}
	    		}
	    		map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
	        }
		} catch (Exception e) {
			// TODO: handle exceptionW
			log.error("满帆查询Task出错" + e);
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
	
	/**
	 * 获取xml标签中的data子标签中的内容
	 * @param xml
	 * @return
	 */
	public static Map<String, String> readXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); 		// 将字符串转为XML
			Element rootElt = doc.getRootElement(); 	// 获取根节点
			List<Element> l = rootElt.elements();
			Element contactElem = rootElt.element("data");
		    List<Element> contactList = contactElem.elements();
		    for (Element e:contactList){
		    	map.put(e.getName(), e.getStringValue());
		    }    
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
