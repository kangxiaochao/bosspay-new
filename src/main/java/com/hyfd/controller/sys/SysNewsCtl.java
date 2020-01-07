package com.hyfd.controller.sys;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hyfd.service.sys.SysNewsSer;

@Controller
public class SysNewsCtl extends BaseController
{
    
    @Autowired
    private SysNewsSer sysNewsSer;
    
    /**
     * @功能描述：	跳转到公告列表页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNewsListPage")
    public String sysNewsListPage()
    {
    	return "system/sysNewsList";
    }
    
    /**
     * @功能描述：	获取公告列表信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @GetMapping("sysNews")
	@ResponseBody
	public String sysNewsGet(HttpServletRequest req) {
		return sysNewsSer.sysNewsList(req);
	}
    
    /**
     * @功能描述：	跳转到公告添加页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNewsAddPage")
    public String sysNewsAddPage() {
    	return "system/sysNewsAdd";
    }
    
    /**
     * @功能描述：	添加公告信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @PostMapping("sysNews")
    public String sysNewsPost(HttpServletRequest req) {
    	return sysNewsSer.sysNewsAdd(req);
    }
    
    /**
     * @功能描述：	跳转到公告编辑页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNewsEditPage/{id}")
    public String sysNewsEditPage(@PathVariable("id") String id) {
    	return sysNewsSer.sysNewsEditPage(id);
    }
    
    /**
     * @功能描述：	更新公告列表信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @PutMapping("sysNews")
    @ResponseBody
	public String sysNewsPut(HttpServletRequest req) {
		return sysNewsSer.sysNewsEdit(req);
	}
    
    /**
     * @功能描述：	删除公告列表指定信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @DeleteMapping("sysNews/{id}")
    @ResponseBody
	public String sysNewsDelete(@PathVariable("id") String id) {
		return sysNewsSer.sysNewsDel(id);
	}
}
