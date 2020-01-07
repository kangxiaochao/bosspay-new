package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.dao.mp.QueryOrderInfoDao;
import com.hyfd.service.BaseService;

@Service
public class QueryOrderInfoSer extends BaseService{

	@Autowired
	QueryOrderInfoDao queryOrderInfoDao;
	
	Logger log = Logger.getLogger(getClass());
	
	/**
	 * 查询订单信息
	 * @author lks 2018年3月23日下午2:12:00
	 * @param request
	 * @param response
	 * @return
	 */
	public String queryOrderInfo(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> param = getMaps(request);
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		Future<List<Map<String,Object>>> submitOrderFuture = fixedThreadPool.submit(()->queryOrderInfoDao.selectSubmitOrderInfo(param));
		Future<List<Map<String,Object>>> orderFuture = fixedThreadPool.submit(()->queryOrderInfoDao.selectOrderInfo(param));
		Future<List<Map<String,Object>>> eOrderFuture = fixedThreadPool.submit(()->queryOrderInfoDao.selectExceptionOrderInfo(param));
		Future<List<Map<String,Object>>> orderPRFuture = fixedThreadPool.submit(()->queryOrderInfoDao.selectOrderPathRecordInfo(param));
		Future<List<Map<String,Object>>> agentACFuture = fixedThreadPool.submit(()->queryOrderInfoDao.selectAgentAccountChargeInfo(param));
		try {
			List<Map<String,Object>> sOrderList = submitOrderFuture.get();
			List<Map<String,Object>> orderList = orderFuture.get();
			List<Map<String,Object>> eOrderList = eOrderFuture.get();
			List<Map<String,Object>> orderPRList = orderPRFuture.get();
			List<Map<String,Object>> agentACList = agentACFuture.get();
			
			map.put("submitOrder", sOrderList);
			map.put("order", orderList);
			map.put("exceptionOrder", eOrderList);
			map.put("orderPathRecord", orderPRList);
			map.put("agentAccountCharge", agentACList);
		} catch (Exception e) {
			log.error("订单综合查询功能出错"+e.getMessage());
			submitOrderFuture.cancel(true);
			orderFuture.cancel(true);
			eOrderFuture.cancel(true);
			orderPRFuture.cancel(true);
			agentACFuture.cancel(true);
			e.printStackTrace();
		}
		return JSONObject.toJSONString(map);
	}
	
}
