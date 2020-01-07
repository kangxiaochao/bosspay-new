package com.hyfd.deal.Bill;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolValidator;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ZhongXingShiTongBillDeal implements BaseDeal {

	Logger log = Logger.getLogger(ZhongXingShiTongBillDeal.class);

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String phoneNo = (String) order.get("phone");// 手机号
			String fee = order.get("fee") + "";// 金额，以元为单位
//			String spec = Double.parseDouble(fee) * 100 + "";// 充值金额，以分为单位
			String spec = new Double(fee).intValue() * 100 + ""; //充值金额，以分为单位,取整数,不要小数

			Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
			String linkUrl = channel.get("link_url").toString(); // 充值地址
			
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			
			String channalId = paramMap.get("ChannalId") + ""; // 中兴提供 代理商充值渠道ID
			String channalPwd = paramMap.get("ChannalPwd") + ""; // 中兴提供, 代理商充值渠道密码

			// 生成自己的id，供回调时查询数据使用
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + getThrSquece();
			map.put("orderId", curids);
			String phoneNumber = "86" + phoneNo;// 中兴号码
			Date operateDate = new Date();// 调用方操作时间
			// 得到充值后的余额，如果余额大于0，则充值成功。
			String balance = send(linkUrl, phoneNumber, spec, curids,operateDate, channalId, channalPwd);
			
			if (null != balance && !"".equals(balance)) {
				String code = "";
				balance = balance.trim();
				System.out.println("|" + balance + "|");

				if (ToolValidator.isNumber2(balance)) { // 当余额是正数或负数时，表示充值成功
					code = "1";
				}

				if ("1".equals(code)) {
					flag = 3;
					map.put("resultCode", "提交成功,充值后余额:"+balance);
				} else {
					flag = 4;
				}
			}
		} catch (Exception e) {
			log.error("中兴视通话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}

	public static int getThrSquece() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	/**
	 * 
	 * @param url
	 * @param phoneNumber
	 *            号码，格式为：国家码+号码,例如：8617057014420
	 * @param amount
	 *            充值金额（单位：分）
	 * @param sn
	 *            调用方操作流水标识（保证唯一）
	 * @param operateDate
	 *            调用方操作时间
	 * @param channalId
	 *            代理商充值渠道ID
	 * @param channalPwd
	 *            代理商充值渠道密码（MD5小写）
	 * @return 返回余额
	 */
	public String send(String url, String phoneNumber, String amount, String sn, Date operateDate, String channalId, String channalPwd) {
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);
			call.setOperationName("DealerRecharge");// WSDL里面描述的接口名称
			// 设置参数名 // 参数名 参数类型:String 参数模式：'IN' or 'OUT'
			call.addParameter("MSISDN", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("Amount", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("SN", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("OperateDate", XMLType.XSD_TIME, ParameterMode.IN);
			call.addParameter("ChannalId", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("ChannalPwd", XMLType.XSD_STRING,	ParameterMode.IN);
			// 设置返回值类型
			call.setReturnType(XMLType.XSD_STRING); // 返回值类型：String
			String md5Password = DigestUtils.md5Hex(channalPwd);
			call.invoke(new Object[] { phoneNumber, amount, sn, operateDate, channalId, md5Password });// 远程调用
			Map result = call.getOutputParams();
			log.error(url + phoneNumber + "中兴视通请求结果|" + result);

			List values = call.getOutputValues();
			log.error(values.get(1));
			return values.get(1).toString();
		} catch (ServiceException e) {
			log.error("中兴视通e1|" + e);
			e.printStackTrace();
		} catch (RemoteException e2) {
			log.error("中兴视通e2|" + e2);
			e2.printStackTrace();
		} catch (Exception e3) {
			log.error("中兴视通e3|" + e3);
			e3.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args) {
		System.out.println( new Double("50").intValue()*100 + "");
	}
}
