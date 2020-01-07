package com.hyfd.service.sys;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GenerateData;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.sys.SysRoleDao;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class SysRoleSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	SysRoleDao sysRoleDao;
	
	
	public int getSysRoleCount(Map<String, Object> m){
		int count=0;
		try{
			count=sysRoleDao.getSysRoleCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
	public Map<String, Object> getSysRoleBySrId(String srId){
		return sysRoleDao.getSysRoleBySrId(srId);
	}
	
	
	public String sysRoleList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		Page p=getPage(m);//提取分页参数
		int total=getSysRoleCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> dataList=sysRoleDao.getSysRoleList(m);
		String dataListJson=BaseJson.listToJson(dataList);
		sb.append(dataListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	
	public String sysRoleOptions(HttpServletRequest req){
		String dataListJson = "";
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		
		List<Map<String, Object>> dataList=sysRoleDao.getSysRoleList(m);
		dataListJson=BaseJson.listToJson(dataList);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return dataListJson;
	}
	
	public String sysRoleAdd(HttpServletRequest req){
		boolean flag=false;
		try{
		Map<String, Object> myData=getMaps(req);
		
		myData.put("srId", GenerateData.getUUID());
		
		int rows=sysRoleDao.sysRoleAdd(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/sysRoleListPage";
	}
	
	public String sysRoleEditPage(String srId){
		try{
			Map<String, Object> sysRole=getSysRoleBySrId(srId);
			Session session=getSession();
			session.setAttribute("sysRole", sysRole);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "system/sysRoleEdit";
	}
	
	public String sysRoleEdit(HttpServletRequest req,String srId){
		
		try{
		boolean flag=false;
		Map<String, Object> myData=getMaps(req);
		
		int rows=sysRoleDao.sysRoleEdit(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "sysRoleListPage";	
	}
	
	public String sysRoleDel(String srId){
		
		try{
		boolean flag=false;
		int rows=sysRoleDao.sysRoleDel(srId);
		if(rows>0){
			flag=true;
		}
		
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"删除成功":"删除失败");
		
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "sysRoleListPage";		
	}
	
	
}
