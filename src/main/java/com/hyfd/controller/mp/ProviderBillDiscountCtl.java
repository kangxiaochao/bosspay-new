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

import com.hyfd.controller.sys.BaseController;
import com.hyfd.service.mp.ProviderBillDiscountSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class ProviderBillDiscountCtl extends BaseController
{
    
    @Autowired
    ProviderBillDiscountSer providerBillDiscountSer;
    
    @GetMapping("providerBillDiscountEditPage/{id}")
    public String providerBillDiscountEditPage(HttpServletRequest req, @PathVariable("id") String id)
    {
        return providerBillDiscountSer.providerBillDiscountEditPage(req, id);
    }
    
    @GetMapping("providerBillDiscountUpload")
    public String providerBillDiscountUpload()
    {
        return "mp/providerBillDiscountUploadPage";
    }
    
    /**
     * @Title:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年8月9日 下午2:56:02
     * @param @param agentId
     * @param @param providerId
     * @param @param mFile
     * @param @param req
     * @param @return
     * @param @throws Exception
     * @return List<String> 返回类型
     * @throws
     */
    @PostMapping("commitProviderBillDiscount/{physicalId}")
    @ResponseBody
    public List<String> commitProviderBillDiscount(@PathVariable("physicalId") String physicalId,
        @RequestParam(value = "file", required = false) MultipartFile mFile, HttpServletRequest req)
        throws Exception
    {
        List<String> list = providerBillDiscountSer.commitProviderBillDiscount(physicalId, mFile);
        return list;
    }
    
    /**
     * 获取列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("providerBillDiscountList/{physicalId}/{providerId}")
    @ResponseBody
    public String providerBillDiscountList(@PathVariable("physicalId") String physicalId,
        @PathVariable("providerId") String providerId, HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountList(physicalId, providerId, req);
    }
    
    @GetMapping("providerBillDiscountByBillPkgId/{bill_pkg_id}")
    @ResponseBody
    public String providerBillDiscountByBillPkgIdGet(@PathVariable("bill_pkg_id") String bill_pkg_id)
    {
        return providerBillDiscountSer.getProviderBillDiscountByBillPkgId(bill_pkg_id);
    }
    
    @GetMapping("providerBillDiscountByBillPkgIdAndProvinceCode")
    @ResponseBody
    public String providerBillDiscountByBillPkgIdAndProvinceCodeGet(HttpServletRequest req)
    {
        return providerBillDiscountSer.getProviderBillDiscountByBillPkgIdAndProvinceCode(req);
    }
    
    @PostMapping("providerBillDiscount")
    @ResponseBody
    public String providerBillDiscountPost(HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountAdd(req);
    }
    
    @PutMapping("providerBillDiscount/{id}")
    @ResponseBody
    public String providerBillDiscountPut(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountEdit(req, id);
    }
    
    @PostMapping("providerBillDiscountEx1")
    @ResponseBody
    public String providerBillDiscountEx1Post(HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountUpload(req);
    }
    
    /**
     * 动态获取折扣表的表头
     * @author lks 2017年12月14日下午3:19:44
     * @param agentId
     * @param providerId
     * @param req
     * @return
     */
    @GetMapping("getColModels/{physicalId}/{providerId}")
    @ResponseBody
    public String getColModel(@PathVariable("physicalId") String physicalId,@PathVariable("providerId") String providerId, HttpServletRequest req){
    	return providerBillDiscountSer.getColModel(physicalId, providerId, req);
    }
    
    @RequestMapping("editCellDiscounts")
    @ResponseBody
    public String editCellDiscount(HttpServletRequest request){
    	return providerBillDiscountSer.editCellDiscount(request);
    }
    
    /**
     * <h5>功能描述:</h5> 跳转到运营商话费折扣列表展示页面
     *
     * @param id
     * @param req
     * @return
     *
     * @作者：zhangpj @创建时间：2017年5月10日
     */
    @GetMapping("providerBillDiscountListPage/{id}")
    public String providerBillDiscountListPage(@PathVariable("id") String id, HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountListPage(id, req);
    }
    
    /**
     * 获取运营商话费折扣列表数据 只能使用get方式提交
     * 
     * @param req
     * @return
     */
    @GetMapping("providerBillDiscount")
    @ResponseBody
    public String providerBillDiscountGet(HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountList(req);
    }
    
    /**
     * 保存虚商物理通道折扣
     * @author lks 2017年12月18日下午2:12:20
     * @param request
     * @return
     */
    @RequestMapping("saveChannelVpd")
    @ResponseBody
    public String saveChannelVpd(HttpServletRequest request){
    	return providerBillDiscountSer.saveChannelVpd(request);
    }
    
    /**
     * 获取虚商物理通道折扣
     * @author lks 2017年12月18日下午2:12:20
     * @param request
     * @return
     */
    @GetMapping("selectChannelVpd")
    @ResponseBody
    public String selectChannelVpd(HttpServletRequest request){
    	return providerBillDiscountSer.selectChannelVpd(request);
    }
    
    /**
     * <h5>功能描述:</h5> 添加运营商流量包折扣信息
     *
     * @param req
     * @param provincesAndDiscountes
     * @return
     *
     * @作者：zhangpj @创建时间：2017年3月31日
     */
    @PostMapping("providerBillDiscountEdit")
    @ResponseBody
    public String providerBillDiscountAddPost(HttpServletRequest req)
    {
        return providerBillDiscountSer.providerBillDiscountEditExt(req);
    }
    
    /**
     * @功能描述： 折扣设置页面获取满足条件的话费包折扣信息
     *
     * @作者：zhangpj @创建时间：2017年5月16日
     * @param req
     * @return
     */
    @GetMapping("providerBillDiscountInfo")
    @ResponseBody
    public String providerDataDiscount(HttpServletRequest req)
    {
        return providerBillDiscountSer.selectProviderBillDiscount(req);
    }
}
