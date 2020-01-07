package com.hyfd.service.mp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.TutubiOrderDao;
import com.hyfd.service.BaseService;

@Service
public class TutubiOrderSer extends BaseService{

	@Autowired
	TutubiOrderDao tutubiOrderDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	CookiesDao cookiesDao;//登录信息
	
	/**
	 * 查询兔兔币列表
	 * @param req
	 * @return
	 */
	public String tutubiOrder(HttpServletRequest req) {
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			Page p=getPage(m);//提取分页参数
			int total = tutubiOrderDao.selectCount(m);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList = tutubiOrderDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return sb.toString();
	}
	
	public String tutubiOrderNet(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			String phone = m.get("accountDest")+"";
			Map<String, Object> cookies = cookiesDao.selectFirstCookie();
			String cookie = (String) cookies.get("cookies");
			HttpClient httpClient = new HttpClient();
			String queryUrl = "http://10040.snail.com/platform/web/agent/order/transfer";
			String url = queryUrl + "?currentPage=1&pageCount=10&paginationId=%23record-pagination_phone&number=10&table_data=table_data&startTime=2015-01-01&endTime=2015-01-31&phone="+phone+"&_eq="+System.currentTimeMillis()+"&_="+System.currentTimeMillis();
			GetMethod queryMethod = new GetMethod(url);
			queryMethod.setRequestHeader("cookie",cookie);
			httpClient.executeMethod(queryMethod);
			String result = queryMethod.getResponseBodyAsString();
			JSONObject json = JSONObject.parseObject(result);
			JSONObject value = json.getJSONObject("value");
			JSONArray array = value.getJSONArray("list");
			Page p=getPage(m);//提取分页参数
			int total = array.size();
			p.setCount(total);
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			sb.append(array);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return sb.toString();
	}
	
	/**
	 * 导出兔兔币报表
	 * @param req
	 * @return
	 */
	public void deriveStatement(HttpServletRequest req, HttpServletResponse response) {
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		List<Map<String, Object>> tutubi = tutubiOrderDao.selectAll(m);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("兔兔币订单列表信息");
		HSSFRow row = sheet.createRow((int) 0);  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("蜗牛订单号");  
        cell = row.createCell((short) 1);  
        cell.setCellValue("消费账号");  
        cell = row.createCell((short) 2);  
        cell.setCellValue("转出账号");  
        cell = row.createCell((short) 3);  
        cell.setCellValue("转入账号");
        cell = row.createCell((short) 4);  
        cell.setCellValue("号码归属地");  
        cell = row.createCell((short) 5);  
        cell.setCellValue("资源类型");  
        cell = row.createCell((short) 6);  
        cell.setCellValue("充值面额(元)");  
        cell = row.createCell((short) 7);  
        cell.setCellValue("缴费金额");  
        cell = row.createCell((short) 8);  
        cell.setCellValue("缴费时间");  
        cell = row.createCell((short) 9);  
        cell.setCellValue("采集时间"); 
        
        for(int i=0;i<tutubi.size();i++) {
        	row = sheet.createRow((int) i + 1);
        	Map<String, Object> map = tutubi.get(i);
        	row.createCell(0).setCellValue(map.get("orderId").toString());
        	row.createCell(1).setCellValue(map.get("account").toString());
        	row.createCell(2).setCellValue(map.get("accountSrc").toString());
        	row.createCell(3).setCellValue(map.get("accountDest").toString());
        	row.createCell(4).setCellValue(map.get("city").toString());
        	row.createCell(5).setCellValue(map.get("resource").toString());
        	row.createCell(6).setCellValue(map.get("money").toString());
        	row.createCell(7).setCellValue(map.get("rechargeAmount").toString());
        	row.createCell(8).setCellValue(map.get("rechargeTime").toString());
        	row.createCell(9).setCellValue(map.get("creatwTime").toString());
        } 
        try {
        	response.setContentType("application/x-excel;charset=utf-8");
            response.setHeader("Content-Disposition",
                "attachment;filename=tutubi-" + DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss") + ".xlsx");
            response.setCharacterEncoding("utf-8");
			OutputStream os = response.getOutputStream();
			wb.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String tutubiCookies(HttpServletRequest req,HttpServletResponse res){
		try{
			String id = "2000000016";
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取通道的数据
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String getCodeUrl = paramMap.get("getCodeUrl");
			GetMethod getCodeMethod = new GetMethod(getCodeUrl);
			HttpClient client = new HttpClient();
			client.executeMethod(getCodeMethod);
			String getCodeResult = getCodeMethod.getResponseBodyAsString();
			JSONObject getCodeJson = JSON.parseObject(getCodeResult);
			boolean getCodeflag = getCodeJson.getBoolean("flag");
			if(getCodeflag){
				String transferCookies = getCodeJson.getString("cookies");
				String msgVerify = getCodeJson.getString("msgVerify");
				Map<String,Object> cookie = new HashMap<String,Object>();
				cookie.put("ids", UUID.randomUUID().toString().replaceAll("-", ""));
				cookie.put("cookies", transferCookies);
				cookie.put("oldcode", msgVerify);
				cookie.put("updatetime", System.currentTimeMillis());
				cookie.put("bz", "TTB");
				int x = cookiesDao.insertSelective(cookie);
				if(x != 1){
					return "刷新失败!";
				}else{
					return "刷新成功！";
				}
			}else{
				return "刷新失败!";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "出现异常，刷新失败!";
		}
	}
	
}
