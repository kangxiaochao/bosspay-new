package com.hyfd.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;

public class zhongxingTest {
	
	
	public static void main(String [] args) {
		
		String xml = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:getChargeResponse xmlns:ns2=\"http://www.seecom.com.cn/webservice\"><ns2:orderNo>44666432466</ns2:orderNo><ns2:code>200</ns2:code><ns2:msg>充值请求完成</ns2:msg></ns2:getChargeResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>";
		Map<String, String> resultMap = readResultXmlToMap(xml);
		String status = String.valueOf(resultMap.get("status"));
		System.out.println(resultMap);
		
	}
	
	
	public static Map<String, String> readResultXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			if (null != xml && !xml.equals("")) {
				
				doc = DocumentHelper.parseText(xml); // 将字符串转为XML
				Element rootElt = doc.getRootElement(); // 获取根节点
				
				Element bodyElement = rootElt.element("Body");
				Element rechargeQueryRespElement = bodyElement.element("getChargeResponse");
				
				try{
					String orderNo=rechargeQueryRespElement.elementTextTrim("orderNo"); // 客户订单号
					String code=rechargeQueryRespElement.elementTextTrim("code");	// 状态码，只有200表示成功，其他表示失败
					String msg=rechargeQueryRespElement.elementTextTrim("msg");		// 描述信息
					
					resultMap.put("orderNo", orderNo);
					resultMap.put("code", code);
					resultMap.put("msg", msg);
				}catch(Exception e){
					e.printStackTrace();
					//发生异常以后设置为处理中
					resultMap.put("status", "underway");
				}
			} else {
				resultMap.put("status", "overtime");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	

}
