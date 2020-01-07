package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentAccountChargeAuditSer;


@Controller
public class AgentAccountChargeAuditCtl extends BaseController{

	@Autowired
	private AgentAccountChargeAuditSer agentAccountChargeAuditSer;
	
	@GetMapping("agentAccountChargeAuditPage")
	public String agentAccountChargeAuditPage() {
		return "mp/agentAccountChargeAuditList";
	}
	
	@GetMapping("agentAccountChargeAuditList")
	@ResponseBody
	public String agentAccountChargeAuditList(HttpServletRequest req) {
		return agentAccountChargeAuditSer.agentAccountChargeAuditList(req);
	}
	/**
	 * 导出代理商冲扣值审核列表
	 * @param req
	 * @return
	 */
	@GetMapping("agentAccountChargeAuditListExport")
	@ResponseBody
	public void agentAccountChargeAuditListExport(HttpServletRequest req,HttpServletResponse resp) {
		 agentAccountChargeAuditSer.agentAccountChargeAuditListExport(req,resp);
	}
	
	@PostMapping("agentAccountChargeAudit")
	public String agentAccountChargeAuditAdd(HttpServletRequest req){
		return agentAccountChargeAuditSer.agentAccountChargeAuditAdd(req);
	}
	
	@GetMapping("agentAccountChargeAuditEdit")
	@ResponseBody
	public String agentAccountChargeAuditEdit(HttpServletRequest req){
		return agentAccountChargeAuditSer.agentAccountChargeAuditEdit(req);
	}
	
	/**
     * 显示编辑页面请求路径中要包括需要修改的ID
     * 
     * @param id
     * @return
     */
    @GetMapping("agentAccountChargeAuditEditPage/{id}")
    public String agentAccountChargeAuditEditPage(@PathVariable("id") String id)
    {
        return agentAccountChargeAuditSer.agentAccountChargeAuditEditPage(id);
    }
    
    @PutMapping("agentAccountChargeEditName")
	@ResponseBody
	public String agentAccountChargeEditName(HttpServletRequest req){
		return agentAccountChargeAuditSer.agentAccountChargeEditName(req);
	}
    
    @GetMapping("/agentAccountChargeAuditReport")
    public void agentAccountChargeAuditReport(HttpServletRequest req,HttpServletResponse resp) {
    	
    	agentAccountChargeAuditSer.agentAccountChargeAuditReport(req,resp);
    }
    
    
	
}
