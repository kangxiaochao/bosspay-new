package com.hyfd.service.mp;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.dao.mp.AgentAccountChargeDao;
import com.hyfd.dao.mp.AgentAccountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;

@Service
public class AgentAccountSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    AgentAccountChargeDao agentAccountChargeDao;
    
    @Autowired
    AgentAccountDao agentAccountDao;
    
    @Autowired
    AgentDao agentDao;
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    
    @Autowired
    IpSer ipSer;
    
    /**
     * 扣款方式
     * 
     * @author lks 2016年12月8日上午11:20:03
     * @param msgMap 消息对象
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public boolean Charge(Map<String, Object> msgMap)
    {
        boolean flag = false;
        List<Map<String, Object>> moneyList = (List<Map<String, Object>>)msgMap.get("moneyList");// 获取扣款的list
        Map<String, Object> order = (Map<String, Object>)msgMap.get("order");// 获取订单对象
        String orderId = (String)order.get("id");// 获取订单ID
        String agentOrderId = (String) order.get("agentOrderId");
        String bizType = (String) order.get("bizType");
        // 循环扣款
        for (int i = 0, size = moneyList.size(); i < size; i++)
        {
            Map<String, Object> moneyMap = moneyList.get(i);
            String agentId = (String)moneyMap.get("agentId");// 获取代理商ID
            double beforeBalance = agentAccountDao.selectBalanceByAgentid(agentId);// 获取代理商余额
            double money = (double)moneyMap.get("money");// 获取应该扣的钱数
            if(beforeBalance + money < 0){
            	return false;
            }
            Map<String, Object> agentAccParam = new HashMap<String, Object>();
            agentAccParam.put("agentId", agentId);
            agentAccParam.put("money", money);
            int chargeFlag = agentAccountDao.charge(agentAccParam);
            if (chargeFlag > 0)
            {
                double afterBalance = beforeBalance + money;// 扣除当前钱数之后的余额
                Map<String, Object> aacMap = new HashMap<String, Object>();
                String id = UUID.randomUUID().toString().replace("-", "");// 生成id
                aacMap.put("id", id);
                aacMap.put("agentId", agentId);
                aacMap.put("orderId", orderId);
                aacMap.put("agentOrderId",agentOrderId);
                aacMap.put("fee", money);
                aacMap.put("balanceBefore", beforeBalance);
                aacMap.put("balanceAfter", afterBalance);
                aacMap.put("type", bizType);
                if(money < 0){
                	aacMap.put("status", "1");
                }else{
                	aacMap.put("status", "2");
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String times = timestamp.toString();
                aacMap.put("applyDate",times);
                int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// 插入扣款记录表
                if (aacFlag > 0)
                {
                    flag = true;// 扣款完成
                }
            }
        }
        return flag;
    }
    
    public String agentAccountEditPage(String id)
    {
        try
        {
            Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(id);
            if(!("0").equals(agent.get("parent_id"))){
            	Session session = getSession();
            	Map<String, Object> user = getUser();
                String suid = user.get("suId") + "";// 用户Id
                if (judgeAgentRole(suid))
                {// 如果有代理商角色
                	Double agentBalance = agentAccountDao.selectBalanceByAgentid(id);
                    agent.put("agentBalance", agentBalance);
                    session.setAttribute("agent", agent);
                	return "mp/agentAccountEdit";
                }
            	session.setAttribute(GlobalSetHyfd.backMsg, "该代理商为二级账户，不可加款");
                return "redirect:/agentListPage";
            }else{
            	Double agentBalance = agentAccountDao.selectBalanceByAgentid(id);
                agent.put("agentBalance", agentBalance);
                Session session = getSession();
                session.setAttribute("agent", agent);
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/agentAccountEdit";
    }
    
    /**
     * 查询客户余额
     * 
     * @author lks 2017年4月13日上午9:57:16
     * @param request
     * @param response
     * @return
     */
	public String queryAgentBalance(HttpServletRequest request,	HttpServletResponse response) {
		JSONObject json = new JSONObject();
		int code = 0;
		String message = "";
		String balance = "0.0";
		boolean flag = ipSer.validateIp(request);
		if (flag) {
			Map<String, Object> paramMap = getMaps(request);
			String terminalName = paramMap.get("terminalName") + "";// 客户名
			String signature = paramMap.get("signature") + "";// 签名
			// 查询客户
			Map<String, Object> agent = agentDao
					.selectByAgentNameForOrder(terminalName);
			if (agent != null) {
				String agentId = agent.get("id") + "";// 代理商ID
				Map<String, Object> agentAccParam = new HashMap<String, Object>();
				agentAccParam.put("agentId", agentId);
				Map<String, Object> agentAccount = agentAccountDao
						.selectByAgentid(agentAccParam);
				if (agentAccount != null) {
					message = "查询成功";
					double balanceResult = Double.parseDouble(agentAccount.get("balance") + "");
					balance = String.format("%.2f", balanceResult);
				} else {
					code = 1;
					message = "用户ID错误";
				}
			} else {
				code = 3;
				message = "提交参数错误，请填写正确的用户ID";
			}
		} else {
			code = 24;
			message = "IP地址不合法";
		}

		json.put("code", code);
		json.put("message", message);
		json.put("balance", balance);
		return json.toJSONString();
	}
    
    /**
     * 给下级代理商分配余额
     * 
     * @author lks 2017年5月10日下午4:16:42
     * @param request
     */
    public String allotBalance(HttpServletRequest request, HttpServletResponse response)
    {
    	Session session = getSession();
        Map<String, Object> user = getUser();
        String suid = user.get("suId") + "";// 用户Id
        String userAgentId = "";
        if (judgeAgentRole(suid))
        {// 如果有代理商角色
            userAgentId = agentDao.selectAgentIdByUserid(suid);
        }
        String msg = "余额划拨失败";
        Map<String, Object> param = getMaps(request);
        String agentId = param.get("agentId") + "";// 代理商ID
        double money = Double.parseDouble(param.get("fee") + "");// 划拨金额
        double balance = getAgentBalance(userAgentId);// 获取当前用户的可用余额
        if (money <= balance)
        {
        	Map<String, Object> m = new HashMap<String, Object>();
        	m.put("agentId", agentId);
			m.put("fee", param.get("fee")+"");
        	int selectNum = agentAccountChargeDao.checkDistinctOrder(m);
			if(selectNum>0){
				msg = "3分钟内不允许重复提交";
        	}else {				
        		double beforeBalance = agentAccountDao.selectBalanceByAgentid(agentId);
        		Map<String, Object> map = new HashMap<String, Object>();
        		map.put("agentId", agentId);
        		map.put("money", money);
        		int num = agentAccountDao.addMoney(map);
        		if (num > 0)
        		{
        			double afterBalance = beforeBalance + money;// 扣除当前钱数之后的余额
        			Map<String, Object> aacMap = new HashMap<String, Object>();
        			String id = UUID.randomUUID().toString().replace("-", "");// 生成id
        			aacMap.put("id", id);
        			aacMap.put("agentId", agentId);
        			aacMap.put("fee", money);
        			aacMap.put("balanceBefore", beforeBalance);
        			aacMap.put("balanceAfter", afterBalance);
        			aacMap.put("type", "2");
        			aacMap.put("status", "3");
        			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String times = timestamp.toString();
                    aacMap.put("applyDate",times);
        			int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// 插入扣款记录表
        			if (aacFlag != 1) {
        				log.error("扣款记录表未插入成功");
        			}
        			msg = "余额划拨成功";
        		}
			}
        }
        else
        {
            msg = "余额不足";
        }
        session.setAttribute(GlobalSetHyfd.backMsg, msg);
        return "redirect:/agentListPage";
    }
    
    /**
     * 统计利润
     * 
     * @author lks 2017年5月10日下午6:05:40
     */
    public String countProfit(HttpServletRequest request, HttpServletResponse response)
    {
        JSONObject json = new JSONObject();
        double profit = 0.0;
        double amount = 0.0;
        int sumNum = 0, succNum = 0, failNum = 0;
        double rate = 0.0;
        Map<String, Object> user = getUser();
        String suid = user.get("suId") + "";// 用户Id
        String agentId = "";
        if (judgeAgentRole(suid))
        {// 如果有代理商角色
            agentId = agentDao.selectAgentIdByUserid(suid);
        }
        Map<String, Object> param = getMaps(request);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> numMap = new HashMap<String, Object>();
        boolean isToday = false;
        if (param.containsKey("isToday"))
        {
            isToday = Boolean.parseBoolean(param.get("isToday") + "");// 是否查询当天的利润
        }
        if (agentId != null && !agentId.equals(""))
        {
            map.put("agentId", agentId);
            map.put("isToday", isToday);
            double thisProfit = agentAccountChargeDao.countProfit(map);// 查询
            amount = thisProfit;
            map.put("parentId", agentId);
            map.remove("agentId");
            double childProfit = agentAccountChargeDao.countProfit(map);
            profit = thisProfit - childProfit;
            numMap.put("agentId", agentId);
            numMap.put("isToday", isToday);
            numMap.put("isAll", true);
            sumNum = agentAccountChargeDao.countOrder(numMap);// 总数
            numMap.put("isFail", true);
            numMap.remove("isAll");
            failNum = agentAccountChargeDao.countOrder(numMap);
            succNum = sumNum - failNum;
        }
        else
        {// 查询平台所有利润
            map.put("isToday", isToday);
            profit = orderDao.selectProfit(map);
            amount = agentAccountChargeDao.countProfit(map);
            numMap.put("isToday", isToday);
            sumNum = orderDao.countOrder(numMap);// 总数
            numMap.put("status", "3");
            succNum = orderDao.countOrder(numMap);
            failNum = sumNum - succNum;
        }
        json.put("profit", profit);
        json.put("amount", Math.abs(amount));
        json.put("sumNum", sumNum);
        json.put("succNum", succNum);
        json.put("failNum", failNum);
        if (sumNum != 0)
        {
            rate = ((double)succNum) / (((double)sumNum) / 100.0);
        }
        json.put("rate", String.format("%.2f", rate) + "%");
        return json.toJSONString();
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
     * 获取代理商的可用余额
     * 
     * @author lks 2017年5月18日下午3:01:26
     * @param agentId
     * @return
     */
    public double getAgentBalance(String agentId)
    {
        double balance = agentAccountDao.selectBalanceByAgentid(agentId);
        double childBalance = agentAccountDao.selectChildBalanceByAgentid(agentId);
        return balance - childBalance;
    }
    
    public String getAgentAccount(String suId)
    {
        Map<String, Object> agentMap = agentDao.selectByUserId(suId);
        String agentId = agentMap.get("id") + "";
        double balance = getAgentBalance(agentId);
        return String.format("%.2f", balance);
    }
    
    /**
     * 单个扣款
     * @param msgMap
     * @return
     */
    @Transactional
    public boolean ChargeForBatch(Map<String, Object> msgMap)
    {
        boolean flag = false;
        String bizType = (String)msgMap.get("bizType");
        String agentId = (String)msgMap.get("agentId");// 获取代理商ID
        String agentOrderId = (String)msgMap.get("agentOrderId");
        double beforeBalance = agentAccountDao.selectBalanceByAgentid(agentId);// 获取代理商余额
        double money = new Double(msgMap.get("money").toString());// 获取应该扣的钱数
        Map<String, Object> agentAccParam = new HashMap<String, Object>();
        agentAccParam.put("agentId", agentId);
        agentAccParam.put("money", money);
        int chargeFlag = agentAccountDao.addMoney(agentAccParam);
        if (chargeFlag > 0)
        {
            double afterBalance = beforeBalance + money;// 扣除当前钱数之后的余额
            Map<String, Object> aacMap = new HashMap<String, Object>();
            String id = UUID.randomUUID().toString().replace("-", "");// 生成id
            aacMap.put("id", id);
            aacMap.put("agentId", agentId);
            aacMap.put("agentOrderId",agentOrderId);
            aacMap.put("fee", money);
            aacMap.put("balanceBefore", beforeBalance);
            aacMap.put("balanceAfter", afterBalance);
            aacMap.put("type", bizType);
            if(money < 0){
            	aacMap.put("status", "1");
            }else{
            	aacMap.put("status", "2");
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String times = timestamp.toString();
            aacMap.put("applyDate",times);
            aacMap.put("remark",msgMap.get("remark"));
            int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// 插入扣款记录表
            if (aacFlag > 0)
            {
                flag = true;// 扣款完成myhaoiswz
            }
        }
        return flag;
    }
}
