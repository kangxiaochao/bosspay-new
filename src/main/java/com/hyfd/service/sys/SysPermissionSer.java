package com.hyfd.service.sys;

import java.util.HashMap;
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
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.sys.SysPermissionDao;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class SysPermissionSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	SysPermissionDao sysPermissionDao;
	
	public int getSysPermissionCount(Map<String, Object> m){
		int count=0;
		try{
			count=sysPermissionDao.getSysPermissionCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
	public Map<String, Object> getSysPermissionBySpId(String spId) {
		Map<String, Object> m=new HashMap<String, Object>();
		try{
			m=sysPermissionDao.getSysPermissionBySpId(spId);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return m;
	}
	
	public String sysPermissionList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		Page p=getPage(m);//提取分页参数
		int total=getSysPermissionCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> dataList=sysPermissionDao.getSysPermissionList(m);
		String dataListJson=BaseJson.listToJson(dataList);
		sb.append(dataListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	public String sysPermissionAdd(HttpServletRequest req){
		boolean flag=false;
		try{
		Map<String, Object> myData=getMaps(req);
		
		int rows=sysPermissionDao.sysPermissionAdd(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/sysPermissionListPage";
	}
	
	public String sysPermissionEditPage(String spId){
		try{
			Map<String, Object> sysPermission=getSysPermissionBySpId(spId);
			Session session=getSession();
			session.setAttribute("sysPermission", sysPermission);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "system/sysPermissionEdit";
	}
	
	public String sysPermissionEdit(HttpServletRequest req,String spId){
		
		try{
		boolean flag=false;
		Map<String, Object> myData=getMaps(req);
		
		int rows=sysPermissionDao.sysPermissionEdit(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "sysPermissionListPage";	
	}
	
	public String sysPermissionDel(String spId){
		
		try{
		boolean flag=false;
		int rows=sysPermissionDao.sysPermissionDel(spId);
		if(rows>0){
			flag=true;
		}
		
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"删除成功":"删除失败");
		
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "sysPermissionListPage";		
	}
	
	
}
