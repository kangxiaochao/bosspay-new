package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface dataDao extends BaseDao{

	List<Map<String, Object>> selectTodayProfit();

	List<Map<String, Object>> selectAllProfit();

	List<Map<String, Object>> selectPhysicalChannelProfit(Map<String, Object> param);

	List<Map<String, Object>> selectForntAgent(Map<String, Object> agentParam);

	List<String> selectAgentChartsData(Map<String, Object> agentMap);

	List<Map<String, Object>> selectForntChannel(Map<String, Object> param);

	List<String> selectChannelChartsData(Map<String, Object> chennelMap);

}
