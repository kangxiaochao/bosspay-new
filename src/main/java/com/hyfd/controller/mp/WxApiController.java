package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller("wxApi")
public class WxApiController {

	@PostMapping("getPhoneInfo")
	public String getPhoneInfo(HttpServletRequest request,HttpServletResponse response){
		return "";
	}
	
}
