package com.hyfd.rabbitMq.backup;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.CardDao;
import com.hyfd.dao.mp.CookiesDao;
import com.hyfd.dao.mp.DixintongKeyDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.BaseDeal;
import com.hyfd.deal.Bill.GuoMeiBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.mp.chargeOrderSer;

public class ChannelMqListenerOne implements MessageListener{
	
	Logger log = Logger.getLogger(getClass());
	
	@Autowired
	RabbitMqProducer mqProducer;
	@Autowired
	chargeOrderSer chargeOrderSer;//话费充值Service
    @Autowired
    CardDao cardDao;
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//分销商物理通道
	@Autowired
	CookiesDao cookiesDao;
	@Autowired
	DixintongKeyDao dixintongKeyDao;
	
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
	
	@Override
	public synchronized void onMessage(Message message) {
		
		Map<String, Object> order = SerializeUtil.getObjMapFromMessage(message);//获取订单对象
		try {
			String providerMark = (String) order.get("providerMark");//获取通道的唯一标识
			if(providerMark.equals("GuoMeiBillDeal")){
				fixedThreadPool.execute(new Runnable() {
					public void run() {
						Map<String,Object> result = new HashMap<String,Object>();
						result = new GuoMeiBillDeal().deal(order);
						result.put("order", order);
						//调用上家接口返回结果的处理；实时接口且会进行上家余额扣除和回调下家
						chargeOrderSer.changeStatus(result);
//						mqProducer.sendDataToQueue(RabbitMqProducer.Status_QueueKey, SerializeUtil.getStrFromObj(result));
					}
				});
			}else{
				if(providerMark.equals("DiXinTongBillDeal")){
					Map<String,Object> key = dixintongKeyDao.selectRecentKey();
					String secretKey = (String) key.get("secret_key");//获取密钥
					order.put("secretKey", secretKey);
				}
				//如果是迪信通卡密则查询一条卡密信息放到order里
	            if(providerMark.equals("DiXinTongKaMiBillDeal")){
	            	int fee = new Double((double) order.get("fee")).intValue();
	            	Map<String,Object> cardParam = new HashMap<String,Object>();
	    			cardParam.put("price", fee);
	    			cardParam.put("type", "迪信通");
	    			Map<String,Object> card = cardDao.selectOneCard(cardParam);
	    			if(card != null){
	    				//更新卡密为提取状态
	    				String cardPass = (String) card.get("cardPass");
	    				cardParam.clear();
	    				cardParam.put("cardPass", cardPass);
	    				cardParam.put("useState", -1);
	    				int i = cardDao.updateState(cardParam);
	    				if(i == 1){
	    					order.put("card", card);
	    				}
	    			}else{
	    				log.error(MapUtils.toString(order)+"迪信通卡密获取为空");
	    			}
	            }
	            //远特卡密
	            if(providerMark.equals("YuanTeKaMiBillDeal")){
	            	int fee = new Double((double) order.get("fee")).intValue();
	            	Map<String,Object> cardParam = new HashMap<String,Object>();
	    			cardParam.put("price", fee);
	    			cardParam.put("type", "远特");
	    			Map<String,Object> card = cardDao.selectOneCard(cardParam);
	    			if(card != null){
	    				//更新卡密为提取状态
	    				String cardPass = (String) card.get("cardPass");
	    				cardParam.clear();
	    				cardParam.put("cardPass", cardPass);
	    				cardParam.put("useState", -1);
	    				int i = cardDao.updateState(cardParam);
	    				if(i == 1){
	    					order.put("card", card);
	    				}
	    			}else{
	    				log.error(MapUtils.toString(order)+"远特卡密获取为空");
	    			}
	            }
	            //兔兔币cookies
	            if(providerMark.equals("TuTuBiBillDeal")){
	            	Map<String,Object> cookie = cookiesDao.selectFirstCookie();
	            	order.put("cookie", cookie);
	            }
	            //用友cookies
	            if(providerMark.equals("YongYouBillDeal")){
	            	Map<String,Object> cookie = cookiesDao.selectFirstYYCookie();
	            	order.put("cookie", cookie);
	            }
	            //酷商中兴cookies
	            if(providerMark.equals("KuShangZhongXinBillDeal")){
	            	Map<String,Object> cookie = cookiesDao.selectFirstKSZXCookie();
	            	order.put("cookie", cookie);
	            }
				Map<String,Object> result = new HashMap<String,Object>();
				String bizType = (String) order.get("bizType");
				String pkg = bizType.equals("1")?"Flow":"Bill";
				BaseDeal deal = (BaseDeal) Class.forName("com.hyfd.deal."+pkg+"."+providerMark).newInstance();
				result = deal.deal(order);
				if(result.containsKey("card")){//如果卡密信息不为空
					@SuppressWarnings("unchecked")
					Map<String,Object> cardParam = (Map<String, Object>) result.get("card");
					int i = cardDao.updateCard(cardParam);
					if(i != 1){
						log.error("在ChannelMqListener中更新卡密信息失败");
					}
				}
				result.put("order", order);
				//调用上家接口返回结果的处理；实时接口且会进行上家余额扣除和回调下家
				chargeOrderSer.changeStatus(result);
//				mqProducer.sendDataToQueue(RabbitMqProducer.Status_QueueKey, SerializeUtil.getStrFromObj(result));
			}
		} catch (Exception e) {
			log.error("通道选择出错"+ExceptionUtils.getExceptionMessage(e)+"||"+MapUtils.toString(order));
			e.printStackTrace();
		}
	}
	
}
