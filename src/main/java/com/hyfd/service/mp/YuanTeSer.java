package com.hyfd.service.mp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.RequestToViewNameTranslator;

import com.hyfd.bean.yuante.BodyInfo;
import com.hyfd.bean.yuante.YuanTeBean;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BatchOfChargerDao;
import com.hyfd.service.BaseService;

import jxl.Sheet;
import jxl.Workbook;

@Service
@SuppressWarnings("unused")
public class YuanTeSer extends BaseService{

	private static String resultCode;
	
	@Autowired
	private BatchOfChargerDao batchOfChargerDao;
	
	@Autowired
	private AgentAccountSer accountSer;
	
	@Autowired
	AgentDao agentDao;
	
	Logger log = Logger.getLogger(getClass());
	
	/**
	 * 批量充值小面额话费
	 */
	public int recharge(MultipartFile file, HttpServletRequest req) {
		Map<String, Object> ms = getMaps(req);
		ExecutorService pool = Executors.newFixedThreadPool(10);
		List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> list = upload(file);
		if(list == null) {
			return -1;
		}
		double money = 0;
		for(int i=0;i<list.size();i++) {
			Map<String, String> m = list.get(i);
			money += Double.parseDouble(m.get("realityMoney"));
		}
        String agentId = ms.get("agentId").toString();
		Map<String, Object> maps = new HashMap<>();
		maps.put("bizType", "2");
		maps.put("agentId", agentId);
		String orderId = ToolDateTime.format(new Date(),"yyyyMMddHHmmssSSS") ;
		maps.put("agentOrderId", "PK"+orderId);
		maps.put("money", -money);
		double agentMoney = accountSer.getAgentBalance(agentId);
		if(agentMoney > money) {
			if(accountSer.ChargeForBatch(maps)){
				for (int i = 0; i < list.size(); i++) {
					final int index = i;
					Map<String, String> map = list.get(i);
					pool.execute(new Runnable() {
						public void run() {
							Map<String, Object> order = new HashMap<>();
							order.put("id", UUID.randomUUID().toString().replace("-", ""));
							order.put("phone", map.get("phone"));
							order.put("money", map.get("money"));
							order.put("realityMoney", map.get("realityMoney"));
							if(!testCreate(map.get("phone"),map.get("money"))){
								map.put("state", "充值失败");
								order.put("state", "1");
								failList.add(map);
							}else {
								order.put("state", "0");
								System.out.println("已充值" + index + "条--------------------");
							}
							order.put("applyDate", DateTimeUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
							order.put("resultCode", resultCode);
							order.put("type", "1");
							order.put("account", "Interface");
							batchOfChargerDao.insertSelective(order);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
								failList.add(map);
							}
						}
					});
				}
			}
			pool.shutdown();
			while (true) {
				if (pool.isTerminated()) {
					if(failList.size()>0){
						money = 0;
						for(int i=0;i<failList.size();i++) {
							Map<String, String> m = list.get(i);
							money += Double.parseDouble(m.get("realityMoney"));
						}
						maps.put("money", +money);
						accountSer.ChargeForBatch(maps);
					}
					break;
				}
			}
		}else {
			return 0;
		}
		return list.size()-failList.size();
	}

