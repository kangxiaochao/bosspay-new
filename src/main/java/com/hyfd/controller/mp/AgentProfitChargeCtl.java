package com.hyfd.controller.mp;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentProfitChargeSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AgentProfitChargeCtl extends BaseController {
	@Autowired
	AgentProfitChargeSer agentProfitChargeSer;
	
	/**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentProfitChargeList")
    @ResponseBody
    public String agentProfitList(HttpServletRequest req){
        return agentProfitChargeSer.agentProfitChargeList(req);
    }
    
    /**
     * 用于跳转页面
     * @return
     */
	@GetMapping("agentProfitChargePage")
	public String agentProfit() {
		return "mp/agentProfitChargeList";
	}

	@GetMapping("/agentProfitChargeExport")
	public void agentProfitExport(HttpServletRequest req, HttpServletResponse resp) {
		agentProfitChargeSer.agentProfitChargeExport(req, resp);
	}
	
}
