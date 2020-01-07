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
import com.hyfd.dao.sys.SysFunctionDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.BaseService;


@Service
@Transactional
public class SysFunctionSer extends BaseService {
	
	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	SysFunctionDao sysFunctionDao;
	
	@Autowired
	RabbitMqProducer mqProducer;


	public int getSysFunctionCount(Map<String, Object> m) {
		int count = 0;
		try {
			count = sysFunctionDao.getSysFunctionCount(m);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return count;
	}

	public List<Map<String, Object>> getSysFunctionList() {
		return sysFunctionDao.getSysFunctionList();
	}

	public String getSysFunction() {
		Session session = getSession();
		List<Map<String, Object>> sysFunctionList = sysFunctionDao.getSysFunctionList();
		session.setAttribute("sysFunction", sysFunctionList);
		return "system/sysFunctionAdd";
	}

	public String sysFunctionList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Page p = getPage(m);// 提取分页参数
			int total = getSysFunctionCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> dataList = sysFunctionDao
					.getSysFunctionList();
			String dataListJson = BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return sb.toString();
	}

	/**
	 * 获取所有角色
	 * */
	public String sysGetRoleList() {
		StringBuilder sb = new StringBuilder();
		try {
			List<Map<String, Object>> dataList = sysFunctionDao
					.sysGetRoleList();
			String dataListJson = BaseJson.listToJson(dataList);
			sb.append(dataListJson);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return sb.toString();
	}

	/**
	 * 获取所有权限
	 * */
	public String sysGetPermissionList() {
		StringBuilder sb = new StringBuilder();
		try {
			List<Map<String, Object>> dataList = sysFunctionDao
					.sysGetPermissionList();
			String dataListJson = BaseJson.listToJson(dataList);
			sb.append(dataListJson);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return sb.toString();
	}

	public String sysFunctionAdd(HttpServletRequest req) {
		boolean flag = false;
		try {
			Map<String, Object> myData = getMaps(req);
			String spId = (String) myData.get("spId");
			if ("".equals(spId)) {
				myData.remove("spId");
			}
			String srId = (String) myData.get("srId");
			if ("".equals(srId)) {
				myData.remove("srId");
			}
			int rows = sysFunctionDao.sysFunctionAdd(myData);
			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败");
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "redirect:/sysFunctionListPage";
	}

	public String sysFunctionEditPage(String sfId) {
		try {
			Map<String, Object> sysFunction = sysFunctionDao
					.getSysFunctionBySfId(sfId);
			Session session = getSession();
			session.setAttribute("sysFunction", sysFunction);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "system/sysFunctionEdit";
	}

	public String sysFunctionEdit(HttpServletRequest req) {
		try {
			boolean flag = false;
			Map<String, Object> myData = getMaps(req);
			String spId = (String) myData.get("spId");
			if ("".equals(spId)) {
				myData.remove("spId");
				myData.put("spId", null);
			}
			String srId = (String) myData.get("srId");
			if ("".equals(srId)) {
				myData.remove("srId");
				myData.put("srId", null);
			}
			
			int rows = sysFunctionDao.sysFunctionEdit(myData);
			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "sysFunctionListPage";
	}

	public String sysFunctionDel(String sfId) {
		try {
			boolean flag = false;
			int rows = sysFunctionDao.sysFunctionDel(sfId);
			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "sysFunctionListPage";
	}

}
