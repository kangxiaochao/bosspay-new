package com.hyfd.task;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MD5Util;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class LeYuTask {


    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

    @Autowired
    OrderDao orderDao;// 订单

    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者


    private static Logger log = Logger.getLogger(LeYuTask.class);


    @Scheduled(fixedDelay = 60000)
    public void queryLeYuOrder() {

        Map<String, Object> resultmap = new HashMap<String, Object>();

        try {
            String id = "2000000071";// 乐语物理通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String ServerUrl = paramMap.get("ServerUrl") + ""; 		// 乐语提供 查单url
            String appKey = paramMap.get("AppKey") + ""; 		// 乐语提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + ""; 	// 乐语提供 平台appSecret

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);

            for (Map<String, Object> order : orderList) {
                int flag = 2;

                String orderId = order.get("orderId") + "";
                String providerOrderId = order.get("providerOrderId") + "";
                String nowTime = DateUtils.getNowTimeToSec();
                resultmap.put("orderId", orderId);

                Map mapParm  = new HashMap();
                mapParm.put("rechargeId",providerOrderId);
                String redultjson = JSONObject.toJSONString(mapParm);

                SortedMap map = new TreeMap();
                map.put("appKey",appKey);
                map.put("requestType","QUERY");
                map.put("timeStamp", nowTime);
                map.put("requestMsg",redultjson);
                String sign = createSign(map, appSecret);

                map.put("sign",sign);
                map.put("transactionId","");
                String redultjsons = JSONObject.toJSONString(map);
                log.info("乐语查单请求参数:"+redultjsons);
                String data = ToolHttp.post(false, ServerUrl, redultjsons,"application/json");
                log.info("乐语查单响应结果:" + data);
//                {"msg":"success","code":0,"responseMsg":{"serviceNum":"17001000000","remark":"充值成功","orderPay":1000,"transactionId":"0a7b0039b6af45ab888c9e7f7066ded9","rechargeId":"LYTXRC202201121530035860917","status":1,"createDate":"2022-01-12 15:30:04","completeDate":"2022-01-12 15:30:12"}}
                if (null != data && !data.equals("")) {
                    JSONObject jsonObj = JSONObject.parseObject(data);
                    String code = jsonObj.getString("code");
                    if (code.equals("0")){
                        String responseMsg = jsonObj.getString("responseMsg");
                        JSONObject resp = JSONObject.parseObject(responseMsg);
                        String status = resp.getString("status");
                        String remark = resp.getString("remark");
                        resultmap.put("resultCode", code + ":" + remark);
                        if (status.equals("1")){
                            flag = 1;	  //充值成功
                        }else if(status.equals("2")) {
                            flag = 0;     //充值失败
                        }else if (status.equals("0")){
                            continue;    //充值订单处理中
                        }
                        resultmap.put("status", flag);
                        mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(resultmap));
                    }else {
                        //查单请求出现异常
                        log.error("乐语请求订单出现异常："+order);
                    }


                }

            }

        }catch (Exception e){
            log.error("乐语Task出错" + e);
        }
    }


    /**
     * @param params 请求参数
     * @return
     * @author
     * @date 2016-4-22
     * @Description：sign签名
     */
    public static String createSign(SortedMap<Object, Object> params, String APP_SECRET) {
        StringBuffer sb = new StringBuffer();
        Set es = params.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + APP_SECRET);
        System.out.println("sb = " + sb);
        String sign = null;
        try {
            sign = MD5Util.MD5Encode(sb.toString(),"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sign;
    }
}
