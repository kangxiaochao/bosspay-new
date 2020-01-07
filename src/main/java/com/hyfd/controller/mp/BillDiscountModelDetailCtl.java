package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.BillDiscountModelDetailSer;


@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class BillDiscountModelDetailCtl extends BaseController{
	
	@Autowired
	private BillDiscountModelDetailSer billDiscountModelDetailSer;
	
	/**
	 * 查询折扣详情 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billDiscountModelDetailList/{id}")
	@ResponseBody
	public String billDiscountModelDetailList(@PathVariable("id")String id, HttpServletRequest req){
		return billDiscountModelDetailSer.billDiscountModelDetailList(req,id);
	}
	/**
	 * 跳转到折扣添加页面
	 * 
	@GetMapping("billDiscountModelDetailAddPage/{id}")
	public String billDiscountModelDetailAddPage(@PathVariable("id")String id,HttpServletRequest req) {
		return billDiscountModelDetailSer.billDiscountModelDetailAddPage(id, req);
	}
	*/
	
	/**
	 * 添加折扣
	 * */
	@PostMapping("billDiscountModelDetail")
	public String billDiscountModelDetailAdd(HttpServletRequest req){
		return billDiscountModelDetailSer.billDiscountModelDetailAdd(req);
	}
	/**
	 * 跳转到折扣设置页面
	 * */
	@GetMapping("billDiscountModelDetailEditPage/{id}")
	public String billDiscountModelDetailEditPage(@PathVariable("id")String id,HttpServletRequest req){
		return billDiscountModelDetailSer.billDiscountModelDetailEditPage(id, req);
	}
	/**
	 * 设置页面详情信息
	 * */
	@GetMapping("billDiscountModelDetailEditPageDetail")
	@ResponseBody
	public String billDiscountModelDetailEditPageDetail(HttpServletRequest req){
		return billDiscountModelDetailSer.billDiscountModelDetailEditPageDetail(req);
	}
	
	/**
	 * 折扣模板上传
	 * */
	@PostMapping("BillDiscountModelEx1")
	@ResponseBody
	public String BillDiscountModelEx1(HttpServletRequest req) {
		return billDiscountModelDetailSer.BillDiscountUpload(req);
	}
}
