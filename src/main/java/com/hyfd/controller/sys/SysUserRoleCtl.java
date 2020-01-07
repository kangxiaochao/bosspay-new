package com.hyfd.controller.sys;


import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.hyfd.service.sys.SysUserRoleSer;


@Controller
public class SysUserRoleCtl extends BaseController{
	
	@Autowired 
	private SysUserRoleSer sysUserRoleService;
		
	/**
	 * 跳转到用户分配页面 
	 * @return
	 */
	@GetMapping("sysUserRoleListPage/{srId}")
	public String sysUserRoleListPage(@PathVariable("srId") String srId) {
		return sysUserRoleService.getSysUserRoleBySrId(srId);
	}
	
	/**
	 * 获取已分配的用户
	 * */
	@GetMapping("sysGetHasUser/{srId}")
	@ResponseBody
	public String sysGetHasUserRole(@PathVariable("srId") String srId){
		return sysUserRoleService.getHasSysUser(srId);
	}
	
	/**
	 * 获取未分配的用户
	 * */
	@GetMapping("sysGetNoUser/{srId}")
	@ResponseBody
	public String sysGetNoUserRole(@PathVariable("srId") String srId){
		return sysUserRoleService.getNoSysUser(srId);
	}
	
	/**
	 * 添加用户到角色
	 * */
	@PostMapping("sysUserAddOrEdit")
	public String sysUserAddOrEdit(HttpServletRequest req){
		return sysUserRoleService.sysUserAddOrEdit(req);
	}
	
	/**
	 * 跳转到角色分配页面 
	 * @return
	 */
	@GetMapping("sysUserRoleList/{suId}")
	public String sysUserRoleList(@PathVariable("suId") String suId) {
		return sysUserRoleService.getSysUserRoleBySuId(suId);
	}
	
	/**
	 * 获取已分配的角色
	 * */
	@GetMapping("sysGetHasRole/{suId}")
	@ResponseBody
	public String sysGetHasRole(@PathVariable("suId") String suId){
		return sysUserRoleService.getHasSysRole(suId);
	}
	
	/**
	 * 获取未分配的角色
	 * */
	@GetMapping("sysGetNoRole/{suId}")
	@ResponseBody
	public String sysGetNoRole(@PathVariable("suId") String suId){
		return sysUserRoleService.getNoSysRole(suId);
	}
	
	/**
	 * 添加角色到用户
	 * */
	@PostMapping("sysRoleAddOrEdit")
	public String sysRoleAddOrEdit(HttpServletRequest req){
		return sysUserRoleService.sysRoleAddOrEdit(req);
	}
}
