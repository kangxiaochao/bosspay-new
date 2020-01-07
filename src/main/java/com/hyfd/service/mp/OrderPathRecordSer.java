package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.OrderPathRecordDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;


@Service
public class OrderPathRecordSer extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	OrderPathRecordDao orderPathRecordDao;
	@Autowired
	AgentDao agentDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;
	@Autowired
	ProviderDao providerDao;
	@Autowired
	BillPkgDao billPkgDao;
	@Autowired
	ProviderBillDispatcherDao providerBillDispatcherDao;
	
	public int getOrderCount(Map<String, Object> m){
		int orderCount=0;
		try{
			orderCount=orderPathRecordDao.selectCount(m);
		}catch(Exception e){
			getMyLog(e,log);
		}
		return orderCount;
	}
	
	public String orderAllList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Map<String, Object> userInfoMap = getUser();
			String userid = userInfoMap.get("suId") + "";

			String agentName = (String) m.get("agentName");
			String dispatcherProviderName = (String) m.get("dispatcherProviderName");
			String providerName = (String) m.get("providerName");

			// 根据channel_person查询agent表,若不为null则代表用户为渠道
			List<Map<String, Object>> agentList = agentDao.selectByChannelPerson(userid);
			if (null != agentList && agentList.size() > 0) {
				m.put("channelPerson", userid);
			}

			if (agentName != null && agentName != "") {
				Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
				if (agent != null) {
					String agentId = (String) agent.get("id");
					m.put("agentId", agentId);
				}
			}
			if (dispatcherProviderName != null && dispatcherProviderName != "") {
				String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
				if (dispatcherProviderId != null && dispatcherProviderId != "") {
					m.put("dispatcherProviderId", dispatcherProviderId);
				}
			}
			if (providerName != null && providerName != "") {
				String providerId = providerDao.getIdByName(providerName);
				if (providerId != null && providerId != "") {
					m.put("providerId", providerId);
				}
			}
			m.remove("agentName");
			m.remove("dispatcherProviderName");
			m.remove("providerName");

			Page p = getPage(m);// 提取分页参数
			int total = getOrderCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();
			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = orderPathRecordDao.selectAll(m);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return sb.toString();
	}
	
	/**
	 * @功能描述：	跳转到订单详细参数页面
	 *
	 * @作者：zhangpj		@创建时间：2017年4月26日
	 * @param req
	 */
	public void orderParamPage(HttpServletRequest req){
		Map<String, Object> orderParam = selectOrderParam(req);
		req.setAttribute("orderParam", orderParam);
		req.setAttribute("orderType", "3");
	}
	
	
	/**
	 * @功能描述：	获取订单详细参数信息
	 *
	 * @作者：zhangpj		@创建时间：2017年4月26日
	 * @param req
	 * @return
	 */
	public String getOrderParam(HttpServletRequest req){
		Map<String, Object> orderParam = selectOrderParam(req);
        return JSONObject.toJSONString(orderParam);
	}
	
	/**
	 * @功能描述：	查询订单详细参数信息
	 *
	 * @作者：zhangpj		@创建时间：2017年4月26日
	 * @param req
	 * @return
	 */
	public Map<String, Object> selectOrderParam(HttpServletRequest req){
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		String phone = String.valueOf(m.get("phone"));
		String agentOrderId = String.valueOf(m.get("agentOrderId"));
		
		Map<String, Object> orderParam = null;
		if ((null != phone && !phone.equals("")) || (null != agentOrderId && !agentOrderId.equals(""))) {
			// 根据订单编号或者电话号码获取最新一条的订单信息
			Map<String, String> orderMap = orderPathRecordDao.selectFastNewOrderPathRecordByPhone(m);
			if (null != orderMap) {
				agentOrderId = orderMap.get("order_id");
				m.put("orderId", agentOrderId);
				// 1.获取订单参数
				orderParam = orderPathRecordDao.selectOrderPathRecordParam(m);
				
				// 2.获取可用流量包
				Map<String, String> param = new HashMap<String, String>();
				param.put("agentId", orderParam.get("agentId").toString());
				param.put("providerId", orderParam.get("providerId").toString());
				param.put("billType", orderParam.get("billType").toString());
				param.put("provinceCode", orderParam.get("provinceCode").toString());
				String billType = orderParam.get("billType").toString();
				if ("2".equals(billType)) {
					// 流量
					param.put("value", orderParam.get("value").toString());
				}
				
				Map<String, Object> pkg = billPkgDao.selectBillPkgForAgent(param);
				if (pkg == null)
				{
					param.put("provinceCode", "全国");
					pkg = billPkgDao.selectBillPkgForAgent(param);
				}
				
				// 3.获取可用通道列表
				Map<String, Object> dispatcherParam = new HashMap<String, Object>();
				dispatcherParam.put("pkgId", orderParam.get("pkgId"));
				dispatcherParam.put("providerId", orderParam.get("providerId"));
				dispatcherParam.put("provinceCode", orderParam.get("provinceCode"));
				List<Map<String, Object>> dpList = providerBillDispatcherDao.selectProviderPhysicalChannel(dispatcherParam);
				
				orderParam.put("result", "success");
				orderParam.put("pkg", pkg);
				orderParam.put("dispatcherParam", dpList);
			}
		}
		
		return orderParam;
	}
}
