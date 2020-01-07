package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.getOrderPriceSer;

@Controller
public class getOrderPriceCtl extends BaseController
{
    
    @Autowired
    getOrderPriceSer getOrderPriceSer;
    
    @RequestMapping("order/getOrderPriceByPhone")
    @ResponseBody
    public String getOrderPriceByPhone(HttpServletRequest request)
    {
        return getOrderPriceSer.getOrderPriceByPhone(request);
    }
    
    
    @RequestMapping("order/getPhoneInfo")
    @ResponseBody
    public String getPhoneInfo(HttpServletRequest request)
    {
        return getOrderPriceSer.getPhoneInfo(request);
    }
    
    @RequestMapping("order/getOrderPriceByNumber")
    @ResponseBody
    public String getOrderPriceByNumber(HttpServletRequest request)
    {
        return getOrderPriceSer.getOrderPriceByNumber(request);
    }
}
