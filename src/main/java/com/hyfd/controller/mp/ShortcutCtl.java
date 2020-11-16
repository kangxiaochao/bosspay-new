package com.hyfd.controller.mp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.deal.Bill.HaiHangBiBillDeal;
import com.hyfd.service.mp.DiXinTongKeyService;
import com.hyfd.task.DixintongKeyTask;

/**
 * <h5>描述:快捷操作集合</h5>
 * 
 * @author zhangpj	2019年1月15日 
 */
@Controller
public class ShortcutCtl extends BaseController {
	
	@Autowired
	DiXinTongKeyService diXinTongKeyService;
	
	@RequestMapping("shortcutPage")
	public String shortcutPage(){
		return "mp/shortcutList";
	}
	
//	@RequestMapping("shortcut/haihangbi/initTimer")
//	@ResponseBody
//	public String initTimer(){
		// 初始化海航币登录时长(设置为未登录状态)
//		HaiHangBiBillDeal.initTimer();
//		return "切换海航币账号成功";
//	}
	
	@RequestMapping("shortcut/dixintong/updateKey")
	@ResponseBody
	public String updateKey(){
		// 更新迪信通卡密
		return diXinTongKeyService.updateDixintongKey(1);
	}
}
