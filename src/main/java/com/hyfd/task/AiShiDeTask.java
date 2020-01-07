package com.hyfd.task;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.RSAUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.Bill.AishideBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.query.AiShiDeQuerySer;

@Component
public class AiShiDeTask
{
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(AiShiDeTask.class);
    
    static Map<String, String> rltMap = new HashMap<String, String>();
	static Map<String, String> stateMap = new HashMap<String, String>();
	static {
		rltMap.put("000000", "成功");
		rltMap.put("S999001", "获取用户真实号码错误");
		rltMap.put("S999002", "请求参数中接口版本号不合法");
		rltMap.put("S999003", "校验请求IP地址不合法");
		rltMap.put("S999004", "请求参数中签名不合法");
		rltMap.put("S999005", "请求参数中订单不存在");
		rltMap.put("S999006", "请求参数中有不合法值");
		rltMap.put("E100006", "无优惠信息");
		rltMap.put("E130223", "取付费账户标志出错");
		rltMap.put("E130298", "取员工区域标识失败");
		rltMap.put("E200001", "工号[xxxx]没有操作[xxxx]的权限");
		rltMap.put("E230113", "重复缴费");
		rltMap.put("E700001", "invalidRoute");
		rltMap.put("E990000", "获取用户真实号码错误");
		rltMap.put("X000001", "开通号码的号段不合法");
		rltMap.put("X000002", "该号码已经注册过迅雷帐号");
		rltMap.put("X000003", "该号码没有注册过迅雷帐号");
		rltMap.put("X000004", "该号码对应的迅雷帐号本月已经开通过会员");
		rltMap.put("X000005", "请求timeStame时间错误！");
		rltMap.put("X999999", "系统错误");
		rltMap.put("990002", "不是在网用户，不能缴费");
		rltMap.put("100001", "未返档状态，不能缴费");
		rltMap.put("100003", "挂失状态，不能缴费");
		rltMap.put("100005", "黑名单状态，不能缴费");
		rltMap.put("100006", "未开通状态，不能缴费");

		stateMap.put("0", "订单已创建");
		stateMap.put("1", "待充值");
		stateMap.put("2", "订单已取消");
		stateMap.put("3", "充值中，运营商已接受请求，但未返回成功充值结果；或代表一个临时性的可短时间恢复的错误。");
		stateMap.put("4", "充值成功");
		stateMap.put("5", "充值失败，且无法重试");
		stateMap.put("6", "请求失败，任何签名错误、缺少必要参数等的协议类错误，都应该返回该类错误");
		stateMap.put("7", "创建订单失败");
	}
    
    @Scheduled(fixedDelay = 60000)
    public void queryAiShiDeOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        try
        {
            String id = "2000000001";
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryUrl = paramMap.get("queryUrl") + "";// 查询地址
            String appKey = paramMap.get("appKey"); //用户编号
            String staffCode = paramMap.get("staffCode"); //加密密码
            String orgCode = paramMap.get("orgCode");// 加密秘钥 由爱施德提供
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            int num=999999;
            for (Map<String, Object> order : orderList)
            {
            	int flag = 2;
            	num--;
                //String curids = order.get("curids") + "";// 上家订单号
                String timeStamp=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String requestId=timeStamp+num+"01";
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                
                String searchResponseXml = sendSearch(queryUrl, appKey, timeStamp, staffCode, orgCode, requestId, timeStamp, orderId);
               
                if (searchResponseXml != null && !searchResponseXml.equals(""))
                {
                	 JSONObject searchResult = null;
                     searchResult =JSONObject.parseObject(searchResponseXml);
         			String resmessage = searchResult.getString("resCode");//错误提示       0 无错误	请求已被后台接收
//         			String searchState = searchResult.getJSONObject("out_data").getString("state").replaceAll("[^\\d]", "");//订单状态        8等待扣款，0充值中，1充值成功，2充值失败
                    if ("888888".equals(resmessage))
                    {// 充值中
                        continue;
                    }
                    else if ("000000".equals(resmessage))
                    {// 充值成功
                    	flag = 1;
                    }
                    else if ("999999".equals(resmessage))
                    {// 充值失败
                        flag = 0;
                    }else {
                    	log.error("爱施德话费查询异常");
                    	continue;
					}
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("爱施德话费查询Task出错" + e);
        }
    }
    
    public String toSign(String orginStr) {
    	
    	String skprivateKeyFile ="";
//    	File f = new File(this.getClass().getResource("/").getPath());
//    	String courseFile="";
//		try {
//			courseFile = URLDecoder.decode(f.toString(),"utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//    	String courseFileStr = courseFile.substring(0, courseFile.indexOf("WEB-INF"));
//    	skprivateKeyFile = courseFileStr+"pem\\jinanhaobai.pem";
    	skprivateKeyFile = getRootPath();
		PrivateKey skPrivateKey = RSAUtils.fileToPrivateKey(skprivateKeyFile);
		orginStr = StringUtil.sortOrginReqStr(orginStr);// 给参数字段顺序排序,toMD5本身并没有排序
		try {
			orginStr = URLEncoder.encode(orginStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("aishide urlencoder error for sign|"+e.getMessage());
		}
		String sign = "";
		try {
			String signStr = RSAUtils.sign(MD5.ToMD5(orginStr).getBytes(), skPrivateKey);
			sign = URLEncoder.encode(signStr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("aishide urlencoder error for sign|"+e.getMessage());
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
    
	public String sendSearch(String url, String appKey, String timeStamp, String staffCode, String orgCode,String requestId
			,String requestTime,String paySign) {

		StringBuffer paramBuffer = new StringBuffer();

		//userid=xxx&pwd=xxx&orderid=xxx&account=xxx&=xxx&amount=1&userkey=xxx
		paramBuffer.append("appKey=").append(appKey);
		paramBuffer.append("&timeStamp=").append(timeStamp);
		paramBuffer.append("&staffCode=").append(staffCode);
		paramBuffer.append("&orgCode=").append(orgCode);
		paramBuffer.append("&requestId=").append(requestId);
		paramBuffer.append("&requestTime=").append(requestTime);
		paramBuffer.append("&paySign=").append(paySign);
		

		String signone = "appKey=" + appKey + "&timeStamp=" + timeStamp + "&staffCode=" + staffCode + "&orgCode="+orgCode+"&requestId="+requestId+"&requestTime="+requestTime+"&paySign="+paySign;
		String encodesign = null;
		try {
			encodesign = URLEncoder.encode(signone,"utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String Sign = toSign(signone);
		paramBuffer.append("&sign=").append(Sign);
		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		url = url + "?" + queryString;
		log.info(url);
		String ret = "";
		ret = ToolHttp.get1(false, url);
		return ret;

	}
    
}
