package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 星博海话费充值处理
 * @author Administrator
 */
public class XingBoHaiDeal implements BaseDeal{

	private static Logger log = Logger.getLogger(QuanGuokongChongBillDeal.class);

	public static Map<String, String> rltMap = new HashMap<String, String>();
	static {
		rltMap.put("1", "操作成功");
		rltMap.put("-100", "账户不存在 agentAccount 字段错误");
		rltMap.put("-101", "sign 字段错误 密钥不对或者是 MD5 值错误");
		rltMap.put("-102", "账户被暂停使用 账户已经被冻结不能使用");
		rltMap.put("-103", "无 API 接口权限 ShtVer 字段错误");
		rltMap.put("-201", "定单号不存在 查询的 Orderid 无效");
		rltMap.put("-200", "定单号不能为空 查询定单时未提交定单号");
		rltMap.put("-301", "IP 错误 超出绑定的 IP 地址范围");
		rltMap.put("-302", "账户没有交易权限 账户交易权限被关闭");
		rltMap.put("-303", "充值号码为空 没有提交 chargeAcct");
		rltMap.put("-304", "定单号为空 没有提交 orderId");
		rltMap.put("-305", "账户余额不足 预存款不足本次定单交易");
		rltMap.put("-310", "定单号重复 提交的定单号已经存在了，本次提交无效");
		rltMap.put("-990", "指令错误 action 字段不正确");
		rltMap.put("-991", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-992", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-993", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-994", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-995", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-996", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-997", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-998", "未知错误 暂未明确定义的错误 ");
		rltMap.put("-999", "报文错误 未提交 busiBody");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		
		try {
			String chargeAcct = (String) order.get("phone");// 手机号
			
			String fee = order.get("fee") + "";// 金额，上家产品编码
			String chargeCash = fee.substring(0,fee.indexOf("."));//充值金额取消后缀
			
			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			
			String link_url = (String) channel.get("link_url");//充值连接
			
			String defaultParameter = (String) channel.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			// 生成自己的id，供回调时查询数据使用
			String orderId = DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmssSSS") + ((int)(Math.random()*9000)+1000);//18位订单号
			String agentAccount = paramMap.get("agentAccount");// 商户账号
			String chargeType = paramMap.get("chargeType");// 充值类型
			String action = "CZ";//充值交易指令码
			String appkey = paramMap.get("appkey");//密钥
			String retUrl = paramMap.get("retUrl"); //回调地址
			String params = jointUrl(action, orderId, chargeAcct, chargeCash, chargeType, agentAccount,appkey,retUrl);
			log.error("星博海请求信息：" + params);
			String result = HttpUtils.doPost(link_url, params);
			if (!StringUtil.empty(result)) {
				JSONObject resultJson = JSONObject.parseObject(result);
				
				String ReCode = resultJson.getString("errorCode");
				
				map.put("resultCode", ReCode + ":" + rltMap.get(ReCode));
				if ("1".equals(ReCode)) {
					flag = 1;
					map.put("providerOrderId", resultJson.getString("TaskID"));
				} else{
					flag = 0;
				}
				log.error("星博海充值提交完成，返回信息为" + result);
			} else {// 未拿到返回数据
				map.put("resultCode", "未拿到返回数据");
			}
			map.put("orderId", orderId);
		} catch (Exception e) {
			log.error("星博海充值方法出错" + e + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	/**
	 * 
	 * @param action 交易指令码
	 * @param orderId 订单号
	 * @param chargeAcct 手机号
	 * @param chargeCash 充值金额
	 * @param chargeType 充值类型
	 * @param agentAccount 商户名称
	 * @return
	 */
	public String jointUrl(String action,String orderId,String chargeAcct,String chargeCash,String chargeType,String agentAccount,String appkey,String retUrl) {
		
		StringBuffer suBuffer = new StringBuffer();
		suBuffer.append("{\"action\":\"" + action + "\",");
		suBuffer.append("\"orderId\":\"" + orderId + "\",");
		suBuffer.append("\"chargeAcct\":\"" + chargeAcct + "\",");
		suBuffer.append("\"chargeCash\":" + chargeCash + ",");
		suBuffer.append("\"chargeType\":" + chargeType + ",");
//		suBuffer.append("\"retUrl\":\"http%3A%2F%2F114.55.26.121%3A9001%2Fbosspaybill%2Fstatus%2FxingBoHaiBack\"}");
		suBuffer.append("\"retUrl\":"+retUrl+"\"}"); 
		String sign = DigestUtils.md5Hex(suBuffer + appkey);
		
		StringBuffer params = new StringBuffer();
		params.append("{\"sign\":\"" + sign + "\",");
		params.append("\"agentAccount\":\"" + agentAccount + "\",");
		params.append("\"busiBody\":" + suBuffer + "}");
		
		return params.toString();
	}

}
