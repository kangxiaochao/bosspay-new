package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * zhitong单号码流量充值接口
 * 
 * @author cxj
 *
 */
public class WFZhitongFlowDeal implements BaseDeal
{
    Logger log = Logger.getLogger(getClass());
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        
        try
        {
            // 命令
            String action = "charge";
            // 版本号
            String ver = "1.1";
            // 手机号
            String mobile = (String)order.get("phone");
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + mobile + GenerateData.getIntData(9, 4);
            }
            String outtradeno = curids;
            map.put("orderId", curids);
            // 账号
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String account = paramMap.get("account");// 账户名
            String apiKey = paramMap.get("apiKey");// 平台的appKey
            // 0 漫游流量 1非漫游流量，不带改参数时默认为0
            String range = paramMap.get("type");
            // 套餐
            String spec = new Double(order.get("value") + "").intValue() + "";
            // 判断流量是否大于1000M，true后置为1000M;
            int val = Integer.parseInt(spec);
            if (val > 1000)
            {
                int i = (val / 1024) * 1000;
                spec = i + "";
            }
            // 计算签名
            String md5Str = "account=" + account + "&mobile=" + mobile + "&package=" + spec + "&key=" + apiKey;
            String sign = MD5.ToMD5(md5Str);
            // 组装参数
            String param =
                "v=" + ver + "&action=" + action + "&outtradeno=" + outtradeno + "&account=" + account + "&mobile="
                    + mobile + "&package=" + spec + "&sign=" + sign;
            String contentType = "application/x-www-form-urlencoded";
            // post请求
            String result = "";// ToolHttp.post(false, linkUrl, param, contentType);
            // 判断是否成功
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                String taskId = jObject.getString("TaskID");
                map.put("providerOrderId", taskId);
                String code = jObject.getString("Code");
                String message = jObject.getString("Message");
                map.put("resultCode", code + ":" + message);
                // 任务编码=0为失败
                if (!"0".equals(taskId))
                {
                    // code=0为成功
                    if ("0".equals(code))
                    {
                        flag = 1;
                    }
                    else
                    {
                        flag = 0;
                    }
                }
                else
                {
                    flag = 0;
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("潍坊指通流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
}
