package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderBillDispatcherDao extends BaseDao
{
    
    List<Map<String, Object>> selectDispatcherByProviderId(Map<String, Object> param);
    
    public int providerBillDispatcherDelByProviderId(String provider_id);
    
    public int providerBillDispatcherAdd(Map<String, Object> param);
    
    public List<Map<String, Object>> getProviderBillDispatcherByProverId(String provider_id);
    
    public List<Map<String, Object>> getProviderAll(String groupId);
    
    public List<Map<String, Object>> getPhysicalAll(Map<String, Object> param);
    
    public List<Map<String, Object>> getProvinceAll(Map<String, Object> param);
    
    /**
     * @功能描述： 获取可用通道组,查询订单详细参数时使用
     *
     * @作者：zhangpj @创建时间：2017年4月26日
     * @param param
     * @return
     */
    List<Map<String, Object>> selectProviderPhysicalChannel(Map<String, Object> param);
    
    List<Map<String, Object>> getDispatcherByPhysicalId(Map<String, Object> map);
    
    int selectCount(Map<String, Object> map);
    
    List<Map<String, Object>> querySelectDispatcherByPhysicalId(String physicalId);
    
    int deleteByPhysicalId(String physicalId);
    
    int insertDispatcher(Map<String, Object> map);
    
    int updateDelFlag(Map<String, Object> map);
    
    int updateDelFlagByDispatcher(Map<String, Object> map);
    
    Map<String, Object> selectDispatcher(Map<String, Object> map);
    
    List<Map<String, Object>> selectProvinceCode(Map<String, Object> map);
    
}