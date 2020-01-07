package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dahanbank.eif.http.common.ResponseMsg;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * dahansantong单号码流量充值接口
 * 
 * @author cxj
 *
 */
public class DHSanTongFlowDeal implements BaseDeal
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
            // 手机号
            String mobiles = (String)order.get("phone");
            // 平台订单号设置
            String orderId = order.get("orderId") + "";
            if (null == orderId || "".equals(orderId) || "null".equals(orderId))
            {
                orderId =
                    ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + mobiles + GenerateData.getIntData(9, 4);
            }
            
            map.put("orderId", orderId);
            // 账号
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String account = paramMap.get("account");// 账户名
            String pwd = paramMap.get("pwd");// 接口密码
            // 套餐
            String packageSize = new Double(order.get("value") + "").intValue() + "";
            // 判断流量是否大于1000M，true后置为1000M;
            int val = Integer.parseInt(packageSize);
            if (val > 1000)
            {
                int i = (val / 1024) * 1000;
                packageSize = i + "";
            }
            ResponseMsg resMsg = new ResponseMsg(null, null, null, null);
            // DaHanClientUtil.orderFlow(linkUrl, account, pwd, mobiles, Integer.parseInt(packageSize), null, orderId);
            // 00 成功 非00失败
            if (null != resMsg)
            {
                String code = resMsg.getResultCode();
                String message = resMsg.getResultMsg();
                map.put("resultCode", code + ":" + message);
                if ("00".equals(code))
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
            log.error("大汉三通指通流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
}
