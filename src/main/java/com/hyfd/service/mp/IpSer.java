package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.common.utils.IPUtil;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.IpDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class IpSer extends BaseService {
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	IpDao ipDao;
	@Autowired
	AgentDao agentDao;
	
	/**
	 * @功能描述：	根据条件获取满足条件的IP信息数量
	 *
	 * @param m
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public int getIpCount(Map<String, Object> m){
		int count=0;
		try{
			count=ipDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
	/**
	 * @功能描述：	根据id获取ip信息
	 *
	 * @param id
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public Map<String, Object> getIpById(String id){
		return ipDao.selectByPrimaryKey(id);
	}
	
	/**
	 * @功能描述：	根据条件分页获取ip信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String ipList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Page p = getPage(m);// 提取分页参数
			int total = getIpCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> dataList = ipDao.selectAll(m);
			String dataListJson = BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}
	
	/**
	 * @功能描述：	添加一条ip信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String ipAdd(HttpServletRequest req){
		boolean flag = false;
		try {
			// 获取参数
			Map<String, Object> myData = getMaps(req);
			// 根据代理商id获取代理商信息
			Map<String, Object> agentMap = agentDao.selectByPrimaryKey(myData.get("agentId").toString());

			// 获取当前用户信息
			Map<String, Object> userInfoMap = getUser();
			myData.put("id", UUID.randomUUID().toString().replace("-", ""));
			myData.put("createUser", userInfoMap.get("suId"));
			myData.put("agentName", agentMap.get("name").toString());

			int rows = ipDao.insert(myData);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加IP白名单成功" : "添加IP白名单失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "redirect:/ipListPage";
	}
	
    /**
     * @功能描述：	跳转到ip编辑页面
     *
     * @param id
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
    public String ipEditPage(String id){
		try{
			Map<String, Object> mpIp= ipDao.selectByPrimaryKey(id);
			Session session=getSession();
			session.setAttribute("ip", mpIp);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/ipEdit";
	}
	
	/**
	 * @功能描述：	根据id修改ip信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String ipEdit(HttpServletRequest req) {
		try {
			boolean flag = false;
			Map<String, Object> myData = getMaps(req);
			
			// 获取当前用户信息
			Map<String, Object> userInfoMap = getUser();
			myData.put("updateUser", userInfoMap.get("suId"));

			int rows = ipDao.updateByPrimaryKeySelective(myData);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改IP白名单成功" : "修改IP白名单失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "ipListPage";
	}
	
    /**
     * @功能描述：	删除一条ip信息
     *
     * @param id
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
	public String ipDel(String id) {
		boolean flag = false;
		try {
			int rows = ipDao.deleteByPrimaryKey(id);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "IP白名单删除成功" : "IP白名单删除失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return "ipListPage";
	}
	
	
	/**
	 * @功能描述：	根据代理商验证ip地址是否合法(代理商无白名单则直接通过,有白名单则验证ip地址是否合法)
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月30日
	 */
	public boolean validateIp(HttpServletRequest req){
		boolean flag = true;
		String ipAddr = IPUtil.getIpAddr(req);
		Map<String, Object> maps = getMaps(req);
		String terminalName = maps.get("terminalName") + "";// 客户名
		maps.clear();
		maps.put("agentName", terminalName);
		// 根据代理商名称查询是否有ip白名单
		int ipCountByInterface = getIpCountByInterface(maps);
		if (ipCountByInterface > 0) {
			maps.put("ip", ipAddr);
			// 根据代理商名称查询IP地址是否合法
			ipCountByInterface = getIpCountByInterface(maps);
			if (ipCountByInterface > 0) {
				flag = true;
			} else {
				flag = false;
			}
		}
		
		return flag;
	}
	
	/**
	 * @功能描述：	根据条件获取满足条件的IP信息数量,主要于接收下游客户请求时使用
	 *
	 * @param m
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月30日
	 */
	private int getIpCountByInterface(Map<String, Object> maps){
		int count=0;
		try{
			count=ipDao.selectCountByInterface(maps);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
}
