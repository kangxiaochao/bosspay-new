package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.TmallProductDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class TmallProductSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    TmallProductDao tmallProductDao;
    
    /**
     * @Title:queryTmallProductList
     * @Description: 天猫流量产品列表查询(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午10:22:54
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String queryTmallProductList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = tmallProductDao.selectCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = tmallProductDao.queryTmallProductList(m);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    /**
     * @Title:tmallProductAdd
     * @Description: 天猫流量产品添加方法(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午10:23:41
     * @param @param req
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String tmallProductAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            // Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            //
            // myBill.put("create_user", userInfoMap.get("suId"));// 放入创建用户
            
            int rows = tmallProductDao.tmallProductAdd(myBill);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "redirect:/tmallProductListPage";
    }
    
    /**
     * @Title:tmallProductEditPage
     * @Description: 天猫流量产品跳转页面(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午11:38:53
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String tmallProductEditPage(String id)
    {
        try
        {
            Map<String, Object> tmallProduct = tmallProductDao.getTmallProductById(id);
            Session session = getSession();
            session.setAttribute("tmallProduct", tmallProduct);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/tmallProductEdit";
    }
    
    /**
     * @Title:tmallProductEdit
     * @Description: 天猫流量产品编辑方法(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 上午11:39:24
     * @param @param req
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String tmallProductEdit(HttpServletRequest req, String id)
    {
        
        try
        {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
            myBill.put("ids", id);
            // Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            //
            // myBill.put("update_user", userInfoMap.get("suId"));// 放入创建用户
            int rows = tmallProductDao.tmallProductEdit(myBill);
            if (rows > 0)
            {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "tmallProductListPage";
    }
    
    /**
     * @Title:tmallProductDel
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月10日 下午2:30:44
     * @param @param id
     * @param @return
     * @return String 返回类型
     * @throws
     */
    public String tmallProductDel(String id)
    {
        
        try
        {
            boolean flag = false;
            int rows = tmallProductDao.tmallProductDel(id);
            
            if (rows > 0)
            {
                flag = true;
            }
            
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
            
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "tmallProductListPage";
    }
    
}
