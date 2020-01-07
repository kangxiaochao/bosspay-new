package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.WebSubmitOrderSer;
import com.hyfd.service.mp.chargeOrderSer;

@Controller
public class chargeOrderCtl extends BaseController
{
    
    @Autowired
    chargeOrderSer chargeOrderSer;
    
    @Autowired
    WebSubmitOrderSer webSubmitOrderSer;
    
    @GetMapping("order/quotaOrder")
    @ResponseBody
    public String submitOrder1(HttpServletRequest request)
    {
        return chargeOrderSer.submitOrder(request);
    }
    
    @RequestMapping("order/webSubmit")
    @ResponseBody
    public String webSubmit(HttpServletRequest request)
    {
        return webSubmitOrderSer.webSubmitOrder(request);
    }
    
    @RequestMapping("order/bitchWebSubmit")
    @ResponseBody
    public String bitchWebSubmit(HttpServletRequest request)
    {
        return webSubmitOrderSer.webSubmitOrder(request);
    }
    
    @GetMapping("order/queryOrder")
    @ResponseBody
    public String queryOrder(HttpServletRequest request)
    {
        return chargeOrderSer.queryOrder(request);
    }
    
    @PostMapping("order/quotaXiChengOrder")
    @ResponseBody
    public String submitXiCheng(HttpServletRequest request)
    {
        return chargeOrderSer.submitXiCheng(request);
    }
    
    @PostMapping("order/queryXiChengOrder")
    @ResponseBody
    public String queryXiChengOrder(HttpServletRequest request)
    {
        return chargeOrderSer.queryXiChengOrder(request);
    }
    
}
