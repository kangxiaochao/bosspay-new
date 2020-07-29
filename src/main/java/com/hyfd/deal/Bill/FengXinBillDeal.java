package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
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

public class FengXinBillDeal  implements BaseDeal{
	 private static Logger log = Logger.getLogger(FengXinBillDeal.class);
		
		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> deal(Map<String, Object> order) {
			Map<String, Object> map = new HashMap<String, Object>();
			int flag = -1;
			try {
				
				String mobile = (String)order.get("phone");								//手机号
	            String fee = order.get("fee")+"";										//充值金额
			    fee = new Double(fee).intValue()+"";									//金额取整 单位元
	            Map<String, Object> channel = (Map<String, Object>)order.get("channel");//获取通道参数
	            String linkUrl = (String)channel.get("link_url");						// 充值地址
	            String defaultParameter = (String)channel.get("default_parameter");		// 默认参数
	            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
	            String signKey = paramMap.get("signKey");								//signKey
				String sourceSite = paramMap.get("sourceSite");							//平台编码钥
				String employeeId = paramMap.get("employeeId");							//操作员工号
				String batchType = paramMap.get("batchType");							//平台业务
				String pri = paramMap.get("pri");										//A普通工单 B加急工单  不填写默认A
				String methodId = paramMap.get("methodId");								//付款方式:210 备付金现金
				String storageId = paramMap.get("storageId");							//储值方式
				String transactionId = sourceSite + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);	//第三方流水号
				map.put("orderId",transactionId);
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
				String timestamp  = sdf2.format(new Date());							//当前时间
				//拼接请求参数
				JSONObject param = new JSONObject();
				param.put("income",fee);
				param.put("sourceSite",sourceSite);
				param.put("phoneNumber",mobile);
				param.put("pri",pri);
				param.put("methodId",methodId);
				param.put("employeeId",employeeId);
				param.put("batchType",batchType);
				param.put("storageId",storageId);
				param.put("transactionId",transactionId);
				String sign = md5Encode(param.toJSONString()+timestamp+signKey);		//加密后的字符串
				//拼接充值地址
				linkUrl += "?timestamp="+timestamp+"&sign="+sign;
				String result = ToolHttp.post(false, linkUrl,param.toJSONString(),null);
				log.info("丰信话费充值返回结果："+result);
				if(result == null || result.equals("")) {
					// 请求超时,未获取到返回数据
					flag = -1;
					String msg = "丰信话费充值,号码[" + mobile + "],金额[" +fee+ "(厘)],请求超时,未接收到返回数据";
					map.put("resultCode", msg);
				}else{
					JSONObject jsonObject = JSONObject.parseObject(result);
					log.info("丰信话费充值提交成功返回结果："+jsonObject.toString());
					String status = jsonObject.getString("resultCode");						//返回码
					String message = jsonObject.getString("resultMsg");						//返回码说明
					JSONObject jsonObject2 = JSONObject.parseObject(jsonObject.getString("result"));
					String order_no = jsonObject2.getString("bizOrderId");					//上家订单号
					map.put("providerOrderId",order_no);		
					if(status.equals("0000")) {
						flag = 3;	// 充值成功
						map.put("resultCode", status+": 充值成功");								//执行结果说明
					}else {
						flag = 4;	// 充值失败
						map.put("resultCode", status+": "+message);								//执行结果说明
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				log.error("丰信话费充值出错" + e.getMessage() + MapUtils.toString(order));
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
