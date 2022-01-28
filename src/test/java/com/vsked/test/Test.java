package com.vsked.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;

public class Test {
	
	public static List<Map<String,String>> connect(){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			String p = "Lks123456";
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://rm-bp15x3195m158018mo.mysql.rds.aliyuncs.com:3306/bosspay_bill?user=lks&password="+p+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
//			ResultSet rs = statement.executeQuery("SELECT a.* from (SELECT * from mp_submit_order where submit_date > '2017-08-17 00:00:00') a LEFT JOIN (SELECT * from mp_order where apply_date > '2017-08-17 00:00:00') b on a.order_id = b.agent_order_id where a.result_code = '0:提交成功' and a.agent_name = 'yijie' and (b.phone is NULL or b.status is null)");
			ResultSet rs = statement.executeQuery("SELECT * from mp_agent_bill_discount where agent_id = (select id from mp_agent where name = 'lilei') and del_flag = 1");
			while(rs.next()){
				Map<String,String> map = new HashMap<String,String>();
//				map.put("agent_id", rs.getString("agent_id"));
				map.put("provider_id", rs.getString("provider_id"));
				map.put("province_code", rs.getString("province_code"));
				map.put("discount", rs.getString("discount"));
				map.put("bill_pkg_id", rs.getString("bill_pkg_id"));
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
	
	public static List<String> getAgentId(){
		List<String> list = new ArrayList<String>();
		try {
			String p = "Lks123456";
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://rm-bp15x3195m158018mo.mysql.rds.aliyuncs.com:3306/bosspay_bill?user=lks&password="+p+"&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
			Connection conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
//			ResultSet rs = statement.executeQuery("SELECT a.* from (SELECT * from mp_submit_order where submit_date > '2017-08-17 00:00:00') a LEFT JOIN (SELECT * from mp_order where apply_date > '2017-08-17 00:00:00') b on a.order_id = b.agent_order_id where a.result_code = '0:提交成功' and a.agent_name = 'yijie' and (b.phone is NULL or b.status is null)");
			ResultSet rs = statement.executeQuery("SELECT id FROM mp_agent WHERE `name` IN ('linweiji')");
			while(rs.next()){
				list.add(rs.getString("id"));
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
	
	//ca431cd890564a47b15593cba5890aa0
	public static void main(String[] args){
		List<Map<String,String>> list = connect();
		System.out.println(list.size());
		List<String> agentIdList = getAgentId();
		System.out.println(agentIdList.size());
		for(int i = 0; i<list.size(); i++){
			Map<String,String> map = list.get(i);
			for(int j = 0 ; j < agentIdList.size() ; j++){
				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','"+agentIdList.get(j)+"','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
			}
//			if("0.995000".equals(map.get("discount"))){
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+id+"','ca431cd890564a47b15593cba5890aa0','"+map.get("provider_id")+"','"+map.get("province_code")+"','0.993','"+map.get("bill_pkg_id")+"','1');");
//			}else{
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+id+"','d7311d94614241a49c0b7066c44675ed','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','287f0b60745d42e582954540c8bacdc6','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','2d9d9628fc44409fa7c79e94681b55a6','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','8b68ab6a1d494f828a61728d154420ba','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','5814b80f08bc4573b497cc4f072aa4dd','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','0eef36e0fe1c47f39e3526a27cdf32e1','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','6bb235028a634edc8fefa7f2faa002e5','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','7b00ed3224c0446a91310bdee8126e09','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','09dbc9d80adf46a9a2972659baddf5fa','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','121c6de4c4d34d348dac58de18773110','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','9362f58308494663b31439e7b7ce0baa','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','ced99bf355da496a93ab2b4f05b84e6d','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','f2d4b819082f45a599a7c31f286e27ec','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','130aad0323a8422d89db49b732902a8c','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','e30905fc56e143bb9dc84edb07b75b0f','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','46b490c89b5445eabdf84192a5e6b0ed','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','95c1b97a1d6b48078115e7159981c43d','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','b277fd40fe6c499ca6e9b4de29367209','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','b213567a426548adb28605616eaf7fb9','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','b9dd9facac1647199bb3915e8363e96c','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','04e0d9e953f2409d9a7e919ca4ca8cbe','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','df0cd739751144b7adec8144aa63cf7d','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','7b3f782a8f0949cf98aaa3c5f3ce499f','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','aee341e4f4644de288b9fdeb1b5269b2','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','5aea3e3aa3e946adb0ce8272a88833cd','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
//				System.out.println("INSERT INTO mp_agent_bill_discount (id,agent_id,provider_id,province_code,discount,bill_pkg_id,del_flag) VALUES ('"+UUID.randomUUID().toString().replaceAll("-", "")+"','786468b182c546c889fb9255e325262a','"+map.get("provider_id")+"','"+map.get("province_code")+"','"+map.get("discount")+"','"+map.get("bill_pkg_id")+"','1');");
				
//			}
		}
	}

}
