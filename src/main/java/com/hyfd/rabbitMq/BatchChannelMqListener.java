package com.hyfd.rabbitMq;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
import com.hyfd.service.mp.RabbitmqPhysicalChannelSer;
import com.hyfd.service.mp.chargeOrderSer;

public class BatchChannelMqListener implements MessageListener{
	
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
	@Autowired
	RabbitmqPhysicalChannelSer rabbitmqPhysicalChannelSer;
	
	private static final int num = 20;
	
	Semaphore semp = new Semaphore(num);//许可证
	
	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num);
	
	@Override
	public synchronized void onMessage(Message message) {
		Map<String, Object> order = SerializeUtil.getObjMapFromMessage(message);//获取订单对象
		try {
			//备用通道------------
			Map<String,Object> channel = (Map<String, Object>) order.get("channel");
			String providerPhysicalChannelId = channel.get("id").toString();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("providerPhysicalChannelId", providerPhysicalChannelId);
			// 获取当前物理通道是否配置备用MQ通道(如果有,则使用备用MQ通道,否则走默认通道)
			Map<String, Object> rabbitmqDataMap = rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelListAll(map);
			if (rabbitmqDataMap != null) {
				// 备用通道
				mqProducer.sendDataToQueue(rabbitmqDataMap.get("mqQueueName").toString(), SerializeUtil.getStrFromObj(order));
			}else{
				//-------------------
				String providerMark = (String) order.get("providerMark");//获取通道的唯一标识
				if(providerMark.equals("DiXinTongBillDeal")){
					Map<String,Object> key = dixintongKeyDao.selectRecentKey();
					String secretKey = (String) key.get("secret_key");//获取密钥
					order.put("secretKey", secretKey);
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
	           
				Map<String, Object> result = new HashMap<String, Object>();
				String bizType = (String) order.get("bizType");
				String pkg = bizType.equals("1") ? "Flow" : "Bill";
				if (providerMark.equals("GuoMeiBillDeal")) {
					fixedThreadPool.execute(new Runnable(){
						public void run() {
							try {
								semp.acquire();//获取一张许可证
								Map<String, Object> guomeiResult = new GuoMeiBillDeal().deal(order);
								guomeiResult.put("order", order);
								mqProducer.sendDataToQueue(RabbitMqProducer.Status_QueueKey, SerializeUtil.getStrFromObj(guomeiResult));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally{
								semp.release();//释放一张许可证
							}
						}
					});
				}else{
					BaseDeal deal = (BaseDeal) Class.forName("com.hyfd.deal." + pkg + "." + providerMark).newInstance();
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
					mqProducer.sendDataToQueue(RabbitMqProducer.Status_QueueKey, SerializeUtil.getStrFromObj(result));	
				}
			}
		} catch (Exception e) {
			log.error("通道选择出错"+ExceptionUtils.getExceptionMessage(e)+"||"+MapUtils.toString(order));
			e.printStackTrace();
		}
	}
	
}
