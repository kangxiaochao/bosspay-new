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
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.RabbitmqPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class RabbitmqPhysicalChannelSer extends BaseService {
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	RabbitmqPhysicalChannelDao rabbitmqPhysicalChannelDao;
	
	/**
	 * @功能描述：	根据条件获取满足条件的物理通道对应的MQ通道信息数量
	 *
	 * @param m
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public int getRabbitmqPhysicalChanneCount(Map<String, Object> m){
		int count=0;
		try{
			count=rabbitmqPhysicalChannelDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return count;
	}
	
	/**
	 * @功能描述：	根据id获取运营商对应的MQ通道信息
	 *
	 * @param id
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public Map<String, Object> getRabbitmqPhysicalChanneById(String id){
		return rabbitmqPhysicalChannelDao.selectByPrimaryKey(id);
	}
	
	/**
	 * @功能描述：	根据条件分页获取运营商对应的MQ通道信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String rabbitmqPhysicalChannelList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Page p = getPage(m);// 提取分页参数
			int total = getRabbitmqPhysicalChanneCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> dataList = rabbitmqPhysicalChannelDao.selectAll(m);
			String dataListJson = BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}
	
	/**
	 * @功能描述：	获取所有运营商对应的MQ通道信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月5日
	 */
	public String rabbitmqPhysicalChannelListAll(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			
			List<Map<String, Object>> dataList = rabbitmqPhysicalChannelDao.selectAll(m);
			sb.append(BaseJson.listToJson(dataList));

		} catch (Exception e) {
			getMyLog(e, log);
		}
		
		return sb.toString();
	}
	
	/**
	 * @功能描述：	获取满足条件的第一条物理通道对应的MQ通道信息记录
	 *
	 * @param m
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年2月5日
	 */
	public Map<String, Object> rabbitmqPhysicalChannelListAll(Map<String, Object> map) {
		Map<String, Object> dataMap = null;
		try {
			List<Map<String, Object>> dataList = rabbitmqPhysicalChannelDao.selectAll(map);
			if (dataList.size() > 0) {
				dataMap = dataList.get(0);
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		
		return dataMap;
	}
	
	/**
	 * @功能描述：	添加一条运营商对应的MQ通道信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String rabbitmqPhysicalChannelAdd(HttpServletRequest req){
		boolean flag = false;
		try {
			// 获取参数
			Map<String, Object> myData = getMaps(req);

			// 获取当前用户信息
			Map<String, Object> userInfoMap = getUser();
			myData.put("id", UUID.randomUUID().toString().replace("-", ""));
			myData.put("createUser", userInfoMap.get("suId"));

			int rows = rabbitmqPhysicalChannelDao.insert(myData);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加物理通道对应的MQ通道成功" : "添加物理通道对应的MQ通道失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "redirect:/rabbitmqPhysicalChannelListPage";
	}
	
    /**
     * @功能描述：	跳转到运营商对应的MQ通道编辑页面
     *
     * @param id
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
    public String rabbitmqPhysicalChannelEditPage(String id){
		try{
			Map<String, Object> mpRabbitmqPhysicalChanne= rabbitmqPhysicalChannelDao.selectByPrimaryKey(id);
			Session session=getSession();
			session.setAttribute("rabbitmqPhysicalChannel", mpRabbitmqPhysicalChanne);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return "mp/rabbitmqPhysicalChannelEdit";
	}
	
	/**
	 * @功能描述：	根据id修改运营商对应的MQ通道信息
	 *
	 * @param req
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月29日
	 */
	public String rabbitmqPhysicalChannelEdit(HttpServletRequest req) {
		try {
			boolean flag = false;
			Map<String, Object> myData = getMaps(req);
			
			// 获取当前用户信息
			Map<String, Object> userInfoMap = getUser();
			myData.put("updateUser", userInfoMap.get("suId"));

			int rows = rabbitmqPhysicalChannelDao.updateByPrimaryKeySelective(myData);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改物理通道对应的MQ通道成功" : "修改物理通道对应的MQ通道失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "rabbitmqPhysicalChannelListPage";
	}
	
    /**
     * @功能描述：	删除一条运营商对应的MQ通道信息
     *
     * @param id
     * @return 
     *
     * @作者：zhangpj		@创建时间：2018年1月29日
     */
	public String rabbitmqPhysicalChannelDel(String id) {
		boolean flag = false;
		try {
			int rows = rabbitmqPhysicalChannelDao.deleteByPrimaryKey(id);
			if (rows == 1) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "物理通道对应的MQ通道删除成功" : "物理通道对应的MQ通道删除失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return "rabbitmqPhysicalChannelListPage";
	}
}
