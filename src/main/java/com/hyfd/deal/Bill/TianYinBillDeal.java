package com.hyfd.deal.Bill;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 天音 话费业务
 *
 */
public class TianYinBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(TianYinBillDeal.class);

	/**
	 * 天音处理
	 * 
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String orderId = order.get("orderId") + "";
			map.put("orderId", orderId);

			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
//			Double spec = Double.parseDouble(fee);// 充值金额，以元为单位
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String checkUrl = paramMap.get("checkUrl").toString();
			String platyformId = paramMap.get("platyformId").toString();// TODO
																		// 平台编号
			String requestid = ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + getSixSquece() + platyformId;
			map.put("orderId", requestid);
			String requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
			String accnbr = phoneNo;
			String servtype = "4";// 4表示手机
			String checkRet = sendCheck(checkUrl, requestid, requesttime, accnbr, servtype);
			String paysign = platyformId + ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + getFourSquece();

			boolean checkSuccess = false;
			boolean isChaoshi = false;
			if (null != checkRet) {
				Map<String, Object> checkresult = readXmlToMapFromCreateResponse(checkRet);
				log.debug("号码[" + phoneNo + "]进行天音[话费充值]号码校验结果：" + checkresult);
				Map outData = (Map) checkresult.get("OUT_DATA");
				if(outData.isEmpty()){
					map.put("resultCode","号码[" + phoneNo + "],"+checkresult.get("RETURN_MSG"));
				}
				String onlineFlag = outData.get("ONLINEFLAG").toString();

				// 返回1标识在网，只有在返回1的情况下才可以缴费； 返回0的话不能缴费
				if ("1".equals(onlineFlag)) {
					// 查询校验通过
					checkSuccess = true;
				} else {
					log.error("号码[" + phoneNo + "]进行天音[话费充值]号码校验：号码校验失败！");
					map.put("resultCode", "号码[" + phoneNo + "]进行天音[话费充值]号码校验失败");
				}
			} else {
				// 请求超时
				isChaoshi = true;
				map.put("resultCode", "号码[" + phoneNo + "]进行天音[话费充值]超时");
			}

			String resultCode = "";
			String msg = "";
			String upids = "";
			if (true) {
				String chongzhiUrl = channel.get("link_url").toString();// "http://220.181.190.189:51000/esbWS/services/s1300AgentCfm?wsdl";
				requestid = ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + getSixSquece() + platyformId;
				requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
				String orgcode = paramMap.get("orgcode").toString();// "17090171400";
																	// //机构代码
																	// 机构代码要传代理商的手机号码
				String opcode = paramMap.get("opcode").toString();// 定值 1302
				String staffcode = paramMap.get("staffcode").toString();// 工号
				String acctid = "";
//				String paymoney = spec.intValue() + "";
				String paymoney = fee;
				String paytype = "G";// 测试样例中该值是G，不是接口中描述的值
										// 文档中的描述：必填;现金0；支票9；托收缴费4；专款分月返还A等等
				String agtdate = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
				String orgacctid = paramMap.get("orgacctid").toString();// 110005016025
																		// 必填测试环境为110005016025
				String key128 = paramMap.get("key128").toString();// "KlaYBqOvCnnSIcPXQJJhlulYyPkpQWAccDIWhfIVTwCQvgSAmZcfmYIKdWjEqmRjgzpzBVmjqJZTrCOOsFGgWUoSdkjqQNyPOxOGpZzXgLKJHnxmmNWQaKlYTRsasFhJ";
				String ret = sendCreate(chongzhiUrl, requestid, requesttime, staffcode, orgcode, opcode, acctid, accnbr,
						paymoney, paytype, paysign, agtdate, servtype, orgacctid, key128);

				log.info("号码[" + phoneNo + "]进行天音[话费充值]提交返回信息：" + ret);
				if (ret == null) {
					// 请求超时
					isChaoshi = true;
					map.put("resultCode", "号码[" + phoneNo + "]进行天音[话费充值]超时");
				} else {
					Map<String, Object> result = readXmlToMapFromCreateResponse(ret);
					// System.out.println("号码["+phoneNo+"]进行天音[话费充值]结果：" +
					// result);

					msg = result.get("RETURN_MSG").toString();
					resultCode = result.get("RETURN_CODE").toString();

					map.put("providerOrderId", paysign);
				}
			}
			// 号码验证成功并且请求没有超时
			if (true && isChaoshi == false) {
				if (resultCode.equals("000000") == false) {// 响应 resultCode ==
															// 000000 成功
					log.error("号码[" + phoneNo + "]进行天音[话费充值]请求：提交充值失败！");

					String submitbackmsg = resultCode + ":" + msg;
					submitbackmsg = StringUtil.getNewColumnValueForSql(submitbackmsg);
					map.put("resultCode", submitbackmsg);
					flag = 4;
				} else {
					map.put("resultCode", resultCode + ":" + msg);
					flag = 3;
					log.debug("号码[" + phoneNo + "]进行天音[话费充值]请求：提交充值成功！");
				}
			}
		} catch (Exception e) {
			log.error("天音[话费充值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * requestid 请求流水 必填 yyyymmddhh24miss+6位循环数字+平台编号
	 * 
	 * 
	 * 例如： 2015050100000010000102 requesttime 请求时间 必填 yyyymmddhh24miss
	 * requestservice 服务名称 必填 staffcode 工号 必填 orgcode 机构代码 必填 opcode 操作代码 必填
	 * 1302 acctid 账户标识 可空 accnbr 服务号码 必填 paymoney 缴费金额 必填 ：单位是元 paytype 缴费类型
	 * 必填;现金0；支票9；托收缴费4；专款分月返还a等等 paysign 缴费流水号 必填 ： 平台编号 +
	 * yyyymmddhh24miss+4位循环数字： 例如：ab100120150501000000 agtdate 缴费日期 必填 servtype
	 * 号码类型 必填，4：手机 5：宽带 2：固话，99其他；此处可填4或者99 orgacctid 机构账户标示 必填
	 * 
	 * 
	 */
	public static String sendCreate(String url, String requestid, String requesttime, String staffcode, String orgcode,
			String opcode, String acctid, String accnbr, String paymoney, String paytype, String paysign,
			String agtdate, String servtype, String orgacctid, String key128) {

		String signSrc = paysign + accnbr + paymoney + key128;
		try {
			signSrc = URLEncoder.encode(signSrc, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String checkString = SHA1(signSrc);

		String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sitech.com\">"
				+ "<soapenv:Header/><soapenv:Body><ws:callService><xmlbody><![CDATA["
				+ "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<ROOT>"
				+ "<REQUESTSERVICE type=\"string\">s1300AgentCfm</REQUESTSERVICE>" + "<REQUESTID type=\"string\">"
				+ requestid + "</REQUESTID>" + "<REQUESTTIME type=\"string\">" + requesttime + "</REQUESTTIME>"
				+ "<STAFFCODE type=\"string\">" + staffcode + "</STAFFCODE>" + "<ORGCODE type=\"string\">" + orgcode
				+ "</ORGCODE>" + "<OPCODE type=\"string\">" + opcode + "</OPCODE>" + "<ACCTID type=\"string\"/>"
				+ "<ACCNBR type=\"string\">" + accnbr + "</ACCNBR>" + "<PAYMONEY type=\"string\">" + paymoney
				+ "</PAYMONEY>" + "<PAYTYPE type=\"string\">" + paytype + "</PAYTYPE>"
				+ "<DELAYRATE type=\"string\">0</DELAYRATE>" // 滞纳金收取比率
				+ "<REMONTHRATE type=\"string\">0</REMONTHRATE>" // 月租补收
				+ "<BANKCODE type=\"string\"/>" + "<CHECKNO type=\"string\"/>" + "<PAYNOTE type=\"string\">B</PAYNOTE>"
				+ "<PAYSIGN type=\"string\">" + paysign + "</PAYSIGN>" + "<AGTDATE type=\"string\">" + agtdate
				+ "</AGTDATE>" + "<SERVTYPE type=\"string\">" + servtype + "</SERVTYPE>" + "<ORGACCTID type=\"string\">"
				+ orgacctid + "</ORGACCTID>" + "<CHECKSTRING type=\"string\">" + checkString + "</CHECKSTRING>"
				+ "</ROOT>" + "]]></xmlbody><ws:pin>?</ws:pin></ws:callService></soapenv:Body></soapenv:Envelope>";
		String ret = ToolHttp.post(false, url, xml.toString(), "application/text");
		return ret;
	}

	/**
	 * 
	 * requestid 请求流水 必填yyyymmddhh24miss+6位循环数字+平台编号 例如： 2015050100000010000102
	 * requesttime 请求时间 必填yyyymmddhh24miss requestservice 服务名称 必填 orgcode 机构代码
	 * 必填 requesttime 请求时间 必填 orderId 空冲订单号 必填
	 * 
	 * @return
	 */
	public static String sendSearch(String url, String requestid, String requesttime, String orgcode, String orderId) {
		String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sitech.com\">"
				+ "<soapenv:Header/><soapenv:Body><ws:callService><xmlbody><![CDATA["
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<ROOT>"
				+ "<REQUESTSERVICE type=\"string\">s1300AgentQry</REQUESTSERVICE> " + "<REQUESTID type=\"string\">"
				+ requesttime + "</REQUESTID> " + "<REQUESTTIME type=\"string\">" + requesttime + "</REQUESTTIME> "
				+ "<PASSWD type=\"string\"/><ORGCODE type=\"string\">" + orgcode + "</ORGCODE> "
				+ "<PAYSIGN type=\"string\">" + orderId + "</PAYSIGN> " + "</ROOT>"
				+ "]]></xmlbody><ws:pin>?</ws:pin></ws:callService></soapenv:Body></soapenv:Envelope>";
		log.info("发送天音查询报文[" + xml.toString() + "]");
		String ret = ToolHttp.post(false, url, xml.toString(), "application/text");
		return ret;
	}

	/**
	 * 
	 * requestid 请求流水 必填yyyymmddhh24miss+6位循环数字+平台编号 例如： 2015050100000010000102
	 * requesttime 请求时间 必填yyyymmddhh24miss requestservice 服务名称 必填 accnbr 手机号码 必填
	 * servtype 号码类型 必填 4：手机 5：宽带 2：固话；此处可固定为4
	 * 
	 */
	public static String sendCheck(String url, String requestid, String requesttime, String accnbr, String servtype) {
		String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sitech.com\">"
				+ "<soapenv:Header/><soapenv:Body><ws:callService><xmlbody><![CDATA["
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<ROOT>" + "<REQUESTID>" + requestid + "</REQUESTID>"
				+ "<REQUESTTIME>" + requesttime + "</REQUESTTIME>" + "<REQUESTSERVICE>sNetPayLimit</REQUESTSERVICE>"
				+ "<ACCNBR>" + accnbr + "</ACCNBR>" + "<SERVTYPE>" + servtype + "</SERVTYPE>" + "</ROOT>"
				+ "]]></xmlbody><ws:pin>?</ws:pin></ws:callService></soapenv:Body></soapenv:Envelope>";

		String ret = ToolHttp.post(false, url, xml.toString(), "application/text");
		return ret;
	}

	// 客户欠费、余额查询（用户余额=账本预存 - 往月总欠费 - 实时欠费（未出账话费））
	/**
	 * requestid 请求流水 必填 yyyymmddhh24miss+6位循环数字+平台编号 例如： 2015050100000010000102
	 * requesttime 请求时间 必填 yyyymmddhh24miss requestservice 服务名称 必填 accnbr 业务号码
	 * 必填 staffcode 工号 必填 opcode 操作代码 可空 acctid 账户标识 按账户查询必填，按用户查询可空 paratype
	 * 查询标识 a按账户查询，b按用户查询 servtype 号码类型 必填，4：手机 5：宽带 2：固话，99其他；此处可填4或者99
	 * 
	 * @return
	 */
	public static String sendCustFee(String url, String requestid, String requesttime, String accnbr, String staffcode,
			String opcode, String acctid, String paratype, String servtype) {
		String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sitech.com\">"
				+ "<soapenv:Header/><soapenv:Body><ws:callService><xmlbody><![CDATA["
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<root>" + "<REQUESTID type=\"string\">" + requestid
				+ "</REQUESTID>" + "<REQUESTTIME type=\"string\">" + requesttime + "</REQUESTTIME>"
				+ "<REQUESTSERVICE type=\"string\">sCustFeeQry</REQUESTSERVICE>" + "<ACCNBR type=\"string\">" + accnbr
				+ "</ACCNBR>" + "<STAFFCODE type=\"string\">" + staffcode + "</STAFFCODE>" + "<OPCODE type=\"string\">"
				+ opcode + "</OPCODE>" + "<ACCTID type=\"string\">" + acctid + "</ACCTID>"
				+ "<PARATYPE type=\"string\">" + paratype + "</PARATYPE>" + "<SERVTYPE type=\"string\">" + servtype
				+ "</SERVTYPE>" + "</root>"
				+ "]]></xmlbody><ws:pin>?</ws:pin></ws:callService></soapenv:Body></soapenv:Envelope>";

		String ret = ToolHttp.post(false, url, xml.toString(), "application/text");
		return ret;
	}

	public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Map<String, Object> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element bodyElt = rootElt.element("Body");
			Element callServiceResponseElt = bodyElt.element("callServiceResponse");
			String resultXml = callServiceResponseElt.getStringValue();

			Document root = DocumentHelper.parseText(resultXml); // 将字符串转为XML
			Element root1Elt = root.getRootElement(); // 获取根节点
			List<Element> l = root1Elt.elements();
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				if (element.getName().equals("OUT_DATA")) {
					Document d = DocumentHelper.parseText(element.asXML()); // 将字符串转为XML
					Map outdatemap = new HashMap();
					Element r = d.getRootElement(); // 获取根节点
					List<Element> outdataList = r.elements();
					for (Iterator iterator2 = outdataList.iterator(); iterator2.hasNext();) {
						Element element2 = (Element) iterator2.next();
						outdatemap.put(element2.getName(), element2.getStringValue());
					}
					map.put(element.getName(), outdatemap);
				} else {
					map.put(element.getName(), element.getStringValue());
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取6位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 1000000);
	}

	/**
	 * 获取4位序列码
	 * 
	 * @return
	 */
	public static int getFourSquece() {
		return (int) ((Math.random() * 9 + 1) * 10000);
	}

}
