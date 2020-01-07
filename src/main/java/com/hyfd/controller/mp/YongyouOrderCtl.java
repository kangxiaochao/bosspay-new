package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.YongyouOrderSer;

@Controller
public class YongyouOrderCtl extends BaseController{
	@Autowired
	YongyouOrderSer yongyouOrderSer;
	
	
	@GetMapping("yongYouCookies")
	@ResponseBody
	public String tutubiCookies(HttpServletRequest req,HttpServletResponse res){
		return yongyouOrderSer.yongYouCookies(req, res);
	}	
}
