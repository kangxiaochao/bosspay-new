package com.hyfd.service.mp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ProviderPhysicalChannelSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderBillDispatcherDao providerBillDispatcherDao;
    
    public int getProviderPhysicalChannelCount(Map<String, Object> m)
    {
        int providerCount = 0;
        try
        {
            providerCount = providerPhysicalChannelDao.getProviderPhysicalChannelCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerCount;
    }
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getCountCount(Map<String, Object> m)
    {
        int providerBillDispatcherCount = 0;
        try
        {
            providerBillDispatcherCount = providerBillDispatcherDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerBillDispatcherCount;
    }
    
    public String providerPhysicalChannelList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getProviderPhysicalChannelCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = providerPhysicalChannelDao.getProviderPhysicalChannelList(m);
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
    
    public String physicalList(HttpServletRequest req)
    {
        List<Map<String, Object>> billList = providerPhysicalChannelDao.getPhysicalList();
        String billListJson = BaseJson.listToJson(billList);
        
        return billListJson;
    }
    
    // 获取所有运营商物理通道
    public String providerPhysicalChannelAllList(HttpServletRequest req)
    {
        String billListJson = "";
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            List<Map<String, Object>> billList = providerPhysicalChannelDao.getProviderPhysicalChannelList(m);
            billListJson = BaseJson.listToJson(billList);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return billListJson;
    }
    
    public String providerPhysicalChannelAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("create_user", userInfoMap.get("suId"));// 放入创建用户
            
            int rows = providerPhysicalChannelDao.providerPhysicalChannelAdd(myBill);
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
        return "redirect:/providerPhysicalChannelListPage";
    }
    
    public String providerPhysicalChannelEditPage(String id)
    {
        try
        {
            Map<String, Object> providerPhysicalChannel = getProviderPhysicalChannelById(id);
            Session session = getSession();
            session.setAttribute("providerPhysicalChannel", providerPhysicalChannel);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerPhysicalChannelEdit";
    }
    
    public Map<String, Object> getProviderPhysicalChannelById(String id)
    {
        Map<String, Object> m = new HashMap<String, Object>();
        try
        {
            m = providerPhysicalChannelDao.getProviderPhysicalChannelById(id);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    /**
     * @功能描述： 获取还未创建物理运营商通道的运营商列表
     *
     * @作者：zhangpj @创建时间：2017年7月27日
     * @param id
     * @return
     */
    public String getSurplusProviderList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        
        Map<String, Object> m = getMaps(req); // 封装前台参数为map
        
        List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
        try
        {
            billList = providerPhysicalChannelDao.getProviderList(m);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
    }
    
    public String providerPhysicalChannelEdit(HttpServletRequest req, String id)
    {
        
        try
        {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("update_user", userInfoMap.get("suId"));// 放入创建用户
            
            if ("".equals(myBill.get("priority")))
            {
                myBill.put("priority", 0);
            }
            
            int rows = providerPhysicalChannelDao.providerPhysicalChannelEdit(myBill);
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
        return "providerPhysicalChannelListPage";
    }
    
    public String providerPhysicalChannelDel(String id)
    {
        
        try
        {
            boolean flag = false;
            int rows = providerPhysicalChannelDao.providerPhysicalChannelDel(id);
            
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
        return "providerPhysicalChannelListPage";
    }
    
    public String providerPhysicalChannelDetail(String id)
    {
        try
        {
            Map<String, Object> m = providerPhysicalChannelDao.getProviderPhysicalChannelById(id);
            Session session = getSession();
            session.setAttribute("providerPhysicalChannel", m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/providerPhysicalChannelDetailPage";
    }
    
    /**
     * <h5>功能描述:</h5> 跳转到运营商物理通道流量折扣列表页面
     *
     * @param req
     * @param id
     * @return
     *
     * @作者：zhangpj @创建时间：2017年3月30日
     */
    public String providerPhysicalChannelDiscountViewPage(HttpServletRequest req, String id)
    {
        Map<String, Object> m = providerPhysicalChannelDao.getProviderPhysicalChannelById(id);
        req.setAttribute("providerPhysicalChannel", m);
        
        return "mp/providerPhysicalChannelDiscountViewList";
    }
    
    public String providerBillDispatcherList(String id)
    {
        try
        {
            Session session = getSession();
            session.setAttribute("physicalId", id);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerBillDispatcherListPage";
    }
    
    public String providerBillDispatcherListBill(String physicalId, HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            if (physicalId != null && physicalId != "" && !physicalId.equals("undefined"))
            {
                // String providerName = (String)m.get("providerName");
                // String providerId = provider.getIdByName(providerName);
                // if (providerId != null)
                // {
                // m.put("providerId", providerId);
                // }
                m.put("physicalId", physicalId);
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
                List<Map<String, Object>> billList = providerBillDispatcherDao.getDispatcherByPhysicalId(m);
//                List<Map<String, Object>> List = new ArrayList<Map<String, Object>>();
//                for (int i = 0; i < billList.size(); i++)
//                {
//                    Map<String, Object> map = billList.get(i);
//                    String pName = (String)map.get("name");
//                    String pCode = (String)map.get("provinceCode");
//                    String channelName = pName + "(" + pCode + ")";
//                    map.put("channelName", channelName);
//                    List.add(map);
//                }
                String billListJson = BaseJson.listToJson(billList);
                sb.append(billListJson);
                sb.append("}");
            }
            
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
}
