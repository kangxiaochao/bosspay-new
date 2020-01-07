package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class DingXinBillDeal implements BaseDeal{

	Logger log = Logger.getLogger(getClass());
	
	static Map<String, String> rltMap = new HashMap<String, String>();
	static Map<String, String> stateMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "无错误	请求已被后台接收");
		rltMap.put("1003", "用户ID错误	充值时可失败,查询时需人工处理");
		rltMap.put("1004", "用户IP错误	充值时可失败,查询时需人工处理");
		rltMap.put("1005", "用户接口已关闭	充值时可失败,查询时需人工处理");
		rltMap.put("1006", "加密结果错误	充值时可失败,查询时需人工处理");
		rltMap.put("1007", "订单号不存在	需在该订单提交2分钟后方可处理失败");
		rltMap.put("1011", "号码归属地未知	充值时可失败,查询时需人工处理");
		rltMap.put("1013", "手机对应的商品有误或者没有上架	充值时可失败,查询时需人工处理");
		rltMap.put("1014", "无法找到手机归属地	充值时可失败,查询时需人工处理");
		rltMap.put("1015", "余额不足	充值时可失败,查询时需人工处理");
		rltMap.put("1016", "QQ号格式错误	充值时可失败");
		rltMap.put("1017", "产品未分配用户,联系商务	充值时可失败");
		rltMap.put("1018", "订单生成失败	充值时可失败");
		rltMap.put("1019", "充值号码与产品不匹配	充值时可失败");
		rltMap.put("1020", "号码运营商未知	充值时可失败");
		rltMap.put("9998", "参数有误	充值时可失败");
		rltMap.put("9999", "系统错误 人工处理");

		stateMap.put("8", "等待扣款");
		stateMap.put("0", "充值中");
		stateMap.put("1", "充值成功");
		stateMap.put("2", "充值失败");
	}
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			//获取订单中的信息
			String phone = (String) order.get("phone");
			String fee = order.get("fee")+"";
			String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS");
			String customerOrderId = timeStamp+phone+((int)(Math.random()*9000)+1000);//32位订单号
			map.put("orderId", customerOrderId);
			//获取该运营商的物理通道
			@SuppressWarnings("unchecked")
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");
			String linkUrl = (String) channel.get("link_url");//充值地址
//			String callbackUrl = (String) channel.get("callback_url");//回调地址
//			String providerMark = (String) channel.get("provider_mark");//通道标识
//			String parameterList = (String) channel.get("parameter_list");//参数列表
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String userid = paramMap.get("userid");//用户编号
			String pwd = paramMap.get("pwd");//密码
			String key = paramMap.get("key");//加密密钥
			String result = charge(/*parameterList,*/ linkUrl, userid, pwd, customerOrderId, fee, phone, "1", key);
			if(result!=null&&!result.equals("")){
				Map<String,String> resultMap = XmlUtils.readXmlToMap(result);
				String error = resultMap.get("error");//错误提示
				String state = resultMap.get("state");//订单状态
				String Porderid = resultMap.get("Porderid");
                map.put("resultCode", error + ":" + rltMap.get(error));
                map.put("providerOrderId", Porderid);
				if(error.equals("0")&&!state.equals("2")){//提交成功
					flag = 1;
				}else{
					flag = 0;
				}
			}
		}catch(Exception e){
			log.error("鼎信充值逻辑出现异常"+e+"||"+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	/**
	 * 获取充值请求的结果
	 * @author lks 2017年1月9日上午9:44:11
	 * @param format
	 * @param url
	 * @param userId
	 * @param pwd
	 * @param orderId
	 * @param face
	 * @param account
	 * @param amount
	 * @param key
	 * @return
	 * 
	 * {"userid":"15169120000","pwd":"79200C9F27B51A0D825C3FC31050D24F","chongzhiUrl":"http://api.ejiaofei.net:11140/chongzhi_jkorders.do","searchUrl":"http://api.ejiaofei.net:11140/query_jkorders.do","key":"FE90BAF5644FC9738E968F521BE14F3C"}
	 */
	public String charge(/*String format,*/String url, String userId, String pwd,
			String orderId, String face, String account, String amount,
			String key){
		String result = "";
		try{
			String userKey = "userid" + userId + "pwd" + pwd + "orderid" + orderId
					+ "face" + face + "account" + account + "amount" + amount + key;
			String md5UserKey = DigestUtils.md5Hex(userKey);
			String param = "userid=" + userId + "&pwd=" + pwd + "&orderid=" + orderId
					+ "&face=" + face + "&account=" + account + "&amount=" + amount+"&userkey="+md5UserKey;
//			param = URIUtil.encodeQuery(param);
			url = url +"?"+ param;
			System.out.println(url);
			log.info("号码[" + account + "]发起鼎信通道充值申请,请求报文是[" + url + "]");
			result = HttpUtils.doGet(url);
			log.info("号码[" + account + "]发起鼎信通道充值申请,返回报文是[" + result + "]");
		}catch(Exception e){
			log.error("号码[" + account + "]发起鼎信通道充值申请发生异常,错误信息[" + e + "]");
		}
		return result;
	}
	
}
