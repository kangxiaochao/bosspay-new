package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface BillPkgDao extends BaseDao
{
    
    public int getBillPkgCount(Map<String, Object> m);
    
    public List<Map<String, Object>> getBillPkgList(Map<String, Object> m);
    
    public int billPkgAdd(Map<String, Object> m);
    
    public Map<String, Object> getBillPkgById(String id);
    
    public int billPkgEdit(Map<String, Object> m);
    
    public int billPkgDel(String id);
    
    public List<Map<String, Object>> getBillPkgByProId(Map<String, Object> m);// 根据代理商ID查询所拥有的流量包
    
    public List<String> getPkgId(Map<String, Object> m);
    
    public Map<String, Object> getBillPkgByProIdAndPrice(Map<String, Object> m);// 根据代理商ID和流量包面值查询流量包
    
    public String getBillPkgIdByProIdAndValue(Map<String, Object> m);
    
    /**
     * 业务逻辑用查询流量包方法
     * 
     * @author lks 2016年12月26日下午4:08:10
     * @param param
     * @return
     */
    public Map<String, Object> selectBillPkgForAgent(Map<String, String> param);
    
    /**
     * 获得流量包ID
     * 
     * @param name
     * @return
     */
    public String getBillPkgId(Map<String, Object> param);
    
    public int batchDeleteByPrimaryKey(String[] ids);
    
    public String getBillPkgByPrice(String price);

	public Map<String, Object> selectPkgs(Map<String, Object> pkgParam);

	public String selectIdByValue(double value);

	public Map<String, Object> selectBillPkgForAgentRYC(Map<String, String> param);
}