package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface TutubiOrderDao extends BaseDao {

	String selectFirstOrderId();

	Map<String, Object> selectByOrderId(Map<String, Object> map);
	
	int selectCount(Map<String, Object> map);
}