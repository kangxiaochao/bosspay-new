package com.hyfd.service.query;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.Base64Utils;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;
import com.sitech.miso.util.MD5;
import com.sitech.miso.util.StringUtil;

@Service
public class AiShiDeQuerySer extends BaseService {

	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	@Autowired
	OrderDao orderDao;

	@Autowired
	PhoneSectionDao phoneSectionDao;

	@Autowired
	ProviderDao providerDao;

	/**
	 * 获取余额信息 传入手机号
	 * 
	 * @param mobileNumber
	 * @return Map<String, String> amountInfoMap key:amount 余额 单位元 key:status
	 *         查询状态 0查询成功 1查询失败 key:phoneownername 机主姓名
	 */

	public Map<String, String> getChargeAmountInfo(String mobileNumber) {
		Map<String, String> amountInfoMap = new HashMap<String, String>();

		String id = "2000000001";
        Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
        String defaultParameter = (String)channel.get("default_parameter");// 默认参数
        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);

		String chargeAmountQryUrl = paramMap
				.get("chargeAmountQryUrl"); // 取出查询接口方法

		String appKey = paramMap.get("appKey");// 商家的APPKEY
		String timeStamp = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");// 时间戳
		String requestid = ToolDateTime.format(new Date(), "yyyyMMddHHmmss")
				+ getSixSquece() + appKey;// 请求流水号
		String requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");// 请求时间
		String requestServic = "sCustFeeQry";// 服务名称
		String staffcode = paramMap.get("staffCode");// 工号
		String paratype = "B";// A按账户查询，B按用户查询
		String servtype = "4";// 4：手机 5：宽带 2：固话，99其他；此处可填4或者99

		String sign = paramMap.get("key128bit");// 明文加密后的字符串

		amountInfoMap.put("appKey", appKey);
		amountInfoMap.put("timeStamp", timeStamp);
		amountInfoMap.put("requestId", requestid);
		amountInfoMap.put("requestTime", requesttime);
		amountInfoMap.put("requestServic", requestServic);
		amountInfoMap.put("accNbr", mobileNumber);
		amountInfoMap.put("staffCode", staffcode);
		amountInfoMap.put("paraType", paratype);
		amountInfoMap.put("servType", servtype);

