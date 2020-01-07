package com.hyfd.task;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.dao.mp.CardDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class DixintongKaMiTask {

	private static Logger log = Logger.getLogger(DixintongKaMiTask.class);
	
	@Autowired
	OrderDao orderDao;
	@Autowired
	CardDao cardDao;
	@Autowired
	RabbitMqProducer mqProducer;//消息队列生产者
	
//	@Scheduled(fixedDelay = 60000)
	public void queryDixintongKaMiOrder(){
		Map<String,Object> map = new HashMap<String,Object>();
		String resultMsg = "";
		String id = "";
		try{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("dispatcherProviderId", id);
			param.put("status", "1");
			List<Map<String,Object>> orderList = orderDao.selectByTask(param);
			for(Map<String,Object> order : orderList){
				int flag = 2;
				String orderId = (String) order.get("orderId");
				String phone = (String) order.get("phone");
				if(orderId != null && !orderId.equals("")){
					Map<String,Object> cardParam = new HashMap<String,Object>();
					cardParam.put("orderId", orderId);
					Map<String,Object> card = cardDao.selectByOrderId(cardParam);
					String cardPass = (String) card.get("cardPass");
					String cardId = (String) card.get("cardId");
					String data = rechargeResult(cardPass);
					if(data != null && !data.equals("")){
						boolean resultFlag = valiRechargeCardResutl(cardId, phone, data);
						if(resultFlag){
							flag = 1;
							resultMsg = "查询充值成功";
							card.put("useState", 1);
							card.put("orderId", orderId);
							card.put("resultMsg", "充值成功");
							card.put("useTime", new Date());
							cardDao.updateCard(card);
						}else{
							flag = 0;
							resultMsg = "查询充值失败";
							card.put("useState", 2);//初始化卡密状态
							card.put("orderId", orderId);
							card.put("resultMsg", "充值失败");
							card.put("failCount", (int)card.get("failCount")+1);
							card.put("useTime", new Date());
							cardDao.updateCard(card);
						}
						map.put("status", flag);
						map.put("orderId", orderId);
						map.put("resultCode", resultMsg);
						mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
					}else{
						continue;
					}
				}else{
					log.error("迪信通卡密查询orderId为空"+MapUtils.toString(order));
				}
			}
		}catch(Exception e){
			log.error("迪信通卡密查询出错"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 根据充值卡卡密发起查询请求(只需要一个参数即可,其余参数固定)
	public static String rechargeResult(String queryNum) {
		String rechargeResultQuery = "http://www.10026.cn/busicenter/cardPay/HomeCardPayMenuAction/initBusiQueryCardpayRecord.menu?_menuId=1050116&_menuId=1050116&QueryType=CardPwd&EndDate=&StartDate=&QueryNum=";
		String data = ToolHttp.post(false, rechargeResultQuery + queryNum, null, null);
		return data;
	}

	// 验证充值是否成功,充值卡卡号,充值号码,查询返回的数据,充值成功返回true,否则返回false
	public static boolean valiRechargeCardResutl(String cardId, String phoneNo, String data) {
		boolean result = false;
		if (null != data && !data.equals("")) {
			int startIndex = data.indexOf("<table");
			int endIndex = data.indexOf("</table");
			data = data.substring(startIndex, endIndex);
			if (data.contains(phoneNo) && data.contains("充值成功")) {
				result = true;
			}
		}
		return result;
	}
	
}
