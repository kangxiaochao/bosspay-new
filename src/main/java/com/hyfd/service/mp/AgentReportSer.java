package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;

@Service
public class AgentReportSer extends BaseService{

	@Autowired
	OrderDao orderDao;
	@Autowired
    SysUserRoleDao sysUserRoleDao;
	@Autowired
	AgentDao agentDao;
	
	/**
	 * 代理商流量结算账单导出功能
	 * @author lks 2017年5月22日上午10:30:53
	 * @param request
	 * @param reqonse
	 * @return
	 */
	public String ProviderBillStatementList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		param.put("bizType", "2");
		Map<String,Object> user = getUser();
		String suid = user.get("suId") + "";// 用户Id
        String userAgentId = "";
        boolean agentFlag = judgeAgentRole(suid);
        if (agentFlag)
        {// 如果有代理商角色
            userAgentId = agentDao.selectAgentIdByUserid(suid);
            param.put("userAgentId", userAgentId);
        }
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			int total = orderDao.countAgentStatementList(param);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> billList = orderDao.selectAgentStatementList(param);
			String billListJson=BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("订单提交记录列表查询方法出错"+e.getMessage());
		}
		
		return sb.toString();
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
	
}
