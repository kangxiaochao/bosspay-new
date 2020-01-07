package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.hyfd.service.mp.AgentBalanceSer;

/**
 * 设置代理商限额
 * @author wyf
 * @center 2017-11-29
 */
@Controller
public class AgentBalanceCtl {

	@Autowired
	AgentBalanceSer agentbalanceser;
	/**
	 * 跳转设置限额页面
	 * @return
	 */
	@GetMapping("agentTask")
	public String agentTask(){
		
		return "mp/agentTask";
	}
	
	@PostMapping("addAgentTask")
	public String addAgentTask(HttpServletRequest req){
		
		return agentbalanceser.addAgentTask(req);
	}
}
