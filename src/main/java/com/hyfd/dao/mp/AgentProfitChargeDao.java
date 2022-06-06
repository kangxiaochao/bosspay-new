package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

import java.util.Map;

public interface AgentProfitChargeDao extends BaseDao {

    Integer selectCount(Map<String,Object> Map);

    /**
     * 根据ID修改isRefund
     * @param map
     * @return
     */
    int updateById(Map<String, Object> map);

}
