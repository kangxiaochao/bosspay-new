package com.hyfd.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao
{
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    int insert(Map<?, ?> record);
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    int insertSelective(Map<?, ?> record);
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    Map<String, Object> selectByPrimaryKey(String id);
    
    /**
     * @功能描述： 获取全部信息
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param record
     * @return
     */
    List<Map<String, Object>> selectAll(Map<?, ?> record);
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Map<?, ?> record);
    
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * mp_agent_account_charge
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Map<?, ?> record);
    
    Map<String, Object> selectByUserId(String userId);
    
}
