package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

import java.util.Map;

public interface SubAgentAccountChargeDao extends BaseDao {

    Integer selectCount(Map<String,Object> Map);

}
