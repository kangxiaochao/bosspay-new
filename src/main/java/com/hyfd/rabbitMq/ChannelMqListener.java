package com.hyfd.rabbitMq;

import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.*;
import com.hyfd.deal.BaseDeal;
import com.hyfd.service.mp.chargeOrderSer;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class ChannelMqListener implements MessageListener{
	
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
    OrderDao orderDao;// 订单
	
	@Override
	public synchronized void onMessage(Message message) {
		Map<String, Object> order = SerializeUtil.getObjMapFromMessage(message);//获取订单对象
		try {
			String providerMark = (String) order.get("providerMark");//获取通道的唯一标识
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
            //建行兑换券
            if(providerMark.equals("JianHangBillDeal")){
            	int fee = new Double((double) order.get("fee")).intValue();
            	Map<String,Object> cardParam = new HashMap<String,Object>();
    			cardParam.put("price", fee);
    			cardParam.put("type", "建行");
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
    				log.error(MapUtils.toString(order)+"建行兑换券获取为空");
    			}
            }
            //兔兔币cookies
            if(providerMark.equals("TuTuBiBillDeal")){
            	Map<String,Object> cookie = cookiesDao.selectFirstCookie();
            	order.put("cookie", cookie);
            }
            //兔兔币专用cookies
            if(providerMark.equals("TuTuBiZhuanYongBillDeal")){
            	Map<String,Object> cookie = cookiesDao.selectFirstTTBZYCookie();
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
            //海航币通道cookies
            if(providerMark.equals("HaiHangBiBillDeal")){
            	//获取cookie
            	Map<String,Object> cookie = cookiesDao.selectFirstHHBCookie();
            	order.put("cookie", cookie);
            }
            //海航币专业通道充值验证/获取cookie
//            if(providerMark.equals("HaiHangZhuanYongBillDeal")) {
            	//获取cookie
//            	Map<String,Object> cookie = cookiesDao.selectFirstHHBZYCookie();
//            	order.put("cookie", cookie);
            	//专属号段手机号验证
//            	String phoneNo = (String) order.get("phone");// 手机号
//            	Map<String,Object> haiHangBiMap = new HashMap<>();
//            	haiHangBiMap.put("dispatcherProviderId","2000000061");
//            	haiHangBiMap.put("section",phoneNo.substring(0, 7));
//            	List<Map<String, Object>> haiHangBiList = orderDao.specifySectionRecharge(haiHangBiMap);
//            	log.info("-=-=----:"+haiHangBiList.toString());
//            	boolean bool = true;
//            	if(haiHangBiList == null || haiHangBiList.size() <= 0) {
//            		 bool = false;
//            	}
//            	order.put("isOk", bool);
//            }
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
//			mqProducer.sendDataToQueue(RabbitMqProducer.Status_QueueKey, SerializeUtil.getStrFromObj(result));
		} catch (Exception e) {
			log.error("通道选择出错"+ExceptionUtils.getExceptionMessage(e)+"||"+MapUtils.toString(order));
			e.printStackTrace();
		}
	}
	
}
