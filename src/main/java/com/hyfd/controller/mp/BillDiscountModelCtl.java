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
import com.hyfd.service.mp.BillDiscountModelSer;

@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class BillDiscountModelCtl extends BaseController{
	
	@Autowired 
	private BillDiscountModelSer billDiscountModelSer;
	
	/**
	 * 跳转到列表页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billDiscountModelListPage")
	public String billDiscountModelListPage() {
		return "mp/billDiscountModelList";
	}
	
	/**
	 * 获取列表数据 只能使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billDiscountModel")
	@ResponseBody
	public String billDiscountModelGet(HttpServletRequest req) {
		return billDiscountModelSer.billDiscountModelList(req);
	}
	
	/**
	 * @功能描述：	不分页根据条件获取全部代理商流量折扣模板列表数据
	 *
	 * @作者：zhangpj		@创建时间：2016年12月16日
	 * @param req
	 * @return
	 */
	@GetMapping("billDiscountModelAll")
	@ResponseBody
	public String billDiscountModelAllGet(HttpServletRequest req) {
		return billDiscountModelSer.billDiscountModelAllList(req);
	}
	
	/**
	 * 代理商流量折扣模板添加页面显示 只能使用get方式提交
	 * @return
	 */
	@GetMapping("billDiscountModelAddPage")
	public String billDiscountModelAddPage() {
		return "mp/billDiscountModelAdd";
	}
	
	/**
	 * 创建代理商流量折扣模板对象只能用post方式来提交
	 * @param req
	 * @return
	 */
	@PostMapping("billDiscountModel")
	public String billDiscountModelPost(HttpServletRequest req) {
		return billDiscountModelSer.billDiscountModelAdd(req);
	}
	
	/**
	 * 显示详单页面要使用get方法并要在请求路径中传入代理商流量折扣模板编号数据
	 * @param id
	 * @return
	 */
	@GetMapping("billDiscountModelDetailListPage/{id}")
	public String billDiscountModelDetail(@PathVariable("id") String id) {
		return billDiscountModelSer.billDiscountModelDetailListPage(id);
	}
	
	/**
	 * 显示编辑页面请求路径中要包括需要修改的ID
	 * @param id
	 * @return
	 */
    @GetMapping("billDiscountModelEditPage/{id}")
	public String billDiscountModelEditPage(@PathVariable("id") String id) {
		return billDiscountModelSer.billDiscountModelEditPage(id);
	}
    
	/**
	 * 更新代理商流量折扣模板信息 只能用put方式提交
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@PutMapping("billDiscountModel/{id}")
	@ResponseBody
	public String billDiscountModelPut(@PathVariable("id") String id, HttpServletRequest req) {
		return billDiscountModelSer.billDiscountModelEdit(req,id);
	}
	
	/**
	 * 删除代理商流量折扣模板方法 只能用delete请求需要在请求路径中添加要删除的代理商流量折扣模板编号
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@DeleteMapping("billDiscountModel/{id}")
	@ResponseBody
	public String billDiscountModelDel(@PathVariable("id") String id) {
		return billDiscountModelSer.billDiscountModelDel(id) ;
	}
}