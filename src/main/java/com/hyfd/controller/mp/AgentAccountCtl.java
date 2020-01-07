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
import com.hyfd.service.mp.AgentAccountSer;

@Controller
public class AgentAccountCtl extends BaseController
{
    
    @Autowired
    private AgentAccountSer agentAccountSer;
    
    @GetMapping("agentAccountEditPage/{id}")
    public String agentAccountEditPage(@PathVariable("id") String id)
    {
        return agentAccountSer.agentAccountEditPage(id);
    }
    
    @GetMapping("agent/queryBalance")
    @ResponseBody
    public String queryAgentBalance(HttpServletRequest request, HttpServletResponse response)
    {
        return agentAccountSer.queryAgentBalance(request, response);
    }
    
    @PostMapping("allotBalance")
    public String allotBalance(HttpServletRequest request, HttpServletResponse response)
    {
        return agentAccountSer.allotBalance(request, response);
    }
    
    @GetMapping("getAgentAccount/{suId}")
    @ResponseBody
    public String getAgentAccount(@PathVariable("suId") String suId)
    {
        return agentAccountSer.getAgentAccount(suId);
    }
    
}
