package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentAccountChargeAuditDao extends BaseDao{

	Integer selectCount(Map<String,Object> Map);
	
	List<Map<String, Object>> selectAgentAccountChargeAuditList(Map<String,Object> map);
	
	Integer agentAccountChargeAuditAdd(Map<String, Object> m);
	
	Integer agentAccountChargeAuditEdit(Map<String, Object> m);
	
	Map<String,Object> agentAccountSumFee(Map<String, Object> m);
	
	Integer agentAccountChargeEditName(Map<String, Object> m);
}
