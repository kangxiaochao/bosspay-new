package com.hyfd.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;

import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.BmOrderCustomGetRequest;
import com.qianmi.open.api.request.BmRechargeMobileCorrectDetailRequest;
import com.qianmi.open.api.request.BmRechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.request.BmRechargeMobilePayBillRequest;
import com.qianmi.open.api.response.BmOrderCustomGetResponse;
import com.qianmi.open.api.response.BmRechargeMobileCorrectDetailResponse;
import com.qianmi.open.api.response.BmRechargeMobileGetItemInfoResponse;
import com.qianmi.open.api.response.BmRechargeMobilePayBillResponse;

public class yunMiYouTest {
	
	public static void main(String [] args) {
		queryYunMiYouOrder();
//		System.out.println(deal(null));
	}

	public static Map<String, Object> deal(Map<String, Object> order) {
        Map<String,Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phone = "17080080880";																//手机号
            String fee = new Double("10").intValue()+"";						//充值金额  单位元  正整数，例如20、50、100等面值
//            Map<String,Object> channel = (Map<String, Object>) order.get("channel");		//获取通道参数
            String linkUrl ="http://apilf.bm001.com/api";										//充值地址
//            String defaultParameter = channel.get("default_parameter")+"";					//默认参数
//            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = "10002760";
            String appSecret = "TrT7Mo0HF5qkkV2yBef70XlqFPbMVFfs";									//秘钥
            String accessToken = "61a948a610b345279f8fa7a157d6d27a";								//接入码
            String callback = "http://8aue8z.natappfree.cc/bosspay/status/YunMiYou";										//回调地址
            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
            map.put("orderId",curids);
            //查询单个话费直充商品
            OpenClient client = new DefaultOpenClient(linkUrl, appSecret);
            BmRechargeMobileGetItemInfoRequest inforeq = new BmRechargeMobileGetItemInfoRequest();
            inforeq.setMobileNo(phone);
            inforeq.setRechargeAmount(fee);
            BmRechargeMobileGetItemInfoResponse inforesponse = client.execute(inforeq, accessToken);
            System.out.println("云米优话费充值：" +  phone + "  -  " + inforesponse.toString());
//            log.info("云米优话费充值：" +  phone + "  -  " + inforesponse.toString());
            if (inforesponse.isSuccess()){
                String itemId = inforesponse.getMobileItemInfo().getItemId();				// 商品编号
                // 话费订单充值
                BmRechargeMobilePayBillRequest createreq = new BmRechargeMobilePayBillRequest();
                createreq.setMobileNo(phone);
                createreq.setRechargeAmount(String.valueOf(fee));
                createreq.setItemId(itemId);
                createreq.setOuterTid(curids);
                createreq.setCallback(callback);
                createreq.setTimestamp(new Date().getTime());
                BmRechargeMobilePayBillResponse createresponse = client.execute(createreq, accessToken);
                if (createresponse.isSuccess()){
                    flag = 1;		//提交成功
                    map.put("resultCode","提交成功");
                }else {
                    flag = 0;
                    map.put("resultCode",createresponse.getErrorCode()+" : "+createresponse.getSubMsg());
                }
            }else {
                flag = 0;
                map.put("resultCode",inforesponse.getErrorCode()+" : "+inforesponse.getSubMsg());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	 System.out.println("云米优话费充值逻辑出错" + e + MapUtils.toString(order));
//            log.error("云米优话费充值逻辑出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
	//{orderId=202012101040544131708008088035, resultCode=9 : 您的余额不足, status=0}
	
    public static void queryYunMiYouOrder(){
        Map<String,Object> map = new HashMap<String,Object>();
        try {
//            String id = "2000000063";
//            Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);	//获取通道参数
//            String defaultParameter = channel.get("default_parameter")+"";					//默认参数
//            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String url = "http://apilf.bm001.com/api";												//查询地址
            String appKey = "10002760";
            String appSecret = "TrT7Mo0HF5qkkV2yBef70XlqFPbMVFfs";
            String accessToken = "61a948a610b345279f8fa7a157d6d27a";								//接入码
//            Map<String,Object> param = new HashMap<String,Object>();
//            param.put("dispatcherProviderId", id);
//            param.put("status", "1");
//            List<Map<String,Object>> orderList = orderDao.selectByTask(param);
//            for(Map<String,Object> order : orderList){
                int flag = 2;
                String orderId = "20121010660154";									//上家订单号
                map.put("orderId", orderId);
       

                OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
                
                BmRechargeMobileCorrectDetailRequest req = new BmRechargeMobileCorrectDetailRequest();
                req.setMobileNo("17080080880");
                req.setBillId(orderId);
                req.setTimestamp(new Date().getTime());
                BmRechargeMobileCorrectDetailResponse response = client.execute(req, accessToken);
                if(response.isSuccess()){
                    // 返回的充值状态
//                    String status = response.get  .getRechargeState();
//                    System.out.println("云米优查询订单号为"+orderId+"的单子返回状态为"+status);
//                    if("1".equals(status)){
//                        /**
//                         * 需要修改  判断输入状态
//                         */
//                        // 充值成功
//                        flag = 1;
//                    }else if("0".equals(status)){
//                        // 充值中
////                        continue;
//                    }else if ("9".equals(status)) {
//                        // 充值失败
//                        flag = 0;
//                    }else {
////                        continue;
//                    }
                }else {
//                    log.error("云米优查询出错 ");
                }
//                map.put("status", flag);
//                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
//            }
        }catch (Exception e){
//            log.error("云米优查询Task出错"+e);
        }
    }

	
}
