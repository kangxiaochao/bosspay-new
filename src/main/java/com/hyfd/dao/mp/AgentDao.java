package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentDao extends BaseDao
{
    
    /**
     * 根据代理商名（name）查询代理商信息
     * 
     * @author lks 2016年12月7日上午11:41:59
     * @param agentName
     * @return
     */
    Map<String, Object> selectByAgentName(String agentName);
    
    /**
     * @功能描述： 根据条件获取代理商总数
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param Map
     * @return
     */
    Integer selectCount(Map<String, Object> Map);
    
    /**
     * 根据代理商名（name）查询代理商信息
     * 
     * @author lks 2016年12月15日下午5:35:36
     * @param agentName
     * @return
     */
    Map<String, Object> selectByAgentNameForOrder(String agentName);
    
    Map<String, Object> selectById(String id);
    
    Map<String, Object> selectByPrimaryKeyForOrder(String id);
    
    /**
     * @功能描述： 根据自定义列名查询代理商信息
     *
     * @作者：zhangpj @创建时间：2016年12月21日
     * @return
     */
    List<Map<String, Object>> selectByColumn(Map<String, Object> map);
    
    List<Map<String, Object>> selectAgentAllParent();
    
    /**
     * @功能描述： 查询所有父级代理商信息
     *
     * @作者：zhangpj @创建时间：2016年12月21日
     * @return
     */
    List<Map<String, Object>> selectParentAgent(Map<String, Object> map);

    /**
     * 获取指定代理商的所有子代理商，不包含自己
     * @param agentId
     * @return
     */
    List<Map<String, Object>> selectAllSubAgent(String agentId);

    Map<String, Object> selectByUserId(String userId);
    
    /**
     * @功能描述：	查询指定渠道下的所有代理商信息
     *
     * @param channelPerson
     * @return 
     *
     * @作者：zhangpj		@创建时间：2017年12月5日
     */
    List<Map<String, Object>> selectByChannelPerson(String channelPerson);
    
    List<String> getIdByParentId(String parentId);
    
    String selectAgentIdByUserid(String userId);
    
    /**
     * @Title:updateByParentPath
     * @Description: 通过父级路径更新(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 下午2:54:13
     * @param @param parentPath
     * @param @return
     * @return int 返回类型
     * @throws
     */
    int updateByParentPath(Map<String, Object> parentPathMap);
    
    Map<String, Object> selectByAgentNameAndParent(Map<String, Object> map);
}