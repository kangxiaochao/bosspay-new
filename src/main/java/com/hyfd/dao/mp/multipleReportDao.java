package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface multipleReportDao extends BaseDao{

	List<String> selectBrand(Object put);

	double selectSumDisprice(Map<String, Object> param);

	double selectSumMydisprice(Map<String, Object> param);

	Map<String, Object> selectChannelDailyDate(Map<String, Object> param);

	List<String> selectFlowGroupName(Map<String, Object> param);

	Map<String, Object> selectDailyData(Map<String, Object> param);

	List<Map<String, Object>> selectChannelData(Map<String, Object> param);

	List<Map<String, Object>> selectAgentData(Map<String, Object> param);

	int countProviderDailyReport(Map<String, Object> param);

	List<Map<String, Object>> selectProviderDailyReport(Map<String, Object> param);

	int countAgentDailyReport(Map<String, Object> param);

	List<Map<String, Object>> selectAgentDailyReport(Map<String, Object> param);

	List<Map<String, Object>> selectAgentDailyData();

	List<Map<String, Object>> selectProviderDailyData();

	List<Map<String, Object>> selectProviderMonthData();

	List<Map<String, Object>> selectAgentMonthData();

	
	
}
