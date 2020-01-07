package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csvreader.CsvWriter;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.AgentAccountChargeAuditDao;
import com.hyfd.dao.mp.AgentAccountChargeDao;
import com.hyfd.dao.mp.AgentAccountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;


@Service
public class AgentAccountChargeAuditSer extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private AgentAccountChargeAuditDao agentAccountChargeAuditDao;
	@Autowired
	AgentAccountDao agentAccountDao;
	@Autowired
	AgentAccountChargeDao agentAccountChargeDao;
	@Autowired
	AgentDao agentDao;

	public String agentAccountChargeAuditAdd(HttpServletRequest req) {
		Session session = getSession();
		try {
			boolean flag = false;
			Map<String, Object> myBill = getMaps(req);
			String agentId = (String) myBill.get("agentId");
			String type = (String) myBill.get("type");
			String money = (String) myBill.get("fee");
			double fee = Double.parseDouble(money);
			Map<String, Object> user = getUser();
			String userName = (String) user.get("suName");
			myBill.put("applyUser", userName);
			myBill.put("myuuid", UUID.randomUUID().toString().replaceAll("-", ""));
			Map<String, Object> agentAccount = agentAccountDao
					.selectByAgentid(myBill);
			String ba = agentAccount.get
					("balance") + "";
			double balan = Double.parseDouble(ba);
			if (type.equals("1")) {
				int status = 0;
				myBill.put("balance", balan);
				myBill.put("flag", status);
				int rows = agentAccountChargeAuditDao
						.agentAccountChargeAuditAdd(myBill);
				if (rows > 0) {
					flag = true;
				}
			}
			if (type.equals("2")) {
				Map<String, Object> m = new HashMap<String, Object>();
				UUID uuid = UUID.randomUUID();
				String id = uuid.toString().replace("-", "");
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String applyDate = df.format(new Date());
				Double balance = fee + balan;
				m.put("id", id);
				m.put("agentId", agentId);
				m.put("orderId", myBill.get("myuuid"));
				m.put("fee", fee);
				m.put("type", 5);
				m.put("balanceBefore", balan);
				m.put("balanceAfter", balance);
				m.put("applyDate", applyDate);
				m.put("remark", myBill.get("remark"));
				myBill.put("balance", balance);
				myBill.put("updateUser", userName);
				
				int rows = agentAccountDao.updateByAgentIdSelective(myBill);
				if (rows > 0) {
					int i = agentAccountChargeDao.insertSelective(m);
					int r = agentAccountChargeAuditDao.agentAccountChargeAuditAdd(myBill);
					if (r > 0 && i > 0) {
						flag = true;
					}
				}
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "提交成功" : "提交失败!");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "redirect:/agentListPage";
	}

	/**
	 * 根据条件分页获取代理商冲扣值列表数据并生成json
	 * 
	 * @param req
	 * @return
	 */
	public String agentAccountChargeAuditList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
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
			List<Map<String, Object>> billList = agentAccountChargeAuditDao
					.selectAgentAccountChargeAuditList(m);
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
	 * 
	 * @param m
	 * @return
	 */
	public int getAgentCount(Map<String, Object> m) {
		int agentCount = 0;
		try {
			agentCount = agentAccountChargeAuditDao.selectCount(m);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return agentCount;
	}

	public String agentAccountChargeAuditEdit(HttpServletRequest req) {
		Session session = getSession();
		try {
			boolean flag = false;
			Map<String, Object> myData = getMaps(req);
			String status = (String) myData.get("flag");
			String id = (String) myData.get("id");
			if (status.equals("1")) {
				String agentId = (String) myData.get("agentId");
				if (agentId != null && agentId != "") {
					String money = (String) myData.get("fee");
					double fee = Double.parseDouble(money);
					Map<String, Object> user = getUser();
					String users = (String) user.get("suName");
					myData.put("confirmUser", users);
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("agentId", agentId);
					Map<String, Object> agentAccount = agentAccountDao.selectByAgentid(m);
					String ba = agentAccount.get("balance") + "";
					double balan = Double.parseDouble(ba);
					Double balance = fee + balan;
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("flag", status);
					param.put("yue", balance);
					param.put("id", id);
					param.put("confirmUser", users);
					Integer rows = agentAccountChargeAuditDao.agentAccountChargeAuditEdit(param);
					if (rows > 0) {
						m.put("balance", balance);
						m.put("updateUser", users);
						int row = agentAccountDao.updateByAgentIdSelective(m);
						if (row > 0) {
							Map<String,Object> map = agentAccountChargeAuditDao.selectByPrimaryKey(id);
							Map<String, Object> accountMap = new HashMap<String, Object>();
							UUID uuid = UUID.randomUUID();
							String accountId = uuid.toString().replace("-", "");
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String applyDate = df.format(new Date());
							accountMap.put("id", accountId);
							accountMap.put("agentId", agentId);
							accountMap.put("orderId", id);
							accountMap.put("fee", fee);
							accountMap.put("type", 3);
							accountMap.put("balanceBefore", balan);
							accountMap.put("balanceAfter", balance);
							accountMap.put("applyDate", applyDate);
							accountMap.put("status", 3);
							accountMap.put("remark", map.get("remark"));
							int i = agentAccountChargeDao.insertSelective(accountMap);
							if (i > 0) {
								flag = true;
							}
						}
					}
				}
			} else {
				Integer row = agentAccountChargeAuditDao.agentAccountChargeAuditEdit(myData);
				if (row > 0) {
					flag = true;
				}
			}

			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "审核成功" : "审核失败!");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "agentAccountChargeAuditPage";
	}
	
	public String agentAccountChargeAuditEditPage(String id){
		try {
			Map<String, Object> agentAccountChargeAudit = agentAccountChargeAuditDao.selectByPrimaryKey(id);
			Session session = getSession();
			session.setAttribute("agentAccountChargeAudit", agentAccountChargeAudit);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/agentAccountChargeAuditEdit";
	}
	
	public String agentAccountChargeEditName(HttpServletRequest req){
		Session session = getSession();
		try {
			boolean flag = false;
			Map<String, Object> myBill = getMaps(req);
			Integer i = agentAccountChargeAuditDao.agentAccountChargeEditName(myBill);
			if(i>0){
				flag = true;
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败!");	
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "agentAccountChargeAuditPage";
	}
	
	/**
	 * 导出订单加扣款记录
	 * @param req
	 */
	public void agentAccountChargeAuditReport(HttpServletRequest req,HttpServletResponse resp) {
		Map<String, Object> param = getMaps(req);
		List<Map<String, Object>> list = agentAccountChargeDao.selectAll(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		exportExcel("agentRechargeRecord", "agentRechargeRecord", map, req, resp);
	}
		
	/**
	 * 导出代理商冲扣值审核列表
	 * @param req	
	 * @return 	
	 */
	public void agentAccountChargeAuditListExport(HttpServletRequest req, HttpServletResponse resp) {
		Map<String, Object> param = getMaps(req);
		List<Map<String, Object>> list = agentAccountChargeAuditDao
				.selectAgentAccountChargeAuditList(param);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		System.err.print("map :   "+map);
		exportExcel("agentAccountChargeAuditList", "agentAccountChargeAuditList", map, req, resp);
	}
	//{del_flag=1, qudao=zhangweiwei, flag=1, agent_id=f923123cef064108bafdda43dcdaa847, fee=9000.0000, yue=9000.0000, confirm_user=admin, apply_user=admin, agentName=lihui, type=1, confirmDate=2018-09-27 11:21:42, apply_date=2018-09-27 11:21:38.0, confirm_date=2018-09-27 11:21:42.0, id=d85ab650ce3a4c83bfa1eab9a025dcaf, applyDate=2018-09-27 11:21:38}, 
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
