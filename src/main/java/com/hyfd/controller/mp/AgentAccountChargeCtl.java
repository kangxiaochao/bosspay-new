package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentAccountChargeSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class AgentAccountChargeCtl extends BaseController {

	@Autowired
	private AgentAccountChargeSer agentAccountChargeSer;

	/**
	 * 跳转到列表页面 使用get方式提交
	 * 
	 * @param req
	 * @return
	 */
	@GetMapping("agentAccountChargeListPage")
	public String agentAccountChargeListPage() {
		return "mp/agentAccountChargeList";
	}

	/**
	 * 获取列表数据 只能使用get方式提交
	 * 
	 * @param req
	 * @return
	 */
	@GetMapping("agentAccountCharge")
	@ResponseBody
	public String agentAccountChargeAllList(HttpServletRequest req) {
		return agentAccountChargeSer.agentAccountChargeAllList(req);
	}

	/**
	 * 收据跳转页面
	 * 
	 * @author lks 2017年6月13日下午2:26:46
	 * @return
	 */
	@GetMapping("receiptListPage")
	public String receiptListPage() {
		return "mp/receiptList";
	}

	/**
	 * 查询收据所有数据
	 * 
	 * @author lks 2017年6月13日下午2:33:08
	 * @param request
	 * @return
	 */
	@GetMapping("receiptList")
	@ResponseBody
	public String receiptList(HttpServletRequest request, HttpServletResponse response) {
		return agentAccountChargeSer.receiptList(request, response);
	}

	/**
	 * 导出代理商收据报表
	 * 
	 * @author lks 2018年4月16日上午10:04:12
	 * @param request
	 * @param response
	 */
	@GetMapping("exportReceipt")
	public void exportReceipt(HttpServletRequest request, HttpServletResponse response) {
		agentAccountChargeSer.exportReceiptExcel(request, response);
	}

	/**
	 * 导出代理商合并收据报表
	 * 
	 * @author lks 2018年4月16日上午10:04:12
	 * @param request
	 * @param response
	 */
	@GetMapping("exportMergeReceipt")
	public void exportMergeReceiptExcel(HttpServletRequest request, HttpServletResponse response) {
		agentAccountChargeSer.exportMergeReceiptExcel(request, response);
	}

	@GetMapping("/exportNewCustomerAgent")
	public void exportNewCustomerAgent(HttpServletRequest req, HttpServletResponse resp) {
		agentAccountChargeSer.exportNewCustomerAgent(req, resp);
	}

	@GetMapping("/exportMonthlyAddMoneyAgent")
	public void exportMonthlyAddMoneyAgent(HttpServletRequest req, HttpServletResponse resp) {
		agentAccountChargeSer.exportMonthlyAddMoneyAgent(req, resp);
	}

}
