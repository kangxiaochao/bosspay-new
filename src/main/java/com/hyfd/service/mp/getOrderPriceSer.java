package com.hyfd.service.mp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.common.utils.OrderSimulateUtil;
import com.hyfd.common.utils.SignatureUtil;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.sys.SysUserDao;
import com.hyfd.service.BaseService;
import com.rabbitmq.client.AMQP.Basic.Return;

@Service
public class getOrderPriceSer extends BaseService {

	Logger log = Logger.getLogger(getClass());

	@Autowired
	AgentDao agentDao;// 代理商

	@Autowired
	private PhoneSectionSer phoneSectionSer;
	
	@Autowired
	ProviderDao providerDao;
	
	@Autowired
	PhoneSectionDao phoneSectionDao;
	
	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;
	
	@Autowired
	SysUserDao sysUserDao;
	/**
	 * 查询订单状态
	 * 
	 * @author zhangjun 2018年01月09日87
	 * @param request
	 * @return
	 */
	public String getOrderPriceByNumber(HttpServletRequest request) {
		Double billSize = 0.0;
		JSONObject json = new JSONObject();
		
		// 获取提交参数
		Map<String, Object> map = getMaps(request);
		Map<String, String> paramMap = new HashMap<String, String>();
		Iterator<String> iter = map.keySet().iterator();

		while (iter.hasNext()) {
		    String key = iter.next();
		    String value = map.get(key).toString();
		    paramMap.put(key, value);
		}
		
		// 提交参数正确性校验
		boolean paramFlag = true;
		if (StringUtils.isEmpty(paramMap.get("terminalName"))
				|| StringUtils.isEmpty(paramMap.get("phoneNo"))
				|| (11 != paramMap.get("phoneNo").length() && 13 != paramMap
						.get("phoneNo").length())
				|| StringUtils.isEmpty(paramMap.get("orderType"))
				|| (!"1".equals(paramMap.get("orderType")) && !"2"
						.equals(paramMap.get("orderType")))) {
			paramFlag = false;
		} else if ("2".equals(paramMap.get("orderType"))) { // 如果是话费(单位:元)
			if (StringUtils.isEmpty(paramMap.get("billPrice"))) {
				paramFlag = false;
			} else {
				try {
					String billPrice = paramMap.get("billPrice");
					billSize = Double.parseDouble(billPrice);
					if (billSize <= 0) {
						paramFlag = false;
					}
				} catch (Exception e) {
					paramFlag = false;
				}
			}
		}
		// 校验传入参数
		if (!paramFlag) {
			json.put("code", "2");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
		}

		// 获取签名
		String signature = paramMap.get("signature");
		// 删除签名
		paramMap.remove("signature");
		// 签名验证合法性校验
		boolean verifyFlag = true;
		try {
			verifyFlag = SignatureUtil.verify(paramMap, signature);
		} catch (Exception e) {
			// 异常情况下，默认校验不通过，跑出异常，记录日志
			verifyFlag = false;
			e.printStackTrace();
		}

		if (!verifyFlag) {
			json.put("code", "3");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
			
		}

		// 获取参数
		String username = paramMap.get("terminalName");
		String mobilenumber = paramMap.get("phoneNo");
		String orderType = paramMap.get("orderType");
		String jsontext = "";

		// 根据手机号，获取当前手机类型；获取当前终端对应话费的折扣
		jsontext = getAppTBillPriceByNumber(username,mobilenumber, billSize);
		//System.err.println("jsontext=" + jsontext);
		return jsontext;
	}

