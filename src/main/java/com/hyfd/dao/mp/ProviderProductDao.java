package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderProductDao extends BaseDao {

	Map<String, Object> selectProviderProductId(Map<String, Object> productParam);

	List<Map<String, Object>> selectByPrimary(Map<String, Object> productParam);
	
	int selectCount(Map<String, Object> productParam);
}