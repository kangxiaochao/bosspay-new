package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface IpDao extends BaseDao {
	/**
     * @功能描述： 根据条件获取代理商总数
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param Map
     * @return
     */
    Integer selectCount(Map<String, Object> Map);
    
    /**
     * @功能描述：	根据条件获取代理商总数,接口调用(充值查询等,主要于接收下游客户请求时使用)
     *
     * @param Map
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月30日
     */
    Integer selectCountByInterface(Map<String, Object> Map);
}