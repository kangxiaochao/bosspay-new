package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.hyfd.dao.mp.ProviderProductDao;
import com.hyfd.service.BaseService;

/**
 * @功能描述：	流量包体相关业务
 *
 * @作者：wanyf		@创建时间：2017年11月23日
 */
@Service
@Transactional
public class ProviderProductSer extends BaseService{
	
	@Autowired
	ProviderProductDao providerProductDao;
	
	public Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 添加包体相关业务
	 * @param req
	 * @return
	 */
	public String ProviderProductAdd(HttpServletRequest req){
		Map<String, Object> myData=getMaps(req);
		myData.put("id", UUID.randomUUID().toString().replace("-",""));
		boolean flag = false;
		int sum	=	providerProductDao.insertSelective(myData);
		if(sum > 0){
			flag = true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		return "redirect:/providerProduct";		
	}
	
	/**
	 * 分页
	 * @param req
	 * @return
	 */
	public String queryProviderProductList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = providerProductDao.selectCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> dataList = providerProductDao.selectByPrimary(m);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
    }
	/**
	 * 删除信息
	 * @param req
	 * @return
	 */
	public String delProvider(HttpServletRequest req){
		Map<String, Object> m = getMaps(req);
		String msg = "删除失败";
		int sum = providerProductDao.deleteByPrimaryKey(m.get("id").toString());
		if(sum>0){
			msg = "删除成功";
		}
		return msg;
	}
	/**
	 * 根据ID回填
	 * @param req
	 * @return
	 */
	public String queryById(String id){
		try
        {
			Map<String, Object> p = providerProductDao.selectByPrimaryKey(id);
            Session session = getSession();
            session.setAttribute("provider", p);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerProductEdit";
	}
	/**
	 * 修改信息
	 * @param req
	 * @return
	 */
	public String updateProvider(HttpServletRequest req){
		Map<String, Object> m = getMaps(req);
		boolean flag = false;
		int sum	=	providerProductDao.updateByPrimaryKeySelective(m);
		if(sum > 0){
			flag = true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		return "redirect:/providerProductList";		
	}
}
