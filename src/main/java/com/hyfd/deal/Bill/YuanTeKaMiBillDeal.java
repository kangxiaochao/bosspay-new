package com.hyfd.deal.Bill;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class YuanTeKaMiBillDeal implements BaseDeal{

	static Logger log = Logger.getLogger(YuanTeKaMiBillDeal.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		int flag = -1;
		String resultCode = "";
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String phone = (String) order.get("phone");
			String orderId = "ytkm" + ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + GenerateData.getIntData(9, 7);
			map.put("orderId", orderId);
			Map<String,Object> card = (Map<String, Object>) order.get("card");
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");//获取通道参数
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String payType = paramMap.get("payType");
			String rechargeType = paramMap.get("requestType");
			String chongzhiUrl = paramMap.get("chongzhiUrl"); // 充值地址
			if(card != null){
				String cardId = (String) card.get("cardId");
				String cardPass = (String) card.get("cardPass");
				String data = ytChargeCard(chongzhiUrl,phone,cardPass,payType,rechargeType);
				if(data != null){
					boolean isSuccess = isSuccess(cardId, phone , data);
					if(isSuccess){
						flag = 3;
						card.put("useState", 1);
						card.put("orderId", order.get("id"));
						card.put("resultMsg", "充值成功");
						card.put("useTime", new Date());
						resultCode = "充值成功";
					}else{
						flag = 4;
						card.put("useState", 2);//初始化卡密状态
						card.put("orderId", order.get("id"));
						card.put("resultMsg", "充值失败");
						card.put("failCount", (int)card.get("failCount")+1);
						card.put("useTime", new Date());
						resultCode = "充值失败";
					}
				}else{//未拿到返回值，等待查询任务查询
					card.put("orderId", order.get("id"));
					card.put("resultMsg", "未收到明确状态");
					card.put("failCount", (int)card.get("failCount")+1);
					card.put("useTime", new Date());
					resultCode = "提交未收到返回值，待确认";
				}
				map.put("card", card);
			}else{
				flag = 0;
				resultCode = "未获取到卡密信息，提交失败";
			}
		}catch(Exception e){
			log.error("远特卡密充值异常"+e.getMessage()+MapUtils.toString(order));
		}
		map.put("status", flag);
		map.put("resultCode", resultCode);
		return map;
	}
	
	/**
	 * <h5>功能:</h5>卡密提交充值
	 * 
	 * @author zhangpj @date 2016年7月7日
	 * @param phoneId
	 *            充值号码
	 * @param rechargePassword
	 *            卡密
	 * @param rechargeType
	 *            充值类型
	 */
	public static String ytChargeCard(String url, String phoneNum, String cardPw, String payType, String requestType) {
		String myParamStr = "";
		myParamStr += "?amount=";
		myParamStr += "&cardNum=";
		myParamStr += "&cardPw=" + cardPw;
		myParamStr += "&payType=" + payType;
		myParamStr += "&phoneNum=" + phoneNum;
		myParamStr += "&requestType=" + requestType;

		url += myParamStr;
		String responseStr = ToolHttp.post(true, url, "", "application/x-www-form-urlencoded");
		return responseStr;
	}

	/**
	 * <h5>功能:</h5>验证充值结果是否成功
	 * 
	 * @author zhangpj @date 2016年7月8日
	 * @param result
	 * @return 成功:true;失败:false
	 */
	public static boolean isSuccess(String code, String phoneNo, String result) {
		log.info("远特卡密卡号:" + code + "充值号码:" + phoneNo + " 执行结果:" + result);
		boolean flag = true;
		try {
			JSONObject jsonObject = JSON.parseObject(result);
			flag = jsonObject.getBoolean("Status");
			if (flag == false) {
				String msg = jsonObject.getString("Msg");
				log.info("远特卡密卡号[" + code + "]为充值号码[" + phoneNo + "]冲值失败,原因["+msg+"]");
			}
		} catch (Exception e) {
			log.info("远特卡密卡号[" + code + "]为充值号码[" + phoneNo + "]冲值发生异常["+e.getMessage()+"]");
			e.printStackTrace();
		}
		return flag;
	}
	
}
