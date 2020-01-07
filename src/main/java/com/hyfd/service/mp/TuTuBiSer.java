package com.hyfd.service.mp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BatchOfChargerDao;
import com.hyfd.service.BaseService;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


@SuppressWarnings("unused")
@Service
public class TuTuBiSer extends BaseService{

	private static String loginUrl = "http://10040.snail.com/platform/web/agent/login";
	private static String checkUrl = "http://10040.snail.com/platform/web/agent/msg/check";
	private static String transferUrl = "http://10040.snail.com/platform/web/agent/order/transfer";
	private static String queryUrl = "http://10040.snail.com/platform/web/agent/order/transfer";

	private static HttpClient httpClient = new HttpClient();

	private static String cookies = "";

	private static String oldCode = "290014";
	
	private static int count;
	
	private static String resultCode;
	
	@Autowired
	private BatchOfChargerDao batchOfChargerDao;
	
	@Autowired
	private AgentAccountSer accountSer;
	
	@Autowired
	AgentDao agentDao;
	
	Logger log = Logger.getLogger(getClass());

	/**
	 * md5加密方法 by lks
	 */
	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 创建登录发送验证码
	 * 
	 * @param loginUrl
	 * @param checkUrl
	 * @param account
	 * @param password
	 * @return
	 */
	public String login4Cookie(String loginUrl, String checkUrl, String account, String password) {
		String msg = "";
		PostMethod loginMethod = new PostMethod(loginUrl);
		NameValuePair[] loginParam = { new NameValuePair("account", account),
				new NameValuePair("password", md5(password)), };
		loginMethod.setRequestBody(loginParam);
		int loginCode = 0;
		try {
			httpClient.executeMethod(loginMethod);
			String loginResult = loginMethod.getResponseBodyAsString();
			JSONObject loginJson = JSONObject.parseObject(loginResult);
			loginCode = loginJson.getIntValue("code");
			if (loginCode == 0) {
				msg = "0";
			} else if (loginCode == 1368) {
				msg = "1";
				log.debug("等待获取验证码，时间两分钟");
			}
		} catch (HttpException e) {
			msg = "2";
			log.error("兔兔币登陆方法报错   --HttpException");
			e.printStackTrace();
		} catch (IOException e) {
			msg = "2";
			log.error("兔兔币登陆方法报错   --IOException");
			e.printStackTrace();
		} catch (JSONException e) {
			msg = "2";
			e.printStackTrace();
		}finally {
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return msg;
	}

	/**
	 * 根据输入的验证码生成充值所需的Cook
	 * 
	 * @param account
	 * @param msgVerify
	 * @return
	 */
	public String cookie(String account, String msgVerify) {
		StringBuffer tmpcookies = new StringBuffer();
		try {
			PostMethod checkMethod = new PostMethod(checkUrl);
			NameValuePair[] checkParam = { new NameValuePair("account", account),
					new NameValuePair("msgVerify", msgVerify), };
			checkMethod.setRequestBody(checkParam);
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.executeMethod(checkMethod);
			String checkResult = checkMethod.getResponseBodyAsString();
			JSONObject checkJson = JSONObject.parseObject(checkResult);
			int checkCode = checkJson.getIntValue("code");
			if (checkCode == 0) {
				Cookie[] cookies = httpClient.getState().getCookies();
				for (Cookie c : cookies) {
					tmpcookies.append(c.toString() + ";");
				}
				System.out.println("cookie____________________" + tmpcookies.toString());
			} else if (checkCode == 1006) {
				log.error("兔兔币充值校验失败");
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}finally {
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return tmpcookies.toString();
	}

	/**
	 * 获得兔兔币要充值的号码及金额
	 * 
	 * @author lks 2017年9月11日上午8:51:19
	 * @return
	 */
	public List<Map<String, String>> getPhoneAndMoney(MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Workbook book = Workbook.getWorkbook(file.getInputStream());
				Sheet sheet = book.getSheet(0);
				for (int rows = 0; rows < sheet.getColumn(0).length; rows++) {
					String phone = sheet.getCell(0, rows).getContents();
					String money = sheet.getCell(1, rows).getContents();
					String realtyMoney = sheet.getCell(2, rows).getContents();
					Map<String, String> map = new HashMap<>();
					map.put("phone", phone);
					map.put("money", money);
					map.put("realityMoney", realtyMoney);
					list.add(map);
				}
				book.close();
				return list;
			} catch (FileNotFoundException e) {
				getMyLog(e, log);
				return null;
			} catch (IOException e) {
				getMyLog(e, log);
				return null;
			}catch (Exception e) {
				getMyLog(e, log);
				return null;
			}
		}
		return null;
	}

	/**
	 * 发送验证码
	 * 
	 * @return
	 */
	public String login(String account,String passWord) {
		return login4Cookie(loginUrl, checkUrl, account.trim(), passWord.trim());
	}
	
	/**
	 * @param file
	 * @param msgVerify
	 * @return
	 */
	public int recharge(MultipartFile file, HttpServletRequest req) {
		Map<String, Object> ms = getMaps(req);
		String msgVerify = ms.get("msgVerify").toString().trim();
		int sum = Integer.parseInt(ms.get("sum").toString());
		String account = ms.get("account").toString().trim();
		String ck = cookie(account, msgVerify);
		//System.out.println(msgVerify+"----"+sum+"----"+account+"----"+ck);
		List<Map<String, String>> list = getPhoneAndMoney(file);
		if(list == null) {
			return -1;
		}
		count = list.size();
		if(list.size() <= 30000){
			double money = 0;
			for(int i=0;i<list.size();i++) {
				Map<String, String> m = list.get(i);
				money += Double.parseDouble(m.get("realityMoney"));
			}
	        String agentId = ms.get("agentId").toString();
	        double agentMoney = accountSer.getAgentBalance(agentId);
	        if(agentMoney >= money){
	        	Map<String, Object> map = new HashMap<>();
				map.put("bizType", "2");
				map.put("agentId", agentId);
				String orderId = ToolDateTime.format(new Date(),"yyyyMMddHHmmssSSS") ;
				map.put("agentOrderId", "PK"+orderId);
				map.put("money", -money);
				map.put("remark", "代理商ID为"+agentId+"的代理批充兔兔币扣款"+money+"元");
				if(accountSer.ChargeForBatch(map)) {
					recursionCharge(1,sum,list,ck,account,map);
				}else {
					count = -1;//
				}// 
	        }else{
	        	count = -3;//余额不足
	        }
		}else{
			count = -2;//最多充值3w笔
		}
		return count;
	}

	public void recursionCharge(int num,int sum,List<Map<String, String>> list,String cookie,String account,Map<String,Object> moneyMap){
		if(num > sum){
			double money = 0;
			for(Map<String, String> map :list) {
				count--;
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				money += Double.parseDouble(map.get("realityMoney"));
				Map<String, Object> order = new HashMap<>();
				order.put("id", UUID.randomUUID().toString().replace("-", ""));
				order.put("phone", map.get("phone"));
				order.put("money", map.get("money"));
				order.put("realityMoney", map.get("realityMoney"));
				order.put("applyDate", DateTimeUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
				order.put("state", "1");
				order.put("resultCode", "失败");
				order.put("type", "2"); 
				order.put("account", account); 
				batchOfChargerDao.insertSelective(order);
			}
			moneyMap.put("money", money);
			if(money > 0) {
				moneyMap.put("remark", "代理商ID为"+moneyMap.get("agentId")+"的代理批充兔兔币失败退款"+money+"元");
				accountSer.ChargeForBatch(moneyMap);
			}
			return;
		}
		final List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
		for (int i = 0, size = list.size(); i < size; i++) {
			final int index = i;
			Map<String, String> map = list.get(i);
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						boolean flag = transferX(transferUrl,map.get("phone"),map.get("money"),cookie);
						if(flag){
							saveOrder(map,account);
						}else {
							failList.add(map); 
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				if(failList.size()>0){
					num++;
					recursionCharge(num,sum,failList,cookie,account,moneyMap);
				}
				break;
			}
		}
	}
	
	public synchronized void saveOrder(Map<String, String> map,String account){
		Map<String, Object> order = new HashMap<>();
		order.put("id", UUID.randomUUID().toString().replace("-", ""));
		order.put("phone", map.get("phone"));//手机号
		order.put("money", map.get("money"));//金额
		order.put("realityMoney", map.get("realityMoney"));//扣款
		order.put("applyDate", DateTimeUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
		order.put("state", "0");
		order.put("resultCode", resultCode);
		order.put("type", "2"); 
		order.put("account", account); 
		int flag = batchOfChargerDao.insertSelective(order);
		if(flag < 1){
			batchOfChargerDao.insertSelective(order);
		}
	}
	
	/**
	 * 兔兔币转账充值方法
	 * @author lks 2017年12月27日上午9:50:38
	 * @param transferUrl
	 * @param phone
	 * @param money
	 * @param transferCookies
	 * @return
	 * @throws IOException
	 */
	public boolean  transferX(String transferUrl,String phone,String money,String transferCookies) throws IOException{
		HttpClient httpClient = new HttpClient();
		boolean flag = false;
		PostMethod transferMethod = new PostMethod(transferUrl);
		NameValuePair[] transferParam = {
                new NameValuePair("accountDest", phone),
                new NameValuePair("money",money),
        };
		transferMethod.setRequestBody(transferParam);
		transferMethod.setRequestHeader("cookie",transferCookies);
		try {
			httpClient.executeMethod(transferMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(transferMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}  
			String transferResult = stringBuffer.toString();  
			log.debug("LksTest--------->"+transferResult);
			JSONObject transferJson = JSON.parseObject(transferResult);
			int transferCode = transferJson.getInteger("code");
			resultCode = JSONObject.toJSONString(transferJson);
			String msg = transferJson.getString("msg");
			System.out.println(phone + "|"+transferCode+msg);	
			if(transferCode == 0){
				flag = true;
				log.debug("兔兔币充值成功   ---- " + phone + "  | " + transferCode + "  | " + msg);
			}else{
				log.error("兔兔币充值方法   ---- " + phone + "  | " + transferCode + "  | " + msg);
			}
		} catch (HttpException e) {
			log.error("兔兔币充值方法报错   -- HttpException" + e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("兔兔币充值方法报错   -- IOException" + e);
			e.printStackTrace();
		}finally {
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
		return flag;
	}
}
