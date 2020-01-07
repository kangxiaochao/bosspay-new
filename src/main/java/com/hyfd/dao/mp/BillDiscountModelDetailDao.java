package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface BillDiscountModelDetailDao extends BaseDao{
	
	public List<Map<String,Object>> getBillDiscountModelDetailList(Map<String,Object> m);//根据模板ID查询折扣详情列表
	
	public Integer selectCount(Map<String,Object> Map);
	
	boolean billDiscountModelDetailDelByModelId(String modelId);
}
