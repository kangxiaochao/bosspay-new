package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 全国空充处理逻辑
 * @author Administrator
 */
public class QuanGuokongChongBillDeal implements BaseDeal{

	private static Logger log = Logger.getLogger(QuanGuokongChongBillDeal.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("00", "受理成功");
		rltMap.put("10", "缺少参数");
		rltMap.put("11", "校验码验证失败");
		rltMap.put("20", "商品状态相关错误");
		rltMap.put("21", "商品价格错误");
		rltMap.put("22", "订单相关错误");
		rltMap.put("23", "流水号重复");
		rltMap.put("30", "上游供货失败");
		rltMap.put("31", "上游供货超时");
		rltMap.put("40", "合作方账号类型或者状态错误");
		rltMap.put("41", "合作方资金账户错误");
		rltMap.put("42", "合作方余额不足等错误");
		rltMap.put("43", "合作方支付错误");
		rltMap.put("44", "合作方没有该业务权限");
		rltMap.put("50", "系统异常");
		rltMap.put("51", "交易繁忙，  上游供货繁忙。");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("providerProductId") + "";// 金额，上家产品编码
			
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数

			// 生成自己的id，供回调时查询数据使用
			String serialno = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS") + phoneNo + ((int)(Math.random()*9000)+1000);//32位订单号
			String dtCreate = DateUtils.getNowTimeToSec();
			
			map.put("orderId", serialno);

			Map<String,Object> data = markUrlAndGet(channel, dtCreate , serialno , phoneNo , fee);
			log.error("全国空充[话费充值]请求返回信息[" + data + "]");
			if (data != null) {
				String code = data.get("code").toString();
				map.put("resultCode", code + ":" + rltMap.get(code));
				if (!"00".equals(code) && rltMap.containsKey(code)) {
					log.debug("全国空充[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["
							+ fee + "]," + code + "[" + rltMap.get(code) + "]");
					flag = 0;
				} else if ("00".equals(code)) {
					flag = 1;
					log.debug("全国空充[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+fee+"]");
				}
			}
		} catch (Exception e) {
			log.error("全国空充[话费充  值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> markUrlAndGet(Map<String, Object> channel, String dtCreate, String serialno,String uid, String itemId) {
		
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String userId = paramMap.get("userId").toString();
		String linkUrl = paramMap.get("linkUrl").toString();
		String appKey = paramMap.get("appKey").toString();
		String param = chargeSignFuc(uid,dtCreate,itemId,serialno,userId,appKey);
		log.error("quanGuoKongChong request|" + linkUrl + "?" + param);
		Map<String,Object> result = new HashMap<>();
		String  msg = HttpUtils.doGets(linkUrl + "?" +param);
		log.error("全国空充[话费充值]返回信息" + msg);
		result = JSONObject.toJavaObject(JSONObject.parseObject(msg),Map.class);
		return result;
	}
	
	/**
	 * 拼接字符串
	 * @param uid
	 * @param dtCreate
	 * @param itemId
	 * @param serialno
	 * @param userId
	 * @param appKey
	 * @return
	 */
	public static String chargeSignFuc(String uid,String dtCreate, String itemId, String serialno,String userId, String appKey) {
		StringBuffer su = new StringBuffer();
		String sign = MD5.ToMD5(dtCreate+itemId+serialno+uid+userId+appKey);//签名
		su.append("sign=").append(sign);
		su.append("&uid=").append(uid);
		su.append("&dtCreate=").append(dtCreate);
		su.append("&userId=").append(userId);
		su.append("&itemId=").append(itemId);
		su.append("&serialno=").append(serialno);
		return su.toString();
	}

}
