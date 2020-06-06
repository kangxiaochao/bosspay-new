package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

public class LianLianKeJiTest2 {
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao; // 物理通道信息

	@Autowired
	OrderDao orderDao;// 订单


	@Autowired
	RabbitMqProducer mqProducer;// 消息队列生产者


	public static void main(String[] args) {
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
		    String agentId = "36";											//用户编码
			String signKey = "a875315e13040b75b808130938c3e9a6";											//秘钥
			String queryUrl = "http://manage.win10030.com/aapi/order_info";
			String timestamp = String.valueOf(new Date().getTime()/1000);  
			String ts =  Integer.valueOf(timestamp)+"";											//当前时间，格式秒
            String orderId = "362020060616304914599018";
	    		String sign = md5Encode(agentId+ts+signKey);
	    		JSONObject json = new JSONObject();
				json.put("agent_id",agentId);
				json.put("sign",sign);
				json.put("trade_no",orderId);
				json.put("ts",ts);
				String result = ToolHttp.post(false, queryUrl,json.toJSONString(),null);
				System.out.println(result);
				if(result != null && !"".equals(result)){
					JSONObject jsonObject = JSONObject.parseObject(result);
					System.out.println(jsonObject.toString());
					String status = jsonObject.getString("error");						//返回码
					JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
					String order_status = jsonData.getString("order_status");
					if(status.equals("0")) {
						if("1".equals(order_status)) {
							//充值中直接跳过查询，等待充值成功在查
//		    				continue;
						}else if("2".equals(order_status)) {
							System.out.println("充值成功");
						}else if("3".equals(order_status)) {
							System.out.println("充值失败");
						}
					}else {
					}
				}
//	        }
		} catch (Exception e) {
		}
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
