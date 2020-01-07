package com.hyfd.deal.Bill;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class YunPuBillDeal implements BaseDeal {

	private static Logger log = Logger.getLogger(YunPuBillDeal.class);
	/*
	 *    云普手支  业务处理       
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String mobile = order.get("phone")+"";									    //手机号码
			String chargeMoney = Math.round(Double.parseDouble(order.get("fee")+""))+"";//充值金额（元），产品为固定金额产品时以产品id为准，产品为不定金额产品时必填，以充值金额为准
			String timestamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");	//系统时间
			String outOrderId = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS")+mobile+randomNumber();	//外部订单号
			String providerId = order.get("providerId")+"";								//运营商
			map.put("orderId", outOrderId);
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");	//获取参数通道
			String url = channel.get("link_url").toString();							//接口地址		http://test.jifenfu.net:58083/flowOrder
			String defaultParameter =  channel.get("default_parameter")+"";				//用于获取默认的参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String productUrl = paramMap.get("productUrl")+"";							//产品列表的地址
			String account = paramMap.get("account")+"";								//账号
			String password = paramMap.get("password")+"";								//密码(MD5加密后的)
			String productId =productID(providerId,Integer.parseInt(chargeMoney));
			JSONObject json = new JSONObject(new TreeMap<String, Object>());
			json.put("account",URLEncoder.encode(account, "utf-8"));
			json.put("timestamp",timestamp);
			json.put("mobile",mobile);
			json.put("productId",productId);				
			json.put("chargeMoney",chargeMoney);
			json.put("outOrderId",outOrderId);
			String sign = MD5.MD5(account+password+timestamp+mobile+productId+chargeMoney+outOrderId).toLowerCase(); //小写校验码
			json.put("sign",sign);		
			String result = ToolHttp.post(false, url,json.toJSONString(),null);
			if(result == null || result.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "云普手支话费充值,号码[" + mobile + "],金额[" +chargeMoney+ "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			}else{
				JSONObject jsonObject = JSONObject.parseObject(result);
				String status = jsonObject.getString("status");					//返回码
				String message = jsonObject.getString("message");				//返回码说明
				if(status.equals("0")) {
					map.put("resultCode", status+":"+message);					//执行结果说明
					map.put("providerOrderId",jsonObject.getString("data"));	//的是上家订单号（非必须）
					flag = 1;	// 提交成功
				}else {
					map.put("resultCode", status);
					flag = 0;	// 提交异常
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("云普手支话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status",flag);														//return 返回执行的结果
		return map;
	}


	/**
	 * 根据运营商ID+充值金额拼接出产品ID
	 * @param providerId 	运营商ID
	 * @param chargeMoney	充值金额
	 * @return  产品ID
	 */
	public static String productID(String providerId,int chargeMoney) {
		if(providerId.equals("0000000001")) {		//中国移动
			return "40"+String.format("%06d",chargeMoney);		//补0
		}else if(providerId.equals("0000000002")){	//中国联通	
			return "42"+String.format("%06d",chargeMoney);
		}else if(providerId.equals("0000000003")) {	//中国电信
			return "41"+String.format("%06d",chargeMoney);
		}
		return null;
	}

	/**
	 * 生成随机有字母+数字  组成的随机4位
	 */
	public static String randomNumber() {
		String val = "";
		Random random = new Random();
		for ( int i = 0; i < 4; i++ )
		{
			String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
			if ( "char".equalsIgnoreCase( str ) )
			{ // 产生字母
				int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
				val += (char) ( nextInt + random.nextInt( 26 ) );
			}
			else if ( "num".equalsIgnoreCase( str ) )
			{ // 产生数字
				val += String.valueOf( random.nextInt( 10 ) );
			}
		}
		return val;
	}

}
