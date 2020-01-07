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
 * 优启话费 业务处理
 * @author Administrator
 *
 */
public class YouQiBillDeal implements BaseDeal{

	private static Logger log = Logger.getLogger(YouQiBillDeal.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "成功");
		rltMap.put("-101", "失败");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
			
			Map<String, Object> channel = (Map<String, Object>) order
					.get("channel");// 获取通道参数

			// 生成自己的id，供回调时查询数据使用
			String order_id = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS") + phoneNo + ((int)(Math.random()*9000)+1000);//32位订单号
			String timestamp = DateUtils.getNowTimeToSec();
			
			map.put("orderId", order_id);

			Map<String,Object> data = markUrlAndPost(channel, "DX", timestamp , order_id , phoneNo , fee);
			log.error("优启[话费充值]请求返回信息[" + data + "]");
			if (data != null) {
				String code = data.get("code").toString();
				map.put("resultCode", code + ":" + rltMap.get(code));
				if (!"0".equals(code) && rltMap.containsKey(code)) {
					log.debug("优启[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["
							+ fee + "]," + code + "[" + rltMap.get(code) + "]");
					flag = 0;
				} else if ("0".equals(code)) {
					flag = 1;
					log.debug("优启[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+fee+"]");
				}
			}
		} catch (Exception e) {
			log.error("优启[话费充  值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> markUrlAndPost(Map<String, Object> channel,
			String product, String timestamp, String order_id,
			String account, String face) {
		String defaultParameter = (String) channel.get("default_parameter");//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String appid = paramMap.get("appId").toString();
		String linkUrl = paramMap.get("chongzhiUrl").toString();
		String appKey = paramMap.get("appKey").toString();
		String param = chargeSignFuc(account,appid,appKey,face,order_id,product,timestamp);
		log.error("youqi request|" + linkUrl + "?" + param);
		Map<String,Object> result = new HashMap<>();
		String  msg = HttpUtils.doGet(linkUrl + "?" +param);
		log.error("优启[话费充值]请求信息" + linkUrl + "?" +param);
		log.error("优启[话费充值]返回信息" + msg);
		result = JSONObject.toJavaObject(JSONObject.parseObject(msg), Map.class);
		return result;
	}
	
	public static String chargeSignFuc(String account, String appid,String appKey, String face, String order_id,String product, String timestamp) {
		StringBuilder sb = new StringBuilder();
		sb.append("account=").append(account);
		sb.append("&appid=").append(appid);
		sb.append("&appkey=").append(appKey);
		sb.append("&face=").append(face);
		sb.append("&order_id=").append(order_id);
		sb.append("&product=").append(product);
		sb.append("&timestamp=").append(timestamp);
		String sing = MD5.ToMD5(sb.toString());
		
		StringBuilder url = new StringBuilder();
		url.append("appid=").append(appid);
		url.append("&product=").append(product);
		url.append("&face=").append(face);
		url.append("&timestamp=").append(timestamp);
		url.append("&order_id=").append(order_id);
		url.append("&account=").append(account);
		url.append("&sign=").append(sing);
		return url.toString();
	}
}
