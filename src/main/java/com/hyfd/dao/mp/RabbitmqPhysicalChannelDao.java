package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface RabbitmqPhysicalChannelDao extends BaseDao {

    /**
     * @功能描述：	根据条件获取物理运营商通道匹配的rabbit通道
     *
     * @param Map
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年2月1日
     */
    Integer selectCount(Map<String, Object> Map);
    
}