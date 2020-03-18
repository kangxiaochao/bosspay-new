package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class LianLianKeJiBillDeal implements BaseDeal{
	
	 private static Logger log = Logger.getLogger(LianLianKeJiBillDeal.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			
			String mobile = (String)order.get("phone");								//手机号
            Double amount = (double)order.get("fee");								//金额，以元为单位
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");//获取通道参数
            String linkUrl = (String)channel.get("link_url");						// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");		// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String sign_key = paramMap.get("signKey");								//加密字符串
			String agent_id = paramMap.get("agentId");								//商户ID
			String reply_url = paramMap.get("replyUrl");;							//回调地址
			String ts =  new Date().getTime()+"";									//当前时间，格式秒
			//商户订单号
			String trade_no = agent_id + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);
			map.put("orderId",trade_no);
			String sign = md5Encode(agent_id+ts+sign_key);
			JSONObject json = new JSONObject();
			json.put("agent_id",agent_id);
			json.put("ts",ts);
			json.put("sign",sign);
			json.put("trade_no",trade_no);
			json.put("amount",amount);
			json.put("mobile",mobile);
			json.put("reply_url",reply_url);
			String result = ToolHttp.post(false, linkUrl,json.toJSONString(),null);
			if(result == null || result.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "连连科技话费充值,号码[" + mobile + "],金额[" +amount+ "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			}else{
				JSONObject jsonObject = JSONObject.parseObject(result);
				String status = jsonObject.getString("error");						//返回码
				String message = jsonObject.getString("msg");						//返回码说明
				JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
				if(status.equals("0")) {
					map.put("resultCode", status+": 充值成功");						//执行结果说明
					map.put("providerOrderId",jsonData.getString("order_no"));	//返回的是上家订单号
					flag = 3;	// 充值成功
				}else {
					map.put("resultCode", status+":"+message);
					flag = 0;	// 提交异常
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("连连科技话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status",flag);													
		return map;
	}
	
	/**
	 * MD5加密 
	 * @param str
	 * @return 加密后小写
	 */
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
		return buf.toString();
	}

}
