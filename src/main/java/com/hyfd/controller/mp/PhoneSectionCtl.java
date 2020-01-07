package com.hyfd.controller.mp;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.PhoneSectionSer;

@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class PhoneSectionCtl extends BaseController{
	
	@Autowired 
	private PhoneSectionSer phoneSectionSer;
	
	/**
	 * 跳转到列表页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("phoneSectionListPage")
	public String phoneSectionListPage() {
		return "mp/phoneSectionList";
	}
	
	/**
	 * 获取列表数据 只能使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("phoneSection")
	@ResponseBody
	public String phoneSectionGet(HttpServletRequest req) {
		return phoneSectionSer.phoneSectionList(req);
	}
	
	/**
	 * @功能描述：	不分页根据条件获取全部号段信息列表数据
	 *
	 * @作者：zhangpj		@创建时间：2016年12月16日
	 * @param req
	 * @return
	 */
	@GetMapping("phoneSectionAll")
	@ResponseBody
	public String phoneSectionAllGet(HttpServletRequest req) {
		return phoneSectionSer.phoneSectionAllList(req);
	}
	
	/**
	 * 号段信息添加页面显示 只能使用get方式提交
	 * @return
	 */
	@GetMapping("phoneSectionAddPage")
	public String phoneSectionAddPage() {
		return "mp/phoneSectionAdd";
	}
	
	@GetMapping("batchAddPage")
	public String batchAddPage() {
		return "mp/batchAddNumberSegment";
	}
	
	/**
	 * 创建号段信息对象只能用post方式来提交
	 * @param req
	 * @return
	 */
	@PostMapping("phoneSection")
	public String phoneSectionPost(HttpServletRequest req) {
		return phoneSectionSer.phoneSectionAdd(req);
	}
	
	/**
	 * 批量手机号有效性校验
	 * 解析上传的txt文件
	 * @throws IOException 
	 */
	@PostMapping("batchFilterPhone")
	@ResponseBody
	public String batchFilterPhone(HttpServletRequest req){
		return phoneSectionSer.numberBatchFilter(req);
	}
	
	/**
	 * 批量手机号有效性校验(余额查询)
	 * 解析上传的txt文件
	 * @throws IOException 
	 */
	@PostMapping("batchQueryFilterPhone")
	@ResponseBody
	public String batchQueryFilterPhone(HttpServletRequest req){
		return phoneSectionSer.numberBatchQueryFilter(req);
	}
	
	/**
	 * 批量手机号有效性校验(同面值批量充值)
	 * 解析上传的excel文件
	 * @throws IOException 
	 */
	@PostMapping("batchFilterPhoneByExcelTwo")
	@ResponseBody
	public String batchFilterPhoneByExcelTwo(HttpServletRequest req){
		return phoneSectionSer.numberBatchFilterByExcelTwo(req);
	}
	
	/**
	 * 批量手机号有效性校验
	 * 解析上传的excel文件
	 * @throws IOException 
	 */
	@PostMapping("batchFilterPhoneByExcel")
	@ResponseBody
	public String batchFilterPhoneByExcel(HttpServletRequest req){
		return phoneSectionSer.numberBatchFilterByExcel(req);
	}
	
	/**
	 * 跳转到详情页面 要使用get方式提交
	 * @return
	 */
	@GetMapping("phoneSectionDetail")
	public String agentDetail() {
		return "mp/phoneSectionDetail";
	}
	
	/**
	 * 显示详单页面要使用get方法并要在请求路径中传入号段信息编号数据
	 * @param id
	 * @return
	 */
	@GetMapping("phoneSectionDetail/{id}")
	public String phoneSectionDetail(@PathVariable("id") String id) {
		return phoneSectionSer.phoneSectionDetail(id);
	}
	
	/**
	 * 显示编辑页面请求路径中要包括需要修改的ID
	 * @param id
	 * @return
	 */
    @GetMapping("phoneSectionEditPage/{id}")
	public String phoneSectionEditPage(@PathVariable("id") String id) {
		return phoneSectionSer.phoneSectionEditPage(id);
	}
    
	/**
	 * 更新号段信息信息 只能用put方式提交
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@PutMapping("phoneSection/{id}")
	@ResponseBody
	public String phoneSectionPut(@PathVariable("id") String id, HttpServletRequest req) {
		return phoneSectionSer.phoneSectionEdit(req,id);
	}
	
	/**
	 * 删除号段信息方法 只能用delete请求需要在请求路径中添加要删除的号段信息编号
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@DeleteMapping("phoneSection/{id}")
	@ResponseBody
	public String phoneSectionDel(@PathVariable("id") String id) {
		return phoneSectionSer.phoneSectionDel(id) ;
	}
	

    /**
     * @功能描述：	验证号段是否已存在
     *
     * @param section
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年4月9日
     */
    @GetMapping("phoneCheck/{section}")
    @ResponseBody
	public String phoneCheck(@PathVariable("section") String section) {
		return phoneSectionSer.phoneCheck(section);
	}
    
    @PostMapping("batchAddPage")
    @ResponseBody
    public String phoneSectionAdd(MultipartFile file, HttpServletRequest req) {
		
		return phoneSectionSer.phoneSectionAdd(file, req);
	}
}