package com.hyfd.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.OrderOtherDao;
import com.hyfd.dao.mp.QianmigonghuoCustomerDao;

/**
 * @功能描述：	暂不启用
 *
 * @作者：zhangpj		@创建时间：2018年1月16日
 */
@Component
public class QianMiGongHuoBillOrderCallback {
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	OrderOtherDao orderOtherDao;
	@Autowired
	QianmigonghuoCustomerDao qianmigonghuoCustomerDao;
	
//	@Scheduled(fixedDelay = 60000) 暂不启用
	public void orderCallbackExecute() {
		// 查询充值已经成功或者失败,回调推送没有成功,回调次数小于等于10次,且充值状态改变已经超过5分钟的订单
		Map<String ,Object> param = new HashMap<String,Object>();
		param.put("ptId", "qm");
		List<Map<String, Object>> qianmigonghuoOrderList = orderDao.selectGongHuoPingTaiOrder(param);

		for(Map<String, Object> orderMap : qianmigonghuoOrderList){
			qianMiGongHuoCallBack(orderMap);
		}
	}
	
	public void qianMiGongHuoCallBack(Map<String, Object> orderParam){
		// 1.从回调信息中获取 话费订单状态主键、客户订单编号和充值结果
		String agentorderid = orderParam.get("agentorderid").toString();	// 客户订单编号
		String ordertype = orderParam.get("bill_type").toString();	// 充值结果
		String status = orderParam.get("status").toString();	// 充值结果

		// 2.根据订单编号和充值类型获取从千米供货平台抓取到的正在充值的订单信息
		Map<String, Object> orderOtherParam = new HashMap<String, Object>();
		orderOtherParam.put("agentorderid", agentorderid);
		orderOtherParam.put("ordertype", ordertype);	//1:流量 2:话费
		List<Map<String, Object>> list = orderOtherDao.selectAll(orderOtherParam);
		Map<String, Object> orderOtherMap = list.get(0);
		
		// 3.获取从千米供货抓取过来的原订单的信息
		String orderinfo = orderOtherMap.get("orderinfo").toString();
		JSONObject ob = JSONObject.parseObject(orderinfo);
		String orderid = ob.getString("order_id");
		String id = ob.getString("id");
		
		// 4.获取通道配置id(channel_template.xml中channelInterface的id)
		String ordersource = orderOtherMap.get("ordersource").toString();
		String templateId = ordersource.split("_")[1];
		
		// 5.获取千米供货平台的配置信息
		Map<String, Object> customerMap = qianmigonghuoCustomerDao.selectByPrimaryKey(templateId);
		String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数
		
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		String setOrdersUrl = paramMap.get("setOrdersUrl").toString();	// 回调千米供货平台地址
		String partner = paramMap.get("partner").toString();	// 合作商代码,定值,供货商提供
		String tplId = paramMap.get("tplId").toString();		// 模板编号,定值,供货商提供
		String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
		String version = paramMap.get("version").toString();	// 版本号
		String orderstate = "";
		
		// 6.设置订单状态 4：充值成功 5：冲值失败 6：可疑订单
		if ("3".equals(status)) {
			orderstate = "4";
		}else if ("4".equals(status)) {
			orderstate = "5";
		}else{
			orderstate = "6";
		}
		
		// 7.返回充值结果接口
		String result = QianMiGongHuoBillOrderTask.setOrders(setOrdersUrl, partner, tplId, apiKey, orderid, id, orderstate,version);
		if ("true".equals(result)) {
//			orderStatus.set("callbackstate", "1").update();		// 设置订单回调成功
			orderParam.put("callbackStatus", "1");
			orderDao.updateByPrimaryKeySelective(orderParam);
		}
	}
}
