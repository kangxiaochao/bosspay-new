package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;
import com.hyfd.service.query.AiShiDeQuerySer;
import com.hyfd.service.query.DiXinTongQuerySer;
import com.hyfd.service.query.FenXiangQuerySer;
import com.hyfd.service.query.HaiHangQuerySer;
import com.hyfd.service.query.HuaJiQuerySer;
import com.hyfd.service.query.LanMaoQuerySer;
import com.hyfd.service.query.TianYinQuerySer;
import com.hyfd.service.query.YongYouQuerySer;
import com.hyfd.service.query.YuanTeQuerySer;

@Service
public class QueryPhoneBalanceSer extends BaseService
{
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    PhoneSectionDao phoneSectionDao;
    
    @Autowired
	ProviderDao providerDao;
    
    @Autowired
	HuaJiQuerySer huaJiQuerySer;
    
    @Autowired
	YuanTeQuerySer yuanTeQuerySer;
    
    @Autowired
	DiXinTongQuerySer diXinTongQuerySer;
    
    @Autowired
	HaiHangQuerySer haiHangQuerySer;
    
    @Autowired
	FenXiangQuerySer fenXiangQuerySer;
    
    @Autowired
	TianYinQuerySer tianYinQuerySer;
    
    @Autowired
	AiShiDeQuerySer aiShiDeQuerySer;
    
    @Autowired
	LanMaoQuerySer lanMaoQuerySer;
    
    @Autowired
    YongYouQuerySer yongYouQuerySer;
    
    public Map<String, String> queryPhoneBalance(HttpServletRequest request){
    	
    	Map<String, String> phoneExtInfo = new HashMap<String, String>();
    	Map<String, String> result = new HashMap<String, String>();
		// 金额单位修复 使用元
		String unKnownName = "未知";
		String myUnit = "元";
		try {
			Map<String, Object> paramMap = getMaps(request);
	        String phone = paramMap.get("phone")+"";
	        //获取手机号码所属运营商
	        String providerName = phoneSection(phone);
	        if(providerName.contains("话机")){
	        	result = huaJiQuerySer.getChargeAmountInfo(phone);
	        }else if (providerName.contains("远特")) {
	        	result = yuanTeQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("迪信通")) {
				result = diXinTongQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("海航")) {
				result = haiHangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("分享")) {
				result = fenXiangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("天音")) {
				result = tianYinQuerySer.getChargeAmountInfo(phone);
//			}else if (providerName.contains("爱施德")) {
//				result = aiShiDeQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("蓝猫")) {
				result = lanMaoQuerySer.getChargeAmountInfo(phone);
			}else if(providerName.contains("用友")){
				result = yongYouQuerySer.getChargeAmountInfo(phone);
			}else {
				result.put("status", "1");
				result.put("amount", "0");
			}
	        phoneExtInfo.put("mobilenumber", phone);
	        if ("0".equals(result.get("status"))) {// 查询成功
				phoneExtInfo.put("amount", result.get("amount") + myUnit);
				phoneExtInfo.put("phoneownername",
						result.get("phoneownername"));
			} else {
				phoneExtInfo.put("phoneownername", unKnownName);
				phoneExtInfo.put("amount", "获取"+providerName+"余额信息失败");
			}
	        
		} catch (Exception e) {
			// TODO: handle exception
			phoneExtInfo.put("phoneownername", unKnownName);
			phoneExtInfo.put("amount", "未查询到余额信息");
			log.error(e.getMessage());
		}
        return phoneExtInfo;
        
    }
    
public Map<String, String> queryPhoneBalanceCharge(String phone,String providerName){
    	
    	Map<String, String> phoneExtInfo = new HashMap<String, String>();
    	Map<String, String> result = new HashMap<String, String>();
		// 金额单位修复 使用元
		String unKnownName = "未知";
		String myUnit = "元";
		try {
	        
	        if(providerName.contains("话机")){
	        	result = huaJiQuerySer.getChargeAmountInfo(phone);
	        }else if (providerName.contains("远特")) {
	        	result = yuanTeQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("迪信通")) {
				result = diXinTongQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("海航")) {
				result = haiHangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("分享")) {
				result = fenXiangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("天音")) {
				result = tianYinQuerySer.getChargeAmountInfo(phone);
//			}else if (providerName.contains("爱施德")) {
//				result = aiShiDeQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("蓝猫")) {
				result = lanMaoQuerySer.getChargeAmountInfo(phone);
			}else if(providerName.contains("用友")){
				result = yongYouQuerySer.getChargeAmountInfo(phone);
			}else{
				result.put("status", "1");
				result.put("amount", "0");
			}
	        
	        if ("0".equals(result.get("status"))) {// 查询成功
				phoneExtInfo.put("amount", result.get("amount") + myUnit);
				phoneExtInfo.put("phoneownername",
						result.get("phoneownername"));
			} else {
				phoneExtInfo.put("phoneownername", unKnownName);
				phoneExtInfo.put("amount", "获取"+providerName+"余额信息失败");
			}
	        
		} catch (Exception e) {
			// TODO: handle exception
			phoneExtInfo.put("phoneownername", unKnownName);
			phoneExtInfo.put("amount", "未查询到余额信息");
			log.error(e.getMessage());
		}
        return phoneExtInfo;
        
    }
    
	/**
	 * 获取号码归属运营商
	 * @param phone
	 * @return
	 * @author zhangjun
	 * 2017-12-18
	 */
    public String phoneSection(String phone){
    	
    	String providerName = "";
    	String section = (phone.length() == 13) ? phone.substring(0, 5)
				: phone.substring(0, 7);// 获取号段
		Map<String, Object> sectionMessage = phoneSectionDao
				.selectBySection(section);
		if (sectionMessage != null) {
			String providerId = (String) sectionMessage.get("provider_id");
			providerName= providerDao.getNameById(providerId);
		}
    	return providerName;
    }
}
