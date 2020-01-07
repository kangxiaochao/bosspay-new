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

import com.hyfd.service.sys.SysPermissionSer;


@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class SysPermissionCtl extends BaseController{
	
	@Autowired 
	private SysPermissionSer sysPermissionService;
	
	@GetMapping("/sysPermissionListPage")
	public String sysPermissionListPage() {
		return "system/sysPermissionList";
	}
	
	@GetMapping("sysPermission")
	@ResponseBody
	public String sysPermissionGet(HttpServletRequest req) {
		return sysPermissionService.sysPermissionList(req);
	}
	
	@GetMapping("sysPermissionAddPage")
	public String sysPermissionAddPage() {
		return "system/sysPermissionAdd";
	}
	
	@PostMapping("sysPermission")
	public String sysUserPost(HttpServletRequest req) {
		return sysPermissionService.sysPermissionAdd(req);
	}
	
    @GetMapping("sysPermissionEditPage/{spId}")
	public String sysPermissionEditPage(@PathVariable("spId") String spId) {
		return sysPermissionService.sysPermissionEditPage(spId);
	}
    
	@PutMapping("sysPermission/{spId}")
	@ResponseBody
	public String sysPermissionPut(@PathVariable("spId") String spId, HttpServletRequest req) {
		return sysPermissionService.sysPermissionEdit(req,spId);
	}
	
	@DeleteMapping("sysPermission/{spId}")
	@ResponseBody
	public String sysPermissionDel(@PathVariable("spId") String spId) {
		return sysPermissionService.sysPermissionDel(spId) ;
	}
	
	
}
