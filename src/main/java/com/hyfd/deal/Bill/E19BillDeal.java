package com.hyfd.deal.Bill;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProductDao;
import com.hyfd.deal.BaseDeal;

public class E19BillDeal implements BaseDeal {

	Logger log = Logger.getLogger(getClass());

	public static Map<String, String> rltMap = new HashMap<String, String>();
	
	static {
		rltMap.put("0000", "下单成功");
		rltMap.put("0001", "支付系统异常请将订单号及时间发送给技术进行处理");
		rltMap.put("0002", "异常请将订单号及时间发送给技术进行处理");
		rltMap.put("0999", "未开通直冲功能");
		rltMap.put("1000", "下单失败请稍后重试");
		rltMap.put("1001", "传入参数不完整");
		rltMap.put("1002", "验证摘要串失败");
		rltMap.put("1005", "没有对应充值产品");
		rltMap.put("1006", "系统异常，请稍后重试");
		rltMap.put("1007", "账户余额不足");
		rltMap.put("1008", "此产品超出当天限额");
		rltMap.put("1010", "产品与手机号不匹配");
		rltMap.put("1011", "定单号不允许重复");
		rltMap.put("1013", "暂不可充值");
		rltMap.put("1015", "无法查到对应号段");
		rltMap.put("1017", "电信手机10秒内不能重复充值");
		rltMap.put("1020", "号码不支持流量充值卡");
		rltMap.put("1022", "充值号码格式错误");
		rltMap.put("1028", "下单接口请求次数超限");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = (String) order.get("phone");// 手机号
//			int fee = new Double(order.get("fee") + "").intValue();
			String orderId = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getStringRandom(2);//19e指定要求30位
			map.put("orderId", orderId);
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String agentId = paramMap.get("agentId");
			String merchantKey = paramMap.get("merchantKey");
			String source = paramMap.get("source");
			String returnType = paramMap.get("returnType");
			String directFillUrl = paramMap.get("directFillUrl");// 充值地址
			String accegmentUrl = paramMap.get("accegmentUrl");// 号段查询地址
			String directSearchUrl = paramMap.get("directSearchUrl");// 订单查询地址
			// 号段验证
			String verifystring = getAccegmentVerify(agentId, source, phone, merchantKey);
			accegmentUrl += "?agentid=" + agentId;
			accegmentUrl += "&source=" + source;
			accegmentUrl += "&mobilenum=" + phone;
			accegmentUrl += "&verifystring=" + verifystring;
			log.error("19eaccegmentReq|" + accegmentUrl);
			String prodId = order.get("providerProductId") + "";// 产品ID
			String backurl = "";
			String mobilenum = phone;
			String mark = "";
			// 准备充值请求参数
			verifystring = getDirectFillVerify(prodId, agentId, backurl, returnType, orderId, mobilenum, source, mark, merchantKey);
			directFillUrl += "?prodid=" + prodId;
			directFillUrl += "&agentid=" + agentId;
			directFillUrl += "&backurl=" + backurl;
			directFillUrl += "&returntype=" + returnType;
			directFillUrl += "&orderid=" + orderId;
			directFillUrl += "&mobilenum=" + mobilenum;
			directFillUrl += "&source=" + source;
			directFillUrl += "&mark=" + mark;
			directFillUrl += "&verifystring=" + verifystring;
			// 发送充值请求
			String directFillData = ToolHttp.get(false, directFillUrl);
			if (directFillData != null && !directFillData.equals("")) {
				Map<String, String> resultMap = getDirectSearchReturnData(directFillData);// 解析XML
				String resultNo = resultMap.get("resultno");
				String upids = resultMap.get("tranid");
				map.put("providerOrderId", upids);
				map.put("resultCode", resultNo + ":" + rltMap.get(resultNo));
				if (resultNo.equals("0000")) {// 提交成功
					flag = 1;
				}else if (resultNo.equals("1006") || resultNo.equals("1011")) {// 扣款状态未知
					//去查询
					verifystring = getDirectSearchVerify(agentId, backurl, returnType, orderId, source,
							merchantKey);
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
					if(!"4".equals(directSearchMap.get("resultno"))){
						flag = 1;
						map.put("resultCode", resultNo + ":" + rltMap.get(resultNo) + "|" +directSearchMap.get("resultno") +"查询获取到提交成功状态");
					}else{
						flag = 4;
						map.put("resultCode", resultNo + ":" + rltMap.get(resultNo) + "|" +directSearchMap.get("resultno") +"查询获取到提交失败状态");
					}
				} else {
					flag = 0;
				}
			}
		} catch (Exception e) {
			log.error("19易充值出错" + e.getMessage() + "||" + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	public static String getDirectFillVerify(String prodid, String agentid, String backurl, String returntype,
			String orderid, String mobilenum, String source, String mark, String merchantKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("prodid=" + prodid);
		sb.append("&agentid=" + agentid);
		sb.append("&backurl=" + backurl);
		sb.append("&returntype=" + returntype);
		sb.append("&orderid=" + orderid);
		sb.append("&mobilenum=" + mobilenum);
		sb.append("&source=" + source);
		sb.append("&mark=" + mark);
		sb.append("&merchantKey=" + merchantKey);
		return getKeyedDigest(sb.toString(), "");
	}

	public static Map<String, String> getAccegmentReturnData(String xml) throws Exception {
		Map<String, String> rm = new HashMap<String, String>();
		Document doc = DocumentHelper.parseText(xml);
		Element rootElt = doc.getRootElement();
		Iterator<?> it = rootElt.elementIterator();
		while (it.hasNext()) {
			Element recordEle = (Element) it.next();
			List<?> elList = recordEle.elements();
			for (int i = 0; i < elList.size(); i++) {
				Element el = (Element) elList.get(i);
				rm.put(el.attributeValue("name"), URLDecoder.decode(el.attributeValue("value"), "utf-8"));
			}
		}
		return rm;
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

	public static String getDirectProductVerify(String agentid, String source, String merchantKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("agentid=" + agentid);
		sb.append("&source=" + source);
		sb.append("&merchantKey=" + merchantKey);
		return getKeyedDigest(sb.toString(), "");
	}

	public static String getAccegmentVerify(String agentid, String source, String mobilenum, String merchantKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("agentid=" + agentid);
		sb.append("&source=" + source);
		sb.append("&mobilenum=" + mobilenum);
		sb.append("&merchantKey=" + merchantKey);
		return getKeyedDigest(sb.toString(), "");
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
	
	public static void main(String[] args){
		Map<String,Object> order = new HashMap<String,Object>();
		order.put("phone", "18764166237");
		Map<String,Object> channel = new HashMap<String,Object>();
		channel.put("default_parameter", "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
											"<channelInterface id=\"100003\" name=\"100003-19e\" switch=\"1\">"+
											        "<agentId name=\"代理商账号\">jinanjiatuo</agentId>"+
											        "<merchantKey name=\"密钥\">xn2p99tp0ahb7tk6ahxer0wtjowmj5rhtgdhtmrajevobwrdh5fbv89288j0y9u4chrpj6k4jey9y4op1vzyz66kfed1uzzq651gegqlvkcyoud2q4k2yv6xxz28jedr</merchantKey>"+
											        "<source name=\"代理商来源\">esales</source>"+
											        "<returnType name=\"返回类型\">2</returnType>"+
											        "<nowUpdate name=\"立即更新1为立即更新\">1</nowUpdate>"+
											        "<directProductUrl name=\"产品列表\">http://hfjk.19ego.com/esales2/prodquery/directProduct.do</directProductUrl>"+
											        "<directFillUrl name=\"充值\">http://hfjk.19ego.com/esales2/directfill/directFill.do</directFillUrl>"+
											        "<accegmentUrl name=\"号段\">http://hfjk.19ego.com/esales2/accegment/accsegment.do</accegmentUrl>"+
											        "<directSearchUrl name=\"订单查询\">http://hfjk.19ego.com/esales2/orderquery/directSearch.do</directSearchUrl>"+
											        "<noticeUrl name=\"回调\">http://120.26.134.145/rcmp/jf/orderDeal/statusBack19E</noticeUrl>"+
											    "</channelInterface>");
		order.put("channel", channel);
		order.put("providerProductId", "50078800");
		new E19BillDeal().deal(order);
	}
	
}
