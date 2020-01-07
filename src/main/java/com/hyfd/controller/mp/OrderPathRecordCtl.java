package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.OrderPathRecordSer;


@Controller
public class OrderPathRecordCtl extends BaseController{
	@Autowired
	OrderPathRecordSer orderPathRecordSer;
	
	@GetMapping("orderPathRecordPage")
	public String orderBillListPage() {
		return "mp/orderPathRecordList";
	}
	
	@GetMapping("orderPathRecordList")
	@ResponseBody
	public String orderPathRecordListAll(HttpServletRequest req) {
		return orderPathRecordSer.orderAllList(req);
	}
	
	@GetMapping("orderPathRecordParamPage")
	public String orderPathRecordParamPage(HttpServletRequest req) {
		orderPathRecordSer.orderParamPage(req);
		return "mp/orderParam";
	}
	
	@GetMapping("orderPathRecordParam")
	@ResponseBody
	public String orderPathRecordParam(HttpServletRequest req) {
		return orderPathRecordSer.getOrderParam(req);
	}
}
