package com.hyfd.controller.mp;


import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.PhonePhysicalchannelSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PhonePhysicalchannel extends BaseController {

    @Autowired
    private PhonePhysicalchannelSer phonePhysicalchannelSer;

    /**
     * 跳转到列表页面 使用get方式提交
     *
     * @param req
     * @return
     */
    @GetMapping("phonePhycicalchannelListPage")
    public String phonePhycicalchannelListPage() {
        return "mp/PhonePhysicalchannelList";
    }

    /**
     * 获取列表数据 只能使用get方式提交
     *
     * @param req
     * @return
     */
    @GetMapping("phonePhysicalchannel")
    @ResponseBody
    public String phoneSectionGet(HttpServletRequest req) {
        return phonePhysicalchannelSer.phonePhycicalchannelList(req);
    }

    /**
     * 创建通道号段信息对象只能用post方式来提交
     *
     * @param req
     * @return
     */
    @PostMapping("phonePhysicalchannel")
    public String phoneSectionPost(HttpServletRequest req) {
        return phonePhysicalchannelSer.phonePhysicalchannelAdd(req);
    }

    /**
     * 通道号段信息添加页面显示 只能使用get方式提交
     *
     * @return
     */
    @GetMapping("phonePhysicalchannelAddPage")
    public String phonePhysicalchannelAddPage() {
        return "mp/phonePhysicalchannelAdd";
    }
}
