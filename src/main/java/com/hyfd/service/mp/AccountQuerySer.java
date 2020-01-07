package com.hyfd.service.mp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.service.accountQuery.MSQueryAccountSer;

import jxl.common.Logger;

@Service
public class AccountQuerySer {
	
	@Autowired
	MSQueryAccountSer msSer;
	
	Logger log = Logger.getLogger(getClass());

	/**
	 * 查询上家账户余额
	 * @param request
	 * @param response
	 * @return
	 */
	public String queryAccounts(HttpServletRequest request, HttpServletResponse response){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		Future<String> msFuture = fixedThreadPool.submit(()->msSer.query());
		try{
			String msBalance = msFuture.get();
			Map<String,Object> msMap = new HashMap<String,Object>();
			msMap.put("name", "民生");
			msMap.put("balance", msBalance);
		}catch(Exception e){
			log.error("上家账户余额查询出错"+e.getMessage());
			msFuture.cancel(true);
		}
		return "";
	}
	
}
