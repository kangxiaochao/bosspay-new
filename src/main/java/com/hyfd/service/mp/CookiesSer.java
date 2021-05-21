package com.hyfd.service.mp;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.IpDao;
import com.hyfd.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class CookiesSer extends BaseService {

    @Autowired
    CookiesDao cookiesDao;




    /**
     * @功能描述：	根据条件分页获取ip信息
     *
     * @param req
     * @return
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
    public String cookiesList(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getIpCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();

            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");

            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> dataList = cookiesDao.selectAll(m);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
            sb.append("}");
        } catch (Exception e) {
            getMyLog(e, log);
        }

        return sb.toString();
    }

    /**
     * @功能描述：	根据条件获取满足条件的IP信息数量
     *
     * @param m
     * @return
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
    public int getIpCount(Map<String, Object> m){
        int count=0;
        try{
            count=cookiesDao.selectCount(m);
        }catch(Exception e){
            getMyLog(e,log);
        }
        return count;
    }
}
