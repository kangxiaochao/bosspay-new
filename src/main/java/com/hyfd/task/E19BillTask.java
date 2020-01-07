package com.hyfd.task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class E19BillTask {

	Logger log = Logger.getLogger(getClass());
	
	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    @Autowired
    OrderDao orderDao;// 订单
    @Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	
    public static Map<String, String> rltMap = new HashMap<String, String>();
	
	static {
		rltMap.put("1", "正在处理");
		rltMap.put("2", "充值成功");
		rltMap.put("3", "部分成功");
		rltMap.put("4", "充值失败");
	}
    
    @Scheduled(fixedDelay = 60000)
	public void queryE19Order(){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String id = "2000000006";
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String agentId = paramMap.get("agentId");
			String merchantKey = paramMap.get("merchantKey");
			String source = paramMap.get("source");
			String returnType = paramMap.get("returnType");
			String backurl = "";
			Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
			List<Map<String, Object>> orderList = orderDao.selectByTask(param);
			for (Map<String, Object> order : orderList){
				int flag = 2;
				String orderId = order.get("orderId")+"";
				map.put("orderId", orderId);
				//去查询
				String verifystring = getDirectSearchVerify(agentId, backurl, returnType, orderId, source,
						merchantKey);
				String directSearchUrl = paramMap.get("directSearchUrl");// 订单查询地址
				directSearchUrl = directSearchUrl + "?agentid=" + agentId;
				directSearchUrl = directSearchUrl + "&backurl=" + backurl;
				directSearchUrl = directSearchUrl + "&returntype=" + returnType;
				directSearchUrl = directSearchUrl + "&orderid=" + orderId;
				directSearchUrl = directSearchUrl + "&source=" + source;
				directSearchUrl = directSearchUrl + "&verifystring=" + verifystring;
				log.error("19edirectSearchReq|" + directSearchUrl);
				String directSearchRespXml = ToolHttp.get(false, directSearchUrl);
				log.error("19edirectSearchRes|" + directSearchRespXml);
				Map<String, String> directSearchMap = getDirectSearchReturnData(directSearchRespXml);
				map.put("resultCode", directSearchMap.get("resultno")+":"+rltMap.get(directSearchMap.get("resultno")));
				if("1".equals(directSearchMap.get("resultno"))){
					continue;
				}else if("2".equals(directSearchMap.get("resultno"))){
					flag = 1;
				}else if("3".equals(directSearchMap.get("resultno"))){
					flag = -1;
				}else if("4".equals(directSearchMap.get("resultno"))){
					flag = 0;
				}
				map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		}catch(Exception e){
			log.error("19e查询出错"+e.getMessage());
		}
	}
	
	public static String getDirectSearchVerify(String agentid, String backurl, String returntype, String orderid,
			String source, String merchantKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("agentid=" + agentid);
		sb.append("&backurl=" + backurl);
		sb.append("&returntype=" + returntype);
		sb.append("&orderid=" + orderid);
		sb.append("&source=" + source);
		sb.append("&merchantKey=" + merchantKey);
		return getKeyedDigest(sb.toString(), "");
	}
	
	public static Map<String, String> getDirectSearchReturnData(String xml) throws Exception {
		Map<String, String> rm = new HashMap<String, String>();
		Document doc = DocumentHelper.parseText(xml);
		Element rootElt = doc.getRootElement(); // 获取根节点
		Iterator<?> it = rootElt.elementIterator();
		while (it.hasNext()) {
			Element recordEle = (Element) it.next();
			List<?> elList = recordEle.elements();
			for (int i = 0; i < elList.size(); i++) {
				Element el = (Element) elList.get(i);
				rm.put(el.attributeValue("name"), el.attributeValue("value"));
			}

		}
		return rm;
	}
	
	/**
	 * MD5加密
	 * 
	 * @author lks 2017年1月12日下午3:00:56
	 * @param strSrc
	 * @param key
	 * @return
	 */
	public static String getKeyedDigest(String strSrc, String key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes("UTF8"));

			String result = "";
			byte[] temp;
			temp = md5.digest(key.getBytes("UTF8"));
			for (int i = 0; i < temp.length; i++) {
				result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}

			return result;

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
