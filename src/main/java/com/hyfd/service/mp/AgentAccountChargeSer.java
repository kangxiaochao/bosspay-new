package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csvreader.CsvWriter;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.AgentAccountChargeDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.service.BaseService;

@Service
public class AgentAccountChargeSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    private AgentAccountChargeDao agentAccountChargeDao;
    
    @Autowired
    AgentDao agentDao;
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getAgentCount(Map<String, Object> m)
    {
        int agentCount = 0;
        try
        {
            agentCount = agentAccountChargeDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return agentCount;
    }
    
    /**
     * 根据条件分页获取代理商列表数据并生成json
     * 
     * @param req
     * @return
     */
    public String agentAccountChargeAllList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            
            Map<String, Object> userInfoMap = getUser();
            String userid = userInfoMap.get("suId") + "";
            // 查询用户是否是代理商
            Map<String, Object> agentMap = agentDao.selectByUserId(userid);
            String agentParentId = "";
            if (null != agentMap)
            {
                agentParentId = agentMap.get("id") + "";
                m.put("agentParentId", agentParentId);
            }
            Page p = getPage(m);// 提取分页参数
            int total = getAgentCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = agentAccountChargeDao.selectAll(m);
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
    
    public String receiptList(HttpServletRequest request, HttpServletResponse response)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(request); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            m.put("type", "3");
            int total = getAgentCount(m);
            m.remove("type");
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = agentAccountChargeDao.selectReceiptList(m);
            for (int i = 0, size = billList.size(); i < size; i++)
            {
                billList.get(i).put("num", i + 1);
            }
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
     * 导出数据库数据
     * 
     * @author lks 2017年5月26日下午2:51:25
     * @param request
     * @param resposne
     */
    public void exportReceiptExcel(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> param = getMaps(request);
        List<Map<String, Object>> list = agentAccountChargeDao.selectReceiptList(param);
        for (int i = 0, size = list.size(); i < size; i++)
        {
            list.get(i).put("num", i + 1);
        }
        String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"+ File.separator+"receiptTemplate.xlsx";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receiptList", list);
        map.put("time", DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        XLSTransformer former = new XLSTransformer();
        try
        {
            response.setContentType("application/x-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=Receipt-" + DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss") + ".xlsx");
            response.setCharacterEncoding("utf-8");
            FileInputStream in = new FileInputStream(templatePath);
            XSSFWorkbook workbook = (XSSFWorkbook)(former.transformXLS(in, map));
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 导出数据库代理商合并数据
     * 
     * @author lks 2017年5月26日下午2:51:25
     * @param request
     * @param resposne
     */
    public void exportMergeReceiptExcel(HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> param = getMaps(request);
        List<Map<String, Object>> list = agentAccountChargeDao.selectMergeReceiptList(param);
        for (int i = 0, size = list.size(); i < size; i++)
        {
            list.get(i).put("num", i + 1);
        }
        String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"+ File.separator+"receiptTemplate.xlsx";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receiptList", list);
        map.put("time", DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        XLSTransformer former = new XLSTransformer();
        try
        {
            response.setContentType("application/x-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=Receipt-" + DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss") + ".xlsx");
            response.setCharacterEncoding("utf-8");
            FileInputStream in = new FileInputStream(templatePath);
            XSSFWorkbook workbook = (XSSFWorkbook)(former.transformXLS(in, map));
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 导出每月新加款客户
     * @param req
     * @param resp
     */
    public void exportNewCustomerAgent(HttpServletRequest req,HttpServletResponse resp) {
    	Map<String, Object> m = getMaps(req);
    	List<Map<String, Object>> agents = agentAccountChargeDao.exportNewCustomerAgent(m);
    	try {
    		File file = File.createTempFile("result","csv");
			CsvWriter csvWriter = new CsvWriter(file.getCanonicalPath(), ',', Charset.forName("UTF-8"));
			String[] headers = { "代理商", "代理商名称", "加款金额", "加款时间", "渠道"};
			csvWriter.writeRecord(headers);
			agents.forEach(agent ->{
				try {
					csvWriter.write(agent.get("name") + "");
					csvWriter.write(agent.get("nickname") + "");
					csvWriter.write(agent.get("fee") + "");
					csvWriter.write("'" + agent.get("apply_date"));
					csvWriter.write(agent.get("suName") + "");
					csvWriter.endRecord();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			csvWriter.close();
			outCsvStream(resp, file, "exportNewCustomerAgent");
			deleteFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    /**
     * 导出月度加款客户
     * @param req
     * @param resp
     */
    public void exportMonthlyAddMoneyAgent(HttpServletRequest req,HttpServletResponse resp) {
    	Map<String, Object> m = getMaps(req);
    	List<Map<String, Object>> agents = agentAccountChargeDao.exportMonthlyAddMoneyAgent(m);
    	try {
			File result = File.createTempFile("result", "csv");
			CsvWriter csvWriter = new CsvWriter(result.getCanonicalPath(),',',Charset.forName("UTF-8"));
			String[] headers = { "代理商", "代理商名称", "加款金额", "渠道"};
			csvWriter.writeRecord(headers);
			agents.forEach(agent ->{
				try {
					csvWriter.write(agent.get("name") + "");
					csvWriter.write(agent.get("nickname") + ""); 
					csvWriter.write(agent.get("fee") + "");
					csvWriter.write(agent.get("suName") + "");
					csvWriter.endRecord();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			csvWriter.close();
			outCsvStream(resp, result, "exportMonthlyAddMoneyAgent");
			deleteFile(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // /**
    // * @功能描述： 根据条件获取列表数据并生成json
    // *
    // * @作者：HYJ @创建时间：2017年2月12日
    // * @param req
    // * @return
    // */
    // public String agentAccountChargeListGet(HttpServletRequest req){
    // StringBuilder sb=new StringBuilder();
    // try{
    // Map<String, Object> m=getMaps(req); //封装前台参数为map
    // Page p=getPage(m);//提取分页参数
    // int total=getAgentCount(m);
    // p.setCount(total);
    // int pageNum=p.getCurrentPage();
    // int pageSize=p.getPageSize();
    //
    // sb.append("{");
    // sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
    // sb.append(""+getKey("total")+":"+p.getNumCount()+",");
    // sb.append(""+getKey("records")+":"+p.getCount()+",");
    // sb.append(""+getKey("rows")+":"+"");
    //
    // PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
    // String agentName = (String) m.get("agentName");
    // if(agentName!=""&&agentName!=null){
    // Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
    // if(agent!=null){
    // String agentId = (String) agent.get("id");
    // m.put("agentId", agentId);
    // }
    // }else{
    // m.remove("agentName");
    // }
    // List<Map<String, Object>> billList=agentAccountChargeDao.selectAll(m);
    // String billListJson=BaseJson.listToJson(billList);
    // sb.append(billListJson);
    // sb.append("}");
    // }catch(Exception e){
    // getMyLog(e,log);
    // }
    // return sb.toString();
    // }
}
