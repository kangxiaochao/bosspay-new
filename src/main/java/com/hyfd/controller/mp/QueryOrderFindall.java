package com.hyfd.controller.mp;


import com.hyfd.service.mp.QueryOrderFindallSer;
import org.bouncycastle.crypto.engines.AESEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.resource.ResourceTransformerChain;

import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class QueryOrderFindall {


    @Autowired
    QueryOrderFindallSer queryOrderFindallSer;

    @GetMapping("queryOrderfindall")
    public String queryOrderInfoPage(HttpServletRequest request, HttpServletResponse response){
        return "mp/queryOrderfindall";
    }


    //手动调用查单接口去查订单状态(单查)
    @PostMapping("findbytask")
    @ResponseBody
    public String findorderbyTask(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> m=new HashMap<String, Object>();
        Enumeration<String> e=request.getParameterNames();
        while(e.hasMoreElements()){
            String s=(String) e.nextElement();
            m.put(s, request.getParameter(s).trim());
        }
        ArrayList<Map> maps = new ArrayList<>();
        maps.add(m);
        return(queryOrderFindallSer.findorderbyTask(maps));
    }

    //通过导入excel的形式去掉查单接口.(excel导入)
    @PostMapping("findbytaskbyexcel")
    @ResponseBody
    public String findbytaskexcel(MultipartFile file, HttpServletRequest req) {

        ArrayList<Map> maps = queryOrderFindallSer.SectionAdd(file, req);
        return(queryOrderFindallSer.findorderbyTask(maps));
    }

}
