package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentCallbackSer;

@Controller
public class AgentCallbackCtl extends BaseController{

	@Autowired
	AgentCallbackSer agentCallbackSer;
	
	@PostMapping("agentCallback")
	@ResponseBody
	public String callback(HttpServletRequest request,HttpServletResponse response){
		return agentCallbackSer.callbackByHand(request,response);
	}
	
	@GetMapping("testCallback")
	public void testCallback(HttpServletRequest request,HttpServletResponse response){
		agentCallbackSer.testCallback(request, response);
	}
	/**
	 * 订单推送页面
	 */
	@GetMapping("OrderPushPage")
	public String OrderPushPage(HttpServletRequest request){
		return "mp/OrderPush";
	}
}
