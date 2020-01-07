package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.OrderOtherDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class OrderOtherSer extends BaseService {
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	OrderOtherDao orderOtherDao;
	
	/**
	 * @功能描述 根据条件获取全类型(话费和流量)订单是否存在
	 * 
	 * @param oldCustomerOrderId 客户平台的订单编号
	 * @param orderType 充值类型(1:流量,2:话费)
	 * @param orderSource 订单来源
	 * 
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月15日
	 */
	public Map<String, Object> getOrderOther(String oldAgentOrderId,String orderType,String orderSource) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("oldagentorderid", oldAgentOrderId);
		param.put("ordertype", orderType);
		param.put("ordersource", orderSource);
		param.put("starttime", DateTimeUtils.addDays(-7));	//查询3天以内的订单数据
		
		Map<String, Object> dataMap = null;
		List<Map<String, Object>> list = orderOtherDao.selectAll(param);
		if (list.size() > 0) {
			dataMap = list.get(0);
		}
		return dataMap;
	}
	
	/**
	 * @功能描述：	保存一条全类型(话费和流量)订单
	 *
	 * @param map
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月15日
	 */
	public int saveOrderOther(Map<String, Object> map){
		return orderOtherDao.insert(map);
	}
}
