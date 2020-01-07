package com.hyfd.service.mp;

import java.util.ArrayList;
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
import com.hyfd.dao.mp.ProviderBillGroupDao;
import com.hyfd.dao.mp.ProviderBillRechargeGroupDao;
import com.hyfd.service.BaseService;

/**
 * @功能描述：	流量通道组相关业务
 *
 * @作者：zhangpj		@创建时间：2016年12月17日
 */
@Service
@Transactional
public class ProviderBillRechargeGroupSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private ProviderBillRechargeGroupDao providerBillRechargeGroupDao;
	
	/**
	 * 根据主键获取记录
	 * @param id
	 * @return
	 */
	public Map<String, Object> getProviderBillRechargeGroupById(String id) {
		Map<String, Object> m=new HashMap<String, Object>();
		try{
			m=providerBillRechargeGroupDao.selectByPrimaryKey(id);
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
	public int getCountCount(Map<String, Object> m){
		int providerBillGroupCount=0;
		try{
			providerBillGroupCount=providerBillRechargeGroupDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return providerBillGroupCount;
	}
	
	/**
	 * 根据条件分页获取流量通道组列表流量并生成json
	 * @param req
	 * @return
	 */
	public String providerBillRechargeBill(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		Page p=getPage(m);//提取分页参数
		int total=getCountCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> billList=providerBillRechargeGroupDao.selectAll(m);
		String billListJson=BaseJson.listToJson(billList);
		sb.append(billListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	/**
	 * 添加复充流量通道组信息
	 * @param req
	 * @return
	 */
	public String providerBillRechargeAdd(HttpServletRequest req){
		boolean flag=false;
		try{
		Map<String, Object> myBill=getMaps(req);
		
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		
		myBill.put("create_user", userInfoMap.get("suId"));//放入创建用户
		
		int rows=providerBillRechargeGroupDao.insert(myBill);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"添加成功":"添加失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "redirect:/providerBillRechargeGroupList";		
	}
	
	/**
	 * 跳转到复充流量通道组编辑页
	 * @param id
	 * @return
	 */
	public String providerBillRechargeGroupEditPage(String id){
		try{
			Map<String, Object> providerBillRechargeGroup=getProviderBillRechargeGroupById(id);
			Session session=getSession();
			session.setAttribute("providerBillRechargeGroup", providerBillRechargeGroup);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/providerBillRechargeGroupEdit";
	}
	
	/**
	 * 修改复充流量通道组信息
	 * @param req
	 * @param id
	 * @return
	 */
	public String editBillRechargeGroup(HttpServletRequest req){
		
		try{
		boolean flag=false;
		Map<String, Object> myBill=getMaps(req);
		
		Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
		
		myBill.put("update_user", userInfoMap.get("suId"));//放入创建用户
		
		int rows=providerBillRechargeGroupDao.updateByPrimaryKeySelective(myBill);
		if(rows>0){
			flag=true;
		}
		Session session=getSession();
		session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "providerBillRechargeGroupList";	
	}
	
	/**
	 * 优先级上移
	 * @param req
	 * @param id
	 * @return
	 */
	public List<String> updateRechargePriority(int type,HttpServletRequest req){
		List<String> list = new ArrayList<String>();
		Map<String, Object> myBill=getMaps(req);
		int maxPr = providerBillRechargeGroupDao.getMaxPriority(myBill);
		String pr = (String)myBill.get("priority");
		if(maxPr==Integer.parseInt(pr) && type==2){
			list.add("maxPr");
			return list;
		}
		int priority=0;
		if(type==1){
			priority=Integer.parseInt(pr)-1;	
		}else{
			priority=Integer.parseInt(pr)+1;
		}
		
		myBill.put("priority", priority);
		Map<String, Object> changeMap = new HashMap<String, Object>();
		changeMap.put("providerId", myBill.get("providerId"));
		changeMap.put("provinceCode",myBill.get("provinceCode"));
		changeMap.put("priority", priority);
		String changeId = providerBillRechargeGroupDao.getRechargeIdByPriority(changeMap);
		if(changeId==null||"".equals(changeId)){
			list.add("false");
			return list;
		}
		changeMap.put("id", changeId);
		if(type==1){
			changeMap.put("priority", priority+1);
		}else{
			changeMap.put("priority", priority-1);
		}
		int r = providerBillRechargeGroupDao.updateRechargePriority(changeMap);
		if(r<0){
			list.add("false");
			return list;
		}
		int a = providerBillRechargeGroupDao.updateRechargePriority(myBill);
		if(a<0){
			list.add("false");
			return list;
		}
		list.add("success");
		return list;	
	}
	
	/**
	 * 删除复充流量通道组信息
	 * @param id
	 * @return
	 */
	public List<String> deleteRechargeGroupById(String id){
		List<String> list = new ArrayList<String>();
		Map<String, Object> map =providerBillRechargeGroupDao.getBillById(id);
		List<Map<String, Object>> billList=providerBillRechargeGroupDao.getRechargeBillById(map);
		if(billList.size()>0){
			for(Map<String, Object> billMap:billList){
				int pr = (int) billMap.get("priority");
				pr=pr-1;
				billMap.put("priority", pr);
				providerBillRechargeGroupDao.updateRechargePriority(billMap);
			}
		}
		int rows=providerBillRechargeGroupDao.deleteByPrimaryKey(id);
		if(rows<=0){
			list.add("false");
			return list;
		}
		list.add("success");
		return list;	
	}
}
