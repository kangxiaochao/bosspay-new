package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class ProviderReportSer extends BaseService {

	@Autowired
	OrderDao orderDao;
	@Autowired
	AgentDao agentDao;
	@Autowired
	SysUserRoleDao sysUserRoleDao;

	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderDao providerDao;
	
	/**
	 * 话费通道结算账单
	 * 
	 * @author lks 2017年5月22日上午11:19:54
	 * @param request
	 * @param response
	 * @return
	 */
	public String ProviderBillStatementList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		param.put("bizType", "2");
		// Map<String,Object> user = getUser();
		StringBuilder sb = new StringBuilder();
		try {
			List<Map<String, Object>> swList = orderDao.selectProviderStatementListSw(param);
			List<Map<String, Object>> xsList = orderDao.selectProviderStatementListXs(param);
			swList.addAll(xsList);
			Page p = getPage(param);// 提取分页参数
//			int total = orderDao.countProviderStatementListExt(param);
			int total = swList.size();
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
//			List<Map<String, Object>> billList = orderDao.selectProviderStatementListExt(param);
			String billListJson = BaseJson.listToJson(swList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			log.error("订单提交记录列表查询方法出错" + e.getMessage());
		}

		return sb.toString();
	}

	/**
	 * 订单详情跳转方法
	 * 
	 * @author lks 2017年5月31日下午4:03:38
	 * @param request
	 * @param response
	 * @return
	 */
	public String toDetailBillOrderList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		Session session = getSession();
		session.setAttribute("param", param);
		return "mp/detailBillOrderList";
	}

	/**
	 * 详情订单
	 * 
	 * @author lks 2017年5月31日下午5:15:49
	 * @param request
	 * @param response
	 * @return
	 */
	public String detailBillOrderList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = getMaps(request);
		StringBuilder sb = new StringBuilder();
		try {
			Page p = getPage(param);// 提取分页参数
			int total = orderDao.countDetailOrderListExt(param);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = orderDao.selectDetailOrderListExt(param);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}

	/**
	 * 导出数据库数据
	 * 
	 * @author lks 2017年5月26日下午2:51:25
	 * @param request
	 * @param response
	 */
	public void exportBillOrderExcel(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> user = getUser();
		String suid = user.get("suId") + "";// 用户Id
		String temName = "reportTemplate.xlsx";
		if (judgeAgentRole(suid)) {// 如果有代理商角色
			temName = "reportTemplate4agent.xlsx";
		}
		Map<String, Object> param = getMaps(request);
		String agentId = (String) param.get("agentId");
		String fileName = DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd_hh:mm:ss");
		if (agentId != null && !agentId.equals("")) {
			String agentName = (String) agentDao.selectById(agentId).get("name");
			fileName = agentName + "-" + fileName;
		}
		List<Map<String, Object>> list = orderDao.selectDetailOrderListExt(param);
		String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"
				+ File.separator + temName;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderList", list);
		XLSTransformer former = new XLSTransformer();
		try {
			response.setContentType("application/x-excel;charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename=Report-" + fileName + ".xlsx");
			response.setCharacterEncoding("utf-8");
			FileInputStream in = new FileInputStream(templatePath);
			XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断用户是否具有代理商的角色
	 * 
	 * @author lks 2017年5月18日下午2:36:33
	 * @param suid
	 * @return
	 */
	public boolean judgeAgentRole(String suid) {
		boolean flag = false;
		List<Map<String, Object>> list = sysUserRoleDao.getHasSysRoleList(suid);
		for (Map<String, Object> role : list) {
			String roleName = role.get("srName") + "";
			if (roleName.equals("代理商")) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 导出所需数据库订单数据 
	 * @param request
	 * @param response
	 */
	public void exportBillOrderCSV(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> param = getMaps(request);
			Map<String, Object> user = getUser();
			String userid = user.get("suId") + "";
			// 根据用户Id查询agent表，若不为null则代表用户为代理商登陆
			Map<String, Object> agentMap = agentDao.selectByUserId(userid);
			if (agentMap != null) {
				// 得到代理商Id，查询其自身订单和其子集订单
				String agentParentId = agentMap.get("id") + "";
				param.put("agentParentId", agentParentId);
			}

			String agentName = (String) param.get("agentName");
			String dispatcherProviderName = (String) param.get("dispatcherProviderName");
			String providerName = (String) param.get("providerName");
			if (agentName != null && agentName != "") {
				Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
				if (agent != null) {
					String agentId = (String) agent.get("id");
					param.put("agentId", agentId);
				} else {
					param.put("agentId", agentName);
				}
			}
			if (dispatcherProviderName != null && dispatcherProviderName != "") {
				String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
				if (dispatcherProviderId != null && dispatcherProviderId != "") {
					param.put("dispatcherProviderId", dispatcherProviderId);
				} else {
					param.put("dispatcherProviderId", dispatcherProviderName);
				}
			}
			if (providerName != null && providerName != "") {
				String providerId = providerDao.getIdByName(providerName);
				if (providerId != null && providerId != "") {
					param.put("providerId", providerId);
				} else {
					param.put("providerId", providerName);
				}
			}

			int total = orderDao.selectCountCSV(param);
			int sum = total % 5000 == 0 ? total / 5000 : total / 5000 + 1;
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
			StringBuffer count = new StringBuffer();

			// List<Future<String>> futureList = new
			// ArrayList<Future<String>>();
			// for(int i = 1; i <= sum; i++) {
			// int currentThreadNum = i;
			// Future<String> futureSubmit = fixedThreadPool.submit(() -> {
			// PageHelper.startPage(currentThreadNum, 5000);// mybatis分页插件
			// List<Map<String, Object>> billList =
			// orderDao.selectDetailOrderListExt(param);
			// int num = 2;
			// if(user.get("suName").equals("admin")) {
			// num = 1;
			// }
			// return Array2CSV(billList,num);
			// });
			// futureList.add(futureSubmit);
			// }
			//
			// futureList.forEach(future -> {
			// try {
			// count.append
			// (future.get());
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (ExecutionException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// });

			for (int i = 1; i <= sum; i++) {
				int currentThreadNum = i;
				fixedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						PageHelper.startPage(currentThreadNum, 5000);// mybatis分页插件
						List<Map<String, Object>> billList = orderDao.selectDetailOrderListCSV(param);
						int num = 2;
						if (user.get("suName").equals("admin")) {
							num = 1;
						}
						count.append(Array2CSV(billList, num));
					}
				});
			}

			fixedThreadPool.shutdown();
			while (true) {
				if (fixedThreadPool.isTerminated()) {
					String fileName = DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd_hh:mm:ss");

					exportCsv(fileName, count.toString(), request, response);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String Array2CSV(List<Map<String, Object>> lists, int states) {
		StringBuffer buf = new StringBuffer();
		try {
			String[] displayColNamesArr = states == 1
					? "平台订单号,客户订单号,上家订单号,运营商,物理通道名称,归属地,手机号,话费包,代理商名,提交时间,结束时间,状态,原价,上家折扣价(元),上家折扣,客户折扣价(元),客户折扣,利润"
							.split(",")
					: "代理商订单号,代理商名称,运营商,手机号,地区,话费包,开始时间,结束时间,原价(元),状态,代理商折扣价(元),代理商折扣".split(",");
			for (int i = 0; i < displayColNamesArr.length; i++) {
				buf.append(displayColNamesArr[i]).append(",");
			}
			buf.append("\r\n");
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> onerows = lists.get(i);
				if (states == 1) {
					buf.append("'" + onerows.get("orderId")).append(",");
					buf.append("'" + onerows.get("agentOrderId")).append(",");
					buf.append("'" + onerows.get("providerOrderId")).append(",");
					buf.append(onerows.get("physicalName") + "").append(",");
					buf.append(onerows.get("providerName") + "").append(",");
					buf.append(onerows.get("provinceCode") + "").append(",");
					buf.append(onerows.get("phone") + "").append(",");
					buf.append(onerows.get("pkgName") + "").append(",");
					buf.append(onerows.get("agentName") + "").append(",");
					buf.append("'" + onerows.get("applyDate")).append(",");
					buf.append("'" + onerows.get("endDate")).append(",");
					buf.append(onerows.get("status") + "").append(",");
					buf.append(onerows.get("fee") + "").append(",");
					buf.append(onerows.get("providerDiscountFee") + "").append(",");
					buf.append(onerows.get("providerDiscount") + "").append(",");
					buf.append(onerows.get("agentDiscountFee") + "").append(",");
					buf.append(onerows.get("agentDiscount") + "").append(",");
					buf.append(onerows.get("profits") + "").append(",");
				} else {
					buf.append("'" + onerows.get("agentOrderId")).append(",");
					buf.append(onerows.get("agentName") + "").append(",");
					buf.append(onerows.get("providerName") + "").append(",");
					buf.append(onerows.get("phone") + "").append(",");
					buf.append(onerows.get("provinceCode") + "").append(",");
					buf.append(onerows.get("pkgName") + "").append(",");
					buf.append("'" + onerows.get("applyDate")).append(",");
					buf.append("'" + onerows.get("endDate")).append(",");
					buf.append(onerows.get("fee") + "").append(",");
					buf.append(onerows.get("status") + "").append(",");
					buf.append(onerows.get("agentDiscountFee") + "").append(",");
					buf.append(onerows.get("agentDiscount") + "").append(",");
				}

				buf.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public void exportCsv(String fileName, String content, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 读取字符编码
		String csvEncoding = "UTF-8";
		// 设置响应
		response.setCharacterEncoding(csvEncoding);
		response.setContentType("text/csv; charset=" + csvEncoding);
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "max-age=30");
		response.setHeader("Content-Disposition", "attachment; filename=Report-" + fileName + ".csv");

		// 写出响应
		OutputStream os = response.getOutputStream();
		os.write(content.getBytes("GBK"));
		os.flush();
		os.close();
	}

}
