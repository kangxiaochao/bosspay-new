package com.hyfd.service.mp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BatchOfChargerDao;
import com.hyfd.service.BaseService;

import jxl.Sheet;
import jxl.Workbook;

@SuppressWarnings("unchecked")
@Service
public class GuoMeiSer extends BaseService{
	
	private static String resultCode;
	
	@Autowired
	private BatchOfChargerDao batchOfChargerDao;
	
	@Autowired
	private AgentAccountSer accountSer;
	
	@Autowired
	AgentDao agentDao;
	
	Logger log = Logger.getLogger(getClass());
	
	public int recharge(MultipartFile file, HttpServletRequest req) {
		Map<String, Object> ms = getMaps(req);
		ExecutorService pool = Executors.newFixedThreadPool(10);
		List<Map<String, String>> list = getProviderProduct(file);
		if(list == null) {
			return -1;
		}
		List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
		double money = 0;
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> m = list.get(i);
			money += Double.parseDouble(m.get("realityMoney"));
		}
		String agentId = ms.get("agentId").toString();;
		Map<String, Object> maps = new HashMap<>();
		maps.put("bizType", "2");
		maps.put("agentId", agentId);
		String orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS");
		maps.put("agentOrderId", "PK" + orderId);
		maps.put("money", -money);
		System.out.println("扣款金额 " + money);
		double agentMoney = accountSer.getAgentBalance(agentId);
		if(agentMoney > money) {
			if (accountSer.ChargeForBatch(maps)) {
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
							if (!testCreate(map.get("phone"), map.get("money"))) {
								failList.add(map);
								order.put("state", "1");
							} else {
								order.put("state", "0");
								System.out.println("已充值成功" + index + "条--------------------");
							}
							order.put("applyDate", DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
							order.put("resultCode", resultCode);
							order.put("account", "Interface");
							order.put("type", "0");
							batchOfChargerDao.insertSelective(order);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								log.error(e.getMessage());
							}
						}
					});
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
			}
		}else {
			return 0;
		}
		return list.size() - failList.size();
	}
	
	
	
	/**
	 * 获得上家的产品列表
	 * @author lks 2017年9月11日上午8:51:19
	 * @return
	 */
	public List<Map<String, String>> getProviderProduct(MultipartFile file) {
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
	
	public static void mutiTest(){
		for(int i=0;i<10;i++){
		Thread t1=new Thread(new Runnable() {
			public void run() {
				String orderId = testCreate();
				System.out.println(new Date());
			}
		});
		t1.start();
		}
	}
	
	// 测试充值请求 我方->上游
	public static String testCreate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String checkUrl = "http://119.254.77.26:14810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck";

		String url = "http://119.254.77.26:14810/MVNO-UIP-GOMEONLINE/orderTrade/charge";
		String secretKey = "test123456789"; // TODO 秘钥
		String applyChlId = "1000160";// XX—根据不同外围系统提供不同编码 TODO
		//applyChlId = "1000157"; 
//		applyChlId="2016042";

		// 业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数
		String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
		String timestamp = format1.format(new Date());// 2013-12-30 17:36:15
		String provinceCode = "00"; // 定值
		String cityCode = "0";// 定值
		String fromType = "8";// XX—根据不同外围系统提供不同编码 TODO
		String orgType = "2";// X-不同外围系统分配不同参数 TODO

		String operId = "1000160";// 操作员IDXXXXXXX-不同外围系统分配不同参数 TODO
		//operId = "1000157";
		String reqSerial = "100016020160523105318373964697";// 充值请求流水号 订单号 TODO
		//reqSerial = "VGY000103201604182210345238511";
		String payChannel = "8";// 操作渠道 X-不同外围系统分配不同参数 TODO
		String payStyle = "2";// 付款方式 X-不同外围系统分配不同参数 TODO
		String payAmount = "10";// 单位元
		String serviceNum = "17090180539";// 服务号码
		
		//用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
//		String checkResult = sendCheck(checkUrl, secretKey, format.format(new Date()) + getSixSquece());
//		System.out.println("check:" + checkResult);
		
		//处理返回值
//		JSONObject check = JSON.parseObject(checkResult);
//		String checkResultCode = check.getJSONObject("responseHeader").getString("resultCode");
//		String checkResultMessage = check.getJSONObject("responseHeader").getString("resultMessage");
//		System.out.println(checkResultCode + ":" + checkResultMessage);
		
		
		String orderResult = sendOrder(url, secretKey, serialNumber, timestamp, provinceCode, cityCode, fromType, orgType, applyChlId, operId, reqSerial, payChannel, payStyle, payAmount, serviceNum);
		System.out.println("||"+orderResult);
		
		//处理返回值
		JSONObject orderResultJson = JSON.parseObject(orderResult);
		String orderResultCode = orderResultJson.getJSONObject("responseHeader").getString("resultCode");
		String orderResultMessage = orderResultJson.getJSONObject("responseHeader").getString("resultMessage");
		System.out.println(orderResultCode + ":" + orderResultMessage);
		return serialNumber;
	}
	
	// 测试充值请求 我方->上游
	public boolean testCreate(String phone,String money) {
		boolean flag = false;
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String checkUrl = "http://119.254.77.26:14810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck";

			String url = "http://115.182.90.35:26810/MVNO-UIP-GOMESKYPAY/orderTrade/charge";
			String secretKey = "JNHB123456789"; // TODO 秘钥
			String applyChlId = "1000161";// XX—根据不同外围系统提供不同编码 TODO
			//applyChlId = "1000157"; 
//			applyChlId="2016042";

			// 业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数
			String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
			String timestamp = format1.format(new Date());// 2013-12-30 17:36:15
			String provinceCode = "00"; // 定值
			String cityCode = "0";// 定值
			String fromType = "8";// XX—根据不同外围系统提供不同编码 TODO
			String orgType = "2";// X-不同外围系统分配不同参数 TODO

			String operId = "1000161";// 操作员IDXXXXXXX-不同外围系统分配不同参数 TODO
			//operId = "1000157";
			String reqSerial = serialNumber;// 充值请求流水号 订单号 TODO
			//reqSerial = "VGY000103201604182210345238511";
			String payChannel = "8";// 操作渠道 X-不同外围系统分配不同参数 TODO
			String payStyle = "2";// 付款方式 X-不同外围系统分配不同参数 TODO
			String payAmount = money;// 单位元
			String serviceNum = phone;// 服务号码
			
			String orderResult = sendOrder(url, secretKey, serialNumber, timestamp, provinceCode, cityCode, fromType, orgType, applyChlId, operId, reqSerial, payChannel, payStyle, payAmount, serviceNum);
			System.out.println(orderResult);
			
			//处理返回值
			JSONObject orderResultJson = JSON.parseObject(orderResult);
			resultCode = JSONObject.toJSONString(orderResultJson);
			String orderResultCode = orderResultJson.getJSONObject("responseHeader").getString("resultCode");
			String orderResultMessage = orderResultJson.getJSONObject("responseHeader").getString("resultMessage");
			System.out.println(orderResultCode + ":" + orderResultMessage);
			if("000000".equals(orderResultCode)){
				flag = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	// 测试充值请求 我方->上游
	public static void testCheck() {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			
			String applyChlId = "1000160";// XX—根据不同外围系统提供不同编码 TODO
			String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
			
			String checkUrl = "http://119.254.77.26:14810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck";
			String secretKey = "test123456789"; // TODO 秘钥
			String serviceNum = "17090180539";// 服务号码
			
			//用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
			String checkResult = sendCheck(checkUrl, secretKey, serialNumber, serviceNum);
			System.out.println("check:" + checkResult);
			
			//处理返回值
			JSONObject check = JSON.parseObject(checkResult);
			String checkResultCode = check.getJSONObject("responseHeader").getString("resultCode");
			String checkResultMessage = check.getJSONObject("responseHeader").getString("resultMessage");
			System.out.println(checkResultCode + ":" + checkResultMessage);
			
	}

		// 用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
	public static String sendCheck(String checkUrl, String secretKey, String serialNumber, String serviceNum) {

			// http://10.128.22.128:16810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck?
			// serialNumber=200000120140902162250011935001
			// &timestamp=2014-09-02 16:22:50
			// &sign=812d196060596c510675d8975106d6d9
			// &data={"serviceNum":"17090181818"}

			StringBuffer paramBuffer = new StringBuffer();
			JSONObject data = new JSONObject();
			data.put("serviceNum", serviceNum);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			paramBuffer.append("serialNumber=").append(serialNumber);

			paramBuffer.append("&timestamp=").append(format.format(new Date()));
			System.out.println(data);
			String sign = MD5.ToMD5(data.toString() + secretKey);
			paramBuffer.append("&sign=").append(sign);

			paramBuffer.append("&data=").append(data);

			String queryString = "";
			System.out.println(paramBuffer);
			try {
				queryString = URIUtil.encodeQuery(paramBuffer.toString());
				// queryString = paramBuffer.toString();
			} catch (URIException e) {
				e.printStackTrace();
			}
			// queryString = paramBuffer.toString();
			String url = checkUrl + "?" + queryString;
			System.out.println(url);
			String ret = ToolHttp.get(false, url);
			return ret;

	}
	
	/**
	 * 测试查询订单
	 */
	public static void testChargeQuery(){
		String url = "http://115.182.90.35:26810/MVNO-UIP-GOMESKYPAY/orderTrade/chargeQuery";
		
		String secretKey = "JNHB123456789"; // TODO 秘钥
		String applyChlId = "1000161";// XX—根据不同外围系统提供不同编码 TODO
		String provinceCode="00";
		String cityCode="00";
		String fromType="8";
		String orgType="2";
		String operId="1000161";
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp=format1.format(new Date());
		
		
		String serialNumber = applyChlId + format.format(new Date()) + getSixSquece();
		String payOrgSerial="100016120161002152208238729111";
		
		String result=sendChargeQuery(url, secretKey, serialNumber, timestamp, provinceCode, cityCode, fromType, orgType, applyChlId, operId, payOrgSerial);
		System.out.println(result);
		System.out.println(getQueryResultCode(result));
		
	}

	/**
	 * @param url
	 *          链接地址
	 * @param serialNumber
	 *          业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数
	 * @param timestamp
	 *          时间戳
	 * @param provinceCode
	 *          省份代码 请求报文头
	 * @param cityCode
	 *          地市代码 请求报文头
	 * @param fromType
	 *          来源类型 请求报文头
	 * @param orgType
	 *          组织类型 请求报文头
	 * @param applyChlId
	 *          渠道id 请求报文头
	 * @param operId
	 *          操作员ID 请求报文头
	 * @param reqSerial
	 *          充值请求流水号 缴费信息
	 * @param payChannel
	 *          操作渠道 缴费信息
	 * @param payStyle
	 *          付款方式 缴费信息
	 * @param payAmount
	 *          缴费金额 缴费信息 位是元
	 * @param serviceNum
	 *          服务号码 缴费信息
	 * @return
	 */
	public static String sendOrder(String url, String secretKey, String serialNumber, String timestamp, String provinceCode, String cityCode, String fromType, String orgType, String applyChlId, String operId, String reqSerial, String payChannel, String payStyle, String payAmount, String serviceNum) {
		Map data = new LinkedHashMap();

		JSONObject paymentRequest = new JSONObject();
		data.put("paymentRequest", paymentRequest);

		JSONObject requestHeader = new JSONObject();
		JSONObject paymentInfo = new JSONObject();
		paymentRequest.put("requestHeader", requestHeader);
		paymentRequest.put("paymentInfo", paymentInfo);

		requestHeader.put("provinceCode", provinceCode);
		requestHeader.put("cityCode", cityCode);
		requestHeader.put("fromType", fromType);
		requestHeader.put("orgType", orgType);
		requestHeader.put("applyChlId", applyChlId);
		requestHeader.put("operId", operId);

		paymentInfo.put("reqSerial", reqSerial);
		paymentInfo.put("payChannel", payChannel);
		paymentInfo.put("payStyle", payStyle);
		paymentInfo.put("payAmount", payAmount);
		paymentInfo.put("serviceNum", serviceNum);

		System.out.println("明文:" + JSONObject.toJSONString(data) + secretKey);
		String sign = MD5.ToMD5(JSONObject.toJSONString(data) + secretKey);

		Map json = new LinkedHashMap();
		json.put("serial_number", serialNumber);
		json.put("timestamp", timestamp);
		json.put("sign", sign);
		json.put("data", data);
		System.out.println("all:" + JSONObject.toJSONString(json));
		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(JSONObject.toJSONString(json));
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
		}
		String ret = ToolHttp.post(false, url, queryString, null);
		return ret;

	}
	
	public static String sendChargeQuery(String url, String secretKey, String serialNumber, String timestamp, String provinceCode, String cityCode, String fromType, String orgType, String applyChlId, String operId, String payOrgSerial) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONObject requestHeader=new JSONObject();
		requestHeader.put("provinceCode", provinceCode);
		requestHeader.put("cityCode", cityCode);
		requestHeader.put("fromType", fromType);
		requestHeader.put("orgType", orgType);
		requestHeader.put("applyChlId", applyChlId);
		requestHeader.put("operId", operId);
		
		JSONObject chargeQueryRequest=new JSONObject();
		chargeQueryRequest.put("requestHeader", requestHeader);
		chargeQueryRequest.put("payOrgSerial", payOrgSerial); 
		
		JSONObject data = new JSONObject();
		data.put("chargeQueryRequest", chargeQueryRequest);
		
		String sign = MD5.ToMD5(data.toString() + secretKey);
		
		Map json = new LinkedHashMap();
		json.put("serial_number", serialNumber);
		json.put("timestamp", format1.format(new Date()));
		json.put("sign", sign);
		json.put("data", data);
		
		System.out.println(JSONObject.toJSONString(json));
		
		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(JSONObject.toJSONString(json));
		} catch (URIException e) {
			e.printStackTrace();
		}
		String ret = ToolHttp.post(false, url, queryString, null);
		return ret;

	}

	/**
	 * 获取5位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}
	
	public static String getQueryResultCode(String responseStr){
		String resultCode="";
		try{
			JSONObject resultJson= JSON.parseObject(responseStr);
			resultCode= resultJson.getJSONObject("responseHeader").getString("resultCode");
			System.out.println();					
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return resultCode;		
	}
}