package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.ProviderAccountDao;
import com.hyfd.dao.mp.ProviderAccountChargeDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;


@Service
public class ProviderAccountChargeSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ProviderAccountChargeDao providerAccountChargeDao;
	@Autowired
	ProviderAccountDao providerAccountDao;
	@Autowired
	ProviderDao providerDao;
	@Autowired
	ProviderPhysicalChannelDao physicalChannelDao;
	
	/**
	 * 根据条件分页获取代理商冲扣值列表数据并生成json
	 * @param req
	 * @return
	 */
	public String providerAccountChargeList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		String providerName = (String) m.get("providerName");
		if(providerName!=null&&providerName!=""){
			String providerId = physicalChannelDao.getProviderIdByName(providerName);
			if(providerId!=null&&providerId!=""){
				m.put("providerId", providerId);
			}
		}
		Page p=getPage(m);//提取分页参数
		int total=getProviderCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> billList=providerAccountChargeDao.selectProviderAccountChargeList(m);
		String billListJson=BaseJson.listToJson(billList);
		sb.append(billListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	/**
	 * 获取记录数量
	 * @param m
	 * @return
	 */
	public int getProviderCount(Map<String, Object> m){
		int ProviderCount=0;
		try{
			ProviderCount=providerAccountChargeDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return ProviderCount;
	}
}
