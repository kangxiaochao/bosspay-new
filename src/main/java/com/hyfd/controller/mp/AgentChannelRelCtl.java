package com.hyfd.controller.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentChannelRelSer;

@Controller
public class AgentChannelRelCtl extends BaseController
{
    
    @Autowired
    private AgentChannelRelSer agentChannelRelSer;
    
    /**
     * @Title:agentChannelRelList
     * @Description: 根据代理商获取对应的特惠通道数据列表(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:19:24
     * @param @param id
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("agentChannelRelList/{id}")
    @ResponseBody
    public String agentChannelRelList(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentChannelRelSer.agentChannelRelList(id, req);
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
    @GetMapping("settingAgentChannelRelByAgentId/{agentId}/{groupId}")
    public String settingAgentChannelRelByAgentId(@PathVariable("agentId") String agentId,
        @PathVariable("groupId") String groupId)
    {
        return agentChannelRelSer.settingAgentChannelRelByAgentId(agentId, groupId);
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
    @GetMapping("getPhysicalIdByProviderId/{groupId}/{providerId}")
    @ResponseBody
    public List<Map<String, Object>> getPhysicalIdByProviderId(@PathVariable("groupId") String groupId,
        @PathVariable("providerId") String providerId)
    {
        
        return agentChannelRelSer.getPhysicalIdByProviderId(groupId, providerId);
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
    @GetMapping("getProvinceCodeByPhysicalId/{groupId}/{providerId}/{physicalId}")
    @ResponseBody
    public List<Map<String, Object>> getProvinceCodeByPhysicalId(@PathVariable("groupId") String groupId,
        @PathVariable("providerId") String providerId, @PathVariable("physicalId") String physicalId)
    {
        
        return agentChannelRelSer.getProvinceCodeByPhysicalId(groupId, providerId, physicalId);
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
    @GetMapping("queryAgentChannelRelByAgentId/{agentId}")
    @ResponseBody
    public List<Map<String, Object>> queryAgentChannelRelByAgentId(@PathVariable("agentId") String agentId)
    {
        
        return agentChannelRelSer.queryAgentChannelRelByAgentId(agentId);
    }
    
    /**
     * @Title:submitAgentChanneRelBill
     * @Description: 特惠通道配置数据提交(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:24:01
     * @param @param id
     * @param @param req
     * @param @return
     * @return boolean 返回类型
     * @throws
     */
    @PostMapping("submitAgentChanneRelBill")
    @ResponseBody
    public boolean submitAgentChanneRelBill(HttpServletRequest req)
    {
        return agentChannelRelSer.submitAgentChanneRelBill( req);
    }
    
}