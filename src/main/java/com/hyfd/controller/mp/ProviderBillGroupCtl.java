package com.hyfd.controller.mp;

import java.util.Map;

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
import com.hyfd.service.mp.ProviderBillGroupSer;

/**
 * @功能描述： 流量通道组相关
 *
 * @作者：zhangpj @创建时间：2016年12月17日
 */
@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderBillGroupCtl extends BaseController
{
    
    @Autowired
    private ProviderBillGroupSer providerBillGroupSer;
    
    /**
     * 跳转到列表页面 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("providerBillGroupListPage")
    public String providerBillGroupListPage()
    {
        return "mp/providerBillGroupList";
    }
    
    /**
     * 获取列表流量 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("providerBillGroup")
    @ResponseBody
    public String providerBillGroupGet(HttpServletRequest req)
    {
        return providerBillGroupSer.providerBillGroupList(req);
    }
    
    /**
     * @功能描述： 不分页根据条件获取全部流量通道组列表流量
     *
     * @作者：zhangpj @创建时间：2016年12月17日
     * @param req
     * @return
     */
    @GetMapping("providerBillGroupAll")
    @ResponseBody
    public String providerBillGroupAllGet(HttpServletRequest req)
    {
        return providerBillGroupSer.providerBillGroupAllList(req);
    }
    
    /**
     * 流量通道组添加页面显示 只能使用get方式提交
     * 
     * @return
     */
    @GetMapping("providerBillGroupAddPage")
    public String providerBillGroupAddPage()
    {
        return "mp/providerBillGroupAdd";
    }
    
    /**
     * 创建流量通道组对象只能用post方式来提交
     * 
     * @param req
     * @return
     */
    @PostMapping("providerBillGroup")
    public String providerBillGroupPost(HttpServletRequest req)
    {
        return providerBillGroupSer.providerBillGroupAdd(req);
    }
    
    /**
     * 跳转到详情页面 要使用get方式提交
     * 
     * @return
     */
    @GetMapping("providerBillGroupDetailPage/{id}")
    public String providerBillGroupDetailPage(@PathVariable("id") String id)
    {
        return providerBillGroupSer.providerBillGroupDetailPage(id);
    }
    
    /**
     * 显示编辑页面请求路径中要包括需要修改的ID
     * 
     * @param id
     * @return
     */
    @GetMapping("providerBillGroupEditPage/{id}")
    public String providerBillGroupEditPage(@PathVariable("id") String id)
    {
        return providerBillGroupSer.providerBillGroupEditPage(id);
    }
    
    /**
     * 更新流量通道组信息 只能用put方式提交
     * 
     * @param id
     * @param req
     * @param res
     * @return
     */
    @PutMapping("providerBillGroup/{id}")
    @ResponseBody
    public String providerBillGroupPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerBillGroupSer.providerBillGroupEdit(req, id);
    }
    
    /**
     * 删除流量通道组方法 只能用delete请求需要在请求路径中添加要删除的流量通道组编号
     * 
     * @param id
     * @param req
     * @param res
     * @return
     */
    @DeleteMapping("providerBillGroup/{id}")
    @ResponseBody
    public String providerBillGroupDel(@PathVariable("id") String id)
    {
        return providerBillGroupSer.providerBillGroupDel(id);
    }
    
    @GetMapping("getBillGroupRoleFlag/{suId}/{id}")
    @ResponseBody
    public Map<String, Object> getBillGroupRoleFlag(@PathVariable("suId") String suId, @PathVariable("id") String id)
    {
        return providerBillGroupSer.getBillGroupRoleFlag(suId, id);
    }
}