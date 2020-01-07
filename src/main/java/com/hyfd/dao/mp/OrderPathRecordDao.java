package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface OrderPathRecordDao extends BaseDao {

	int selectCount(Map<String, Object> m);
	
	/**
	 * @功能描述：	获取订单参数
	 *
	 * @作者：zhangpj		@创建时间：2017年5月2日
	 * @param m
	 * @return
	 */
	public Map<String, Object> selectOrderPathRecordParam(Map<String, Object> m);
	
	/**
	 * @功能描述：	根据订单编号或者电话号码获取最新一条的订单信息
	 *
	 * @作者：zhangpj		@创建时间：2017年5月2日
	 * @param m
	 * @return
	 */
	public Map<String, String> selectFastNewOrderPathRecordByPhone(Map<String, Object> m);
}