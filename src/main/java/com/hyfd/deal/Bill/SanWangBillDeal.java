package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.Sign;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.deal.BaseDeal;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.omg.CORBA.FloatSeqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;


public class SanWangBillDeal implements BaseDeal {

    private static Logger log = Logger.getLogger(SanWangBillDeal.class);

    @Autowired
    PhoneSectionDao phoneSectionDao;// 号段

    @Autowired
    ProviderDao providerDao;// 代理商


    public static Map<String, String> ydMap = new HashMap<String, String>();
    public static Map<String, String> ltMap = new HashMap<String, String>();
    public static Map<String, String> dxMap = new HashMap<String, String>();

    static {
        ydMap.put("100", "H10100");
        ydMap.put("50", "H10050");
        ydMap.put("30", "H10030");
        ltMap.put("100", "H20100");
        ltMap.put("50", "H20050");
        ltMap.put("30", "H20030");
        dxMap.put("100", "H30100");
        dxMap.put("50", "H30050");
        dxMap.put("30", "H30030");
    }

    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String,Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            log.info("发起三网话费充值-------------------------");
            String phone = (String) order.get("phone")+"";// 手机号
            String fee = order.get("fee") + "";//充值金额
            Map<String, Object> channel = (Map<String, Object>) order
                    .get("channel");// 获取通道参数
            String defaultParameter = (String) channel.get("default_parameter");// 默认参数
            log.info("defaultParameter"+defaultParameter);
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String accessToken = paramMap.get("accessToken");
            log.info(accessToken+"accessToken");
            String secret = paramMap.get("secret");
            log.info(secret+"secret");
            String url = paramMap.get("url");
            log.info(url+"url");
            String section = (phone.length() == 13) ? phone.substring(0, 5) : phone.substring(0, 7);// 获取号段
            Map<String, Object> sectionMap = phoneSectionDao.selectBySection(section);
            String providerId = (String) sectionMap.get("provider_id");// 运营商ID
            String nameById = providerDao.getNameById(providerId);//运营商名称
            System.out.println("nameById = " + nameById);
            String gearCode = "";
            if (nameById.equals("中国移动")) {
                gearCode = ydMap.get(fee);
            }else if (nameById.equals("中国联通")){
                gearCode = ltMap.get(fee);
            }else if (nameById.equals("中国电信")){
                gearCode = dxMap.get(fee);
            }
            log.info("手机号为"+phone+"充值编码为"+gearCode);
            String orderId = order.get("order_id")+"";//订单号
            String timestamp = String.valueOf(System.currentTimeMillis());//提单时间


            Map maps = new HashMap();
            maps.put("phone",phone);//手机号
            maps.put("gearCode",gearCode);//充值面值
            maps.put("serialNumber",orderId);
            maps.put("token",accessToken);
            maps.put("timestamp",String.valueOf(System.currentTimeMillis()));//提单时间

            String sign = Sign.signAES(secret, maps);//签名
            maps.put("sign",sign);
            String parameter = JSONObject.toJSONString(maps);

            //发送post请求.
            String data = ToolHttp.post(false, url, parameter, "application/json");
            log.info("三网接受的响应为:"+ data);
            JSONObject response = JSONObject.parseObject(data);
            String body = response.get("body")+"";
            JSONObject bodys = JSONObject.parseObject(body);
            String status = bodys.get("status")+"";
            if (status.equals("1")){//提交成功
                flag = 1;
                map.put("resultCode","提交成功");
            }else if (status.equals("3")){//提交失败
                flag = 0;
            }else if (status.equals("2")){//充值成功
                flag = 3;
                map.put("resultCode","充值成功");
            }

        }catch (Exception e){
            log.error("三网话费充值逻辑出错" + e + MapUtils.toString(order));
        }

        map.put("status", flag);
        return map;
    }
}
