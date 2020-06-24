package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
//		    String agentId = "36";											//用户编码
//			String signKey = "a875315e13040b75b808130938c3e9a6";			//秘钥
			String serviceNum = "17006838856";								//手机号
			
			String fee = 10.0+"";										//充值金额
		    fee = new Double(fee).intValue()*1000+"";								//金额取整
			String app_key = "QuOyq0AK";				
			String app_secret = "1MoquQs1aEO8s6Ws";
			String queryUrl = "http://115.238.34.45:8002/jupiter-agent/api/recharge";
			String time_stamp = String.valueOf(new Date().getTime()/1000);  
			String ts =  Integer.valueOf(time_stamp)+"";					//当前时间，格式秒
			
			Map<String, Object> map2 = new HashMap<String, Object>();
//			map2.put("serviceNum",serviceNum);
//			map2.put("amount",amount);
			map2.put("app_key",app_key);
//			map2.put("app_secret",app_secret);
			map2.put("time_stamp",ts);
			SortedMap<Object, Object> params = new TreeMap<Object, Object>(map2);
			String sign  = createSign(params,app_secret);
			String str = queryUrl+"?app_key="+app_key+"&time_stamp="+ts+"&sign="+sign;
			JSONObject json = new JSONObject();
			json.put("serviceNum",serviceNum);
			json.put("amount",fee);
			String result = ToolHttp.post(false, str,json.toJSONString(),null);
			System.out.println(result);
			JSONObject jsonObject = JSONObject.parseObject(result);
			String status = jsonObject.getString("code");						//返回码
			String message = jsonObject.getString("msg");						//返回码说明
			JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("data"));
			if(status.equals("0")) {
				map.put("resultCode", status+": 充值成功");						//执行结果说明
				map.put("providerOrderId",jsonData.getString("order_no"));	//返回的是上家订单号
				System.out.println("充值成功了");
			}else {
				map.put("resultCode", status+":"+message);
				System.out.println("充值失败了");
			}
			System.out.println(json.toJSONString());
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拼接参数并加密
	 * @param params
	 * @param APP_SECRET
	 * @return MD5加密后的字符串
	 */
	public static String createSign(SortedMap<Object, Object> params, String APP_SECRET) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<Object, Object>> es = params.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + APP_SECRET);
        String sign = md5Encode(sb.toString());
        return sign;
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
