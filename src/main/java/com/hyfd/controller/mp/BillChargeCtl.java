package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.BillChargeSer;
import com.hyfd.service.mp.PhoneSectionSer;


@Controller
public class BillChargeCtl extends BaseController{

	@Autowired 
	private PhoneSectionSer phoneSectionSer;
	@Autowired 
	private BillChargeSer billChargeSer;
	
	/**
	 * 跳转到流量单号充值页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billSingleRechargePage")
	public String billSingleRechargePage() {
		return "mp/billSingleRecharge";
	}
	
	/**
	 * 跳转到流量批号充值页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billBatchRechargePage")
	public String billBatchRechargePage() {
		return "mp/billBatchRecharge";
	}	
	
	/**
	 * 跳转到同面值话费充值页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billWithParValueVolumeRechargePage")
	public String billWithParValueVolumeRechargePage() {
		return "mp/billWithParValueVolumeRechargePage";
	}	
	
	/**
	 * 流量充值时根据手机号查询号段、流量包
	 * */
	@GetMapping("sectionAndBillPkgGet")
	@ResponseBody
	public String sectionAndBillPkgGet(HttpServletRequest req){
		return billChargeSer.sectionAndBillPkgGet(req);
	}
	
	/**
	 * 跳转到话费批量查询页面 使用get方式提交
	 * @param req
	 * @return
	 */
	@GetMapping("billBatchQueryPhonePage")
	public String billBatchQueryPhonePage() {
		return "mp/billBatchQueryPhone";
	}	
	
	/**
	 * 检测手机号有关信息,返回给页面显示
	 * 
	 */
	@PostMapping("billChargeBatchCheckPhone")
	@ResponseBody
	public String batchCheckPhone(HttpServletRequest req){
		return billChargeSer.batchCheckPhone(req);
	}
	/**
	 * 检测同面值充值手机号有关信息,返回给页面显示
	 * 
	 */
	@PostMapping("billChargeBatchCheckPhoneTwo")
	@ResponseBody
	public String batchCheckPhoneTwo(HttpServletRequest req){
		return billChargeSer.batchCheckPhoneTwo(req);
	}
	
	/**
	 * 获取号段归属地
	 * 
	 */
	@RequestMapping("order/getPhoneAddress")
	@ResponseBody
	public String getPhoneAddress(HttpServletRequest req){
		return billChargeSer.getPhoneAddress(req);
	}
	
	/**
	 * 批量获取手机号码信息,地区、姓名、余额等
	 * */
	@PostMapping("batchQueryPhoneInfoGet")
	@ResponseBody
	public String batchQueryPhoneInfoGet(HttpServletRequest req){
		return billChargeSer.batchQueryPhoneInfo(req);
	}
	
	/**
	 * 检测手机号有关信息,返回给页面显示
	 * 
	 */
	@PostMapping("billQueryBatchCheckPhone")
	@ResponseBody
	public String batchQueryCheckPhone(HttpServletRequest req){
		return billChargeSer.batchCheckPhoneByQuery(req);
	}
}
