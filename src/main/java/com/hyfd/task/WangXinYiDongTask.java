package com.hyfd.task;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.ToolMD5;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class WangXinYiDongTask {
    private static Logger log = Logger.getLogger(WangXinYiDongTask.class);

    public static Map<String, String> rltMap = new HashMap<String, String>();



    @Autowired
    OrderDao orderDao;

    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息

    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者

    @Scheduled(fixedDelay = 60000)
    public void queryPengBoShiOrder() {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            String id = "2000000072";// 共享通信物理通道ID
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String companyCode = paramMap.get("companyCode");
            String companyType = paramMap.get("companyType");
            String appKey = paramMap.get("appKey");
            String queryUrl = paramMap.get("queryUrl"); //查单url

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList) {
                int flag = 2;
                String orderId = order.get("orderId") + "";
                String providerOrderId = order.get("providerOrderId") + "";
                map.put("orderId", orderId);
                map.put("providerOrderId", providerOrderId);


                StringBuffer str = new StringBuffer(appKey);
                str.append("companyCode="+companyCode);
                str.append(":");
                str.append("companyType="+companyType);
                str.append(":");
                str.append("customerId="+orderId);
                str.append(":");
                str.append("orderCode="+providerOrderId); //流水号,上家返回的.
                str.append(appKey);

                String  sign = ToolMD5.encodeMD5Hex(str.toString()).toUpperCase();



                Map maps = new HashMap();
                Map maps2 = new HashMap();
                Map maps3 = new HashMap();
                maps.put("companyCode",companyCode);//必填
                maps.put("companyType",companyType);// 必填
                maps.put("sign",sign);
                maps2.put("publicParams",maps);
                maps3.put("orderCode",providerOrderId);
                maps3.put("customerId",orderId);
                maps2.put("businessParams",maps3);
                String redultjson = JSONObject.toJSONString(maps2);
                log.error("北京华夏查单请求参数" + redultjson);
                //发送httppost请求
                String data = ToolHttp.post(false, queryUrl, redultjson,"text/plain");


                log.error("北京华夏查单完成，返回信息为" + data);
                if (null != data && !data.equals("")){
                    JSONObject resultJson = JSONObject.parseObject(data);
                    String status = resultJson.get("status") + ""; //status 状态	0为接受订单成功，具体是否充值成功，异步通知里体现
                    String message = resultJson.get("message") + "";
                    String orderCode = resultJson.get("orderCode") + "";
                    String customerorderId = resultJson.get("customerId") + "";
                    map.put("resultCode",message);

                    if ("4".equals(status)) {
                        flag = 1; //充值成功
                    }else if ("3".equals(status)){
                        continue; //充值中
                    }else if ("5".equals(status)){
                        flag = 0; //充值失败
                    }
                    map.put("status", flag);
                    log.error(map);
                    mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
                }else {
                    log.error("北京华夏查单出错,订单号:"+orderId);
                }
            }
        } catch (Exception e) {
            log.error("北京华夏Task出错" + e);
        }
    }

    /**
     * 拼接请求信息
     *
     * @param nonce_str
     * @param order_id
     * @param partner_id
     * @param appKey
     * @return
     */
    public String joinUrl(String nonce_str, String order_id, String partner_id, String appKey) {
        StringBuffer suBuffer = new StringBuffer();
        suBuffer.append("nonce_str" + nonce_str);
        suBuffer.append("order_id" + order_id);
        suBuffer.append("partner_id" + partner_id);
        suBuffer.append(appKey);
        String sign = DigestUtils.md5Hex(suBuffer + "");// 签名

        StringBuffer url = new StringBuffer();
        url.append("partner_id=" + partner_id);
        url.append("&nonce_str=" + nonce_str);
        url.append("&order_id=" + order_id);
        url.append("&sign=" + sign);

        return url.toString() + "";
    }

    public static void main(String[] args) throws Exception {
        String queryUrl = "http://47.94.145.229:8081/pos/fee/queryorder";
        String appKey = "fb1e3eba-5d99-4aec-99db-00f4609d7270";
        String orderId = "20220920095847170001394246605";
        String providerOrderId = "qmc90001663639128371wotpv";
        String companyCode="900101";
        String companyType = "1";
        StringBuffer str = new StringBuffer(appKey);
        str.append("companyCode="+"900101");
        str.append(":");
        str.append("companyType="+"1");
        str.append(":");
        str.append("customerId="+"orderId");
        str.append(":");
        str.append("orderCode="+providerOrderId); //流水号,上家返回的.
        str.append(appKey);

        String  sign = ToolMD5.encodeMD5Hex(str.toString()).toUpperCase();



        Map maps = new HashMap();
        Map maps2 = new HashMap();
        Map maps3 = new HashMap();
        maps.put("companyCode",companyCode);//必填
        maps.put("companyType",companyType);// 必填
        maps.put("sign",sign);
        maps2.put("publicParams",maps);
        maps3.put("orderCode",providerOrderId);
        maps3.put("customerId",orderId);
        maps2.put("businessParams",maps3);
        String redultjson = JSONObject.toJSONString(maps2);
        String data = ToolHttp.post(false, queryUrl, redultjson,"text/plain");
        System.out.println(data);
    }
}
