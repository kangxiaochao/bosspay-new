package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentProfitSer;	

@Controller
public class AgentProfitCtl extends BaseController {
	@Autowired
	AgentProfitSer agentProfitSer;
	
	/**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentProfitList")
    @ResponseBody
    public String agentProfitList(HttpServletRequest req){
        return agentProfitSer.agentProfitList(req);
    }
    
    /**
     * 打开代理商利润页面
     * @return
     */
	@GetMapping("agentProfit")
	public String agentProfit() {

		return "mp/agentProfit";
	}

	/**
	 * 打开代理商利润加款界面
	 * @return
	 */
	@GetMapping("agentProfitEditPage")
	public String agentProfitEditPage() {
		return agentProfitSer.agentProfitEditPage();
	}

	@PostMapping("allotProfit")
	public String allotProfit(HttpServletRequest request, HttpServletResponse response)
	{
		return agentProfitSer.allotProfit(request, response);
	}
	
	@GetMapping("/agentProfitExport")
	public void agentProfitExport(HttpServletRequest req, HttpServletResponse resp) {
		agentProfitSer.agentProfitExport(req, resp);
	}
	
}
