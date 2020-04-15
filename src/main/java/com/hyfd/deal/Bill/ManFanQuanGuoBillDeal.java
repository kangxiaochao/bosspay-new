package com.hyfd.deal.Bill;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.deal.BaseDeal;

public class ManFanQuanGuoBillDeal implements BaseDeal{
	
	private static Logger log = Logger.getLogger(ManFanQuanGuoBillDeal.class);
	
	@Autowired
	private PhoneSectionDao phoneSectionDao;
	
	//中国移动各省份对应的产品代码
	static Map<String,Map<String,String>> moveMap = new HashMap<String, Map<String,String>>();
	static Map<String,String> map1 = new HashMap<String, String>();
	static Map<String,String> map2 = new HashMap<String, String>();

	static {
		map1.put("10","21530");
		map1.put("20","21531");
		map1.put("30","21532");
		map1.put("50","21533");
		map1.put("100","21534");
		map1.put("200","21535");
		map1.put("300","21536");
		map1.put("500","21537");
		//湖北 浙江 陕西 山西 贵州 云南 吉林 内蒙古 新疆 黑龙江
		moveMap.put("湖北",map1);
		moveMap.put("浙江",map1);
		moveMap.put("山西",map1);
		moveMap.put("陕西",map1);
		moveMap.put("贵州",map1);
		moveMap.put("云南",map1);
		moveMap.put("吉林",map1);
		moveMap.put("内蒙古",map1);
		moveMap.put("新疆",map1);
		moveMap.put("黑龙江",map1);
		map2.put("10","21538");
		map2.put("20","21539");
		map2.put("30","21540");
		map2.put("50","21541");
		map2.put("100","21542");
		map2.put("200","21543");
		map2.put("300","21544");
		map2.put("500","21545");
		//西藏 江苏 山东 广东 广西 甘肃 河北 重庆 辽宁 四川 安徽 福建 江西 湖南 河南 青海 宁夏 北京 上海 天津 海南地区提交这个编码
		moveMap.put("西藏",map2);
		moveMap.put("江苏",map2);
		moveMap.put("山东",map2);
		moveMap.put("广东",map2);
		moveMap.put("广西",map2);
		moveMap.put("甘肃",map2);
		moveMap.put("河北",map2);
		moveMap.put("重庆",map2);
		moveMap.put("辽宁",map2);
		moveMap.put("四川",map2);
		moveMap.put("安徽",map2);
		moveMap.put("福建",map2);
		moveMap.put("江西",map2);
		moveMap.put("湖南",map2);
		moveMap.put("河南",map2);
		moveMap.put("青海",map2);
		moveMap.put("宁夏",map2);
		moveMap.put("北京",map2);
		moveMap.put("上海",map2);
		moveMap.put("天津",map2);
		moveMap.put("海南",map2);
	}
	
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
			String itemId = getItemId(fee,uid,providerId);								//对应金额与省份的产品代码
			if(itemId == null || itemId.equals("")) {
				log.error("满帆分省话费充值查询号码归属地出错-运营商：" + providerId + " 充值金额： " + fee +" 手机号： "+ uid);
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
	 * 获取对应省份与价格的产品代码
	 * @param uid
	 * @param operator
	 * @return 返回空未查询到对应面值产品代码
	 */
	public String getItemId(String fee,String phone,String providerId) {
		String section = phone.substring(0, 7);// 获取号段
		Map<String, Object> sectionMessage = phoneSectionDao
				.selectBySection(section);
		String province_code = (String) sectionMessage.get("province_code");
		if(province_code == null || province_code.equals("")) {
			return "";
		}
		if(providerId.equals("0000000001")) { 		//中国移动
			String itemId = moveMap.get(province_code).get(fee);
			return itemId;
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
