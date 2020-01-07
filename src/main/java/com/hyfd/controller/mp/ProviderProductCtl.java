package com.hyfd.controller.mp;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.hyfd.service.mp.BillPkgSer;
import com.hyfd.service.mp.ProviderPhysicalChannelSer;
import com.hyfd.service.mp.ProviderProductSer;

@Controller
public class ProviderProductCtl {

	@Autowired
	ProviderProductSer providerProductSer;

	@Autowired
	BillPkgSer billPkgSer;

	@Autowired
	ProviderPhysicalChannelSer providerPhysicalChannelSer;

	@GetMapping("providerProduct")
	public String ProviderProduct() {
		return "mp/providerProductAdd";
	}
	@GetMapping("providerProductList")
	public String providerProductList(){
		return "mp/providerProductList";
	}

	@GetMapping("providerProductEdit")
	public String providerProductEdit(){
		return "mp/providerProductEdit";
	}
	
	@RequestMapping("providerProducts")
	public String providerProduct(HttpServletRequest req) {
		return providerProductSer.ProviderProductAdd(req);
	}

	@PostMapping("pkg")
	public void ProcessRequest(HttpServletResponse res) {
		try {
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(billPkgSer.billPkgListGet());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@PostMapping("physicalChannel")
	public void physicalChannel(HttpServletRequest req,HttpServletResponse res) {
		try {
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(providerPhysicalChannelSer.providerPhysicalChannelAllList(req));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@PostMapping("queryProviderProductList")
	@ResponseBody
	public String queryProviderProductList(HttpServletRequest req){
		return providerProductSer.queryProviderProductList(req);
	}
	
	@PostMapping("delProvider")
	public void delProvider(HttpServletRequest req,HttpServletResponse res){
		try {
			res.setCharacterEncoding("utf-8");
			res.getWriter().print(providerProductSer.delProvider(req));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@GetMapping("queryById/{id}")
	public String queryById(@PathVariable("id") String id){
		return providerProductSer.queryById(id);
	}
	
	@PostMapping("updateProvider")
	public String updateProvider(HttpServletRequest req){
		return providerProductSer.updateProvider(req);
	}
}
