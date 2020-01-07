package com.hyfd.deal.Flow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.SanWangTongTrafficService;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.deal.BaseDeal;

public class SanWangTongFlowDeal implements BaseDeal
{
    
    @Autowired
    PhoneSectionDao phoneSectionDao;// 号段
    
    /**
     * 日志
     */
    private static final Logger log = Logger.getLogger(YunShangFlowDeal.class);
    
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phoneNo = order.get("phone") + "";// 手机号
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");// 获取产品包
            String scope = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            String spec = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            // 平台订单号
            String orderNo = order.get("orderId") + "";
            if (null == orderNo || "".equals(orderNo) || "null".equals(orderNo))
            {
                orderNo = ToolDateTime.format(new Date(), "yyyyMMddHHmmssS") + GenerateData.getIntData(9, 7);// 订单号
            }
            map.put("orderId", orderNo);
            // 产品包编码
            String packCode = getPkgCode(scope, spec);
            // 获取通道参数
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");
            // 充值地址
            String url = (String)channel.get("link_url");
            // 默认参数
            String defaultParameter = (String)channel.get("default_parameter");
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            // 客户接入代码
            String custInteId = paramMap.get("custInteId");
            // 1 订购 2 退购
            String orderType = paramMap.get("orderType");
            // 密钥
            String secrtKey = paramMap.get("secretKey");
            // 版本
            String sion = paramMap.get("version");
            int version = Integer.parseInt(sion);
            // 生效方式 1 立即生效 2 下半月生效 3 下月生效
            String effectType = paramMap.get("effectType");
            // 生成随机字符串
            String echo = getRandomString(8);
            // 生成时间戳
            String timestamp = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
            Map<String, Object> item = new HashMap<String, Object>();
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            item.put("effectType", effectType);
            item.put("mobile", phoneNo);
            item.put("packCode", packCode);
            items.add(item);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("custInteId", custInteId);
            data.put("SecrtKey", secrtKey);
            data.put("echo", echo);
            data.put("orderId", orderNo);
            data.put("version", version);
            data.put("orderType", orderType);
            data.put("timestamp", timestamp);
            data.put("items", items);
            String xml = SanWangTongTrafficService.doOrderXML(data);
            String response = "";// SanWangTongTrafficService.doPostOrder(xml, url);
            Map<String, String> resultMap = XmlUtils.readXmlToMap(response);
            if (resultMap != null)
            {
                // 执行结果代码，0000 表示成功。其他表示失败
                String result = resultMap.get("result");
                // 失败时的错误描述
                String desc = resultMap.get("desc");
                map.put("resultCode", result + ":" + desc);
                if ("0000".equals(result))
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;
                }
            }
        }
        catch (Exception e)
        {
            log.error("三网通充值接口出错" + e);
        }
        map.put("status", 3);
        return map;
    }
    
    /**
     * 生成随机字符串
     * 
     * @param length 字符串长度
     * @return
     */
    public static String getRandomString(int length)
    { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++)
        {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 拼接产品编码
     * */
    public String getPkgCode(String scope, String spec)
    {
        String str = "";
        if (scope.equals("1"))
        {
            str = "2";
        }
        else
        {
            str = "1";
        }
        int length = spec.length();
        if (length == 1)
        {
            str = str + "0000" + spec;
        }
        else if (length == 2)
        {
            str = str + "000" + spec;
        }
        else if (length == 3)
        {
            str = str + "00" + spec;
        }
        else if (length == 4)
        {
            str = str + "0" + spec;
        }
        else
        {
            str = "";
        }
        return str;
    }
    
}
