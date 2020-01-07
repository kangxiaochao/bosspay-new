package com.hyfd.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.hyfd.test.TiaoHaoTest;

public class XSLMTestOrderSql {

	public static void main(String[] args) throws DateParseException, ParseException {
		// List<Map<String, Object>> list = readExcel();
//		List<String> list = createOrdinaryPhone(TiaoHaoTest.createPhones("1876416"), 6000);
//		System.out.println(list.size());
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i));
//		}
		getOrderSql();
//		System.out.println(getTime("2019-08-01"));
//		System.out.println(getTime("2019-07-01"));
	}

	public static void getOrderSql() throws DateParseException, ParseException {
		List<Map<String, Object>> list = readExcel();
		System.err.println(list.size());
		for(Map<String,Object> map : list){
			String type = map.get("type")+"";
			Date date = DateUtils.parseDate(map.get("date")+"", "yyyy-MM-dd");
			String orderId = "S" + DateUtil.formatDate(date, "yyyyMMdd")+getNum(6);
			String agent = map.get("agent")+"";
			Map<String,Object> user = getUserInfo(agent);
			double money = Double.parseDouble(map.get("money")+"")*100;
			String channel = map.get("channel")+"";
			String unit = map.get("unit")+"";
			if(type.equals("直接销售")&&unit.equals("张")){
				String sql = "INSERT INTO nms_shopping_order "
						+ "(`user_phone`, `user_name`, `user_id`, `create_time`, `modify_time`, `sys_order_id`, `order_state`, `payment_state`, `payment_type`, `delivery_state`, `delivery_order_id`, `delivery_name`, `delivery_address`, `total_price`, `discount_price`, `total_strike_price`, `depart_id`, `depart_name`, `operator_id`, `operator_phone`, `operator_name`, `expire_time`, `payment_create_time`, `payment_serial_number`, `terminal_type`, `sync_flag`, `sync_retry`, `return_delivery_id`, `return_delivery_name`, `dealer_id`, `dealer_id_name`, `dealer_id_phone`, `return_flag`, `return_order_id`, `agent_flag`, `agent_id`, `agent_name`, `agent_phone`, `remark`, `address`, `rebate_remark`, `receipt`, `cms_sign`, `expressage`) VALUES "
						+ "('"+user.get("phone")+"', '"+agent+"', '"+user.get("userId")+"', '"+date.getTime()/100000+getNum(5)+"', '"+date.getTime()/100000+getNum(5)+"', '"+orderId+"', '2', '2', '4', '3', NULL, NULL, '"+user.get("address")+"', '"+money+"', '"+money+"', '"+money+"', NULL, NULL, NULL, NULL, NULL, '"+date.getTime()/100000+getNum(5)+"', '"+date.getTime()/100000+getNum(5)+"', '102585693395201808164414083071', '3', '0', '4', NULL, NULL, NULL, "+System.currentTimeMillis()+", '"+channel+"', '0', NULL, '0', '', '', '', '', '', '', '0', '0', '0');";
				System.out.println(sql);
			}else if(type.equals("虚商业务")){
				String sql = "INSERT INTO nms_shopping_order "
						+ "(`user_phone`, `user_name`, `user_id`, `create_time`, `modify_time`, `sys_order_id`, `order_state`, `payment_state`, `payment_type`, `delivery_state`, `delivery_order_id`, `delivery_name`, `delivery_address`, `total_price`, `discount_price`, `total_strike_price`, `depart_id`, `depart_name`, `operator_id`, `operator_phone`, `operator_name`, `expire_time`, `payment_create_time`, `payment_serial_number`, `terminal_type`, `sync_flag`, `sync_retry`, `return_delivery_id`, `return_delivery_name`, `dealer_id`, `dealer_id_name`, `dealer_id_phone`, `return_flag`, `return_order_id`, `agent_flag`, `agent_id`, `agent_name`, `agent_phone`, `remark`, `address`, `rebate_remark`, `receipt`, `cms_sign`, `expressage`) VALUES "
						+ "('"+user.get("phone")+"', '"+agent+"', '"+user.get("userId")+"', '"+date.getTime()/100000+getNum(5)+"', '"+date.getTime()/100000+getNum(5)+"', '"+orderId+"', '2', '2', '4', '3', NULL, NULL, '"+user.get("address")+"', '"+money+"', '"+money+"', '"+money+"', NULL, NULL, NULL, NULL, NULL, '"+date.getTime()/100000+getNum(5)+"', '"+date.getTime()/100000+getNum(5)+"', '102585693395201808164414083071', '3', '0', '4', NULL, NULL, NULL, "+System.currentTimeMillis()+", '"+channel+"', '0', NULL, '0', '', '', '', '', '', '', '0', '0', '0');";
				System.out.println(sql);
			}
		}
	}
	
	public static List<Map<String, Object>> readExcel() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			FileInputStream in = new FileInputStream("C:/Users/Administrator/Desktop/17order.xlsx");
			Workbook wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				Map<String, Object> map = new HashMap<String, Object>();
				String date = row.getCell(9).getStringCellValue();// 日期
				String type = row.getCell(1).getStringCellValue();// 业务类型
				String agent = row.getCell(13).getStringCellValue();// 客户
				String channel = row.getCell(13).getStringCellValue();// 渠道(缺失)
				String province = row.getCell(14).getStringCellValue();
				String city = row.getCell(15).getStringCellValue();
				String agentCode = row.getCell(4).getStringCellValue();// 客户编码
				String catagory = row.getCell(5).getStringCellValue();// 分类
				String product = row.getCell(6).getStringCellValue();// 存货
				String setMeal = row.getCell(18).getStringCellValue();// 套餐
				String provider = row.getCell(8).getStringCellValue();// 品牌
				String unit = row.getCell(7).getStringCellValue();// 单位
				double number = row.getCell(20).getNumericCellValue();// 数量
				double money = row.getCell(21).getNumericCellValue();// 金
				map.put("date", date);
				map.put("type", type);
				map.put("agent", agent);
				map.put("address", province+city);
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
	
	public static String getTime(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime()/100000+getNum(5);
	}

	public static void getPhones(List<Map<String, Object>> list) {
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			System.out.println(map.get("number"));
			if ((double) map.get("number") == 108) {
				List<String> phones = TiaoHaoTest.phones108(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 118) {
				List<String> phones = TiaoHaoTest.phones118(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 144) {
				List<String> phones = TiaoHaoTest.phones144(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 223) {
				List<String> phones = TiaoHaoTest.phones223(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 377) {
				List<String> phones = TiaoHaoTest.phones377(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 378) {
				List<String> phones = TiaoHaoTest.phones378(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			} else if ((double) map.get("number") == 458) {
				List<String> phones = TiaoHaoTest.phones458(
						TiaoHaoTest.createPhones(getPhoneSection(map.get("product") + "").get("section") + ""));
				for (String phone : phones) {
					System.out.println(phone);
				}
			}
		}
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

	/**
	 * 按数量生成普号
	 * 
	 * @param list1
	 * @param num
	 * @return
	 */
	public static List<String> createOrdinaryPhone(List<String> list1, int num) {
		List<String> list = TiaoHaoTest.ordinaryPhones(list1);
		return list.subList(0, num);
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

	public static Map<String,Object> getUserInfo(String username){
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String p = "1Qaz2Wsx#$%25Test";
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://rm-hp377ec970035gl6ajo.mysql.huhehaote.rds.aliyuncs.com:3306/mhsc?user=root&password="+p+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("SELECT user_id,detail_address,phone from ums_user_address_info where username = '"+username+"';");
			while(rs.next()){
				map.put("userId", rs.getString("user_id"));
				map.put("address", rs.getString("detail_address"));
				map.put("phone", rs.getString("phone"));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
}
