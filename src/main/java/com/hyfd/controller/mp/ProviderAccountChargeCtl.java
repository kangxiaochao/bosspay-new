package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.mp.ProviderAccountChargeSer;


@Controller
public class ProviderAccountChargeCtl {

	@Autowired
	private ProviderAccountChargeSer providerAccountChargeSer;
	
	@GetMapping("providerAccountChargePage")
	public String providerAccountChargePage() {
		return "mp/providerAccountChargeList";
	}
	
	@GetMapping("providerAccountChargeList")
	@ResponseBody
	public String providerAccountChargeList(HttpServletRequest req) {
		return providerAccountChargeSer.providerAccountChargeList(req);
	}
}
