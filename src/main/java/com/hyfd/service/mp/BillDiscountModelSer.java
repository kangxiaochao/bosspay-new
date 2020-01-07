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

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.BillDiscountModelDao;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class BillDiscountModelSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private BillDiscountModelDao billDiscountModelDao;
	
	/**
	 * 根据主键获取记录
	 * @param id
	 * @return
	 */
	public Map<String, Object> getBillDiscountModelById(String id) {
		Map<String, Object> m=new HashMap<String, Object>();
		try{
			m=billDiscountModelDao.selectByPrimaryKey(id);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return m;
	}
	
	/**
	 * 获取记录数量
	 * @param m
	 * @return
	 */
	public int getBillDiscountModelCount(Map<String, Object> m){
		int billDiscountModelCount=0;
		try{
			billDiscountModelCount=billDiscountModelDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return billDiscountModelCount;
	}
	
	/**
	 * 根据条件分页获取代理商流量折扣模板列表数据并生成json
	 * @param req
	 * @return
	 */
	public String billDiscountModelList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		Page p=getPage(m);//提取分页参数
		int total=getBillDiscountModelCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> billList=billDiscountModelDao.selectAll(m);
		String billListJson=BaseJson.listToJson(billList);
		sb.append(billListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	/**
	 * @功能描述：	根据条件获取全部代理商流量折扣模板列表数据并生成json
	 *
	 * @作者：zhangpj		@创建时间：2016年12月16日
	 * @param req
	 * @return
	 */
	public String billDiscountModelAllList(HttpServletRequest req){
		String str = null;
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			List<Map<String, Object>> billList=billDiscountModelDao.selectAll(m);
			str = BaseJson.listToJson(billList);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return str;
	}
	
	/**
	 * 添加代理商流量折扣模板信息
	 * @param req
	 * @return
	 */
	public String billDiscountModelAdd(HttpServletRequest req){
		boolean flag=false;
		try{
		Map<String, Object> myBill=getMaps(req);
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		myBill.put("createUser", userInfoMap.get("suName"));//放入用户
		int rows=billDiscountModelDao.insert(myBill);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/billDiscountModelListPage";		
	}
	
	/**
	 * 修改代理商流量折扣模板信息
	 * @param req
	 * @param id
	 * @return
	 */
	public String billDiscountModelEdit(HttpServletRequest req,String id){
		
		try{
		boolean flag=false;
		Map<String, Object> myBill=getMaps(req);
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		myBill.put("updateUser", userInfoMap.get("suName"));//放入用户
		int rows=billDiscountModelDao.updateByPrimaryKeySelective(myBill);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "billDiscountModelListPage";	
	}
	
	/**
	 * 跳转到代理商流量折扣模板编辑页
	 * @param id
	 * @return
	 */
	public String billDiscountModelEditPage(String id){
		try{
			Map<String, Object> billDiscountModel=getBillDiscountModelById(id);
			Session session=getSession();
			session.setAttribute("billDiscountModel", billDiscountModel);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/billDiscountModelEdit";
	}
	
	/**
	 * 带着模板Id跳转到代理商话费折扣模板详情页
	 * @param id
	 * @return
	 */
	public String billDiscountModelDetailListPage(String id){
		try{
			Session session=getSession();
			session.setAttribute("modelId", id);
		}catch(Exception e){
			log.error(e.getMessage());
		}
		return "mp/billDiscountModelDetailList";
	}
	
	/** 
	 * 删除代理商流量折扣模板信息
	 * @param id
	 * @return
	 */
	public String billDiscountModelDel(String id){
		
		try{
		boolean flag=false;
		int rows=billDiscountModelDao.deleteByPrimaryKey(id);
		if(rows>0){
			flag=true;
		}
		
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"删除成功":"删除失败");
		
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "billDiscountModelListPage";		
	}
	
	
}
