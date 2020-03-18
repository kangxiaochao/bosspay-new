package com.hyfd.test;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.math.RandomUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;

public class ManFanTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String uid = "17705305254";													//充值手机号
		String fee =  "10"; 														//充值金额
//		String providerId = order.get("providerId")+"";								//运营商
		String providerId = "0000000003";
		String userId = "753";														//用户编码
		String itemId = getItemId(fee,providerId);									//对应金额的产品代码
		String checkItemFacePrice = (Integer.parseInt(fee)*1000)+"";				//充值金额（单位厘 1元=1000厘）
		String serialno = userId + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);	//商户流水订单号
		String dtCreate = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss"); 	// 系统时间
		String signKey = "ff2b34974a5808e2b1fe5e1adb77b4b029d584e1433354944b6fc3d266d39a1f";
		String sign = getSign(uid,userId,itemId,checkItemFacePrice,serialno,dtCreate,signKey);
//		String notifyURL = "http://47.93.197.171:8760/unicomAync/buy.do?sign="+sign+"&uid="+uid+"&dtCreate="+dtCreate+"&userId="+userId
//				+"&itemId="+itemId+"&serialno="+serialno+"&checkItemFacePrice="+checkItemFacePrice;
//		//<?xml version="1.0" encoding="UTF-8" standalone="yes"?><response><code>00</code><desc>交易成功</desc><status>success</status><amount>9745</amount><areaCode>370000</areaCode><bizOrderId>94998674</bizOrderId><carrierType>2</carrierType><itemFacePrice>10000</itemFacePrice><itemId>20023</itemId><itemName>全国电信无票资源10元</itemName><price>9745</price><serialno>7532020031716351210411077</serialno></response>
//		String result = ToolHttp.get(false,notifyURL);
//		Map<String,String> paramMap = XmlUtils.readXmlToMap(result);
//		System.out.println("返回结果 : "+paramMap.get("code")+"  -  "+paramMap.get("desc"));
		query();
	}
	
	public static void query() {
		String userId = "753";														//用户编码
		String serialno = "7532020031717314514583134";	//商户流水订单号
		String signKey = "ff2b34974a5808e2b1fe5e1adb77b4b029d584e1433354944b6fc3d266d39a1f";
		String sign = md5Encode(serialno+userId+signKey);
		String queryURL = "http://47.93.197.171:8760/unicomAync/queryBizOrder.do?sign="+sign+"&userId="+userId+"&serialno="+serialno;
		String result = ToolHttp.get(false,queryURL);
		Map<String,String> paramMaps = readXmlToMap(result);
		System.out.println(paramMaps.toString());
		System.out.println("返回结果 : "+paramMaps.get("status")+"  -  "+paramMaps.get("statusDesc"));
	}
	
	/**
	 * 拼接获取sign
	 * @param uid
	 * @param userId
	 * @param itemId
	 * @param checkItemFacePrice
	 * @param serialno
	 * @param dtCreate
	 * @param signKey
	 * @return
	 */
	public static String getSign(String uid,String userId,String itemId,String checkItemFacePrice,String serialno,String dtCreate,String signKey) {
		Map<String, String> map = new TreeMap<>();
        //用户编号
        map.put("userId", userId);
        //商品编号
        map.put("itemId", itemId);
        //校验商品面值,单位厘,比如10元话费就传10000
        map.put("checkItemFacePrice", checkItemFacePrice);
        //充值号码
        map.put("uid", uid);
        //合作方商户系统的流水号
        map.put("serialno", serialno);
        //交易时间,yyyyMMddHHmmss格式
        map.put("dtCreate",dtCreate);
        StringBuilder sb = new StringBuilder();
        //循环拼接所有value
        for (String k : map.keySet()) {
            sb.append(map.get(k));
        }
        //最后拼接上秘钥
        sb.append(signKey);
        
        System.out.println(sb.toString());
        System.out.println(md5Encode(sb.toString()));
		return md5Encode(sb.toString());
	}
	
	/**
	 * 获取对应价格的产品代码（三网）
	 * @param uid
	 * @param operator
	 * @return 返回空未查询到对应面值产品代码
	 */
	public static String getItemId(String fee,String operator) {
		//中国移动对应产品代码
		Map<String,String> moveMap = new HashMap<String, String>();
		moveMap.put("10","20049");
		moveMap.put("20","20076");
		moveMap.put("30","20077");
		moveMap.put("50","20074");
		moveMap.put("100","20075");
		moveMap.put("200","20078");
		moveMap.put("300","20079");
		moveMap.put("500","20080");
		//中国联通对应产品代码
		Map<String,String> linkMap = new HashMap<String, String>();
		linkMap.put("10","20081");
		linkMap.put("20","20082");
		linkMap.put("30","20083");
		linkMap.put("50","20084");
		linkMap.put("100","20085");
		linkMap.put("200","20086");
		linkMap.put("300","20087");
		linkMap.put("500","20088");
		//中国电信对应产品代码
		Map<String,String> telecomMap = new HashMap<String, String>();
		telecomMap.put("10","20023");
		telecomMap.put("20","20024");
		telecomMap.put("30","20025");
		telecomMap.put("50","20026");
		telecomMap.put("100","20027");
		telecomMap.put("200","20028");
		telecomMap.put("300","20029");
		telecomMap.put("500","20030");
		if(operator.equals("0000000001")) {
			return moveMap.get(fee);
		}else if(operator.equals("0000000002")) {
			return linkMap.get(fee);
		}else if(operator.equals("0000000003")) {
			return telecomMap.get(fee);
		}
		return "";
	}

	/**
	 * MD5加密 
	 * @param str
	 * @return 加密后小写
	 */
	public static String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
			String s = Integer.toHexString(bytes[i] & 0xff);
			if(s.length()==1){
			buf.append("0");
			}
			buf.append(s);
		}
		}
		catch(Exception ex){	
		}
		return buf.toString();
	}
	
	
	public static Map<String, String> readXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); 		// 将字符串转为XML
			Element rootElt = doc.getRootElement(); 	// 获取根节点
			List<Element> l = rootElt.elements();
			Element contactElem = rootElt.element("data");
		    List<Element> contactList = contactElem.elements();
		    for (Element e:contactList){
		    	map.put(e.getName(), e.getStringValue());
		    }    
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
