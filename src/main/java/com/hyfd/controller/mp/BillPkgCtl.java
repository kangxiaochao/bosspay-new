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
import com.hyfd.service.mp.BillPkgSer;

@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class BillPkgCtl extends BaseController{
	
	@Autowired
	BillPkgSer billPkgSer;

	@GetMapping("billPkgListPage")
	public String billPkgListPage() {
		return "mp/billPkgList";
	}
	
	@GetMapping("billPkg")
	@ResponseBody
	public String billPkgGet(HttpServletRequest req) {
		return billPkgSer.billPkgList(req);
	}
	
	@GetMapping("billPkgAddPage")
	public String billPkgAddPage() {
		return "mp/billPkgAdd";
	}

	@PostMapping("billPkg")
	public String providerPost(HttpServletRequest req) {
		return billPkgSer.billPkgAdd(req);
	}
	
	/**
	 * 根据运营商ID查询话费包
	 * */
	@GetMapping("billPkgList")
	@ResponseBody
	public String dataListGetByProId() {
		return billPkgSer.billPkgListGet();
	}
	
    @GetMapping("billPkgEditPage/{id}")
	public String billPkgEditPage(@PathVariable("id") String id) {
		return billPkgSer.billPkgEditPage(id);
	}
	 
	@PutMapping("billPkg/{id}")
	@ResponseBody
	public String billPkgPut(@PathVariable("id") String id, HttpServletRequest req) {
		return billPkgSer.billPkgEdit(req,id);
	}
	
	@DeleteMapping("billPkg/{id}")
	@ResponseBody
	public String billPkgDel(@PathVariable("id") String id) {
		return billPkgSer.billPkgDel(id) ;
	}
  
}
