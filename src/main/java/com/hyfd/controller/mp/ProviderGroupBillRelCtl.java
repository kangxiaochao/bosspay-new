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
import com.hyfd.service.mp.ProviderGroupBillRelSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderGroupBillRelCtl extends BaseController
{
    
    @Autowired
    ProviderGroupBillRelSer providerGroupBillRelSer;
    
    @GetMapping("providerGroupBillRelEditPage/{id}")
    public String providerGroupBillRelEditPage(@PathVariable("id") String id)
    {
        return providerGroupBillRelSer.providerGroupBillRelEditPage(id);
    }
    
    @PostMapping("providerGroupBillRel")
    public String providerGroupBillRelAddEdit(HttpServletRequest req)
    {
        return providerGroupBillRelSer.providerGroupBillRelEdit(req);
    }
    
    /**
     * 通道组详情列表数据
     * */
    @GetMapping("providerGroupBillRelList/{id}")
    @ResponseBody
    public String providerGroupBillRelList(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerGroupBillRelSer.providerGroupBillRelList(id, req);
    }
    
    @GetMapping("providerGroupBillRel")
    @ResponseBody
    public String providerGroupBillRel(HttpServletRequest req)
    {
        return providerGroupBillRelSer.providerGroupBillRel(req);
    }
    
    @GetMapping("getPhysicalByDispatcher/{providerId}")
    @ResponseBody
    public List<Map<String, Object>> getPhysicalByDispatcher(@PathVariable("providerId") String providerId)
    {
        return providerGroupBillRelSer.getPhysicalByDispatcher(providerId);
    }
    
    @GetMapping("getProvinceByDispatcher/{providerId}/{physicalId}")
    @ResponseBody
    public List<Map<String, Object>> getProvinceByDispatcher(@PathVariable("providerId") String providerId,
        @PathVariable("physicalId") String physicalId)
    {
        return providerGroupBillRelSer.getProvinceByDispatcher(providerId, physicalId);
    }
    
    @GetMapping("querySelectGroupByGroupId/{groupId}")
    @ResponseBody
    public List<Map<String, Object>> querySelectGroupByGroupId(@PathVariable("groupId") String groupId)
    {
        return providerGroupBillRelSer.querySelectGroupByGroupId(groupId);
    }
    
    @PostMapping("submitBillGroupRelBill")
    @ResponseBody
    public boolean submitBillGroupRelBill(HttpServletRequest req)
    {
        return providerGroupBillRelSer.submitBillGroupRelBill(req);
    }
}
