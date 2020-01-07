package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.QueryOrderInfoSer;

@Controller
public class QueryOrderInfoCtl extends BaseController{

	@Autowired
	QueryOrderInfoSer queryOrderInfoSer;
	
	@GetMapping("queryOrderInfoPage")
	public String queryOrderInfoPage(HttpServletRequest request,HttpServletResponse response){
		return "mp/queryOrderInfo";
	}
	
	@GetMapping("queryOrderInfo")
	@ResponseBody
	public String queryOrderInfo(HttpServletRequest request, HttpServletResponse response){
		return queryOrderInfoSer.queryOrderInfo(request, response);
	}
	
}
