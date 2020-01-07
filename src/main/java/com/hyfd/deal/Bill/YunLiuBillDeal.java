package com.hyfd.deal.Bill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 云流全国话费充值处理
 * @author Administrator
 *
 */
public class YunLiuBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "成功");
		rltMap.put("001", "参数错误");
		rltMap.put("002", "充值号码不合法");
		rltMap.put("003", "帐号密码错误");
		rltMap.put("004", "余额不足 ");
		rltMap.put("005", "不存在指定话费包");
		rltMap.put("006", "不支持该地区");
		rltMap.put("007", "卡号或者密码错误");
		rltMap.put("008", "该卡已使用过");
		rltMap.put("009", "该卡不支持(移动/电信/联通)号码");
		rltMap.put("010", "协议版本错误");
		rltMap.put("100", "签名错误");
		rltMap.put("999", "其他错误(不直接作为失败，是否成功请联系平台确认)");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			
			String url = (String) channel.get("link_url");//充值连接
			
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String account = paramMap.get("account");//账户名
			String apiKey = paramMap.get("apiKey");//密钥
			String action = paramMap.get("action");//命令
			String v = paramMap.get("v");//版本
			String mobile = (String) order.get("phone");//手机号
			String outTradeNo = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + mobile + ((int) (Math.random() * 9000) + 1000);//订单号
			String fee = order.get("fee") + "";
			String packages = fee.substring(0,fee.indexOf("."));
			//加密
			Map<String, String> signMap = new HashMap<>();
			signMap.put("mobile", mobile);
			signMap.put("package", packages);
			signMap.put("account", account);
			
			String sign = getSign(signMap,apiKey);
			
			url += "?v=" + v + "&action=" + action + "&account=" + account + "&mobile=" + mobile 
					+ "&package=" + packages + "&outTradeNo=" + outTradeNo + "&sign=" + sign;
			log.error("云流充值请求信息："+url);
			map.put("orderId", outTradeNo);

			
			String result = HttpUtils.doGet(url);
			if (!StringUtil.empty(result)) {
				JSONObject resultJson = JSONObject.parseObject(result);
				String ReCode = resultJson.getString("Code");
				map.put("resultCode", ReCode + ":" + rltMap.get(ReCode));
				if ("0".equals(ReCode)) {
					flag = 1;
					map.put("providerOrderId", resultJson.getString("TaskID"));
				} else if("999".equals(ReCode)){
					flag = -1;
				}else{
					flag = 0;
				}
				log.error("云流充值提交完成，返回信息为" + result);
			} else {// 未拿到返回数据
				map.put("resultCode", "未拿到返回数据");
			}
		} catch (Exception e) {
			log.error("云流充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	/**
	 * 签名加密
	 * @param map
	 * @return
	 */
	public String getSign(Map<String, String> map,String apiKey) {
		String result = "";
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			
			// 构造签名键值对的格式
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> item : infoIds) {
				if (item.getKey() != null || item.getKey() != "") {
					String key = item.getKey();
					String val = item.getValue();
					if (!(val == "" || val == null)) {
						sb.append(key + "=" + val +"&");
					}
				}
 
			}
			sb.append("key="+apiKey);
			result = sb.toString();
			log.error("云流加密信息为："+result);
			//进行MD5加密
			result = MD5.ToMD5(result).toLowerCase();
		} catch (Exception e) {
			return null;
		}
		return result;
	}
}
