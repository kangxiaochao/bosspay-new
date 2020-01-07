package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

public interface QianmiTokenDao extends BaseDao {

	/**
	 * 查询最新的token
	 * @author lks 2017年2月9日下午2:55:28
	 * @param type 
	 * @return
	 */
	String selectCurrentToken(String type);

}