package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface YunpuOrderDao extends BaseDao{
	/**
	 * 按照主键查询
	 * @param key
	 * @return
	 */
	Map<String,Object>	selectByPrimaryKey(int key);
	
	/**
	 * 添加数据
	 */
	int insert(Map<?, ?> map);
	
	/**
	 * 查询全部数据
	 */
	List<Map<String, Object>> selectAll(Map<?, ?> map);
	
}