package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentReportSer;

@Controller
public class AgentReportCtl extends BaseController{

	@Autowired
	AgentReportSer agentReportSer;
	
	@GetMapping("agentBillStatementPage")
	public String agentFlowStatementListPage(HttpServletRequest request,HttpServletResponse response){
		return "mp/agentBillStatement";
	}
	
	@PostMapping("agentBillStatementList")
	@ResponseBody
	public String agentBillStatementList(HttpServletRequest request,HttpServletResponse response){
		return agentReportSer.ProviderBillStatementList(request, response);
	}
	
	
	
}
