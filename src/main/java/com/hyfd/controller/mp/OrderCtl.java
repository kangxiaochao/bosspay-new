package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.OrderSer;

@Controller
public class OrderCtl extends BaseController {

	@Autowired
	OrderSer orderSer;

	@GetMapping("orderListPage")
	public String orderBillListPage() {

		return orderSer.orderListPage();
	}

	// @GetMapping("orderList")
	// @ResponseBody
	// public String orderList(HttpServletRequest req){
	// return orderSer.orderList(req);
	// }

	/**
	 * @功能描述： 根据条件获取订单列表数据
	 *
	 * @作者：hyj @创建时间：2017年1月12日
	 * @param req
	 * @return
	 */
	@GetMapping("orderList")
	@ResponseBody
	public String orderListAll(HttpServletRequest req) {
		return orderSer.orderAllList(req);
	}

	@GetMapping("orderReport")
	public void orderReport(HttpServletResponse response, HttpServletRequest req) {
		orderSer.expOrder(response, req);
	}
	
	@GetMapping("exportCsv4Future")
	public void exportCsv4Future(HttpServletResponse response, HttpServletRequest request) {
		orderSer.exportCsv4Future(request, response);
	}

	/**
	 * 复充方法
	 * 
	 * @author lks 2017年3月8日下午7:33:11
	 * @param request
	 * @param response
	 * @return 复充成功与否
	 */
	@GetMapping("reChargeForOrder")
	@ResponseBody
	public String reCharge(HttpServletRequest request) {
		return orderSer.batchReCharge(request);
	}

	/**
	 * 退款方法
	 * 
	 * @author lks 2017年3月8日下午7:33:17
	 * @param request
	 * @param response
	 * @return 退款成功与否
	 */
	@GetMapping("refundForOrder")
	@ResponseBody
	public String refund(HttpServletRequest request) {
		return orderSer.batchReFund(request);
	}

	/**
	 * 修改订单状态
	 * 
	 * @author lks 2017年4月22日下午2:24:03
	 * @param request
	 * @return
	 */
	@GetMapping("changeStatus")
	@ResponseBody
	public String updateStatus(HttpServletRequest request) {
		return orderSer.updateStatus(request);
	}

	@GetMapping("orderParamPage")
	public String orderParamPage(HttpServletRequest req) {
		orderSer.orderParamPage(req);
		return "mp/orderParam";
	}

	@GetMapping("batchModifyOrderStatus")
	public String batchModifyOrderStatus() {

		return "mp/batchModifyOrderStatus";
	}

	@GetMapping("orderParam")
	@ResponseBody
	public String orderParam(HttpServletRequest req) {
		return orderSer.getOrderParam(req);
	}

	@PostMapping("/batchModifyOrderStatus")
	@ResponseBody
	public String batchModifyOrderStatus(HttpServletRequest req) {
		
		return orderSer.batchModifyOrderStatus(req);
	}
}
