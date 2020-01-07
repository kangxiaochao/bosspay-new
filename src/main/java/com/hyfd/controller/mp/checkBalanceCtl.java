package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.checkBalanceSer;

@Controller
public class checkBalanceCtl extends BaseController{

	@Autowired
	checkBalanceSer cbSer;
	
	@GetMapping("checkBalance")
	@ResponseBody
	public void checkBalance(HttpServletRequest request,HttpServletResponse response){
		cbSer.checkBalance(request, response);
	}
	
	@GetMapping("checkBalancePage")
	public String checkBalancePage(HttpServletRequest request,HttpServletResponse response){
		return "mp/checkBalance";
	}
	
	@GetMapping("checkBalanceList")
	@ResponseBody
	public String checkBalanceList(HttpServletRequest request,HttpServletResponse response){
		return cbSer.checkBalanceList(request, response);
	}
	
	@GetMapping("additionalEstimate")
	public String additionalEstimate(HttpServletRequest request,HttpServletResponse response){
		return "mp/additionalEstimate";
	}
	
	@GetMapping("additionalEstimateList")
	@ResponseBody
	public String additionalEstimateList(HttpServletRequest request,HttpServletResponse response){
		return cbSer.additionalEstimateList(request, response);
	}
	
}
