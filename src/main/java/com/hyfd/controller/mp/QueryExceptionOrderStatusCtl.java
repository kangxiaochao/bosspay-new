package com.hyfd.controller.mp;


import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.QueryExceptionOrderStatusSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class QueryExceptionOrderStatusCtl extends BaseController {


    @Autowired
    QueryExceptionOrderStatusSer queryExceptionOrderStatusSer;

    @GetMapping("queryOrderStatusPage")
    public String queryOrderInfoPage(HttpServletRequest request, HttpServletResponse response){
        return "mp/queryExceptionOrderStatus";
    }


    @GetMapping("queryOrderStatusInq")
    @ResponseBody
    public String qeuryOrderStatus(HttpServletRequest req){
        return queryExceptionOrderStatusSer.qeuryOrderStatus(req);
    }

}
