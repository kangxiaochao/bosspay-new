package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentChannelRelDao extends BaseDao
{
    
    Map<String, Object> queryAgentChannelRel(Map<String, Object> map);
    
    /**
     * @Title:selectCount
     * @Description: 根据条件获取总数(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:29:20
     * @param @param map
     * @param @return
     * @return int 返回类型
     * @throws
     */
    int selectCount(Map<String, Object> map);
    
    /**
     * @Title:getAgentChannelRelByAgentId
     * @Description: 根据条件得到特惠数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:29:44
     * @param @param map
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    List<Map<String, Object>> getAgentChannelRelByAgentId(Map<String, Object> map);
    
    /**
     * @Title:queryAgentChannelRelByAgentId
     * @Description: 查询已配置的特惠通道(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:30:29
     * @param @param agentId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    List<Map<String, Object>> queryAgentChannelRelByAgentId(String agentId);
    
    /**
     * @Title:deleteByAgentId
     * @Description: 删除已配置的特惠通道(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:32:15
     * @param @param agentId
     * @param @return
     * @return int 返回类型
     * @throws
     */
    int deleteByAgentId(Map<String, Object> map);
    
    /**
     * @Title:insertAgentChannelRel
     * @Description: 插入特惠通道(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:32:47
     * @param @param map
     * @param @return
     * @return int 返回类型
     * @throws
     */
    int insertAgentChannelRel(Map<String, Object> map);
}