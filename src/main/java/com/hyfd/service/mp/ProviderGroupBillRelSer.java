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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderBillGroupDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderGroupBillRelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ProviderGroupBillRelSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    ProviderBillGroupDao providerBillGroupDao;
    
    @Autowired
    ProviderGroupBillRelDao providerGroupBillRelDao;
    
    @Autowired
    ProviderDao provider;
    
    @Autowired
    ProviderBillDispatcherDao providerBillDispatcherDao;
    
    public String providerGroupBillRelEditPage(String id)
    {
        try
        {
            Map<String, Object> providerBillGroup = providerBillGroupDao.selectByPrimaryKey(id);
            Session session = getSession();
            session.setAttribute("providerBillGroup", providerBillGroup);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerGroupBillRelEdit";
    }
    
    public String providerGroupBillRelEdit(HttpServletRequest req)
    {
        Map<String, Object> myBill = getMaps(req);
        String groupId = (String)myBill.get("groupId");
        String providerId = (String)myBill.get("providerId");
        try
        {
            boolean flag = false;
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            String name = (String)userInfoMap.get("suName");
            if (groupId != null && groupId != "" && providerId != null && providerId != "")
            {
                List<Map<String, Object>> list = providerGroupBillRelDao.selectAll(myBill);
                if (list.size() > 0)
                {
                    boolean i = providerGroupBillRelDao.deleteBygroupIdAndProviderId(myBill);
                    if (i)
                    {
                        myBill.put("name", name);
                        flag = providerGroupBillRelAdd(myBill);
                    }
                }
                else
                {
                    myBill.put("name", name);
                    flag = providerGroupBillRelAdd(myBill);
                }
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "更新成功" : "更新失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:providerBillGroupDetailPage/" + groupId;
    }
    
    public boolean providerGroupBillRelAdd(Map<String, Object> m)
    {
        boolean flag = false;
        try
        {
            String groupId = (String)m.get("groupId");
            String providerId = (String)m.get("providerId");
            String createUser = (String)m.get("name");
            m.remove("groupId");
            m.remove("providerId");
            m.remove("name");
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : m.entrySet())
            {
                String provinceCode = entry.getKey();
                map.put("groupId", groupId);
                map.put("providerId", providerId);
                map.put("provinceCode", provinceCode);
                map.put("createUser", createUser);
				providerGroupBillRelDao.insertSelective(map);
            }
            flag = true;
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return flag;
    }
    
    /**
     * 根据条件分页获取流量通道组通道列表并生成json
     * 
     * @param req
     * @return
     */
    public String providerGroupBillRelList(String groupId, HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            if (groupId != null && groupId != "" && !groupId.equals("undefined"))
            {
                String providerName = (String)m.get("providerName");
                String providerId = provider.getIdByName(providerName);
                if (providerId != null)
                {
                    m.put("providerId", providerId);
                }
                m.put("groupId", groupId);
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
                List<Map<String, Object>> dataList = providerGroupBillRelDao.selectAll(m);
                List<Map<String, Object>> List = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < dataList.size(); i++)
                {
                    Map<String, Object> map = dataList.get(i);
                    String pName = (String)map.get("providerName");
                    String pCode = (String)map.get("province_code");
                    String channelName = pName + "(" + pCode + ")";
                    map.put("channelName", channelName);
                    List.add(map);
                }
                String dataListJson = BaseJson.listToJson(List);
                sb.append(dataListJson);
                sb.append("}");
            }
            
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    public String providerGroupBillRel(HttpServletRequest req)
    {
        String str = "";
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            List<Map<String, Object>> dataList = providerGroupBillRelDao.selectAll(m);
            str = BaseJson.listToJson(dataList);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getCountCount(Map<String, Object> m)
    {
        int providerGroupBillRelCount = 0;
        try
        {
            providerGroupBillRelCount = providerGroupBillRelDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerGroupBillRelCount;
    }
    
    /**
     * @Title:getPhysicalByDispatcher
     * @Description: 根据运营商获取物理通道数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午11:18:31
     * @param @param providerId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getPhysicalByDispatcher(String providerId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("providerId", providerId);
        List<Map<String, Object>> physicalList = providerBillDispatcherDao.getPhysicalAll(map);
        return physicalList;
    }
    
    /**
     * @Title:getProvinceByDispatcher
     * @Description: 根据条件获取物理通道地区(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午11:19:08
     * @param @param providerId
     * @param @param physicalId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getProvinceByDispatcher(String providerId, String physicalId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("providerId", providerId);
        map.put("physicalId", physicalId);
        List<Map<String, Object>> physicalList = providerBillDispatcherDao.getProvinceAll(map);
        return physicalList;
    }
    
    /**
     * @Title:querySelectGroupByGroupId
     * @Description: 根据条件查询已存在的数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午11:19:40
     * @param @param groupId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> querySelectGroupByGroupId(String groupId)
    {
        List<Map<String, Object>> physicalList = providerGroupBillRelDao.querySelectGroupByGroupId(groupId);
        return physicalList;
    }
    
    /**
     * @Title:submitBillGroupRelBill
     * @Description: 提交保存流量通道组配置数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午11:20:03
     * @param @param groupId
     * @param @param req
     * @param @return
     * @return boolean 返回类型
     * @throws
     */
    @SuppressWarnings("unchecked")
	public boolean submitBillGroupRelBill(HttpServletRequest req)
    {
        // Enumeration<String> e = req.getParameterNames();
        Map<String, Object> agentMap = getMaps(req);
        String agentChannel = (String)agentMap.get("dataMap");// 获取前台组装数据
        String groupId = (String)agentMap.get("groupId");
        Map<String, Object> userInfoMap = getUser();
        String suId = userInfoMap.get("suId") + "";
        Map<String, Object> groupChannelJson = JSON.parseObject(agentChannel);// 将前台组装数据转为map
        // 删除之前的数据
        providerGroupBillRelDao.deleteByGroupId(groupId);
        boolean flag = false;
        if (groupChannelJson == null || groupChannelJson.size() <= 0)
        {
            flag = true;
        }
        // 遍历map
        for (Map.Entry<String, Object> provider : groupChannelJson.entrySet())
        {
            Map<String, Object> insMap = new HashMap<String, Object>();
            String providerId = provider.getKey();// 获取key值，运营商ID
			Map<String, Object> physicalMap = (Map<String, Object>)provider.getValue();// 获取value,二级数据，物理通道
            // 遍历二级数据
            for (Map.Entry<String, Object> physical : physicalMap.entrySet())
            {
                String physicalId = physical.getKey();// 获取key值，物理通道ID
                JSONArray province = JSON.parseArray(physical.getValue().toString());// 得到三级数据，物理通道可充值的地区
                for (int i = 0; i < province.size(); i++)
                {
                    String provinceCode = province.get(i).toString(); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    insMap.put("groupId", groupId);
                    insMap.put("providerId", providerId);
                    insMap.put("physicalId", physicalId);
                    insMap.put("provinceCode", provinceCode);
                    insMap.put("createUser", suId);
                    insMap.put("updataUser", suId);
                    int result = providerGroupBillRelDao.insertBillGroupRel(insMap);
                    if (result == 1)
                    {
                        flag = true;
                    }
                    else
                    {
                        flag = false;
                    }
                }
            }
        }
        return flag;
    }
}
