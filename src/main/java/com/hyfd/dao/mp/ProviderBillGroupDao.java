package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderBillGroupDao extends BaseDao
{
    
    /**
     * @功能描述： 根据条件获流量通道组总数
     *
     * @作者：zhangpj @创建时间：2016年12月17日
     * @param Map
     * @return
     */
    Integer selectCount(Map<String, Object> Map);
    
    String getCrtUserById(String id);
}