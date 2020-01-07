package com.hyfd.dao.sys;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface SysNoticeDao extends BaseDao {

	public int getNoticeCount(Map<String, Object> m);
	
	public List<Map<String, Object>> selectList(Map<String, Object> m);
}