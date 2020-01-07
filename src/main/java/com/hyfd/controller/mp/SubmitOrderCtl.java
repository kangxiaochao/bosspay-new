package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.SubmitOrderSer;

@Controller
public class SubmitOrderCtl extends BaseController{

	@Autowired
	SubmitOrderSer submitOrderSer;
	
	/**
	 * 订单提交页面
	 * @author lks 2017年5月3日下午5:17:59
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("submitOrderPage")
	public String submitOrderPage(HttpServletRequest request,HttpServletResponse response){
		return "mp/submitOrderList";
	}
	
	/**
	 * 
	 * @author lks 2017年5月3日下午5:54:09
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("submitOrderList")
	@ResponseBody
	public String submitOrderList(HttpServletRequest request,HttpServletResponse response){
		return submitOrderSer.submitOrderList(request);
	}
	
	@GetMapping("/expsubmitOrder")
	public void expsubmitOrder(HttpServletResponse rep,HttpServletRequest req) {
		submitOrderSer.expsubmitOrder(rep, req);
	}
}
