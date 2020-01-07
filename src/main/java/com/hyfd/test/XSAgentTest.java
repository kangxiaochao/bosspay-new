package com.hyfd.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSAgentTest {

	public static List<Map<String, Object>> readExcel() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			FileInputStream in = new FileInputStream("C:/Users/Administrator/Desktop/order.xlsx");
			Workbook wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				Map<String, Object> map = new HashMap<String, Object>();
				String name = row.getCell(0).getStringCellValue();// 客户名
				String phone = row.getCell(1).getStringCellValue();// 手机号
				String address = row.getCell(2).getStringCellValue();// 地址
				String qq = row.getCell(3).getStringCellValue();// qq
				String createTime = row.getCell(4).getStringCellValue();// 创建时间
				String lastLxTime = row.getCell(5).getStringCellValue();// 最后联系时间
				String remark = row.getCell(6).getStringCellValue();// 存货
				String channel = row.getCell(7).getStringCellValue();// 套餐
				String phone1 = row.getCell(8).getStringCellValue();// 品牌
				String wx = row.getCell(9).getStringCellValue();// 单位
				String wxName = row.getCell(10).getStringCellValue();// 数量
				String md = row.getCell(11).getStringCellValue();// 门店
				String lastNkTime = row.getCell(12).getStringCellValue();// 最后拿卡时间
				String type = row.getCell(13).getStringCellValue();// 拿卡类型
				String pj = row.getCell(14).getStringCellValue();// 评级
				String province = row.getCell(15).getStringCellValue();// 省
				String city = row.getCell(16).getStringCellValue();// 市
				String area = row.getCell(16).getStringCellValue();// 区
				String ly = row.getCell(17).getStringCellValue();// 来源
				String tjr = row.getCell(18).getStringCellValue();// 来源
				map.put("name", name);
				map.put("phone", phone);
				map.put("address", address);
				map.put("qq", qq);
				map.put("createTime", createTime);
				map.put("lastLxTime", lastLxTime);
				map.put("remark", remark);
				map.put("channel", channel);
				map.put("phone1", phone1);
				map.put("wx", wx);
				map.put("wxName", wxName);
				map.put("md", md);
				map.put("lastNkTime", lastNkTime);
				map.put("type", type);
				map.put("pj", pj);
				map.put("province", province);
				map.put("city", city);
				map.put("area", area);
				map.put("ly", ly);
				map.put("tjr", tjr);
				list.add(map);
			}
			wb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
