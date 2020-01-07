package com.hyfd.controller.sys;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.sys.SysRoleSer;



@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class SysRoleCtl extends BaseController{
	
	@Autowired 
	private SysRoleSer sysRoleSer;
	
	@GetMapping("/sysRoleListPage")
	public String sysRoleListPage() {
		return "system/sysRoleList";
	}
	
	@GetMapping("sysRole")
	@ResponseBody
	public String sysRoleGet(HttpServletRequest req) {
		return sysRoleSer.sysRoleList(req);
	}
	
	@GetMapping("sysRoleOptions")
	@ResponseBody
	public String sysRoleOptions(HttpServletRequest req) {
		return sysRoleSer.sysRoleOptions(req);
	}
	
	@GetMapping("sysRoleAddPage")
	public String sysRoleAddPage() {
		return "system/sysRoleAdd";
	}
	
	@PostMapping("sysRole")
	public String sysUserPost(HttpServletRequest req) {
		return sysRoleSer.sysRoleAdd(req);
	}
	
    @GetMapping("sysRoleEditPage/{spId}")
	public String sysRoleEditPage(@PathVariable("spId") String spId) {
		return sysRoleSer.sysRoleEditPage(spId);
	}
    
	@PutMapping("sysRole/{spId}")
	@ResponseBody
	public String sysRolePut(@PathVariable("spId") String spId, HttpServletRequest req) {
		return sysRoleSer.sysRoleEdit(req,spId);
	}
	
	@DeleteMapping("sysRole/{spId}")
	@ResponseBody
	public String sysRoleDel(@PathVariable("spId") String spId) {
		return sysRoleSer.sysRoleDel(spId) ;
	}
	
	
}
