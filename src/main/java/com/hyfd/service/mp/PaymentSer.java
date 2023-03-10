package com.hyfd.service.mp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.IPUtil;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.common.utils.zx.SignUtil;
import com.hyfd.dao.mp.AgentAccountChargeDao;
import com.hyfd.dao.mp.AgentAccountDao;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.PaymentDao;
import com.hyfd.service.BaseService;

@Service
public class PaymentSer extends BaseService {

	public Logger log = Logger.getLogger(this.getClass());
	
	public static String private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmNwzPHTZpjr1thAG0AqsuhdfU6wu1fY6i/AoPsYCfbS8Zu9c/V5LYM/06mNWZMo0GbPPCXgYBLPWr6n5POpIFUGjezAkp6gqB7wy+3DGFvL815SdG6VDvXlq6wNEvk2/z7DkSwJ6ydQsRgAa1zzKIo2Bx+ROWwTIonaqOQYrJedEW7Z+0YyHpVJe5o8EBbML+B6t+kr2AdKnYi7jdQvHhvOg99/sjoPGtdFxX/7y8fUdPfNUo6LNBAGxXRydVF547yMo6oIifZHLzQCfjbnfbn78/Rx7dJSQwldP+bczfx071GkakJfRKAvFV1VO2oYOwzrD97h1Er86PWPs1INttAgMBAAECggEAfY/wmTVUOYGfD/A86SNq9YYeebXl7oCfE/iaESjiJY1xm1sPS8Z4I+OCVJXIJibN6iC8NASbPKAeJnTKXke8r08DKeuwsH0g2u8fDb+BmanwOGKXvTXb8jexCotGe05BsF6u7r+cIuvQJLQ2XXncjuP8o5ypc6UWoyAYC8bvdSrZbGUwR1xRGTBLGjGYMaUWqLXmNS9MarapkCCfVf/SGWfZgV/CSPXlhczJ4EWYSUBbQTx262Nmj1nQaRCEPKauTQwOZhylI1Fy1s7Fdtz0mNjoGAXceAzkEjp7CwB5seA95VnRvfYMfnUHg0D3UX+pnrF53nXnB5AWVx1h6cqa8QKBgQDx7WUMpr0gg8YvEXNNKleDM7wCZroAr9FLSZQucbuV504oHjpTMWvov4VyQPKvXlVcmsxrxEB76a1RHIZvV5VhqEw0FU+wwpbJ1hJtVhq+sJ8vC4K92oORRJ6vn7akEtoXheEyEv4GbX7cqB0twPy4WgRNtzuQFMsRwvOjin5l7wKBgQCv4jQERagHsoh96COdylCMGmZjpIHrpxxDXaq0RRpl4Rscp9+O37xgAjY7nngA+/0UIG5ZAznG2ZEd50o8m2RFiJMCo+yyjLQQXWSEmdlHld2/gCl08HbHcReROD8hGBSerasCiDjGBbWrcHkSJxUm2fhdRzyGCy9P/Ixt0qeQYwKBgQCnX+NiTlVYknJhjAQmZd3nwroXOzUCVqFJIh2nyD3MpxuWe9tERVPv0dkkQ7TjCTOrftlg0YT/dZrYHBESJRkPWF1oRzo8A1RJTZawGDsmpJFs/dy1NuGBdmUHAktc1kIWtiQn/aXLkqOLS6/sQLFXZcfGcLd/pjMcFbFz+tQZ9wKBgDPOhVPc0KmzxNCRWCxaM2+RNyeShVJMXt7Y8Dc5Q5VOwRWnvQnwjSQagq789owTrXAIPXiqopIX887DGfLQMrDokaTtdAMDvuYzHDCSwJV9Z1vI/G0dh18nyLFVyAKPfUNgFEciqNZrSB+/eWoHaQuQmL+hBArXsI/268ZNjl3RAoGBALwIcC1DQi2w1JiC6jlesbO5fdolZ7tC0Z9QVpD0ewED0wQdMx28urwPR06Uc51TDzaZDNniqI8W5hBh1R2Q/t5Tpy/snEXX3hpU7i5IIFNqgIqh3BSsR8VBxlwztWjfAD2+iWAtb5X7o2tsAwsnisTmBbRwnybUfiW597Ea+HJZ";
	
