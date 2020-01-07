package com.hyfd.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.OrderOtherDao;
import com.hyfd.dao.mp.JuhegonghuoCustomerDao;

/**
 * @功能描述：	暂不启用
 *
 * @作者：zhangpj		@创建时间：2018年1月16日
 */
@Component
public class JuHeBillOrderCallback {
	
	private static Logger log = LogManager.getLogger(JuHeBillOrderCallback.class);
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	OrderOtherDao orderOtherDao;
	@Autowired
	JuhegonghuoCustomerDao juhegonghuoCustomerDao;
	
	@Scheduled(fixedDelay = 60000) //暂不启用
	public void orderCallbackExecute() {
		// 查询充值已经成功或者失败,回调推送没有成功,回调次数小于等于10次,且充值状态改变已经超过5分钟的订单
		Map<String ,Object> param = new HashMap<String,Object>();
		param.put("ptId", "jh");
		List<Map<String, Object>> juhegonghuoOrderList = orderDao.selectGongHuoPingTaiOrder(param);

		for(Map<String, Object> orderMap : juhegonghuoOrderList){
			juHeGongHuoCallBack(orderMap);
		}
	}
	
	public void juHeGongHuoCallBack(Map<String, Object> orderParam){
		// 1.从回调信息中获取 话费订单状态主键、客户订单编号和充值结果
		String agentorderid = orderParam.get("agent_order_id").toString();	// 代理商订单编号
		String outOrderNo = orderParam.get("order_id").toString();	// 平台订单编号
		String ordertype = orderParam.get("bill_type").toString();	// 充值类型
		String status = orderParam.get("status").toString();	// 充值结果

		// 2.根据订单编号和充值类型获取从聚合供货平台抓取到的正在充值的订单信息
		Map<String, Object> orderOtherParam = new HashMap<String, Object>();
		orderOtherParam.put("agentorderid", agentorderid);
		orderOtherParam.put("ordertype", ordertype);	//1:流量 2:话费
		List<Map<String, Object>> list = orderOtherDao.selectAll(orderOtherParam);
		if(list.size() > 0){
			Map<String, Object> orderOtherMap = list.get(0);
			
			// 3.获取从聚合供货抓取过来的原订单的信息
			String orderinfo = orderOtherMap.get("orderinfo").toString();
			JSONObject ob = JSONObject.parseObject(orderinfo);
			String orderId = ob.getString("tradeId");
			
			// 4.获取通道配置id(channel_template.xml中channelInterface的id)
			String ordersource = orderOtherMap.get("ordersource").toString();
			String templateId = ordersource.split("_")[1];
			
			// 5.获取聚合供货平台的配置信息
			Map<String, Object> customerMap = juhegonghuoCustomerDao.selectByPrimaryKey(templateId);
			String defaultParameter = customerMap.get("default_parameter").toString();	//默认参数
			
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			String updateOrderStatusUrl = paramMap.get("updateOrderStatusUrl").toString();	// 回调聚合供货平台地址
			String supplierId = paramMap.get("supplierId").toString();	// 模板编号,定值,供货商提供
			String apiKey = paramMap.get("apiKey").toString();	// 定值,供货商提供,供货网 –> 服务 –> 接口 –>接口供货下的接口密钥值
			String orderState = "";
//			orderid = orderid.substring(orderid.length() - 19);	// 聚合平台能接收的上游平台id最长为19位
			String sign = DigestUtils.md5Hex(supplierId + orderId + apiKey);
			
			// 6.设置订单状态 2：充值成功 3：冲值失败
			if ("3".equals(status)) {
				orderState = "2";
			}else if ("4".equals(status)) {
				orderState = "3";
			}
			
			if (orderState.equals("2") || orderState.equals("3")) {
				// 7.返回充值结果接口
				String result = updateOrderStatus(updateOrderStatusUrl, supplierId, orderId, orderState, sign, outOrderNo);
				if (null != result) {
					JSONObject jsonObj = JSONObject.parseObject(result);
					String success = jsonObj.getString("success");
					if ("true".equals(success)) {
						// 如果回调成功,则删除掉这条临时信息
						orderOtherDao.deleteByPrimaryKey(orderOtherMap.get("id").toString());
						orderParam.put("callbackStatus", "1");
						orderDao.updateByPrimaryKeySelective(orderParam);
					}else{
						log.info("回调聚合供货平台订单状态失败,返回信息["+result+"]");
					}
				}
			}
		}
	}
	
	/**
	 * @功能描述：	回调充值结果给聚合平台
	 *
	 * @作者：zhangpj		@创建时间：2018年7月3日
	 * @param url 充值结果回调地址
	 * @param supplierId 模板编号,定值,供货商提供
	 * @param orderId 聚合平台订单编号
	 * @param orderState 订单状态
	 * @param sign 密钥
	 * @param outOrderNo bosspaybill订单编号
	 * @return
	 */
	public static String updateOrderStatus(String url, String supplierId, String orderId ,String orderState, String sign, String outOrderNo){
		StringBuffer sbf = new StringBuffer();
		sbf.append(url);
		sbf.append("?supplierId=" + supplierId);
		sbf.append("&orderId=" + orderId);
		sbf.append("&orderState=" + orderState);
		sbf.append("&outOrderNo=" + outOrderNo);
		sbf.append("&sign=" + sign);
		
		String retrunStr = "";
		try {
			retrunStr = ToolHttp.post(false, sbf.toString(), null, null);
		} catch (Exception e) {
			log.error("回调充值结果给聚合平台发生异常["+sbf.toString()+"]");
			e.printStackTrace();
		}
		return retrunStr;
	}
}
