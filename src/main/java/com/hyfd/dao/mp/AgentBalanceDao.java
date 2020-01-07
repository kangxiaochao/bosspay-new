package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentBalanceDao extends BaseDao{
	
	//查看代理商的email是否存在
	Map<String, Object> selectByPrimaryKets(String id);
}