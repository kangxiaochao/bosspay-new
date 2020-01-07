package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.dataSer;

@Controller
public class dataCtl extends BaseController{

	@Autowired
	dataSer dSer;
	
	@GetMapping("countProfit")
    @ResponseBody
    public String countProfit(HttpServletRequest request, HttpServletResponse response)
    {
        return dSer.countProfit(request, response);
    }
	
	@GetMapping("countPhysicalChannelProfit")
    @ResponseBody
    public String countPhysicalChannelProfit(HttpServletRequest request, HttpServletResponse response)
    {
        return dSer.countPhysicalChannelProfit(request, response);
    }
	
	@GetMapping("dataChartsPage")
	public String dataChartsPage(){
		return "mp/dataCharts";
	}
    
    @GetMapping("getChartsData")
    @ResponseBody
    public String getChartsData(HttpServletRequest request, HttpServletResponse response)
    {
        return dSer.getChartsData(request, response);
    }
    
    @GetMapping("getChannelChartsData")
    @ResponseBody
    public String getChannelChartsData(HttpServletRequest request, HttpServletResponse response)
    {
        return dSer.getChannelChartsData(request, response);
    }
	
}
