package com.hyfd.controller.mp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.BatchOfChargerSer;
import com.hyfd.service.mp.GuoMeiSer;
import com.hyfd.service.mp.TuTuBiSer;
import com.hyfd.service.mp.YuanTeSer;

@Controller
public class BatchOfCharger extends BaseController{

	@Autowired
	YuanTeSer yuante; //远特
	
	@Autowired
	GuoMeiSer guomei; //国美
	
	@Autowired
	TuTuBiSer tutubi; //兔兔币
	
	@Autowired
	BatchOfChargerSer batchOfChargerSer;
	
	@GetMapping("BatchOfCharger")
	public String batchOfCharger() {
		return "mp/batchOfCharger";
	}
	
	@GetMapping("BatchOfChargerList")
	public String batchOfChargerList() {
		return "mp/batchOfChargerList";
	}
    /**
     * 话费充值
     *
     * @param file
     * @return
     * 
     */
    @RequestMapping(value = "/charge", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,@RequestParam("state") String state,HttpServletRequest req) {
    	
    	int count = 0;
    	if(state.equals("0")) { //进入国美批量充值
    		count = guomei.recharge(file,req);
    	}
    	if(state.equals("1")) { //进入远特批量充值
    		count = yuante.recharge(file,req);
    	}
    	if(state.equals("2")) { //进入兔兔币批量充值
    		count = tutubi.recharge(file,req);
    	}
    	return ""+count;
    }
    
    /**
     * 验证登录密码
     * @param pass
     * @param response
     */
    @RequestMapping(value = "/validate", method=RequestMethod.POST)
    public void validate(@RequestParam("pass") String pass,HttpServletResponse response) {
    	String password = "~a1127^";
    	String msg = "0";
    	if(password.equals(pass)) {
    		msg = "1";
    	}
    	try {
			response.getWriter().write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 发送验证码
     * @param pass
     * @param response
     * 小面值充值网址：http://120.26.134.145:8001/Batchofcharger/NewFile.html
     * 充值密码：~a1127^	支持远特，国美和兔兔币三种小面值充值
     */
    @RequestMapping(value = "/login4Cookie", method=RequestMethod.POST)
    public void login4Cookie(@RequestParam("account") String account,@RequestParam("password") String password,
    							HttpServletResponse response) {
    	String msg = tutubi.login(account,password);
    	try {
			response.getWriter().write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "/batchOfChargerOrder", method=RequestMethod.GET)
    @ResponseBody
    public String batchOfChargerOrder(HttpServletRequest req) {
    	
    	return batchOfChargerSer.batchOfChargerOrder(req);
    }
    
    /**
	 * 导出报表
	 * @param req
	 */
    @RequestMapping(value = "/deriveBatch", method=RequestMethod.GET)
	public void deriveStatement(HttpServletRequest req,HttpServletResponse res) {
		batchOfChargerSer.deriveStatement(req,res);
	}
}