		List list = new ArrayList();
		for (String key : amountInfoMap.keySet()) {
			list.add(key);
		}
		Collections.sort(list);
		String requestParameters = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == list.size() - 1) {
				requestParameters += list.get(i) + "="
						+ amountInfoMap.get(list.get(i));
			} else {
				requestParameters += list.get(i) + "="
						+ amountInfoMap.get(list.get(i)) + "&";
			}
		}

		String data = sendserchget(chargeAmountQryUrl, requestParameters);
		System.out.println("查询余额返回：" + data);
		JSONObject jsonObject = JSON.parseObject(data);// 返回查询结果
		String resCode = jsonObject.getString("resCode");// 返回000000为成功，否则失败
		Map<String, String> rMap = (Map<String, String>) jsonObject
				.get("out_data");
		String acctname = rMap.get("acctName");// 获取用户名

		if (jsonObject != null && resCode.equals("000000")) {// 判断
			amountInfoMap.put("status", "0");// 0为成功1为失败
			int servBalance = Integer.parseInt(rMap.get("servBalance"));
			int unBillfee  = Integer.parseInt(rMap.get("unBillfee"));
			amountInfoMap.put("amount",
					(Double.parseDouble((servBalance-unBillfee)+"") / 100)
							+ "");// 获取余额，单位为分

			if (acctname != null) {
				amountInfoMap.put("phoneownername", acctname);
			} else {
				amountInfoMap.put("phoneownername", "未知");
			}

		} else {
			amountInfoMap.put("status", "1");
			amountInfoMap.put("amount", "0");

		}
		return amountInfoMap;
	}

	/**
	 * 获取6位序列码
	 * 
	 * @return
	 */
	public static int getSixSquece() {
		return (int) ((Math.random() * 9 + 1) * 1000000);
	}

	public static String sendserchget(String url, String par) {

		StringBuffer paramBuffer = new StringBuffer();
		paramBuffer.append(par);

		String sign = toSign(par);

		paramBuffer.append("&sign=").append(sign);

		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
		} catch (URIException e) {
			e.printStackTrace();
		}
		url = url + "?" + queryString;
		System.out.println(url);
		String ret = "";
		ret = ToolHttps.get(false, url);
		return ret;

	}

	public static String toSign(String orginStr) {
		String skprivateKeyFile = "";
//		File f = new File(AiShiDeQuerySer.class.getResource("/").getPath());
//    	String courseFile="";
//		try {
//			courseFile = URLDecoder.decode(f.toString(),"utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
			
//		skprivateKeyFile = getRootPath();
		String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANwU81FFO0ODACC2" + 
				"+6lgsqSudetSuuyR5OLXNrOB9sn2TAkzwbWhOgYPTfnRcOE+3wRKxHyE9j6Qvoew" + 
				"T73BWMuQ9qJQOvvVpjGorZearBUrtKG9fcYDcrKhNsOGjOH8eO9D9Opj83vyVIXh" + 
				"lrvV2f5/YtFmMS7/RlbzJ2rivDqtAgMBAAECgYB9a2MOWYnge3NDMYRRjk3AE7wX" + 
				"k1n7H1l0/Hir69CcX8QNKnWl8G+ErIwfZgASHSYy5/j5jTxXg11RLmUDGoBi009s" + 
				"p6IwKM0mXwvkYIH+j3Q+QeDXAqMhHFCvRwVknPiYyRDuCFu1CFDP3FnLq87TFBot" + 
				"pNWLvsUWFn41bWEAcQJBAPYJwZOPPkCmL9OAZJ8/eP33ISH81Z4XbnyqDCmLziH+" + 
				"+I+ZmvQfXZUpufjOhiZwbuNpJFOtYo1Wok3BVAGckSsCQQDk/ia9uH3CpEXa3Suo" + 
				"qTy6Nbe65zqQjWM9BX6VQszvTj9ntlDvnGxsGOQ4jne5RAGXqX6UGM91d7EqusGO" + 
				"2oeHAkABbisW7YVCIPU0OJHdLyRH7bDenrarNZ2p0d9COpLXNcFCLHVvJ+OGY3i2" + 
				"TpUPEiZC4jCY3/ArvC4zX4VagQuDAkAA3AtdLvIZ5u/0MFxXl7sIn+b+ppuLq1wy" + 
				"AHFHMib+xvZp9z86hwXJKhbBN5evdFflL9evyqAMutRJasYLOTrFAkAgtpd8OqsO" + 
				"meb5EDbUJX2CJWjQyMEYG9BNB59vRvoFjATrEODvzcPurgArulbBq58X2Y9gzt6O" + 
				"FuRW9XaBJOso";
		PrivateKey skPrivateKey = null;
		try {
			byte[] keyBytes = Base64Utils.decode(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			skPrivateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//String courseFileStr = courseFile.substring(0, courseFile.indexOf("WEB-INF"));
		//skprivateKeyFile = courseFileStr+"pem\\jinanhaobai.pem";
//		PrivateKey skPrivateKey = RSAUtils.fileToPrivateKey(skprivateKeyFile);
		
		
		
		
		orginStr = StringUtil.sortOrginReqStr(orginStr);// 给参数字段顺序排序,toMD5本身并没有排序
		try {
			orginStr = URLEncoder.encode(orginStr, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String sign = "";
		try {
			String signStr = RSAUtils.sign(MD5.ToMD5(orginStr).getBytes(),
					skPrivateKey);
			sign = URLEncoder.encode(signStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sign;
	}
	
	public static String getRootPath() { 
		String classPath = AiShiDeQuerySer.class.getClassLoader().getResource("/").getPath();
        String rootPath = "";  
        //windows下  
        if("\\".equals(File.separator)){  
            //System.out.println("windows");  
        	rootPath = classPath.substring(1,classPath.indexOf("WEB-INF"));  
	        rootPath = rootPath.replace("/", "\\"); 
	        rootPath = rootPath+"pem\\jinanhaobai.pem";
        }  
        //linux下  
        if("/".equals(File.separator)){  
            //System.out.println("linux");  
        	rootPath = classPath.substring(0,classPath.indexOf("WEB-INF"));  
        	rootPath = rootPath.replace("\\", "/");
        	rootPath = rootPath+"pem/jinanhaobai.pem";
        }  
        return rootPath;  
    } 
}
