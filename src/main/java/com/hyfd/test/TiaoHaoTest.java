package com.hyfd.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TiaoHaoTest {
	
	public static String A5 = ".*(\\d)\\1{4,9}$";// 五条
	public static String A4 = ".*(\\d)\\1{3}$";// 四条
	public static String A3 = ".*(\\d)\\1{2}$";// 三条
	// 大循环
	public static String ABCD = ".*(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3,8}\\d$";// ABCD
	public static String ABABAB = ".*(\\d)((?!\\1)\\d)(\\1\\2){2,4}$";// ABABAB
	public static String AAAAB = ".*(\\d)\\1{3}((?!\\1)\\d)$";// AAAAB
	public static String DCBA = ".*(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3,8}\\d$";// DCBA
	public static String AAAB = ".*(\\d)\\1{2}((?!\\1)\\d)$";// AAAB
	public static String AABAAB = ".*((\\d)\\1{1}((?!\\1)\\d)){1}$";// AAAB
	public static String AAABB = ".*(\\d)\\1{2}((?!\\1)\\d)\\2{1}$";// AAABB
	public static String AABB = ".*(\\d)\\1{1}((?!\\1)\\d)\\2{1}$";// AABB
	public static String ABAB = ".*(\\d)((?!\\1)\\d)(\\1\\2){1}$";// ABAB
	public static String ABC = ".*(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2}\\d$";// ABC

	public static void main(String[] args) throws FileNotFoundException {
		List<String> list = createPhones("1719876");
		List<String> x = phones118(list);
		System.out.println(x.size());
		for(int i = 0 ; i < x.size() ; i++){
			System.out.println(x.get(i));
		}
//		getOrderList();
//		String phone = "17043112112";
//		System.out.println(Pattern.matches(AABAAB, phone));
	}

	//获取号码
	public static void getOrderList() throws FileNotFoundException{
		BufferedWriter csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("E:/crm/xxx.csv"))), 1024);
		try {
			FileInputStream in = new FileInputStream("E:/crm/大靓号出库明细表2017-2019.xlsx");
			Workbook wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			for(Row row : sheet){
				if(row.getRowNum() != 0){
					String str1 = row.getCell(0).getStringCellValue();
					String str2 = row.getCell(1).getStringCellValue();
					String str3 = row.getCell(2).getStringCellValue();
					String str4 = row.getCell(3).getStringCellValue();
					//String str5 = row.getCell(4).getStringCellValue();
					String str6 = row.getCell(5).getStringCellValue();//号段
					String str7 = row.getCell(6).getStringCellValue();
					String str8 = row.getCell(7).getStringCellValue();
					String str9 = row.getCell(8).getStringCellValue();//个数
					String str10 = row.getCell(9).getStringCellValue();
					String str11 = row.getCell(10).getStringCellValue();
					String x =str1+"|"+str2+"|"+str3+"|"+str4+"|"+str6+"|"+str7+"|"+str8+"|"+str9+"|"+str10+"|"+str11+"|";
					List<String> list = createPhones(str6,str9);
					for(String str : list){
						System.out.println(x+str);
						String[] arr = str.split("\\|");
						StringBuffer sb = new StringBuffer();
			            String rowStr = sb.append("\"").append(str1).append("\",")
			            		.append("\"").append(str2).append("\",")
			            		.append("\"").append(str3).append("\",")
			            		.append("\"").append(str4).append("\",")
			            		.append("\"").append(str6).append("\",")
			            		.append("\"").append(str7).append("\",")
			            		.append("\"").append(str8).append("\",")
			            		.append("\"").append(str9).append("\",")
			            		.append("\"").append(str10).append("\",")
			            		.append("\"").append(str11).append("\",")
			            		.append("\"").append(arr[0]).append("\",")
			            		.append("\"").append(arr[1]).append("\",").toString();
			            csvWtriter.write(rowStr);
			            csvWtriter.newLine();
					}
					System.out.println("写入完成");
				}
			}
			wb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> createPhones(String section,String num){
		List<String> phones = createPhones(section);
		if(!num.equals("整段")){
			int n = Integer.parseInt(num);
			if(n >= 100 && n <= 108){
				return phones108(phones);
			}else if(n > 108 && n <= 118){
				return phones118(phones);
			}else if(n > 118 && n <= 144){
				return phones144(phones);
			}else if(n > 114 && n <= 223){
				return phones223(phones);
			}else if(n > 223 && n < 378){
				return phones377(phones);
			}else if(n == 378){
				return phones378(phones);
			}else if(n > 378 && n <= 458){
				return phones458(phones);
			}
		}
		return new ArrayList<String>();
	}
	
	public static List<String> phones108(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone+"|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			}
		}
		return list;
	}
	
	public static List<String> phones118(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone+"|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			}else if (Pattern.matches(ABABAB, phone)) {
				list.add(phone + "|ABABAB");
			}
		}
		return list;
	}
	
	public static List<String> phones144(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone+"|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			}else if (Pattern.matches(ABABAB, phone)) {
				list.add(phone + "|ABABAB");
			} else if (Pattern.matches(AAAAB, phone)) {
				list.add(phone + "|AAAAB");
			} else if (Pattern.matches(DCBA, phone)) {
				list.add(phone + "|DCBA");
			} else if (Pattern.matches(AAABB, phone)) {
				list.add(phone + "|AAABB");
			}else if (phone.substring(5,8).equals(phone.substring(8,11))) {
				list.add(phone + "|ABCABC");
			}
		}
		return list;
	}
	
	public static List<String> phones223(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone+"|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			} else if (Pattern.matches(AAAAB, phone)) {
				list.add(phone + "|AAAAB");
			} else if (Pattern.matches(DCBA, phone)) {
				list.add(phone + "|DCBA");
			} else if (Pattern.matches(AAABB, phone)) {
				list.add(phone + "|AAABB");
			}else if (phone.substring(5,8).equals(phone.substring(8,11))) {
				list.add(phone + "|AABAAB");
			}else if (Pattern.matches(AABB, phone)) {
				list.add(phone + "|AABB");
			}
		}
		return list;
	}
	
	public static List<String> phones377(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			}else if (Pattern.matches(AABB, phone)) {
				list.add(phone + "|AABB");
			}else if(Pattern.matches(AAAB, phone)){
				list.add(phone + "|AAAB");
			}else if(Pattern.matches(ABAB, phone)){
				list.add(phone + "|ABAB");
			}
		}
		return list;
	}
	
	public static List<String> phones378(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone+"|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone+"|AAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone+"|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone+"|ABCD");
			}else if (Pattern.matches(AABB, phone)) {
				list.add(phone + "|AABB");
			}else if(Pattern.matches(AAAB, phone)){
				list.add(phone + "|AAAB");
			}else if(Pattern.matches(ABAB, phone)){
				list.add(phone + "|ABAB");
			}
		}
		return list;
	}
	
	public static List<String> phones458(List<String> phones){
		List<String> list = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone + "|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone + "|AAAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone + "|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone + "|ABCD");
			} else if (Pattern.matches(ABABAB, phone)) {
				list.add(phone + "|ABABAB");
			} else if (Pattern.matches(AAAAB, phone)) {
				list.add(phone + "|AAAAB");
			} else if (Pattern.matches(DCBA, phone)) {
				list.add(phone + "|DCBA");
			} else if (Pattern.matches(AAAB, phone)) {
				list.add(phone + "|AAAB");
			} else if (Pattern.matches(AAABB, phone)) {
				list.add(phone + "|AAABB");
			} else if (Pattern.matches(AABB, phone)) {
				list.add(phone + "|AABB");
			} else if (Pattern.matches(ABAB, phone)) {
				list.add(phone + "|ABAB");
			} else if (Pattern.matches(ABAB, phone)) {
				list.add(phone + "|ABAB");
			} else if (Pattern.matches(ABC, phone)) {
				list.add(phone + "|ABC");
			}
		}
		return list;
	}
	
	public static List<String> ordinaryPhones(List<String> phones){
		List<String> list = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		for(String phone : phones){
			if (Pattern.matches(A5, phone)) {
				list.add(phone+"|AAAAA");
			} else if (Pattern.matches(A4, phone)) {
				list.add(phone + "|AAAA");
			} else if (Pattern.matches(A3, phone)) {
				list.add(phone + "|AAAA");
			} else if ((phone.substring(3, 7)).equals(phone.substring(7, 11))) {
				list.add(phone + "|大循环");
			} else if (Pattern.matches(ABCD, phone)) {
				list.add(phone + "|ABCD");
			} else if (Pattern.matches(ABABAB, phone)) {
				list.add(phone + "|ABABAB");
			} else if (Pattern.matches(AAAAB, phone)) {
				list.add(phone + "|AAAAB");
			} else if (Pattern.matches(DCBA, phone)) {
				list.add(phone + "|DCBA");
			} else if (Pattern.matches(AAAB, phone)) {
				list.add(phone + "|AAAB");
			} else if (Pattern.matches(AAABB, phone)) {
				list.add(phone + "|AAABB");
			} else if (Pattern.matches(AABB, phone)) {
				list.add(phone + "|AABB");
			} else if (Pattern.matches(ABAB, phone)) {
				list.add(phone + "|ABAB");
			} else if (Pattern.matches(ABAB, phone)) {
				list.add(phone + "|ABAB");
			} else if (Pattern.matches(ABC, phone)) {
				list.add(phone + "|ABC");
			}else{
				list1.add(phone);
			}
		}
		return list1;
	}
	
	/**
	 * 生成手机号
	 * 
	 * @param section
	 * @return
	 */
	public static List<String> createPhones(String section) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10000; i++) {
			list.add(section + String.format("%04d", i));
		}
		return list;
	}
	
}
