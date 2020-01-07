package com.hyfd.service.mp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.Echarts;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.dataDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.service.BaseService;

@Service
public class dataSer extends BaseService {

	@Autowired
	dataDao dDao;
	@Autowired
	SysUserRoleDao sysUserRoleDao;

	/**
	 * 首页统计利润数据
	 * 
	 * @author lks 2018年3月2日上午10:20:51
	 * @param request
	 * @param response
	 * @return
	 */
	public String countProfit(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> user = getUser();
		String suid = user.get("suId") + "";// 用户Id
		if (judgeAgentRole(suid)) {
			return "error";
		}
		List<Map<String, Object>> todayList = dDao.selectTodayProfit();
		List<Map<String, Object>> allList = dDao.selectAllProfit();
		JSONObject json = new JSONObject();
		json.put("today", getDataJson(todayList));
		json.put("all", getDataJson(allList));
		return json.toJSONString();
	}

	public JSONObject getDataJson(List<Map<String, Object>> list) {
		JSONObject json = new JSONObject();
		if (list != null && list.size() == 2) {
			Map<String, Object> succMap = list.get(0);
			Map<String, Object> failMap = list.get(1);
			if (succMap != null && failMap != null) {
				int succNum = Integer.parseInt(succMap.get("count") + "");
				int failNum = Integer.parseInt(failMap.get("count") + "");
				double amount = Double.parseDouble(succMap.get("fee") + "");
				double profit = Double.parseDouble(succMap.get("profit") + "");
				json.put("amount", amount);// 充值总金额
				json.put("succNum", succNum);// 成功笔数
				json.put("failNum", failNum);// 失败笔数
				json.put("sumNum", succNum + failNum);// 总笔数
				json.put("profit", profit);
				if ((succNum + failNum) != 0) {
					double rate = ((double) succNum) / (((double) (succNum + failNum)) / 100.0);
					json.put("rate", String.format("%.2f", rate) + "%");
				}
			}
		}
		return json;
	}

	/**
	 * 获取主页分上家列表
	 * 
	 * @author lks 2018年3月2日下午3:06:18
	 * @param request
	 * @param response
	 * @return
	 */
	public String countPhysicalChannelProfit(HttpServletRequest request, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		Map<String, Object> param = getMaps(request);
		int sum = 0;
		List<Map<String, Object>> list = dDao.selectPhysicalChannelProfit(param);
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			int num = Integer.parseInt(map.get("num") + "");
			sum = sum + num;
		}
		json.put("sum", sum);
		json.put("data", JSONObject.toJSON(list));
		return json.toJSONString();
	}

	/**
	 * 获取主页需要显示的数据
	 * 
	 * @author lks 2017年6月3日下午2:17:15
	 * @param request
	 * @param response
	 * @return
	 */
	public String getChartsData(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> param = getMaps(request);
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			//获取时间横坐标
			Date sDate = DateTimeUtils.parseDate(startDate, "yyyy-MM-dd");
			Date eDate = DateTimeUtils.parseDate(endDate, "yyyy-MM-dd");
			Long num = DateTimeUtils.diffDays(eDate, sDate);
			int number = num.intValue();
			List<String> dateList = new ArrayList<String>();
			for (int i = number; i > 0; i--) {
				Calendar theCa = Calendar.getInstance();
				theCa.setTime(eDate);
				theCa.add(Calendar.DATE, -i+1);
				Date date = theCa.getTime();
				dateList.add(DateUtil.formatDate(date, "yyyy-MM-dd"));
			}
			map.put("dateData", dateList);
			//判断搜索框是否存在代理商
			param.put("num", 10);
			List<Map<String, Object>> agentList = dDao.selectForntAgent(param);
			List<String> agentNameList = new ArrayList<String>();
			List<Echarts> seriesList = new ArrayList<Echarts>();
			for (int i = 0; i < agentList.size(); i++) {
				Map<String, Object> agentMap = agentList.get(i);
				agentNameList.add((String) agentMap.get("name"));
				List<String> chartsDataList = getDataList(dateList, agentMap);
				// map.put(agentMap.get("name")+"", chartsBillList);
				Echarts charts = new Echarts(agentMap.get("name") + "", "line", "销量", chartsDataList);
				seriesList.add(charts);
			}
			map.put("agentNameData", agentNameList);
			map.put("seriesData", seriesList);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject json = (JSONObject) JSONObject.toJSON(map);
		return json.toJSONString();
	}

	/**
	 * 获取主页需要显示的数据(物理通道)
	 * 
	 * @author lks 2017年6月3日下午2:17:15
	 * @param request
	 * @param response
	 * @return
	 */
	public String getChannelChartsData(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> param = getMaps(request);
		String startDate = (String) param.get("startDate");
		String endDate = (String) param.get("endDate");
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			//获取时间横坐标
			Date sDate = DateTimeUtils.parseDate(startDate, "yyyy-MM-dd");
			Date eDate = DateTimeUtils.parseDate(endDate, "yyyy-MM-dd");
			Long num = DateTimeUtils.diffDays(eDate, sDate);
			int number = num.intValue();
			List<String> dateList = new ArrayList<String>();
			for (int i = number; i > 0; i--) {
				Calendar theCa = Calendar.getInstance();
				theCa.setTime(eDate);
				theCa.add(Calendar.DATE, -i+1);
				Date date = theCa.getTime();
				dateList.add(DateUtil.formatDate(date, "yyyy-MM-dd"));
			}
			map.put("dateData", dateList);
			//判断搜索框是否存在代理商
//			param.put("num", 10);
			List<Map<String, Object>> channelList = dDao.selectForntChannel(param);
			List<String> channelNameList = new ArrayList<String>();
			List<Echarts> seriesList = new ArrayList<Echarts>();
			for (int i = 0; i < channelList.size(); i++) {
				Map<String, Object> channelMap = channelList.get(i);
				channelNameList.add((String) channelMap.get("name"));
				List<String> chartsDataList = getChannelDataList(dateList, channelMap);
				// map.put(agentMap.get("name")+"", chartsBillList);
				Echarts charts = new Echarts(channelMap.get("name") + "", "line", "销量", chartsDataList);
				seriesList.add(charts);
			}
			map.put("channelNameData", channelNameList);
			map.put("seriesData", seriesList);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject json = (JSONObject) JSONObject.toJSON(map);
		return json.toJSONString();
	}
	
	public List<String> getDataList(List<String> dateList, Map<String, Object> agentMap) {
		agentMap.put("dateList", dateList);
		List<String> dataList = dDao.selectAgentChartsData(agentMap);
		return dataList;
	}
	
	public List<String> getChannelDataList(List<String> dateList, Map<String, Object> chennelMap) {
		chennelMap.put("dateList", dateList);
		List<String> dataList = dDao.selectChannelChartsData(chennelMap);
		return dataList;
	}

	/**
	 * 判断用户是否具有代理商的角色
	 * 
	 * @author lks 2017年5月18日下午2:36:33
	 * @param suid
	 * @return
	 */
	public boolean judgeAgentRole(String suid) {
		boolean flag = false;
		List<Map<String, Object>> list = sysUserRoleDao.getHasSysRoleList(suid);
		for (Map<String, Object> role : list) {
			String roleName = role.get("srName") + "";
			if (roleName.equals("代理商")) {
				flag = true;
			}
		}
		return flag;
	}

}
