package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface BaseBillDiscountDetailDao extends BaseDao {

	double selectDiscount(Map<String, String> param);

	Map<String, Object> selectDiscountMap(Map<String, Object> param);

}