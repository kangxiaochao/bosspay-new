package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface CookiesDao extends BaseDao {

	Map<String, Object> selectFirstCookie();
	
	Map<String, Object> selectFirstTTBZYCookie();

	Map<String, Object> selectFirstYYCookie();
	
	Map<String, Object> selectFirstKSZXCookie();
	
	Map<String, Object> selectFirstHHBCookie();
	
	Map<String, Object> selectFirstHHBZYCookie();

	Integer selectCount(Map<String, Object> Map);
}