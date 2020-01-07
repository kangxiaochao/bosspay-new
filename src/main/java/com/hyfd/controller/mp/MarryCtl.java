package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.MarrySer;

@Controller
public class MarryCtl extends BaseController{

	@Autowired
	private MarrySer marrySer;
	
	@GetMapping("marry")
	public String agentAccountChargeAuditPage() {
		return "mp/marry";
	}
	
	@PostMapping("marry")
	@ResponseBody
	public void  marry(MultipartFile file, HttpServletResponse res) {
		marrySer.marry(file, res);
	}
}
