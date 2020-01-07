package com.hyfd.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hyfd.common.utils.DateUtils;

public class ValidatePhone {
	
	// 验证充值号码一定时间段内是否重复充值
	private static Map<String,String> phoneMap = new ConcurrentHashMap<String, String>();

	/**
	 * @功能描述：	验证手机号码是否允许充值
	 *
	 * @param phoneNo
	 * @param intervalTime 最小间隔时间(秒)
	 * @return 验证通过返回true
	 *
	 * @作者：zhangpj		@创建时间：2018年4月4日
	 */
	public static boolean valiPhone(String phoneInfo,int intervalTime){
		boolean flag = false;
		
		// 清理过期的手机号码信息
		clearOverduePhoneInfo(intervalTime);
		
		if (null == phoneMap.get(phoneInfo)) {
			String time = DateUtils.getNowTime();
			if (null == time) {
				System.out.println(phoneInfo +" time = "+ time);
			}
			phoneMap.put(phoneInfo, time);
			flag = true;
		}
		return flag;
	}
	
	// ================ private Method ================
	
	/**
	 * @功能描述：	清理过期的手机号码信息
	 *
	 * @param intervalTime 默认最小间隔时间(秒)
	 *
	 * @作者：zhangpj		@创建时间：2018年4月4日
	 */
	private static void clearOverduePhoneInfo(int intervalTime){
		// 当前时间
		String nowTime = DateUtils.getNowTime();
		// 同步清理,方式多线程操作时发生异常
		synchronized (phoneMap) {
			for (String key : phoneMap.keySet()) {
				int second = DateUtils.getDistanceSecond(nowTime, phoneMap.get(key));
				
				// 实际间隔时间大于最小间隔时间,则清理
				if (second > intervalTime) {	
					phoneMap.remove(key);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		valiPhone(null,30);
	}
}
