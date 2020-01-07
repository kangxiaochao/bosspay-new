package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderReportSer;

@Controller
public class ProviderReportCtl extends BaseController{

	@Autowired
	ProviderReportSer providerReportSer;
	
	@GetMapping("providerBillStatementPage")
	public String ProviderBillStatementListPage(HttpServletRequest request,HttpServletResponse response){
		return "mp/providerBillStatement";
	}
	
	@PostMapping("providerBillStatementList")
	@ResponseBody
	public String ProviderBillStatementList(HttpServletRequest request,HttpServletResponse response){
		return providerReportSer.ProviderBillStatementList(request, response);
	}
	
	@GetMapping("toDetailBillOrderList")
	public String toDetailOrderList(HttpServletRequest request,HttpServletResponse response){
		return providerReportSer.toDetailBillOrderList(request, response);
	}
	
	@PostMapping("detailBillOrderList")
	@ResponseBody
	public String detailOrderList(HttpServletRequest request,HttpServletResponse response){
		return providerReportSer.detailBillOrderList(request, response);
	}
	
	@GetMapping("exportBillOrderExcel")
	public void exportFlowOrderExcel(HttpServletRequest request,HttpServletResponse response){
		providerReportSer.exportBillOrderExcel(request, response);
	}
	
	@GetMapping("exportBillOrderCsv")
	public void exportFlowOrderCsv(HttpServletRequest request,HttpServletResponse response){
		providerReportSer.exportBillOrderCSV(request, response);
	}
	
}
