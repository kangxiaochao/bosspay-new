package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentBillDiscountDao extends BaseDao
{
    
    double selectDiscount(Map<String, String> param);
    
    // 获取指定代理商的所有上级代理商满足条件的折扣信息
    public List<Map<String, Object>> getAllParentAgentBillDiscount(Map<String, Object> param);
    
    Integer selectCount(Map<String, Object> Map);
    
    Integer selectCountByAgentId(Map<String, Object> Map);
    
    public List<Map<String, Object>> getAgentBillDiscountList(Map<String, Object> Map);
    
    public Map<String, Object> agentBillDiscountGet(Map<String, Object> Map);
    
    Map<String, Object> selectDiscountMap(Map<String, Object> param);
    
    int delByAgentAndProvider(Map<String, Object> param);
    
    int deleteDiscount(Map<String, Object> param);
    
    List<Map<String, Object>> selectByBillType(Map<String, Object> param);
    
    int updateDiscount(Map<String, Object> param);
    
    public List<Map<String, Object>> getAgentBillDiscountPkgList(Map<String, Object> Map);
    
    public List<Map<String, Object>> getAgentBillDiscountPkgListBySuId(Map<String, Object> Map);
    
    List<Map<String, Object>> getAllAgentBillDiscount(Map<String, Object> Map);
    
    int getAgentBillDiscountListCount(Map<String, Object> Map);
    
    int checkProvince(Map<String, Object> map);
    
    int updateDiscountDel(Map<String, Object> map);
    
    /**
     * @功能描述：	删除指定代理商的指定运营商的折扣信息
     *
     * @param map
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月18日
     */
    int discountDel(Map<String, Object> map);
    
    /**
     * @功能描述：	删除指定代理商的所有折扣信息
     *
     * @param map
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年3月2日
     */
    int discountDelByAgentId(Map<String, Object> map);

	List<Map<String, Object>> getAllAgentBillDiscountX(Map<String, Object> m);

	String getAllAgentBillDiscountSql(Map<String, Object> m);

	List<Map<String, Object>> getAllAgentBillDiscountForSql(String sql);

	List<String> getColModel(Map<String, Object> param);

	double selectVpd(Map<String, Object> param);

	
	/**
	 * @功能描述：	获取指定代理商的所有折扣信息
	 *
	 * @param Map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年3月1日
	 */
	public List<Map<String, Object>> selectAgentBillDiscountListByAgentid(String agentId);
	
	/**
	 * @功能描述：	获取微信话费充值的活动信息
	 *
	 * @param Map
	 * @return 
	 *
	 * @作者：xiexz	@创建时间：2020年3月3日
	 */
	public Map<String,Object> selectActivity();
	
	public void updateActivity(String places);
}