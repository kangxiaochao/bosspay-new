package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;


/**
 * 千米话费充值
 * @author zhangjun
 *
 */
public class XiangjiaofeiBillDeal implements BaseDeal{
	private static Logger log = Logger.getLogger(XiangjiaofeiBillDeal.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();

	static{
		rltMap.put("0", "正在处理");
		rltMap.put("1", "充值成功");
		rltMap.put("2", "充值失败");
		rltMap.put("3", "订单不存在,请5 分钟后核实后再做退款处理");
		rltMap.put("FAIL|0001", "不存在此代理");
		rltMap.put("FAIL|0002", "数据验证失败");
		rltMap.put("FAIL|0003", "电话号码异常");
		rltMap.put("FAIL|0004", "金额异常");
		rltMap.put("FAIL|0005", "缴费模式异常");
		rltMap.put("FAIL|006", "订单重复,请通过查询接口确认订单状态再做处理");
		rltMap.put("FAIL|007", "暂不支持此类型号码充值");
		rltMap.put("FAIL|008", "余额不足");
		rltMap.put("FAIL|009", "暂时不能受理该种类型的号码,请与客服联系");
		rltMap.put("FAIL|010", "未获取产品价格");
		rltMap.put("FAIL|011", "未知错误");
		rltMap.put("FAIL|012|转换订单时间出错", "转换订单时间出错,SubTime格式错误");
		rltMap.put("FAIL|013|订单超时不予处理", "订单提交时间与系统时间相差超过10分钟,订单超时不予处理");
		rltMap.put("FAIL|014", "非接口用户");
		rltMap.put("FAIL|015|提交 IP ", "非绑定 IP 提交");
		rltMap.put("FAIL|00X", "非配置异常,请发起查询订单请求确认提交结果");
		rltMap.put("FAIL|00X|参数不完整", "系统无法识别对应参数,请仔细检查各参数名是否正确,");
		rltMap.put("M0000", "内部错误");
		rltMap.put("M1000", "参数不完整");
		rltMap.put("M1001", "签名不正确");
		rltMap.put("M1002", "IP未授权");
		rltMap.put("M1003", "指定商户不存在");
		rltMap.put("M1010", "电话号码不正确");
		rltMap.put("M1011", "运营商错误");
		rltMap.put("M1020", "单号不存在");
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;

		try {
			String phone = (String) order.get("phone");//手机号
			Double fee = new Double(order.get("fee")+"");//金额，以元为单位
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String linkUrl = (String) channel.get("link_url");//充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String agentID = paramMap.get("agentID"); // 代理商编号,由系统分配给代理
			String key = paramMap.get("key"); // 用户密钥
			//String getChargeUrl = paramMap.get("getChargeUrl"); // 充值地址
			String queryUrl = paramMap.get("queryUrl"); // 查询地址
			String chargeMode = paramMap.get("chargeMode"); //缴费模式 0-快充 1-慢充
			String callBackUrl = paramMap.get("callBackUrl"); // 回调地址
			String spec =String.valueOf(fee.intValue());
			// 将我方平台电话号码类型转换为享缴费充值接口电话号码类型
			String telephoneType = "";//convertTelephoneType(carrierstype);
			//0-移动 1-联通 2-电信 3-移动固话 4-联通固话 5-电信固话 1-虚商
			String providerId = order.get("providerId")+"";
			if("0000000001".equals(providerId)){
				telephoneType="0";
			}else if ("0000000002".equals(providerId)) {
				telephoneType="1";
			}else if("0000000003".equals(providerId)){
				telephoneType="2";
				
			}else{
				telephoneType="1";
			}
			// 生成自己的id，供回调时查询数据使用(格式:yyyyMMddHHmmssSSS+电话号码+4位随机字符=32位长度)
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getCharAndNumr(4);// 交易流水号，每笔交易均不一样
			map.put("orderId", curids);
			
			// 1.发起话费充值
			String data = getCharge(agentID, key, linkUrl, phone, telephoneType, spec, chargeMode, curids, null, callBackUrl);
			
			if (null == data) {
				log.error("享缴费话费充值请求超时"+MapUtils.toString(order));
				map.put("resultCode", "请求超时");
			} else {
				String[] backDataArray = data.split("\\|");
				if (backDataArray[0].equalsIgnoreCase("SUCCESS")) {
					String providerOrderId = backDataArray[1];
					map.put("providerOrderId", providerOrderId);
					map.put("resultCode", backDataArray[0]);
					flag = 1;
				}else if (backDataArray[0].equalsIgnoreCase("FAIL") && "006".equals(backDataArray[1])) {
					// 2.2.如果提交失败返回FAIL|006,不能直接定义为失败，需通过查询接口确认订单状态再做处理
					// 根据我方平台订单编号发起话费订单查询
					String queryData = query(agentID, key, queryUrl, null, curids);
					if(null != queryData){
						backDataArray = queryData.split("\\|");
						if (backDataArray[0].equalsIgnoreCase("SUCCESS")) {
							String state = backDataArray[1];
							map.put("resultCode", state + ":" + rltMap.get(state));
							// 查询成功
							flag = 1;
						}else {
							map.put("resultCode", data + ":" + rltMap.get(data));
							// 提交失败
							flag = 0;
						}
					}
				}else {
					map.put("resultCode", data + ":" + rltMap.get(data));
					//提交失败
					flag = 0;
				}
			}
		} catch (Exception e) {
			log.error("享缴费话费充值逻辑出错"+e+MapUtils.toString(order));
			map.put("resultCode", "充值异常,请联系技术人员查看");
		}
		map.put("status", flag);
		return map;
	}
	/**
	 * <h5>功能描述:</h5>	将我方平台电话号码类型转换为享缴费充值接口电话号码类型
	 *
	 * @param carrierstype 我方平台电话号码类型
	 * @return 享缴费充值接口电话号码类型  0-移动 1-联通 2-电信 3-移动固话 4-联通固话 5-电信固话 1-虚商
	 *
	 * @作者：zhangjun		@创建时间：2017年2月18日
	 */
	private static String convertTelephoneType(String carrierstype){
		String telephoneType = "";
		int key = Integer.valueOf(carrierstype);
		switch (key) {
		case 1:
			telephoneType = "0";
			break;
		case 2:
			telephoneType = "1";
			break;
		case 3:
			telephoneType = "2";
			break;
		case 4:
			telephoneType = "1";
			break;
		case 5:
			telephoneType = "1";
			break;
		case 6:
			telephoneType = "1";
			break;
		}
		return telephoneType;
	}
	
	/**
	 * @功能描述：	发起话费充值
	 *
	 * @param agentID 代理商编号,由系统分配给代理
	 * @param key 用户密钥
	 * @param getChargeUrl 充值地址
	 * @param telephone 要充值的电话号码
	 * @param telephoneType 电话类型 0-移动 1-联通 2-电信 3-移动固话 4-联通固话 5-电信固话 1-虚商
	 * @param money 充值金额(小于 4 位的整数)例：10
	 * @param chargeMode 缴费模式 0-快充 1-慢充
	 * @param requestID 充值订单号码,由代理商负责生成该订单号,且保证每笔唯一,重复则不允许充值
	 * @param extendInfo 扩展信息,扩展信息，通知时将原样返回.（不包含中文）
	 * @param callBackUrl 后台通知地址,注意回调主要已 Get方式进行,所以提交的地址不允许带有Get参数
	 * @return
	 * 
	 * @作者：zhangjun		@创建时间：2017年2月16日
	 */
	public static String getCharge(String agentID, String key, String getChargeUrl,
			String telephone, String telephoneType, String money,String chargeMode,
			String requestID, String extendInfo,	String callBackUrl) {

		Map<String, String> linkedHashMap = new LinkedHashMap<String, String>();
		linkedHashMap.put("AgentID", agentID);
		linkedHashMap.put("Telephone", telephone);
		linkedHashMap.put("TelephoneType", telephoneType);
		linkedHashMap.put("Money", money);
		linkedHashMap.put("ChargeMode", chargeMode);
		linkedHashMap.put("RequestID", requestID);
		linkedHashMap.put("ExtendInfo", extendInfo);
		linkedHashMap.put("CallBackUrl", callBackUrl);
		linkedHashMap.put("SubTime", DateUtils.getNowTime());
		
		//1.生成充值请求参数信息
		String param = getParamStr(linkedHashMap);

		//2.签名信息 ,列表中的参数值按照签名顺序拼接所产生的字符串与用户密钥一起进行 MD5 签名	验证,结果大写
		String hmac = md5(param+"&Key="+key);
		param += "&Hmac="+hmac;
		
		String data = ToolHttps.post(false, getChargeUrl, param, "application/x-www-form-urlencoded");
		log.error("享缴费发起话费充值请求(getChargeTest)返回信息["+data+"]");
		return data;
	}
	
	/**
	 * @功能描述：	生成充值请求参数信息
	 *
	 * @作者：zhangjun	@创建时间：2017年2月16日
	 * @param paramMap
	 * @return
	 */
	private static String getParamStr(Map<String, String> paramMap){
		StringBuffer sbf = new StringBuffer();
		for (String key : paramMap.keySet()) {
			sbf.append(key + "=" + paramMap.get(key) + "&");
		}
		//deleteCharAt(int index)是去除掉指定位置的字符,这里是去除掉最后一个位置的字符&,因为下标是从0开始,所以最后一个位置要长度-1
		String paramStr = sbf.deleteCharAt(sbf.length()-1).toString();
		return paramStr;
	}
	/**
	 * @功能描述：	发起话费订单查询
	 *
	 * @param agentID 代理商编号,由系统分配给代理
	 * @param key 用户密钥
	 * @param queryUrl 话费订单查询地址
	 * @param orderID 系统订单号,由系统生成的充值订单号码（可以为空,不为空时使用此字段查询）
	 * @param requestID 代理商订单号,代理商系统订单号,查询时优先使用(可以为空,不为空且OrderID为空时,使用此字段查询)
	 * @return
	 *
	 * @作者：zhangjun		@创建时间：2017年2月16日
	 */
	public static String query(String agentID, String key, String queryUrl, String orderID, String requestID) {
		
		Map<String, String> linkedHashMap = new LinkedHashMap<String, String>();
		linkedHashMap.put("AgentID", agentID);
		linkedHashMap.put("OrderID", orderID);
		linkedHashMap.put("RequestID", requestID);
		
		//1.生成充值请求参数信息
		String param = getParamStr(linkedHashMap);
		//2.签名信息 ,列表中的参数值按照签名顺序拼接所产生的字符串与用户密钥一起进行 MD5 签名验证,结果大写
		String hmac = md5(param+"&Key="+key);
		queryUrl += "?"+param + "&Hmac="+hmac;
		String data = ToolHttps.get(false, queryUrl);
		return data;
	}
	
    /**
     * @功能描述：	MD5加密,结果大写
     *
     * @作者：zhangjun		@创建时间：2017年2月16日
     * @param str 要被加密的字符串 
     * @return
     */
    private static String md5(String str) {
        try {
            byte[] btInput = str.getBytes("UTF-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return Hex.encodeHexString(md).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }
}
