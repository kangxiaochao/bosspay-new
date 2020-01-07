package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentProfitSer;	

@Controller
public class AgentProfitCtl extends BaseController {
	@Autowired
	AgentProfitSer agentSer;
	
	/**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentProfitList")
    @ResponseBody
    public String agentProfitList(HttpServletRequest req){
        return agentSer.agentProfitList(req);
    }
    
    /**
     * 用于跳转页面
     * @return
     */
	@GetMapping("agentProfit")
	public String agentProfit() {

		return "mp/agentProfit";
	}
	
	@GetMapping("/agentProfitExport")
	public void agentProfitExport(HttpServletRequest req, HttpServletResponse resp) {
		agentSer.agentProfitExport(req, resp);
	}
	
}
