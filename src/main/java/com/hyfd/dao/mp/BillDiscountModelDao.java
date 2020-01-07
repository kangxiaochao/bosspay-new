package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface BillDiscountModelDao extends BaseDao {
	
	/**
	 * @功能描述：	根据条件获取代理商流量折扣模板总数
	 *
	 * @作者：zhangpj		@创建时间：2016年12月12日
	 * @param Map
	 * @return
	 */
	Integer selectCount(Map<String,Object> Map);
}