package com.hyfd.service.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class TianYinQuerySer extends BaseService {

	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	@Autowired
	OrderDao orderDao;

	@Autowired
	PhoneSectionDao phoneSectionDao;

	@Autowired
	ProviderDao providerDao;

	/**
	 * 获取余额信息 传入手机号
	 * 
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap key:amount 余额 单位元 key:status
	 *         查询状态 0查询成功 1查询失败 key:phoneownername 机主姓名
	 */

	public Map<String, String> getChargeAmountInfo(String mobileNumber) {

		Map<String, String> amountInfoMap = new HashMap<String, String>();
		String id = "2000000015";
        Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
        String defaultParameter = (String)channel.get("default_parameter");// 默认参数
        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
        
		String chargeAmountQryUrl = paramMap
				.get("chargeAmountQryUrl"); // 取出查询接口方法

		String platyformId = paramMap.get("platyformId");// 取出平台编号
		String requestid = ToolDateTime.format(new Date(), "yyyyMMddHHmmss")
				+ getSixSquece() + platyformId;// 请求流水号
		String requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");// 请求时间
		String staffcode = paramMap.get("staffcode");// 工号
		String opcode = paramMap.get("opcode");// 操作代码
		String acctid = "";// 按账户查询必填，按用户查询可空
		String paratype = "B";// A按账户查询，B按用户查询
		String servtype = "4";// 4表示手机
		String ret = sendCustFee(chargeAmountQryUrl, requestid, requesttime,
				mobileNumber, staffcode, opcode, acctid, paratype, servtype);
		if(null == ret){
			amountInfoMap.put("status", "1");
			amountInfoMap.put("amount", "0");
		}else {
			Map<String, Object> result = readXmlToMapFromCreateResponse(ret);// 返回查询结果
//			{RESPONSEID=201812201614459233817DLS010, RETURN_MSG=确定帐户在网离网标识出错, RETURN_CODE=130002, RESPONSETIME=20181220161445}
			String returncode = (String) result.get("RETURN_CODE");// 返回000000为成功，否则失败
			if(result.containsKey("OUT_DATA")) {
				Map<String, String> rMap = (Map<String, String>) result.get("OUT_DATA");

				String acctname = rMap.get("ACCTNAME");// 获取用户名
				if (result != null && returncode.equals("000000")) {// 判断
					amountInfoMap.put("status", "0");// 0为成功1为失败
					int servBalance = Integer.parseInt(rMap.get("SERVBALANCE"));
					int unBillfee  = Integer.parseInt(rMap.get("UNBILLFEE"));
					int oweamount  = Integer.parseInt(rMap.get("OWEAMOUNT"));
					amountInfoMap.put("amount",
							(Double.parseDouble("" + (servBalance-unBillfee-oweamount)) / 100)
									+ "");// 获取余额，单位为分
					if (acctname != null) {
						amountInfoMap.put("phoneownername", acctname);
					} else {
						amountInfoMap.put("phoneownername", "未知");
					}
				} else {
					amountInfoMap.put("status", "1");
					amountInfoMap.put("amount", "0");
				}
			}else {
				amountInfoMap.put("phoneownername", "未知");
				amountInfoMap.put("status", "1");
				amountInfoMap.put("amount", "0");
			}
		}
		return amountInfoMap;
	}

	public static Map<String, Object> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element bodyElt = rootElt.element("Body");
			Element callServiceResponseElt = bodyElt
					.element("callServiceResponse");

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
					for (Iterator iterator2 = outdataList.iterator(); iterator2
							.hasNext();) {
						Element element2 = (Element) iterator2.next();
						outdatemap.put(element2.getName(),
								element2.getStringValue());
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
	 * requestid 请求流水 必填 yyyymmddhh24miss+6位循环数字+平台编号 例如： 2015050100000010000102
	 * requesttime 请求时间 必填 yyyymmddhh24miss requestservice 服务名称 必填 accnbr 业务号码
	 * 必填 staffcode 工号 必填 opcode 操作代码 可空 acctid 账户标识 按账户查询必填，按用户查询可空 paratype
	 * 查询标识 a按账户查询，b按用户查询 servtype 号码类型 必填，4：手机 5：宽带 2：固话，99其他；此处可填4或者99
	 * 
	 * @return
	 */

	public static String sendCustFee(String url, String requestid,
			String requesttime, String accnbr, String staffcode, String opcode,
			String acctid, String paratype, String servtype) {
		String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.sitech.com\">"
				+ "<soapenv:Header/><soapenv:Body><ws:callService><xmlbody><![CDATA["
				+ "<?xml version=\"1.0\" encoding=\"GBK\"?>"
				+ "<root>"
				+ "<REQUESTID type=\"string\">"
				+ requestid
				+ "</REQUESTID>"
				+ "<REQUESTTIME type=\"string\">"
				+ requesttime
				+ "</REQUESTTIME>"
				+ "<REQUESTSERVICE type=\"string\">sCustFeeQry</REQUESTSERVICE>"
				+ "<ACCNBR type=\"string\">"
				+ accnbr
				+ "</ACCNBR>"
				+ "<STAFFCODE type=\"string\">"
				+ staffcode
				+ "</STAFFCODE>"
				+ "<OPCODE type=\"string\">"
				+ opcode
				+ "</OPCODE>"
				+ "<ACCTID type=\"string\">"
				+ acctid
				+ "</ACCTID>"
				+ "<PARATYPE type=\"string\">"
				+ paratype
				+ "</PARATYPE>"
				+ "<SERVTYPE type=\"string\">"
				+ servtype
				+ "</SERVTYPE>"
				+ "</root>"
				+ "]]></xmlbody><ws:pin>?</ws:pin></ws:callService></soapenv:Body></soapenv:Envelope>";

		String ret = ToolHttps.post(false, url, xml.toString(),
				"application/text");
		return ret;
	}

	/**
	 * 获取6位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 1000000);
	}
}
