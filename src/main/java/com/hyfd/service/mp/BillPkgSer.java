package com.hyfd.service.mp;

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
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class BillPkgSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	BillPkgDao billPkgDao;
	
	public int getBillPkgCount(Map<String, Object> m){
		int providerCount=0;
		try{
			providerCount=billPkgDao.getBillPkgCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return providerCount;
	}
	
	public String billPkgList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		Page p=getPage(m);//提取分页参数
		int total=getBillPkgCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> dataList=billPkgDao.selectAll(m);
		String dataListJson=BaseJson.listToJson(dataList);
		sb.append(dataListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}

	public String billPkgAdd(HttpServletRequest req){
		boolean flag=false;
		try{
		Map<String, Object> myData=getMaps(req);
		
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		
		myData.put("createUser", userInfoMap.get("suId"));//放入创建用户
		
		int rows=billPkgDao.insert(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/billPkgListPage";		
	}
	
	public String billPkgListGet(){
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=new HashMap<String, Object>();
			List<Map<String, Object>> dataList=billPkgDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
		}catch(Exception e){
			getMyLog(e,log);
		}		
		return sb.toString();
	}
	
	public String billPkgEditPage(String id){
		try{
			Map<String, Object> billPkg = billPkgDao.selectByPrimaryKey(id);
			Session session=getSession();
			session.setAttribute("billPkg", billPkg);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/billPkgEdit";
	}

	public String billPkgEdit(HttpServletRequest req,String id){
		
		try{
		boolean flag=false;
		Map<String, Object> myData=getMaps(req);
		
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		
		myData.put("update_user", userInfoMap.get("suId"));//放入创建用户
		int rows=billPkgDao.updateByPrimaryKeySelective(myData);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "billPkgListPage";	
	}

	public String billPkgDel(String id) {
		try {
			boolean flag = false;
			
			// 组合id格式
			String[] ids = id.split(",");
			
			int rows = billPkgDao.batchDeleteByPrimaryKey(ids);

			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "billPkgListPage";
	}

}
