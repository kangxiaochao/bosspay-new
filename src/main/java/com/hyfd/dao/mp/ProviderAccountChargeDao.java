package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderAccountChargeDao extends BaseDao{

	Integer selectCount(Map<String,Object> Map);
	
	List<Map<String, Object>> selectProviderAccountChargeList(Map<String,Object> map);
	
	Integer providerAccountChargeAdd(Map<String, Object> m);
	
}
