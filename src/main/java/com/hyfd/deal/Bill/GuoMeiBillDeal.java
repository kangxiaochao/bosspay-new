package com.hyfd.deal.Bill;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 国美业务处理
 * 
 */
public class GuoMeiBillDeal implements BaseDeal
{
    private static Logger log = Logger.getLogger(GuoMeiBillDeal.class);
    
    public static Map<String, String> rltMap = new HashMap<String, String>();
    static
    {
        rltMap.put("888888", "传入的{0}参数为空");
        rltMap.put("999999", "系统执行错误");
        rltMap.put("108007", "账户余额不足");
        rltMap.put("1080187", "充值金额低于{0}元，业务被限制！");
        rltMap.put("103009", "受理单不存在");
        rltMap.put("108002", "支付流水中交易单号已存在支付流水已存在");
        rltMap.put("103125", "处理中");
        rltMap.put("108001", "失败订单, 支付流水不存在");
        rltMap.put("108002", "支付流水已存在");
        rltMap.put("000000", "成功");
        rltMap.put("102002", "用户号码资料不存在");
        rltMap.put("000001", "强停用户不能办理充值业务");
        rltMap.put("000002", "号码已销户");
    }
    
    /**
     * 国美业务处理
     * 
     * @param obj
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
        	String phoneNo = (String)order.get("phone");// 手机号
            String fee = order.get("fee") + "";// 金额，以元为单  位
            String spec = Double.parseDouble(fee) + "";// 充值金额，以分为单位
            
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String url = channel.get("link_url").toString();
            String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
//            String checkUrl = paramMap.get("checkUrl").toString();
            String secretKey = paramMap.get("secretKey").toString();
            String applyChlId = paramMap.get("applyChlId").toString();
            String cityCode = paramMap.get("cityCode").toString();// 定值
            String fromType = paramMap.get("fromType").toString();// XX—根据不同外围系统提供不同编码
            String orgType = paramMap.get("orgType").toString();// X-不同外围系统分配不同参数
            String operId = paramMap.get("operId").toString();// 操作员IDXXXXXXX-不同外围系统分配不同参数
            String payChannel = paramMap.get("payChannel").toString();// 操作渠道 X-不同外围系统分配不同参数
            String payStyle = paramMap.get("payStyle").toString();// 付款方式 X-不同外围系统分配不同参数
            String provinceCode = (String)paramMap.get("provinceCode");// 定值
            
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数 与上面的curids是否需要统一
            String curids = applyChlId + format.format(new Date()) + getSixSquece();
            map.put("orderId", curids);
            String timestamp = format1.format(new Date());// 2013-12-30 17:36:15
            String reqSerial = curids;// 充值请求流水号 订单号
            String payAmount = spec;// 单位元
            String serviceNum = phoneNo;// 服务号码
            
//            // 检查此号是否能充值
//            // 用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
//            String checkResult = sendCheck(checkUrl, secretKey, curids, serviceNum);
//            // 处理返回值
//            String checkResultCode = "000000";
//            String checkResultMessage = "";
//			if (checkResult == null || "".equals(checkResult)) {
//				checkResultCode = "000000";
//				checkResultMessage = "国美[话费充值],自动检查号码["+phoneNo+"]是否能充值返回null,做人工通过处理";
//			} else {
//				JSONObject check = JSON.parseObject(checkResult);
//				checkResultCode = check.getJSONObject("responseHeader").getString("resultCode");
//				checkResultMessage = check.getJSONObject("responseHeader").getString("resultMessage");
//			}

			// 检查可以充值
			String orderResult = sendOrder(url, secretKey, curids,timestamp, provinceCode, cityCode, fromType, orgType,
					applyChlId, operId, reqSerial, payChannel, payStyle,payAmount, serviceNum);
			// 处理返回值
			JSONObject orderResultJson = null;
			String orderResultCode = "";
			String orderResultMessage = "";
			try {
				orderResultJson = JSON.parseObject(orderResult);
				orderResultCode = orderResultJson.getJSONObject("responseHeader").getString("resultCode");
				orderResultMessage = orderResultJson.getJSONObject("responseHeader").getString("resultMessage");
				log.error(orderResultCode + ":" + orderResultMessage);
			} catch (Exception e) {
				orderResultJson = null;
			}
			if (orderResultJson == null) {
				map.put("resultCode", "解析充值返回数据发生异常");
				log.debug("号码["+phoneNo+"],国美[话费充值]请求：提交失败！103125处理中|" + phoneNo + "|" + curids + "返回数据[" + orderResult + "]");
			} else {
				String code = orderResultCode;
				map.put("resultCode", code + ":" + rltMap.get(code));
				if (!"000000".equals(code) && rltMap.containsKey(code)) {
					String submitbackmsg = code + ":" + orderResultMessage;
					submitbackmsg = StringUtil.getNewColumnValueForSql(submitbackmsg);
					if ("103125".equals(code)) {
						log.debug("号码["+phoneNo+"],国美[话费充值]请求：提交成功！103125处理中|" + phoneNo + "|" + curids);
						flag = 1;
						// 订单状态置为提交成功
					} else {
						flag = 4;
						log.debug("号码["+phoneNo+"],国美[话费充值]请求：提交失败！");
					}
				} else if ("000000".equals(code)) {
					flag = 3;
					log.debug("号码["+phoneNo+"],国美[话费充值]请求：提交成功！");
				} else {
					flag = 4;
					log.debug("号码["+phoneNo+"],国美[话费充值]请求：提交失败！");
				}
			}
		} catch (Exception e) {
			map.put("resultCode", "充值过程发生异常");
			log.error("国美[话费充值]方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
    }
    
	// 用户充值前先调用此接口确认能否进行充值操作，目前只需考虑联通转售的充值
	public static String sendCheck(String checkUrl, String secretKey,
			String serialNumber, String serviceNum) {

		// http://10.128.22.128:16810/MVNO-UIP-GOMEONLINE/orderTrade/chargeCheck?
		// serialNumber=200000120140902162250011935001
		// &timestamp=2014-09-02 16:22:50
		// &sign=812d196060596c510675d8975106d6d9
		// &data={"serviceNum":"17090181818"}

		StringBuffer paramBuffer = new StringBuffer();
		JSONObject data = new JSONObject();
		data.put("serviceNum", serviceNum);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		paramBuffer.append("serialNumber=").append(serialNumber);

		paramBuffer.append("&timestamp=").append(format.format(new Date()));
		System.out.println(data);
		String sign = MD5.ToMD5(data.toString() + secretKey);
		paramBuffer.append("&sign=").append(sign);

		paramBuffer.append("&data=").append(data);

		String queryString = "";
		log.error(paramBuffer);
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		// queryString = paramBuffer.toString();
		String url = checkUrl + "?" + queryString;
		log.error(url);
		String ret = ToolHttp.get(false, url);
		return ret;

	}

	/**
	 * @param url
	 *            链接地址
	 * @param serialNumber
	 *            业务流水号applyChlId +YYYYMMDDHH MMSS+毫秒(3) +6 位随机数
	 * @param timestamp
	 *            时间戳
	 * @param provinceCode
	 *            省份代码 请求报文头
	 * @param cityCode
	 *            地市代码 请求报文头
	 * @param fromType
	 *            来源类型 请求报文头
	 * @param orgType
	 *            组织类型 请求报文头
	 * @param applyChlId
	 *            渠道id 请求报文头
	 * @param operId
	 *            操作员ID 请求报文头
	 * @param reqSerial
	 *            充值请求流水号 缴费信息
	 * @param payChannel
	 *            操作渠道 缴费信息
	 * @param payStyle
	 *            付款方式 缴费信息
	 * @param payAmount
	 *            缴费金额 缴费信息 位是元
	 * @param serviceNum
	 *            服务号码 缴费信息
	 * @return
	 */
	public static String sendOrder(String url, String secretKey,
			String serialNumber, String timestamp, String provinceCode,
			String cityCode, String fromType, String orgType,
			String applyChlId, String operId, String reqSerial,
			String payChannel, String payStyle, String payAmount,
			String serviceNum) {
		JSONObject data = new JSONObject();

		JSONObject paymentRequest = new JSONObject();
		data.put("paymentRequest", paymentRequest);

		JSONObject requestHeader = new JSONObject();
		JSONObject paymentInfo = new JSONObject();
		paymentRequest.put("requestHeader", requestHeader);
		paymentRequest.put("paymentInfo", paymentInfo);

		requestHeader.put("provinceCode", provinceCode);
		requestHeader.put("cityCode", cityCode);
		requestHeader.put("fromType", fromType);
		requestHeader.put("orgType", orgType);
		requestHeader.put("applyChlId", applyChlId);
		requestHeader.put("operId", operId);

		paymentInfo.put("reqSerial", reqSerial);
		paymentInfo.put("payChannel", payChannel);
		paymentInfo.put("payStyle", payStyle);
		paymentInfo.put("payAmount", payAmount);
		paymentInfo.put("serviceNum", serviceNum);

		log.error("明文:" + data.toJSONString() + secretKey);
		String sign = MD5.ToMD5(data.toJSONString() + secretKey);

		JSONObject json = new JSONObject();
		json.put("serial_number", serialNumber);
		json.put("timestamp", timestamp);
		json.put("sign", sign);
		json.put("data", data);
		log.error("all:" + json.toJSONString());
		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(json.toJSONString());
			// queryString = paramBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		String ret = ToolHttp.post(false, url, queryString, null);
		return ret;

	}

	/**
	 * 获取5位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

}
