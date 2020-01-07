package com.hyfd.deal.Bill;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.utils.Base64;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 紫藤话费业务处理
 * 
 * @author Administrator
 *
 */
public class ZiTengBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(ZiTengBillDeal.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("0000", "成功");
		rltMap.put("1100", "ID错误");
		rltMap.put("1000", "错误的交易类型");
		rltMap.put("1001", "错误的号码运营商");
		rltMap.put("1002", "错误的充值金额");
		rltMap.put("1003", "错误的手机号码");
		rltMap.put("1004", "省份编码错误");
		rltMap.put("1005", "号码与运营商不匹配");
		rltMap.put("1006", "错误的手机归属地");
		rltMap.put("1007", "数字签名错误");
		rltMap.put("1008", "错误的订单号");
		rltMap.put("1009", "重复的订单提交");
		rltMap.put("1010", "余额不足");
		rltMap.put("1011", "该地区暂停充值服务");
	}

	/**
	 * 紫藤业务处理
	 * 
	 * @param obj
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			double fee = new Double(order.get("fee") + "");// 金额，以元为单位
			String spec = new Double(fee * 100).intValue() + "";// 充值金额，以分为单位
			String providerId = order.get("providerId")+"";
			
			String provinceCode = (String) order.get("channelProvinceCode"); // 手机号段所属省份
			Map<String, Object> channel = (Map<String, Object>) order
					.get("channel");// 获取通道参数

			// 生成自己的id，供回调时查询数据使用
			String orderId = ToolDateTime.format(new Date(),
					"yyyyMMddHHmmssSSS") + getThrSquece();
			map.put("orderId", orderId);

			String data = markUrlAndPost(channel, providerId, provinceCode,
					orderId, spec, phoneNo);
			log.error("紫藤[话费充值]请求返回信息[" + data + "]");
			if (data != null) {
				String code = data.substring(data.lastIndexOf("=") + 1);
				map.put("resultCode", code + ":" + rltMap.get(code));
				if (!"0000".equals(code) && rltMap.containsKey(code)) {
					log.debug("紫藤[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["
							+ spec + "]," + code + "[" + rltMap.get(code) + "]");
					flag = 0;
				} else if ("0000".equals(code)) {
					flag = 1;
					log.debug("紫藤[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+spec+"]");
				}
			}
		} catch (Exception e) {
			log.error("紫藤[话费充  值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 
	 * @param templateId
	 * @param customerOrderId
	 * @param phoneNo
	 * @param spec
	 * @param scope
	 * @return
	 */
	public static String markUrlAndPost(Map<String, Object> channel,
			String providerType, String belongzone, String orderId,
			String amount, String mobile) {
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String appId = paramMap.get("appId").toString();
		String linkUrl = channel.get("link_url").toString();
		String secretKey = paramMap.get("secretKey").toString();

		String timestamp = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
		String operator = null;
		if("0000000001".equals(providerType)){
			operator = "YD";
		}else if ("0000000002".equals(providerType)) {
			operator = "UN";
		}else if("0000000003".equals(providerType)){
			operator = "DX";
			
		}else{
			operator = "VT"; // 虚拟运营商
		}
		String province = "";
		try {
			province = Base64.encodeBytes(belongzone.getBytes("gbk"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String param = chargeSignFuc(appId, operator, province, timestamp,
				mobile, orderId, amount, secretKey);
		log.error("ziteng request|" + linkUrl + "?" + param);
		String result = ToolHttp.get(false, linkUrl + "?" + param);
		return result;
	}

	/**
	 * 获取3位序列码
	 * 
	 * @return
	 */
	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100);
	}

	/**
	 * md5摘要
	 * 
	 * @param custInteId
	 * @param orderId
	 * @param secretKey
	 * @param echo
	 * @param timestamp
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String chargeSignFuc(String cpid, String operator,
			String province, String create_time, String mobile_num,
			String cp_order_no, String amount, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append("cpid=").append(cpid).append("&trade_type=CZ&operator=")
				.append(operator).append("&province=").append(province)
				.append("&create_time=").append(create_time)
				.append("&mobile_num=").append(mobile_num)
				.append("&cp_order_no=").append(cp_order_no).append("&amount=")
				.append(amount).append("&ret_para=");
		String param = sb.toString() + key;
		String md5 = MD5.ToMD5(param);
		StringBuilder sb1 = new StringBuilder();
		sb1.append("cpid=").append(cpid).append("&trade_type=CZ&operator=")
				.append(operator).append("&province=")
				.append(URLEncoder.encode(province)).append("&create_time=")
				.append(create_time).append("&mobile_num=").append(mobile_num)
				.append("&cp_order_no=").append(cp_order_no).append("&amount=")
				.append(amount).append("&ret_para=");
		sb1.append("&sign=").append(md5);
		return sb1.toString();
	}

	/**
	 * 当订单提交失败或上家无响应时，需要向上家提交查询，查询 此订单 是否成功
	 * 
	 * @param queryUrl
	 * @param cpid
	 * @param cp_order_no
	 * @param mobile_num
	 * @param secretKey
	 * @return
	 */
	public static boolean isSuccess(String queryUrl, String cpid,
			String cp_order_no, String mobile_num, String secretKey) {
		boolean flag = false;
		try {
			String sign = getQueryOrderSign(cpid, cp_order_no, mobile_num,
					secretKey);
			String queryOrderUrl = queryUrl;
			queryOrderUrl += "?cpid=" + cpid;
			queryOrderUrl += "&cp_order_no=" + cp_order_no;
			queryOrderUrl += "&mobile_num=" + mobile_num;
			queryOrderUrl += "&sign=" + sign;
			log.error("ziteng query request|" + queryOrderUrl);
			String responseStr = ToolHttp.get(false, queryOrderUrl);
			String st = responseStr.substring(responseStr.lastIndexOf("=") + 1);
			log.error("ziteng query response|" + responseStr);
			if ("0001".equals(st) || "0000".equals(st)) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ziteng query exception");
		}
		return flag;
	}

	public static String getQueryOrderSign(String cpid, String cp_order_no,
			String mobile_num, String secretKey) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		sb.append("cpid=" + cpid);
		sb.append("&cp_order_no=" + cp_order_no);
		sb.append("&mobile_num=" + mobile_num);
		sb.append("" + secretKey);
		result = sb.toString();
		return MD5.ToMD5(result);
	}
}
