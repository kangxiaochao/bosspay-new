package com.hyfd.controller.mp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.AgentBillDiscountSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class AgentBillDiscountCtl extends BaseController
{
    
    @Autowired
    private AgentBillDiscountSer agentBillDiscountSer;
    
    /**
     * 跳转到列表页面 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentBillDiscountListPage/{id}")
    public String agentBillDiscountListPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountListPage(id, req);
    }
    
    /**
     * 跳转到代理商折扣上传 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentBillDiscountUpload")
    public String agentBillDiscountUpload(HttpServletRequest req)
    {
        return "mp/agentBillDiscountUploadPage";
    }
    
    /**
     * 跳转到代理商折扣上传 使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentBillDiscountSet")
    public String agentBillDiscountSet(HttpServletRequest req)
    {
        return "mp/agentBillDiscountSetPage";
    }
    
    @RequestMapping("editCellDiscount")
    @ResponseBody
    public String editCellDiscount(HttpServletRequest request){
    	return agentBillDiscountSer.editCellDiscount(request);
    }
    
    
    @RequestMapping("saveVpd")
    @ResponseBody
    public String saveVpd(HttpServletRequest request){
    	return agentBillDiscountSer.saveVpd(request);
    }
    
    @RequestMapping("selectVpd")
    @ResponseBody
    public String selectVpd(HttpServletRequest request){
    	return agentBillDiscountSer.selectVpd(request);
    }
    /**
     * 动态获取折扣表的表头
     * @author lks 2017年12月14日下午3:19:44
     * @param agentId
     * @param providerId
     * @param req
     * @return
     */
    @GetMapping("getColModel/{agentId}/{providerId}")
    @ResponseBody
    public String getColModel(@PathVariable("agentId") String agentId,@PathVariable("providerId") String providerId, HttpServletRequest req){
    	return agentBillDiscountSer.getColModel(agentId, providerId, req);
    }
    
    /**
     * @Title:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年8月8日 上午9:48:43
     * @param @param agentId
     * @param @param providerId
     * @param @param mFile
     * @param @param req
     * @param @return
     * @param @throws Exception
     * @return List<String> 返回类型
     * @throws
     */
    @PostMapping("commitAgentBillDiscount/{agentId}")
    @ResponseBody
    public List<String> commitAgentBillDiscount(@PathVariable("agentId") String agentId,
        @RequestParam(value = "file", required = false) MultipartFile mFile, HttpServletRequest req)
        throws Exception
    {
        List<String> list = agentBillDiscountSer.commitAgentBillDiscount(agentId, mFile);
        return list;
    }
    
    /**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("agentBillDiscountList/{agentId}/{providerId}")
    @ResponseBody
    public String agentBillDiscountGet(@PathVariable("agentId") String agentId,
        @PathVariable("providerId") String providerId, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountList(agentId, providerId, req);
    }
    
    /**
     * @功能描述： 不分页根据条件获取全部代理商话费折扣列表数据
     *
     * @param req
     * @return
     */
    @GetMapping("agentBillDiscountAll")
    @ResponseBody
    public String agentBillDiscountAllGet(HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountAllList(req);
    }
    
    /**
     * 代理商话费折扣添加页面显示 只能使用get方式提交
     * 
     * @return
     */
    @GetMapping("agentBillDiscountAddPage/{id}")
    public String agentBillDiscountAddPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountAdd(id, req);
    }
    
    /**
     * 查询话费包折扣价格
     * */
    @GetMapping("agentBillDiscountGet")
    @ResponseBody
    public String agentBillDiscount(HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountGet(req);
    }
    
    /**
     * 创建代理商话费折扣对象只能用post方式来提交
     * 
     * @param req
     * @return
     */
    @PostMapping("agentBillDiscount")
    public String agentBillDiscountPost(HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountAdd(req);
    }
    
    /**
     * 显示详单页面要使用get方法并要在请求路径中传入代理商话费折扣编号数据
     * 
     * @param id
     * @return
     */
    @GetMapping("agentBillDiscountDetailPage/{id}")
    public String agentBillDiscountDetailPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountDetailPage(id, req);
    }
    
    /**
     * 详情页面数据
     * */
    @GetMapping("agentBillDiscountDetail/{id}")
    @ResponseBody
    public String agentBillDiscountDetail(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountDetail(id, req);
    }
    
    /**
     * 详情页面数据
     * */
    @GetMapping("agentBillDiscountDetailPkgList/{id}")
    @ResponseBody
    public String agentBillDiscountDetailPkgList(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountPkgList(id, req);
    }
    
    /**
     * 显示编辑页面请求路径中要包括需要修改的agentId
     * 
     * @param id
     * @return
     */
    @GetMapping("agentBillDiscountEditPage/{id}")
    @ResponseBody
    public String agentBillDiscountPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountPage(id, req);
    }
    
    // /**
    // * 跳转到修改页面
    // * @param id
    // * @return
    // */
    // @GetMapping("agentBillDiscountEditPage")
    // public String agentBillDiscountEditPage(HttpServletRequest req) {
    // return agentBillDiscountSer.agentBillDiscountEditPage(req);
    // }
    
    // /**
    // * 更新代理商话费折扣信息 只能用put方式提交
    // * @param id
    // * @param req
    // * @param res
    // * @return
    // */
    // @PutMapping("agentBillDiscount")
    // @ResponseBody
    // public String agentBillDiscountPut(HttpServletRequest req) {
    // return agentBillDiscountSer.agentBillDiscountEdit(req);
    // }
    
    /**
     * 修改折扣信息
     * */
    @PutMapping("agentBillDiscountUpdate")
    @ResponseBody
    public String agentBillDiscountUpdate(HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountUpdate(req);
    }
    
    // /**
    // * 删除代理商话费折扣
    // * @param id
    // * @param req
    // * @param res
    // * @return
    // */
    // @DeleteMapping("agentBillDiscount/{id}")
    // @ResponseBody
    // public String agentBillDiscountDel(@PathVariable("id") String agentId,HttpServletRequest req) {
    // return agentBillDiscountSer.agentBillDiscountDel(agentId,req) ;
    // }
    
    /**
     * @Title:queryAgentDiscountBySuId
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月9日 下午4:03:17
     * @param @param agentId
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("queryAgentDiscountBySuId/{suId}")
    @ResponseBody
    public String queryAgentDiscountBySuId(@PathVariable("suId") String suId, HttpServletRequest req)
    {
        return agentBillDiscountSer.queryAgentDiscountBySuId(suId, req);
    }
    
    /**
     * 复制折扣信息
     * */
    @PostMapping("agentBillDiscountCopy")
    @ResponseBody
    public String agentBillDiscountCopy(HttpServletRequest req)
    {
        return agentBillDiscountSer.agentBillDiscountCopy(req);
    }
}
