package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderPhysicalChannelSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderPhysicalChannelCtl extends BaseController
{
    
    @Autowired
    ProviderPhysicalChannelSer providerPhysicalChannelSer;
    
    @GetMapping("providerPhysicalChannelListPage")
    public String providerPhysicalChannelListPage()
    {
        return "mp/providerPhysicalChannelList";
    }
    
    @GetMapping("providerPhysicalChannel")
    @ResponseBody
    public String providerPhysicalChannelGet(HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelList(req);
    }
    
    @GetMapping("physicalList")
    @ResponseBody
    public String physicalList(HttpServletRequest req)
    {
        return providerPhysicalChannelSer.physicalList(req);
    }
    
    @GetMapping("providerPhysicalChannelAll")
    @ResponseBody
    public String providerPhysicalChannelAllGet(HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelAllList(req);
    }
    
    @GetMapping("providerPhysicalChannelAddPage")
    public String providerPhysicalChannelAddPage()
    {
        return "mp/providerPhysicalChannelAdd";
    }
    
    @PostMapping("providerPhysicalChannel")
    public String providerPost(HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelAdd(req);
    }
    
    @GetMapping("providerPhysicalChannelEditPage/{id}")
    public String providerPhysicalChannelEditPage(@PathVariable("id") String id)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelEditPage(id);
    }
    
    @GetMapping("providerPhysicalChannelDetailPage")
    public String providerPhysicalChannelDetailPage()
    {
        return "mp/providerPhysicalChannelDetail";
    }
    
    @GetMapping("providerPhysicalChannel/{id}")
    public String providerPhysicalChannelDetailGet(@PathVariable("id") String id)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelDetail(id);
    }
    
    @PutMapping("providerPhysicalChannel/{id}")
    @ResponseBody
    public String providerPhysicalChannelPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelEdit(req, id);
    }
    
    @DeleteMapping("providerPhysicalChannel/{id}")
    @ResponseBody
    public String providerPhysicalChannelDel(@PathVariable("id") String id)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelDel(id);
    }
    
    @GetMapping("providerPhysicalChannelDiscountViewPage/{id}")
    public String providerPhysicalChannelDiscountViewPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerPhysicalChannelDiscountViewPage(req, id);
    }
    
    @GetMapping("providerBillDispatcherList/{id}")
    public String providerBillDispatcherList(@PathVariable("id") String id)
    {
        return providerPhysicalChannelSer.providerBillDispatcherList(id);
    }
    
    @GetMapping("providerBillDispatcherListBill/{id}")
    @ResponseBody
    public String providerBillDispatcherListBill(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerPhysicalChannelSer.providerBillDispatcherListBill(id, req);
    }
    
    @GetMapping("surplusProvider")
    @ResponseBody
    public String surplusProviderGet(HttpServletRequest req)
    {
        return providerPhysicalChannelSer.getSurplusProviderList(req);
    }
}