	/**
	 * app根据手机号，获取当前手机类型；获取当前终端对应话费的折扣
	 */
	public String getAppTBillPriceByNumber(String username,String mobilenumber, double billSize) {

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(mobilenumber)
				|| billSize <= 0
				|| (mobilenumber.length() != 11 && mobilenumber.length() != 13)) {
			// 参数不完整
			return "{\"code\":\"-1\", \"msg\":\"参数不完整，请检查手机号或充值金额是否正确！\"}";
		}
		String returnStr = sectionAndBillPkgGet1(mobilenumber, username, billSize);
		return returnStr;
	}

	/**
	 * 根据手机号查询号段、流量包信息
	 * */
	public String sectionAndBillPkgGet(String phone,String terminalName) {
		String str = null;
		Map<String, Object> returnMessage = new HashMap<String, Object>(); 
		try {
			Map<String, Object> m = new HashMap<String, Object>();
			//String phone = (String) m.get("phone");
			m.put("phone", phone);
			String providerName ="";
			// 获取手机号相关信息
			String section = (phone.length() == 13) ? phone.substring(0, 5): phone.substring(0, 7);// 获取号段
			
			
			Map<String, Object> sectionMessage = phoneSectionDao
					.selectBySection(section);
			if (sectionMessage != null) {
				String providerId = (String) sectionMessage.get("provider_id");
				String provinceCode = (String) sectionMessage.get("province_code");
				String cityCode = (String) sectionMessage.get("city_code");
				returnMessage.put("province", provinceCode+"-"+cityCode);
				providerName= providerDao.getNameById(providerId);
			
				Map<String, Object> agent = sysUserDao.agentGetByUserName(terminalName);
				String agentId = (String) agent.get("id");
				m.put("agentId", agentId);
				//String agentName = (String) agent.get("name");
				//sectionMessage.put("agentName", agentName);
				returnMessage.put("mobiletype", providerName);
				m.put("providerId", providerId);
				
				List<Map<String, Object>> pkgIdList = agentBillDiscountDao.getAgentBillDiscountPkgList(m);
				
				List<Map<String, Object>> pkgList = new ArrayList<Map<String,Object>>();
				
				//判断活动期间每天的100个名额是否还有，存在则充值金额减0.5元，不存在则不减
				Map<String, Object> activityMap = agentBillDiscountDao.selectActivity();
				int places = Integer.parseInt(activityMap.get("places")+"");							 //当前剩余活动人数/天
				double discountAmount = Double.parseDouble(activityMap.get("discount_amount")+"");		 //优惠金额
				String activityAgent = activityMap.get("activity_agent")+"";							 //参与活动的用户
				String activityProvider = activityMap.get("activity_provider")+"";						 //参与多动的运营商
				String [] activityProviderStr = activityProvider.split(",");							 //拆分出产于活动的运营商
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
				SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");	
				Date activity_end_time = formatter.parse(activityMap.get("activity_end_time")+""); 		 //活动结束时间
				Date activity_static_time = formatter.parse(activityMap.get("activity_static_time")+""); //活动开始时间
				Date d1 = new Date();																	 //当前时间，用来判断是否在活动时间范围内
				Date current_time = formatter2.parse(activityMap.get("current_time")+"");				 //当天日期   yyyy-MM-dd
				Date d2 = formatter2.parse(formatter2.format(d1));					
				boolean providerStatus = false;
				//验证该号码是否为参与活动的运营商号码
				for (int i = 0; i < activityProviderStr.length; i++) {
					if(providerId.equals(activityProviderStr[i])) {
						providerStatus = true;
					}
				}
				//新的一天活动人数还原
				if(d1.after(activity_static_time) && d1.before(activity_end_time)){
					if(current_time.before(d2)) {
						Map<String, Object> map = new HashMap<>();
						map.put("places",activityMap.get("number")+"");
						map.put("current_time",formatter2.format(d1));
						agentBillDiscountDao.updataActivityPlaces(map);
					}
				}
				//只有3网的手机号参与话费充值活动
				
				for (int i = 0; i <pkgIdList.size(); i++) {
					Map<String, Object> pkgMessage = new HashMap<String, Object>();  
					Map<String, Object> map = pkgIdList.get(i);
					String pkgId = (String) map.get("id");
					String price = map.get("value")+"";
					String pkgName  = map.get("name")+"";
					if(price.indexOf(".") > 0){
					     //正则表达
					           price = price.replaceAll("0+?$", "");//去掉后面无用的零
					           price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
					}
					String discount= agentBillDiscountGet(agentId, providerId, pkgId,provinceCode);
					if(!StringUtils.isEmpty(discount)){
						if(pkgName.equals("任意充")){
							pkgMessage.put("discount", discount);	//任意充传的是折扣    非任意充传的是折扣后的价格
						}else {
							double disprice1 = (Double.parseDouble(price))*(Double.parseDouble(discount));
							//验证该号码是否为参与活动的运营商号码
							if(providerStatus) {
								//判断活动期间每天的100个名额是否还有，存在则充值金额减0.5元，不存在则不减
								//places=100, activity_end_time=2020-03-03 13:38:19.0, activity_static_time=2020-03-03 13:38:19.0, activityAgent=newweixin, discountAmount=1.0
								if(terminalName.equals(activityAgent) &&  places > 0 && d1.after(activity_static_time) && d1.before(activity_end_time)){
									if(price.equals("50")) {
										disprice1 = disprice1 - 1;
									}else if(price.equals("100")){
										disprice1 = disprice1 - 2;
									}
//									disprice1 = disprice1 - discountAmount;
								}
							}
							String disprice =String.format("%.2f", disprice1);
							if(disprice.indexOf(".") > 0){
							     //正则表达
							           disprice = disprice.replaceAll("0+?$", "");//去掉后面无用的零
							           disprice = disprice.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
							     }
							pkgMessage.put("discount", disprice);
						}
						pkgMessage.put("price",price);
						pkgMessage.put("pkgName",pkgName);
					}else {
						return "{\"code\":\"-1\", \"msg\":\"不能对当前手机号进行话费充值，无此终端用户折扣，请联系管理员！\"}";
					}
					pkgList.add(pkgMessage);
				}
				returnMessage.put("code", "0");
				returnMessage.put("msg", "sucess");
				returnMessage.put("bill", pkgList);
				returnMessage.put("flow", "");
			}else {
				returnMessage.put("code", "1");
				returnMessage.put("msg", "不支持该手机号段，不能对当前手机号进行话费充值！");
			}
			str = BaseJson.mapToJson(returnMessage);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}
	
	/**
	 * 根据手机号查询号段、流量包信息
	 * */
	public String sectionAndBillPkgGet1(String phone, String terminalName, double billprice) {
		String str = null;
		Map<String, Object> returnMessage = new HashMap<String, Object>();
		try {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("phone", phone);
			String providerName = "";
			// 获取手机号相关信息
			String section = (phone.length() == 13) ? phone.substring(0, 5) : phone.substring(0, 7);// 获取号段

			Map<String, Object> sectionMessage = phoneSectionDao.selectBySection(section);
			if (sectionMessage != null) {
				String providerId = (String) sectionMessage.get("provider_id");
				String provinceCode = (String) sectionMessage.get("province_code");
				String cityCode = (String) sectionMessage.get("city_code");
				// returnMessage.put("province", provinceCode+"-"+cityCode);
				providerName = providerDao.getNameById(providerId);

				Map<String, Object> agent = sysUserDao.agentGetByUserName(terminalName);
				String agentId = (String) agent.get("id");
				m.put("agentId", agentId);
				m.put("providerId", providerId);

				List<Map<String, Object>> pkgIdList = agentBillDiscountDao.getAgentBillDiscountPkgList(m);

				String disprice = "";
				boolean rycFlag = false;
				String rycPkgId = "";
				
				//判断活动期间每天的100个名额是否还有，存在则充值金额减0.5元，不存在则不减
				Map<String, Object> activityMap = agentBillDiscountDao.selectActivity();
				int places = Integer.parseInt(activityMap.get("places")+"");							 //活动人数/天
				double discountAmount = Double.parseDouble(activityMap.get("discount_amount")+"");		 //优惠金额
				String activityAgent = activityMap.get("activity_agent")+"";							 //参与活动的用户
				String activityProvider = activityMap.get("activity_provider")+"";						 //参与多动的运营商
				String [] activityProviderStr = activityProvider.split(",");							 //拆分出产于活动的运营商
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");				
				Date activity_end_time = formatter.parse(activityMap.get("activity_end_time")+""); 		 //活动结束时间
				Date activity_static_time = formatter.parse(activityMap.get("activity_static_time")+""); //活动开始时间
				Date d1 = new Date();	
				boolean providerStatus = false;
				//验证该号码是否为参与活动的运营商号码
				for (int i = 0; i < activityProviderStr.length; i++) {
					if(providerId.equals(activityProviderStr[i])) {
						providerStatus = true;
					}
				}
				for (int i = 0; i < pkgIdList.size(); i++) {
					Map<String, Object> map = pkgIdList.get(i);
					String pkgId = (String) map.get("id");
					String pkgName = map.get("name") + "";
					double price = 0.0;
					if(!"任意充".equals(pkgName)){
						price = Double.parseDouble(map.get("value") + "");
					}
//					if (price.indexOf(".") > 0) {
//						// 正则表达
//						price = price.replaceAll("0+?$", "");// 去掉后面无用的零
//						price = price.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
//					}
					if (billprice==price) {
						String discount = agentBillDiscountGet(agentId, providerId, pkgId, provinceCode);
						if (!StringUtils.isEmpty(discount)) {
							double disprice1 = (price) * (Double.parseDouble(discount));
							
							//添加判断活动是否结束、是否有名额，如有就将折扣减去优惠金额，如无结束
							if(terminalName.equals(activityAgent) &&  places > 0 && d1.after(activity_static_time) && d1.before(activity_end_time)){
//								disprice1 = disprice1 - discountAmount;
								if(billprice == 50) {
									disprice1 = disprice1 - 1;
									//活动名额减去1
									places = places - 1;				
									agentBillDiscountDao.updateActivity(""+places);
								}else if(billprice == 100){
									disprice1 = disprice1 - 2;
									//活动名额减去1
									places = places - 1;				
									agentBillDiscountDao.updateActivity(""+places);
								}
							}
							disprice = String.format("%.2f", disprice1);
							if (disprice.indexOf(".") > 0) {
								// 正则表达
								disprice = disprice.replaceAll("0+?$", "");// 去掉后面无用的零
								disprice = disprice.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
							}
						} else {
							return "{\"code\":\"-1\", \"msg\":\"不能对当前手机号进行话费充值，无此终端用户折扣，请联系管理员！\"}";
						}
					}
					if ("任意充".equals(pkgName)) {
						rycFlag = true;
						rycPkgId = pkgId;
					}
				}
				if (rycFlag && !"".equals(rycPkgId) && "".equals(disprice)) {
					String discount = agentBillDiscountGet(agentId, providerId, rycPkgId, provinceCode);
					if (!StringUtils.isEmpty(discount)) {
						double disprice1 = (billprice) * (Double.parseDouble(discount));
						disprice = String.format("%.2f", disprice1);
						if (disprice.indexOf(".") > 0) {
							// 正则表达
							disprice = disprice.replaceAll("0+?$", "");// 去掉后面无用的零
							disprice = disprice.replaceAll("[.]$", "");// 如小数点后面全是零则去掉小数点
						}
					} else {
						return "{\"code\":\"-1\", \"msg\":\"不能对当前手机号进行话费充值，无此终端用户折扣，请联系管理员！\"}";
					}
				}
				returnMessage.put("code", "0");
				returnMessage.put("msg", provinceCode + "-" + providerName);
				returnMessage.put("disprice", disprice);
				returnMessage.put("carrierstype", "1");
			} else {
				returnMessage.put("code", "-1");
				returnMessage.put("msg", "不支持该手机号段，不能对当前手机号进行话费充值！");
			}
			str = BaseJson.mapToJson(returnMessage);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}
	

	/**
	 * 根据包、代理商、运营商查询折扣
	 * */
	public String agentBillDiscountGet(String agentId,String providerId,String pkgId,String provinceCode){
		String str="false";
		try {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("agentId", agentId);
			m.put("providerId", providerId);
			m.put("billPkgId", pkgId);
			m.put("provinceCode", provinceCode);
			Map<String, Object> agentBillDiscount = agentBillDiscountDao.agentBillDiscountGet(m);
			if(agentBillDiscount!=null){
				str= agentBillDiscount.get("discount")+"";
			}			
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
	}
	
	
	/**
	 * 提交订单
	 * 
	 * @author zhangjun 2018年01月09日
	 * @param request
	 * @return
	 */
	public String getOrderPriceByPhone(HttpServletRequest request) {
		int billSize = 0;
		JSONObject json = new JSONObject();
		// 获取提交参数
		Map<String, Object> map = getMaps(request);
		Map<String, String> paramMap = new HashMap<String, String>();
		Iterator<String> iter = map.keySet().iterator();
		
		while (iter.hasNext()) {
			String key = iter.next();
			String value = map.get(key).toString();
			paramMap.put(key, value);
		}
		// 提交参数正确性校验
		if (StringUtils.isEmpty(paramMap.get("terminalName"))
				|| StringUtils.isEmpty(paramMap.get("phoneNo"))
				|| 11 != paramMap.get("phoneNo").length()) {
			// 校验传入参数
			json.put("code", "2");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
		}
		// 获取签名
		String signature = paramMap.get("signature");
		// 删除签名
		paramMap.remove("signature");

		// 签名验证合法性校验
		boolean verifyFlag = true;
		try {
			verifyFlag = SignatureUtil.verify(paramMap, signature);
		} catch (Exception e) {
			// 异常情况下，默认校验不通过，跑出异常，记录日志
			verifyFlag = false;
			e.printStackTrace();
		}

		if (!verifyFlag) {
			json.put("code", "3");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
		}

		//校验是否为苏宁代理商专属手机号，此号码不允许在公众号上充值。
		String suningnumber = paramMap.get("phoneNo");
		Integer integer = phoneSectionDao.selectSectionbyunique(suningnumber);
		if (integer.equals(1)){
			json.put("code", "4");
			json.put("msg", "此号码为专属手机号，暂不支持充值");
			return json.toJSONString();
		}


		// 获取参数
		String username = paramMap.get("terminalName");
		String mobilenumber = paramMap.get("phoneNo");
		
		String returnMessage = sectionAndBillPkgGet(mobilenumber, username);
		
		return returnMessage;
	}
	
	/**
	 * 提交订单
	 * 
	 * @author zhangjun 2018年01月09日
	 * @param request
	 * @return
	 */
	public String getPhoneInfo(HttpServletRequest request) {
		int billSize = 0;
		JSONObject json = new JSONObject();
		// 获取提交参数
		Map<String, Object> map = getMaps(request);
		Map<String, String> paramMap = new HashMap<String, String>();
		Iterator<String> iter = map.keySet().iterator();
		
		while (iter.hasNext()) {
			String key = iter.next();
			String value = map.get(key).toString();
			paramMap.put(key, value);
		}
		// 提交参数正确性校验
		if (StringUtils.isEmpty(paramMap.get("terminalName"))
				|| StringUtils.isEmpty(paramMap.get("phoneNo"))
				|| 11 != paramMap.get("phoneNo").length()) {
			// 校验传入参数
			json.put("code", "2");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
		}
		// 获取签名
		String signature = paramMap.get("signature");
		// 删除签名
		paramMap.remove("signature");

		// 签名验证合法性校验
		boolean verifyFlag = true;
		try {
			String agentName = (String) paramMap.get("terminalName");// 客户名
			Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
			String publicKeyStr = (String) agent.get("app_key");// 获取该客户的公钥
			verifyFlag = OrderSimulateUtil.verify(paramMap, signature,publicKeyStr);
		} catch (Exception e) {
			// 异常情况下，默认校验不通过，跑出异常，记录日志
			verifyFlag = false;
			e.printStackTrace();
		}

		if (!verifyFlag) {
			json.put("code", "3");
			json.put("message", "提交参数错误，请检查提交参数信息是否正确");
			return json.toJSONString();
		}

		// 获取参数
		String username = paramMap.get("terminalName");
		String mobilenumber = paramMap.get("phoneNo");
		
		String returnMessage = sectionAndBillPkgGet(mobilenumber, username);
		
		return returnMessage;
	}

}
