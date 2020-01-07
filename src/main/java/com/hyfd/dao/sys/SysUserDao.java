package com.hyfd.dao.sys;

import java.util.List;
import java.util.Map;

public interface SysUserDao
{
    
    public Map<String, Object> getSysUserBySuName(String suName);
    
    public Map<String, Object> getSysUserBySuId(String suId);
    
    public int getSysUserCount(Map<String, Object> m);
    
    public List<Map<String, Object>> getSysUserList(Map<String, Object> m);
    
    /**
     * @功能描述： 获取未和代理商绑定的用户信息
     *
     * @作者：zhangpj @创建时间：2016年12月20日
     * @return
     */
    public List<Map<String, Object>> getSysUsersByNotAgentUserId();
    
    public int sysUserAdd(Map<String, Object> m);
    
    public int sysUserEdit(Map<String, Object> m);
    
    public int sysUserDel(String suId);
    
    public int sysUserPassChange(Map<String, Object> m);
    
    public Map<String, Object> agentGetByUserId(String suId);
    public Map<String, Object> agentGetByUserName(String userName);
    
    /**
     * @Title:getAllChannelPerson
     * @Description: 根据渠道角色获取所有渠道人员（但是角色直接写中文名感觉不合适）(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:16:51
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getAllChannelPerson();
    
}
