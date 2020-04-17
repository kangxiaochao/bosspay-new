package com.hyfd.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.math.RandomUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;

public class ManFanTest {
	
	
	//中国移动各省份对应的产品代码
		static Map<String,Map<String,String>> moveMap = new HashMap<String, Map<String,String>>();
		static Map<String,String> ganshuMap = new HashMap<String, String>();
		static Map<String,String> hubeiMap = new HashMap<String, String>();
		static Map<String,String> jiangsuMap = new HashMap<String, String>();
		static Map<String,String> shandongMap = new HashMap<String, String>();
		static Map<String,String> liaoningMap = new HashMap<String, String>();
		static Map<String,String> jilinMap = new HashMap<String, String>();
		static Map<String,String> tianjingMap = new HashMap<String, String>();
		static {
			ganshuMap.put("10","20513");
			ganshuMap.put("20","20514");
			ganshuMap.put("30","20515");
			ganshuMap.put("50","20516");
			ganshuMap.put("100","20517");
			ganshuMap.put("200","20518");
			ganshuMap.put("300","20519");
			ganshuMap.put("500","20520");
			moveMap.put("甘肃", ganshuMap);
			hubeiMap.put("10","20465");
			hubeiMap.put("20","20466");
			hubeiMap.put("30","20467");
			hubeiMap.put("50","20468");
			hubeiMap.put("100","20469");
			hubeiMap.put("200","20470");
			hubeiMap.put("300","20471");
			hubeiMap.put("500","20472");
			moveMap.put("湖北", hubeiMap);
			jiangsuMap.put("10","20263");
			jiangsuMap.put("20","20264");
			jiangsuMap.put("30","20265");
			jiangsuMap.put("50","20266");
			jiangsuMap.put("100","20267");
			jiangsuMap.put("200","20268");
			jiangsuMap.put("300","20269");
			jiangsuMap.put("500","20270");
			moveMap.put("江苏", jiangsuMap);
			shandongMap.put("10","20247");
			shandongMap.put("20","20248");
			shandongMap.put("30","20249");
			shandongMap.put("50","20250");
			shandongMap.put("100","20251");
			shandongMap.put("200","20252");
			shandongMap.put("300","20253");
			shandongMap.put("500","20254");
			moveMap.put("山东", shandongMap);
			liaoningMap.put("10","20215");
			liaoningMap.put("20","20216");
			liaoningMap.put("30","20217");
			liaoningMap.put("50","20218");
			liaoningMap.put("100","20219");
			liaoningMap.put("200","20220");
			liaoningMap.put("300","20221");
			liaoningMap.put("500","20222");
			moveMap.put("辽宁", liaoningMap);
			jilinMap.put("10","20239");
			jilinMap.put("20","20240");
			jilinMap.put("30","20241");
			jilinMap.put("50","20242");
			jilinMap.put("100","20243");
			jilinMap.put("200","20244");
			jilinMap.put("300","20245");
			jilinMap.put("500","20246");
			moveMap.put("吉林", jilinMap);
			tianjingMap.put("10","20356");
			tianjingMap.put("20","20357");
			tianjingMap.put("30","20358");
			tianjingMap.put("50","20359");
			tianjingMap.put("100","20360");
			tianjingMap.put("200","20361");
			tianjingMap.put("300","20362");
			tianjingMap.put("500","20363");
			moveMap.put("天津", tianjingMap);
		}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String uid = "17705305254";													//充值手机号
		String fee =  "30.0"; 														//充值金额
		fee = new Double(fee).intValue()+"";
//		String providerId = order.get("providerId")+"";								//运营商
		String providerId = "0000000003";
		String userId = "1234";														//用户编码
//		String itemId = getItemId(fee,providerId);									//对应金额的产品代码
		String checkItemFacePrice = new Double(fee).intValue()*1000+"";				//充值金额（单位厘 1元=1000厘）
		String serialno = userId + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);	//商户流水订单号
		String dtCreate = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss"); 	// 系统时间
		String signKey = "cdb48793aac75fc4c15c97850dbca9a962bd786d45be3ab0a1668c39f459e5e9";
//		String sign = getSign(uid,userId,itemId,checkItemFacePrice,serialno,dtCreate,signKey);
//		System.out.println(itemId+"  "+checkItemFacePrice);
//		String notifyURL = "http://39.108.59.67:7660/unicomAync/buy.do?sign="+sign+"&uid="+uid+"&dtCreate="+dtCreate+"&userId="+userId
//				+"&itemId="+itemId+"&serialno="+serialno+"&checkItemFacePrice="+checkItemFacePrice;
//		String result = ToolHttp.get(false,notifyURL);
//		System.out.println(result);
//		Map<String,String> paramMap = XmlUtils.readXmlToMap(result);
//		System.out.println("返回结果 : "+paramMap.get("code")+"  -  "+paramMap.get("desc"));
//		query();
		aa();
	}
	
	public static void aa() {
		try {
			HttpClient httpClient = new HttpClient();
			PostMethod loginMethod = new PostMethod("http://jiaofei.jiatuo100.com/BelongingTo/queryBysection");
			NameValuePair[] transferParam = {
	                new NameValuePair("section", "17705305254"),
	        };
			loginMethod.setRequestBody(transferParam);
			httpClient.executeMethod(loginMethod);
			BufferedReader reader = new BufferedReader(new InputStreamReader(loginMethod.getResponseBodyAsStream()));  
			StringBuffer stringBuffer = new StringBuffer();  
			String str = "";  
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
			JSONObject jsonStatus = JSONObject.parseObject(stringBuffer.toString());
			System.out.println(jsonStatus.getString("code"));
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void query() {
		String userId = "753";														//用户编码
		String serialno = "7532020031717314514583134";	//商户流水订单号
		String signKey = "ff2b34974a5808e2b1fe5e1adb77b4b029d584e1433354944b6fc3d266d39a1f";
		String sign = md5Encode(serialno+userId+signKey);
		String queryURL = "http://47.93.197.171:8760/unicomAync/queryBizOrder.do?sign="+sign+"&userId="+userId+"&serialno="+serialno;
		String result = ToolHttp.get(false,queryURL);
		Map<String,String> paramMaps = readXmlToMap(result);	
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
		moveMap.put("30","20015");
		moveMap.put("50","20017");
		moveMap.put("100","20018");
		moveMap.put("200","20019");
		moveMap.put("300","20020");
		moveMap.put("500","20021");
		//中国联通对应产品代码
		Map<String,String> linkMap = new HashMap<String, String>();
		linkMap.put("10","20005");
		linkMap.put("20","20006");
		linkMap.put("30","20007");
		linkMap.put("50","20008");
		linkMap.put("100","20009");
		linkMap.put("200","20010");
		linkMap.put("300","20011");
		linkMap.put("500","20012");
		//中国电信对应产品代码
		Map<String,String> telecomMap = new HashMap<String, String>();
		telecomMap.put("10","20050");
		telecomMap.put("20","20051");
		telecomMap.put("30","20052");
		telecomMap.put("50","20053");
		telecomMap.put("100","20054");
		telecomMap.put("200","20055");
		telecomMap.put("300","20056");
		telecomMap.put("500","20057");
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
