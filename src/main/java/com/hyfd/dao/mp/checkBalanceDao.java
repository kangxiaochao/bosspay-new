package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface checkBalanceDao extends BaseDao{

	//获取订单失败但扣款记录不是两条的订单id
	List<Map<String, Object>> selectMultipleDeductions(Map<String, Object> param);

	//获取实际扣款金额与应扣款金额不同的订单数据
	List<Map<String, Object>> selectDiff(Map<String, Object> param);

	//获取实际扣款
	double selectActualDeductions(Map<String, Object> param);

	//获取消耗额
	Map<String, Object> selectConsume(Map<String, Object> param);

	List<String> selectAgentId(Map<String, Object> param);

	List<Map<String, Object>> selectChargeList(Map<String, Object> map);

	double selectChargeFee(Map<String, Object> map);
	
	double selectAgentDiff(Map<String, Object> map);
	
	double selectAgentBalance(Map<String, Object> map);

	int selectAgentIdCount(Map<String, Object> param);

	double selectAgentAddMoney(Map<String, Object> param);

	double selectAdjustment(Map<String, Object> param);
	
	List<String> selectDispatcherProviderId(Map<String, Object> param);
	
	int selectDispatcherProviderIdCount(Map<String, Object> param);
	
	double selectProviderBalance(Map<String, Object> map);
	
	double selectCutMoney(Map<String, Object> param);
	
	String selectProviderNameById(Map<String, Object> param);
	
}
