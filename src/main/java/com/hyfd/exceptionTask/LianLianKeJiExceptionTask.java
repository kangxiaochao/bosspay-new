package com.hyfd.exceptionTask;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

/**
 * 连连科技查询
 * @author Administrator
 */
@Component
public class LianLianKeJiExceptionTask implements BaseTask{

	private static Logger log = Logger.getLogger(LianLianKeJiExceptionTask.class);

	@Override
	public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
			String defaultParameter = channelMap.get("default_parameter")+"";					//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("appKey");												//key
            String requestType = paramMap.get("requestType_query");								//RECHARGE：充值，QUERY：查询，BUSINESS:业务
			String appSecret = paramMap.get("appSecret");										//秘钥
			String timeStamp = ToolDateTime.format(new Date(),"yyyyMMddHHmmss");				//时间戳，格式yyyyMMddHHmmss（年月日时分秒）
			String transactionId = "";															//订单号ID查询时为空
			String queryUrl = paramMap.get("queryUrl");											//查询地址

			String upids = order.get("providerOrderId") + "";
			String orderId = order.get("orderId") + "";
			map.put("orderId", orderId);
			map.put("providerOrderId", upids);
			map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
			map.put("phone",order.get("phone"));
			map.put("fee",order.get("fee"));

			JSONObject responseJson = new JSONObject();											//请求提交参数 json格式
			responseJson.put("rechargeId",upids);
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("appKey",appKey);
			parameterMap.put("requestMsg",responseJson.toJSONString());
			parameterMap.put("requestType",requestType);
			parameterMap.put("timeStamp",timeStamp);
			parameterMap.put("transactionId",transactionId);
			SortedMap<Object, Object> params = new TreeMap<Object, Object>(parameterMap);
			String sign = createSign(params,appSecret);
			JSONObject json = new JSONObject();
			json.put("appKey",appKey);
			json.put("requestMsg",responseJson);
			json.put("requestType",requestType);
			json.put("sign",sign);
			json.put("timeStamp",timeStamp);
			json.put("transactionId",transactionId);
			String result = ToolHttp.post(false,queryUrl,json.toJSONString(),null);
			log.debug("连连科技异常订单查询返回结果成功：" + result);
			if(result != null && !"".equals(result)){
				JSONObject jsonObject = JSONObject.parseObject(result);
				String status = jsonObject.getString("code");						//返回码
				if(status.equals("0")) {
					JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("responseMsg"));
					String order_status = jsonData.getString("status");
					if("0".equals(order_status)) {
						//充值中直接跳过查询，等待充值成功在查
						map.put("status", 2);
						map.put("resultCode", "充值中！" );
					}else if("1".equals(order_status)) {
						map.put("status", 1);	// 充值成功
						map.put("resultCode", "充值成功！" );
					}else if("2".equals(order_status)) {
						map.put("status", 0);	//充值失败
						map.put("resultCode", "充值失败！" );
					}else{
						map.put("status",3);	// 查询订单状态失败
						map.put("resultCode", "未知的订单状态，请与上家核实后处理！");
					}
				}else {
					map.put("status", 3);
					map.put("resultCode", "查询提交异常，请稍后再试！");
				}
			}else{
				map.put("status", 3);
				map.put("resultCode", "查询接口返回信息为空！");
			}
		} catch (Exception e) {
			// TODO: handle exceptionW
			log.error("连连科技Task出错" + e);
		}
		return map;
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
            Entry entry = it.next();
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
