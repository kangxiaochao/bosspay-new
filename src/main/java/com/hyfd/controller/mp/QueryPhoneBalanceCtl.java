package com.hyfd.controller.mp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentCallbackSer;
import com.hyfd.service.mp.QueryPhoneBalanceSer;

@Controller
public class QueryPhoneBalanceCtl extends BaseController{

	@Autowired
	QueryPhoneBalanceSer queryPhoneBalanceSer;
	
	@GetMapping("queryPhoneBalance")
	@ResponseBody
	public Map<String, String> queryPhoneBalance(HttpServletRequest request){
		return queryPhoneBalanceSer.queryPhoneBalance(request);
	}
	
	@RequestMapping("order/queryPhoneBalance")
	@ResponseBody
	public Map<String, String> queryPhoneBalance1(HttpServletRequest request){
		return queryPhoneBalanceSer.queryPhoneBalance(request);
	}
	
}
