package com.hyfd.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.PaymentDao;
import com.hyfd.service.mp.PaymentSer;

/**
 * @ClassName: HaiHangFlowTask
 * @Description: 微信扫码支付查询接口
 * @author wyf
 * @date 2018年3月2日
 * @version 1.0
 */
//@Component
public class PaymentTask {

	/**
	 * 日志
	 */
	private static final Logger log = Logger.getLogger(PaymentTask.class);
	
	@Autowired
	PaymentDao paymentDao;
	
	@Autowired
	PaymentSer paymentSer;
	
	@Autowired
	AgentDao agentDao;// 代理商

	@Scheduled(fixedDelay = 60000)
	public void queryWePayOrder() {
		Map<String, String> addition = new HashMap<>();
		addition.put("state", "1");//支付状态  待支付
		List<Map<String, Object>> maps = paymentDao.SelectQueyByOne(addition);
		if(maps.size() > 0) {
			maps.forEach(map -> {
				map.keySet().forEach(string -> {
					addition.put(string, map.get(string).toString());
				});
				String data = Sign(map.get("transaction_id").toString(),map.get("pay_money").toString());
				String url = "https://pay.swiftpass.cn/pay/gateway";
				String xml = HttpPostXml(url,data);
				Map<String,String> map1 = XmlUtils.readXmlToMap(xml);
				log.error("自助加款查询返回："+ map1);
				if(map1.get("status").equals("0") && map1.get("trade_state").equals("SUCCESS") && map1.get("out_trade_no").toString().equals(map.get("out_trade_no"))) {
					Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(map.get("agent_id").toString());
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					list = paymentSer.getChargeList(list,agent,map.get("add_money").toString(),map.get("id").toString());
					if(list.size()>0) {
						if(paymentSer.allotBalance(list) > 0) {
							map.put("state", "2"); //支付状态  加款成功
						}else {
							map.put("state", "3"); //支付状态  加款失败
						}
					}
					List<Map<String, Object>> map4 = paymentDao.SelectQueyByOne(addition);
					if(map4.size() > 0) {
						paymentDao.updateByPrimaryKeySelective(map);
					}
				}else {
					map.put("state", "-1");
					List<Map<String, Object>> map4 = paymentDao.SelectQueyByOne(addition);
					if(map4.size() > 0) {
						paymentDao.updateByPrimaryKeySelective(map);
					}
				}
			});
		}
	}
	
	public String Sign(String trade_no,String money) {
		String body = "自助加款"; // 商品描述
		String mch_create_ip = "127.0.0.1";// 终端IP
		String mch_id = "102510157346";// 商户号
		String nonce_str = System.currentTimeMillis()+"";// 随机字符串
		String notify_url = "http://118.31.229.23:8080/bosspaybill/status/returnUrl";// 通知地址
		String out_trade_no = trade_no;// 商户订单号
		String service = "pay.weixin.native";// 接口类型
		String total_fee = money;// 总金额
		String string1 = "body=" + body + "&mch_create_ip=" + mch_create_ip + "&mch_id=" + mch_id + "&nonce_str="
					   + nonce_str + "&notify_url=" + notify_url + "&out_trade_no=" + out_trade_no 
					   + "&service=" + service + "&total_fee=" + total_fee;
		String data = "<xml><service>pay.weixin.native</service><mch_id>102510157346</mch_id><body>自助加款</body><mch_create_ip>" + mch_create_ip
						+ "</mch_create_ip><nonce_str>" + nonce_str + "</nonce_str><notify_url>"+notify_url+"</notify_url><total_fee>" + total_fee
						+ "</total_fee><sign>" + MD5.MD5(string1 + "&key=4f3f956a9c97ade3de51f367f9858869").toUpperCase() + "</sign><out_trade_no>" + out_trade_no
						+ "</out_trade_no></xml>";
		log.error("查询请求信息"+data);
		return data;
	}
	
	public String HttpPostXml(String url,String data) {
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(url);
		String info = null;
		try {
			RequestEntity entity = new StringRequestEntity(data, "text/plain", "utf-8");
			post.setRequestEntity(entity);
			httpclient.executeMethod(post);
			int code = post.getStatusCode();
			if (code == HttpStatus.SC_OK) {
				info = new String(post.getResponseBodyAsString()); // 接口返回的信息
			}
			
		} catch (Exception e) {
			log.error("微信扫码支付查询出错" + e);
		} finally {
			post.releaseConnection();
		}
		return info;
	}
	
}
