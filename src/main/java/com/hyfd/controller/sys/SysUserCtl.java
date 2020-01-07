package com.hyfd.controller.sys;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.sys.SysUserSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class SysUserCtl extends BaseController
{
    
    @Autowired
    private SysUserSer sysUserSer;
    
    /**
     * 跳转到列表页面 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("sysUserListPage")
    public String sysUserListPage()
    {
        return "system/sysUserList";
    }
    
    /**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("sysUser")
    @ResponseBody
    public String sysUserGet(HttpServletRequest req)
    {
        return sysUserSer.sysUserList(req);
    }
    
    /**
     * @功能描述： 根据用户名查询用户数量
     *
     * @作者：zhangpj @创建时间：2017年4月20日
     * @param req
     * @return
     */
    @GetMapping("sysUserName")
    @ResponseBody
    public String sysUserNameGet(HttpServletRequest req)
    {
        return String.valueOf(sysUserSer.getSysUserCount(req));
    }
    
    @GetMapping("sysUserCount")
    @ResponseBody
    public String sysUserCountGet(HttpServletRequest req)
    {
    	return String.valueOf(sysUserSer.sysUserCountBySuName(req));
    }
    
    /**
     * 用户添加页面显示 只能使用get方式提交
     * 
     * @return
     */
    @GetMapping("sysUserAddPage")
    public String sysUserAddPage()
    {
        return "system/sysUserAdd";
    }
    
    /**
     * 创建用户对象只能用post方式来提交
     * 
     * @param req
     * @return
     */
    @PostMapping("sysUser")
    public String sysUserPost(HttpServletRequest req)
    {
        return sysUserSer.sysUserAdd(req);
    }
    
    /**
     * 跳转到详情页面 要使用get方式提交
     * 
     * @return
     */
    @GetMapping("sysUserDetailPage")
    public String sysUserDetailPage()
    {
        return "system/sysUserDetail";
    }
    
    /**
     * 显示详单页面要使用get方法并要在请求路径中传入用户编号数据
     * 
     * @param suId
     * @return
     */
    @GetMapping("sysUser/{suId}")
    public String sysUserDetailGet(@PathVariable("suId") String suId)
    {
        return sysUserSer.sysUserDetail(suId);
    }
    
    /**
     * 显示编辑页面请求路径中要包括需要修改的ID
     * 
     * @param suId
     * @return
     */
    @GetMapping("sysUserEditPage/{suId}")
    public String sysUserEditPage(@PathVariable("suId") String suId)
    {
        return sysUserSer.sysUserEditPage(suId);
    }
    
    /**
     * 更新用户信息 只能用put方式提交
     * 
     * @param suId
     * @param req
     * @param res
     * @return
     */
    @PutMapping("sysUser/{suId}")
    @ResponseBody
    public String sysUserPut(@PathVariable("suId") String suId, HttpServletRequest req)
    {
        return sysUserSer.sysUserEdit(req, suId);
    }
    
    /**
     * 跳转到用户密码修改页
     * 
     * @return
     */
    @GetMapping("sysUserChangePassPage/{suId}")
    public String sysUserChangePassPage(@PathVariable("suId") String suId)
    {
        return sysUserSer.sysUserChangePassPage(suId);
    }
    
    /**
     * 跳转到用户密码修改页
     * 
     * @return
     */
    @GetMapping("userChangePassPage/{suId}")
    public String userChangePassPage(@PathVariable("suId") String suId)
    {
        return sysUserSer.userChangePassPage(suId);
    }
    
    /**
     * 用户密码修改
     * 
     * @param suId
     * @param req
     * @param res
     * @return
     */
    @PatchMapping("sysUser/{suId}")
    @ResponseBody
    public String sysUserPatch(@PathVariable("suId") String suId, HttpServletRequest req)
    {
        return sysUserSer.sysUserChangePass(req, suId);
    }
    
    /**
     * 用户密码修改
     * 
     * @param suId
     * @param req
     * @param res
     * @return
     */
    @PatchMapping("updateUserPass/{suId}")
    @ResponseBody
    public String updateUserPass(@PathVariable("suId") String suId, HttpServletRequest req)
    {
        return sysUserSer.updateUserPass(req, suId);
    }
    
    /**
     * 删除用户方法 只能用delete请求需要在请求路径中添加要删除的用户编号
     * 
     * @param suId
     * @param req
     * @param res
     * @return
     */
    @DeleteMapping("sysUser/{suId}")
    @ResponseBody
    public String sysUserDel(@PathVariable("suId") String suId)
    {
        return sysUserSer.sysUserDel(suId);
    }
    
}