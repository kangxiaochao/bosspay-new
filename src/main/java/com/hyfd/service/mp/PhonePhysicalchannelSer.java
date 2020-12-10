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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.security.provider.Sun;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
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

    /**
     * 批量导入物理通道号段
     * @param file
     * @param req
     * @return
     */
    public String phonePhysicalchannelExcelAdd(MultipartFile file, HttpServletRequest req) {
        int flag = -1;
        int sum = 0;
        Map<String, Object> map = getMaps(req);
        List<Map<String, Object>> sections = new ArrayList<>();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        try {
            Workbook book = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = book.getSheetAt(0);
            flag = sheet.getLastRowNum();
            for (int i = 0; i < flag+1; i++) {
                Row row = sheet.getRow(i);
                //判断获取的数据是否为‘手机号码段’
                Pattern pattern = Pattern.compile("[0-9]*");
                row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                Matcher isNum = pattern.matcher(row.getCell(0).getStringCellValue());
                if( !isNum.matches() ){
                    log.error("批量添加号段时，获取的参数有误！     section："+row.getCell(0).getStringCellValue());
                    return "-1";
                }

                Map<String, Object> phone = new HashMap<>();
                phone.put("section",row.getCell(0).getStringCellValue());
                phone.put("dispatcher_provider_id",row.getCell(1).getStringCellValue());
                phone.put("create_time",new Date().getTime());
                sections.add(phone);
            }
            sum = phonePhysicalchannelDao.listAddPhonePhysicalchannel(sections);
            System.out.println(sum+"------------------------------充值结果");
        } catch (Exception e) {
            getMyLog(e, log);
        }finally {
            fixedThreadPool.shutdown();
        }
        return sum+"";
    }
}
