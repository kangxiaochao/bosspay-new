package com.hyfd.service.mp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.SHA1;
import com.hyfd.common.utils.ToolMD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.JuhegonghuoCustomerDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.OrderOtherDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.QianmigonghuoCustomerDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.BaseService;
import com.hyfd.task.JuHeBillOrderCallback;
import com.hyfd.task.QianMiGongHuoBillOrderTask;

@Service
public class CallBackForProviderSer extends BaseService
{

	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	RabbitMqProducer mqProducer;

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;

	@Autowired
	OrderDao orderDao;

	@Autowired
	ExceptionOrderDao eOrderDao;

	@Autowired
	OrderOtherDao orderOtherDao;

	@Autowired
	QianmigonghuoCustomerDao qianmigonghuoCustomerDao;

	@Autowired
	JuhegonghuoCustomerDao juhegonghuoCustomerDao;

	/**
	 * 好亚飞达话费状态回调
	 * 
	 * @author lks 2016年12月13日上午11:20:33
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackForHyfdBill(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("orderId");// 获取订单id
		String customerOrderId = (String)param.get("customerOrderId");// 获取客户订单号
		String phoneNo = (String)param.get("phoneNo");// 获取手机号
		String spec = (String)param.get("spec");// 金额，以分为单位
		String scope = (String)param.get("scope");// 应用范围
		String status = (String)param.get("status");// 状态
		String signature = (String)param.get("signature");// 签名
		// TODO 签名验证
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", customerOrderId);
		map.put("providerOrderId", orderId);
		if (status.equals("success"))
		{
			map.put("status", 1);
		}
		else if (status.equals("fail"))
		{
			map.put("status", 0);
		}
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "success";
	}

	/**
	 * 获取request中的数据
	 * 
	 * @author lks 2017年4月1日下午2:48:15
	 * @param request
	 * @return
	 */
	public static String getRequestContext(HttpServletRequest request)
	{
		String str = "";
		try
		{
			StringBuilder sb = new StringBuilder();
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			str = sb.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> readXml(String xml)
	{
		Document doc = null;
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element head = rootElt.element("head");
			List<Element> headList = head.elements();

			for (Iterator<Element> iterator = headList.iterator(); iterator.hasNext();)
			{
				Element element = (Element)iterator.next();
				map.put(element.getName(), element.getStringValue());
			}
			Element body = rootElt.element("body");
			List<Element> bodyList = body.elements("item");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Element iterator : bodyList)
			{
				List<Element> itemList = iterator.elements();
				Map<String, Object> itemMap = new HashMap<String, Object>();
				for (Iterator<Element> iteratorItem = itemList.iterator(); iteratorItem.hasNext();)
				{
					Element element = (Element)iteratorItem.next();
					itemMap.put(element.getName(), element.getStringValue());
				}
				list.add(itemMap);
			}
			map.put("item", list);
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @功能描述： 蓝猫接口回调处理
	 *
	 * @作者：zhangjun @创建时间：2017年7月25日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackLanMao(HttpServletRequest request, HttpServletResponse response)
	{

		String backRequestXml = getRequestContext(request);

		log.error("蓝猫话费回调开始：[" + backRequestXml + "]");

		StringBuilder sb = new StringBuilder();
		Map<String, String> reqMap = readXmlToMapFromCreateResponse(backRequestXml);
		String streamNo = reqMap.get("StreamNo");
		String result = reqMap.get("flag");
		String desc = reqMap.get("message");
		String upids = reqMap.get("flowNumber");

		sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
		.append("<soapenv:Body>");
		sb.append("<StreamNo>").append(streamNo).append("</StreamNo>");
		if (StringUtils.isEmpty(backRequestXml))
		{
			sb.append("<ResultCode>").append(0).append("</ResultCode>");
			sb.append("<ResultInfo>").append("蓝猫回调获取信息为空").append("</ResultInfo>");
			sb.append("</soapenv:Body>").append("</soapenv:Envelope>");
			log.error("蓝猫回调失败,接收到的报文信息为[" + backRequestXml + "]");
			return sb.toString();
		}

		Map<String, Object> order = orderDao.selectByOrderId(streamNo);// 查询订单

		if (null == order)
		{
			log.error("蓝猫回调的数据查询为空，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>没有获取到此流水的相关信息</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}
		// 判断是否为重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("蓝猫重复回调，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>重复回调</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", streamNo);

		if (result.equals("0"))
		{
			// 充值成功
			map.put("status", 1);
			map.put("providerOrderId", upids);
			map.put("resultCode", result + ":" + desc);
		}
		else
		{
			// 充值失败
			map.put("status", 0);
			map.put("resultCode", result + ":" + desc);
		}
		// 加入订单状态修改队列
		mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));

		log.error("蓝猫话费回调成功，订单号为" + streamNo);
		sb.append("<ResultCode>0</ResultCode>");
		sb.append("<ResultInfo>接收回调信息成功</ResultInfo>");
		sb.append("</soapenv:Body>");
		sb.append("</soapenv:Envelope>");

		return sb.toString();
	}

	/**
	 * @功能描述： 联想接口回调处理
	 *
	 * @作者：lks @创建时间：2017年7月25日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackLianXiang(HttpServletRequest request, HttpServletResponse response)
	{

		String backRequestXml = getRequestContext(request);

		log.error("联想话费回调开始：[" + backRequestXml + "]");

		StringBuilder sb = new StringBuilder();
		Map<String, String> reqMap = readXmlToMapFromCreateResponse(backRequestXml);
		String streamNo = reqMap.get("StreamNo");
		String result = reqMap.get("flag");
		String desc = reqMap.get("message");
		String upids = reqMap.get("flowNumber");

		sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
		.append("<soapenv:Body>");
		sb.append("<StreamNo>").append(streamNo).append("</StreamNo>");
		if (StringUtils.isEmpty(backRequestXml))
		{
			sb.append("<ResultCode>").append(0).append("</ResultCode>");
			sb.append("<ResultInfo>").append("联想回调获取信息为空").append("</ResultInfo>");
			sb.append("</soapenv:Body>").append("</soapenv:Envelope>");
			log.error("联想回调失败,接收到的报文信息为[" + backRequestXml + "]");
			return sb.toString();
		}

		Map<String, Object> order = orderDao.selectByOrderId(streamNo);// 查询订单

		if (null == order)
		{
			log.error("联想回调的数据查询为空，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>没有获取到此流水的相关信息</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}
		// 判断是否为重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("联想重复回调，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>重复回调</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", streamNo);

		if (result.equals("0"))
		{
			// 充值成功
			map.put("status", 1);
			map.put("providerOrderId", upids);
			map.put("resultCode", result + ":" + desc);
		}
		else
		{
			// 充值失败
			map.put("status", 0);
			map.put("resultCode", result + ":" + desc);
		}
		// 加入订单状态修改队列
		mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));

		log.error("联想话费回调成功，订单号为" + streamNo);
		sb.append("<ResultCode>1</ResultCode>");
		sb.append("<ResultInfo>接收回调信息成功</ResultInfo>");
		sb.append("</soapenv:Body>");
		sb.append("</soapenv:Envelope>");

		return sb.toString();
	}
	
	/**
	 * @功能描述： 北纬接口回调处理
	 *
	 * @作者：zhangjun @创建时间：2017年7月25日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackBeiWei(HttpServletRequest request, HttpServletResponse response)
	{

		String backRequestXml = getRequestContext(request);

		log.error("北纬话费回调开始：[" + backRequestXml + "]");

		StringBuilder sb = new StringBuilder();
		Map<String, String> reqMap = readXmlToMapFromCreateResponse(backRequestXml);
		String streamNo = reqMap.get("StreamNo");
		String result = reqMap.get("flag");
		String desc = reqMap.get("message");
		String upids = reqMap.get("flowNumber");

		sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
		.append("<soapenv:Body>");
		sb.append("<StreamNo>").append(streamNo).append("</StreamNo>");
		if (StringUtils.isEmpty(backRequestXml))
		{
			sb.append("<ResultCode>").append(0).append("</ResultCode>");
			sb.append("<ResultInfo>").append("北纬回调获取信息为空").append("</ResultInfo>");
			sb.append("</soapenv:Body>").append("</soapenv:Envelope>");
			log.error("北纬回调失败,接收到的报文信息为[" + backRequestXml + "]");
			return sb.toString();
		}

		Map<String, Object> order = orderDao.selectByOrderId(streamNo);// 查询订单

		if (null == order)
		{
			log.error("北纬回调的数据查询为空，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>没有获取到此流水的相关信息</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}
		// 判断是否为重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("北纬重复回调，订单号为" + streamNo);
			sb.append("<ResultCode>1</ResultCode>");
			sb.append("<ResultInfo>重复回调</ResultInfo>");
			sb.append("</soapenv:Body>");
			sb.append("</soapenv:Envelope>");
			return sb.toString();
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", streamNo);

		if (result.equals("0"))
		{
			// 充值成功
			map.put("status", 1);
			map.put("providerOrderId", upids);
			map.put("resultCode", result + ":" + desc);
		}
		else
		{
			// 充值失败
			map.put("status", 0);
			map.put("resultCode", result + ":" + desc);
		}
		// 加入订单状态修改队列
		mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));

		log.error("北纬话费回调成功，订单号为" + streamNo);
		sb.append("<ResultCode>1</ResultCode>");
		sb.append("<ResultInfo>接收回调信息成功</ResultInfo>");
		sb.append("</soapenv:Body>");
		sb.append("</soapenv:Envelope>");

		return sb.toString();
	}
	
	/**
	 * 将xml字符串转换成map(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 * @作者：zhangjun @创建时间：2017年7月25日 发送充值请求后，上游返回的xml
	 * @return Map结构:<"StreamNo", string> 消息流水（3位系统标识 + yyyymmddhhmiss + 8位流水），同请求消息头<br />
	 *         <"ResultCode", string> 接收结果 0 成功 1失败<br />
	 *         <"ResultInfo", string> 失败原因<br />
	 */
	public static Map<String, String> readXmlToMapFromCreateResponse(String xml)
	{
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try
		{
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element recordEle1 = rootElt.element("Body");
			String streamNo = recordEle1.elementTextTrim("StreamNo");
			String flag = recordEle1.elementTextTrim("flag");
			String flowNumber = recordEle1.elementTextTrim("flowNumber");
			String resultCode = recordEle1.elementTextTrim("ResultCode");
			String resultInfo = recordEle1.elementTextTrim("ResultInfo");
			String message = recordEle1.elementTextTrim("message").replace(" ", "");
			map.put("StreamNo", streamNo);
			map.put("flag", flag);
			map.put("flowNumber", flowNumber);
			map.put("ResultCode", resultCode);
			map.put("ResultInfo", resultInfo);
			map.put("message", message);
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @功能描述： 享缴费接口回调处理
	 *
	 * @作者：zhangjun @创建时间：2017年7月25日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackXiangJiaoFei(HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = getMaps(request);
		// String agentId = param.get("AgentId")+"";
		// String telephone = param.get("Telephone ")+"";
		// String telephoneType = param.get("TelephoneType")+"";
		// String money = param.get("Money")+"";
		// String chargeMode = param.get("ChargeMode")+"";
		String requestId = param.get("RequestID") + "";
		// String extendInfo = param.get("ExtendInfo")+"";
		// String orderID = param.get("OrderID")+"";
		String result = param.get("Result") + "";
		// String inTime = param.get("InTime")+"";
		// String hmac = param.get("Hmac")+"";

		if (StringUtils.isEmpty(requestId) || StringUtils.isEmpty(result))
		{
			log.error("享缴费话费充值回调：订单编号[RequestID]或充值结果[Result]为null");
			return "fail";
		}
		// 查询订单
		Map<String, Object> order = orderDao.selectByOrderId(requestId);
		if (order == null)
		{
			log.error("享缴费回调的数据查询为空，订单号为" + requestId);
			return "fail";
		}

		if (!"1".equals(order.get("status")))
		{
			log.error("享缴费重复回调，订单号为" + requestId);
			return "fail";
		}
		map.put("orderId", requestId);

		if ("success-成功".equalsIgnoreCase(result))
		{// 充值成功
			map.put("status", 1);
		}
		else if ("failed-失败".equalsIgnoreCase(result))
		{// 充值失败
			map.put("status", 0);
		}
		else
		{// 如果接收到的回调信息既不是成功也不是失败,则直接返回接收失败
			map.put("status", 0);
		}
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		return "success";
	}

	/**
	 * @Title:statusBackSaiNong
	 * @Description:赛浓回调 TODO(这里用一句话描述这个方法的作用)
	 * @author CXJ
	 * @date 2017年7月26日 下午5:50:03
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @return String 返回类型
	 * @throws
	 */
	public String statusBackSaiNong(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = getMaps(request);
		String orderId = param.get("custom_bid") + "";// 上家订单号
		String proStatus = param.get("status") + "";// 订单状态

		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (order == null)
		{
			log.error("赛浓回调的数据查询为空，订单号为" + orderId);
			return "error";
		}
		String status = order.get("status") + "";
		if (!status.equals("1"))
		{
			log.error("赛浓重复回调，订单号为" + orderId);
			return "success";
		}
		map.put("orderId", orderId);
		if (proStatus.equals("16"))
		{// 充值成功
			map.put("status", 1);
		}
		else if (proStatus.equals("26"))
		{// 充值失败
			map.put("status", 0);
		}
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "success";
	}

	/**
	 * 19E回调
	 * 
	 * @author lks 2017年8月24日下午2:20:25
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBack19E(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = getMaps(request);
		String orderid = (String)param.get("orderid");
		String status = (String)param.get("status");
		String ordermoney = (String)param.get("ordermoney");
		String verifystring = (String)param.get("verifystring");
		String mobileBalance = (String)param.get("mobileBalance");
		log.error("19e回调开始：===orderid=" + orderid + "  status=" + status + "   ordermoney=" + ordermoney
				+ "   verifystring" + verifystring + "  mobileBalance=" + mobileBalance);
		if (StringUtils.isEmpty(orderid) || StringUtils.isEmpty(status) || StringUtils.isEmpty(ordermoney)
				|| StringUtils.isEmpty(verifystring))
		{
			return status;
		}

		Map<String, Object> order = orderDao.selectByOrderId(orderid);
		if (order == null)
		{
			log.error("19e回调" + orderid + "查询订单为空");
			return status;
		}
		// 如果当前状态不等于1后面就不处理了 需要处理五处 vsked20151028 add
		if (!"1".equals(order.get("status")))
		{
			log.error("平台订单号" + orderid + "重复回调不处理");
			return status;
		}
		map.put("orderId", orderid);
		String myFailState = "充值成功";
		if (status.equals("2"))
		{// 成功
			map.put("status", 1);
		}
		else
		{
			if (status.equals("3"))
				myFailState = "部分成功";
			if (status.equals("4"))
				myFailState = "充值失败 ";
			if (status.equals("1009"))
				myFailState = "充值失败,没有对应订单 ";
			map.put("status", 0);

		}
		map.put("resultCode", status + ":" + myFailState);
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return status;
	}

	/**
	 * 鼎信回调
	 * 
	 * @author lks 2017年8月24日下午3:01:33
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackDingxin(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = getMaps(request);
		String userId = (String)param.get("userid");
		String orderId = (String)param.get("orderid");
		String state = (String)param.get("state");
		String account = (String)param.get("account");
		String userkey = (String)param.get("userkey");
		log.error("鼎信回调开始：userId=" + userId + ",orderId=" + orderId + ",state=" + state + ",account=" + account
				+ ",userkey=" + userkey);

		StringBuilder sb = new StringBuilder();
		if (StringUtils.isEmpty(orderId))
		{
			return sb.append("0001").append("</result><desc>参数为空</desc></response>").toString();
		}

		/** sunzhe end **/
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (order == null)
		{
			log.error("鼎信回调orderId = " + orderId + "查询订单为空");
			return "fail";
		}
		// 如果当前状态不等于1后面就不处理了 需要处理五处 vsked20151028 add
		if (!"1".equals(order.get("status")))
		{
			log.error("鼎信回调平台订单号" + orderId + "重复回调不处理");
			return "平台订单号" + orderId + "重复回调不处理";
		}
		map.put("orderId", orderId);
		if (state.equals("1"))
		{
			map.put("status", 1);
		}
		else
		{
			map.put("status", 0);
		}
		map.put("resultCode", state);
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "OK";
	}

	public String statusBackYuanTe(HttpServletRequest request, HttpServletResponse response)
	{
		// 1.接收远特回调数据
		String xmlstr = getRequestContext(request);
		log.error("远特回调开始：回调信息[" + xmlstr + "]");

		// 2.验证远特回调数据是否为空
		if (null == xmlstr || "".equals(xmlstr))
		{
			log.error("远特回调数据为空");

			// 返回接收结果 0 成功 1失败
			StringBuffer xml = new StringBuffer();
			xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			xml.append("<soapenv:Body>");
			xml.append("<StreamNo>未知</StreamNo>");
			xml.append("<ResultCode>0</ResultCode>");
			xml.append("<ResultInfo>获取到请求信息为null</ResultInfo>");
			xml.append("</soapenv:Body>");
			xml.append("</soapenv:Envelope>");
			return xml.toString();
		}

		// 3.解析远特回调数据
		Map<String, String> xmlMap = readXmlToMapFromCreateResponse(xmlstr);
		String orderId = xmlMap.get("StreamNo");
		String result = xmlMap.get("flag");
		String desc = xmlMap.get("message");
		String upids = xmlMap.get("flowNumber");

		// 4.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		Map<String, Object> eOrder = eOrderDao.selectByOrderId(orderId);
		if (order == null && eOrder == null)
		{
			log.error("远特回调orderId = " + orderId + "查询订单为空");

			// 返回接收结果 0 成功 1失败
			StringBuffer xml = new StringBuffer();
			xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			xml.append("<soapenv:Body>");
			xml.append("<StreamNo>" + orderId + "</StreamNo>");
			xml.append("<ResultCode>0</ResultCode>");
			xml.append("<ResultInfo>没有获取到流水对应的订单信息</ResultInfo>");
			xml.append("</soapenv:Body>");
			xml.append("</soapenv:Envelope>");
			return xml.toString();
		}

		// 5.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("远特回调平台订单号[" + orderId + "]重复回调不处理");
			// 返回接收结果 0 成功 1失败
			StringBuffer xml = new StringBuffer();
			xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
			xml.append("<soapenv:Body>");
			xml.append("<StreamNo>" + orderId + "</StreamNo>");
			xml.append("<ResultCode>0</ResultCode>");
			xml.append("<ResultInfo>此流水已处理,不再进行重复处理</ResultInfo>");
			xml.append("</soapenv:Body>");
			xml.append("</soapenv:Envelope>");
			return xml.toString();
		}

		// 6.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("providerOrderId", upids);
		map.put("resultCode", result + ":" + desc);
		map.put("orderId", orderId);
		if (result.equals("0"))
		{
			map.put("status", 1);
		}
		else
		{
			map.put("status", 0);
		}

		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		// 7. 返回接收结果给远特 0 成功 1失败
		StringBuffer xml = new StringBuffer();
		xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		xml.append("<soapenv:Body>");
		xml.append("<StreamNo>" + orderId + "</StreamNo>");
		xml.append("<ResultCode>0</ResultCode>");
		xml.append("<ResultInfo>接收回调信息成功!</ResultInfo>");
		xml.append("</soapenv:Body>");
		xml.append("</soapenv:Envelope>");
		return xml.toString();
	}

	/**
	 * @功能描述： 爱施德回调处理
	 *
	 * @作者：zhangjun @创建时间：2017年8月26日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackAiShiDe(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("orderId");
		String status = (String)param.get("status");

		StringBuilder sb = new StringBuilder();
		// 返回参数为空
		if (StringUtils.isEmpty(orderId))
		{
			return sb.append("0001").append("</result><desc>参数为空</desc></response>").toString();
		}

		// 根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (null == order)
		{
			log.error("爱施德回调查不到订单");
			return "fail";
		}

		// 判断是否为重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("平台订单号" + orderId + "重复回调不处理");
			return "平台订单号" + orderId + "重复回调不处理";
		}
		map.put("orderId", orderId);
		if ("0".equals(status))
		{// 充值成功
			map.put("status", "1");
		}
		else
		{// 其他情况做失败处理
			map.put("status", "0");
		}

		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		return "success";
	}

	/**
	 * @功能描述：银盛回调
	 *
	 * @作者：zhangpj @创建时间：2017年8月26日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackYinSheng(HttpServletRequest request, HttpServletResponse response)
	{
		// 1.接收银盛回调数据
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("termTransID");
		String transID = (String)param.get("transID");
		String result = (String)param.get("result");
		String userNumber = (String)param.get("userNumber");
		log.error("银盛回调开始：回调信息[termTransID=" + orderId + ",transID=" + transID + ",result=" + result + ",userNumber="
				+ userNumber + "]");

		// 2.验证银盛回调数据是否为空
		if (null == orderId || "".equals(orderId) || null == result || "".equals(result))
		{
			log.error("银盛回调数据异常[termTransID=" + orderId + ",result=" + result + "]");
			return "error";
		}

		// 3.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (order == null)
		{
			log.error("银盛回调orderId = " + orderId + "查询订单为空");
			return "error";
		}

		// 4.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("银盛回调平台订单号[" + orderId + "]重复回调不处理");
			return "error";
		}

		// 5.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", result);
		map.put("orderId", orderId);
		if (result.equals("0"))
		{
			// 充值成功
			map.put("status", 1);
		}
		else if (result.equals("1"))
		{
			// 充值失败
			map.put("status", 0);
		}

		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		// 6. 返回接收结果给远特 0 成功 1失败
		return "SUCCESS";
	}

	/**
	 * @Title:statusBackZiTeng
	 * @Description:紫藤回调 TODO(这里用一句话描述这个方法的作用)
	 * @author CXJ
	 * @date 2017年8月28日 上午11:50:52
	 * @param @param request
	 * @param @param response
	 * @param @return
	 * @return String 返回类型
	 * @throws
	 */
	public String statusBackZiTeng(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> param = getMaps(request);
		String cpid = param.get("cpid") + "";
		String trade_type = param.get("trade_type") + "";
		String operator = param.get("operator") + "";
		String sign = param.get("sign") + "";
		String mobile_num = param.get("mobile_num") + "";
		String status = param.get("status") + "";
		String zt_order_no = param.get("zt_order_no") + "";
		String cp_order_no = param.get("cp_order_no") + "";
		String charge_time = param.get("charge_time") + "";
		String amount = param.get("amount") + "";
		log.error("紫藤回调开始：===cpid=" + cpid + "  trade_type=" + trade_type + "   operator=" + operator + "   sign"
				+ sign + "  mobile_num=" + mobile_num + "  status=" + status + "  zt_order_no=" + zt_order_no
				+ "  cp_order_no=" + cp_order_no);
		if (StringUtils.isEmpty(cpid) || StringUtils.isEmpty(trade_type) || StringUtils.isEmpty(operator)
				|| StringUtils.isEmpty(charge_time) || StringUtils.isEmpty(mobile_num) || StringUtils.isEmpty(zt_order_no)
				|| StringUtils.isEmpty(cp_order_no) || StringUtils.isEmpty(amount) || StringUtils.isEmpty(status)
				|| StringUtils.isEmpty(sign))
		{
			log.error("紫藤回调：参数存在空值~！" + mobile_num);
			return "cpid=" + cpid + "&cp_order_no=" + cp_order_no + "&ret_result=1111";
		}
		// 3.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(cp_order_no);
		if (order == null)
		{
			log.error("紫藤回调orderId = " + cp_order_no + "查询订单为空");
			return "cpid=" + cpid + "&cp_order_no=" + cp_order_no + "&ret_result=1111";
		}
		// 4.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("紫藤回调平台订单号[" + cp_order_no + "]重复回调不处理");
			return "cpid=" + cpid + "&cp_order_no=" + cp_order_no + "&ret_result=1111";
		}
		// 5.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", status);
		map.put("orderId", cp_order_no);
		if ("SUCCESS".equals(status))
		{
			// 充值成功
			map.put("status", 1);
		}
		else if ("FAILED".equals(status))
		{
			// 充值失败
			map.put("status", 0);
		}
		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "cpid=" + cpid + "&cp_order_no=" + cp_order_no + "&ret_result=0000";
	}

	/**
	 * @功能描述：	海航话费充值回调
	 *
	 * @作者：zhangpj		@创建时间：2017年8月31日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackHaiHang(HttpServletRequest request, HttpServletResponse response)
	{
		// 1.接收银盛回调数据
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("orderid");	// 定单号
		String state = (String)param.get("state");		// 充值结果，0-成功，1-失败 2-正在充值中
		String account = (String)param.get("account");	// 充值号码
		log.error("海航回调开始：回调信息[orderId=" + orderId + ",state=" + state + ",account=" + account + "]");

		// 2.验证银盛回调数据是否为空
		if (null == orderId || "".equals(orderId) || null == state || "".equals(state))
		{
			log.error("海航回调数据异常[termTransID=" + orderId + ",result=" + state + "]");
			return "error";
		}

		// 3.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (order == null)
		{
			log.error("海航回调orderId = " + orderId + "查询订单为空");
			return "error";
		}

		// 4.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("海航回调平台订单号[" + orderId + "]重复回调不处理");
			return "error";
		}

		// 5.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		map.put("resultCode", state);

		if (state.equals("0"))
		{
			// 充值成功
			map.put("status", 1);
		}
		else if (state.equals("1"))
		{
			// 充值失败
			map.put("status", 0);
		}

		if (map.containsKey("status"))
		{
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		// 6. 返回接收结果给远特 0 成功 1失败
		return "OK";
	}

	/**
	 * @功能描述：	话机世界回调
	 *
	 * @作者：zhangpj		@创建时间：2017年9月14日
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackHuaJiShiJie(HttpServletRequest request, HttpServletResponse response)
	{
		// 1.接收话机世界回调数据
		String jsonStr = getRequestContext(request);
		log.error("话机世界回调开始：回调信息[" + jsonStr + "]");

		// 2.验证话机世界回调数据是否为空
		if (null == jsonStr || "".equals(jsonStr))
		{
			log.error("话机世界回调数据为空");
			// 返回0代表接收成功
			return "{\"status\":\"回调数据为空\"}";
		}

		// 3.解析话机世界回调数据
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		String status = jsonObject.getString("status");// 充值状态 返回0代表成功,返回1代表失败
		String desc = jsonObject.getString("message");	// 充值信息 充值回调信息,失败原因等
		String orderId = jsonObject.getString("serial"); // 流水号,下游客户订单
		String timestamp = jsonObject.getString("timestamp"); // 回调时间 格式:yyyymmddhhmiss
		String sign = jsonObject.getString("sign");	// 签名 MD5(status+message+serial+timestamp+密码)32位大写

		String signSrc = status + desc + orderId + timestamp;
		try {
			String signLocal = ToolMD5.encodeMD5Hex(signSrc.toUpperCase());
			if (signLocal.equals(sign)) {
				log.error("话机世界回调:解密信息不匹配");
				return "{\"status\":\"解密信息不匹配\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 4.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderId);
		if (order == null)
		{
			log.error("话机世界回调orderId = " + orderId + "查询订单为空");
			return "{\"status\":\"serial为空\"}";
		}

		// 5.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("话机世界回调平台订单号[" + orderId + "]重复回调不处理");
			return "{\"status\":\"serial重复回调不处理\"}";
		}

		// 6.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", status + ":" + desc);
		map.put("orderId", orderId);
		if ("0".equals(status)) {
			map.put("status", 1);	// -1异常;1成功;0失败;3实时成功;4实时失败
		} else if("1".equals(status)){
			map.put("status", 0);
		} else {
			log.error("话机世界回调,平台订单号[" + orderId + "]返回结果不明确");
			return "{\"status\":\"返回结果不明确\"}";
		}

		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,SerializeUtil.getStrFromObj(map));
		}

		// 8. 返回接收结果给话机世界 0 成功
		return "{\"status\":\"0\"}";
	}

	/**
	 * @功能描述：	回调千米供货平台,非回调我家平台,因此不需要将充值结果加入到队列当中
	 *
	 * @param request
	 * @param response
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月16日
	 */
	public String statusBackForQianmigonghuo(HttpServletRequest request, HttpServletResponse response)
	{
		// 1.从回调信息中获取 话费订单状态主键、客户订单编号和充值结果
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("orderId");// 获取订单id
		String agentorderid = (String)param.get("customerOrderId");// 获取客户订单号
		String phoneNo = (String)param.get("phoneNo");// 获取手机号
		String spec = (String)param.get("spec");// 金额，以分为单位
		String scope = (String)param.get("scope");// 应用范围
		String status = (String)param.get("status");// 状态,充值结果
		String signature = (String)param.get("signature");// 签名

		// 2.根据订单编号和充值类型获取从千米供货平台抓取到的正在充值的订单信息
		Map<String, Object> orderOtherParam = new HashMap<String, Object>();
		orderOtherParam.put("agentorderid", agentorderid);
		orderOtherParam.put("ordertype", "2");	//1:流量 2:话费
		List<Map<String, Object>> list = orderOtherDao.selectAll(orderOtherParam);
		Map<String, Object> orderOtherMap = list.get(0);

		// 3.获取从千米供货抓取过来的原订单的信息
		String orderinfo = orderOtherMap.get("orderinfo").toString();
		JSONObject ob = JSONObject.parseObject(orderinfo);
		String orderid = ob.getString("order_id");
		String id = ob.getString("id");

		// 4.获取通道配置id(channel_template.xml中channelInterface的id)
		String ordersource = orderOtherMap.get("ordersource").toString();
		String templateId = ordersource.split("_")[1];

		// 5.获取千米供货平台的配置信息
		Map<String, Object> customerMap = qianmigonghuoCustomerDao.selectByPrimaryKey(templateId);
		String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数

		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String setOrdersUrl = paramMap.get("setOrdersUrl").toString();	// 回调地址
		String partner = paramMap.get("partner").toString();	// 合作商代码,定值,供货商提供
		String tplId = paramMap.get("tplId").toString();		// 模板编号,定值,供货商提供
		String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String version = paramMap.get("version").toString();	// 版本号
		String orderstate = "";

		// 6.设置订单状态 4：充值成功 5：冲值失败 6：可疑订单
		if ("success".equals(status)) {
			orderstate = "4";
		}else if ("fail".equals(status)) {
			orderstate = "5";
		}

		// 7.返回充值结果接口
		String result = QianMiGongHuoBillOrderTask.setOrders(setOrdersUrl, partner, tplId, apiKey, orderid, id, orderstate,version);
		String str = "fail";
		if ("true".equals(result) && !result.equals("")) {
			str = "success";
		}

		return str;
	}



	public String statusBackForJuhegonghuo(HttpServletRequest request, HttpServletResponse response) {
		// 1.从回调信息中获取 话费订单状态主键、客户订单编号和充值结果
		Map<String, Object> param = getMaps(request);
		String orderId = (String)param.get("orderId");// 平台订单号
		String agentorderid = (String)param.get("customerOrderId");// 获取客户订单号
		String status = (String)param.get("status");// 状态,充值结果

		// 2.根据订单编号和充值类型获取从千米供货平台抓取到的正在充值的订单信息
		Map<String, Object> orderOtherParam = new HashMap<String, Object>();
		orderOtherParam.put("agentorderid", agentorderid);
		orderOtherParam.put("ordertype", "2");	//1:流量 2:话费
		List<Map<String, Object>> list = orderOtherDao.selectAll(orderOtherParam);
		Map<String, Object> orderOtherMap = list.get(0);

		// 3.获取从千米供货抓取过来的原订单的信息
		String orderinfo = orderOtherMap.get("orderinfo").toString();
		JSONObject ob = JSONObject.parseObject(orderinfo);
		String outOrderNo = ob.getString("tradeId");

		// 4.获取通道配置id(channel_template.xml中channelInterface的id)
		String ordersource = orderOtherMap.get("ordersource").toString();
		String templateId = ordersource.split("_")[1];

		// 5.获取千米供货平台的配置信息
		Map<String, Object> customerMap = juhegonghuoCustomerDao.selectByPrimaryKey(templateId);
		String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数

		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String updateOrderStatusUrl = paramMap.get("updateOrderStatusUrl").toString();	// 回调聚合供货平台地址
		String supplierId = paramMap.get("supplierId").toString();	// 模板编号,定值,供货商提供
		String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String orderState = "";
		String sign = DigestUtils.md5Hex(supplierId + outOrderNo + apiKey);

		// 6.设置订单状态 2：充值成功 3：冲值失败
		if ("success".equals(status)) {
			orderState = "2";
		}else if ("fail".equals(status)) {
			orderState = "3";
		}

		// 7.返回充值结果接口
		String result = JuHeBillOrderCallback.updateOrderStatus(updateOrderStatusUrl, supplierId, outOrderNo, orderState, sign, orderId);
		String str = "fail";   
		System.out.println(orderState);
		if (null != result && !result.equals("")) {
			JSONObject jsonObj = JSONObject.parseObject(result);
			String success = jsonObj.getString("success");
			if ("true".equals(success)) {
				// 如果回调成功,则删除掉这条临时信息
				orderOtherDao.deleteByPrimaryKey(orderOtherMap.get("id").toString());
				str = "success";
			}else{
				log.info("回调聚合供货平台订单状态失败,返回信息["+result+"]");
			}
		}

		return str;
	}

	/**
	 * @功能描述：	中兴回调
	 *
	 * @param request
	 * @param response
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月16日
	 */
	public String statusBackZhongXing(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> maps = getMaps(request);
		String jsonStr = "";
		for (String key : maps.keySet()) {
			jsonStr = key;
		}
		// 1.接收中兴回调数据
		log.error("中兴回调开始：回调信息[" + jsonStr + "]");

		// 2.验证中兴回调数据是否为空
		if (null == jsonStr || "".equals(jsonStr))
		{
			log.error("中兴回调数据为空");
			// 返回0代表接收成功
			return "{\"status\":\"回调数据为空\"}";
		}

		// 3.解析中兴回调数据
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		String orderNo = jsonObject.getString("orderNo");	// 订单号
		String code = jsonObject.getString("code"); 		// 状态码，只有200表示成功，其他表示失败
		String msg = jsonObject.getString("msg"); 			// 描述信息
		Double balance = jsonObject.getDouble("balance"); 	// 描述信息

		// 4.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderNo);
		if (order == null)
		{
			log.error("中兴回调orderNo = " + orderNo + "查询订单为空");
			return "{\"status\":\"serial为空\"}";
		}

		// 5.验证订单是否重复回调
		if (!"1".equals(order.get("status")))
		{
			log.error("中兴回调平台订单号[" + orderNo + "]重复回调不处理");
			return "{\"status\":\"orderNo重复回调不处理\"}";
		}

		// 6.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + msg + "充值后余额" + balance/100);
		map.put("orderId", orderNo);
		if ("200".equals(code)) {
			map.put("status", 1);	// 状态码，只有200表示成功，其他表示失败
		} else {
			map.put("status", 0);
		}

		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey,SerializeUtil.getStrFromObj(map));
		}