	@Autowired
	PaymentDao paymentDao;

	@Autowired
	AgentDao agentDao;// ?????????

	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;// ?????????????????????

	@Autowired
	AgentAccountChargeDao agentAccountChargeDao;

	@Autowired
	AgentAccountDao agentAccountDao;

	/**
	 * ??????????????????????????????
	 * @return
	 */
	public String queryByName(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		try{
			Map<String, Object> m = getMaps(req); //?????????????????????map
			Page p = getPage(m);//??????????????????
			int total = paymentDao.selectCount(m);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			
			PageHelper.startPage(pageNum, pageSize);//mybatis????????????
			List<Map<String, Object>> dataList = paymentDao.selectAll(m);
			String dataListJson=BaseJson.listToJson(dataList);
			sb.append(dataListJson);
			sb.append("}");
		}catch(Exception e){
			getMyLog(e,log);
		}
		return sb.toString();
	}
	
	/**
	 * ??????????????????
	 * @param req
	 * @return
	 */
	public String WeChatPay(HttpServletRequest req) {
		Map<String, Object> user = getUser();
		Map<String, Object> map = getMaps(req);
		String money = map.get("money").toString();
		String agentId = agentDao.selectAgentIdByUserid(user.get("suId").toString());
		String type = map.get("type").toString();
		
		String body = "????????????"; // ????????????
		String mch_create_ip = "127.0.0.1";// ??????IP
		String mch_id = "102556452165";// ?????????
		String nonce_str = System.currentTimeMillis() + "";// ???????????????
		String notify_url = "http://118.31.229.23:8080/bosspaybill/status/returnUrl";// ????????????
		String out_trade_no = UUID.randomUUID().toString().replace("-", "");// ???????????????
		System.out.println(out_trade_no);
		String service = "pay.weixin.native";// ????????????
		String pay_type = "1";
		if(type.equals("1")) {
			service = "pay.alipay.native";
			pay_type = "2";
		}
		int fee = (int) ((Integer.parseInt(money) * 100) + (Integer.parseInt(money) * 100) * 0.005);
		//??????????????????1000?????????????????????????????????
		if(Integer.parseInt(money) >= 1000) {
			fee = (int) ((Integer.parseInt(money) * 100) + (Integer.parseInt(money) * 100) * 0.004);
		}
		//??????????????????3000?????????????????????????????????
		if(Integer.parseInt(money) >= 3000) {
			fee = (int) ((Integer.parseInt(money) * 100) + (Integer.parseInt(money) * 100) * 0.0035);
		}
		//????????????????????????????????????
		if(agentId.equals("bfbf863c209b4e35b13555682c6b49fa")) {
			fee = (int) ((Integer.parseInt(money) * 100) + (Integer.parseInt(money) * 100) * 0.0035);
		}
//		System.out.println(fee + "-  --" + agentId);
		String total_fee = fee+"";// ?????????
		String string1 = "body=" + body + "&charset=UTF-8" + "&mch_create_ip=" + mch_create_ip + "&mch_id=" + mch_id + "&nonce_str="
				+ nonce_str + "&notify_url=" + notify_url + "&out_trade_no=" + out_trade_no + "&service=" + service
				+"&sign_type=RSA_1_256" + "&total_fee=" + total_fee;
		log.error("????????????????????????????????????" + string1);
		String img = "";
		try {
			String sign = SignUtil.sign(string1,"RSA_1_256",private_key);// ??????
			log.error("????????????????????????????????????" + sign);
			String xml = payResult(service,"UTF-8",mch_id, out_trade_no, body, total_fee, mch_create_ip, notify_url, nonce_str,"RSA_1_256",sign);
			log.error("???????????????????????????????????????" + xml);
			Map<String, String> result = XmlUtils.readXmlToMap(xml);
			img = result.get("code_img_url") != null ? result.get("code_img_url") : "";
			if (img != null || img != "") {
				Map<String, String> addition = new HashMap<>();
				addition.put("id", UUID.randomUUID().toString().replace("-", ""));
				addition.put("user_ip", IPUtil.getIpAddr(req)); // ??????IP
				addition.put("agent_id", agentId); // ?????????ID
				addition.put("add_money", money); // ????????????
				addition.put("pay_money", total_fee); // ????????????
				addition.put("out_trade_no", out_trade_no); // ???????????????
				addition.put("create_date", DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")); // ????????????
				addition.put("state", "1");// ???????????? ?????????
				addition.put("pay_type", pay_type);// ???????????? ?????????
				List<Map<String, Object>> maps = paymentDao.SelectQueyByOne(addition);
				if (maps.size() <= 0) {
					paymentDao.insertSelective(addition);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			getMyLog(e, log);
		}
		return img;
	}

	/**
	 * ????????????
	 * @param req
	 * @return
	 */
	public String returnUrl(HttpServletRequest req) {
		try {
			String xml = getRequestContext(req);
			log.error("????????????????????????XMl?????????" + xml);
			Map<String,String> map = XmlUtils.readXmlToMap(xml);
			log.error("???????????????????????????????????????" + map);
			if(map.get("status").equals("0")) {
				String pay_money = map.get("total_fee");  //????????????	
				String pay_result = map.get("pay_result"); //????????????	
				Map<String, String> addition = new HashMap<>();
				addition.put("pay_money", pay_money); //????????????	
				addition.put("state", "1");//????????????  ????????? 
				List<Map<String, Object>> maps = paymentDao.SelectQueyByOne(addition);
				if(maps.size() > 0) {
					maps.forEach(map2 -> {
						map2.keySet().forEach(string -> {
							addition.put(string, map2.get(string).toString());
						});
						if(map2.get("out_trade_no").toString().equals(map.get("out_trade_no"))) {
//							String add_money = map2.get("add_money").toString();
							addition.put("out_transaction_id", map.get("out_transaction_id"));//?????????????????? 
							addition.put("time_end",map.get("time_end")); //?????????????????? 
							addition.put("pay_result", pay_result); //????????????	
							addition.put("transaction_id", map.get("transaction_id")); //???????????????   
							addition.put("pay_info", map.get("pay_info")); //?????????????????? 
							addition.put("openid", map.get("openid")); //????????????	
							addition.put("sub_openid", map.get("sub_openid")); //??????openid
//							Integer money = (int) ((Integer.parseInt(add_money) * 100) + (Integer.parseInt(add_money) * 100)*0.005);
							if(pay_result.equals("0") && pay_money.equals(map2.get("pay_money").toString())) {
								addition.put("state", "0");//????????????  ????????????
							}
							paymentDao.updateByPrimaryKeySelective(addition);
						}
					});
				}
			}else {
				return "fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			getMyLog(e, log);
		}
		return "success";
	}
	
	/**
	 * ???????????????????????????
	 *
	 * @return
	 */
	public String addAgentMoney(HttpServletRequest req) {
		String msg = "1";
		try {
			Map<String, Object> user = getUser();
			Map<String, Object> map = getMaps(req);
			String money = map.get("money").toString();
			String agentId = agentDao.selectAgentIdByUserid(user.get("suId").toString());
			Map<String, String> addition = new HashMap<>();
			addition.put("agent_id", agentId);
			List<Map<String, Object>> maps = paymentDao.SelectQueyByOne(addition);
			for (Map<String, Object> map2 : maps) {
				String state = map2.get("state").toString();
				if (map2.get("state").toString().equals("0")) {
					if (money.equals(map2.get("add_money"))) {
						Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(map2.get("agent_id").toString());
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						list = getChargeList(list, agent, map2.get("add_money").toString(),map2.get("id").toString());
						System.out.println(list);
						if (list.size() > 0) {
							if (allotBalance(list) > 0) {
								map2.put("state", "2"); // ???????????? ????????????
							} else {
								map2.put("state", "3"); // ???????????? ????????????
							}
						}
					}
				}
				List<Map<String, Object>> map4 = paymentDao.SelectQueyByOne(addition);
				if (map4.size() > 0 && !map2.get("state").toString().equals(state)) {
					paymentDao.updateByPrimaryKeySelective(map2);
					msg = "0";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			getMyLog(e, log);
		}
		return msg;
	}

	/**
	 * ??????????????????????????????????????????XML?????????????????????
	 *
	 * @return
	 */
	public String payResult(String service, String charset,String mch_id, String out_trade_no, String body, String total_fee,
			String mch_create_ip, String notify_url, String nonce_str,String sign_type, String sign) {
		String url = "https://pay.swiftpass.cn/pay/gateway";
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(url);
		String data = "<xml>" + "<service>" + service + "</service>"+ "<charset>" + charset + "</charset>" + "<mch_id>" + mch_id + "</mch_id>"
				+ "<out_trade_no>" + out_trade_no + "</out_trade_no>" + "<body>" + body + "</body>" + "<total_fee>"
				+ total_fee + "</total_fee>" + "<mch_create_ip>" + mch_create_ip + "</mch_create_ip>" + "<notify_url>"
				+ notify_url + "</notify_url>" + "<nonce_str>" + nonce_str + "</nonce_str>" +"<sign_type>" + sign_type + "</sign_type>" + "<sign>" + sign
				+ "</sign>" + "</xml>";
		log.error("???????????????????????????"+data);
		String info = null;
		try {
			RequestEntity entity = new StringRequestEntity(data, "text/plain", "utf-8");
			post.setRequestEntity(entity);
			httpclient.executeMethod(post);
			int code = post.getStatusCode();
			if (code == HttpStatus.SC_OK)
				info = new String(post.getResponseBodyAsString()); // ?????????????????????
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return info;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestContext(HttpServletRequest request) {
		String str = "";
		try {
			StringBuilder sb = new StringBuilder();
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			str = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * ???????????????????????????????????????
	 */
	public List<Map<String, Object>> getChargeList(List<Map<String, Object>> list, Map<String, Object> agent,
			String price,String id) {
		String agentId = (String) agent.get("id"); // ???????????????id
		String parentId = (String) agent.get("parent_id");// ?????????id
		if (agent != null) {
			Map<String, Object> discountMap = new HashMap<String, Object>();
			discountMap.put("agentId", agentId);
			discountMap.put("money", price);
			discountMap.put("agentOrderId", id);
			list.add(discountMap);
			if (!parentId.equals("0") && !parentId.trim().equals("")) {
				Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(parentId);
				list = getChargeList(list, parentAgent, price,id);
			}
		}
		return list;
	}

	/**
	 * ???????????????
	 *
	 */
	public int allotBalance(List<Map<String, Object>> maps) {
		int sum = 0;
		try {
			for (Map<String, Object> map1 : maps) {
				String agentId = map1.get("agentId").toString();
				String money = map1.get("money").toString();
				double beforeBalance = agentAccountDao.selectBalanceByAgentid(agentId);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("agentId", agentId);
				map.put("money", money);
				map.put("beforeBalance", beforeBalance);
				int num = agentAccountDao.addMoney(map);
				if (num > 0) {
					double afterBalance = beforeBalance + Double.parseDouble(money);// ?????????????????????????????????
					Map<String, Object> aacMap = new HashMap<String, Object>();
					String id = UUID.randomUUID().toString().replace("-", "");// ??????id
					aacMap.put("id", id);
					aacMap.put("agentId", agentId);
					aacMap.put("fee", money);
					aacMap.put("balanceBefore", beforeBalance);
					aacMap.put("balanceAfter", afterBalance);
					aacMap.put("type", "2");
					aacMap.put("status", "3");
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	                String times = timestamp.toString();
	                aacMap.put("applyDate",times);
					aacMap.put("agentOrderId", map1.get("agentOrderId"));
					int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// ?????????????????????
					if (aacFlag != 1) {
						log.error("??????????????????????????????");
					}
					sum += aacFlag;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			getMyLog(e, log);
		}
		return sum;
	}
	
	/**
	 * ????????????
	 * 
	 * @param str
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static String sign(String str, String privateKeyStr) {
		String sign = "";
		try{
			byte[] keyByteArray = Base64.decodeBase64(privateKeyStr);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			byte[] rsa = encodeBytePrivate(str.getBytes(), privateKey);
			sign = Hex.encodeHexString(rsa);
		}catch(Exception e){
			e.printStackTrace();
//			log.error(e.getMessage());
		}
		return sign;
	}

	/**
	 * RSA ????????????byte[]
	 * 
	 * @param content
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(content);
	}

}
