package com.hyfd.service.mp;

import com.github.pagehelper.PageHelper;
import com.graphbuilder.math.func.LgFunction;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.PhonePhysicalchannelDao;
import com.hyfd.service.BaseService;
import org.apache.log4j.Logger;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class PhonePhysicalchannelSer extends BaseService {
    public Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private PhonePhysicalchannelDao phonePhysicalchannelDao;

    /**
     * 根据条件分页获取通道号段模板数据并生成json
     *
     * @param req
     * @return
     */
    public String phonePhycicalchannelList(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try {
            Map<String, Object> maps = getMaps(req);
            Page page = getPage(maps);//获取分页参数
            int total = getphonePhycicalchannelCount(maps);//总数,页数,每页显示条数
            page.setCount(total);
            int pageNum = page.getCurrentPage();
            int pageSize = page.getPageSize();//分页的基本信息

            sb.append("{");
            sb.append("" + getKey("page") + ":" + page.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + page.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + page.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");

            PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
//            List<Map<String, Object>> billList = phonePhysicalchannelDao.selectAll(maps);
//            String billListJson = BaseJson.listToJson(billList);
//            sb.append(billListJson);
//            sb.append("}");
            return null;
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return sb.toString();
    }

    /**
     * 获取记录数量
     */
    public int getphonePhycicalchannelCount(Map<String, Object> maps) {
        int phonePhycicalchannelCount = 0;
        try {
            phonePhycicalchannelCount = phonePhysicalchannelDao.selectCount(maps);
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return phonePhycicalchannelCount;
    }
}
