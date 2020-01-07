package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
	
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.BaseJson;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.AgentProfitDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class AgentProfitSer extends BaseService{
	public Logger log = Logger.getLogger(this.getClass());
	@Autowired
	AgentProfitDao agentProfitDao;
	@Autowired
	AgentDao agentDao;
	/**
	 * 查询出指定时间段内代理商的利润
	 * @return
	 */
	public String agentProfitList(HttpServletRequest req) {
		 String billListJson = "";
	        try
	        {
	            Map<String, Object> m = getMaps(req); // 封装前台参数为map
	            List<Map<String, Object>> billList = agentProfitDao.selectAgentProfitList(m);
	            billListJson = BaseJson.listToJson(billList);
	        }
	        catch (Exception e)
	        {
	            getMyLog(e, log);
	        }
	        
	        return billListJson;
	}
	
	/**
	 * 导出代理商利润
	 * @param req	
	 * @return 
	 */
	public void agentProfitExport(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> param = getMaps(req);
		List<Map<String, Object>> list = agentProfitDao.selectAgentProfitList(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		exportExcel("agentProfit", "agentProfit", map, req, resp);
	}
	/**
	 * 用流导出excel
	 * 
	 * @param filename
	 * @param list
	 * @param request
	 * @param response
	 */
	public void exportExcel(String tempName, String filename, Map<String, Object> map, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"
					+ File.separator + tempName + ".xlsx";
			XLSTransformer former = new XLSTransformer();
			FileInputStream in = new FileInputStream(templatePath);
			response.setContentType("application/x-excel;charset=utf-8");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + filename + "_" + System.currentTimeMillis() + ".xlsx");
			response.setCharacterEncoding("utf-8");
			XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
