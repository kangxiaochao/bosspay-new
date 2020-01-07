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
import com.hyfd.service.mp.TmallProductSer;

@Controller
// @Scope("prototype") //开启非单例模式 用于并发控制
public class TmallProductCtl extends BaseController
{
    
    @Autowired
    TmallProductSer tmallProductSer;
    
    /**
     * @Title:tmallProductListPage
     * @Description: 跳转至天猫流量产品配置列表页(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午9:45:52
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("tmallProductListPage")
    public String tmallProductListPage()
    {
        return "mp/tmallProductList";
    }
    
    /**
     * @Title:queryTmallProductList
     * @Description: 查询天猫流量产品列表列表数据(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午9:46:32
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("queryTmallProductList")
    @ResponseBody
    public String queryTmallProductList(HttpServletRequest req)
    {
        return tmallProductSer.queryTmallProductList(req);
    }
    
    /**
     * @Title:tmallProductAddPage
     * @Description: 天猫流量产品跳转添加页面(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午9:47:33
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("tmallProductAddPage")
    public String tmallProductAddPage()
    {
        return "mp/tmallProductAdd";
    }
    
    /**
     * @Title:tmallProductAdd
     * @Description: 天猫流量产品添加方法(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午10:22:13
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @PostMapping("tmallProductAdd")
    public String tmallProductAdd(HttpServletRequest req)
    {
        return tmallProductSer.tmallProductAdd(req);
    }
    
    /**
     * @Title:tmallProductEditPage
     * @Description: 天猫流量产品跳转编辑页面(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午11:37:29
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @GetMapping("tmallProductEditPage/{id}")
    public String tmallProductEditPage(@PathVariable("id") String id)
    {
        return tmallProductSer.tmallProductEditPage(id);
    }
    
    /**
     * @Title:tmallProductEdit
     * @Description: 天猫流量产品编辑方法(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午11:37:52
     * @param @param id
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @PutMapping("tmallProductEdit/{id}")
    @ResponseBody
    public String tmallProductEdit(@PathVariable("id") String id, HttpServletRequest req)
    {
        return tmallProductSer.tmallProductEdit(req, id);
    }
    
    /**
     * @Title:tmallProductDel
     * @Description: 天猫流量产品删除(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 下午2:29:26
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    @DeleteMapping("tmallProductDel/{id}")
    @ResponseBody
    public String tmallProductDel(@PathVariable("id") String id)
    {
        return tmallProductSer.tmallProductDel(id);
    }
}
