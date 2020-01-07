package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderChargeRecordDao extends BaseDao {
	
	/**
	 * @功能描述：	获取指定通道指定时间段内消费总额和余额
	 *
	 * @param param
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月9日
	 */
	public Map<String, Object> selectDispatcherProviderStatisticsInfo(Map<String, Object> param);
}