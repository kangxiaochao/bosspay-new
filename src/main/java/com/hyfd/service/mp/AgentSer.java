package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GenerateData;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.dao.mp.AgentAccountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.MpKeytDao;
import com.hyfd.dao.mp.ProviderGroupBillRelDao;
import com.hyfd.dao.sys.SysUserDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;
import com.hyfd.service.sys.SysUserSer;

@Service
@Transactional
public class AgentSer extends BaseService
{
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private AgentDao agentDao;
    
    @Autowired
    private AgentAccountDao agentAccountDao;
    
    @Autowired
    private SysUserDao sysUserDao;
    
    @Autowired
    private SysUserSer sysUserSer;
    
    @Autowired
    private ProviderGroupBillRelDao providerGroupBillRelDao;
    
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    
    @Autowired
    private MpKeytDao mpKeytDao;
    
    private final static String AGENTROLE = "代理商";
    
    /**
     * 根据主键获取记录
     * 
     * @param id
     * @return
     */
    public Map<String, Object> getAgentById(String id)
    {
        Map<String, Object> m = new HashMap<String, Object>();
        try
        {
            m = agentDao.selectByPrimaryKey(id);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    /**
     * 获取代理商详细信息
     * 
     * @param id
     */
    public String getAgentDetail(String id)
    {
        try
        {
            Map<String, Object> m = getAgentById(id);
            Session session = getSession();
            session.setAttribute("agent", m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/agentDetail";
    }
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getAgentCount(Map<String, Object> m)
    {
        int agentCount = 0;
        try
        {
            agentCount = agentDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return agentCount;
    }
    
    /**
     * 根据条件分页获取代理商列表数据并生成json
     * 
     * @param req
     * @return
     */
    public String agentList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Map<String, Object> userInfoMap = getUser();
            String userid = userInfoMap.get("suId") + "";
            
            Map<String, Object> agentMap = agentDao.selectByUserId(userid);
            if (agentMap != null)
            {
                String agentParentId = agentMap.get("id") + "";
                m.put("agentParentId", agentParentId);
            }
            Page p = getPage(m);// 提取分页参数
            int total = getAgentCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> dataList = agentDao.selectAll(m);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    public String agentDiscountList(HttpServletRequest req)
    {
    	Map<String, Object> m = getMaps(req);
    	Map<String, Object> userInfoMap = getUser();
        String userid = userInfoMap.get("suId") + "";
        
        Map<String, Object> agentMap = agentDao.selectByUserId(userid);
        if (agentMap != null)
        {
            String agentParentId = agentMap.get("id") + "";
            m.put("agentParentId", agentParentId);
        }
        List<Map<String, Object>> dataList = agentDao.selectAll(m);
        String dataListJson = BaseJson.listToJson(dataList);
        
        return dataListJson;
    }
    
    /**
     * @功能描述： 根据条件获取全部代理商列表数据并生成json
     *
     * @作者：zhangpj @创建时间：2016年12月16日
     * @param req
     * @return
     */
    public String agentAllList(HttpServletRequest req)
    {
        String str = null;
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            List<Map<String, Object>> dataList = agentDao.selectParentAgent(m);
            str = JSONObject.toJSONString(dataList);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    public String agentAddPage(String userId)
    {
        List<Map<String, Object>> dataList = sysUserRoleDao.getHasSysRoleList(userId);
        String agentRole = null;
        for (Map<String, Object> map : dataList)
        {
            String srName = map.get("srName") + "";
            if (AGENTROLE.equals(srName))
            {
                agentRole = srName;
            }
        }
        Session session = getSession();
        session.setAttribute("agentRole", agentRole);
        return "mp/agentAdd";
    }
    
    /**
     * 添加代理商信息
     * 
     * @param req
     * @return
     */
    public String agentAdd(HttpServletRequest req)
    {
        Session session = getSession();
        try
        {
            boolean flag = false;
            // 生成代理商id
            String agentId = GenerateData.getUUID();
            
            Map<String, Object> myData = getMaps(req);
            myData.put("id", agentId);
            // Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            // String createId = userInfoMap.get("suId") + "";
            // List<Map<String, Object>> dataList = sysUserRoleDao.getHasSysRoleList(createId);
            // String agentRole = null;
            // for (Map<String, Object> map : dataList)
            // {
            // String srName = map.get("srName") + "";
            // if (AGENTROLE.equals(srName))
            // {
            // agentRole = srName;
            // }
            // }
            Map<String, Object> userInfoMap = getUser();
            String userid = userInfoMap.get("suId") + "";
            // 查询用户是否是代理商
            Map<String, Object> agentMap = agentDao.selectByUserId(userid);
            // 将true、false自动转换成1、0
            myData = convertStatus(myData, "status");
            // 代理商等级
            // __________________________________________________
            int level = 1;
            String parentPath = null;
            if (null != agentMap)
            {
                // Map<String, Object> parentAgent = agentDao.selectByUserId(userid);
                myData.put("channelPerson", agentMap.get("channel_person"));
                myData.put("parentId", agentMap.get("id"));
                myData.put("billGroupId", agentMap.get("bill_group_id"));
                myData.put("dataGroupId", agentMap.get("data_group_id"));
                myData.put("billModelId", agentMap.get("bill_model_id"));
                myData.put("dataModelId", agentMap.get("data_model_id"));
                parentPath = agentMap.get("parent_path") + ">" + agentId;
                myData.put("parentPath", parentPath);
                level = Integer.parseInt(agentMap.get("level") + "") + 1;
            }
            else
            {
                myData.put("parentPath", agentId);
                myData.put("parentId", "0");
                myData.put("channelPerson", userid);
            }
            
            myData.put("level", level);
            
            myData.put("createUser", userid);// 放入创建用户
            // 生成用户id
            String suId = GenerateData.getUUID();
            myData.put("suId", suId);// 放入创建用户
            // 保存用户信息
            int userRows = sysUserSer.saveUser(myData);
            if (userRows > 0)
            {
                // 验证用户名是否存在 已存在返回true,不存在返回false
                flag = valiAgentNameYesOrNo(null, myData.get("name").toString());
                if (flag == false)
                {
                    String userId = myData.get("suId").toString();
                    myData.put("userId", userId);
                    // 保存代理商信息
                    int rows = agentDao.insert(myData);
                    if (rows > 0)
                    {
                        Map<String, Object> userRole = new HashMap<String, Object>();
                        userRole.put("suId", suId);
                        userRole.put("srId", "a82b0c03137f11e7af9a008cfae41260");
                        rows = sysUserRoleDao.sysUserRoleAdd(userRole);
                        myData.put("agentId", agentId);
                        myData.put("balance", 0);
                        myData.put("credit", 0);
                        myData.put("profit", 0);
                        // 保存代理商折扣信息
                        rows = agentAccountDao.insert(myData);
                        if (rows > 0)
                        {
                            flag = true;
                        }
                        
                    }
                    session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败!");
                }
                else
                {
                    session.setAttribute(GlobalSetHyfd.backMsg, "添加失败");
                }
            }
            else
            {
                session.setAttribute(GlobalSetHyfd.backMsg, "添加失败,添加用户异常,请稍后重试");
            }
        }
        catch (Exception e)
        {
            session.setAttribute(GlobalSetHyfd.backMsg, "添加代理商异常,请稍后重试");
            getMyLog(e, log);
        }
        return "redirect:/agentListPage";
    }
    
    /**
     * 跳转到代理商编辑页
     * 
     * @param id
     * @return
     */
    public String agentEditPage(String id, String userId)
    {
        try
        {
            List<Map<String, Object>> dataList = sysUserRoleDao.getHasSysRoleList(userId);
            String agentRole = null;
            String roleAdmin = null;
            for (Map<String, Object> map : dataList)
            {
                String srName = map.get("srName") + "";
                if (AGENTROLE.equals(srName))
                {
                    agentRole = srName;
                }
                
                if ("超级管理员".equals(srName))
                {
                    roleAdmin = srName;
                }
            }
            Session session = getSession();
            session.setAttribute("agentRole", agentRole);
            session.setAttribute("roleAdmin", roleAdmin);
            Map<String, Object> agent = getAgentById(id);
            session.setAttribute("agent", agent);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/agentEdit";
    }
    
    /**
     * 修改代理商信息
     * 
     * @param req
     * @param id
     * @return
     */
    public String agentEdit(HttpServletRequest req, String id)
    {
        Session session = getSession();
        try
        {
            boolean flag = false;
            Map<String, Object> myData = getMaps(req);
            // Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            // String createId = userInfoMap.get("suId") + "";
            // List<Map<String, Object>> dataList = sysUserRoleDao.getHasSysRoleList(createId);
            // String agentRole = null;
            // for (Map<String, Object> map : dataList)
            // {
            // String srName = map.get("srName") + "";
            // if (AGENTROLE.equals(srName))
            // {
            // agentRole = srName;
            // }
            // }
            
            Map<String, Object> userInfoMap = getUser();
            String userid = userInfoMap.get("suId") + "";
            // 查询用户是否是代理商
            Map<String, Object> agentMap = agentDao.selectByUserId(userid);
            
            // 将true、false自动转换成1、0
            myData = convertStatus(myData, "status");
            
            // __________________________________________________
            // int level = 1;
            Map<String, Object> agent = agentDao.selectById(id);
            // 验证子代理商继承父代理商的内容是否有变化，若有子集同步进行修改
            if (null == agentMap)
            {
                String newChannelPerson = myData.get("channelPerson") + "";
                String newBillGroupId = myData.get("billGroupId") + "";
                String newDataGroupId = myData.get("dataGroupId") + "";
                String newBillModelId = myData.get("billModelId") + "";
                String newDataModelId = myData.get("dataModelId") + "";
                
                String oldChannelPerson = agent.get("channel_person") + "";
                String oldBillGroupId = agent.get("bill_group_id") + "";
                String oldDataGroupId = agent.get("data_group_id") + "";
                String oldBillModelId = agent.get("bill_model_id") + "";
                String oldDataModelId = agent.get("data_model_id") + "";
                Map<String, Object> parentPathMap = new HashMap<String, Object>();
                if (!newChannelPerson.equals(oldChannelPerson))
                {
                    parentPathMap.put("channelPerson", newChannelPerson);
                }
                
                if (!newBillGroupId.equals(oldBillGroupId))
                {
                    parentPathMap.put("billGroupId", newBillGroupId);
                }
                
                if (!newDataGroupId.equals(oldDataGroupId))
                {
                    parentPathMap.put("dataGroupId", newDataGroupId);
                }
                
                if (!newBillModelId.equals(oldBillModelId))
                {
                    parentPathMap.put("billModelId", newBillModelId);
                }
                
                if (!newDataModelId.equals(oldDataModelId))
                {
                    parentPathMap.put("dataModelId", newDataModelId);
                }
                
                if (!newChannelPerson.equals(oldChannelPerson) || !newBillGroupId.equals(oldBillGroupId)
                    || !newDataGroupId.equals(oldDataGroupId) || !newBillModelId.equals(oldBillModelId)
                    || !newDataModelId.equals(oldDataModelId))
                {
                    
                    parentPathMap.put("parentPath", agent.get("parent_path"));
                    agentDao.updateByParentPath(parentPathMap);
                }
            }
            // myData.put("level", level);
            myData.put("updateUser", userid);// 放入更新用户
            
            // 验证用户名是否存在 已存在返回true,不存在返回false
            flag = valiAgentNameYesOrNo(myData.get("id").toString(), myData.get("name").toString());
            if (flag == false)
            {
                // 更新代理商信息
                int rows = agentDao.updateByPrimaryKeySelective(myData);
                if (rows > 0)
                {
                    myData.put("agentId", myData.get("id"));
                    // 更新代理商折扣信息
                    rows = agentAccountDao.updateByAgentIdSelective(myData);
                    if (rows > 0)
                    {
                        flag = true;
                    }
                }
                session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败!");
            }
            else
            {
                session.setAttribute(GlobalSetHyfd.backMsg, "修改失败,代理商名称已存在!");
            }
        }
        catch (Exception e)
        {
            session.setAttribute(GlobalSetHyfd.backMsg, "更新代理商失败,请稍后重试!");
            getMyLog(e, log);
        }
        return "agentListPage";
    }
    
    /**
     * @功能描述： 跳转到代理商密钥设置页
     *
     * @作者：zhangpj @创建时间：2016年12月27日
     * @param id
     * @return
     */
    public String agentKeyEditPage(String id)
    {
        try
        {
            Map<String, Object> agent = getAgentById(id);
            Session session = getSession();
            session.setAttribute("agent", agent);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/agentKeyEdit";
    }
    
    /**
     * 设置代理商密钥信息
     * 
     * @param req
     * @param id
     * @return
     */
    public String agentKeyEdit(HttpServletRequest req, String id)
    {
        Session session = getSession();
        try
        {
            boolean flag = false;
            Map<String, Object> myData = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            myData.put("updateUser", userInfoMap.get("suId"));// 放入更新用户
            
            // 更新代理商信息,设置代理商密钥
            int rows = agentDao.updateByPrimaryKeySelective(myData);
            if (rows > 0)
            {
                flag = true;
            }
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "设置成功" : "设置失败!");
        }
        catch (Exception e)
        {
            session.setAttribute(GlobalSetHyfd.backMsg, "设置代理商密钥失败,请稍后重试!");
            getMyLog(e, log);
        }
        return "agentListPage";
    }
    
    /**
     * 删除代理商信息
     * 
     * @param id
     * @return
     */
    public String agentDel(String id)
    {
        Session session = getSession();
        
        try
        {
            
            boolean flag = false;
            Map<String, Object> agent = agentDao.selectById(id);
            String userId = agent.get("user_id") + "";
            int rows = agentDao.deleteByPrimaryKey(id);
            
            if (rows > 0)
            {
                // 删除代理商折扣信息
                rows = agentAccountDao.deleteByAgentId(id);
                int r = sysUserRoleDao.sysUserRoleDelBySuId(userId);
                int i = sysUserDao.sysUserDel(userId);
                
                if (rows > 0 && i > 0 && r > 0)
                {
                    flag = true;
                }
            }
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败!");
        }
        catch (Exception e)
        {
            session.setAttribute(GlobalSetHyfd.backMsg, "删除代理商失败,请稍后重试!");
            getMyLog(e, log);
        }
        return "agentListPage";
    }
    
    /**
     * 检测代理商名字是否重复
     * */
    public String agentNameCheck(HttpServletRequest req)
    {
        String str = "no";
        try
        {
            Map<String, Object> map = getMaps(req);
            String name = (String)map.get("name");
            boolean flag = valiAgentNameYesOrNo(null, name);
            if (flag)
            {
                str = "yes";
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * 查询该代理商可用流量通道
     * */
    public String agentBillGroupRelGet(HttpServletRequest req)
    {
        String str = "false";
        try
        {
            Map<String, Object> map = getMaps(req);
            String agentId = (String)map.get("agentId");
            String providerId = (String)map.get("providerId");
            String dataType = (String)map.get("dataType");
            Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
            if (agent != null && providerId != "" && dataType != "")
            {
                String dataGroupId = (String)agent.get("data_group_id");
                if (dataGroupId != null && dataGroupId != "")
                {
                    map.put("groupId", dataGroupId);
                    if ("1".equals(dataType))
                    {
                        String provinceCode = "全国";
                        map.put("provinceCode", provinceCode);
                    }
                    else
                    {
                        String provinceCode = "";
                        map.put("provinceCode", provinceCode);
                    }
                    List<String> province = providerGroupBillRelDao.getGroupRelProvince(map);
                    if (province.size() > 0)
                    {
                        str = BaseJson.listToJson(province);
                    }
                }
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * @功能描述： 获取未和代理商绑定的用户信息
     *
     * @作者：zhangpj @创建时间：2016年12月20日
     * @return
     */
    public String sysUsers()
    {
        String str = null;
        try
        {
            // 获取未和代理商绑定的用户信息
            List<Map<String, Object>> dataList = sysUserDao.getSysUsersByNotAgentUserId();
            str = JSONObject.toJSONString(dataList);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * @功能描述： 验证用户名是否存在 已存在返回true,不存在返回false
     *
     * @作者：zhangpj @创建时间：2016年12月27日
     * @param id 代理商id
     * @param agentName 代理商名称
     * @return
     */
    private boolean valiAgentNameYesOrNo(String id, String agentName)
    {
        boolean flag = true;
        Map<String, Object> agentMap = null;
        try
        {
            // 根据代理商名查询代理商信息
            agentMap = agentDao.selectByAgentNameForOrder(agentName);
            if (null == id)
            {
                // 如果是新增代理商且此代理商名不存在,返回false,否则返回true
                flag = agentMap == null ? false : true;
            }
            else
            {
                if (agentMap == null)
                {
                    // 如果是修改代理商且此代理商名不存在,返回false
                    flag = false;
                }
                else
                {
                    // 如果是修改代理商且此代理商的id和被修改的id一致,返回false,否则返回true
                    flag = agentMap.get("id").equals(id) ? false : true;
                }
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return flag;
    }
    
    /**
     * @Title:agentChannelRelListPage
     * @Description: 代理商特惠通道(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:12:45
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String agentChannelRelListPage(String id)
    {
        try
        {
            
            Session session = getSession();
            Map<String, Object> m = agentDao.selectByPrimaryKey(id);
            String groupId = m.get("bill_group_id") + "";
            session.setAttribute("agentId", id);
            session.setAttribute("name", m.get("agentIdName"));
            session.setAttribute("nickname", m.get("nickname"));
            session.setAttribute("groupId", groupId);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/agentChannelRelList";
    }
    
    /**
     * @Title:getAllChannelPerson
     * @Description: 代理商新建修改获取所有渠道角色人员(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:13:22
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getAllChannelPerson()
    {
        
        return sysUserDao.getAllChannelPerson();
    }
    
    /**
     * @Title:getAgentRoleFlag
     * @Description: 判断可否修改代理商(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月6日 下午4:50:25
     * @param @param suId
     * @param @param id
     * @param @return
     * @return Map<String,Object> 返回类型
     * @throws
     */
    public Map<String, Object> getAgentRoleFlag(String suId, String id)
    {
        boolean roleFlag = false;
        boolean queryFlag = false;
        boolean tmallRole = false;
        
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> dataList = sysUserRoleDao.getHasSysRoleList(suId);
        for (Map<String, Object> map : dataList)
        {
            String srName = map.get("srName") + "";
            if ("渠道".equals(srName))
            {
                roleFlag = true;
            }
            
            if ("天猫".equals(srName))
            {
                tmallRole = true;
            }
        }
        if (roleFlag || tmallRole)
        {
            Map<String, Object> crtUserMap = agentDao.selectById(id);
            if (suId.equals(crtUserMap.get("create_user") + ""))
            {
                queryFlag = true;
            }
            else if (("6fc1e7e29f4142f48110bc6c5286d723".equals(crtUserMap.get("id") + "") || "81c2390df095475f86f0e9c6dff1cfd2".equals(crtUserMap.get("id")
                + ""))
                && tmallRole)
            {
                queryFlag = true;
            }
        }
        resultMap.put("roleFlag", roleFlag);
        resultMap.put("queryFlag", queryFlag);
        return resultMap;
    }
    
    public Map<String, Object> getAgentRoleFlagBySuId(String suId)
    {
        boolean roleFlag = false;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> agentMap = agentDao.selectByUserId(suId);
        if (null != agentMap)
        {
            roleFlag = true;
        }
        resultMap.put("roleFlag", roleFlag);
        return resultMap;
    }
    
    /**
     * 生成密钥
     * @param req
     * @return
     */
	public String selectKey(HttpServletRequest req) {
		Map<String, Object> map = getMaps(req);
		String agentId = (String) map.get("id");
		System.out.println("ID=" + agentId);
		Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
		String msg = "0";
		System.out.println(agent.get("app_key"));
		System.out.println(agent.containsKey("app_key"));
		try {
			if (agent.containsKey("app_key") == false && agent.get("app_key") == null) {
				Map<String, Object> rsa = RSAUtils.genKeyPair();
				agent.remove("appKey");
				agent.put("appKey", RSAUtils.getPublicKey(rsa));
				System.out.println(agent.get("id"));
				System.out.println(agentDao.updateByPrimaryKeySelective(agent));
				Map<String, Object> keyt = new HashMap<String, Object>();
				keyt.put("id", UUID.randomUUID().toString().replace("-", ""));
				keyt.put("agentId", agent.get("id"));// 代理商ID
				keyt.put("agentName", agent.get("name"));// 代理商name
				keyt.put("agentNickname", agent.get("nickname"));// 代理商nickName
				keyt.put("publickey", RSAUtils.getPublicKey(rsa));// 公钥
				keyt.put("privatekey", RSAUtils.getPrivateKey(rsa));// 私钥
				System.out.println(keyt);
				System.out.println(mpKeytDao.insertSelective(keyt));
				msg = RSAUtils.getPrivateKey(rsa);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * 跳转到设置代理商限额页面
	 * @return
	 */
	public String addBalance(String id){
		Map<String, Object> map = agentDao.selectById(id);
		Session session = getSession();
		session.setAttribute("agent", map);
		return "mp/agentTask";
	}
}
