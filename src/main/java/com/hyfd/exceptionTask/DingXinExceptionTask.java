package com.hyfd.exceptionTask;

import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class DingXinExceptionTask implements BaseTask {

	Logger log = Logger.getLogger(DingXinExceptionTask.class);

	static Map<String, String> rltMap = new HashMap<String, String>();
	static Map<String, String> stateMap = new HashMap<String, String>();
	static {
		rltMap.put("0", "无错误	请求已被后台接收");
		rltMap.put("1003", "用户ID错误	充值时可失败，查询时需人工处理");
		rltMap.put("1004", "用户IP错误	充值时可失败，查询时需人工处理");
		rltMap.put("1005", "用户接口已关闭	充值时可失败，查询时需人工处理");
		rltMap.put("1006", "加密结果错误	充值时可失败，查询时需人工处理");
		rltMap.put("1007", "订单号不存在	需在该订单提交2分钟后方可处理失败");
		rltMap.put("1011", "号码归属地未知	充值时可失败，查询时需人工处理");
		rltMap.put("1013", "手机对应的商品有误或者没有上架	充值时可失败，查询时需人工处理");
		rltMap.put("1014", "无法找到手机归属地	充值时可失败，查询时需人工处理");
		rltMap.put("1015", "余额不足	充值时可失败，查询时需人工处理");
		rltMap.put("1016", "QQ号格式错误	充值时可失败");
		rltMap.put("1017", "产品未分配用户，联系商务	充值时可失败");
		rltMap.put("1018", "订单生成失败	充值时可失败");
		rltMap.put("1019", "充值号码与产品不匹配	充值时可失败");
		rltMap.put("1020", "号码运营商未知	充值时可失败");
		rltMap.put("9998", "参数有误	充值时可失败");
		rltMap.put("9999", "系统错误 人工处理");

		stateMap.put("8", "等待扣款");
		stateMap.put("0", "充值中");
		stateMap.put("1", "充值成功");
		stateMap.put("2", "充值失败");
	}

	@Override
	public Map<String, Object> task(Map<String, Object> order, Map<String, Object> channelMap) {
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			String linkUrl = channelMap.get("link_url") + "";
			String defaultParameter = (String)channelMap.get("default_parameter");// 默认参数
			Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
			String searchUrl = paramMap.get("searchUrl");
			String userId = paramMap.get("userid");// 用户编号	由鼎信商务提供，如：HR0001
			String pwd = paramMap.get("pwd");// 加密密码	由鼎信商务提供。（需大写）
			String key = paramMap.get("key");// 加密秘钥 由鼎信商务提供
			//获取订单信息
			String orderId = order.get("orderId") != null ? order.get("orderId")+"" : "";
			map.put("orderId", orderId);
			map.put("agentOrderId",order.get("agentOrderId") != null ? order.get("agentOrderId") : "");
			map.put("providerOrderId",order.get("providerOrderId") != null ? order.get("providerOrderId") : "");
			map.put("phone",order.get("phone"));
			map.put("fee",order.get("fee"));
			String resultXml = sendSearch(searchUrl,userId,pwd,orderId,key);
			log.error("鼎信话费充值查单返回结果:"+resultXml);
			if(resultXml == null || resultXml.equals("")){
				map.put("status", 3);
				map.put("resultCode", "查询接口返回信息为空！");
				return map;
			}
			Map<String, String> searchResult = readXmlToMapFromCreateResponse(resultXml);
			String searchError = searchResult.get("error");//错误提示       0 无错误	请求已被后台接收
			String searchState = searchResult.get("state");//订单状态        8等待扣款，0充值中，1充值成功，2充值失败
			String searchStateMsg = stateMap.get(searchState);//获取订单状态
			String searchErrorMsg = rltMap.get(searchError);//错误信息描述
			String submitbackmsg = "error:" + searchError + " searchErrorMsg" +searchErrorMsg+ ",state:" + searchState + " " + searchStateMsg;
			if("8".equals(searchState) || "0".equals(searchState) ) { //8等待扣款 0充值中
				//处理中状态 正在处理
				map.put("status", 2);
				map.put("resultCode", "充值中！");
			}else if("2".equals(searchState) || ("1007".equals(searchError)  || "9999".equals(searchError))){
				//(充值失败 返回2 ) 或者 (订单不存在、错误 重复请求超过次数的)。
				map.put("status", 0);
				map.put("resultCode", "充值失败："+submitbackmsg);
			}else if ("1".equals(searchState)) {
				//成功状态处理
				map.put("status", 1);
				map.put("resultCode", "充值成功！");
			}else{
				map.put("status", 3);
				map.put("resultCode", "未知的订单状态，请与上家核实后处理："+submitbackmsg);
			}
			//mp:订单status,订单状态,订单id.
		}catch(Exception e){
			log.error("鼎信的查询出错"+e.getMessage());
		}
		return map;
	}
	
	/**
	 * 查询订单状态
	 * 
	 * @param url
	 * @param userId
	 *            用户编号 由鼎信商务提供，如：HR0001
	 * @param pwd
	 *            加密密码 由鼎信商务提供。（需大写）
	 * @param orderId
	 * @param key
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String sendSearch(String url, String userId, String pwd,
			String orderId, String key) throws ClientProtocolException, IOException {

		StringBuffer paramBuffer = new StringBuffer();

		// userid=xxx&pwd=xxx&orderid=xxx&account=xxx&=xxx&amount=1&userkey=xxx
		paramBuffer.append("userid=").append(userId);
		paramBuffer.append("&pwd=").append(pwd);
		paramBuffer.append("&orderid=").append(orderId);

		String userKey = "userid" + userId + "pwd" + pwd + "orderid" + orderId
				+ key;
		String md5UserKey = DigestUtils.md5Hex(userKey);
		paramBuffer.append("&userkey=").append(md5UserKey);

		String queryString = "";
		try {
			queryString = URIUtil.encodeQuery(paramBuffer.toString());
			// queryString = paramBuffer.toString();
		} catch (URIException e) {
			e.printStackTrace();
		}
		url = url + "?" + queryString;
		System.out.println(url);
		String ret = "";
		ret = ToolHttp.get(false, url);
		return ret;

	}
	
	public static Map<String, String> readXmlToMapFromCreateResponse(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List<Element> l = rootElt.elements();
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				map.put(element.getName(), element.getStringValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
