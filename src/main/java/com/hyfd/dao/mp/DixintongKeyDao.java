package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface DixintongKeyDao extends BaseDao {

	Map<String, Object> selectRecentKey();

}