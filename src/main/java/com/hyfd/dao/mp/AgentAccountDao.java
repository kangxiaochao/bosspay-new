package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

/**
 * @功能描述：	
 *
 * @作者：zhangpj		@创建时间：2016年12月26日
 */
/**
 * @功能描述：	
 *
 * @作者：zhangpj		@创建时间：2016年12月26日
 */
public interface AgentAccountDao extends BaseDao {

	/**
	 * 根据代理商id查询余额（包括余额与信用额度，以分为单位的）
	 * @author lks 2016年12月7日上午11:48:18
	 * @param agentId
	 * @return
	 */
	Double selectBalanceByAgentid(String agentId);

	/**
	 * 扣款方法
	 * @author lks 2016年12月8日下午5:28:02
	 * @param agentAccParam (agentId,money)
	 * @return
	 */
	int charge(Map<String, Object> agentAccParam);
	
	int updateByAgentIdSelective(Map<String, Object> map);
	
	Map<String,Object> selectByAgentid(Map<String, Object> map);
	
	/**
	 * @功能描述：	根据代理商id删除代理商对应的折扣信息
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2016年12月26日
	 */
	int deleteByAgentId(String id);

	double selectChildBalanceByAgentid(String agentId);

	int addMoney(Map<String, Object> map);

}