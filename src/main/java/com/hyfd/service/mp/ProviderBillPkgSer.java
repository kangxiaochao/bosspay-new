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
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ProviderBillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class ProviderBillPkgSer extends BaseService{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired	
	ProviderBillPkgDao providerBillPkgDao;
	@Autowired
	ProviderDao providerDao;
	@Autowired
	BillPkgDao billPkgDao;
	
	/**
	 * 运营商话费包列表页面
	 * @param id
	 * @return
	 */
	public String providerBillPkgList(String id){
		try{
			Session session=getSession();
			Map<String, Object> provider = providerDao.getProviderById(id);
			session.setAttribute("provider", provider);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/providerBillPkgList";
	}
	
	/**
	 * 运营商话费包数据
	 * @param id
	 * @return
	 */
	public String providerBillPkg(String id,HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		m.put("providerId", id);
		Page p=getPage(m);//提取分页参数
		int total=getProviderBillPkgCount(m);
		p.setCount(total);
		int pageNum=p.getCurrentPage();
		int pageSize=p.getPageSize();
		
		sb.append("{");
		sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
		sb.append(""+getKey("total")+":"+p.getNumCount()+",");
		sb.append(""+getKey("records")+":"+p.getCount()+",");
		sb.append(""+getKey("rows")+":"+"");
		
		PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
		List<Map<String, Object>> billList=providerBillPkgDao.selectAll(m);
		String dataListJson=BaseJson.listToJson(billList);
		sb.append(dataListJson);
		sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	public int getProviderBillPkgCount(Map<String, Object> m){
		int count=0;
		try{
			count=providerBillPkgDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
	public String providerBillPkgEditPage(String id, HttpServletRequest req){
		try {
			Map<String, Object> provider = providerDao.getProviderById(id);
			Map<String,Object> map = new HashMap<String, Object>();
			req.setAttribute("provider", provider);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "mp/providerBillPkgEdit";
	}
	
	public String providerBillPkgDetail(String id){
		String str = "";
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("providerId", id);
			List<Map<String, Object>> allList = providerBillPkgDao.selectAll(map);
			str = BaseJson.listToJson(allList);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
	}
	
	public String providerBillPkgEdit(String id, HttpServletRequest req,String[] billPkgId){
		boolean flag =false;
		try {
			Map<String, Object> map = getMaps(req);
			map.put("providerId", id);
			map.remove("billPkgId");
			String meg = (String) map.get("provinceMeg");
			if(meg!=""&&meg!=null){
				List<Map<String, Object>> billList=providerBillPkgDao.selectAll(map);
				if(billList.size()>0){
					int i = providerBillPkgDao.deleteByPrividerId(id);
					if(i>0){
						flag= providerBillPkgAdd(id,meg,billPkgId);
					}
				}else{
					flag = providerBillPkgAdd(id,meg,billPkgId);
				}
			}
			Session session=getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag?"设置成功":"设置失败");
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "redirect:/providerBillPkgList/"+id+"";
	}
	
	public boolean providerBillPkgAdd(String providerId,String meg,String[] billPkgId){
		boolean flag=false;
		try {
			Map<String, Object> user = getUser();
			String name = (String) user.get("suName");
			String[] split = meg.split(",");
			for(String provinceMeg:split){
				String provinceCode="";
				String cityCode="";
				String[] split2 = provinceMeg.split("-");
				if(split2.length>1){
					provinceCode = split2[0];
					cityCode = split2[1];
				}else{
					provinceCode = split2[0];
					cityCode = "";
				}
//				Map<String,Object> m = new HashMap<String, Object>();
//				List<Map<String, Object>> billPkgList = billPkgDao.selectAll(m);
//				for(Map<String, Object> pkg:billPkgList){
				for (int i = 0; i < billPkgId.length; i++) {
					Map<String,Object> map = new HashMap<String, Object>();
//					String pkgId = (String) pkg.get("id");
					String pkgId = billPkgId[i];
					map.put("providerId", providerId);
					map.put("provinceCode", provinceCode);
					map.put("cityCode", cityCode);
					map.put("billPkgId", pkgId);
					map.put("createUser", name);
					providerBillPkgDao.insertSelective(map);
				}
			}
			flag=true;
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return flag;
	}
	
	public String providerBillPkgDelete(String[] ids){
		boolean flag=false;
		try {
			for(String id:ids){
				int i = providerBillPkgDao.deleteByPrimaryKey(id);
				if(i>0){
					flag=true;
				}else{
					flag=false;
					break;
				}
			}
		} catch (Exception e) {
			flag=false;
			getMyLog(e,log);
		}
		return flag+"";
	}
	
	/**
	 * @功能描述：	根据运营商、流量使用范围、充值地区 获取对应的流量包信息
	 *
	 * @作者：zhangpj		@创建时间：2017年5月11日
	 * @param req
	 * @return
	 */
	public String selectProviderBillPkg(HttpServletRequest req){
		//封装前台参数为map
		Map<String, Object> m=getMaps(req); 
		
		// 根据运营商、流量使用范围、充值地区 获取对应的流量包信息
		List<Map<String, Object>> providerBillPkgList = providerBillPkgDao.selectProviderBillPkg(m);
		
		return JSONObject.toJSONString(providerBillPkgList);
	}
	
	/**
	 * @功能描述：	根据运营商 获取对应的话费包
	 *
	 * @作者：zhangpj		@创建时间：2017年8月1日
	 * @param req
	 * @return
	 */
	public String selectProviderBillPkgByProviderId(HttpServletRequest req){
		//封装前台参数为map
		Map<String, Object> m=getMaps(req); 
		
		// 根据运营商 获取对应的话费包
		List<Map<String, String>> providerBillPkgList = providerBillPkgDao.selectProviderBillPkgByProviderId(m);
		
		return JSONObject.toJSONString(providerBillPkgList);
	}
}
