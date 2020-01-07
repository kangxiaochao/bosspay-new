package com.hyfd.controller.mp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.util.HSSFColor.BLACK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.mp.ProviderBillRechargeGroupSer;

/**
 * 流量复充通道组
 * @author Administrator
 *
 */
@Controller
public class ProviderBillRechargeGroupCtl {

	@Autowired
	ProviderBillRechargeGroupSer providerBillRechargeGroupSer;
	
	/**
	 * 跳转到列表页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("providerBillRechargeGroupList")
	public String providerBillRechargeGroupList() {
		return "mp/providerBillRechargeGroupList";
	}
	
	/**
	 * 获取复充流量通道组列表 只能使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("providerBillRechargeBill")
	@ResponseBody
	public String providerBillRechargeBill(HttpServletRequest req) {
		return providerBillRechargeGroupSer.providerBillRechargeBill(req);
	}
	
	/**
	 * 复充流量通道组添加页面显示 只能使用get方式提交
	 * @return
	 */
	@GetMapping("providerBillRechargeGroupAddPage")
	public String providerBillRechargeGroupAddPage() {
		return "mp/providerBillRechargeGroupAdd";
	}
	
	/**
	 * 创建复充流量通道组对象只能用post方式来提交
	 * @param req
	 * @return
	 */
	@PostMapping("providerBillRechargeAdd")
	public String providerBillGroupPost(HttpServletRequest req) {
		return providerBillRechargeGroupSer.providerBillRechargeAdd(req);
	}
	
	/**
	 * 显示编辑页面请求路径中要包括需要修改的ID
	 * @param id
	 * @return
	 */
    @GetMapping("providerBillRechargeGroupEditPage/{id}")
	public String providerBillRechargeGroupEditPage(@PathVariable("id") String id) {
		return providerBillRechargeGroupSer.providerBillRechargeGroupEditPage(id);
	}
    
    /**
	 * 更新复充流量通道组信息 只能用put方式提交
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@PutMapping("editBillRechargeGroup")
	@ResponseBody
	public String editBillRechargeGroup(HttpServletRequest req) {
		return providerBillRechargeGroupSer.editBillRechargeGroup(req);
	}
	
	/**
	 * 优先级上移
	 * @param id
	 * @return
	 */
    @GetMapping("updateRechargePriorityUp/{type}")
    @ResponseBody
	public List<String> updateRechargePriority(@PathVariable("type") int type, HttpServletRequest req) {
		return providerBillRechargeGroupSer.updateRechargePriority(type,req);
	}
    
    /**
	 * 删除复充流量通道组方法
	 * @param id
	 * @param req
	 * @param res
	 * @return
	 */
	@DeleteMapping("deleteRechargeGroupById/{id}")
	@ResponseBody
	public List<String> deleteRechargeGroupById(@PathVariable("id") String id) {
		return providerBillRechargeGroupSer.deleteRechargeGroupById(id) ;
	}
    
}
