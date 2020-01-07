package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.CardSer;

@Controller
public class CardCtl extends BaseController{

	@Autowired
	CardSer cardSer;
	
	@GetMapping("cardListPage")
	public String cardListPage(){
		return "mp/cardList";
	}
	
	@GetMapping("cardList")
	@ResponseBody
	public String cardList(HttpServletRequest request,HttpServletResponse response){
		return cardSer.cardList(request);
	}
	
	@GetMapping("exportCardTemp")
	public void exportCardTemp(HttpServletRequest request,HttpServletResponse response){
		cardSer.exportCardTemp(request, response);
	}
	
	@PostMapping("importCard")
	@ResponseBody
	public String importCard(HttpServletRequest request,HttpServletResponse response){
		return cardSer.importCard(request, response);
	}
	
	@GetMapping("detailOrder/{orderId}")
	@ResponseBody
	public String detailOrder(@PathVariable("orderId") String orderId){
		return cardSer.detailOrder(orderId);
	}
	
	@RequestMapping("updateCard")
	@ResponseBody
	public String updateCard(HttpServletRequest req) {
		
		return cardSer.updateCard(req);
	}
	
	@GetMapping("exportCard")
	public void exportCard(HttpServletRequest req,HttpServletResponse res){
		cardSer.exportCard(req, res);
	}
}
