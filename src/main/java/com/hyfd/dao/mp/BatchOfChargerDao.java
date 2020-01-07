package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface BatchOfChargerDao extends BaseDao{

	int selectCount(Map<String, Object> map);
	
}
