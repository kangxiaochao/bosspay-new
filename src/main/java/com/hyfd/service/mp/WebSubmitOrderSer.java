package com.hyfd.service.mp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import jdk.nashorn.internal.ir.Flags;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.BaseJson;
import com.hyfd.common.ValidatePhone;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolValidator;
import com.hyfd.dao.mp.SubmitOrderDao;
import com.hyfd.service.BaseService;

@Service
public class WebSubmitOrderSer extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	SubmitOrderDao submitOrderDao;//提交订单
	@Autowired
	chargeOrderSer chargeOrderSer;
	
	
	public String webSubmitOrder(HttpServletRequest request){
		String result="";
		Map<String,Object> param = getMaps(request);
		String callbackUrl="http://baidu.com";
		String type = (String) param.get("type");
		if(type.equals("1")){//单充
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String timeStamp = sdf.format(date);
			UUID uuid = UUID.randomUUID();
			String customerOrderId = uuid.toString().replace("-", "");
			param.put("timeStamp", timeStamp);
			param.put("callbackUrl", callbackUrl);
			param.put("customerOrderId", customerOrderId);
			Map<String, Object> map = submitOrder(param);
			result = BaseJson.mapToJson(map);
		}else if(type.equals("2")){//根据传入金额进行批充
			final Map<String,Object> bill= new HashMap<String, Object>();
			String terminalName = (String) param.get("terminalName");
			String scope = (String) param.get("scope");
			String orderType = (String) param.get("orderType");//1流量2话费
			String p = (String) param.get("phoneNo");
			String[] phoneArr = p.split(",");
			String errorMessage="";
			final List<String> succList = new ArrayList<String>();
			final List<String> failList = new ArrayList<String>();
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
			for (int i = 0, size = phoneArr.length; i < size; i++) {
				final int index = i;
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						String[] split = phoneArr[index].split("-");
						String phone = split[0];//手机号
						String spec = split[1];//面值
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
						String timeStamp = sdf.format(date);
						UUID uuid = UUID.randomUUID();
						String customerOrderId = uuid.toString().replace("-", "");
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("terminalName", terminalName);
						map.put("scope", scope);
						map.put("orderType", orderType);
						map.put("callbackUrl", callbackUrl);
						map.put("phoneNo",  phone);
						map.put("spec", Math.round(new Double(spec)*100)+"");
						map.put("customerOrderId", customerOrderId);
						map.put("timeStamp", timeStamp);
						map.put("isBatch", true);
						Map<String, Object> submitOrder = submitOrder(map);
						int code = (int) submitOrder.get("code");
						if(code == 0){
							succList.add(phone);
						}else{
							String message = (String) submitOrder.get("message");
							failList.add(phone+":"+message+"\n");
						}
					}
				});
			}
			fixedThreadPool.shutdown();
			while (true) {
				if (fixedThreadPool.isTerminated()) {
					for (int i = 0; i < failList.size(); i++) {
						errorMessage += failList.get(i);
					}
					bill.put("success", succList.size());
					bill.put("error", failList.size());
					bill.put("errorMessage", errorMessage);
					result = BaseJson.mapToJson(bill);
					break;
				}
			}
		}else if(type.equals("3")){//相同面值批充
			final Map<String,Object> bill= new HashMap<String, Object>();
			String terminalName = (String) param.get("terminalName");
			String scope = (String) param.get("scope");
			String fee = param.get("fee")+"";
			String orderType = (String) param.get("orderType");//1流量2话费
			String p = (String) param.get("phoneNo");
			String[] phoneArr = p.split(",");
			String errorMessage="";
			final List<String> succList = new ArrayList<String>();
			final List<String> failList = new ArrayList<String>();
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
			for (int i = 0, size = phoneArr.length; i < size; i++) {
				final int index = i;
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						String phone = phoneArr[index]; //手机号
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
						String timeStamp = sdf.format(date);
						UUID uuid = UUID.randomUUID();
						String customerOrderId = uuid.toString().replace("-", "");
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("terminalName", terminalName);
						map.put("scope", scope);
						map.put("orderType", orderType);
						map.put("callbackUrl", callbackUrl);
						map.put("phoneNo",  phone);
						map.put("spec", Math.round(new Double(fee)*100)+"");
						map.put("customerOrderId", customerOrderId);
						map.put("timeStamp", timeStamp);
						map.put("isBatch", true);
						Map<String, Object> submitOrder = submitOrder(map);
						int code = (int) submitOrder.get("code");
						if(code == 0){
							succList.add(phone);
						}else{
							String message = (String) submitOrder.get("message");
							failList.add(phone+":"+message+"\n");
						}
					}
				});
			}
			fixedThreadPool.shutdown();
			while (true) {
				if (fixedThreadPool.isTerminated()) {
					for (int i = 0; i < failList.size(); i++) {
						errorMessage += failList.get(i);
					}
					bill.put("success", succList.size());
					bill.put("error", failList.size());
					bill.put("errorMessage", errorMessage);
					result = BaseJson.mapToJson(bill);
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 提交订单
	 * */
	public Map<String,Object> submitOrder(Map<String,Object> param){
		Map<String,Object> m = new HashMap<String, Object>();
		int code = -1;
		if(MapUtils.checkEmptyValue(param)&&checkParamIntact(param)){//验证参数
			String price = (String) param.get("spec");// 充值金额
			String phoneNo = (String) param.get("phoneNo");// 手机号
			// 验证手机号码是否允许充值(60秒内不允许重复充值)
			boolean flag = ValidatePhone.valiPhone(phoneNo + "_" +price, 60);
			if (flag) {
				code = chargeOrderSer.createOrder(param);
			} else{
				code = 13;
			}
		}else{
			code = 1;//参数不全
		}
		String message = "";
		switch(code){
			case 0:
				message = "提交成功";
				break;
			case 1:
				message = "参数不正确";
				break;
			case 2:
				message = "签名错误";
				break;
			case 3:
				message = "该代理商不可用";
				break;
			case 4:
				message = "不支持该手机号段";
				break;
			case 5:
				message = "不允许充值该运营商号段，请联系业务人员获取权限";
				break;
			case 6:
				message = "无法充值该产品";
				break;
			case 7:
				message = "余额不足";
				break;
			case 8:
				message = "订单提交出现异常";
				break;
			case 9:
				message = "折扣已变更，请联系商务";
				break;
			case 10:
	            message = "订单号重复，请勿重复提交";
	            break;
			case 11:
	            message = "获取不到代理商的扣款折扣";
	            break;
			case 13:
				message = "一分钟内不允许重复充值";
				break;
			default:
				code = -1;
				message = "状态返回值异常";
				break;
		}
		param.put("code", code);
		param.put("message", message);
		saveSubmitOrder(param);//保存订单
		m.put("code", code);
		m.put("message", message);
		return m;
	}
	
	/**
	 * 判断参数是否完整
	 * @author 
	 * @param param
	 * @return
	 */
	public boolean checkParamIntact(Map<String,Object> param){
		boolean flag = true;
		//校验参数不为空
		if(param.get("terminalName")==null||param.get("customerOrderId")==null||
				param.get("phoneNo")==null||param.get("orderType")==null
				||param.get("spec")==null||param.get("scope")==null||
				param.get("callbackUrl")==null||param.get("timeStamp")==null){
			flag = false;
		}else{
			//校验参数符合规则
			if (32 != ((String) param.get("customerOrderId")).length()
					|| (11 != ((String) param.get("phoneNo")).length() && 13 != ((String) param.get("phoneNo")).length())
					|| (!"1".equals(param.get("orderType")) && !"2".equals(param.get("orderType")))
					|| 256 < ((String) param.get("callbackUrl")).length()
					|| ((!ToolValidator.isMobile((String) param.get("phoneNo")))
							&& (!ToolValidator.isWLWMobile((String) param.get("phoneNo"))))) {
				flag = false;
			}
		}
		if(Double.parseDouble(param.get("spec")+"") == 0){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 保存下家提交的订单
	 * @author 
	 * @param param
	 */
	public void saveSubmitOrder(Map<String,Object> param){
		try{
			String agentName = (String) param.get("terminalName");//客户名
			String orderId = (String) param.get("customerOrderId");//客户订单号
			String submitParam = MapUtils.toString(param);//提交参数
			String phone = (String) param.get("phoneNo");//手机号
			String bizType = (String) param.get("orderType");//订单类型
			String submitDate = DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");//提交时间
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", UUID.randomUUID().toString().replace("-", ""));
			map.put("agentName", agentName);
			map.put("orderId", orderId);
			map.put("submitParam", submitParam);
			map.put("resultCode", param.get("code")+":"+param.get("message"));
			map.put("phone", phone);
			map.put("bizType", bizType);
			map.put("submitDate", submitDate);
			submitOrderDao.insertSelective(map);
		}catch(Exception e){
			log.error("保存提交订单出错"+ExceptionUtils.getExceptionMessage(e));
		}
	}
}
