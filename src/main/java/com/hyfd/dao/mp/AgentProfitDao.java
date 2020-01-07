package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentProfitDao extends BaseDao {
	
	List<Map<String, Object>> selectAgentProfitList(Map<?, ?> record);
}
