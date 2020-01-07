package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ProviderGroupBillRelDao extends BaseDao
{
    
    public List<String> getGroupRelProvince(Map<String, Object> m);
    
    public int ProviderGroupBillRelAdd(Map<String, Object> m);
    
    public int ProviderGroupBillRelEdit(Map<String, Object> m);
    
    public int countByBillGroupIdAndProviderId(Map<String, String> param);
    
    Integer selectCount(Map<String, Object> Map);
    
    boolean deleteBygroupIdAndProviderId(Map<String, Object> Map);
    
    List<Map<String, Object>> querySelectGroupByGroupId(String groupId);
    
    int insertBillGroupRel(Map<String, Object> insMap);
    
    int deleteByGroupId(String groupId);
    
    /**
     * @功能描述： 获取可用通道组,查询订单详细参数时使用
     *
     * @作者：zhangpj @创建时间：2017年4月26日
     * @param param
     * @return
     */
    List<Map<String, Object>> selectPhysicalChannel(Map<String, Object> param);
    
    /**
     * @功能描述：	获取可用通道组及是否是特惠通道
     *
     * @param param
     * @return 
     *
     * @作者：zhangpj		@创建时间：2017年11月28日
     */
    List<Map<String, Object>> selectPhysicalChannelExt(Map<String, Object> param);
    
    List<Map<String, Object>> getProviderIdByGroupId(String param);
    
    List<Map<String, Object>> getPhysicalIdByProviderId(Map<String, Object> param);
    
    List<Map<String, Object>> getProvinceCodeByPhysicalId(Map<String, Object> param);
    
    int updateGroupBillByDispatcher(Map<String, Object> param);
}