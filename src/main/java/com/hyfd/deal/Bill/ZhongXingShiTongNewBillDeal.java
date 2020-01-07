package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ZhongXingShiTongNewBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(ZhongXingShiTongNewBillDeal.class);

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			String spec = new Double(fee).intValue() * 100 + ""; //充值金额，以分为单位,取整数,不要小数

			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = channel.get("link_url").toString(); // 充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String channalId = paramMap.get("ChannalId") + ""; // 中兴提供 代理商充值渠道ID
			String channalPwd = paramMap.get("ChannalPwd") + ""; // 中兴提供, 代理商充值渠道密码

			// 生成自己的id，供回调时查询数据使用
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + getThrSquece();
			map.put("orderId", curids);

			String xml = sendNew(linkUrl, channalId, channalPwd, curids, phoneNo, spec);
			Map<String, String> resultMap = readResultXmlToMap(xml);
			String status = String.valueOf(resultMap.get("status"));
			if (status.equals("overtime")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "中兴话费充值,号码[" + phoneNo + "],金额[" + spec + "(分)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			} else if (status.equals("underway")) {
				// 解析xml文件发生异常
				flag = -1;
				String msg = "中兴话费充值,号码[" + phoneNo + "],金额[" + spec + "(分)],解析返回数据发生异常,xml内容[" + xml + "]";
				map.put("resultCode", msg);
				log.error(msg);
			} else {
				String resultCode = resultMap.get("code");
				String msg = resultMap.get("msg");
				map.put("resultCode", resultCode+":"+msg);
				
				//状态码，只有200表示成功，其他表示失败
				if ("200".equals(resultCode)) {
					flag = 1;
				} else {
					flag = 0;
				}
			}
		} catch (Exception e) {
			log.error("中兴视通话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	/**
	 * @功能描述：	中兴充值接口(新)
	 *
	 * @param url 请求地址
	 * @param channel 渠道编码
	 * @param pwd 渠道密码
	 * @param orderNo 提交订单编号
	 * @param phone 充值手机号
	 * @param money 充值金额,单位:分
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月14日
	 */
	public String sendNew(String url, String channel, String pwd, String orderNo, String phone, String money) {
		String xml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.seecom.com.cn/webservice\">"+
				"   <soapenv:Header/>"+
				"   <soapenv:Body>"+
				"      <web:getChargeRequest>"+
				"         <web:charge>"+
				"            <web:channel>" + channel + "</web:channel>"+
				"            <web:pwd>" + pwd + "</web:pwd>"+
				"            <web:orderNo>" + orderNo + "</web:orderNo>"+
				"            <web:phone>" + phone + "</web:phone>"+
				"            <web:money>" + money + "</web:money>"+
				"         </web:charge>"+
				"      </web:getChargeRequest>"+
				"   </soapenv:Body>"+
				"</soapenv:Envelope>";
		String resultXml = ToolHttps.post(false, url, xml, "text/xml");
		
		return resultXml;
	}
	
	/**
	 * @功能描述：	解析提交结果
	 *
	 * @param xml
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月14日
	 */
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
	
	public static void main(String[] args) {
		String url = "http://14.215.135.10:18097/ws";
		String xml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.seecom.com.cn/webservice\">"+
					"   <soapenv:Header/>"+
					"   <soapenv:Body>"+
					"      <web:getChargeRequest>"+
					"         <web:charge>"+
					"            <web:channel>205</web:channel>"+
					"            <web:pwd>fea1920da4045adeafda10bcd47f3c9f</web:pwd>"+
					"            <web:orderNo>446666</web:orderNo>"+
					"            <web:phone>17060226666</web:phone>"+
					"            <web:money>12</web:money>"+
					"         </web:charge>"+
					"      </web:getChargeRequest>"+
					"   </soapenv:Body>"+
					"</soapenv:Envelope>";
		
		String resultXml = ToolHttps.post(false, url, xml, "text/xml");
		System.out.println(resultXml);
		
		Map<String, String> readXmlToMapFromQueryResponse = readResultXmlToMap(resultXml);
		for (String key : readXmlToMapFromQueryResponse.keySet()) {
			System.out.println(key + "= " +readXmlToMapFromQueryResponse.get(key));
		}
		
	}
}
