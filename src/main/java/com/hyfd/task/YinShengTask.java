package com.hyfd.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.Bill.YinShengBillDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;

@Component
public class YinShengTask
{
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(YinShengTask.class);
    
    @Scheduled(fixedDelay = 60000)
    public void queryMinShengOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
       
        try
        {
            String id = "2000000020";// 银盛物理通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String queryOrderUrl = paramMap.get("searchUrl");// 查询地址
            String terminalID = paramMap.get("terminalID");// "20147601";//终端号
            String factoryID = paramMap.get("factoryID");// "0000";//企业编号
            String key = paramMap.get("key");// "000000";//加密key
            
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList)
            {
                int flag = 2;
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                String reqDateTime = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
                String searchResponseXml =
                    YinShengBillDeal.sendSearch(queryOrderUrl,
                        orderId,
                        DateTimeUtils.formatDate(new Date(), "yyyyMMdd"),
                        terminalID,
                        factoryID,
                        reqDateTime,
                        key);
                log.debug("银盛查询结果：" + searchResponseXml);
                JSONObject jsonObject = JSON.parseObject(searchResponseXml);
                String status = jsonObject.getString("status");
                // 0000 表示成功，7 0007 指上游 超时，4 1004 表示业务已受理正在处理中，
                if ("0004".equals(status) || "0006".equals(status) || "0007".equals(status) || "1010".equals(status))
                {
                    flag = -1;
                }
                else if ("1004".equals(status))
                {
                    continue;
                }
                else if ("0000".equals(status) || "1027".equals(status))
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;
                }
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("银盛查询Task出错" + e);
        }
    }
}
