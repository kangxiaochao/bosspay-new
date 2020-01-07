package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface PaymentDao extends BaseDao{

	public List<Map<String, Object>> SelectQueyByOne(Map<String, String> map);
	
	public int selectCount(Map<String, Object> map);
}
