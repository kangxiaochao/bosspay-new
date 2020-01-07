package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.FinancialReportDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class FinancialReportSer extends BaseService {

	@Autowired
	FinancialReportDao frDao;

	/**
	 * 页面加载的数据
	 * 
	 * @author lks 2018年2月27日上午9:40:39
	 * @param request
	 * @param response
	 * @return
	 */
	public String FinancialChannelReportList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		List<Map<String, Object>> swlist = frDao.selectSWChannelData(param);
		List<Map<String, Object>> xslist = frDao.selectXSChannelData(param);
		swlist.addAll(xslist);

		StringBuilder sb = new StringBuilder();
		try {
			Page p = getPage(param);// 提取分页参数
			int total = swlist.size();
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			String billListJson = BaseJson.listToJson(getPageList(pageNum, pageSize, swlist));
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			log.error("订单提交记录列表查询方法出错" + e.getMessage());
		}
		return sb.toString();
	}

	/**
	 * 返回list分页后的数据
	 * 
	 * @author lks 2018年2月28日下午3:04:30
	 * @param pageNum
	 * @param pageSize
	 * @param list
	 * @return
	 */
	public List<Map<String, Object>> getPageList(int pageNum, int pageSize, List<Map<String, Object>> list) {
		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
		int startNum = (pageNum - 1) * pageSize;
		int endNum = pageNum * pageSize - 1;
		if (startNum != 0) {
			startNum = startNum - 1;
		}
		if ((pageNum * pageSize) > list.size()) {
			endNum = list.size();
		}
		l = list.subList(startNum, endNum);
		return l;
	}

	/**
	 * 代理商页面加载的数据
	 * 
	 * @author lks 2018年2月27日上午9:40:39
	 * @param request
	 * @param response
	 * @return
	 */
	public String FinancialAgentReportList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		List<Map<String, Object>> swlist = frDao.selectSWAgentData(param);
		List<Map<String, Object>> xslist = frDao.selectXSAgentData(param);
		param.put("providerId", "2000000005");
		List<Map<String, Object>> dxtkmlist = frDao.selectTSAgentData(param);
		param.put("providerId", "2000000009");
		List<Map<String, Object>> hhblist = frDao.selectTSAgentData(param);
		param.put("providerId", "2000000016");
		List<Map<String, Object>> ttblist = frDao.selectTSAgentData(param);
		param.put("providerId", "2000000022");
		List<Map<String, Object>> ytkmlist = frDao.selectTSAgentData(param);
		swlist.addAll(xslist);
		swlist.addAll(dxtkmlist);
		swlist.addAll(hhblist);
		swlist.addAll(ttblist);
		swlist.addAll(ytkmlist);
		StringBuilder sb = new StringBuilder();
		try {
			Page p = getPage(param);// 提取分页参数
			int total = swlist.size();
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			String billListJson = BaseJson.listToJson(getPageList(pageNum, pageSize, swlist));
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			log.error("订单提交记录列表查询方法出错" + e.getMessage());
		}
		return sb.toString();
	}

	/**
	 * 经营分析报表
	 * 
	 * @author lks 2018年3月20日上午9:17:39
	 * @param request
	 * @param response
	 */
	public String BusinessList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		List<Map<String, Object>> list = frDao.selectBusinessList(param);
		StringBuilder sb = new StringBuilder();
		try {
			Page p = getPage(param);// 提取分页参数
			int total = list.size();
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			String billListJson = BaseJson.listToJson(getPageList(pageNum, pageSize, list));
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			log.error("订单提交记录列表查询方法出错" + e.getMessage());
		}
		return sb.toString();
	}

	/**
	 * 导出经验分析报表
	 * 
	 * @author lks 2018年3月20日上午9:56:30
	 * @param request
	 * @param response
	 */
	public void exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		List<Map<String, Object>> list = frDao.selectBusinessList(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		exportExcel("BusinessReportTemp", "BusinessReport", map, request, response);
	}

	/**
	 * 导出财务报表下家数据
	 * 
	 * @author lks 2018年2月28日下午3:56:38
	 * @param request
	 * @param response
	 */
	public void exportFinancialAgentReport(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		try {
			List<Map<String, Object>> swlist = frDao.selectSWAgentData(param);
			List<Map<String, Object>> xslist = frDao.selectXSAgentData(param);
			param.put("providerId", "2000000005");
			List<Map<String, Object>> dxtkmlist = frDao.selectTSAgentData(param);
			param.put("providerId", "2000000009");
			List<Map<String, Object>> hhblist = frDao.selectTSAgentData(param);
			param.put("providerId", "2000000016");
			List<Map<String, Object>> ttblist = frDao.selectTSAgentData(param);
			param.put("providerId", "2000000022");
			List<Map<String, Object>> ytkmlist = frDao.selectTSAgentData(param);
			swlist.addAll(xslist);
			swlist.addAll(dxtkmlist);
			swlist.addAll(hhblist);
			swlist.addAll(ttblist);
			swlist.addAll(ytkmlist);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", swlist);
			exportExcel("financialAgentReportTemp", "financialAgentReport", map, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出财务报表上家数据
	 * 
	 * @author lks 2018年2月28日下午3:56:38
	 * @param request
	 * @param response
	 */
	public void exportFinancialChannelReport(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		List<Map<String, Object>> swlist = frDao.selectSWChannelData(param);
		List<Map<String, Object>> xslist = frDao.selectXSChannelData(param);
		swlist.addAll(xslist);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", swlist);
		exportExcel("financialChannelReportTemp", "financialChannelReport", map, request, response);
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
