package com.hyfd.controller.mp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.TutubiOrderSer;

@Controller
public class TutubiOrderCtl extends BaseController{
	
	@Autowired
	TutubiOrderSer tutubiOrderSer;
	
	@GetMapping("tutubiOrderList")
	public String tutubiOrderList() {
		
		return "mp/tutubiOrderList";
	}
	
	/**
	 * 前台兔兔币列表查询
	 * @return
	 */
	@GetMapping("tutubiOrder")
	@ResponseBody
	public String tutubiOrder(HttpServletRequest req) {
		
		return tutubiOrderSer.tutubiOrder(req);
	}
	
	/**
	 * 去兔兔币后台列表查询
	 * @return
	 */
	@GetMapping("tutubiOrderNet")
	@ResponseBody
	public String tutubiOrderNet(HttpServletRequest req) {
		return tutubiOrderSer.tutubiOrderNet(req);
	}
	
	/**
	 * 导出报表
	 * @param req
	 */
	@GetMapping("derive")
	public void deriveStatement(HttpServletRequest req,HttpServletResponse res) {
		tutubiOrderSer.deriveStatement(req,res);
	}
	
	@GetMapping("tutubiCookies")
	@ResponseBody
	public String tutubiCookies(HttpServletRequest req,HttpServletResponse res){
		return tutubiOrderSer.tutubiCookies(req, res);
	}
}
