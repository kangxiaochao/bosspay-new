package com.hyfd.controller.sys;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.common.utils.CaptchaUtil;
import com.hyfd.service.sys.SysUserSer;


@Controller
//@Scope("prototype") //开启非单例模式 用于并发控制
public class LoginCtl extends BaseController{
	
	@Autowired 
	private SysUserSer sysUserSer;
	
	/**
	 * 登陆页面只能用get方式提交
	 * @return
	 */
	@GetMapping("loginPage")
	public String loginPage(){
		return "login";
	}
	
	/**
	 * 主页显示 只能用get方式提交
	 * @param request
	 * @return
	 */
	@GetMapping("/mainPage")
	public String mainPage() {
		return "main";
	}
	
	/**
	 * 跳转到403页面只能用get方式提交
	 * @param request
	 * @return
	 */
	@GetMapping("/page403")
	public String page403() {
		return "page403";
	}
	
	/**
	 * 创建用户会话 只能用post方式提交
	 * @param req
	 * @return
	 */
	@PostMapping("/login")
	public String login(HttpServletRequest req) {
		return sysUserSer.login(req);
	}
	
	@DeleteMapping("logout")
	@ResponseBody
	public String logout(){
		sysUserSer.logout();
		return "loginPage";
	}
	
	/**
	 * 获取登录页面验证码，把生成的图片输出到前台
	 * */
	@GetMapping("captcha")
	public void captcha(HttpServletRequest req,HttpServletResponse res){
		sysUserSer.captcha(req,res);
	}
	
	/**
	 * 任缴费验证用户是否存在
	 * @param req
	 * @return
	 */
	@RequestMapping("order/appLogin")
	@ResponseBody
	public String appLogin(HttpServletRequest req) {
		return sysUserSer.appLogin(req);
	}
}
