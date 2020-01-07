package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderBillPkgSer;


@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderBillPkgCtl extends BaseController{

	
	@Autowired
	ProviderBillPkgSer providerBillPkgSer;
	
	/**
	 * 跳转到运营商话费包页面
	 * @param id
	 * @return
	 */
	@GetMapping("providerBillPkgList/{id}")
	public String providerBillPkgList(@PathVariable("id") String id) {
		return providerBillPkgSer.providerBillPkgList(id);
	}
	
	/**
	 * 查询话费包数据
	 * @param req
	 * @return
	 */
	@GetMapping("providerBillPkg/{id}")
	@ResponseBody
	public String providerBillPkg(@PathVariable("id") String id, HttpServletRequest req) {
		return providerBillPkgSer.providerBillPkg(id,req);
	}
	
	/**
	 * 跳转到话费包设置页面，把运营商ID带过去
	 * */
	@GetMapping("providerBillPkgEditPage/{id}")
	public String providerBillPkgEditPage(@PathVariable("id") String id, HttpServletRequest req){
		return providerBillPkgSer.providerBillPkgEditPage(id,req);
	}
	
	/**
	 * 修改运营商话费包
	 * */
	@PostMapping("providerBillPkgEdit/{id}")
	public String providerBillPkgEdit(@PathVariable("id") String id, HttpServletRequest req,String[] billPkgId){
		return providerBillPkgSer.providerBillPkgEdit(id,req,billPkgId);
	}
	
	/**
	 * 修改运营商话费包
	 * */
	@GetMapping("providerBillPkgDetail/{id}")
	@ResponseBody
	public String providerBillPkgDetail(@PathVariable("id") String id){
		return providerBillPkgSer.providerBillPkgDetail(id);
	}
	
	@DeleteMapping("providerBillPkg/{ids}")
	@ResponseBody
	public String providerBillPkgDelete(@PathVariable("ids") String[] ids){
		return providerBillPkgSer.providerBillPkgDelete(ids);
	}
	
	/**
	 * @功能描述：		根据运营商、流量使用范围、充值地区 获取对应的流量包信息
	 *
	 * @作者：zhangpj		@创建时间：2017年5月11日
	 * @param req
	 * @return
	 */
	@GetMapping("providerBillPkg")
	@ResponseBody
	public String providerBillPkg(HttpServletRequest req) {
		return providerBillPkgSer.selectProviderBillPkg(req);
	}
	
	/**
	 * @功能描述：	根据运营商获取对应的流量包信息
	 *
	 * @作者：zhangpj		@创建时间：2017年8月1日
	 * @param req
	 * @return
	 */
	@GetMapping("providerBillPkgByProviderId")
	@ResponseBody
	public String providerBillPkgByProviderId(HttpServletRequest req) {
		return providerBillPkgSer.selectProviderBillPkgByProviderId(req);
	}
}