	/**
	 * 获得上家的产品列表
	 * 
	 * @author lks 2017年9月11日上午8:51:19
	 * @return
	 */
	public List<Map<String, String>> upload(MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Workbook book = Workbook.getWorkbook(file.getInputStream());
				Sheet sheet = book.getSheet(0);
				for (int rows = 0; rows < sheet.getColumn(0).length; rows++) {
					String phone = sheet.getCell(0, rows).getContents();
					String money = sheet.getCell(1, rows).getContents();
					String realtyMoney = sheet.getCell(2, rows).getContents();
					Map<String, String> map = new HashMap<>();
					map.put("phone", phone);
					map.put("money", money);
					map.put("realityMoney", realtyMoney);
					list.add(map);
				}
				book.close();
				return list;
			} catch (FileNotFoundException e) {
				getMyLog(e, log);
				return null;
			} catch (IOException e) {
				getMyLog(e, log);
				return null;
			}catch (Exception e) {
				getMyLog(e, log);
				return null;
			}
		}
		return null;
	}

	// 测试充值请求 我方->上游
	public static boolean testCreate(String phone, String money) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String desUrl = "http://120.26.134.145:22336/des.aspx"; // 加密地址

		String notifyUrl = "http://120.26.134.145/rcmp/jf/orderDeal/statusBackYuanTe";
		String privateKey = "NEUSOFT2";// 加密秘钥
		String phoneNo = phone;// 充值号码
		String userName = "ENTERF0oh5DGw5da"; // Boss系统分配，定值
		String userPwd = "Aa123456789"; // Boss系统分配，定值
		String payFee = money;// 充值金额(元)
		String systemId = "118";// 接入Boss系统的外系统个数，Boss系统分配，定值
		String streamNo = systemId + dateFormat.format(new Date()) + GenerateData.getIntData(9, 8);
		// 3位系统标识 + yyyymmddhhmiss + 8位流水
		String intfType = "1";// 接口类型：1异步接口,定值
		String userId = "0";// 用户标识, 填写0,定值
		String accountId = "0";// 帐户标识，填写0,定值
		String cityCode = "0"; // 受理地市, 填写0,定值
		String dealerId = "A100303443";// 营业厅代码，定值，就写这个
		String serviceKind = "8"; // 业务类型，定值
		String url = "http://61.135.223.103:10023"; // 请求地址,定值
		// 61.135.223.103:10023 正式环境地址
		String data = sendPost(url, streamNo, phoneNo, systemId, userName, userPwd, intfType, notifyUrl, cityCode,
				accountId, userId, dealerId, payFee, serviceKind, desUrl, privateKey);
		System.out.println("---" + data);
		// 解析xml
		// Map<String, String> rltMap = ToolXml.readXmlToMapBack(data);//
		// 此方法得到的map不对，不适用远特返回的数据结构，具体原因:此方法适用于根节点下只有1层的xml结构，远特返回的数据结构为根节点下还有两层
		Map<String, String> rltMap = readXmlToMapFromCreateResponse(data);
		String code = rltMap.get("ResultInfo");
		String submitbackmsg = rltMap.get("ResultCode");
		resultCode = rltMap.toString();
		if ("0".equals(submitbackmsg)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param url
	 * @param orderId
	 *            消息流水（3位系统标识 + yyyymmddhhmiss + 8位流水）
	 * @param phoneNo
	 *            业务号码
	 * @param systemId
	 *            接入boss系统的外系统个数，由boss系统统一分配
	 * @param userName
	 *            登陆系统的用户名，boss系统分配
	 * @param userPwd
	 *            用户名对应的密码
	 * @param intfType
	 *            接口类型：1异步接口
	 * @param notifyUrl
	 *            回调地址
	 * @param cityCode
	 *            城市代码
	 * @param accountId
	 *            帐户标识
	 * @param userId
	 *            用户标识
	 * @param dealerId
	 *            营业厅代码
	 * @param payFee
	 *            充值金额
	 * @param serviceKind
	 *            业务类型
	 * @param privateKey
	 *            加密秘钥
	 * @return
	 */
	public static String sendPost(String url, String orderId, String phoneNo, String systemId, String userName,
			String userPwd, String intfType, String notifyUrl, String cityCode, String accountId, String userId,
			String dealerId, String payFee, String serviceKind, String desUrl, String privateKey) {

		YuanTeBean bean = new YuanTeBean();
		bean.setStreamNo(orderId);
		bean.setSystemId(systemId);
		bean.setUserName(userName);
		bean.setUserPwd(userPwd);
		bean.setIntfType(intfType);// 接口类型：1异步接口
		bean.setRechargeType("0");// 充值类型：0 代理商充值
		bean.setNotifyURL(notifyUrl);
		List bodyInfos = new ArrayList();
		BodyInfo bodyInfo = new BodyInfo();
		bodyInfo.setCityCode(cityCode);
		bodyInfo.setAccountId(accountId);// 帐户标识
		bodyInfo.setUserId(userId);// 用户标识
		bodyInfo.setDealerId(dealerId);// 营业厅代码
		bodyInfo.setNotifyDate(ToolDateTime.format(new Date(), "yyyyMMddHHMMdd"));// 缴费时间
		bodyInfo.setOperator(userName);
		bodyInfo.setPayFee(payFee);
		bodyInfo.setServiceId(phoneNo);// 充值号码，业务号码
		bodyInfo.setServiceKind(serviceKind);// 业务类型： 8
		bodyInfo.setIfContinue("30");// 缴费方式30代理商缴费

		bodyInfos.add(bodyInfo);
		bean.setBodys(bodyInfos);

		// 拼接xml
		String xml = createRequestXML(bean, desUrl, privateKey);

		String ret = ToolHttp.post(true, url, xml, null);
		// String ret = yuanTeInterface.sendPostCreate(xml, url);
		return ret;
		// 解析xml
		// Map<String, String> result = ToolXml.readXmlToMapBack(ret);//
		// 此方法得到的map不对，不适用远特返回的数据结构
		// result = YuanTeInterface.handleCreateResponse(ret);

	}

	/**
	 * 根据参数bean拼成上游所需xml字符串
	 * 
	 * @param bean
	 * @return 包含xml信息的字符串
	 */
	public static String createRequestXML(YuanTeBean bean, String desUrl, String privateKey) {

		StringBuffer encryptInfoXML = new StringBuffer();
		encryptInfoXML.append("<StreamNo>" + bean.getStreamNo() + "</StreamNo>");
		encryptInfoXML.append("<SystemId>" + bean.getSystemId() + "</SystemId>");
		encryptInfoXML.append("<UserName>" + bean.getUserName() + "</UserName>");
		encryptInfoXML.append("<UserPwd>" + bean.getUserPwd() + "</UserPwd>");
		encryptInfoXML.append("<IntfType>" + bean.getIntfType() + "</IntfType>");
		encryptInfoXML.append("<RechargeType>" + bean.getRechargeType() + "</RechargeType>");
		encryptInfoXML.append("<NotifyURL>" + bean.getNotifyURL() + "</NotifyURL>");
		for (BodyInfo bodyInfo : bean.getBodys()) {
			encryptInfoXML.append("<BodyInfo>");
			encryptInfoXML.append("<accountId>" + bodyInfo.getAccountId() + "</accountId>");
			encryptInfoXML.append("<cityCode>" + bodyInfo.getCityCode() + "</cityCode>");
			encryptInfoXML.append("<dealerId>" + bodyInfo.getDealerId() + "</dealerId>");
			encryptInfoXML.append("<ifContinue>" + bodyInfo.getIfContinue() + "</ifContinue>");
			encryptInfoXML.append("<notifyDate>" + bodyInfo.getNotifyDate() + "</notifyDate>");
			encryptInfoXML.append("<operator>" + bodyInfo.getOperator() + "</operator>");
			encryptInfoXML.append("<payFee>" + bodyInfo.getPayFee() + "</payFee>");
			encryptInfoXML.append("<userId>" + bodyInfo.getUserId() + "</userId>");
			encryptInfoXML.append("<serviceId>" + bodyInfo.getServiceId() + "</serviceId>");
			encryptInfoXML.append("<serviceKind>" + bodyInfo.getServiceKind() + "</serviceKind>");
			encryptInfoXML.append("</BodyInfo>");
		}
		String encryptInfo = encryptInfoXML.toString();
		// encryptInfo =
		// "<StreamNo>1012015033118555510001234</StreamNo><SystemId>101</SystemId><UserName>test</UserName><UserPwd>test</UserPwd><IntfType>1</IntfType><RechargeType>0</RechargeType><NotifyURL>http://127.0.0.1:8080/services/chargeNotify</NotifyURL><BodyInfo><accountId>14508</accountId><cityCode>110</cityCode><dealerId>1111404</dealerId><ifContinue>29</ifContinue><notifyDate>20150402152003</notifyDate><operator>LYBJ_TEST01</operator><payFee>10</payFee><userId>41629</userId><serviceId>17090060696</serviceId><serviceKind>8</serviceKind></BodyInfo>";
		// key = "NEUSOFT2";//远特给的key

		// 得到密文
		// String desEncryptInfo = DES.encrypt(privateKey, encryptInfo.toString());
		String desEncryptInfo = getDes(desUrl, "1", encryptInfo.toString(), privateKey);
		desEncryptInfo = desEncryptInfo.trim();

		StringBuffer xml = new StringBuffer();
		xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">")
				.append("<soapenv:Header/>").append("<soapenv:Body>")
				.append("<ContentLen>" + desEncryptInfo.length() + "</ContentLen>").append("<EncryptInfo>");
		String ming = xml.toString();
		xml.append(desEncryptInfo);
		StringBuffer lastXml = new StringBuffer();
		lastXml.append("</EncryptInfo>");
		lastXml.append("</soapenv:Body>");
		lastXml.append("</soapenv:Envelope>");
		String mingwen = ming + encryptInfoXML + lastXml;
		String desWen = xml.toString() + lastXml;
		// String testxml =
		// "<soapenv:Envelope
		// xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><ContentLen>728</ContentLen><EncryptInfo><StreamNo>1012015033118555510001234</StreamNo><SystemId>101</SystemId><UserName>test</UserName><UserPwd>test</UserPwd><IntfType>1</IntfType><RechargeType>0</RechargeType><NotifyURL>http://127.0.0.1:8080/services/chargeNotify</NotifyURL><BodyInfo><accountId>14508</accountId><cityCode>110</cityCode><dealerId>1111404</dealerId><ifContinue>29</ifContinue><notifyDate>20150402152003</notifyDate><operator>LYBJ_TEST01</operator><payFee>10</payFee><userId>41629</userId><serviceId>17090060696</serviceId><serviceKind>8</serviceKind></BodyInfo></EncryptInfo></soapenv:Body></soapenv:Envelope>";
		return desWen.toString();
	}

	/**
	 * 将xml字符串转换成map(给上游发送请求，有数据返回时调用)
	 * 
	 * @param xml
	 *            发送充值请求后，上游返回的xml
	 * @return Map结构:<"StreamNo", string> 消息流水（3位系统标识 + yyyymmddhhmiss +
	 *         8位流水），同请求消息头<br />
	 *         <"ResultCode", string> 接收结果 0 成功 1失败<br />
	 *         <"ResultInfo", string> 失败原因<br />
	 */
	public static Map<String, String> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List l = rootElt.elements();
			Element recordEle1 = rootElt.element("Body");
			String streamNo = recordEle1.elementTextTrim("StreamNo");
			String flag = recordEle1.elementTextTrim("flag");
			String resultCode = recordEle1.elementTextTrim("ResultCode");
			String resultInfo = recordEle1.elementTextTrim("ResultInfo");
			map.put("StreamNo", streamNo);
			map.put("flag", flag);
			map.put("ResultCode", resultCode);
			map.put("ResultInfo", resultInfo);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 接收的上游主动推送的xml,将xml字符串转换成map(回调时调用)
	 * 
	 * @param xml
	 *            上游主动推送的xml
	 * @return Map Map结构:<"StreamNo", string> 消息流水（3位系统标识 + yyyymmddhhmiss +
	 *         8位流水），同请求消息头<br />
	 *         <"flag", string> 接返回码 0 成功<br />
	 *         <"flowNumber", string> 缴费流水<br />
	 *         <"message", string> 失败原因<br />
	 *         <"payDate", string> 缴费时间<br />
	 *         <"writeOffDate", string> 本次销账的月份<br />
	 *         <"restFee", string> 余额<br />
	 */
	public static Map<String, String> readXmlToMapFromCallback(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List l = rootElt.elements();
			Element recordEle1 = rootElt.element("Body");

			String streamNo = recordEle1.elementTextTrim("StreamNo");
			String flag = recordEle1.elementTextTrim("flag");
			String flowNumber = recordEle1.elementTextTrim("flowNumber");
			String message = recordEle1.elementTextTrim("message");
			String payDate = recordEle1.elementTextTrim("payDate");
			String writeOffDate = recordEle1.elementTextTrim("writeOffDate");
			String restFee = recordEle1.elementTextTrim("restFee");

			map.put("StreamNo", streamNo);
			map.put("flag", flag);
			map.put("flowNumber", flowNumber);
			map.put("message", message);
			map.put("payDate", payDate);
			map.put("writeOffDate", writeOffDate);
			map.put("restFee", restFee);

		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}

	/**
	 * @功能描述： 对时局进行加密
	 *
	 * @作者：zhangpj @创建时间：2016年2月19日
	 * @param desUrl
	 *            加密地址
	 * @param desType
	 *            1:加密 0：解密
	 * @param srcCode
	 * @return
	 */
	public static String getDes(String desUrl, String desType, String srcCode, String desKey) {
		String data = "desType=" + desType;
		data += "&srcCode=" + srcCode;
		data += "&desKey=" + desKey;
		String result = ToolHttp.post(false, desUrl, data, "application/x-www-form-urlencoded");
		return result;
	}

}
