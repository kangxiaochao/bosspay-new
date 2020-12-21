package com.hyfd.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
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
public class DingXinTask {

	Logger log = Logger.getLogger(DingXinTask.class);
	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
	@Autowired
	OrderDao orderDao;
	@Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
	
	static Map<String, String> rltMap = new HashMap<String, String>();
	static Map<String, String> stateMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "无错误	请求已被后台接收");
		rltMap.put("1003", "用户ID错误	充值时可失败，查询时需人工处理");
		rltMap.put("1004", "用户IP错误	充值时可失败，查询时需人工处理");
		rltMap.put("1005", "用户接口已关闭	充值时可失败，查询时需人工处理");
		rltMap.put("1006", "加密结果错误	充值时可失败，查询时需人工处理");
		rltMap.put("1007", "订单号不存在	需在该订单提交2分钟后方可处理失败");
		rltMap.put("1011", "号码归属地未知	充值时可失败，查询时需人工处理");
		rltMap.put("1013", "手机对应的商品有误或者没有上架	充值时可失败，查询时需人工处理");
		rltMap.put("1014", "无法找到手机归属地	充值时可失败，查询时需人工处理");
		rltMap.put("1015", "余额不足	充值时可失败，查询时需人工处理");
		rltMap.put("1016", "QQ号格式错误	充值时可失败");
		rltMap.put("1017", "产品未分配用户，联系商务	充值时可失败");
		rltMap.put("1018", "订单生成失败	充值时可失败");
		rltMap.put("1019", "充值号码与产品不匹配	充值时可失败");
		rltMap.put("1020", "号码运营商未知	充值时可失败");
		rltMap.put("9998", "参数有误	充值时可失败");
		rltMap.put("9999", "系统错误 人工处理");

		stateMap.put("8", "等待扣款");
		stateMap.put("0", "充值中");
		stateMap.put("1", "充值成功");
		stateMap.put("2", "充值失败");
	}
	@Scheduled(fixedDelay = 600000)
	public void queryDingXinOrder(){
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String channelId = "2000000003";
			Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(channelId);// 获取通道的数据
            String linkUrl = channel.get("link_url") + "";
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String searchUrl = paramMap.get("searchUrl");
    		String userId = paramMap.get("userid");// 用户编号	由鼎信商务提供，如：HR0001
    		String pwd = paramMap.get("pwd");// 加密密码	由鼎信商务提供。（需大写）
    		String key = paramMap.get("key");// 加密秘钥 由鼎信商务提供
    		Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", channelId);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);//查询通道下提交成功的订单list
            for(Map<String,Object> order : orderList){
            	int flag = 2;
            	String orderId = (String) order.get("orderId");
            	map.put("orderId", orderId);
            	String resultXml = sendSearch(searchUrl,userId,pwd,orderId,key);
            	log.error("鼎信话费充值查单返回结果:"+resultXml);
            	if(resultXml == null || resultXml.equals("")){
            		continue;
            	}
            	Map<String, String> searchResult = readXmlToMapFromCreateResponse(resultXml);
    			String searchError = searchResult.get("error");//错误提示       0 无错误	请求已被后台接收
    			String searchState = searchResult.get("state");//订单状态        8等待扣款，0充值中，1充值成功，2充值失败
    			String searchStateMsg = stateMap.get(searchState);//获取订单状态
    			String searchErrorMsg = rltMap.get(searchError);//错误信息描述
    			String submitbackmsg = "error:" + searchError + " searchErrorMsg" +searchErrorMsg+ ",state:" + searchState + " " + searchStateMsg;
    			map.put("resultCode", submitbackmsg);
    			if("8".equals(searchState) || "0".equals(searchState) ) { //8等待扣款 0充值中
    				//处理中状态 正在处理
    				continue;
    			}
    			//(充值失败 返回2 ) 或者 (订单不存在、错误 重复请求超过次数的)。
    			if("2".equals(searchState) || ("1007".equals(searchError)  || "9999".equals(searchError))){
    				flag = 0;
    			}

    			if ("1".equals(searchState)) {
    				//成功状态处理
    				flag = 1;
					String phoneaccont = searchResult.get("accont");
					log.info("手机号为"+phoneaccont+"订单充值成功,订单id为"+orderId);
    			}
    			 map.put("status", flag);//mp:订单status,订单状态,订单id.
                 mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
		}catch(Exception e){
			log.error("鼎信的查询出错"+e.getMessage());
		}
	}
	
	/**
	 * 查询订单状态
	 * 
	 * @param url
	 * @param userId
	 *            用户编号 由鼎信商务提供，如：HR0001
	 * @param pwd
	 *            加密密码 由鼎信商务提供。（需大写）
	 * @param key加密密钥
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String sendSearch(String url, String userId, String pwd,
			String orderId, String key) throws ClientProtocolException, IOException {

		StringBuffer paramBuffer = new StringBuffer();

		// userid=xxx&pwd=xxx&orderid=xxx&account=xxx&=xxx&amount=1&userkey=xxx
		paramBuffer.append("userid=").append(userId);
		paramBuffer.append("&pwd=").append(pwd);
		paramBuffer.append("&orderid=").append(orderId);

		String userKey = "userid" + userId + "pwd" + pwd + "orderid" + orderId
				+ key;
		String md5UserKey = DigestUtils.md5Hex(userKey);
		paramBuffer.append("&userkey=").append(md5UserKey);

		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
		}
		url = url + "?" + queryString;
		System.out.println(url);
		String ret = "";
		ret = ToolHttp.get(false, url);
		return ret;

	}
	
	public static Map<String, String> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List<Element> l = rootElt.elements();
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				map.put(element.getName(), element.getStringValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
