package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

public interface OrderAllAgentDao extends BaseDao {
	
	public void deleteByOrderId(String agentOrderId);

}