package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

import java.util.Map;

public interface AgentProfitChargeDao extends BaseDao {

    /**
     * 根据ID修改
     * @param map
     * @return
     */
    int updateById(Map<String, Object> map);

}
