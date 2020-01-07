package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.FinancialReportSer;

@Controller
public class FinancialReportCtl extends BaseController{

	@Autowired
	FinancialReportSer frSer;
	
	/**
	 * 财务报表页面跳转
	 * @author lks 2018年2月27日上午9:28:19
	 * @return
	 */
	@GetMapping("FinancialReportPage")
	public String FinancialReportPage(){
		return "mp/FinancialReportList";
	}
	
	/**
	 * 获取上家页面数据
	 * @author lks 2018年2月27日上午9:35:03
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("FinancialChannelReportList")
	@ResponseBody
	public String FinancialChannelReportList(HttpServletRequest request, HttpServletResponse response){
		return frSer.FinancialChannelReportList(request, response);
	}
	
	/**
	 * 获取下家页面数据
	 * @author lks 2018年2月27日上午9:35:03
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("FinancialAgentReportList")
	@ResponseBody
	public String FinancialAgentReportList(HttpServletRequest request, HttpServletResponse response){
		return frSer.FinancialAgentReportList(request, response);
	}
	
	
	/**
	 * 导出财务上家数据报表
	 * @author lks 2018年2月28日下午3:51:12
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportFinancialChannelReport")
	public void exportFinancialChannelReport(HttpServletRequest request, HttpServletResponse response){
		frSer.exportFinancialChannelReport(request, response);
	}
	
	/**
	 * 导出财务上家数据报表
	 * @author lks 2018年2月28日下午3:51:12
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportFinancialAgentReport")
	public void exportFinancialAgentReport(HttpServletRequest request, HttpServletResponse response){
		frSer.exportFinancialAgentReport(request, response);
	}
	
	/**
	 * 经营分析报表
	 * @author lks 2018年3月20日上午9:14:42
	 * @param request
	 * @param response
	 */
	@RequestMapping("BusinessList")
	@ResponseBody
	public String BusinessList(HttpServletRequest request, HttpServletResponse response){
		return frSer.BusinessList(request, response);
	}
	
	/**
	 * 导出经营分析报表
	 * @author lks 2018年3月20日上午9:47:01
	 * @param request
	 * @param response
	 */
	@RequestMapping("exportBusinessReport")
	public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
		frSer.exportBusinessReport(request, response);
	}
	
}
