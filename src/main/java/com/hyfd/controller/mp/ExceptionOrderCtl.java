package com.hyfd.controller.mp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ExceptionOrderSer;

@Controller
public class ExceptionOrderCtl extends BaseController
{
    
    @Autowired
    ExceptionOrderSer exceptionOrderSer;
    
    /**
     * 跳转到异常订单页面
     * 
     * @author lks 2017年3月8日下午7:31:33
     * @param request
     * @param response
     * @return
     */
    @GetMapping("ExceptionOrderListPage")
    public String ExceptionOrderListPage(HttpServletRequest request, HttpServletResponse response)
    {
        return "exceptionOrder/exceptionOrderList";
    }
    
    /**
     * 复充方法
     * 
     * @author lks 2017年3月8日下午7:33:11
     * @param request
     * @param response
     * @return 复充成功与否
     */
    @GetMapping("reCharge")
    @ResponseBody
    public String reCharge(HttpServletRequest request)
    {
        return exceptionOrderSer.batchReCharge(request);
    }
    
    /**
     * 原通道复充方法
     * 
     * @author lks 2017年3月8日下午7:33:11
     * @param request
     * @param response
     * @return 复充成功与否
     */
    @GetMapping("reChargeOld")
    @ResponseBody
    public String reChargeOld(HttpServletRequest request)
    {
        return exceptionOrderSer.batchReChargeOld(request);
    }
    
    /**
     * 退款方法
     * 
     * @author lks 2017年3月8日下午7:33:17
     * @param request
     * @param response
     * @return 退款成功与否
     */
    @GetMapping("refund")
    @ResponseBody
    public String refund(HttpServletRequest request)
    {
        return exceptionOrderSer.batchReFund(request);
    }
    
    /**
     * 异常订单改成功
     * @author lks 2017年9月8日下午3:37:42
     * @param request
     * @param response
     */
    @GetMapping("changeSucc")
    @ResponseBody
    public String changeSucc(HttpServletRequest request,HttpServletResponse response){
    	return exceptionOrderSer.changeSucc(request, response);
    }
    
    /**
     * @功能描述： 根据条件获取订单列表数据
     *
     * @作者：hyj @创建时间：2017年1月12日
     * @param req
     * @return
     */
    @GetMapping("exceptionOrderListPage")
    public String exceptionOrderListPage()
    {
        return "mp/exceptionOrderList";
    }
    
    @GetMapping("exceptionOrderList")
    @ResponseBody
    public String exceptionOrderList(HttpServletRequest req)
    {
        return exceptionOrderSer.exceptionOrderList(req);
    }
    
    @GetMapping("exceptionOrderParamPage")
    public String exceptionOrderParamPage(HttpServletRequest req)
    {
        exceptionOrderSer.orderParamPage(req);
        return "mp/orderParam";
    }
    
    @GetMapping("exceptionOrderParam")
    @ResponseBody
    public String exceptionOrderParam(HttpServletRequest req)
    {
        return exceptionOrderSer.getOrderParam(req);
    }
    
    @GetMapping("judgeTianmaoRoleException")
    @ResponseBody
    public Map<String, Object> judgeTianmaoRoleException(HttpServletRequest req)
    {
        String suId = req.getParameter("suId");
        String id = req.getParameter("ids");
        String[] ids = id.split(",");
        return exceptionOrderSer.judgeTianmaoRoleException(suId, ids);
    }
    
    @GetMapping("exportException")
    public void exportException(HttpServletRequest req,HttpServletResponse res) {
    	exceptionOrderSer.exportException(req, res);
    }
}
