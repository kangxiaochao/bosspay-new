package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface FinancialReportDao extends BaseDao{

	List<Map<String, Object>> selectSWChannelData(Map<String, Object> param);

	List<Map<String, Object>> selectXSChannelData(Map<String, Object> param);

	List<Map<String, Object>> selectSWAgentData(Map<String, Object> param);

	List<Map<String, Object>> selectXSAgentData(Map<String, Object> param);

	List<Map<String, Object>> selectTSAgentData(Map<String, Object> param);

	List<Map<String, Object>> selectBusinessList(Map<String, Object> param);

}
