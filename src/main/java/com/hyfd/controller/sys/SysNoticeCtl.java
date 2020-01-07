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

import com.hyfd.service.sys.SysNoticeSer;

@Controller
public class SysNoticeCtl extends BaseController
{
    
    @Autowired
    private SysNoticeSer sysNoticeSer;
    
    /**
     * @功能描述：	跳转到公告列表页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNoticeListPage")
    public String sysNoticeListPage()
    {
    	return "system/sysNoticeList";
    }
    
    /**
     * @功能描述：	获取公告列表信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @GetMapping("sysNotice")
	@ResponseBody
	public String sysNoticeGet(HttpServletRequest req) {
		return sysNoticeSer.sysNoticeList(req);
	}
    
    /**
     * @功能描述：	跳转到公告添加页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNoticeAddPage")
    public String sysNoticeAddPage() {
    	return "system/sysNoticeAdd";
    }
    
    /**
     * @功能描述：	添加公告信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @PostMapping("sysNotice")
    public String sysNoticePost(HttpServletRequest req) {
    	return sysNoticeSer.sysNoticeAdd(req);
    }
    
    /**
     * @功能描述：	跳转到公告编辑页面
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @return
     */
    @GetMapping("sysNoticeEditPage/{id}")
    public String sysNoticeEditPage(@PathVariable("id") String id) {
    	return sysNoticeSer.sysNoticeEditPage(id);
    }
    
    /**
     * @功能描述：	更新公告列表信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @PutMapping("sysNotice")
    @ResponseBody
	public String sysNoticePut(HttpServletRequest req) {
		return sysNoticeSer.sysNoticeEdit(req);
	}
    
    /**
     * @功能描述：	删除公告列表指定信息
     *
     * @作者：zhangpj		@创建时间：2017年8月9日
     * @param req
     * @return
     */
    @DeleteMapping("sysNotice/{id}")
    @ResponseBody
	public String sysNoticeDelete(@PathVariable("id") String id) {
		return sysNoticeSer.sysNoticeDel(id);
	}
    
    /**
     * @功能描述： 查询平台公告
     * @param req
     * @return
     */
    @GetMapping("status/sysNoticeLists")
    @ResponseBody
    public String sysNoticeList(HttpServletRequest req) {
    	
    	return sysNoticeSer.sysNoticeList1(req);
    }
}
