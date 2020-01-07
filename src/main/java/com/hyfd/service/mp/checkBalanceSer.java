package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderChargeRecordDao;
import com.hyfd.dao.mp.checkBalanceDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class checkBalanceSer extends BaseService{

	@Autowired
	checkBalanceDao cbDao;
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	ProviderChargeRecordDao providerChargeRecordDao;
	
	public void checkBalance(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		String startDate = param.get("startDate")+"";
		String endDate = param.get("endDate")+"";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		//获取当前时间段跑量的代理商ID
		List<String> agentIdList = cbDao.selectAgentId(param);
		for(String agentId : agentIdList){
//			Map<String,Object> balanceMap = new HashMap<String,Object>();
//			balanceMap.put("agentId", agentId);
//			balanceMap.put("date", startDate);
//			double startBalance = cbDao.selectAgentBalance(balanceMap);
//			balanceMap.put("date", endDate);
//			double endBalance = cbDao.selectAgentBalance(balanceMap);
//			param.put("agentId", agentId);
//			double addmoney = cbDao.selectAgentAddMoney(param);
//			double adjustment = cbDao.selectAdjustment(param);
//			Map<String,Object> consumeMap = cbDao.selectConsume(param);
//			double consume = Double.parseDouble(consumeMap.get("consume")+"");
//			double actualDeductions = cbDao.selectActualDeductions(param);
//			double agentDiff = cbDao.selectAgentDiff(param);
//			consumeMap.put("agentdiff", agentDiff);
//			consumeMap.put("diff", String.format("%.2f",(consume + actualDeductions - agentDiff)));
//			consumeMap.put("actualDeductions", actualDeductions);
//			consumeMap.put("startbalance", startBalance);
//			consumeMap.put("endbalance", endBalance);
//			consumeMap.put("addmoney", addmoney);
//			consumeMap.put("adjustment", adjustment);
//			list.add(consumeMap);
			list.add(getConsumeMap(param,agentId,startDate,endDate));
		}
		List<Map<String,Object>> mdList = cbDao.selectMultipleDeductions(param);
		double sumMd = 0.0;
		for(Map<String,Object> md : mdList){
			sumMd += Double.parseDouble(md.get("fee")+"");
			String orderId = md.get("order_id")+"";
			String agentId = md.get("agent_id")+"";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("orderId", orderId);
			map.put("agentId", agentId);
			double sumFee = cbDao.selectChargeFee(map);
			if(sumFee==0.0){
				md.put("result", "是");
			}else{
				md.put("result", "否");
			}
		}
		List<Map<String,Object>> diffList = cbDao.selectDiff(param);
		double sumDiff = 0.0;
		for(Map<String,Object> diff : diffList){
			sumDiff += Double.parseDouble(diff.get("diff")+"");
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("title", startDate+"——"+endDate);
		map.put("time", DateUtil.formatDate(new Date(), "yyyy.MM.dd"));
		map.put("list", list);
		map.put("mdList", mdList);
		map.put("diffList", diffList);
		map.put("sumMd", sumMd);
		map.put("sumDiff", String.format("%.2f", sumDiff));
		exportExcel("balanceTemp","checkBalance"+System.currentTimeMillis(),map,request,response);
	}
	
	/**
	 * 导出excel文件
	 * @author lks 2017年7月5日上午10:21:46
	 * @param tempName
	 * @param fileName
	 * @param map
	 * @param request
	 * @param response
	 */
	public static void exportExcel(String tempName,String fileName,Map<String,Object> map,HttpServletRequest request,HttpServletResponse response){
		String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"+ File.separator +tempName+".xlsx";
        XLSTransformer former = new XLSTransformer();
        try
        {
            response.setContentType("application/x-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=" + fileName + ".xlsx");
            response.setCharacterEncoding("utf-8");
            FileInputStream in = new FileInputStream(templatePath);
            XSSFWorkbook workbook = (XSSFWorkbook)(former.transformXLS(in, map));
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	public String checkBalanceList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		String startDate = param.get("startDate")+"";
		String endDate = param.get("endDate")+"";
		//Map<String,Object> user = getUser();
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			List<String> agentIdList = cbDao.selectAgentId(param);
			int total = agentIdList.size();
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			//获取当前时间段跑量的代理商ID
			for(String agentId : agentIdList){
				list.add(getConsumeMap(param,agentId,startDate,endDate));
			}
//			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
//			for(String agentId : agentIdList){
//				fixedThreadPool.execute(new Runnable() {
//					public void run() {
//						list.add(getConsumeMap(param,agentId,startDate,endDate));
//					}
//				});
//			}
//			fixedThreadPool.shutdown();
//			while (true) {
//				if (fixedThreadPool.isTerminated()) {
//					String billListJson=BaseJson.listToJson(list);
//					sb.append(billListJson);
//					break;
//				}
//			}
			String billListJson=BaseJson.listToJson(list);
			sb.append(billListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("订单提交记录列表查询方法出错"+e.getMessage());
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public Map<String,Object> getConsumeMap1(Map<String,Object> param, String agentId ,String startDate, String endDate){
		Map<String,Object> balanceMap = new HashMap<String,Object>();
		balanceMap.put("agentId", agentId);
		balanceMap.put("date", startDate);
		double startBalance = cbDao.selectAgentBalance(balanceMap);
		balanceMap.put("date", endDate);
		double endBalance = cbDao.selectAgentBalance(balanceMap);
		param.put("agentId", agentId);
		double addmoney = cbDao.selectAgentAddMoney(param);
		double adjustment = cbDao.selectAdjustment(param);
		Map<String,Object> consumeMap = cbDao.selectConsume(param);
		double consume = Double.parseDouble(consumeMap.get("consume")+"");
		double actualDeductions = cbDao.selectActualDeductions(param);
		double agentDiff = cbDao.selectAgentDiff(param);
		consumeMap.put("agentdiff", agentDiff);
		consumeMap.put("diff", String.format("%.2f",(consume + actualDeductions - agentDiff)));
		consumeMap.put("actualDeductions", actualDeductions);
		consumeMap.put("startbalance", startBalance);
		consumeMap.put("endbalance", endBalance);
		consumeMap.put("addmoney", addmoney);
		consumeMap.put("adjustment", adjustment);
		return consumeMap;
	}
	
	public Map<String,Object> getConsumeMap(Map<String,Object> param, String agentId ,String startDate, String endDate){
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> sBalanceMap = new HashMap<String,Object>();
		sBalanceMap.put("agentId", agentId);
		sBalanceMap.put("date", startDate);
		Map<String,Object> eBalanceMap = new HashMap<String,Object>();
		eBalanceMap.put("agentId", agentId);
		eBalanceMap.put("date", endDate);
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
		//期初余额
		Future<Double> startBalanceFuture = fixedThreadPool.submit(()->cbDao.selectAgentBalance(sBalanceMap));
		//期末余额
		Future<Double> endBalanceFuture = fixedThreadPool.submit(()->cbDao.selectAgentBalance(eBalanceMap));
		param.put("agentId", agentId);
		//加款额
		Future<Double> addmoneyFuture = fixedThreadPool.submit(()->cbDao.selectAgentAddMoney(param));
		//调账金额
		Future<Double> adjustmentFuture = fixedThreadPool.submit(()->cbDao.selectAdjustment(param));
		//获取消耗
		Future<Map<String,Object>> consumeMapFuture = fixedThreadPool.submit(()->cbDao.selectConsume(param));
		//实际扣款
		Future<Double> actualDeductionsFuture = fixedThreadPool.submit(()->cbDao.selectActualDeductions(param));
		//代理商差额
		Future<Double> agentDiffFuture = fixedThreadPool.submit(()->cbDao.selectAgentDiff(param));
		
		try{
			map = consumeMapFuture.get();
			double consume = Double.parseDouble(map.get("consume")+"");
			double startBalance = startBalanceFuture.get();
			double endBalance = endBalanceFuture.get();
			double addmoney = addmoneyFuture.get();
			double adjustment = adjustmentFuture.get();
			double actualDeductions = actualDeductionsFuture.get();
			double agentDiff = agentDiffFuture.get();
			
			map.put("agentdiff", agentDiff);
			map.put("diff", String.format("%.2f",(consume + actualDeductions - agentDiff)));
			map.put("actualDeductions", actualDeductions);
			map.put("startbalance", startBalance);
			map.put("endbalance", endBalance);
			map.put("addmoney", addmoney);
			map.put("adjustment", adjustment);
		}catch(Exception e){
			log.error("获取余额核查信息出错"+e.getMessage());
			e.printStackTrace();
		}
		
		return map;
	}
	
	/**
	 * 余额核查列表数据
	 * @author lks 2018年4月8日上午9:45:29
	 * @param request
	 * @param response
	 * @return
	 */
	public String checkBalanceList1(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		String startDate = param.get("startDate")+"";
		String endDate = param.get("endDate")+"";
		//Map<String,Object> user = getUser();
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			List<String> agentIdList = cbDao.selectAgentId(param);
			int total = cbDao.selectAgentIdCount(param);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			//获取当前时间段跑量的代理商ID
			for(String agentId : agentIdList){
				Map<String,Object> balanceMap = new HashMap<String,Object>();
				balanceMap.put("agentId", agentId);
				balanceMap.put("date", startDate);
				double startBalance = cbDao.selectAgentBalance(balanceMap);
				balanceMap.put("date", endDate);
				double endBalance = cbDao.selectAgentBalance(balanceMap);
				param.put("agentId", agentId);
				double addmoney = cbDao.selectAgentAddMoney(param);
				double adjustment = cbDao.selectAdjustment(param);
				Map<String,Object> consumeMap = cbDao.selectConsume(param);
				double consume = Double.parseDouble(consumeMap.get("consume")+"");
				double actualDeductions = cbDao.selectActualDeductions(param);
				double agentDiff = cbDao.selectAgentDiff(param);
				consumeMap.put("agentdiff", agentDiff);
				consumeMap.put("diff", String.format("%.2f",(consume + actualDeductions - agentDiff)));
				consumeMap.put("actualDeductions", actualDeductions);
				consumeMap.put("startbalance", startBalance);
				consumeMap.put("endbalance", endBalance);
				consumeMap.put("addmoney", addmoney);
				consumeMap.put("adjustment", adjustment);
				list.add(consumeMap);
			}
			String billListJson=BaseJson.listToJson(list);
			sb.append(billListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("订单提交记录列表查询方法出错"+e.getMessage());
		}
		
		return sb.toString();
	}
	
	public String additionalEstimateList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		
		// 获取指定时间段内有充值成功订单的通道id
		List<Map<String, Object>> providerList = orderDao.selectDispatcherProvider(param);
		int providerListSize = providerList.size();
		
		Page page=getPage(param);//提取分页参数
		int beginNum=page.getBeginNum(); // 开始记录数
		int endNum=page.getEndNum(); // 结束记录数
		page.setCount(providerListSize);
		
		// 设置循环数量
		if (endNum > providerListSize) {
			endNum = providerListSize;
		}
		
		// 获取服务器cpu数量
		int processorsCount = Runtime.getRuntime().availableProcessors();
		ExecutorService fixedThreadPoolService = Executors.newFixedThreadPool(processorsCount * 4);
		
		List<Future<Map<String, Object>>> futureList = new ArrayList<Future<Map<String, Object>>>();
		for (int i = beginNum; i < endNum; i++) {
			Map<String,Object> paramMap = new HashMap<String, Object>();
			paramMap.putAll(param);
			paramMap.put("providerId", providerList.get(i).get("dispatcher_provider_id"));
			// 获取指定通道指定时间段内消费总额和余额
			Future<Map<String, Object>> future = fixedThreadPoolService.submit(() -> providerChargeRecordDao.selectDispatcherProviderStatisticsInfo(paramMap));
			futureList.add(future);
		}
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			for (Future<Map<String, Object>> future : futureList) {
				Map<String, Object> userList = future.get();
				resultList.add(userList);
			}
		} catch (InterruptedException | ExecutionException e) {
			for (Future<Map<String, Object>> future : futureList) {
				future.cancel(true);
			}
			e.printStackTrace();
		} finally {
			fixedThreadPoolService.shutdown();
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page.getCurrentPage());
		map.put("total", page.getNumCount());
		map.put("records", providerListSize);
		map.put("rows", resultList);
		System.out.println(JSONObject.toJSONString(map));
		return JSONObject.toJSONString(map);
	}
}

