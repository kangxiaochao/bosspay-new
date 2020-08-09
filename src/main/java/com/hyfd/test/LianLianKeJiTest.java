package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class LianLianKeJiTest{
	
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			
			String serviceNum = "17006838856";												//手机号
            String fee = 10+"";																//充值金额
		    fee = new Double(fee).intValue()*1000+"";										//金额取整 单位厘
		    JSONObject responseJson = new JSONObject();										//请求提交参数 json格式
		    responseJson.put("serviceNum",serviceNum);
		    responseJson.put("amount",fee);
		    System.out.println(responseJson);
            String linkUrl = "http://115.238.34.45:8015/sun-api/api/v1/recharge";			// 充值地址
            String appKey = "QuOyq0AK";														//key
            String requestType = "RECHARGE";
			String appSecret = "1MoquQs1aEO8s6Ws";											//秘钥
		    String timeStamp = ToolDateTime.format(new Date(),"yyyyMMddHHmmss");			//时间戳，格式yyyyMMddHHmmss（年月日时分秒）
		    String transactionId = appKey + timeStamp +(RandomUtils.nextInt(9999999) + 10000000);
			map.put("orderId",transactionId);
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("appKey",appKey);
			parameterMap.put("requestMsg",responseJson.toJSONString());
			parameterMap.put("requestType",requestType);
			parameterMap.put("timeStamp",timeStamp);
			parameterMap.put("transactionId",transactionId);
			SortedMap<Object, Object> params = new TreeMap<Object, Object>(parameterMap);
			System.out.println(params.toString());
			String sign = createSign(params,appSecret);
			System.out.println(sign);
			JSONObject json = new JSONObject();										//请求提交参数 json格式
			json.put("appKey",appKey);
			json.put("requestMsg",responseJson);
			json.put("requestType",requestType);
			json.put("sign",sign);
			json.put("timeStamp",timeStamp);
			json.put("transactionId",transactionId);
			System.out.println(json.toJSONString());
			String result = ToolHttp.post(false, linkUrl,json.toJSONString(),null);
			System.out.println(result);
			if(result == null || result.equals("")) {
				// 请求超时,未获取到返回数据
				flag = -1;
			}else{		
				JSONObject jsonObject = JSONObject.parseObject(result);
				System.out.println(jsonObject.toString());
				String status = jsonObject.getString("code");						//返回码
				String message = jsonObject.getString("msg");						//返回码说明
				JSONObject responseMsgJson = JSONObject.parseObject(jsonObject.getString("responseMsg"));
				map.put("providerOrderId",responseMsgJson.getString("rechargeId"));	
				map.put("resultCode", status+": "+message);						//执行结果说明
				if(status.equals("0")) {
					flag = 1;	// 提交成功
				}else {
					flag = 4;	// 充值失败
				}				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		map.put("status",flag);						
		System.out.println(map.toString());
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
	public static String md5Encode(String str){
		StringBuffer buf = new StringBuffer();
		try{
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
		}catch(Exception ex){	
		}
		return buf.toString();
	}
}
