package com.hyfd.service.mp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;
import com.hyfd.service.query.FenXiangQuerySer;
import com.hyfd.service.query.YongYouQuerySer;

@Service
public class BillChargeSer extends BaseService {

	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private PhoneSectionSer phoneSectionSer;
	@Autowired
	BillPkgDao billPkgDao;
	@Autowired
	ProviderDao providerDao;
	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;
	@Autowired
	PhoneSectionDao phoneSectionDao;
	
	/**
	 * 检测输入手机号的信息
	 * */
	public String batchCheckPhone(HttpServletRequest req) {
		String str = "";
		try {
			Map<String, Object> map = getMaps(req);
			String numberMeg = (String) map.get("numberMeg");
			Map<String, Object> agent = agentGetByUserId();
			if (null != agent) {
				String agentName = (String) agent.get("name");
				String agentId = (String) agent.get("id");
				String billType = (String) map.get("billType");
				String[] mobileMeg = numberMeg.split(" |\\+|\\r\\n|,|。|;|\\n");
				int count = mobileMeg.length;// 前台传来手机号的个数
				Map<String, Object> checkPhoneMessage = phoneSectionSer.checkPhoneMessage(mobileMeg);// 检测前台传入的手机号
				List<String> phoneMessage = (List<String>) checkPhoneMessage.get("phoneMessage");// 有效的手机号信息
				Set<String> faceValue = (Set<String>) checkPhoneMessage.get("faceValue");
				int valid = phoneMessage.size();// 有效的手机号个数
				int inValid = count - valid;// 无效的手机号个数
//				Map<String, Object> distributionByBill = phoneSectionSer.distributionByBill(phoneMessage);//号码信息分类，放入各自运营商下
				Map<String, Object> result = new HashMap<String, Object>();
				Map<String, Object> bill = new HashMap<String, Object>();
//				for (Map.Entry<String, Object> entry : distributionByBill.entrySet()) {
//					String providerId = entry.getKey();// 运营商Id
//					String providerName = providerDao.getNameById(providerId);
//					List<String> value = (List<String>) entry.getValue();
//					String res = getTotal(value, billType, providerId, agentId);
//					if(res!=""){
//						bill.put(providerName, res);
//					}
//				}
				result.put("phoneMeg", bill);
				result.put("phone", phoneMessage);
				result.put("agentName", agentName);
				result.put("count", count);
				result.put("valid", valid);
				result.put("inValid", inValid);
				result.put("code", "1");
				str = BaseJson.mapToJson(result);
			}else{
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("code", "99");
				result.put("msg", "当前用户不是代理商!");
				str = BaseJson.mapToJson(result);
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}
	
	/**
	 * 检测同面值批充输入手机号的信息
	 * */
	public String batchCheckPhoneTwo(HttpServletRequest req) {
		String str = "";
		try {
			Map<String, Object> map = getMaps(req);
			String numberMeg = (String) map.get("numberMeg");
			Map<String, Object> agent = agentGetByUserId();
			if (null != agent) {
				String agentName = (String) agent.get("name");
				String agentId = (String) agent.get("id");
				String billType = (String) map.get("billType");
				String[] mobileMeg = numberMeg.split(" |\\+|\\r\\n|,|。|;|\\n");
				int count = mobileMeg.length;// 前台传来手机号的个数
				Map<String, Object> checkPhoneMessage = phoneSectionSer.checkPhoneMessageTwo(mobileMeg);// 检测前台传入的手机号
				List<String> phoneMessage = (List<String>) checkPhoneMessage.get("phoneMessage");// 有效的手机号信息
				int valid = phoneMessage.size();// 有效的手机号个数
				int inValid = count - valid;// 无效的手机号个数
				Map<String, Object> result = new HashMap<String, Object>();
				Map<String, Object> bill = new HashMap<String, Object>();
				result.put("phoneMeg", bill);
				result.put("phone", phoneMessage);
				result.put("agentName", agentName);
				result.put("count", count);
				result.put("valid", valid);
				result.put("inValid", inValid);
				result.put("code", "1");
				str = BaseJson.mapToJson(result);
			}else{
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("code", "99");
				result.put("msg", "当前用户不是代理商!");
				str = BaseJson.mapToJson(result);
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	public String getTotal(List<String> val, String billType, String providerId,String agentId) {
		String result="";
		try {
			Map<String, Integer> m = new HashMap<String, Integer>();// 生成卡包和卡包对应的面值
			List<String> valueList = new ArrayList<String>();
			double total = 0.0;
			for (String phoneMeg : val) {
				String[] meg = phoneMeg.split("-");
				String value = meg[1];
				valueList.add(value);
				double price = checkPhone(phoneMeg, billType, providerId, agentId);
				total += price;
			}
			if(total!=0.0){
				for (String i : valueList) {
				    if (m.get(i) == null) {
				        m.put(i, 1);
				    } else {
				        m.put(i, m.get(i) + 1);
				    }
				}
				for(Entry<String, Integer> entry : m.entrySet()){
					String value = entry.getKey();
					Integer count = entry.getValue();
					String meg="充值<font color=\"red\">"+value+"元</font>的手机号有<font color=\"red\">"+count+"个</font>  ";
					result=result+meg;
				}
				result=result+"  总价为<font color=\"red\">"+total+"元</font>";
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return result;
	}

	/**
	 * 获取号码折扣
	 * */
	public double checkPhone(String phoneMeg, String billType,String providerId, String agentId) {
		double discountPrice = 0.0;
		try {
			String[] split = phoneMeg.split("-");
			String phone = split[0];
			String value = split[1];
			String provinceCode = split[2];
			if ("2".equals(billType)) {
				provinceCode = "全国";
			}
			Map<String, Object> bill = new HashMap<String, Object>();
			bill.put("providerId", providerId);
			bill.put("value", value);
			bill.put("billType", billType);
			bill.put("provinceCode", provinceCode);
			Map<String, Object> billPkg = billPkgDao.getBillPkgByProIdAndPrice(bill);
			if (null != billPkg) {
				String billPkgId = (String) billPkg.get("id");
				BigDecimal billPrice = (BigDecimal) billPkg.get("price");
				double pkgPrice = billPrice.doubleValue();
				if (billPkgId != null && billPkgId != "") {
					Map<String, Object> p = new HashMap<String, Object>();
					p.put("agentId", agentId);
					p.put("providerId", providerId);
					p.put("billPkgId", billPkgId);
					p.put("provinceCode", provinceCode);
					Map<String, Object> agentBillDiscount = agentBillDiscountDao
							.agentBillDiscountGet(p);
					if (agentBillDiscount != null) {
						String discount = agentBillDiscount.get("discount")
								+ "";
						double dis = Double.parseDouble(discount);
						BigDecimal b1 = new BigDecimal(pkgPrice);
						BigDecimal b2 = new BigDecimal(dis);
						discountPrice = new Double(b1.multiply(b2)
								.doubleValue());
					}
				}
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return discountPrice;
	}
	
	/**
	 * 根据手机号查询号段、流量包信息
	 * */
	public String sectionAndBillPkgGet(HttpServletRequest req) {
		String str = null;
		try {
			Map<String, Object> m = getMaps(req);
			String phone = (String) m.get("phone");
			String providerName ="";
			// 获取手机号相关信息
			String section = (phone.length() == 13) ? phone.substring(0, 5)
					: phone.substring(0, 7);// 获取号段
			Map<String, Object> sectionMessage = phoneSectionDao
					.selectBySection(section);
			if (sectionMessage != null) {
				String providerId = (String) sectionMessage.get("provider_id");
				providerName= providerDao.getNameById(providerId);
			}
			Map<String, String> map = phoneSectionSer.queryPhoneBalanceCharge(phone,providerName);
			if (sectionMessage != null) {
				Map<String, Object> agent = agentGetByUserId();
				sectionMessage.put("phoneName", map.get("phoneownername"));
				sectionMessage.put("phoneAmount", map.get("amount"));
				String agentId = (String) agent.get("id");
				m.put("agentId", agentId);
				String agentName = (String) agent.get("name");
				sectionMessage.put("agentName", agentName);
				String providerId = (String) sectionMessage.get("provider_id");
				sectionMessage.put("providerName", providerName);
				m.put("providerId", providerId);
				
				List<Map<String, Object>> pkgIdList = agentBillDiscountDao.getAgentBillDiscountPkgList(m);
				sectionMessage.put("billPkgMessage", pkgIdList);
			}
			str = BaseJson.mapToJson(sectionMessage);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 获取手机号归属地
	 * */
	public String getPhoneAddress(HttpServletRequest req) {
		String str = "";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> map = getMaps(req);
			String mobileNumber = (String) map.get("mobileNumber");
			if(null == mobileNumber || "".equals(mobileNumber.trim())){
				resultMap.put("state", 0);
				resultMap.put("msg", "手机号码不能为空，请确认");
			}else if (mobileNumber.trim().length() != 11 && mobileNumber.trim().length() != 13) {
				resultMap.put("state", 0);
				resultMap.put("msg", "手机号码位数不正确,请重新输入");
			}else {
				// 获取手机号相关信息
				String section = (mobileNumber.length() == 13) ? mobileNumber.substring(0, 5)
						: mobileNumber.substring(0, 7);// 获取号段
				Map<String, Object> sectionMessage = phoneSectionDao.selectBySection(section);
				if (sectionMessage != null) {
					String providerId = (String) sectionMessage.get("provider_id");
					String providerName= providerDao.getNameById(providerId);
					Map<String, Object> provider = new HashMap<String, Object>();
					provider.put("province", sectionMessage.get("province_code"));
					provider.put("carrierstype", sectionMessage.get("provider_type"));
					provider.put("mobiletype", providerName);
					resultMap.put("state", 1);
					resultMap.put("msg", provider);
				}else {
					resultMap.put("state", 0);
					resultMap.put("msg", "无该号码信息");
				}
			}		
		} catch (Exception e) {
			getMyLog(e, log);
		}
		str = BaseJson.mapToJson(resultMap);
		return str;
	}
	
	/**
	 * @功能描述：	批量获取手机号码信息,地区、姓名、余额等
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月17日
	 */
	public String batchQueryPhoneInfo(HttpServletRequest req){
		List<Map<String, Object>> phoneList = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> m = getMaps(req);
		String phones = (String)m.get("phoneNo");
		String[] phoneArray = phones.split(",");
		int phoneArrayLength = phoneArray.length;
		
		String p = (phoneArray[0].length() == 13) ? phoneArray[0].substring(0, 5) : phoneArray[0].substring(0, 7);// 获取号段
		Map<String, Object> sectionMaps = phoneSectionDao.selectBySectionExt(p);
		
		// 实现方式一
		if(sectionMaps.get("providerName").equals("分享") || sectionMaps.get("providerName").equals("用友")){
			String phoneNo = "";
			String section = "";
			for (int i = 0; i < phoneArrayLength; i++) {
				Map<String, Object> map = new HashMap<String,Object>();
				phoneNo = phoneArray[i];
				// 获取手机号相关信息
				section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
				Map<String, Object> sectionMap = phoneSectionDao.selectBySectionExt(section);
				Map<String, String> phoneInfoMap = phoneSectionSer.queryPhoneBalanceCharge(phoneNo,String.valueOf(sectionMap.get("providerName")));
				map.putAll(sectionMap);
				map.put("phoneNo", phoneNo);
				map.put("phoneName", phoneInfoMap.get("phoneownername"));
				map.put("phoneAmount", phoneInfoMap.get("amount"));
				
				phoneList.add(map);
			}

		}else if(sectionMaps.get("providerName").equals("用友")){
			final List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
			for (int i = 0, size = phoneArrayLength; i < size; i++) {
				String phoneNo = phoneArray[i];
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						Map<String, Object> map = new HashMap<String,Object>();
						// 获取手机号相关信息
						String section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
						Map<String, Object> sectionMap = phoneSectionDao.selectBySectionExt(section);
						Map<String, String> phoneInfoMap = phoneSectionSer.queryPhoneBalanceCharge(phoneNo,String.valueOf(sectionMap.get("providerName")));
						map.putAll(sectionMap);
						map.put("phoneNo", phoneNo);
						map.put("phoneName", phoneInfoMap.get("phoneownername"));
						map.put("phoneAmount", phoneInfoMap.get("amount"));
						
						list.add(map);
					}
				});
			}
			return JSONObject.toJSON(list).toString();
			/*fixedThreadPool.shutdown();
			while (true) {
				if (fixedThreadPool.isTerminated()) {
					
					}	
					break;
				}*/
		}
//		else{
//
//		}
//		else if(sectionMaps.get("providerName").equals("用友")){
//			final List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
//			for (int i = 0, size = phoneArrayLength; i < size; i++) {
//				String phoneNo = phoneArray[i];
//				fixedThreadPool.execute(new Runnable() {
//					public void run() {
//						Map<String, Object> map = new HashMap<String,Object>();
//						// 获取手机号相关信息
//						String section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
//						Map<String, Object> sectionMap = phoneSectionDao.selectBySectionExt(section);
//						Map<String, String> phoneInfoMap = phoneSectionSer.queryPhoneBalanceCharge(phoneNo,String.valueOf(sectionMap.get("providerName")));
//						map.putAll(sectionMap);
//						map.put("phoneNo", phoneNo);
//						map.put("phoneName", phoneInfoMap.get("phoneownername"));
//						map.put("phoneAmount", phoneInfoMap.get("amount"));
//						
//						list.add(map);
//					}
//				});
//			}
//			fixedThreadPool.shutdown();
//			while (true) {
//				if (fixedThreadPool.isTerminated()) {
//					return JSONObject.toJSON(list).toString();
//					}	
//					break;
//				}
//		}
		else{

			// 实现方式二
			// 获取服务器cpu数量
//			int processorsCount = Runtime.getRuntime().availableProcessors();
//			ExecutorService exeService = Executors.newFixedThreadPool(processorsCount * 2);
			ExecutorService exeService = Executors.newFixedThreadPool(10);
			List<Future<Map<String, Object>>> futureList  = new ArrayList<Future<Map<String,Object>>>();
			
			try {
				System.out.println(phoneArray);
				for (int i = 0; i < phoneArrayLength; i++) {
					String phoneNo = phoneArray[i];
					Future<Map<String, Object>> futureSubmit = exeService.submit(() -> getPhoneInfo(phoneNo));
					futureList.add(futureSubmit);
				}
				for (Future<Map<String, Object>> future : futureList) {
					Map<String, Object> sectionMap = future.get();
					phoneList.add(sectionMap);
				}
			} catch (InterruptedException | ExecutionException e) {
				for (Future<Map<String, Object>> future : futureList) {
					future.cancel(true);
				}
				e.printStackTrace();
			} finally {
				exeService.shutdown();
			}
		}
		return JSONObject.toJSON(phoneList).toString();
	}
	
	// =============================批量查询余额====================================
	
	public String batchCheckPhoneByQuery(HttpServletRequest req) {
		String str = "";
		try {
			Map<String, Object> map = getMaps(req);
			String numberMeg = (String) map.get("numberMeg");
			String[] mobileMeg = numberMeg.replace("\n", "").split(",");
			int count = mobileMeg.length;// 前台传来手机号的个数
			Map<String, Object> checkPhoneMessage = phoneSectionSer.checkPhone(mobileMeg);// 检测前台传入的手机号
			List<String> phoneMessage = (List<String>) checkPhoneMessage.get("phoneMessage");// 有效的手机号信息
			int valid = phoneMessage.size();// 有效的手机号个数
			int inValid = count - valid;// 无效的手机号个数
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("phone", phoneMessage);
			result.put("count", count);
			result.put("valid", valid);
			result.put("inValid", inValid);
			result.put("code", "1");
			str = BaseJson.mapToJson(result);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}
	
	/**
	 * @功能描述：	根据手机号获取手机号信息,地区、姓名、余额等
	 *
	 * @param phoneNo
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月17日
	 */
	private Map<String, Object> getPhoneInfo (String phoneNo){
		Map<String,Object> map = new HashMap<String,Object>();
		String section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
		
		Map<String, Object> sectionMap = phoneSectionDao.selectBySectionExt(section);
		Map<String, String> phoneInfoMap = phoneSectionSer.queryPhoneBalanceCharge(phoneNo,String.valueOf(sectionMap.get("providerName")));
		
		map.putAll(sectionMap);
		map.put("phoneNo", phoneNo);
		map.put("phoneName", phoneInfoMap.get("phoneownername"));
		map.put("phoneAmount", phoneInfoMap.get("amount"));
		System.out.println(sectionMap);
		return map;
	}
}
