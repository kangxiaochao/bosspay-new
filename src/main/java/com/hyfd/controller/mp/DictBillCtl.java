package com.hyfd.controller.mp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.DictBillSer;


@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class DictBillCtl extends BaseController{

	@Autowired 
	private DictBillSer dictBillSer;
	
	/**
	 * 获取字典表内省份编码
	 * */
	@GetMapping("getProvinceCode/{name}")
	@ResponseBody
	public String getProvinceCode(@PathVariable("name") String name) {
		return dictBillSer.getProvinceCode(name);
	}
}
