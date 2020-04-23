package com.hyfd.deal.Bill;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ManFanFenShengBillDeal implements BaseDeal{
	
	private static Logger log = Logger.getLogger(ManFanFenShengBillDeal.class);
	
	//中国移动各省份对应的产品代码
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
		hubeiMap.put("10","20465");
		hubeiMap.put("20","20466");
		hubeiMap.put("30","20467");
		hubeiMap.put("50","20468");
		hubeiMap.put("100","20469");
		hubeiMap.put("200","20470");
		hubeiMap.put("300","20471");
		hubeiMap.put("500","20472");
		jiangsuMap.put("10","20263");
		jiangsuMap.put("20","20264");
		jiangsuMap.put("30","20265");
		jiangsuMap.put("50","20266");
		jiangsuMap.put("100","20267");
		jiangsuMap.put("200","20268");
		jiangsuMap.put("300","20269");
		jiangsuMap.put("500","20270");
		shandongMap.put("10","20247");
		shandongMap.put("20","20248");
		shandongMap.put("30","20249");
		shandongMap.put("50","20250");
		shandongMap.put("100","20251");
		shandongMap.put("200","20252");
		shandongMap.put("300","20253");
		shandongMap.put("500","20254");
		liaoningMap.put("10","20215");
		liaoningMap.put("20","20216");
		liaoningMap.put("30","20217");
		liaoningMap.put("50","20218");
		liaoningMap.put("100","20219");
		liaoningMap.put("200","20220");
		liaoningMap.put("300","20221");
		liaoningMap.put("500","20222");
		jilinMap.put("10","20239");
		jilinMap.put("20","20240");
		jilinMap.put("30","20241");
		jilinMap.put("50","20242");
		jilinMap.put("100","20243");
		jilinMap.put("200","20244");
		jilinMap.put("300","20245");
		jilinMap.put("500","20246");
		tianjingMap.put("10","20356");
		tianjingMap.put("20","20357");
		tianjingMap.put("30","20358");
		tianjingMap.put("50","20359");
		tianjingMap.put("100","20360");
		tianjingMap.put("200","20361");
		tianjingMap.put("300","20362");
		tianjingMap.put("500","20363");
	}
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String uid = order.get("phone")+"";											//手机号码
			String fee = order.get("fee")+"";											//充值金额
			String provinceCode = order.get("channelProvinceCode")+"";					//省份
			fee = new Double(fee).intValue()+"";										//金额取整
			String itemId = getItemId(fee,provinceCode);								//对应金额与省份的产品代码
			if(itemId == null || itemId.equals("")) {
				log.error("满帆分省话费充值查询号码归属地出错-充值金额： " + fee +" 手机号： "+ uid + "省份" + provinceCode + "产品代码：" + itemId);
			}
			String checkItemFacePrice = new Double(fee).intValue()*1000+"";				//充值金额（单位厘 1元=1000厘）
			String dtCreate = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss"); 	//系统时间
			Map<String, Object> channel = (Map<String, Object>)order.get("channel");	//获取通道参数
	        String linkUrl = (String)channel.get("link_url");							//充值地址
	        String defaultParameter = (String)channel.get("default_parameter");			//默认参数
	        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
	        String userId = paramMap.get("userId");										//用户编码
			String signKey = paramMap.get("apiKey");
			String serialno = userId + ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000);//商户流水订单号
			map.put("orderId",serialno);
			String sign = getSign(uid,userId,itemId,checkItemFacePrice,serialno,dtCreate,signKey); 
			String notifyURL = linkUrl+"?sign="+sign+"&uid="+uid+"&dtCreate="+dtCreate+"&userId="+userId
					+"&itemId="+itemId+"&serialno="+serialno+"&checkItemFacePrice="+checkItemFacePrice;
			String result = ToolHttp.get(false,notifyURL);
			if(result != null && !(result.equals(""))) {
				Map<String,String> utilsMap = XmlUtils.readXmlToMap(result);
				log.error("满帆分省[话费充值]请求返回信息[" + utilsMap.toString() + "]");
				if(utilsMap.get("code").equals("00")) {
					map.put("resultCode", utilsMap.get("code")+" : "+utilsMap.get("desc"));			//执行结果说明
					map.put("providerOrderId",utilsMap.get("bizOrderId"));							//返回的是上家订单号
					flag = 1;	// 充值成功
				}else {
					map.put("resultCode", utilsMap.get("code")+":"+utilsMap.get("desc"));
					flag = 0;	// 提交异常
				}
			}else {
				// 请求超时,未获取到返回数据
				flag = -1;
				String msg = "满帆分省话费充值,号码[" + uid + "],金额[" + fee + "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("满帆分省话费充值出错" + e.getMessage() + MapUtils.toString(order));
		}
		map.put("status",flag);				
		return map;
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
		return md5Encode(sb.toString());
	}

	/**
	 * 获取对应省份与价格的产品代码
	 * @param uid
	 * @param operator
	 * @return 返回空未查询到对应面值产品代码
	 */
	public String getItemId(String fee,String provinceCode) {
		String itemId = "";
		try {
			if(provinceCode.equals("甘肃")) {
				itemId = ganshuMap.get(fee);
			}else if(provinceCode.equals("湖北")) {
				itemId = hubeiMap.get(fee);
			}else if(provinceCode.equals("江苏")) {
				itemId = jiangsuMap.get(fee);
			}else if(provinceCode.equals("山东")) {
				itemId = shandongMap.get(fee);
			}else if(provinceCode.equals("辽宁")) {
				itemId = liaoningMap.get(fee);
			}else if(provinceCode.equals("吉林")) {
				itemId = jilinMap.get(fee);
			}else if(provinceCode.equals("天津")) {
				itemId = tianjingMap.get(fee);
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("满帆分省话费充值出错" + e.toString());
		}
		return itemId;
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
}
