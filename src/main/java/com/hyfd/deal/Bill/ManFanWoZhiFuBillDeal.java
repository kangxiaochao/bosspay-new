package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class ManFanWoZhiFuBillDeal implements BaseDeal{
	
	private static Logger log = Logger.getLogger(ManFanWoZhiFuBillDeal.class);
	
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		try {
			String uid = order.get("phone")+"";											//手机号码
			String fee = order.get("fee")+"";											//充值金额
			fee = new Double(fee).intValue()+"";										//金额取整
			String providerId = order.get("providerId")+"";								//运营商
			String itemId = getItemId(fee,providerId);									//对应金额的产品代码
			if(itemId == null || itemId.equals("")) {
				log.error("满帆沃支付话费充值出错-运营商：" + providerId + " 充值金额： " + fee +" 手机号： "+ uid);
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
				log.error("满帆沃支付[话费充值]请求返回信息[" + utilsMap.toString() + "]");
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
				String msg = "满帆沃支付话费充值,号码[" + uid + "],金额[" + fee + "(元)],请求超时,未接收到返回数据";
				map.put("resultCode", msg);
				log.error(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("满帆沃支付话费充值出错" + e.getMessage() + MapUtils.toString(order));
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
	 * 获取对应价格的产品代码（三网）
	 * @param uid
	 * @param operator
	 * @return 返回空未查询到对应面值产品代码
	 */
	public static String getItemId(String fee,String operator) {
		//中国移动对应产品代码
		Map<String,String> moveMap = new HashMap<String, String>();
		moveMap.put("10","20057");
		moveMap.put("20","20058");
		moveMap.put("30","20059");
		moveMap.put("50","20060");
		moveMap.put("100","20061");
		moveMap.put("200","20062");
		moveMap.put("300","20063");
		moveMap.put("500","20064");
		//中国联通对应产品代码
		Map<String,String> linkMap = new HashMap<String, String>();
		linkMap.put("10","20066");
		linkMap.put("20","20067");
		linkMap.put("30","20068");
		linkMap.put("50","20069");
		linkMap.put("100","20070");
		linkMap.put("200","20071");
		linkMap.put("300","20072");
		linkMap.put("500","20073");
		if(operator.equals("0000000001")) { 		//中国移动
			return moveMap.get(fee);
		}else if(operator.equals("0000000002")) {   //中国联通
			return linkMap.get(fee);
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
}
