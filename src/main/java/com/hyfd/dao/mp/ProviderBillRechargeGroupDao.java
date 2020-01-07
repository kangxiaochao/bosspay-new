package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderBillRechargeGroupDao extends BaseDao
{
    
    /**
     * 根据运营商ID查询分销商
     * 
     * @author lks 2016年12月15日下午3:16:52
     * @param param
     * @return
     */
    List<Map<String, Object>> selectByProviderId(Map<String, Object> param);
    
    Integer selectCount(Map<String, Object> Map);
    
    String getRechargeIdByPriority(Map<String, Object> Map);
    
    Integer updateRechargePriority(Map<String, Object> Map);
    
    Integer getMaxPriority(Map<String, Object> Map);
    
    Map<String, Object> getBillById(String id);
    
    List<Map<String, Object>> getRechargeBillById(Map<String, Object> param);
}