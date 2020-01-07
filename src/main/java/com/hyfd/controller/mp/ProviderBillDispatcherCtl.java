package com.hyfd.controller.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderBillDispatcherSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderBillDispatcherCtl extends BaseController
{
    
    @Autowired
    ProviderBillDispatcherSer providerBillDispatcherSer;
    
    @GetMapping("providerBillDispatcherEditPage/{id}")
    public String providerBillDispatcherEditPage(@PathVariable("id") String id)
    {
        return providerBillDispatcherSer.providerBillDispatcherEditPage(id);
    }
    
    @GetMapping("providerBillDispatcherByProviderId")
    @ResponseBody
    public String providerBillDispatcherByProviderId(HttpServletRequest req)
    {
        return providerBillDispatcherSer.getProviderBillDispatcherByProviderId(req);
    }
    
    @PutMapping("providerBillDispatcher/{id}")
    @ResponseBody
    public String providerBillDispatcherEdit(HttpServletRequest req, @PathVariable("id") String providerId)
    {
        return providerBillDispatcherSer.providerBillDispatcherPut(req, providerId);
    }
    
    @GetMapping("providerPhysicalChannelDispatcherEdit/{id}")
    public String providerPhysicalChannelDispatcherEdit(@PathVariable("id") String id)
    {
        return providerBillDispatcherSer.providerPhysicalChannelDispatcherEdit(id);
    }
    
    @GetMapping("querySelectDispatcherByPhysicalId/{physicalId}")
    @ResponseBody
    public List<Map<String, Object>> querySelectDispatcherByPhysicalId(@PathVariable("physicalId") String physicalId)
    {
        return providerBillDispatcherSer.querySelectDispatcherByPhysicalId(physicalId);
    }
    
    @GetMapping("submitPoviderDispatcherBill/{physicalId}")
    @ResponseBody
    public boolean submitPoviderDispatcherBill(@PathVariable("physicalId") String physicalId, HttpServletRequest req)
    {
        return providerBillDispatcherSer.submitPoviderDispatcherBill(physicalId, req);
    }
    
    @GetMapping("providerBillDispatcherToProvinceCode")
    @ResponseBody
    public String providerBillDispatcherToProvinceCode(HttpServletRequest req)
    {
        return providerBillDispatcherSer.getProviderBillDispatcherToProvinceCode(req);
    }
    
    @GetMapping("changeDispatcherStatus")
    @ResponseBody
    public String changeDispatcherStatus(HttpServletRequest req)
    {
        return providerBillDispatcherSer.changeDispatcherStatus(req);
    }
}
