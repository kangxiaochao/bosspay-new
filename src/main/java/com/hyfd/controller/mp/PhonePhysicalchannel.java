package com.hyfd.controller.mp;


import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.PhonePhysicalchannelSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PhonePhysicalchannel extends BaseController {

    @Autowired
    private PhonePhysicalchannelSer phonePhysicalchannelSer;

    /**
     * 跳转到列表页面 使用get方式提交
     *
     * @param
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

    /**
     * 删除通道号段管理
     *
     */
    @DeleteMapping("phonePhysicalchanneldelate/{id}")
    @ResponseBody
    public String phonePhysicalchanneldelate(@PathVariable("id") String id){
        return phonePhysicalchannelSer.deletePhonePhysicalchannel(id);
    }

    /**
     * 通道号段详细
     * @param id
     * @return
     */
    @GetMapping("phonePhysicalchannelDetail/{id}")
    public String phoneSectionDetail(@PathVariable("id") String id) {
        return phonePhysicalchannelSer.phonePhysicalchannelDetail(id);
    }

    /**
     * 通道号段编辑页面
     * @param id
     * @return
     */
    @GetMapping("phonePhysicalchannelEditPage/{id}")
    public String phoneSectionEditPage(@PathVariable("id") String id) {
        return phonePhysicalchannelSer.PhysicalchannelEditPage(id);
    }

    /**
     * 修改通道号段信息
     *
     * @param req
     * @return
     */
    @PutMapping("phonePhysicalchannel/{id}")
    @ResponseBody
    public String phonePhysicalchannelPut(@PathVariable("id") String id, HttpServletRequest req) {
        return phonePhysicalchannelSer.phonePhysicalchannel(req,id);
    }
}
