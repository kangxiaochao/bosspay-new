package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.ProviderBillGroupDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;

/**
 * @功能描述： 流量通道组相关业务
 *
 * @作者：zhangpj @创建时间：2016年12月17日
 */
@Service
@Transactional
public class ProviderBillGroupSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private ProviderBillGroupDao providerBillGroupDao;
    
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    
    /**
     * 根据主键获取记录
     * 
     * @param id
     * @return
     */
    public Map<String, Object> getProviderBillGroupById(String id)
    {
        Map<String, Object> m = new HashMap<String, Object>();
        try
        {
            m = providerBillGroupDao.selectByPrimaryKey(id);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getCountCount(Map<String, Object> m)
    {
        int providerBillGroupCount = 0;
        try
        {
            providerBillGroupCount = providerBillGroupDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerBillGroupCount;
    }
    
    /**
     * 根据条件分页获取流量通道组列表流量并生成json
     * 
     * @param req
     * @return
     */
    public String providerBillGroupList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getCountCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = providerBillGroupDao.selectAll(m);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    /**
     * @功能描述： 根据条件获取全部流量通道组流量
     *
     * @作者：zhangpj @创建时间：2016年12月17日
     * @return
     */
    public String providerBillGroupAllList(HttpServletRequest req)
    {
        String str = null;
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            List<Map<String, Object>> billList = providerBillGroupDao.selectAll(m);
            str = JSONObject.toJSONString(billList);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    public String providerBillGroupDetailPage(String groupId)
    {
        try
        {
            Session session = getSession();
            Map<String, Object> groupMap = providerBillGroupDao.selectByPrimaryKey(groupId);
            session.setAttribute("groupMap", groupMap);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerGroupBillRelList";
    }
    
    /**
     * 添加流量通道组信息
     * 
     * @param req
     * @return
     */
    public String providerBillGroupAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("create_user", userInfoMap.get("suId"));// 放入创建用户
            
            int rows = providerBillGroupDao.insert(myBill);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/providerBillGroupListPage";
    }
    
    /**
     * 修改流量通道组信息
     * 
     * @param req
     * @param id
     * @return
     */
    public String providerBillGroupEdit(HttpServletRequest req, String id)
    {
        
        try
        {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("update_user", userInfoMap.get("suName"));// 放入创建用户
            
            int rows = providerBillGroupDao.updateByPrimaryKeySelective(myBill);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "providerBillGroupListPage";
    }
    
    /**
     * 跳转到流量通道组编辑页
     * 
     * @param id
     * @return
     */
    public String providerBillGroupEditPage(String id)
    {
        try
        {
            Map<String, Object> providerBillGroup = getProviderBillGroupById(id);
            Session session = getSession();
            session.setAttribute("providerBillGroup", providerBillGroup);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerBillGroupEdit";
    }
    
    /**
     * 删除流量通道组信息
     * 
     * @param id
     * @return
     */
    public String providerBillGroupDel(String id)
    {
        
        try
        {
            boolean flag = false;
            int rows = providerBillGroupDao.deleteByPrimaryKey(id);
            if (rows > 0)
            {
                flag = true;
            }
            
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
            
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "providerBillGroupListPage";
    }
    
    public Map<String, Object> getBillGroupRoleFlag(String suId, String id)
    {
        boolean roleFlag = false;
        boolean queryFlag = false;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> billList = sysUserRoleDao.getHasSysRoleList(suId);
        for (Map<String, Object> map : billList)
        {
            String srName = map.get("srName") + "";
            if ("运营".equals(srName) || "渠道".equals(srName) || "天猫".equals(srName))
            {
                roleFlag = true;
            }
            
        }
        if (roleFlag)
        {
            String crtUser = providerBillGroupDao.getCrtUserById(id);
            if (suId.equals(crtUser))
            {
                queryFlag = true;
            }
        }
        resultMap.put("roleFlag", roleFlag);
        resultMap.put("queryFlag", queryFlag);
        return resultMap;
    }
    
}
