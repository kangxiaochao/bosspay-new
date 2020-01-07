package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface TmallProductDao extends BaseDao
{
    
    public int selectCount(Map<String, Object> m);
    
    public List<Map<String, Object>> queryTmallProductList(Map<String, Object> m);
    
    public int tmallProductAdd(Map<String, Object> m);
    
    public Map<String, Object> getTmallProductById(String id);
    
    public int tmallProductEdit(Map<String, Object> m);
    
    public int tmallProductDel(String id);
    
}