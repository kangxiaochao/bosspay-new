package com.hyfd.controller.mp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderCtl extends BaseController
{
    
    @Autowired
    ProviderSer providerSer;
    
    @GetMapping("providerListPage")
    public String providerListPage()
    {
        return "mp/providerList";
    }
    
    @GetMapping("provider")
    @ResponseBody
    public String providerGet(HttpServletRequest req)
    {
        return providerSer.providerList(req);
    }
    
    @GetMapping("providerList")
    @ResponseBody
    public String providerListGet(HttpServletRequest req)
    {
        return providerSer.providerListGet(req);
    }
    
    @GetMapping("providerAddPage")
    public String providerAddPage()
    {
        return "mp/providerAdd";
    }
    
    @PostMapping("provider")
    public String providerPost(HttpServletRequest req)
    {
        return providerSer.providerAdd(req);
    }
    
    @GetMapping("providerEditPage/{id}")
    public String providerEditPage(@PathVariable("id") String id)
    {
        return providerSer.providerEditPage(id);
    }
    
    @PutMapping("provider/{id}")
    @ResponseBody
    public String providerPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerSer.providerEdit(req, id);
    }
    
    @DeleteMapping("provider/{id}")
    @ResponseBody
    public String providerDel(@PathVariable("id") String id)
    {
        return providerSer.providerDel(id);
    }
    
    /**
     * 上传运营商流量包数据Excel
     * 
     * @param id
     * @param mFile
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("commitProviderPkg/{id}")
    @ResponseBody
    public List<String> commitProviderPkg(@PathVariable("id") String id,
        @RequestParam(value = "file", required = false) MultipartFile mFile, HttpServletRequest req)
        throws Exception
    {
        List<String> list = providerSer.commitProviderPkg(id, mFile);
        return list;
    }
    
    @GetMapping("agentProviderList")
    @ResponseBody
    public String agentProviderList(HttpServletRequest req)
    {
        return providerSer.agentProviderList(req);
    }
}
