package com.hyfd.task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.TutubiOrderDao;

//@Component
public class TuTuBiOrderTask {

	private static Logger log = Logger.getLogger(TuTuBiOrderTask.class);
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	CookiesDao cookiesDao;
	@Autowired
	TutubiOrderDao tutubiOrderDao;
	
	private static String queryUrl = "http://10040.snail.com/platform/web/agent/order/transfer";
	
//	@Scheduled(fixedDelay = 3000000)
	public void queryOrder(){
		Map<String, Object> cookies = cookiesDao.selectFirstCookie();
		if(cookies != null){
			String cookie = (String) cookies.get("cookies");
			log.error("兔兔币订单查询获取的cookie为"+ cookie +"，兔兔币订单查询开始");
			queryOrder(100,cookie);
		}else{
			log.error("兔兔币订单查询获取的cookie为空");
		}
	}
	
	
	/**
	 * 多线程查询订单的方法
	 * @author lks 2017年12月27日上午9:51:30
	 * @param num
	 * @param account
	 * @param password
	 * @throws IOException
	 */
	public void queryOrder(int num, String cookie){
		final List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		for (int i = 1; i <= num; i++) {
			final int index = i;
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try{
						HttpClient httpClient = new HttpClient();
						String url = queryUrl + "?currentPage="+index+"&pageCount=10&paginationId=#record-pagination_phone&number=10&table_data=table_data&startTime=2015-01-01&endTime=2015-01-31&_="+System.currentTimeMillis();
						GetMethod queryMethod = new GetMethod(url);
						queryMethod.setRequestHeader("cookie",cookie);
						httpClient.executeMethod(queryMethod);
						String result = queryMethod.getResponseBodyAsString();
						JSONObject json = JSONObject.parseObject(result);
						JSONObject value = json.getJSONObject("value");
						JSONArray array = value.getJSONArray("list");
						for(int x = 0 ; x < array.size() ; x++){
							JSONObject order = array.getJSONObject(x);
							Map<String,Object> orderMap = JSON.parseObject(order.toJSONString(), Map.class);
							list.add(orderMap);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				for(Map<String,Object> map : list){
					map.put("rechargeTime", map.get("createTime"));
					map.put("createTime", new Date(System.currentTimeMillis()));
					Map<String,Object> order = tutubiOrderDao.selectByOrderId(map);
					if(order == null){
						tutubiOrderDao.insertSelective(map);
					}
				}
				break;
			}
		}
	}
	
}