		// 8. 返回接收结果给中兴 success 成功
		return "success";
	}

	/**
	 * @功能描述：	麦远回调
	 *
	 * @param request
	 * @param response
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年5月22日
	 */
	public String statusBackMaiYuan(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);

		// 1.接收麦远回调数据
		if (maps.isEmpty()) {
			log.error("麦远回调数据为空");
			// 返回0代表接收成功
			return "{\"status\":\"回调数据为空\"}";
		}

		// 2.验证麦远回调数据是否为空
		log.error("麦远回调开始：回调信息[" + maps.toString() + "]");

		// 3.解析麦远回调数据
		String orderNo = String.valueOf(maps.get("outTradeNo"));// 订单号
		String code = String.valueOf(maps.get("Status"));		// 状态码 4.状态成功, 5.状态失败
		String msg = String.valueOf(maps.get("ReportCode"));	// 描述信息

		// 4.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(orderNo);
		if (order == null) {
			log.error("麦远回调orderNo = " + orderNo + "查询订单为空");
			return "{\"status\":\"outTradeNo为空\"}";
		}

		// 5.验证订单是否重复回调
		if (!"1".equals(order.get("status"))) {
			log.error("麦远回调平台订单号[" + orderNo + "]重复回调不处理");
			return "{\"status\":\"outTradeNo重复回调不处理\"}";
		}

		// 6.组合信息发送到消息队列
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + msg);
		map.put("orderId", orderNo);
		if ("4".equals(code)) {
			map.put("status", 1);	// 状态码,4表示成功
		} else if("5".equals(code)){
			map.put("status", 0);	// 状态码,5表示失败
		}

		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}

		// 8. 返回接收结果给麦远 ok 成功
		return "ok";
	}

	/**
	 * 优启回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackYouQi(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		// 1.接收优启回调数据
		if (maps.isEmpty()) {
			log.error("优启回调数据为空");
			// 返回0代表接收成功
			return "{\"error\":\"回调数据为空\"}";
		}
		log.error("优启回调开始：回调信息[" + maps.toString() + "]");

		// 3.解析优启回调数据
		Map<String, Object> count = JSONObject.toJavaObject(JSONObject.parseObject(maps.get("content").toString()), Map.class);
		String orderNo = String.valueOf(count.get("order_id"));// 订单号
		String code = String.valueOf(count.get("status"));		// 状态码 4.状态成功, 5.状态失败
		String msg = String.valueOf(count.get("desc"));	// 描述信息
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + msg);
		map.put("orderId", orderNo);
		map.put("providerOrderId", count.get("oid"));
		if ("0".equals(code)) {
			map.put("status", 1);	// 状态码,4表示成功
		} else if("2".equals(code)){
			map.put("status", 0);	// 状态码,5表示失败
		}

		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "success";
	}

	/**
	 * 全国话费空充
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackKongChong(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		// 1.接收全国空充回调数据
		if (maps.isEmpty()) {
			log.error("全国空充回调数据为空");
			// 返回0代表接收成功
			return "failed";
		}
		log.error("全国空充回调开始：回调信息[" + maps.toString() + "]");

		// 3.解析全国空充回调数据
		String orderNo = String.valueOf(maps.get("downstreamSerialno"));// 订单号
		String code = String.valueOf(maps.get("status"));		// 状态码 4.状态成功, 5.状态失败
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + maps);
		map.put("orderId", orderNo);
		map.put("providerOrderId", maps.get("ejId"));
		if(maps.containsKey("voucher")){
			map.put("voucher", maps.get("voucher").toString().replaceAll("[^(0-9)]", ""));
		}
		if ("2".equals(code)) {
			map.put("status", 1);	// 状态码,2表示成功
		} else {
			map.put("status", 0);	// 状态码,其余表示失败
		}

		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "success";
	}

	/**
	 * 云流全国
	 * @param request
	 * @param response
	 * @return
	 */
	public String statusBackYunliu(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		// 1.接收云流全国回调数据
		if (maps.isEmpty()) {
			log.error("云流全国回调数据为空");
			// 返回0代表接收成功
			return "failed";
		}
		log.error("云流全国回调开始：回调信息[" + MapUtils.toString(maps) + "]");

		// 3.解析云流全国回调数据
		String orderNo = String.valueOf(maps.get("OutTradeNo"));// 订单号
		String code = String.valueOf(maps.get("Status"));// 状态码 4.状态成功, 5.状态失败
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + maps);
		map.put("orderId", orderNo);
		map.put("providerOrderId", maps.get("TaskID")+"");
		if(maps.containsKey("ReportCode")){
			String voucher =  maps.get("ReportCode") + "";
			Pattern pattern = Pattern.compile("0034[0-9]+");
			Matcher matcher = pattern.matcher(voucher);
			if(matcher.find()) {
				map.put("voucher", matcher.group(0));
			}
		}
		if ("4".equals(code)) {
			map.put("status", 1);	// 状态码,4表示成功
		} else {
			map.put("status", 0);	// 状态码,其余表示失败
		}
		log.error("云流放入队列中："+MapUtils.toString(map));
		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			log.error("云流放入队列完成");
		}
		return "ok";
	}

	/**
	 * 星博海充值回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String xingBoHaiBack(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		String success = "OK";

		// 1.接收星博海回调数据
		if (maps.isEmpty()) {
			log.error("星博海回调数据为空");
			// 返回0代表接收成功
			return "failed";
		}
		log.error("星博海回调开始：回调信息[" + MapUtils.toString(maps) + "]");

		// 3.解析星博海回调数据
		String orderId = String.valueOf(maps.get("Orderid"));// 订单号
		String code = String.valueOf(maps.get("Orderstatu_int"));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", code + ":" + maps.get("Orderstatu_text"));
		map.put("orderId", orderId);
		map.put("providerOrderId", maps.get("Chargeid")+"");

		if(map.containsKey("orderStatuText")){
			String orderStatuText = map.get("orderStatuText") + "";
			String regEx="[^0-9]";  
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(orderStatuText);
			map.put("voucher", m.replaceAll("").trim());
		}

		//		0：等待处理 1：暂停处理
		//		2：正在处理 6：正在缴费
		//		11：处理成功 16：缴费成功
		//		20：取消处理 21：处理失败
		//		26：缴费失败 99：冻结
		switch (code) {
		case "16":
			map.put("status", 1);
			break;
		case "1":
			map.put("status", 0);
			break;
		case "20":
			map.put("status", 0);
			break;
		case "21":
			map.put("status", 0);
			break;
		case "26":
			map.put("status", 0);
			break;
		case "99":
			map.put("status", 0);
			break;
		default:
			success = "error";
			break;
		}
		log.error("星博海订单放入队列中："+MapUtils.toString(map));
		// 7.将回调结果放置到队列中
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return success;
	}

	/**
	 * 鹏博士回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String pengBoShiBack(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);

		// 1.接收鹏博士回调数据
		if (maps.isEmpty()) {
			log.error("鹏博士回调数据为空");
			// 返回0代表接收成功
			return "failed";
		}
		log.error("鹏博士回调开始：回调信息[" + MapUtils.toString(maps) + "]");

		String status = maps.get("status") + "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", status + ":" + maps.get("failure_reason")+"");
		map.put("orderId",  maps.get("order_id"));
		map.put("providerOrderId", maps.get("drpeng_order_id")+"");
		if(status.equals("3002")) {
			map.put("status", 1);
		}else {
			map.put("status", 0);
		}
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "SUCCESS";
	}

	/**
	 * 飞游全国移动回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String FeiYouBack(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		// 1.接收飞游全国移动回调数据
		if (maps.isEmpty()) {
			log.error("飞游全国移动回调数据为空");
			// 返回0代表接收成功
			return "1";
		}
		log.error("飞游全国移动回调开始：回调信息[" + MapUtils.toString(maps) + "]");

		String status = maps.get("status") + "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resultCode", status +"流水号"+ maps.get("srcOrderid"));
		map.put("orderId",  maps.get("outOrderID"));
		map.put("providerOrderId", maps.get("orderID"));
		map.put("voucher", maps.get("srcOrderid"));
		if(status.equals("2")) {
			map.put("status", 1);
		}else {
			map.put("status", 0);
		}
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "0";
	}


	/**
	 * 海口雨水文化回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String HaiKouYuShuiBack(HttpServletRequest request, HttpServletResponse response) {
		// 1.接收海口雨水文化回调信息
		String jsonStr = getRequestContext(request);
		log.error("海口雨水文化回调开始：回调信息[" + jsonStr + "]");
		// 2.验证海口雨水文化回调数据是否为空
		if (null == jsonStr || jsonStr.equals("")) {
			log.error("海口雨水文化回调数据为空");
			return "FAIL";
		}
		// 3.解析海口雨水文化回调数据
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		String orderId = jsonObject.getString("orderId"); //上家订单号
		String partnerStreamNumber = jsonObject.getString("partnerStreamNumber"); // 商户流水号
		String rechargeStatus = jsonObject.getString("rechargeStatus"); // 交易状态
		String voucher = jsonObject.getString("voucher"); // 凭证
		// 4.根据回调数据查询订单,验证订单是否存在
		Map<String, Object> order = orderDao.selectByOrderId(partnerStreamNumber);
		if (order == null) {
			log.error("海口雨水文化回调orderId = " + orderId + "查询订单为空");
			return "FAIL";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", partnerStreamNumber);
		map.put("resultCode", rechargeStatus);
		map.put("voucher", voucher);
		map.put("providerOrderId", orderId);
		if (rechargeStatus.equals("SUCCESS")) {
			map.put("status", "1");
		} else {
			map.put("status", "0");
		}
		if (map.containsKey("status")) {
			mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
		}
		return "SUCCESS";
	}
	/**
	 * 云普手支回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String YunPuBack(HttpServletRequest request,HttpServletResponse response) {
		BufferedReader reader;
		StringBuilder stringBuilder;
		String inputStr = null;
		String flag = "0";
		try{
			//获取request中的json
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			stringBuilder = new StringBuilder();
			while ((inputStr = reader.readLine()) != null) {
				stringBuilder.append(inputStr);
			}
			//	1.接收云普回调信息
			log.error("云普回调开始：回调信息[" + stringBuilder.toString() +"]");
			// 	2.验证云普回调数据是否为空
			if (null == stringBuilder.toString() || stringBuilder.toString().equals(""))
			{
				log.error("云普回调数据为空");
				//返回0代表成功，其他均为失败
				return "1";
			}
			//	3.解析云普回调数据
			try {
				//判断获取的是否为JSonArray类型，jsonArray:有新订单     jsonObject类型：无新订单，不需要操作
				JSONArray jsonArray = JSON.parseArray(stringBuilder.toString());
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);  
					String orderId = jsonObject.getString("id");							//上家订单号
					String providerOrderId = jsonObject.getString("outOrderId");			//外部订单号
					String resultCode = jsonObject.getString("reportDetail");				//执行结果说明
					String reportStatus = jsonObject.getString("reportStatus");				//状态码
					//  4.根据回调数据查询订单,验证订单是否存在
					Map<String, Object> order = orderDao.selectByOrderId(providerOrderId);
					if (order == null)
					{
						log.error("云普回调orderId = " + orderId + "查询订单为空");
						return "1";
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("orderId",providerOrderId);										
					map.put("resultCode",resultCode);		
					map.put("providerOrderId",orderId);		

					if(reportStatus.equals("1")) {								
						map.put("status","1");
					}else {
						map.put("status","0");
						flag = "1";
					}
					if (map.containsKey("status")){
						mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				log.error("云普回调接收数据类型错误"+stringBuilder.toString());
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}


	/**
	 * 千米回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String QianMiTwoback(HttpServletRequest request, HttpServletResponse response) {
		try {
			String id = "2000000046";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);	//获取通道参数
			String defaultParameter = channel.get("default_parameter")+"";					//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appSecret = paramMap.get("appSecret");
			Map<String, Object> map = new HashMap<String, Object>();
			// 从request获取参数
			Enumeration keys = request.getParameterNames();
			/**
			 * 用于存放request请求中的参数
			 */
			Map<String, String> reqMap = new HashMap<String, String>();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String value = request.getParameter(key);
				reqMap.put(key, value);
			}
			//取出返回的签名
			String sign = reqMap.get("sign")+"";
			//进行验签，需要删除签名
			reqMap.remove("sign");
			String orderId = reqMap.get("outer_tid")+"";
			String resultCode = reqMap.get("recharge_state")+"";
			String providerOrderId = reqMap.get("tid")+"";
			if (resultCode.equals("") || resultCode.equals("null")){
				log.error("千米回调 = " + reqMap + "查询订单为空");
				return "fail";
			}
			log.error("千米回调开始：回调信息[" + reqMap +"]");
			map.put("orderId",orderId);
			map.put("providerOrderId",providerOrderId);
			map.put("resultCode",resultCode);
			//将返回的参数进行sha1加密转16进制后与返回的sign进行对比，确保参数一致
			if(SHA1.sign(reqMap,appSecret).equals(sign)) {
				// 1充值成功   9失败
				if(resultCode.equals("1")) {
					map.put("status", "1");
				}else {
					map.put("status", "0");
				}
			}else {
				log.error("千米验证签名失败"+reqMap);
				return "fail";
			}
			if (map.containsKey("status")) {
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	/**
	 * 连连科技回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String lianLianKeJiBack(HttpServletRequest request, HttpServletResponse response) {
		BufferedReader reader;
		StringBuilder stringBuilder;
		String inputStr = null;
		String flag = "1";			//回调返回上家 1 充值成功，0充值失败
		try {
			//获取request中的json
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			stringBuilder = new StringBuilder();
			while ((inputStr = reader.readLine()) != null) {
				stringBuilder.append(inputStr);
			}
			//1.验证连连科技回调数据是否为空
			if (null == stringBuilder.toString() || stringBuilder.toString().equals(""))
			{
				log.error("连连科技回调数据为空");
				return "0";
			}
			//2.接收连连科技回调信息
			log.error("连连科技回调开始：回调信息[" + stringBuilder.toString() +"]");
			Map<String, Object> map = new HashMap<String, Object>();
			JSONObject json = JSON.parseObject(stringBuilder.toString());
			String data = json.getString("data");
			JSONObject jsonObject = JSONObject.parseObject(data);
			String providerOrderId = jsonObject.getString("order_no");		//上家订单号
			String resultCode = json.getString("error")+"";					//error为0充值成功
			String orderId = jsonObject.getString("trade_no");				//平台生成的订单号
			map.put("orderId",orderId);
			map.put("providerOrderId",providerOrderId);
			map.put("resultCode",resultCode);
			if(resultCode != null) {
				log.error("LianLianKeJiBack--map: "+map+" ---- jsonObject : "+jsonObject);
				// 0充值成功   其余都失败
				if(resultCode.equals("0")) {
					//连连科技家没有查询接口
					map.put("status", "1");						
				}else {
					map.put("status", "0");
					flag = "0";
				}
				if (map.containsKey("status")) {
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}else {
				return "0";
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
		return flag;
	}
	
	/**
	 * 满帆回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String ManFanBack(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		//验证满帆回调数据是否为空
		if (maps.isEmpty()) {
			log.error("满帆回调数据为空");
			// 返回0代表接收成功
			return "error";
		}
		try {
			log.error("满帆回调开始：回调信息[" + maps.toString() + "]");
			Map<String, Object> map = new HashMap<String, Object>();
			String providerOrderId = maps.get("ejId")+"";						//上家订单号
			String resultCode = maps.get("status")+"";							//error为2:成功  3:失败
			String orderId = maps.get("downstreamSerialno")+"";					//平台生成的订单号
			String voucher = maps.get("voucher")+"";							//凭证流水
			String voucherType = maps.get("voucherType")+"";					//凭证类型
			map.put("orderId",orderId);
			map.put("providerOrderId",providerOrderId);
			if(resultCode != null) {
				if(resultCode.equals("2")) {
					map.put("status", "1");		
					map.put("resultCode",resultCode+" : 充值成功-"+"凭证类型 : "+voucherType+"-凭证流水 : "+voucher);
				}else{
					map.put("status", "0");
					map.put("resultCode",resultCode+" : 充值失败");
				}
				if (map.containsKey("status")) {
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}else {
				return "error";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "success";
	}
	
	/**
	 * 玖玥回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String JiuYueBack(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> maps = getMaps(request);
		//验证玖玥回调数据是否为空
		if (maps.isEmpty()) {
			log.error("玖玥回调数据为空");
			// 返回0代表接收成功
			return "error";
		}
		try {
			log.error("玖玥回调开始：回调信息[" + maps.toString() + "]");
			Map<String, Object> map = new HashMap<String, Object>();
			String providerOrderId = maps.get("ejId")+"";						//上家订单号
			String resultCode = maps.get("status")+"";							//error为2:成功  3:失败
			String orderId = maps.get("downstreamSerialno")+"";					//平台生成的订单号
			String voucher = maps.get("voucher")+"";							//凭证流水
			String voucherType = maps.get("voucherType")+"";					//凭证类型
			map.put("orderId",orderId);
			map.put("providerOrderId",providerOrderId);
			if(resultCode != null) {
				if(resultCode.equals("2")) {
					map.put("status", "1");		
					map.put("resultCode",resultCode+" : 充值成功-"+"凭证类型 : "+voucherType+"-凭证流水 : "+voucher);
				}else{
					map.put("status", "0");
					map.put("resultCode",resultCode+" : 充值失败");
				}
				if (map.containsKey("status")) {
					mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
				}
			}else {
				return "error";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "success";
	}
	
	/**
	 * 云米优回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String YunMiYouBack(HttpServletRequest request, HttpServletResponse response) {

		try {
			String id = "2000000063";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);	//获取通道参数
			String default_parameter = channel.get("default_parameter")+"";
			Map<String,String> paramMap = XmlUtils.readXmlToMap(default_parameter);
			String appSecret = paramMap.get("appSecret");
			Map<String, Object> map = new HashMap<String, Object>();
			//从rquest中获取参数
			Enumeration keys = request.getParameterNames();
			/**
			 * 用于存放request请求中的参数
			 */
			Map<String, String> reqMap = new HashMap<String, String>();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				String value = request.getParameter(key);
				reqMap.put(key, value);
			}
			//取出返回的标签
			String sign = reqMap.get("sign")+"";
			//进行验签，需要删除签名
			reqMap.remove("sign");
			String orderId = reqMap.get("outer_tid")+"";
			String resultCode = reqMap.get("recharge_state")+"";
			String providerOrderId = reqMap.get("tid")+"";
			if (resultCode.equals("") || resultCode.equals("null")){
				log.error("云米优回调 = " + reqMap + "查询订单为空");
				return "fail";
			}
			log.error("云米优回调开始：回调信息[" + reqMap +"]");
			map.put("orderId",orderId);
			map.put("providerOrderId",providerOrderId);
			map.put("resultCode",resultCode);
			//将返回参数进行shal加密转16进制后与返回的sign进行对比,确保参数一致
			if (SHA1.sign(reqMap,appSecret).equals(sign)){
				//1充值成功  0失败
				if (resultCode.equals("1")){
					map.put("status","1");
				}else {
					map.put("status","0");
				}
			}else {
				log.error("云米优验证签名失败"+reqMap);
				return "fail";
			}
			if (map.containsKey("status")){
				mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
	/**
	 * 三网回调
	 * @param request
	 * @param response
	 * @return
	 */
	public String SanWang(HttpServletRequest request, HttpServletResponse response){
		log.info("三网回调开始---------------------");
		BufferedReader reader;
		StringBuilder stringBuilder;
		String inputStr = null;
		String flag = "0";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			//获取request中的json
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			stringBuilder = new StringBuilder();
			while ((inputStr = reader.readLine()) != null) {
				stringBuilder.append(inputStr);
			}
			try {
				JSONObject data = JSONObject.parseObject(stringBuilder.toString());
				log.info("三网回调接受的数据为--"+data);
				if (data == null) {
					log.error("三网回调数据为空");
					return "回调数据为空";
				}
				String resultCode = data.get("status") + "";
				String operatorSerialNumber = data.get("operatorSerialNumber") + "";
				String serialNumber = data.get("serialNumber")+"";
				String orderId = data.get("orderId")+"";
				map.put("orderId",serialNumber);
				map.put("providerOrderId",orderId);
				if (resultCode != null) {
					if (resultCode.equals("2")) {
						map.put("status", "1");
						map.put("resultCode", resultCode + " : 充值成功-" + "-凭证流水 : "+operatorSerialNumber);
					} else {
						map.put("status", "0");
						map.put("resultCode", resultCode + " : 充值失败");
					}
					log.info("三网回调的map为"+map);
					if (map.containsKey("status")) {
						mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
					}
				} else {
					return "error";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return flag;
		}catch (Exception e){
			log.info("三网回调出现异常");
			e.printStackTrace();
		}
		log.info("三网回调成功");
		return "success";
	}

}
