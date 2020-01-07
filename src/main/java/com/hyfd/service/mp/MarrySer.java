package com.hyfd.service.mp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hyfd.service.BaseService;

@Service
public class MarrySer  extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 匹配信息找到不同
	 * @param file
	 * @param req
	 */
	public void marry(MultipartFile file, HttpServletResponse res) {
		if (!file.isEmpty()) {
			try {
				List<String> list = new ArrayList<String>();
				List<String> lists = new ArrayList<String>();
				HSSFWorkbook wb = new HSSFWorkbook();
				Workbook book = new XSSFWorkbook(file.getInputStream());
				Sheet sheet = book.getSheetAt(0);
				for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
					Row row = sheet.getRow(i);
					if(row. getCell(0) != null) {
						String phone = row.getCell(0).getStringCellValue();
						list.add(phone);
					}
					if(row. getCell(1) != null) {
						String phones = row.getCell(1).getStringCellValue();
						lists.add(phones);
					}
				}
				book.close();
				
				List<String> list2 = getDifference(list,lists);//查出不同信息
				//导出异常信息
				HSSFSheet sheet1 = wb.createSheet("异常信息详情");
				HSSFRow row = sheet1.createRow((int) 0);  
		        HSSFCell cell = row.createCell((short) 0);  
		        cell.setCellValue("手机号");   
		        for(int i=0;i<list2.size();i++) {
		        	row = sheet1.createRow((int) i + 1);
		        	row.createCell(0).setCellValue(list2.get(i));
		        } 
		        res.setContentType("application/x-excel;charset=utf-8");
	        	res.setHeader("Content-Disposition","attachment;filename= 异常信息详情表.xlsx");
	        	res.setCharacterEncoding("utf-8");
				OutputStream os = res.getOutputStream();
				wb.write(os);
			} catch (FileNotFoundException e) {
				getMyLog(e, log);
			} catch (IOException e) {
				getMyLog(e, log);
			}catch (Exception e) {
				getMyLog(e, log);
			}
		}
	}

	/**
	 * 找不同
	 * @param phone1
	 * @param phone2
	 * @return
	 */
	public List<String> getDifference(List<String> phone1,List<String> phone2){
		long st = System.nanoTime();  
        Map<String,Integer> map = new HashMap<String,Integer>(phone1.size()+phone2.size());  
        List<String> diff = new ArrayList<String>();  
        List<String> maxList = phone1;  
        List<String> minList = phone2;  
        if(phone2.size()>phone1.size())  
        {  
            maxList = phone2;  
            minList = phone1;  
        }  
        for (String string : maxList) {  
            map.put(string, 1);  
        }  
        for (String string : minList) {  
            Integer cc = map.get(string);  
            if(cc!=null)
            {  
                map.put(string, ++cc);  
                continue;  
            }  
            map.put(string, 1);  
        }  
        for(Map.Entry<String, Integer> entry:map.entrySet())  
        {  
            if(entry.getValue()==1)  
            {  
                diff.add(entry.getKey());  
            }  
        }  
        return diff;  
	}
}
