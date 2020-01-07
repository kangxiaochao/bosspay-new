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
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentChannelRelDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.ProviderGroupBillRelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class AgentChannelRelSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    AgentChannelRelDao agentChannelRelDao;
    
    @Autowired
    ProviderGroupBillRelDao providerGroupBillRelDao;
    
    @Autowired
    AgentDao agentDao;
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getCountCount(Map<String, Object> m)
    {
        int agentChannelRelCount = 0;
        try
        {
            agentChannelRelCount = agentChannelRelDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return agentChannelRelCount;
    }
    
    /**
     * @Title:agentChannelRelList
     * @Description: 根据代理商获取对应的特惠通道数据列表(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:19:24
     * @param @param agentId
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String agentChannelRelList(String agentId, HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            if (agentId != null && agentId != "" && !agentId.equals("undefined"))
            {
                m.put("agentId", agentId);
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
                List<Map<String, Object>> billList = agentChannelRelDao.getAgentChannelRelByAgentId(m);
                List<Map<String, Object>> List = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < billList.size(); i++)
                {
                    Map<String, Object> map = billList.get(i);
                    String pName = (String)map.get("providerName");
                    String pCode = (String)map.get("province_code");
                    String channelName = pName + "(" + pCode + ")";
                    map.put("channelName", channelName);
                    List.add(map);
                }
                String billListJson = BaseJson.listToJson(List);
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
    
    /**
     * @Title:settingAgentChannelRelByAgentId
     * @Description: 根据代理商和流量分组ID得到可用的运营商(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:20:05
     * @param @param agentId
     * @param @param groupId
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String settingAgentChannelRelByAgentId(String agentId, String groupId)
    {
        try
        {
            List<Map<String, Object>> providerList = providerGroupBillRelDao.getProviderIdByGroupId(groupId);
            Map<String, Object> agentMap = agentDao.selectByPrimaryKey(agentId);
            Session session = getSession();
            session.setAttribute("providerList", providerList);
            session.setAttribute("agentMap", agentMap);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/agentChannelRelEdit";
    }
    
    /**
     * @Title:getPhysicalIdByProviderId
     * @Description: 根据条件从流量通道组详情表中获取可充值的通道(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:21:16
     * @param @param groupId
     * @param @param providerId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getPhysicalIdByProviderId(String groupId, String providerId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("groupId", groupId);
        map.put("providerId", providerId);
        return providerGroupBillRelDao.getPhysicalIdByProviderId(map);
        
    }
    
    /**
     * @Title:getProviderCodeByPhysicalId
     * @Description: 根据条件从流量通道组详情表中获取可充值的通道地区(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:22:16
     * @param @param groupId
     * @param @param providerId
     * @param @param physicalId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> getProvinceCodeByPhysicalId(String groupId, String providerId, String physicalId)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("groupId", groupId);
        map.put("providerId", providerId);
        map.put("physicalId", physicalId);
        return providerGroupBillRelDao.getProvinceCodeByPhysicalId(map);
        
    }
    
    /**
     * @Title:queryAgentChannelRelByAgentId
     * @Description: 查询代理商已配置的通道数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:23:06
     * @param @param agentId
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    public List<Map<String, Object>> queryAgentChannelRelByAgentId(String agentId)
    {
        
        return agentChannelRelDao.queryAgentChannelRelByAgentId(agentId);
    }
    
    /**
     * @Title:submitAgentChanneRelBill
     * @Description: 特惠通道配置数据提交(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:24:01
     * @param @param agentId
     * @param @param req
     * @param @return
     * @return boolean 返回类型
     * @throws
     */
    public boolean submitAgentChanneRelBill(HttpServletRequest req)
    {
        // Enumeration<String> e = req.getParameterNames();
        Map<String, Object> agentMap = getMaps(req);
        Map<String, Object> userInfoMap = getUser();
        String suId = userInfoMap.get("suId") + "";
        // 获取前台组装的数据
        String agentChannel = (String)agentMap.get("billMap");
        Map<String, Object> agentChannelJson = JSON.parseObject(agentChannel);// 字符串转map
        // 删除以前的特惠通道
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("suId", suId);
        conditionMap.put("agentId", agentMap.get("agentId"));
        int delResult = agentChannelRelDao.deleteByAgentId(conditionMap);
        boolean flag = false;
        if (agentChannelJson == null || agentChannelJson.size() <= 0)
        {
            flag = true;
        }
        // 遍历前台map数据
        for (Map.Entry<String, Object> provider : agentChannelJson.entrySet())
        {
            Map<String, Object> insMap = new HashMap<String, Object>();
            // 拿到key值，运营商ID
            String providerId = provider.getKey();
            // 获取二级数据，物理通道数据
            Map<String, Object> physicalMap = (Map<String, Object>)provider.getValue();
            // 遍历二级map
            for (Map.Entry<String, Object> physical : physicalMap.entrySet())
            {
                
                String physicalId = physical.getKey();// 获取key值，物理通道ID
                JSONArray province = JSON.parseArray(physical.getValue().toString());// 获取三级数据,物理通道对应的地区
                // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                for (int i = 0; i < province.size(); i++)
                {
                    String provinceCode = province.get(i).toString(); // 获取地区
                    insMap.put("agentId", agentMap.get("agentId"));
                    insMap.put("providerId", providerId);
                    insMap.put("physicalId", physicalId);
                    insMap.put("provinceCode", provinceCode);
                    insMap.put("flag", "1");
                    insMap.put("createUser", suId);
                    insMap.put("upbillUser", suId);
                    // 将数据插入特惠通道
                    int result = agentChannelRelDao.insertAgentChannelRel(insMap);
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
