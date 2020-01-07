package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.dao.mp.multipleReportDao;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class multipleReportSer extends BaseService{

	private static final String Bill_TYPE = "话费";
	private static final String Flow_TYPE = "流量";
	private static final String Bill_Table_NAME = "mp_order_bill_status";
	private static final String Flow_Table_NAME = "mp_order_status";
	
	@Autowired
	multipleReportDao mrDao;
	
	public void flowDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = getMaps(request);
		
	}
	
	public String ExportFlowDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		Map<String,Object> map = new HashMap<String,Object>();
		List<Map<String,Object>> channelDataList = mrDao.selectChannelData(param);
		sortList(channelDataList,"count",true);//按数量排名
		for(int i = 0 ; i < channelDataList.size() ; i++){
			channelDataList.get(i).put("countNum", i+1);
		}
		sortList(channelDataList,"fee",true);//按原价排名
		for(int i = 0 ; i < channelDataList.size() ; i++){
			channelDataList.get(i).put("feeNum", i+1);
		}
		sortList(channelDataList,"profit",true);
		for(int i = 0 ; i < channelDataList.size() ; i++){
			channelDataList.get(i).put("profitNum", i+1);
		}
		List<Map<String,Object>> agentDataList = mrDao.selectAgentData(param);
		sortList(agentDataList,"count",true);
		for(int i = 0 ; i < agentDataList.size() ; i++){
			agentDataList.get(i).put("countNum", i+1);
		}
		sortList(agentDataList,"fee",true);
		for(int i = 0 ; i < agentDataList.size() ; i++){
			agentDataList.get(i).put("feeNum", i+1);
		}
		sortList(agentDataList,"profit",true);
		for(int i = 0 ; i < agentDataList.size() ; i++){
			agentDataList.get(i).put("profitNum", i+1);
		}
		map.put("channelList", channelDataList);
		map.put("agentList", agentDataList);
		
		return JSONObject.toJSONString(channelDataList);
	}
	
	/**
	 * 导出流量报表
	 * @param request
	 * @param response
	 */
	public void exportFlowDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<String> brandList = mrDao.selectBrand(new HashMap<String,Object>().put("type", Flow_TYPE));
		double sumDisprice = mrDao.selectSumDisprice(param);
		double sumMydisprice = mrDao.selectSumMydisprice(param);
		for(int i = 0 , size = brandList.size(); i < size ; i++ ){
			String brand = brandList.get(i);
			param.put("brand", brand);
			param.put("type", "流量");
			Map<String,Object> map = mrDao.selectChannelDailyDate(param);
			map.put("brand", brand);
			double disprice = (double) map.get("disprice");
			double mydisprice = (double) map.get("mydisprice");
			map.put("profit", new Double(String .format("%.3f",disprice - mydisprice)));
			NumberFormat numberFormat = NumberFormat.getInstance();   
	        numberFormat.setMaximumFractionDigits(2);   
			map.put("proportion",numberFormat.format(mydisprice/sumMydisprice*100)+"%");
			if(Integer.parseInt(map.get("count")+"")!=0){
				list.add(map);
			}
		}
		//获取排名
		sortList(list,"profit",true);
		for(int i = 0 ; i < list.size() ; i++){
			list.get(i).put("sortnum", i+1);
		}
		sortList(list,"groupname",false);
		//查询下家数据
		List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
		List<String> nameList = mrDao.selectFlowGroupName(param);
		for(int i = 0 , size = nameList.size() ; i < size ; i++){
			String groupName = nameList.get(i);
			Map<String,Object> map = mrDao.selectDailyData(param);
			map.put("name", groupName);
			double disprice = (double) map.get("disprice");
			double mydisprice = (double) map.get("mydisprice");
			map.put("profit", new Double(String .format("%.3f",disprice - mydisprice)));
			NumberFormat numberFormat = NumberFormat.getInstance();   
	        numberFormat.setMaximumFractionDigits(2);   
			map.put("proportion",numberFormat.format(disprice/sumDisprice*100)+"%");
			if(Integer.parseInt(map.get("count")+"")!=0){
				list1.add(map);
			}
		}
		//获取排名
		sortList(list1,"profit",true);
		for(int i = 0 ; i < list1.size() ; i++){
			list1.get(i).put("sortnum", i+1);
		}
		sortList(list1,"groupname",false);
		//定义导出的参数
		String filename = "flowDailyReport";
		String title = startDate.replaceAll("-", ".") + "---" + endDate.replaceAll("-", ".");
		String time = DateUtils.getNowTime("yyyy.MM.dd");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("title", title);
		map.put("time", time);
		map.put("list", list);
		map.put("list1", list1);
		exportExcel("tempDailyFlow",filename,map,request,response);
	}
	
	/**
	 * 获取通道日报表
	 * @author lks 2017年10月23日上午11:27:08
	 * @param request
	 * @param response
	 * @return
	 */
	public String selectProviderDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			int total = mrDao.countProviderDailyReport(param);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList = mrDao.selectProviderDailyReport(param);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("通道每日数据查询方法出错"+e.getMessage());
		}
		
		return sb.toString();
	}
	
	/**
	 * 导出通道日数据报表
	 * @author lks 2017年10月24日下午3:51:03
	 * @param request
	 * @param response
	 */
	public void exportProviderDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		List<Map<String,Object>> list = mrDao.selectProviderDailyReport(param);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		exportExcel("providerDRTemp","providerDailyReport",map,request,response);
	}
	
	/**
	 * 导出代理商日数据报表
	 * @author lks 2017年10月24日下午3:51:03
	 * @param request
	 * @param response
	 */
	public void exportAgentDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		List<Map<String,Object>> list = mrDao.selectAgentDailyReport(param);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		exportExcel("agentDRTemp","agentDailyReport",map,request,response);
	}
	
	/**
	 * 获取代理商日报表
	 * @author lks 2017年10月23日上午11:27:08
	 * @param request
	 * @param response
	 * @return
	 */
	public String selectAgentDailyReport(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> param = getMaps(request);
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			int total = mrDao.countAgentDailyReport(param);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList = mrDao.selectAgentDailyReport(param);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("通道每日数据查询方法出错"+e.getMessage());
		}
		
		return sb.toString();
	}
	
	/**
	 * 用流导出excel
	 * @param filename
	 * @param list
	 * @param request
	 * @param response
	 */
	public void exportExcel(String tempName,String filename,Map<String,Object> map,HttpServletRequest request,HttpServletResponse response){
		try{
			String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"+ File.separator+tempName+".xlsx";
			XLSTransformer former = new XLSTransformer();
	        FileInputStream in = new FileInputStream(templatePath);
	        response.setContentType("application/x-excel;charset=utf-8");
	        response.setHeader("Content-Disposition", "attachment;filename="+filename+"_"+System.currentTimeMillis()+".xlsx");
	        response.setCharacterEncoding("utf-8");
	        XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
	        OutputStream os = response.getOutputStream();
	        workbook.write(os);
	        os.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	/**
	 * list排序
	 * @param list
	 * @param sortParam
	 */
	public void sortList(List<Map<String,Object>> list , String sortParam ,boolean isDesc){
		Collections.sort(list, new Comparator<Object>(){
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Object o1, Object o2) {
				Map<String,Object> map1 = (Map<String, Object>) o1;
				Map<String,Object> map2 = (Map<String, Object>) o2;
				if(isDesc){
					return (map2.get(sortParam)+"").compareTo(map1.get(sortParam)+"");
				}else{
					return (map1.get(sortParam)+"").compareTo(map2.get(sortParam)+"");
				}
			}
		});
	}
	
}
