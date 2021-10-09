package com.hyfd.task;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.Sign;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.YunpuOrderDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.BmOrderCustomGetRequest;
import com.qianmi.open.api.response.BmOrderCustomGetResponse;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.security.MessageDigest;
import java.util.*;

@Component
public class SanWangTask {
    private static Logger log = Logger.getLogger(SanWangTask.class);

    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao; // 物理通道信息


    @Autowired
    OrderDao orderDao;// 订单

    @Autowired
    YunpuOrderDao yunpuOrderDao;

    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者


    @Scheduled(fixedDelay = 60000)
    public void querySanWangOrder(){
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            log.error("三网查单开始");
            String id = "2000000066";
            Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);	//获取通道参数
            String defaultParameter = channel.get("default_parameter")+"";					//默认参数
            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String taskurl = paramMap.get("taskurl");										//查询地址
            log.info("三网查询地址"+taskurl);
            String secret  = paramMap.get("secret");                              //密钥
            log.info("三网密钥"+secret);
            String accountName = paramMap.get("accountName");								//接入码
            log.info("三网商户的账号为"+accountName);
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            Map map1 = new HashMap();
            List<Map<String,Object>> orderList = orderDao.selectByTask(param);
            for(Map<String,Object> order : orderList){
                int flag = 2;
                String orderId = order.get("orderId")+"";									//平台订单号
                map.put("orderId", orderId);

                map1.put("outOrderId", orderId);
                map1.put("accountName",accountName);
                String sign = getSign(map1, secret);
//                String sign = Sign.signAES(secret, map1);//签名
                log.info("三网查单签名为"+sign);

                map1.put("sign",sign);
                String parameter = JSONObject.toJSONString(map1);


                //发送post请求.
                String data = ToolHttp.post(true, taskurl, parameter, "application/json");
                log.info("三网查单返回结果为"+data);
                JSONObject response = JSONObject.parseObject(data);
                boolean status = (Boolean) response.get("status");
                String entry = (String)response.get("entry");
                String message = (String)response.get("message");
                if (status == true) {
                    if (entry .equals("成功")){
                        flag = 1; //充值成功
                    }else if (entry == "充值中"){
                        continue;
                    }else if (entry == "失败"){
                        flag = 0;
                        map.put("resultCode", message);
                    }
                }

                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }

        }catch (Exception e){
            log.error("三网查询Task出错"+e);
        }


    }

    /**
     * 加密方式
     *
     * @param map
     * @return
     */
    public static String getSign(Map map,String sign) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        String buf = "";
        for (Map.Entry<String, Object> stringStringEntry : infoIds) {
            if (stringStringEntry.getValue() != null) {
                buf += stringStringEntry.getKey() + stringStringEntry.getValue();
            }
        }
        String sign2 = encrypt32(sign + encrypt32(buf) + sign);
        return sign2;
    }


    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

}
