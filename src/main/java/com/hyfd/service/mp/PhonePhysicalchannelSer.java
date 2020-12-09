package com.hyfd.service.mp;

import com.github.pagehelper.PageHelper;
import com.graphbuilder.math.func.LgFunction;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.PhonePhysicalchannelDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.service.BaseService;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

            List<Map<String, Object>> billList = phonePhysicalchannelDao.selectAll(maps);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
            sb.append("}");
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

    /**
     * 增加物理通道号段
     * @param req
     * @return
     */
    public String phonePhysicalchannelAdd(HttpServletRequest req) {
        Session session = getSession();
        boolean flag = false;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(new Date());
            System.out.println("format = " + format);
            Map<String, Object> myBill = getMaps(req);
            myBill.forEach((key, value) -> {
                System.out.println(key + "：" + value);
            });
            myBill.put("create_time",format);
            System.out.println("|11111111111111");
            int rows =  phonePhysicalchannelDao.phonePhysicalchanneladd(myBill);
            System.out.println("rows = " + rows);
        }catch (Exception e){
            session.setAttribute(GlobalSetHyfd.backMsg,
                    "小Y不小心感冒了,通道号段添加失败,请稍后重试!");
            getMyLog(e, log);
        }

        return "redirect:/phonePhycicalchannelListPage";

    }

    /**
     * 删除通道号段
     * @param id
     * @return
     */
    public String deletePhonePhysicalchannel(String id) {
        try {
            boolean flag = false;
            int rows = phonePhysicalchannelDao.deleteByPrimaryKey(id);
            if (rows > 0) {
                flag = true;
            }

            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");

        } catch (Exception e) {
            getMyLog(e, log);
        }
        return "phonePhycicalchannelListPage";
    }

    /**
     * 通道号段详细
     * @param id
     * @return
     */
    public String phonePhysicalchannelDetail(String id) {
        try {
            Map<String, Object> m = getphonePhysicalchannelById(id);
            Session session = getSession();
            session.setAttribute("phonePhysicalchannel", m);
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return "mp/phonePhysicalchannelDetail";
    }

    /**
     * 根据主键获取记录
     *
     * @param id
     * @return
     */
    public Map<String, Object> getphonePhysicalchannelById(String id) {
        Map<String, Object> m = new HashMap<String, Object>();
        try {
            m = phonePhysicalchannelDao.selectByPrimaryKey(id);
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return m;
    }

    /**
     * 物理通道号段编辑页面
     * @param id
     * @return
     */
    public String PhysicalchannelEditPage(String id) {
        try {
            Map<String, Object> Physicalchannel = getphonePhysicalchannelById(id);
            Session session = getSession();
            session.setAttribute("phonePhysicalchannel", Physicalchannel);
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return "mp/PhonePhysicalchannelEdit";

    }

    /**
     * 物理通道号段编辑数据修改
     * @param req
     * @param id
     * @return
     */
    public String phonePhysicalchannel(HttpServletRequest req, String id) {


        try {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
//            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
//            myBill.put("updateUser", userInfoMap.get("suName"));// 放入用户
            myBill.put("id",id);
            int rows = phonePhysicalchannelDao.updateByPrimaryKeySelective(myBill);
            if (rows > 0) {
                flag = true;
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
        } catch (Exception e) {
            getMyLog(e, log);
        }
        return "phonePhycicalchannelListPage";

    }
}
