package com.hyfd.deal.Bill;

import java.io.File;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.hyfd.service.query.AiShiDeQuerySer;

public class AishideBillDeal implements BaseDeal{

	//private static String skprivateKeyFile = "c://jinanhaobai.pem";
	
	static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("000000", "成功");
		rltMap.put("S999001", "获取用户真实号码错误");
		rltMap.put("S999002", "请求参数中接口版本号不合法");
		rltMap.put("S999003", "校验请求IP地址不合法");
		rltMap.put("S999004", "请求参数中签名不合法");
		rltMap.put("S999005", "请求参数中订单不存在");
		rltMap.put("S999006", "请求参数中有不合法值");
		rltMap.put("E100006", "无优惠信息");
		rltMap.put("E130223", "取付费账户标志出错");
		rltMap.put("E130298", "取员工区域标识失败");
		rltMap.put("E200001", "工号[xxxx]没有操作[xxxx]的权限");
		rltMap.put("E230113", "重复缴费");
		rltMap.put("E700001", "invalidRoute");
		rltMap.put("E990000", "获取用户真实号码错误");
		rltMap.put("X000001", "开通号码的号段不合法");
		rltMap.put("X000002", "该号码已经注册过迅雷帐号");
		rltMap.put("X000003", "该号码没有注册过迅雷帐号");
		rltMap.put("X000004", "该号码对应的迅雷帐号本月已经开通过会员");
		rltMap.put("X000005", "请求timeStame时间错误！");
		rltMap.put("X999999", "系统错误");
		rltMap.put("990002", "不是在网用户，不能缴费");
		rltMap.put("100001", "未返档状态，不能缴费");
		rltMap.put("100003", "挂失状态，不能缴费");
		rltMap.put("100005", "黑名单状态，不能缴费");
		rltMap.put("100006", "未开通状态，不能缴费");
	}
	Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		String responseId="";
		try{
			String phone = (String) order.get("phone");//手机号
			String fee = order.get("fee")+"";//金额，以元为单位
			String payMoney = Double.parseDouble(fee)+"";//充值金额，以元为单位
			String timeStamp = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss");
			String customerOrderId = timeStamp + GenerateData.getIntData(9, 6) + "01";//32位订单号
			map.put("orderId", customerOrderId);
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			//appKey=%s&timeStamp=%s&requestId=%s&staffCode=%s&orgCode=%s&opCode=%s&accNbr=%s&payMoney=%s&payType=%s&paySign=%s&agtDate=%s&servType=%s&orgAcctId=%s&checkString=%s&sign=%s
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String appKey = paramMap.get("appKey");//商家appKey
			String staffCode = paramMap.get("staffCode");//工号
			String orgCode = paramMap.get("orgCode");//机构代码
			String opCode = paramMap.get("opCode");//操作代码
			String payType = paramMap.get("payType");//缴费类型
			String paySign = customerOrderId;//缴费流水号
			String servType = paramMap.get("servType");//号码类型
			String orgAcctId = paramMap.get("orgAcctId");//机构账户标示
			String key128bit = paramMap.get("key128bit");//checkString128位字符串
			String result = send(linkUrl, appKey, timeStamp, customerOrderId, staffCode, orgCode, opCode, phone, payMoney, payType, paySign, timeStamp, servType, orgAcctId, key128bit);
			if(result!=null&&!result.equals("")){//有返回值
				JSONObject resultJson = JSONObject.parseObject(result);
				String resCode = resultJson.getString("resCode");//返回代码
				String resMsg="";
				if(rltMap.containsKey(resCode)){
					resMsg = rltMap.get(resCode);//返回消息
				}
				map.put("resultCode", resCode+":"+resMsg);
				responseId = resultJson.getString("responseId");
				if(resCode.equals("000000")){//提交成功
					flag = 1;
				}else{
					flag = 0;
				}
			}
		}catch(Exception e){
			log.error("爱施德充值方法出错"+e+MapUtils.toString(order));
		}
		map.put("providerOrderId", responseId);
		map.put("status", flag);
		return map;
	}
	
	/**
	 * 发送充值请求
	 * @author lks 2017年1月10日上午9:25:00
	 * @param url
	 * @param appKey
	 * @param timeStamp
	 * @param requestId
	 * @param staffCode
	 * @param orgCode
	 * @param opCode
	 * @param accNbr
	 * @param payMoney
	 * @param payType
	 * @param paySign
	 * @param agtDate
	 * @param servType
	 * @param orgAcctId
	 * @param key128bit
	 * @return
	 */
	public String send(String url, String appKey, String timeStamp, String requestId, String staffCode, String orgCode, String opCode, String accNbr, String payMoney, String payType, String paySign, String agtDate, String servType, String orgAcctId,String key128bit) {
		String result = "";
		try{
			StringBuffer paramBuffer = new StringBuffer();
			paramBuffer.append("appKey=").append(appKey);
			paramBuffer.append("&timeStamp=").append(timeStamp);
			paramBuffer.append("&requestId=").append(requestId);
			paramBuffer.append("&staffCode=").append(staffCode);
			paramBuffer.append("&orgCode=").append(orgCode);
			paramBuffer.append("&opCode=").append(opCode);
			paramBuffer.append("&accNbr=").append(accNbr);
			paramBuffer.append("&payMoney=").append(payMoney);
			paramBuffer.append("&payType=").append(payType);
			paramBuffer.append("&paySign=").append(paySign);
			paramBuffer.append("&agtDate=").append(agtDate);
			paramBuffer.append("&servType=").append(servType);
			paramBuffer.append("&orgAcctId=").append(orgAcctId);
			String orgStr = paySign + accNbr + payMoney + key128bit;
			orgStr = URLEncoder.encode(orgStr, "UTF-8");
			String checkString = SHA1(orgStr);
			paramBuffer.append("&checkString=").append(checkString);
			String sign = toSign(paramBuffer.toString());
			paramBuffer.append("&sign=").append(sign);
			String queryString = URIUtil.encodeQuery(paramBuffer.toString());
			url = url + "?" + queryString;
			log.error("爱施德请求信息：" + url);
			result = HttpUtils.doGet(url);
			
			log.error("爱施德请求返回信息：" + result);
		}catch(Exception e){
			log.error("爱施德充值请求出错"+e.getMessage()+"||订单号为"+requestId);
		}
		return result;
	}
	
	public String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("爱施德签名方法出错|"+e.getMessage());
		}
		return "";
	}

	public String toSign(String orginStr) {
		String skprivateKeyFile = getRootPath();
		PrivateKey skPrivateKey = RSAUtils.fileToPrivateKey(skprivateKeyFile);
		orginStr = StringUtil.sortOrginReqStr(orginStr);// 给参数字段顺序排序,toMD5本身并没有排序
		try {
			orginStr = URLEncoder.encode(orginStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("aishide urlencoder error for sign|"+e.getMessage());
		}
		String sign = "";
		try {
			String signStr = RSAUtils.sign(MD5.ToMD5(orginStr).getBytes(), skPrivateKey);
			sign = URLEncoder.encode(signStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("aishide urlencoder error for sign|"+e.getMessage());
		}
		return sign;
	}
	
	public static String getRootPath() { 
		String classPath = AiShiDeQuerySer.class.getClassLoader().getResource("/").getPath();
        String rootPath = "";  
        //windows下  
        if("\\".equals(File.separator)){  
            //System.out.println("windows");  
        	rootPath = classPath.substring(1,classPath.indexOf("WEB-INF"));  
	        rootPath = rootPath.replace("/", "\\"); 
	        rootPath = rootPath+"pem\\jinanhaobai.pem";
        }  
        //linux下  
        if("/".equals(File.separator)){  
            //System.out.println("linux");  
        	rootPath = classPath.substring(0,classPath.indexOf("WEB-INF"));  
        	rootPath = rootPath.replace("\\", "/");
        	rootPath = rootPath+"pem/jinanhaobai.pem";
        }  
        return rootPath;  
    } 
}
