package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.CallBackForProviderSer;

@Controller
public class CallBackForProviderCtl extends BaseController
{
    
    @Autowired
    CallBackForProviderSer callBackForProviderSer;
    
    /**
     * 好亚飞达话费状态回调
     * 
     * @author lks 2016年12月13日上午11:21:21
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/status/HyfdBill")
    @ResponseBody
    public String hyfdBillCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackForHyfdBill(request, response);
    }
    
    /**
     * 蓝猫话费回调
     * 
     * @author zhangjun 2017年7月25日
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/status/LanMao")
    @ResponseBody
    public String LanMaoCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackLanMao(request, response);
    }
    
    /**
     * 北纬话费回调
     * 
     * @author zhangjun 2017年7月25日
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/status/BeiWei")
    @ResponseBody
    public String BeiWeiCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackBeiWei(request, response);
    }
    
    /**
     * 联想话费回调
     * 
     * @author zhangjun 2017年7月25日
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/status/LianXiang")
    @ResponseBody
    public String LianXiangCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackLianXiang(request, response);
    }
    
    
    /**
     * 享缴费话费回调
     * 
     * @author zhangjun 2017年7月25日
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/status/XiangJiaoFei")
    @ResponseBody
    public String XiangJiaoFeiCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackXiangJiaoFei(request, response);
    }
    
    @GetMapping("/status/SaiNong")
    @ResponseBody
    public String SaiNongCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackSaiNong(request, response);
    }
    
    @GetMapping("/status/E19")
    @ResponseBody
    public String E19Callback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBack19E(request, response);
    }
    
    @GetMapping("/status/DingXin")
    @ResponseBody
    public String DingXinCallback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackDingxin(request, response);
    }
    
    @PostMapping("/status/YuanTe")
    @ResponseBody
    public String YuanTeback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackYuanTe(request, response);
    }
    
    @PostMapping("/status/AiShiDe")
    @ResponseBody
    public String AiShiDeback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackAiShiDe(request, response);
    }
    
    @RequestMapping("/status/YinSheng")
    @ResponseBody
    public String YinShengback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackYinSheng(request, response);
    }
    
    @RequestMapping("/status/ZiTeng")
    @ResponseBody
    public String ZiTengback(HttpServletRequest request, HttpServletResponse response)
    {
        return callBackForProviderSer.statusBackZiTeng(request, response);
    }
    
    @RequestMapping("/status/HuaJiShiJie")
    @ResponseBody
    public String HuaJiShiJieback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackHuaJiShiJie(request, response);
    }
    
    @RequestMapping("/status/HaiHang")
    @ResponseBody
    public String HaiHangback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackHaiHang(request, response);
    }
    
    @RequestMapping("/status/QianMiGongHuo")
    @ResponseBody
    public String QianMiGongHuoback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackForQianmigonghuo(request, response);
    }
    
    @RequestMapping("/status/JuHeGongHuo")
    @ResponseBody
    public String JuHeGongHuoback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackForJuhegonghuo(request, response);
    }
    
    @RequestMapping("/status/ZhongXing")
    @ResponseBody
    public String zhongXingback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackZhongXing(request, response);
    }
    
    @RequestMapping("/status/MaiYuan")
    @ResponseBody
    public String maiYuanback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackMaiYuan(request, response);
    }
    
    @RequestMapping("/status/YouQi")
    @ResponseBody
    public String youQiback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackYouQi(request, response);
    }
    
    @RequestMapping("/status/KongChong")
    @ResponseBody
    public String QuanGuoKongChong(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackKongChong(request, response);
    }
    
    @RequestMapping("/status/YunLiuBillDeal")
    @ResponseBody
    public String yunLiuBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.statusBackYunliu(request, response);
    }
    
    @RequestMapping("/status/xingBoHaiBack")
    @ResponseBody
    public String xingBoHaiBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.xingBoHaiBack(request, response);
    }
    
    @RequestMapping("/status/pengBoShiBack")
    @ResponseBody
    public String pengBoShiBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.pengBoShiBack(request, response);
    }
    
    @RequestMapping("/status/FeiYouBack")
    @ResponseBody
    public String FeiYouBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.FeiYouBack(request, response);
    }
    /**
     * 海口雨水文化回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/HaiKouYuShuiBack")
    @ResponseBody
    public String HaiKouYuShuiBack(HttpServletRequest request,HttpServletResponse response) 
    {
		return callBackForProviderSer.HaiKouYuShuiBack(request, response);
    }
    /**
     * 云普回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/YunPuBack")
    @ResponseBody
    public String YunPuBack(HttpServletRequest request,HttpServletResponse response) 
    {
		return callBackForProviderSer.YunPuBack(request, response);
    }
    /**
     * 新 千米回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/QianMiTwo")
    @ResponseBody
    public String QianMiTwoback(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.QianMiTwoback(request, response);
    }
    /**
     * 连连科技回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/lianLianKeJiBack")
    @ResponseBody
    public String lianLianKeJiBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.lianLianKeJiBack(request, response);
    }
    /**
     * 满帆回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/ManFanBack")
    @ResponseBody
    public String ManFanBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.ManFanBack(request, response);
    }
    /**
     * 玖玥回调
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/status/JiuYueBack")
    @ResponseBody
    public String JiuYueBack(HttpServletRequest request, HttpServletResponse response)
    {
    	return callBackForProviderSer.JiuYueBack(request, response);
    }
}
