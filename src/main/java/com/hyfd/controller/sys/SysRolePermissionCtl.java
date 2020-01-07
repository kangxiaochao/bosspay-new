package com.hyfd.controller.sys;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.hyfd.service.sys.SysRolePermissionSer;


@Controller
public class SysRolePermissionCtl extends BaseController {
	
	@Autowired
	private SysRolePermissionSer sysRolePermissionService;
	
	/**
	 * 跳转到权限分配页面 
	 * @return
	 */
	@GetMapping("sysRolePermissionPage/{srId}")
	public String sysRolePermissionPage(@PathVariable("srId") String srId) {
		return sysRolePermissionService.sysRolePermissionBySrId(srId);
	}
	
	/**
	 * 获取已分配的权限
	 * */
	@GetMapping("sysGetHasPermission/{srId}")
	@ResponseBody
	public String sysGetHasPermission(@PathVariable("srId") String srId){
		return sysRolePermissionService.getHasSysPermission(srId);
	}
	
	/**
	 * 获取未分配的权限
	 * */
	@GetMapping("sysGetNoPermission/{srId}")
	@ResponseBody
	public String sysGetNoPermission(@PathVariable("srId") String srId){
		return sysRolePermissionService.getNoSysPermission(srId);
	}
	
	/**
	 * 添加权限到角色
	 * */
	@PostMapping("sysPermissionAssign")
	public String sysPermissionAssign(HttpServletRequest req){
		return sysRolePermissionService.sysPermissionAssign(req);
	}
	
}
