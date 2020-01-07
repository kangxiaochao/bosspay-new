package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface CodeDao extends BaseDao{

	public List<Map<String,Object>> getProvinceCode(String parent);
	
	public List<Map<String,Object>> getCityCode(String parent);
}
