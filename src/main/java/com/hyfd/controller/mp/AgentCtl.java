package com.hyfd.controller.mp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class AgentCtl extends BaseController
{
    
    @Autowired
    private AgentSer agentSer;
    
    /**
     * 跳转到列表页面 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentListPage")
    public String agentListPage()
    {
        return "mp/agentList";
    }
    
    /**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agent")
    @ResponseBody
    public String agentGet(HttpServletRequest req)
    {
        return agentSer.agentList(req);
    }
    
    @GetMapping("agentDiscountList")
    @ResponseBody
    public String ageagentDiscountListntGet(HttpServletRequest req)
    {
        return agentSer.agentDiscountList(req);
    }
    
    /**
     * @功能描述： 不分页根据条件获取全部代理商列表数据
     *
     * @作者：zhangpj @创建时间：2016年12月16日
     * @param req
     * @return
     */
    @GetMapping("agentAll")
    @ResponseBody
    public String agentAllGet(HttpServletRequest req)
    {
        return agentSer.agentAllList(req);
    }
    
    /**
     * 代理商添加页面显示 只能使用get方式提交
     * 
     * @return
     */
    @GetMapping("agentAddPage/{userId}")
    public String agentAddPage(@PathVariable("userId") String userId)
    {
        
        return agentSer.agentAddPage(userId);
    }
    
    /**
     * 创建代理商对象只能用post方式来提交
     * 
     * @param req
     * @return
     */
    @PostMapping("agent")
    public String agentPost(HttpServletRequest req)
    {
        return agentSer.agentAdd(req);
    }
    
    /**
     * 跳转到详情页面 要使用get方式提交
     * 
     * @return
     */
    @GetMapping("agentDetail")
    public String agentDetail()
    {
        return "mp/agentDetail";
    }
    
    /**
     * 显示详单页面要使用get方法并要在请求路径中传入代理商编号数据
     * 
     * @param id
     * @return
     */
    @GetMapping("agentDetail/{id}")
    public String agentDetail(@PathVariable("id") String id)
    {
        return agentSer.getAgentDetail(id);
    }
    
    /**
     * 显示编辑页面请求路径中要包括需要修改的ID
     * 
     * @param id
     * @return
     */
    @GetMapping("agentEditPage/{id}/{userId}")
    public String agentEditPage(@PathVariable("id") String id, @PathVariable("userId") String userId)
    {
        return agentSer.agentEditPage(id, userId);
    }
    
    /**
     * 显示密钥设置页面请求路径中要包括需要修改的ID
     * 
     * @param id
     * @return
     */
    @GetMapping("agentKeyEditPage/{id}")
    public String agentKeyEditPage(@PathVariable("id") String id)
    {
        return agentSer.agentKeyEditPage(id);
    }
    
    /**
     * 更新代理商信息 只能用put方式提交
     * 
     * @param id
     * @param req
     * @param res
     * @return
     */
    @PutMapping("agent/{id}")
    @ResponseBody
    public String agentPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentSer.agentEdit(req, id);
    }
    
    /**
     * @功能描述： 设置代理商密钥信息 只能用put方式提交
     *
     * @作者：zhangpj @创建时间：2016年12月27日
     * @param id
     * @param req
     * @return
     */
    @PutMapping("agentKey/{id}")
    @ResponseBody
    public String agentKeyPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentSer.agentKeyEdit(req, id);
    }
    
    /**
     * 删除代理商方法 只能用delete请求需要在请求路径中添加要删除的代理商编号
     * 
     * @param id
     * @param req
     * @param res
     * @return
     */
    @DeleteMapping("agent/{id}")
    @ResponseBody
    public String agentDel(@PathVariable("id") String id)
    {
        return agentSer.agentDel(id);
    }
    
    /**
     * 检测代理商编号是否重复
     * */
    @GetMapping("agentNameCheck")
    @ResponseBody
    public String agentNameCheck(HttpServletRequest req)
    {
        return agentSer.agentNameCheck(req);
    }
    
    /**
     * 查询代理商可用流量通道
     * */
    @GetMapping("agentBillGroupRelGet")
    @ResponseBody
    public String agentBillGroupRelGet(HttpServletRequest req)
    {
        return agentSer.agentBillGroupRelGet(req);
    }
    
    /**
     * @功能描述： 获取未和代理商绑定的用户信息
     *
     * @作者：zhangpj @创建时间：2016年12月20日
     * @return
     */
    // @GetMapping("sysUsers")
    // @ResponseBody
    // public String sysUsers()
    // {
    // return agentSer.sysUsers();
    // }
    
    /**
     * @Title:agentChannelRelListPage
     * @Description: 获取代理商特惠通道列表页(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年4月20日 下午2:42:56
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("agentChannelRelListPage/{id}")
    public String agentChannelRelListPage(@PathVariable("id") String id)
    {
        return agentSer.agentChannelRelListPage(id);
    }
    
    /**
     * @Title:getAllChannelPerson
     * @Description: 代理商新建修改获取所有渠道角色人员(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年5月22日 上午10:11:46
     * @param @return
     * @return List<Map<String,Object>> 返回类型
     * @throws
     */
    @GetMapping("getAllChannelPerson")
    @ResponseBody
    public List<Map<String, Object>> getAllChannelPerson()
    {
        return agentSer.getAllChannelPerson();
    }
    
    @GetMapping("getAgentRoleFlag/{suId}/{id}")
    @ResponseBody
    public Map<String, Object> getAgentRoleFlag(@PathVariable("suId") String suId, @PathVariable("id") String id)
    {
        return agentSer.getAgentRoleFlag(suId, id);
    }
    
    @GetMapping("getAgentRoleFlagBySuId/{suId}")
    @ResponseBody
    public Map<String, Object> getAgentRoleFlagBySuId(@PathVariable("suId") String suId)
    {
        return agentSer.getAgentRoleFlagBySuId(suId);
    }
    
    /**
     * 为代理商 生成密钥
     * @param req
     * @param rson
     */
    @GetMapping("validate")
    public void selectKey(HttpServletRequest req,HttpServletResponse rson){
		try {
			rson.getWriter().write(agentSer.selectKey(req));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 为代理商设置限额
     * @param id
     * @return
     */
    @GetMapping("addBalance/{id}")
    public String addBalance(@PathVariable("id")String id){
    	
    	return agentSer.addBalance(id);
    }
    
    /**
     * 查询所有代理商
     * @param req
     * @return
     */
    @GetMapping("selectAgentList")
    @ResponseBody
    public String selectAgent(HttpServletRequest req) {
    	
    	return agentSer.agentAllList(req);
    }
}