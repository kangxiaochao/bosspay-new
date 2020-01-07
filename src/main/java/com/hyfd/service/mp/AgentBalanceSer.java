package com.hyfd.service.mp;

import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.dao.mp.AgentBalanceDao;
import com.hyfd.service.BaseService;

/**
 * 设置代理商限额
 * @author Administrator
 *
 */
@Service
public class AgentBalanceSer extends BaseService{

	@Autowired
	AgentBalanceDao agentBalanceDao;
	/**
	 * 添加代理商限额
	 * @param req
	 * @return
	 */
	public String addAgentTask(HttpServletRequest req){
		boolean flag = false;
		Map<String, Object> map = getMaps(req); 
		Session session =getSession();
		Map<String, Object> mapsa = agentBalanceDao.selectByPrimaryKets(map.get("agentId").toString());
		if(mapsa != null){
			Map<String, Object> maps = agentBalanceDao.selectByPrimaryKey(map.get("agentId").toString());
			int sum  = 0;
			if(maps != null){
				maps.put("balance", map.get("balance"));
				sum = agentBalanceDao.updateByPrimaryKeySelective(maps);
			}else{
				map.put("id", UUID.randomUUID().toString().replace("-",""));
				sum = agentBalanceDao.insertSelective(map);
			}
			if(sum > 0){
				flag = true;
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败!");
		}else {
			session.setAttribute(GlobalSetHyfd.backMsg, "该代理商没有邮箱不可设置限额!");
		}
		return "mp/agentTask";
	}
}
