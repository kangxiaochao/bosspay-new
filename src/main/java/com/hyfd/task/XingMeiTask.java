package com.hyfd.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.RSAEncrypt;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class XingMeiTask {

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	OrderDao orderDao;//订单
	@Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	private static Logger log = Logger.getLogger(XingMeiTask.class);
	
	private static RSAEncrypt rsaEncrypt = new RSAEncrypt();
	
	@Scheduled(fixedDelay = 60000)
	public void queryXingMeiOrder(){
		Map<String,Object> map = new HashMap<String,Object>();

		try{
			String id = "2000000019";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String url = paramMap.get("url");//查询地址
			String type = "RechargeQry";
			String ServiceCode = "/ServiceBus/custView/cust/RechargeQry";
			String SYS_ID = paramMap.get("SYS_ID");
			String RequestId = new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date());  
			String RequestTime = format.format(new Date());
			String RequestUser = "haobai";
			String RequestSource = paramMap.get("RequestSource");
			String Type = "1";
			String TransactionID = paramMap.get("TransactionID");
			Date date = new Date();
		    DateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		    String BeginTime = format1.format(date);
		    Date afterDate = new Date(date.getTime() + 30000);
		    String EndTime = format.format(afterDate);
			    
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			
			for(Map<String,Object> order : orderList){
				int flag = 2;
				String orderId = order.get("orderId")+"";
				map.put("orderId", orderId);
				
				String value = order.get("providerOrderId")+"";
				
				String response = sendPost(url, type, RequestSource, RequestUser, RequestId, Type, value, BeginTime, 
				        EndTime, TransactionID, ServiceCode, SYS_ID, RequestTime);
				
				if (response == null) {
			        log.error("星美查询job返回值为空 orderids=" + value);
			    }else {
			    	  JSONObject responseJson = readXmlToJson(response);
			    	  String result = responseJson.getJSONObject("SvcCont").getJSONArray("SOO").getJSONObject(0).getJSONObject("RESP").getString("Result");
			    	  if(!"0".equals(result)){
			    		  //充值失败
			    		  flag = 0;
			    	  }else {
			    		  flag = 1;
			    	  }
			    	  map.put("status", flag);
			    	  mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}
		}catch(Exception e){
			log.error("星美查询Task出错"+e);
		}
	}	
	
	/**
	 * 发送查询请求
	 * @param url
	 * @param type
	 * @param RequestSource
	 * @param RequestUser
	 * @param RequestId
	 * @param Type
	 * @param Value
	 * @param BeginTime
	 * @param EndTime
	 * @param TransactionID
	 * @param ServiceCode
	 * @param SYS_ID
	 * @param ReqTime
	 * @return
	 */
	public static String sendPost(String url, String type,  String RequestSource, String  RequestUser,String RequestId, String Type,String Value,String BeginTime,String EndTime,
			String TransactionID,String ServiceCode,String SYS_ID,String ReqTime) {
		StringBuffer xml = new StringBuffer();
		xml.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"http://www.tydic.com/\"><SOAP-ENV:Header/><SOAP-ENV:Body><Business>");
		JSONObject jsonObject = new JSONObject(true);

		JSONObject tcpCont = new JSONObject();
		JSONObject svcCont = new JSONObject();
		JSONArray SOOArray = new JSONArray();
		JSONObject SOO = new JSONObject();

		Map<String, Object> map = new TreeMap<String, Object>(
              new Comparator<String>() {
                  public int compare(String obj1, String obj2) {
                      // 降序排序
                      return obj1.compareTo(obj2);
                  }
              });
			
		map.put("RequestSource",RequestSource );
		map.put("RequestUser",RequestUser );
		map.put("RequestId",RequestId );
		map.put("Type",Type );
		map.put("Value",Value );
		map.put("BeginTime",BeginTime );
		map.put("EndTime",EndTime );
		SOO.put("RECHARGEQRY_REQ", map);
		SOOArray.add(SOO);
		svcCont.put("SOO", SOOArray);
		//把MD5加密内容通过私钥签名
		String code = rsaEncrypt.MD5(svcCont.toString());
		//把MD5加密内容通过私钥签名
		String signatureInfo= rsaEncrypt.enc(code);
		// 把MD5加密内容通过私钥签名
		tcpCont.put("TransactionID", TransactionID);
		tcpCont.put("ReqTime", ReqTime);
		tcpCont.put("SignatureInfo", signatureInfo);
		tcpCont.put("ServiceCode", ServiceCode);
		tcpCont.put("SYS_ID", SYS_ID);
		jsonObject.put("TcpCont", tcpCont);
		jsonObject.put("SvcCont", svcCont);
		
		xml.append(jsonObject);
		xml.append("</Business></SOAP-ENV:Body></SOAP-ENV:Envelope>");

		String ret = ToolHttps.post(true, url, xml.toString(), "text/html;charset=UTF-8");
		return ret;
	}
	
	/**
	 * 将xml转为json串
	 * @param xml
	 * @return
	 */
	public static JSONObject readXmlToJson(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element body = rootElt.element("Body");
			String jsonStr = body.elementTextTrim("Business");

			JSONObject json = JSONObject.parseObject(jsonStr);

			return json;

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("解析出錯");
		return null;
	}
}
