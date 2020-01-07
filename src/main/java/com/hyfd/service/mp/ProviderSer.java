package com.hyfd.service.mp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ProviderAccountDao;
import com.hyfd.dao.mp.ProviderBillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ProviderSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    ProviderDao providerDao;
    
    @Autowired
    ProviderAccountDao providerAccountDao;
    
    @Autowired
    ProviderBillPkgDao providerBillPkgDao;
    
    @Autowired
    BillPkgDao billPkgDao;
    
    public int getProviderCount(Map<String, Object> m)
    {
        int providerCount = 0;
        try
        {
            providerCount = providerDao.getProviderCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerCount;
    }
    
    public String providerList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getProviderCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = providerDao.getProviderList(m);
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
    
    public String providerListGet(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req);
            List<Map<String, Object>> billList = providerDao.getProviderList(m);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    public String providerAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("create_user", userInfoMap.get("suId"));// 放入创建用户
            
            int rows = providerDao.providerAdd(myBill);
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
        return "redirect:/providerListPage";
    }
    
    public String providerEditPage(String id)
    {
        try
        {
            Map<String, Object> provider = getProviderById(id);
            Session session = getSession();
            session.setAttribute("provider", provider);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerEdit";
    }
    
    public Map<String, Object> getProviderById(String id)
    {
        Map<String, Object> m = null;
        try
        {
            m = providerDao.getProviderById(id);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return m;
    }
    
    public String providerEdit(HttpServletRequest req, String id)
    {
        
        try
        {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("update_user", userInfoMap.get("suId"));// 放入创建用户
            int rows = providerDao.providerEdit(myBill);
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
        return "providerListPage";
    }
    
    public String providerDel(String id)
    {
        
        try
        {
            boolean flag = false;
            int rows = providerDao.providerDel(id);
            
            providerAccountDao.providerAccountDel(id);// 删除余额信息
            
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
        return "providerListPage";
    }
    
    public String agentProviderList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> m = getMaps(req); // 封装前台参数为map
        List<Map<String, Object>> billList = providerDao.getProviderList(m);
        String billListJson = BaseJson.listToJson(billList);
        sb.append(billListJson);
        
        return billListJson;
    }
    
    /**
     * excel文件上传解析
     * 
     * @param id
     * @param mFile
     * @return
     * @throws Exception
     */
    public List<String> commitProviderPkg(String id, MultipartFile mFile)
        throws Exception
    {
        InputStream is = mFile.getInputStream();
        List<String> errorList = new ArrayList<String>();
        try
        {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            XSSFSheet normalSheet = workbook.getSheetAt(0);
            if (id != null && !"".equals(id))
            {
                // for (int r = 1; r < normalSheet.getPhysicalNumberOfRows(); r++) {
                // Map<String, Object> map = new HashMap<String, Object>();
                // XSSFRow itemSheetRow = normalSheet.getRow(r);
                // if(itemSheetRow.getCell(0)!=null){
                // String provinceCode = itemSheetRow.getCell(0).getStringCellValue();
                // double value = itemSheetRow.getCell(1).getNumericCellValue();
                // double price = itemSheetRow.getCell(2).getNumericCellValue();
                // String billType = itemSheetRow.getCell(3).getStringCellValue();
                // String billPkgId =null;
                // if(value!=0 && price!=0 &&!"".equals(billType)&&billType!=null){
                // Map<String, Object> dpkMap = new HashMap<String, Object>();
                // dpkMap.put("value", value);
                // dpkMap.put("price", price);
                // if(billType.equals("全国")){
                // billType="2";
                // }else if(billType.equals("省份")){
                // billType="1";
                // }
                // dpkMap.put("bill_type", billType);
                // billPkgId = billPkgDao.getBillPkgId(dpkMap);
                // if(billPkgId!=null){
                // if(provinceCode!=null&& !"".equals(provinceCode)){
                // map.put("providerId", id);
                // map.put("provinceCode", provinceCode);
                // map.put("billPkgId", billPkgId);
                // list.add(map);
                // }else{
                // errorList.add("地区不能为空！");
                // return errorList;
                // }
                // }else{
                // errorList.add("数据包不存在！");
                // return errorList;
                // }
                // }else{
                // errorList.add("数据包不能为空！");
                // return errorList;
                // }
                // }
                // }
                // providerBillPkgDao.deleteByProviderId(id);
                // for(Map<String, Object> map :list){
                // providerBillPkgDao.insert(map);
                // }
            }
            else
            {
                errorList.add("运营商不能为空！");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorList.add("上传表格格式不匹配！");
        }
        return errorList;
    }
}
