package com.hyfd.dao.mp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderBillDiscountDao extends BaseDao
{
    
    /**
     * @功能描述： 根据条件获取运营商物理通道折扣总数
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param Map
     * @return
     */
    Integer selectCount(Map<String, Object> Map);
    
    /**
     * 获取运营商折扣
     * 
     * @author lks 2016年12月9日上午10:08:38
     * @param param
     * @return
     */
    public double selectDiscount(Map<String, Object> param);
    
    public int providerBillDiscountAdd(Map<String, Object> m);
    
    public int providerBillDiscountEdit(Map<String, Object> m);
    
    public int deleteProviderBillDiscountByBillPkgId(String bill_pkg_id);
    
    public List<Map<String, Object>> getProviderBillDiscountByBillPkgId(String bill_pkg_id);
    
    public List<Map<String, Object>> getProviderBillDiscountByBillPkgIdAndProvinceCode(Map<String, Object> param);
    
    /**
     * @功能描述： 根据物理运营商通道id,运营商id和地区删除对应的话费折扣信息
     *
     * @作者：zhangpj @创建时间：2017年5月13日
     * @param m
     * @return
     */
    public int deleteProviderBillDiscount(Map<String, Object> m);
    
    public List<Map<String, Object>> selectDiscountMap(Map<String, Object> param);
    
    int getProviderBillDiscountListCount(Map<String, Object> param);
    
    List<Map<String, Object>> getAllProviderBillDiscount(Map<String, Object> param);
    
    // 根据物理运营商通道和运营商类型删除折扣
    int deleteDiscountDel(Map<String, Object> param);

	Map<String, BigDecimal> selectChannelVpd(Map<String, Object> param);
	
	String getAllAgentBillDiscountSql(Map<String, Object> m);

	List<Map<String, Object>> getAllAgentBillDiscountForSql(String sql);

	List<String> getColModel(Map<String, Object> param);
	
	int updateDiscount(Map<String, Object> param);
	
}