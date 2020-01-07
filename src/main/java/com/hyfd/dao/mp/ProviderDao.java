package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderDao extends BaseDao
{
    
    public int getProviderCount(Map<String, Object> m);
    
    public List<Map<String, Object>> getProviderList(Map<String, Object> m);
    
    public int providerAdd(Map<String, Object> m);
    
    public String getNameById(String id);
    
    public Map<String, Object> getProviderById(String id);
    
    public int providerEdit(Map<String, Object> m);
    
    public int providerDel(String id);
    
    public List<String> getProviderIdList();
    
    public String getIdByName(String name);

	public Object getIdByShortName(String prodIsptype);
    
}