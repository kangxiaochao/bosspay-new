package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderDataDiscountDao extends BaseDao
{
    
    public Double selectDiscount(Map<String, Object> param);
    
    public List<Map<String, Object>> selectByDataPkgId(Map<String, Object> param);
    
    public int providerDataDiscountAdd(Map<String, Object> m);
    
    public int providerDataDiscountEdit(Map<String, Object> m);
    
    public int providerDataDiscountEditByDataPkgId(Map<String, Object> m);
    
    public int updateNameByDataPkgId(Map<String, Object> m);
    
    public int deleteProviderDataDiscountByProviderIdAndDataPkgId(Map<String, Object> m);
    
    public int deleteProviderDataDiscountByProviderIdAndProviderPhysicalChannelId(Map<String, Object> m);
    
    /**
     * @功能描述： 根据物理运营商通道id,运营商id和地区删除对应的折扣信息
     *
     * @作者：zhangpj @创建时间：2017年5月13日
     * @param m
     * @return
     */
    public int deleteProviderDataDiscount(Map<String, Object> m);
    
    public List<Map<String, Object>> getProviderDataDiscountByDataPkgId(String data_pkg_id);
    
    public List<Map<String, Object>> getProviderDataDiscountByDataPkgIdAndProvinceId(Map<String, Object> m);
    
    public List<Map<String, Object>> selectDiscountMap(Map<String, Object> param);
    
    /**
     * @功能描述： 根据条件获取运营商物理通道折扣总数
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param Map
     * @return
     */
    Integer selectCount(Map<String, Object> Map);
    
    /**
     * <h5>功能描述:</h5> 根据物理运营商id、运营商、流量包类型获取流量包范围
     *
     * @param Map
     * @return
     *
     * @作者：zhangpj @创建时间：2017年4月1日
     */
    public List<Map<String, Object>> selectProvinceCode(Map<String, Object> Map);
    
    /**
     * <h5>功能描述:</h5> 根据物理运营商id、运营商、流量包类型、流量包范围获取运营商折扣信息
     *
     * @param Map
     * @return
     *
     * @作者：zhangpj @创建时间：2017年4月1日
     */
    public List<Map<String, Object>> selectDataPkgAndDiscount(Map<String, Object> Map);
    
}