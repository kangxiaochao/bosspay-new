package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.BaseService;
import com.hyfd.service.mp.PaymentSer;

/**
 * 支付
 * 
 * @author wyf
 *
 */
@Controller
public class PaymentCtl extends BaseController {

	@Autowired
	PaymentSer paymentSer;

	@GetMapping("Payment")
	public String Payment() {

		return "mp/Payment";
	}
	
	@GetMapping("PaymentList")
	public String PaymentList() {

		return "mp/paymentList";
	}
	
	@GetMapping("WeChatPay")
	@ResponseBody
	public String WeChatPay(HttpServletRequest req) {
		
		return paymentSer.WeChatPay(req);
	}
	
	@GetMapping("addAgentMoney")
	@ResponseBody
	public String addAgentMoney(HttpServletRequest req) {
		
		return paymentSer.addAgentMoney(req);
	}
	
	@RequestMapping("status/returnUrl")
	@ResponseBody
	public String returnUrl(HttpServletRequest req) {
		
		return paymentSer.returnUrl(req);
	}
	
	@RequestMapping("queryByName")
	@ResponseBody
	public String queryByName(HttpServletRequest req) {
		
		return paymentSer.queryByName(req);
	}
}
