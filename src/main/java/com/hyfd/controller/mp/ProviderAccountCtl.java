package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.mp.ProviderAccountSer;

@Controller
public class ProviderAccountCtl {
	
	@Autowired
	ProviderAccountSer providerAccountSer;
	
    @GetMapping("providerAccountEditPage/{id}")
	public String providerAccountEditPage(@PathVariable("id") String id) {
		return providerAccountSer.providerAccountEditPage(id);
	}
    
	@PutMapping("providerAccount/{id}")
	@ResponseBody
	public String providerAccountPut(@PathVariable("id") String id, HttpServletRequest req) {
		return providerAccountSer.providerAccountEdit(req,id);
	}

}
