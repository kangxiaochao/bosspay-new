package com.hyfd.controller.mp;

import com.hyfd.service.mp.CookiesSer;
import com.hyfd.service.mp.IpSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
@Controller
public class CookiesCtl {


    @Autowired
    private CookiesSer cookiesSer;

    /**
     * @功能描述：	跳转到cookies列表页面
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @return
     */
    @GetMapping("cookiesListPage")
    public String ipListPage()
    {
        return "mp/cookiesList";
    }



    /**
     * @功能描述：	获取cookies列表信息
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @param req
     * @return
     */
    @GetMapping("cookies")
    @ResponseBody
    public String ipGet(HttpServletRequest req) {
        return cookiesSer.cookiesList(req);
    }
}
