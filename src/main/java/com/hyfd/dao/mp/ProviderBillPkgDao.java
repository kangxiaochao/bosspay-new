package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderBillPkgDao  extends BaseDao{

	Integer selectCount(Map<String,Object> map);
	
	int deleteByPrividerId(String providerId);
	
	// 根据运营商、充值地区 获取对应的话费包信息
	List<Map<String,Object>> selectProviderBillPkg(Map<?,?> record);
	
	List<Map<String,String>> selectProviderBillPkgByProviderId(Map<?,?> record);

	List<String> selectPkgIdByProviderId(Map<String, Object> param);
	
}
