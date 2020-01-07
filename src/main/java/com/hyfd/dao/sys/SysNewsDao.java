package com.hyfd.dao.sys;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface SysNewsDao extends BaseDao {

	public int getNewsCount(Map<String, Object> m);
}