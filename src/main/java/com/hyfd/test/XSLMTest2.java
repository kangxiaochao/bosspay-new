package com.hyfd.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XSLMTest2 {

	public static void main(String[] args){
//		List<String> list = getSysOrderId();
//		List<Map<String,Object>> xList = getProductId();
//		for(String str : list){
//			//INSERT INTO `mhsc`.`nms_shopping_order_product` 
//			//( `sys_order_id`, `product_id`, `product_name`, `product_type`, `total_price`, `remaining_total_price`, `discount_price`, `strike_price`, `create_time`, `normal_total`, `remaining_normal_total`, `cute_total`, `isp`, `brand`, `split_flag`, `area`, `number_id`, `total`) VALUES 
//			//('S20190112374477', '19010356371', '17150006靓号包', '2', '4500000', '2000000', '4500000', '1', '1546524637214', '955', '955', '2', '2', '1', '2', '北京(市)', '20190128631309', '1');
//			System.out.println(str);
//		}
		genPSql();
	}
	
	public static void genPSql(){
		List<Map<String, Object>> list = readExcel();
		List<String> slist = getSysOrderId();
		int x = 0 ;
		for(int i = 0 ;i < list.size() ; i++){
			Map<String,Object> map = list.get(i);
			String type = map.get("type")+"";
			String unit = map.get("unit")+"";
			if(type.equals("普通销售")&&unit.equals("张")){
				String product = map.get("product")+"";
				Map<String, Object> pMap = getPhoneSection(product);
				String section = pMap.get("section") + "";
				String area = pMap.get("area")+"";
				Double n = Double.parseDouble(map.get("number")+"");
				int number  = n.intValue();
				String sec = "";
				if(number <= 458){
					sec = section + "靓号包";
				}else{
					sec = section + "普号包";
				}
				String money = map.get("money")+"";
				Double m = Double.parseDouble(money)*100;
				String productId = "19033"+getNum(6);
				String sql = "INSERT INTO `mhsc`.`nms_product_info` "
						+ "( `product_id`, `product_name`, `product_type`, `product_state`, `section_id`, `isp`, `brand`, `normal_total`, `remaining_normal_total`, `cute_total`, `remaining_cute_total`, `area`, `create_time`, `operator_id`, `operator_phone`, `operator_name`, `remark`, `modify_time`, `total_price`, `remaining_total_price`, `discount_price`, `split_flag`, `promotion_id`, `sale_mode`) VALUES "
						+ "('"+productId+"', '"+section+"', '1', '2', '"+sec+"', '2', '1', '376', '376', '0', '0', '"+area+"', '1519228800000', 'system', '', 'system', '', '1557860513955', '"+m.intValue()+"', '"+m.intValue()+"', '"+m.intValue()+"', '1', '0', '0');";
				System.out.println(sql);
				String sql1 = "INSERT INTO `mhsc`.`nms_shopping_order_product` "
						+ "(`sys_order_id`, `product_id`, `product_name`, `product_type`, `total_price`, `remaining_total_price`, `discount_price`, `strike_price`, `create_time`, `normal_total`, `remaining_normal_total`, `cute_total`, `isp`, `brand`, `split_flag`, `area`, `number_id`, `total`) VALUES "
						+ "('"+slist.get(x++)+"', '"+productId+"', '"+sec+"', '1', '"+m.intValue()+"', '"+m.intValue()+"', '"+m.intValue()+"', '1', '1534423561667', '0', '0', '1', '3', '2', '1', '"+area+"', '20190801"+getNum(6)+"', '1');";
				System.out.println(sql1);
			}
		}
	}
	
	public static List<Map<String, Object>> readExcel() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			FileInputStream in = new FileInputStream("C:/Users/Administrator/Desktop/order.xlsx");
			Workbook wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				Map<String, Object> map = new HashMap<String, Object>();
				String date = row.getCell(0).getStringCellValue();// 日期
				String type = row.getCell(1).getStringCellValue();// 业务类型
				String agent = row.getCell(2).getStringCellValue();// 客户
				String channel = row.getCell(3).getStringCellValue();// 渠道
				String agentCode = row.getCell(4).getStringCellValue();// 客户编码
				String catagory = row.getCell(5).getStringCellValue();// 分类
				String product = row.getCell(6).getStringCellValue();// 存货
				String setMeal = row.getCell(7).getStringCellValue();// 套餐
				String provider = row.getCell(8).getStringCellValue();// 品牌
				String unit = row.getCell(9).getStringCellValue();// 单位
				double number = row.getCell(10).getNumericCellValue();// 数量
				double money = row.getCell(11).getNumericCellValue();// 金
				map.put("date", date);
				map.put("type", type);
				map.put("agent", agent);
				map.put("channel", channel);
				map.put("agentCode", agentCode);
				map.put("catagory", catagory);
				map.put("product", product);
				map.put("setMeal", setMeal);
				map.put("provider", provider);
				map.put("unit", unit);
				map.put("number", number);
				map.put("money", money);
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
	

	public static Map<String, Object> getPhoneSection(String str) {
		Map<String, Object> map = new HashMap<String, Object>();
		str = str.replaceAll("-L", "");
		Pattern pattern = Pattern.compile("\\d{7}");
		Matcher m = pattern.matcher(str);
		if (m.find()) {
			String section = m.group();
			map.put("section", section);
			map.put("area", str.replaceAll(section, ""));
		}
		return map;
	}
	
	public static String getNum(int x) {
		String num = "";
		String str = "0123456789";
		char[] arr = str.toCharArray();
		for (int i = 0; i < x; i++) {
			int index = (int) (Math.random() * arr.length);
			num += arr[index];
		}
		return num;
	}
	
	public static List<String> getSysOrderId(){
		List<String> list = new ArrayList<String>();
		try {
			String p = "1Qaz2Wsx#$%25Test";
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://rm-hp377ec970035gl6ajo.mysql.huhehaote.rds.aliyuncs.com:3306/mhsc?user=root&password="+p+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT sys_order_id from nms_shopping_order where payment_serial_number = '102585693395201808164414083071';");
			while(rs.next()){
				list.add(rs.getString("sys_order_id"));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<Map<String,Object>> getProductId(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try {
			String p = "1Qaz2Wsx#$%25Test";
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://rm-hp377ec970035gl6ajo.mysql.huhehaote.rds.aliyuncs.com:3306/mhsc?user=root&password="+p+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT product_id,product_name from nms_product_info;");
			while(rs.next()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("productId", rs.getString("product_id"));
				map.put("productName", rs.getString("product_name"));
				list.add(map);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
