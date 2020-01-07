package com.hyfd.controller.mp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.IpSer;

@Controller
public class IpCtl extends BaseController
{
    
    @Autowired
    private IpSer ipSer;
    
    /**
     * @功能描述：	跳转到IP列表页面
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @return
     */
    @GetMapping("ipListPage")
    public String ipListPage()
    {
    	return "mp/ipList";
    }
    
    /**
     * @功能描述：	获取IP列表信息
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @param req
     * @return
     */
    @GetMapping("ip")
	@ResponseBody
	public String ipGet(HttpServletRequest req) {
		return ipSer.ipList(req);
	}
    
    /**
     * @功能描述：	跳转到IP添加页面
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @return
     */
    @GetMapping("ipAddPage")
    public String ipAddPage() {
    	return "mp/ipAdd";
    }
    
    /**
     * @功能描述：	添加IP信息
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @param req
     * @return
     */
    @PostMapping("ip")
    public String ipPost(HttpServletRequest req) {
    	return ipSer.ipAdd(req);
    }
    
    /**
     * @功能描述：	跳转到IP编辑页面
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @return
     */
    @GetMapping("ipEditPage/{id}")
    public String ipEditPage(@PathVariable("id") String id) {
    	return ipSer.ipEditPage(id);
    }
    
    /**
     * @功能描述：	更新IP列表信息
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @param req
     * @return
     */
    @PutMapping("ip")
    @ResponseBody
	public String ipPut(HttpServletRequest req) {
		return ipSer.ipEdit(req);
	}
    
    /**
     * @功能描述：	删除IP列表指定信息
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     * @param req
     * @return
     */
    @DeleteMapping("ip/{id}")
    @ResponseBody
	public String ipDelete(@PathVariable("id") String id) {
		return ipSer.ipDel(id);
	}
}
