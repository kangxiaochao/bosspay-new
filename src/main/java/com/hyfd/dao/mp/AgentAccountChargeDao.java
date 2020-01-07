package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentAccountChargeDao extends BaseDao {
	
	Integer selectCount(Map<String,Object> Map);

	double countProfit(Map<String, Object> map);

	int countOrder(Map<String, Object> numMap);

	List<Map<String, Object>> selectReceiptList(Map<String, Object> m);
	
	int checkDistinctOrder(Map<String, Object> numMap);

	List<Map<String, Object>> selectMergeReceiptList(Map<String, Object> param);

	List<Map<String, Object>> selectChargeList(String agentOrderId);
	
	List<Map<String, Object>> exportNewCustomerAgent(Map<String, Object> m);
	
	List<Map<String, Object>> exportMonthlyAddMoneyAgent(Map<String, Object> m);
	
}