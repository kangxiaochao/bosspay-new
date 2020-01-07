package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.bonc.vip.bj.md5.VIPMD5;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 北京联通流量充值接口
 * 
 * @author cxj
 *
 */
public class BeijingUnicomFlowDeal implements BaseDeal
{
    Logger log = Logger.getLogger(getClass());
    
    public static Map<String, String> dataGear = new HashMap<String, String>();
    static
    {
        dataGear.put("10", "G1");
        dataGear.put("20", "G2");
        dataGear.put("50", "G3");
        dataGear.put("100", "G4");
        dataGear.put("200", "G5");
        dataGear.put("500", "G6");
        dataGear.put("1024", "G7");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        
        try
        {
            // 订购电话号码
            String mobile = (String)order.get("phone");
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            // 渠道工号
            String gonghao = paramMap.get("gonghao");
            String privateKey = paramMap.get("privateKey");// 平台的appKey
            // 生效类型
            String type = "0";
            // 接口服务器地址
            String host = paramMap.get("Host");
            // 接口服务端口
            String port = paramMap.get("Port");
            // 套餐
            String spec = new Double(order.get("value") + "").intValue() + "";
            // 流量产品档位代码
            String dataCode = dataGear.get(spec);
            // 计算签名
            String md5Str = privateKey + mobile + dataCode + type + gonghao;
            // String sign = MD5.ToMD5(md5Str);
            md5Str = VIPMD5.encode(md5Str);
            // 组装参数
            // String param = "t="+mobile+"&b="+dataCode+"&a=0"+"&u="+gonghao+"&m="+md5Str;
            // String linkUrl = "http://"+host+":"+port+"/ua/proOrder";
            String url =
                "http://" + host + ":" + port + "/ua/proOrder/t/" + mobile + "/b/" + dataCode + "/a/0" + "/u/"
                    + gonghao + "/m/" + md5Str;
            String result = "";// ToolHttp.post(false, url, "", null);
            // 平台订单号设置
            String orderId = order.get("orderId") + "";
            if (null == orderId || "".equals(orderId) || "null".endsWith(orderId))
            {
                orderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + mobile + GenerateData.getIntData(9, 2);
            }
            
            map.put("orderId", orderId);
            // post请求
            // String result = ToolHttp.post(false, linkUrl, param, contentType);
            // 判断是否成功
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                JSONObject resultJson = jObject.getJSONObject("result");
                String code = resultJson.getString("code");
                String message = resultJson.getString("message");
                // code=1为成功
                if ("1".equals(code))
                {
                    flag = 3;
                }
                else
                {
                    flag = 4;// 失败
                }
                map.put("resultCode", code + ":" + message);
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("北京联通流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
}
