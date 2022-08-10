package com.hyfd.service.mp;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.AgentProfitChargeDao;
import com.hyfd.service.BaseService;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentProfitChargeSer extends BaseService{
	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	AgentDao agentDao;

	@Autowired
	AgentProfitChargeDao agentProfitChargeDao;

	/**
	 * 获取记录数量
	 *
	 * @param m
	 * @return
	 */
	public int getAgentProfitChargeCount(Map<String, Object> m)
	{
		int agentCount = 0;
		try
		{
			agentCount = agentProfitChargeDao.selectCount(m);
		}
		catch (Exception e)
		{
			getMyLog(e, log);
		}
		return agentCount;
	}

	/**
	 * 查询代理商利润明细
	 * @return
	 */
	public String agentProfitChargeList(HttpServletRequest req) {
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
			int total = getAgentProfitChargeCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = agentProfitChargeDao.selectAll(m);
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
	 * 导出代理商利润
	 * @param req	
	 * @return 
	 */
	public void agentProfitChargeExport(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> param = getMaps(req);
		Map<String, Object> userInfoMap = getUser();
		String userid = userInfoMap.get("suId") + "";
		// 查询用户是否是代理商
		Map<String, Object> agentMap = agentDao.selectByUserId(userid);
		String agentParentId = "";
		if (null != agentMap)
		{
			agentParentId = agentMap.get("id") + "";
			param.put("agentParentId", agentParentId);
		}
		List<Map<String, Object>> list = agentProfitChargeDao.selectAll(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		exportExcel("agentProfitCharge", "agentProfitCharge", map, req, resp);
	}

	/**
	 * 用流导出excel
	 * 
	 * @param filename
	 * @param map
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
