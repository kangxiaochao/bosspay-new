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
import com.hyfd.service.sys.SysFunctionSer;

@Controller
public class SysFunctionCtl extends BaseController {
	@Autowired
	SysFunctionSer sysFunctionSer;

	@GetMapping("sysFunction")
	@ResponseBody
	public String sysFunctionGet(HttpServletRequest req) {
		return sysFunctionSer.sysFunctionList(req);
	}

	@GetMapping("/sysFunctionListPage")
	public String sysFunctionListPage() {
		return "system/sysFunctionList";
	}

//	@GetMapping("sysGetRoleList")
//	@ResponseBody
//	public String sysGetRoleList() {
//		return sysFunctionSer.sysGetRoleList();
//	}

	@GetMapping("sysGetPermissionList")
	@ResponseBody
	public String sysGetPermissionList() {
		return sysFunctionSer.sysGetPermissionList();
	}

	@GetMapping("sysFunctionAddPage")
	public String sysFunctionAddPage() {
		return sysFunctionSer.getSysFunction();
	}

	@PostMapping("sysFunctionAdd")
	public String sysUserPost(HttpServletRequest req) {
		return sysFunctionSer.sysFunctionAdd(req);
	}

	@GetMapping("sysFunctionEditPage/{sfId}")
	public String sysFunctionEditPage(@PathVariable("sfId") String sfId) {
		return sysFunctionSer.sysFunctionEditPage(sfId);
	}

	@PutMapping("sysFunctionEdit")
	@ResponseBody
	public String sysFunctionPut(HttpServletRequest req) {
		return sysFunctionSer.sysFunctionEdit(req);
	}

	@DeleteMapping("sysFunction/{sfId}")
	@ResponseBody
	public String sysFunctionDel(@PathVariable("sfId") String sfId) {
		return sysFunctionSer.sysFunctionDel(sfId);
	}

}
