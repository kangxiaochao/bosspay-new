package com.hyfd.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttps;

public class YongYouTest {

	private static HttpClient httpClient = new HttpClient();
	
	public static void main(String[] args) throws InterruptedException{
//		String s = "0";
//		System.out.println(Double.parseDouble(s));
//		Double d = Double.parseDouble(1.01+"")*100;
//		System.out.println(d.intValue());
		//queryCustInfo();
//		login();
//		Thread.sleep(10000);
//		String ccc = login2(login1(login()));
//		String c = payQuery();
//		payLogin(ccc);
//		Fenlie(c);
		String oid = System.currentTimeMillis()+"";
		queryCustInfo("17009356230",oid,"JSESSIONID_4_MVNO_CRM=9608953BB7914C8A9DDD42BAA1A5654A;");
//		queryCustInfo2(oid,c);
		
		charge();
		System.out.println(ToolHttps.post(false, "http://54.222.154.14:9100/MVNO-SSF/login", "username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_cC9DB193E-C709-7179-16B8-B26256F90798_kCA15C499-059D-2B33-F706-0C40D95FB5F3&_eventId=submit", "application/x-www-form-urlencoded"));
	}
	//{"RES_DATA":{"custId":18260,"freezeTotalBalance":0,"acctId":181980,"certType":"居民身份证(临时居民身份证)","myOID":"1555913182981","currentFee":0,"state":"正常","custState":"1","integralValue":0,"stateCode":"1","acctCount":6,"stateFlag":"1","custVipLevel":"","CITYCODE":"760","certNum":"371402********1220","employeeFlag":"0","acctName":"姜亚男-17012490803","owningFee":0,"custTypeId":"1","custMainNum":"17012490803","creditFee":0,"custName":"姜亚男","PRIVENCECODE":"76","custType":"个人客户"},"RES_RESULT":"SUCCESS"}
	//{"RES_MSG":"success","RES_DATA":{"custId":18260,"could_fee":"-5.40","should_fee":"5.40","this_fee":"5.40","acctId":181980,"used_limit_fee":"42356.830","acct_name":"姜亚男-17012490803","acct_type_value":"1","limit_fee":"50000.000"},"RES_RESULT":"SUCCESS"}
	//获取号码
	public static void Fenlie(String cookie){
//		String cookie = "JSESSIONID_4_MVNO_CRM=A50D3EB967104C9BAAE10E917D6E7192;";
		try {
			FileInputStream in = new FileInputStream("D:/yongyou.xlsx");
			Workbook wb = new XSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);
			for(Row row : sheet){
				String str = row.getCell(0).getStringCellValue();//手机号1
//				System.out.println(str);
				String oid = System.currentTimeMillis()+"";
//				System.out.println(queryCustInfo(str,oid,cookie));
				JSONObject json1 = JSONObject.parseObject(queryCustInfo(str,oid,cookie));
				JSONObject resData1 = json1.getJSONObject("RES_DATA");
//				System.out.println(queryCustInfo2(oid,cookie));
				JSONObject json2 = JSONObject.parseObject(queryCustInfo2(oid,cookie));
				JSONObject resData2 = json2.getJSONObject("RES_DATA");
				System.out.println(str +"|"+resData1.get("custName")+"|"+resData1.get("certNum")+"|"+resData1.get("state")+"|"+resData2.get("could_fee")+"|"+resData2.get("should_fee")+"|"+resData2.get("this_fee")+"|"+resData2.get("acct_name"));
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

	
	public static String login(){
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			GetMethod loginMethod = new GetMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			String result = stringBuffer.toString();
			System.out.println(result.trim());
			int x = result.indexOf("name=\"lt\" value=\"");
			System.out.println(x);
			String sss = result.substring(x+17, x+76+17);
			System.out.println(sss);
			return sss;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String login1(String lt){
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			NameValuePair[] transferParam = {
	                new NameValuePair("username", "hnzzhd"),
	                new NameValuePair("password","E9FF95C61178D1668A6C8502EFC89AFA"),
	                new NameValuePair("strong","true"),
	                new NameValuePair("command",""),
	                new NameValuePair("lt",lt),
	                new NameValuePair("_eventId","submit"),
	        };
			loginMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//			loginMethod.setRequestHeader("Cookie","JSESSIONID_4_SYSFRAME=A806FEE5D6D0472E84FE7F435A4B027C; ssoServerInst=CIS-SSF1; crmSid=1ad96079-2ad2-4eaa-b72c-77a4af4cfe8d-3135");
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			String result = stringBuffer.toString();
			System.out.println(result.trim());
			int x = result.indexOf("<input type=\"hidden\" name=\"lt\" value=\"");
			System.out.println(x);
			String sss = result.substring(x+38, x+76+38);
			System.out.println(sss);
			return sss;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String login2(String lt){
		StringBuffer tmpcookies = new StringBuffer();
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9100/MVNO-SSF/login");
			NameValuePair[] transferParam = {
					new NameValuePair("operId","828"),
	                new NameValuePair("operCode", "hnzzhd-110004e"),
	                new NameValuePair("_eventId_cancel","%E8%BF%94++%E5%9B%9E"),
	                new NameValuePair("lt",lt),
	                new NameValuePair("_eventId","submit")
	        };
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			loginMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36
			loginMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//			loginMethod.setRequestHeader("Cookie","JSESSIONID_4_SYSFRAME=A806FEE5D6D0472E84FE7F435A4B027C; ssoServerInst=CIS-SSF1; crmSid=1ad96079-2ad2-4eaa-b72c-77a4af4cfe8d-3135");
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies) {
				tmpcookies.append(c.toString() + ";");
			}
			System.out.println("cookie______1______________" + tmpcookies.toString());
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			System.out.println(stringBuffer.toString());
			return tmpcookies.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String payQuery(){
//		HttpClient httpClient = new HttpClient();
		StringBuffer tmpcookies = new StringBuffer();
		try{
			GetMethod pqMethod = new GetMethod("http://54.222.154.14:9200/MVNO-AR/payDispose/payquery");
			pqMethod.setFollowRedirects(false);
//			pqMethod.getParams().setParameter("http.protocol.allow-circular-redirects", true);
			//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36
			pqMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			httpClient.executeMethod(pqMethod);
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies) {
				tmpcookies.append(c.toString() + ";");
			}
			System.out.println("cookie________2____________" + tmpcookies.toString());
			return tmpcookies.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static void payLogin(String ccc){
		StringBuffer tmpcookies = new StringBuffer();
		try{
			GetMethod pqMethod = new GetMethod("http://54.222.154.14:9100/MVNO-SSF/login?service=http%3A%2F%2F54.222.154.14%3A9200%2FMVNO-AR&menuUrl=%2FpayDispose%2Fpayquery");
			pqMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
			pqMethod.setRequestHeader("Cookie",ccc);
			httpClient.executeMethod(pqMethod);
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies) {
				tmpcookies.append(c.toString() + ";");
			}
			System.out.println("cookie______3______________" + tmpcookies.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String queryCustInfo(String phone,String oid,String cookies){
//		System.out.println(cookies);
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/custComprehensive/queryCustInfo");
			NameValuePair[] transferParam = {
	                new NameValuePair("zjType", "num"),
	                new NameValuePair("zjNum",phone),
	                new NameValuePair("zjPwd",""),
	                new NameValuePair("busiOperCode",""),
	                new NameValuePair("myOID",oid),
	                new NameValuePair("pay_HideCustName","no"),
	                new NameValuePair("newAcctId","-1")
	        };
			loginMethod.setRequestHeader("Cookie",cookies);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
//			System.out.println(stringBuffer.toString());
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static String queryCustInfo2(String oid,String c){
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/pay/queryCustInfo");
			NameValuePair[] transferParam = {
	                new NameValuePair("oid",oid)
	        };
			loginMethod.setRequestHeader("Cookie",c);
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
//			System.out.println(stringBuffer.toString());
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static void charge(){
		try{
			//username=hnzzhd&password=E9FF95C61178D1668A6C8502EFC89AFA&strong=true&command=&lt=_c8D17302E-1E93-F5C6-F8E9-A5F7047FFF1E_kB7C7B276-8236-C465-225D-BCB1CB5E7C28&_eventId=submit
			PostMethod loginMethod = new PostMethod("http://54.222.154.14:9200/MVNO-AR/pay/submit");
			NameValuePair[] transferParam = {
//	                new NameValuePair("_FORM_TOKEN","1555657861667-1645"),
	                new NameValuePair("acct_type_value","1"),
	                new NameValuePair("acctID","185463"),
	                new NameValuePair("collect_fee","0.01"),
	                new NameValuePair("custId","16998"),
	                new NameValuePair("has_query","1"),
	                new NameValuePair("old_pay_fee",""),
	                new NameValuePair("payAmount","0.01"),
	                new NameValuePair("payStyle","1"),
	                new NameValuePair("should_fee_old","0.00"),
	        };
			loginMethod.setRequestHeader("Cookie","JSESSIONID_4_MVNO_CRM=27E270368B7D4A829B2AFD36767E3DFD; crmSid=1ad96079-2ad2-4eaa-b72c-77a4af4cfe8d-3135");
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			System.out.println(stringBuffer.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
