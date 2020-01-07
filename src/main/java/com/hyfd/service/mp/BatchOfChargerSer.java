package com.hyfd.service.mp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.BatchOfChargerDao;
import com.hyfd.service.BaseService;

@Service
public class BatchOfChargerSer extends BaseService {

	@Autowired
	BatchOfChargerDao batchOfChargerDao;
	
	public String batchOfChargerOrder(HttpServletRequest req) {
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m=getMaps(req); //封装前台参数为map
			Page p=getPage(m);//提取分页参数
			int total = batchOfChargerDao.selectCount(m);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String, Object>> dataList = batchOfChargerDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return sb.toString();
	}
	
	public void deriveStatement(HttpServletRequest req,HttpServletResponse res) {
		Map<String, Object> m=getMaps(req); //封装前台参数为map
		List<Map<String, Object>> tutubi = batchOfChargerDao.selectAll(m);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("兔兔币批量充值订单列表信息");
		HSSFRow row = sheet.createRow((int) 0);  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("手机号");  
        cell = row.createCell((short) 1);  
        cell.setCellValue("充值金额");  
        cell = row.createCell((short) 2);  
        cell.setCellValue("充值时间");  
        cell = row.createCell((short) 3);  
        cell.setCellValue("充值状态");
        cell = row.createCell((short) 4);  
        cell.setCellValue("运营商通道");  
        cell = row.createCell((short) 5);  
        cell.setCellValue("充值账号");  
        
        for(int i=0;i<tutubi.size();i++) {
        	row = sheet.createRow((int) i + 1);
        	Map<String, Object> map = tutubi.get(i);
        	row.createCell(0).setCellValue(map.get("phone").toString());
        	row.createCell(1).setCellValue(map.get("money").toString());
        	row.createCell(2).setCellValue(map.get("dateTime").toString());
        	row.createCell(3).setCellValue(map.get("state").toString());
        	row.createCell(4).setCellValue(map.get("type").toString());
        	row.createCell(5).setCellValue(map.get("account").toString());
        } 
        try {
        	res.setContentType("application/x-excel;charset=utf-8");
        	res.setHeader("Content-Disposition",
                "attachment;filename=tutubi-" + DateTimeUtils.formatDate(new Date(), "yyyy/MM/dd HH:mm:ss") + ".xlsx");
        	res.setCharacterEncoding("utf-8");
			OutputStream os = res.getOutputStream();
			wb.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
