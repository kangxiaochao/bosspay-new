package com.hyfd.deal.Bill;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class YinShengBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(getClass());

	// 响应码
	public static Map<String, String> rltMap = new HashMap<String, String>();

	static {
		rltMap.put("0000", "成功");
		rltMap.put("0001", "不成功");
		rltMap.put("0002", "无效银行代码");
		rltMap.put("0003", "无效品种");
		rltMap.put("0004", "数据未授权");
		rltMap.put("0005", "系统维护中（YS手动暂停了业务）");
		rltMap.put("0006", "系统故障");
		rltMap.put("0007", "网络超时");
		rltMap.put("0008", "线路忙");
		rltMap.put("0009", "报文数据格式错");
		rltMap.put("0010", "流水号重复");
		rltMap.put("0011", "该品种暂不受理");
		rltMap.put("1001", "系统忙，暂不受理");
		rltMap.put("1002", "帐号密码有误");
		rltMap.put("1003", "帐号欠费或无效，无法交易");
		rltMap.put("1004", "业务已受理，正在处理中");
		rltMap.put("1005", "商品价格策略未设置");
		rltMap.put("1006", "金额错误");
		rltMap.put("1007", "余额不足");
		rltMap.put("1008", "数据库错误");
		rltMap.put("1009", "需要先查欠费");
		rltMap.put("1010", "查无记录");
		rltMap.put("1011", "无效银行卡帐户");
		rltMap.put("1012", "银行卡帐户已冻结");
		rltMap.put("1013", "银行卡帐户未授权");
		rltMap.put("1014", "银行卡帐户暂不支持");
		rltMap.put("1015", "银行卡号未绑定");
		rltMap.put("1016", "MAC校验错");
		rltMap.put("1017", "无效终端号");
		rltMap.put("1018", "没有电子券");
		rltMap.put("1019", "无效数量值");
		rltMap.put("1020", "无此商户");
		rltMap.put("1021", "重复定制");
		rltMap.put("1022", "单笔刷卡金额超上限");
		rltMap.put("1023", "终端刷卡笔数超上限");
		rltMap.put("1024", "银行卡日交易累计金额超上限");
		rltMap.put("1025", "已过冲正时间");
		rltMap.put("1026", "超出冲正次数");
		rltMap.put("2101", "系统忙，业务暂停受理");
		rltMap.put("1027", "已受理成功，等对账出结果（发生在查询时，当客户端收到此响应，当成功处理，无需再查，等第二天对账）");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
//			String spec = (int)(Double.parseDouble(fee))+ "";// 充值金额，以分为单位
			Map<String, Object> channel = (Map<String, Object>) order
					.get("channel");// 获取通道参数
			String linkUrl = (String) channel.get("link_url");// 充值地址
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String noticeUrl = paramMap.get("noticeUrl");
			String searchUrl = paramMap.get("searchUrl");

			String terminalID = paramMap.get("terminalID");// "20147601";//终端号
			String factoryID = paramMap.get("factoryID");// "0000";//企业编号
			String key = paramMap.get("key");// "000000";//加密key
			String timeStamp = DateTimeUtils.formatDate(new Date(),"yyyyMMddHHmmssSSS");
			String customerOrderId = timeStamp + phone + GenerateData.getIntData(9, 2);
			map.put("orderId", customerOrderId);
			String userNumber = phone;// 充值帐号，如 13534260380
			String billValue = fee;// 充值面额（元）。
			String reqDateTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
			String termTransID = customerOrderId;
			String curType = "001";// 币种 001 人民币
			String msgFlag = "Y";// 是否下发短信标志;Y=下发，N=不发（目前只能是 Y）
			String providerId = order.get("providerId")+"";
			String serFlag = "V";// 是普通充值，还是虚商充值 G=普通，V 虚商（指 170）
			if("0000000001".equals(providerId)||"0000000002".equals(providerId)||"0000000003".equals(providerId)){
				serFlag="G";
			}
			
//			if (userNumber != null && userNumber.indexOf("170") == 0) {
//				serFlag = "V";
//			}
			String ret = sendCreate(linkUrl, terminalID, factoryID, reqDateTime, termTransID, userNumber, billValue,
					curType, msgFlag, serFlag, key);
			log.debug("银盛充值结果：" + ret);
			if (ret == null) {
				log.error("银盛请求：超时！");
			}else{
				JSONObject jsonObject = JSON.parseObject(ret);
				String status = jsonObject.getString("status");
				String upids = jsonObject.getString("transID");
				String msg = jsonObject.getString("msg");
				map.put("resultCode", status+":"+msg);
				// 0000 表示成功，7 0007 指上游 超时，4 1004 表示业务已受理正在处理中，7 1027
				// 表示业务已受理；其它代码都表示订单状态为失败。
				if (status.equals("0000")|| "1004".equals(status) || "1027".equals(status)) {
					map.put("providerOrderId", upids);
					flag = 1;
				}else if ("0007".equals(status)) {
					flag = -1;
				}else{
					flag = 0;
				}
			}
		} catch (Exception e) {
			log.error("银盛充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	/**
	 * 
	 * @param url
	 * @param terminalID
	 *            终端号；YS 分配，如“04800001”
	 * @param factoryID
	 *            企业编号；YS 分配，如“1011”
	 * @param reqDateTime
	 *            请求时间 格式： YYYYMMDDHHMMSS
	 * @param termTransID
	 *            请求流水号(接入商流水) 最少16位，最大32位，用factoryID+定长的序列，请保持唯一
	 * @param userNumber
	 *            充值帐号，如 13534260380
	 * @param billValue
	 *            充值面额（元）。
	 * @param curType
	 *            币种 001 人民币
	 * @param msgFlag
	 *            是否下发短信标志;Y=下发，N=不发（目前只能是 Y）
	 * @param serFlag
	 *            是普通充值，还是虚商充值 G=普通，V 虚商（指 170）
	 * @param key
	 *            秘钥
	 * @return
	 */
	public static String sendCreate(String url, String terminalID, String factoryID, String reqDateTime,
			String termTransID, String userNumber, String billValue, String curType, String msgFlag, String serFlag,
			String key) {

		// System.out.println(ToolValidator.isMobile(account));
		StringBuffer paramBuffer = new StringBuffer();

		// http://113.106.160.201:9800/ysInterfaceServe/phoneRecharge.cgi?
		// terminalID=88888888&
		// factoryID=1011&
		// reqDateTime=20151028162422&
		// termTransID=1020304050607080&
		// userNumber=13534260388&
		// billValue=50&
		// curType=001&
		// msgFlag=Y&
		// serFlag=G&
		// sign=b826e6b186da9a532cc519f11fac90a0
		paramBuffer.append("terminalID=").append(terminalID);
		paramBuffer.append("&factoryID=").append(factoryID);
		paramBuffer.append("&reqDateTime=").append(reqDateTime);
		paramBuffer.append("&termTransID=").append(termTransID);
		paramBuffer.append("&userNumber=").append(userNumber);
		paramBuffer.append("&billValue=").append(billValue);
		paramBuffer.append("&curType=").append(curType);
		paramBuffer.append("&msgFlag=").append(msgFlag);
		paramBuffer.append("&serFlag=").append(serFlag);
//		System.out.println("加密前参数'["+paramBuffer + "&key=" + key+"]");
		String sign = md5Encode(paramBuffer + "&key=" + key);
//		System.out.println("加密后密文'["+sign+"]");
		paramBuffer.append("&sign=").append(sign);
		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
		}
		url = url + "?" + queryString;
		System.out.println(url);
		String ret = ToolHttp.get(false, url);
		return ret;

	}

	public static String sendSearch(String url, String termTransID, String orderDate, String terminalID,
			String factoryID, String reqDateTime, String key) {

		StringBuffer paramBuffer = new StringBuffer();

		paramBuffer.append("terminalID=").append(terminalID);
		paramBuffer.append("&factoryID=").append(factoryID);
		paramBuffer.append("&reqDateTime=").append(reqDateTime);
		paramBuffer.append("&termTransID=").append(termTransID);
		paramBuffer.append("&orderDate=").append(orderDate);
//		System.out.println("加密前参数'["+paramBuffer + "&key=" + key+"]");
		String sign = md5Encode(paramBuffer + "&key=" + key);
//		System.out.println("加密后密文'["+sign+"]");
		paramBuffer.append("&sign=").append(sign);

		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
		}
		url = url + "?" + queryString;
		String ret = ToolHttp.get(false, url);
		return ret;

	}

	public static String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
			String s = Integer.toHexString(bytes[i] & 0xff);
			if(s.length()==1){
			buf.append("0");
			}
			buf.append(s);
		}
		}
		catch(Exception ex){	
		}
		return buf.toString().toLowerCase();//转换成小写字母
	}
	
	public static void main(String[] args) {
		System.out.println(md5Encode("terminalID=20141011&factoryID=JNHB&reqDateTime=20170913154347&termTransID=20170913154344934170633628850&userNumber=17063362885&billValue=10&curType=001&msgFlag=Y&serFlag=V&key=000000"));
	}
}
