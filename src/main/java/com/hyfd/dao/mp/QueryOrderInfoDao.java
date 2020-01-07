package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

public interface QueryOrderInfoDao {

	List<Map<String,Object>> selectOrderInfo(Map<String, Object> param);

	List<Map<String,Object>> selectExceptionOrderInfo(Map<String, Object> param);

	List<Map<String,Object>> selectOrderPathRecordInfo(Map<String, Object> param);

	List<Map<String,Object>> selectSubmitOrderInfo(Map<String, Object> param);

	List<Map<String,Object>> selectAgentAccountChargeInfo(Map<String, Object> param);

}
