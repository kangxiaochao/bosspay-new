package com.hyfd.deal.Bill;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 飞游全国移动话费充值处理
 * @author Administrator
 *
 */
public class FeiYouBillDeal implements BaseDeal {
	
	Logger log = Logger.getLogger(getClass());
	
	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("A00", "操作成功");
		rltMap.put("A11", "参数错误");
		rltMap.put("A20", "请求超速");
		rltMap.put("A20", "用户状态正常 ");
		rltMap.put("A21", "用户不存在");
		rltMap.put("A22", "用户状态暂停");
		rltMap.put("A23", "用户状态测试");
		rltMap.put("A24", "用户状态不可用");
		rltMap.put("A30", "签名正确");
		rltMap.put("A31", "签名错误");
		rltMap.put("A40", "订单号不存在错误");
		rltMap.put("A41", "订单号重复");
		rltMap.put("A52", "余额不足");
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phone = (String) order.get("phone");// 手机号
			String productID = order.get("providerProductId") + "";// 金额，上家产品编码
			
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String linkUrl = (String) channel.get("link_url");// 充值地址
			String appkey = paramMap.get("appKey");
			String secret = paramMap.get("secret");
			
			// 生成自己的id，供回调时查询数据使用
			String outOrderID = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + phone + ((int) (Math.random() * 9000) + 1000);//订单号
			String timeStamp = DateUtils.getNowTimeToSec();
			
			String prams = jointUrl(appkey, outOrderID, phone, productID, timeStamp, secret);
			JSONObject result = JSONObject.parseObject(HttpUtils.doPost(linkUrl,prams));
			log.error("飞游全国移动充值返回信息："+result);
			String code = result.getString("code");
			if("A00".equals(code)) {
				String status = result.getString("status");
				if(status.equals("1")) {
					flag = 1;
					log.error("飞游[话费充值]请求:提交成功!手机号["+phone+"],商品ID["+productID+"]");
				}else if(!status.equals("2")){
					flag = 0;
					log.error("飞游[话费充值]请求:提交失败!手机号["+phone+"],商品ID["+productID+"]");
				}
			}else {
				flag = 0;
			}
			map.put("orderId", outOrderID);
			map.put("resultCode", code + ":" + rltMap.get(code));
		} catch (Exception e) {
			log.error("飞游全国移动[话费充  值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	public String jointUrl(String appkey,String outOrderID,String phone,String productID,String timeStamp,String secret){
		StringBuffer url = new StringBuffer();
		try {
			StringBuffer sing = new StringBuffer();
			sing.append("appKey="+appkey);
			sing.append("&callbackUrl=http://118.31.229.23:8080/bosspaybill/status/FeiYouBack");
			sing.append("&outOrderID="+outOrderID);
			sing.append("&phone="+phone);
			sing.append("&productID="+productID);
			sing.append("&timeStamp="+timeStamp);
			sing.append(secret);
			
			url.append("appKey="+appkey);
			url.append("&callbackUrl=http://118.31.229.23:8080/bosspaybill/status/FeiYouBack");
			url.append("&outOrderID="+outOrderID);
			url.append("&phone="+phone);
			url.append("&productID="+productID);
			url.append("&timeStamp="+timeStamp);
			url.append("&sign="+MD5Util.MD5Encode(sing + "", "UTF-8"));
			log.error("飞游全国移动提交信息为：" + url);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url + "";
	}
}
