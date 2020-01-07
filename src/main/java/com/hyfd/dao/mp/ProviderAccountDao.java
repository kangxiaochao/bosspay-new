package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderAccountDao extends BaseDao {

	/**
	 * 上家扣款
	 * @author lks 2016年12月16日上午10:28:45
	 * @param chargeParam
	 * @return
	 */
	public int charge(Map<String, Object> chargeParam);
	
	
	public int providerAccountEdit(Map<String, Object> chargeParam);

	/**
	 * 根据代理商Id查询余额
	 * @author lks 2016年12月16日上午10:28:41
	 * @param providerId
	 * @return
	 */
	public String getBalanceByProviderId(String providerId);
	
	public Map<String, Object> getProviderAccountByProviderId(String providerId);
	
	public int providerAccountAdd(Map<String, Object> parmsMap);
	
	public int providerAccountDel(String id);

}