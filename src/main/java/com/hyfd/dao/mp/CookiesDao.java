package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface CookiesDao extends BaseDao {

	Map<String, Object> selectFirstCookie();

	Map<String, Object> selectFirstYYCookie();
	
	Map<String, Object> selectFirstKSZXCookie();
}