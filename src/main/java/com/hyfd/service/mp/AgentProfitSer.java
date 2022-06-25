package com.hyfd.service.mp;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.dao.mp.*;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AgentProfitSer extends BaseService{
	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	AgentDao agentDao;

	@Autowired
	AgentProfitDao agentProfitDao;

	@Autowired
	SysUserRoleDao sysUserRoleDao;

	@Autowired
	AgentAccountDao agentAccountDao;

	@Autowired
	AgentProfitChargeDao agentProfitChargeDao;

	@Autowired
	AgentAccountChargeDao agentAccountChargeDao;

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


	public String agentProfitEditPage(){
		try
		{
			Session session = getSession();
			Map<String, Object> user = getUser();
			String suid = user.get("suId") + "";// 用户Id
			// 查询用户是否是代理商
			Map<String, Object> agentMap = agentDao.selectByUserId(suid);
			if (agentMap != null)
			{
				String agentId = agentMap.get("id") + "";
				Double agentProfit = agentAccountDao.selectProfitByAgentid(agentId);
				agentMap.put("agentProfit", agentProfit);
				Double agentBalance = agentAccountDao.selectBalanceByAgentid(agentId);
				agentMap.put("agentBalance", agentBalance);
				session.setAttribute("agent", agentMap);
				return "mp/agentProfitEdit";
			}
			session.setAttribute(GlobalSetHyfd.backMsg, "非代理商用户无法进行利润加款！");
		}
		catch (Exception e)
		{
			getMyLog(e, log);
		}
		return "redirect:/agentListPage";
	}

	/**
	 * 给代理商利润加款
	 *
	 * @author xxz 2022年06月04日下午22:24:30
	 * @param request
	 */
	@Transactional
	public String allotProfit(HttpServletRequest request, HttpServletResponse response)
	{
		Session session = getSession();
		Map<String, Object> user = getUser();
		String suid = user.get("suId") + "";// 用户Id
		Map<String, Object> agentMap = agentDao.selectByUserId(suid);
		if(agentMap != null && judgeAgentRole(suid)){
			String userAgentId = agentMap.get("id")+"";
			String msg = "余额划拨失败";
			Map<String, Object> param = getMaps(request);
			double money = Double.parseDouble(param.get("fee") + "");			// 加款金额
			double balance = agentAccountDao.selectProfitByAgentid(userAgentId);	// 获取当前用户的可用利润余额
			if (money <= balance)
			{
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("agentId", userAgentId);
				m.put("fee", param.get("fee")+"");
				int selectNum = agentAccountChargeDao.checkDistinctOrder(m);
				if(selectNum>0){
					msg = "3分钟内不允许重复提交";
				}else {
					//获取加款代理商Set集合
					Set<String> agentIdSet = new HashSet<>();
					String parentId = (String) agentMap.get("parent_id");
					//判断当前代理商是否存在上级代理商
					if (!parentId.equals("0") && !parentId.trim().equals("")) {
						//递归获取包含当前代理商在内的所有上级代理商ID
						agentIdSet = getParentAgentId(userAgentId);
					}else{
						agentIdSet.add(userAgentId);
					}
					//扣除代理商利润
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("agentId", userAgentId);
					map.put("profit", -money);  //利润加款金额为负数
					int num = agentAccountDao.agentProfitCharge(map);
					if(num > 0){
						num = 0;    						//清零,用于统计上级代理商余额是否添加成功
						//获取加款前余额
						double beforeBalance = agentAccountDao.selectBalanceByAgentid(userAgentId);
						//代理商余额加款， 非一级代理商使用利润加款，需要给其所有上级代理商加款,才符合代理商扣款逻辑
						map = new HashMap<String, Object>();
						for (String agentId : agentIdSet) {
							map.put("agentId", agentId);
							map.put("money", money);
							map.put("beforeBalance", beforeBalance);
							num += agentAccountDao.addMoney(map);
						}
						if (num == agentIdSet.size())
						{
							String remark = StringUtils.isNotEmpty(param.get("remark")+"") ? param.get("remark")+"" : "代理商利润自主加款";
							//生成代理商利润加款明细
							double afterProfit = balance - money;                 // 代理商利润扣除加款金额后的余额
							Map<String, Object> aacMap = new HashMap<String, Object>();
							String id = UUID.randomUUID().toString().replace("-", "");    // 生成id
							aacMap.put("id", id);
							aacMap.put("agentId", userAgentId);
							aacMap.put("orderId", "");
							aacMap.put("agentOrderId","");
							aacMap.put("fee", -money);
							aacMap.put("balanceBefore", balance);
							aacMap.put("balanceAfter", afterProfit);
							aacMap.put("type", "2");
							aacMap.put("isRefund", "0"); //标识该笔记录的利润是否被退回 0：利润未退回  1：利润已退回
							aacMap.put("status", "1");   //扣款
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());
							String times = timestamp.toString();
							aacMap.put("applyDate",times);
							aacMap.put("remark",remark);
							int saveCount = agentProfitChargeDao.insertSelective(aacMap);// 插入利润变更记录
							if(saveCount == 0){
								log.error("新增代理商利润扣款明细失败！");
							}
							//生成当前代理商的加款明细
							double afterBalance = beforeBalance + money;// 加款后余额
							aacMap = new HashMap<String, Object>();
							id = UUID.randomUUID().toString().replace("-", "");// 生成id
							aacMap.put("id", id);
							aacMap.put("agentId", userAgentId);
							aacMap.put("fee", money);
							aacMap.put("balanceBefore", beforeBalance);
							aacMap.put("balanceAfter", afterBalance);
							aacMap.put("type", "2");
							aacMap.put("status", "3");
							timestamp = new Timestamp(System.currentTimeMillis());
							times = timestamp.toString();
							aacMap.put("applyDate",times);
							aacMap.put("remark",remark);
							int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// 插入扣款记录表
							if (aacFlag != 1) {
								log.error("扣款记录表未插入成功");
							}
							msg = "余额划拨成功";
						}else{
							msg = "利润加款失败!";
							session.setAttribute(GlobalSetHyfd.backMsg, msg);
							TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						}
					}
				}
			}
			else
			{
				msg = "利润余额不足！";
			}
			session.setAttribute(GlobalSetHyfd.backMsg, msg);
		}
		return "redirect:/agentListPage";
	}

	/**
	 * 递归获取指定代理商的所有上级代理商ID
	 * @param agentId
	 * @return agentIdSet
	 */
	public Set<String> getParentAgentId(String agentId){
		Set<String> set = new HashSet<>();
		set.add(agentId);
		Map<String, Object> agentMap = agentDao.selectById(agentId);
		String parentId = (String) agentMap.get("parent_id");
		//判断当前代理商是否存在上级代理商
		if (!parentId.equals("0") && !parentId.trim().equals("")) {
			set.addAll(getParentAgentId(parentId));
		}
		return set;
	}

	/**
	 * 判断用户是否具有代理商的角色
	 *
	 * @author lks 2017年5月18日下午2:36:33
	 * @param suid
	 * @return
	 */
	public boolean judgeAgentRole(String suid)
	{
		boolean flag = false;
		List<Map<String, Object>> list = sysUserRoleDao.getHasSysRoleList(suid);
		for (Map<String, Object> role : list)
		{
			String roleName = role.get("srName") + "";
			if (roleName.equals("代理商"))
			{
				flag = true;
			}
		}
		return flag;
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
	 * @param tempName
	 * @param filename
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
