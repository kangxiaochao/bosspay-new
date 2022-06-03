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
	 * 根据代理商id查询利润余额
	 * @author xxz 2022年05月28日下午20:08:30
	 * @param agentId
	 * @return
	 */
	Double selectProfitByAgentid(String agentId);

	/**
	 * 扣款方法
	 * @author lks 2016年12月8日下午5:28:02
	 * @param agentAccParam (agentId,money)
	 * @return
	 */
	int charge(Map<String, Object> agentAccParam);

	/**
	 * 代理商利润 加款/扣款 方法
	 * @author xxz 2022年05月28日下午20:08:30
	 * @param agentAccParam (agentId,profit)
	 * @return
	 */
	int agentProfitCharge(Map<String, Object> agentAccParam);

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