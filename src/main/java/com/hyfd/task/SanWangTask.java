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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            String accessToken = paramMap.get("accessToken");								//接入码
            log.info("三网查单Token为"+accessToken);
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            Map map1 = new HashMap();
            List<Map<String,Object>> orderList = orderDao.selectByTask(param);
            for(Map<String,Object> order : orderList){
                int flag = 2;
                String orderId = order.get("orderId")+"";									//平台订单号
                map.put("orderId", orderId);

                map1.put("serialNumber", orderId);
                map1.put("token",accessToken);

                String sign = Sign.signAES(secret, map1);//签名
                log.info("三网查单签名为"+sign);

                map1.put("sign",sign);
                String parameter = JSONObject.toJSONString(map1);


                //发送post请求.
                String data = ToolHttp.post(false, taskurl, parameter, "application/json");
                log.info("三网查单返回结果为"+data);
                JSONObject response = JSONObject.parseObject(data);
                String body = response.get("body")+"";
                JSONObject bodys = JSONObject.parseObject(body);
                String status = bodys.get("status")+"";
                if (status.equals("2")){
                    flag = 1;	// 充值成功
                }else if (status.equals("8")){
                    flag = 0; //充值失败,退款成功
                }if (status.equals("1")){
                    continue;	// 充值订单处理中
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }

        }catch (Exception e){
            log.error("三网查询Task出错"+e);
        }






    }



}
