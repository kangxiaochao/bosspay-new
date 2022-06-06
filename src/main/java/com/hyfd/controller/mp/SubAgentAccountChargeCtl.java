package com.hyfd.controller.mp;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentProfitChargeSer;
import com.hyfd.service.mp.SubAgentAccountChargeSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SubAgentAccountChargeCtl extends BaseController {
	@Autowired
	SubAgentAccountChargeSer subAgentAccountChargeSer;
	
	/**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("subAgentAccountChargeList")
    @ResponseBody
    public String agentProfitList(HttpServletRequest req){
        return subAgentAccountChargeSer.subAgentAccountChargeList(req);
    }
    
    /**
     * 用于跳转页面
     * @return
     */
	@GetMapping("subAgentAccountCharge")
	public String agentProfit() {

		return "mp/subAgentAccountChargeList";
	}
	
	@GetMapping("/subAgentAccountChargeExport")
	public void agentProfitExport(HttpServletRequest req, HttpServletResponse resp) {
		subAgentAccountChargeSer.subAgentAccountChargeExport(req, resp);
	}
	
}
