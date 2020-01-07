package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderPhysicalChannelDao extends BaseDao
{
    
    /**
     * 根据运营商ID查询物理通道
     * 
     * @author lks 2016年12月10日上午11:37:16
     * @param providerId 运营商ID
     * @return
     */
    Map<String, Object> selectByProviderId(String providerId);
    
    public int getProviderPhysicalChannelCount(Map<String, Object> m);
    
    public List<Map<String, Object>> getProviderPhysicalChannelList(Map<String, Object> m);
    
    List<Map<String, Object>> getPhysicalList();
    
    public List<Map<String, Object>> getProviderList(Map<String, Object> m);
    
    public int providerPhysicalChannelAdd(Map<String, Object> m);
    
    public Map<String, Object> getProviderPhysicalChannelById(String id);
    
    public int providerPhysicalChannelEdit(Map<String, Object> m);
    
    public int providerPhysicalChannelDel(String id);
    
    public String getProviderIdByName(String name);
    
    public List<Map<String, Object>> getAllPhysicalChannel();
}