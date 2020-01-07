package com.hyfd.service.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.CardDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.service.BaseService;

@Service
public class CardSer extends BaseService{

	@Autowired
	CardDao cardDao;
	@Autowired
	OrderDao orderDao;
	
	Logger log = Logger.getLogger(getClass());
	
	public String cardList(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			Page p=getPage(m);//提取分页参数
			int total = cardDao.countCard(m);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList = cardDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		
		return sb.toString();
	}
	
	/**
	 * 导出模板
	 * @author lks 2017年7月26日下午5:31:04
	 * @param request
	 * @param response
	 */
	public void exportCardTemp(HttpServletRequest request,HttpServletResponse response){
		String templatePath = request.getServletContext().getRealPath("/")+File.separator + "downloadFiles"+ File.separator+"cardTemp.xlsx";
		try{
			response.setContentType("application/x-excel;charset=utf-8");
	        response.setHeader("Content-Disposition", "attachment;filename=cardTemp.xlsx");
	        response.setCharacterEncoding("utf-8");
	        FileInputStream in = new FileInputStream(templatePath);
	        OutputStream os = response.getOutputStream();
	        int b;  
	        while((b = in.read())!= -1){  
	        	os.write(b);  
	        }  
	        os.close();
	        in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入卡密信息
	 * @author lks 2017年7月26日下午5:54:59
	 * @param request
	 * @param response
	 * @return
	 */
	public String importCard(HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		int i = 0;
		String message = "请上传文件";
		try{
			DefaultMultipartHttpServletRequest req = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = req.getFileNames();
			 while (iterator.hasNext()) {
	            MultipartFile file = req.getFile(iterator.next());
	            Workbook wb = new XSSFWorkbook(file.getInputStream());
				Sheet sheet = wb.getSheetAt(0);
				for(Row row : sheet){
					if(row.getRowNum() != 0){
						Map<String,Object> card = new HashMap<String,Object>();
						card.put("cardId", row.getCell(0).getStringCellValue().trim());
						card.put("cardPass", row.getCell(1).getStringCellValue().trim());
						card.put("expireTime", row.getCell(2).getDateCellValue());
						card.put("price", new Double(row.getCell(3).getNumericCellValue()).intValue());
						card.put("type", row.getCell(4).getStringCellValue());
						card.put("useState", 0);
						Map<String,Object> param = new HashMap<String,Object>();
						param.put("cardId", row.getCell(0).getStringCellValue());
						int flag = cardDao.countCard(param);
						if(flag < 1){
							int x = cardDao.insertSelective(card);
							i = i + x;	
						}
					}
				}
				message = "有"+i+"条卡密信息上传成功！";
				wb.close();
			 }
		}catch(Exception e){
			message = "上传卡密出错";
			e.printStackTrace();
			log.error(e.getMessage());
		}
		json.put("message", message);
		return json.toJSONString();
	}
	
	/**
	 * 获取订单详细记录
	 * @author lks 2018年1月6日下午4:24:08
	 * @param orderId
	 * @return
	 */
	public String detailOrder(String orderId){
		Map<String,Object> order = orderDao.selectById(orderId);
		return JSONObject.toJSONString(order);
	}
	
	/**
	 * 修改卡密状态
	 * @param request
	 * @return
	 */
	public String updateCard(HttpServletRequest req) {
		Map<String, Object> m = getMaps(req); 
		String cardId = m.get("cardId").toString();
		String[] str = cardId.split(",");
		List<Map<String, Object>> card = new ArrayList<>();
		for(String s:str){
			Map<String, Object> map = cardDao.queryByCardId(s);
			if(map.get("useState").toString().equals("2")) {
				card.add(map);
			}
        }
		for(Map<String, Object> map : card) {
			map.put("useState", "0");
			cardDao.updateByPrimaryKeySelective(map);
		}		
		return card.size()+"";
	}
	
	/**
	 * 导出卡密使用信息
	 * @param request
	 * @param response
	 * @return
	 */
	public void exportCard(HttpServletRequest req,HttpServletResponse res) {
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		List<Map<String, Object>> card = cardDao.selectAll(m);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("卡密信息");
		HSSFRow row = sheet.createRow((int) 0);  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("卡号");  
        cell = row.createCell((short) 1);  
        cell.setCellValue("金额");  
        cell = row.createCell((short) 2);  
        cell.setCellValue("使用时间");  
        cell = row.createCell((short) 3);  
        cell.setCellValue("种类");
        cell = row.createCell((short) 4);  
        cell.setCellValue("状态"); 
        
        for(int i=0;i<card.size();i++) {
        	row = sheet.createRow((int) i + 1);
        	Map<String, Object> map = card.get(i);
        	row.createCell(0).setCellValue(map.get("cardId").toString());
        	row.createCell(1).setCellValue(map.get("price").toString());
        	row.createCell(2).setCellValue(map.get("useTime").toString());
        	row.createCell(3).setCellValue(map.get("type").toString());
        	row.createCell(4).setCellValue(map.get("resultMsg").toString());
        } 
        try {
        	res.setContentType("application/x-excel;charset=utf-8");
        	res.setHeader("Content-Disposition",
                "attachment;filename=card.xlsx");
        	res.setCharacterEncoding("utf-8");
			OutputStream os = res.getOutputStream();
			wb.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
