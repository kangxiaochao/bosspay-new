package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ShanDongDianXinDes;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * @ClassName: ShanDongDianXinFlowDeal
 * @Description: 山东电信流量充值接口
 * @author CXJ
 * @date 2017年4月10日 上午9:17:33
 * @version 1.0
 */
public class ShanDongDianXinFlowDeal implements BaseDeal
{
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
            // 手机号
            String phoneNumber = order.get("phone") + "";
            // 流量值
            String flowValue = order.get("value") + "";
            // 获取产品包
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids =
                    ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNumber + GenerateData.getIntData(9, 4);
            }// 客户端订单流水
            String clientNum = curids;
            map.put("orderId", curids);
            // 获取通道参数
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");
            // 充值地址
            String linkUrl = (String)channel.get("link_url");
            // 默认参数
            String defaultParameter = (String)channel.get("default_parameter");
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            // 支付密码
            String payPassword = paramMap.get("payPassword");
            // 流量类型，适用范围，1是省内，2是国内
            String offerType = paramMap.get("offerType");
            // 加密key
            String desKey = paramMap.get("desKey");
            // 认证码加密前
            String myCode = paramMap.get("myCode");
            // 编码
            String charset = paramMap.get("charset");
            // 接口调用认证码
            String authenticationCode = ShanDongDianXinDes.encrypt(myCode, desKey);
            Map<String, String> dataMap = new HashMap<String, String>();
            String dispacthInfo =
                "{'clientNum':'" + clientNum + "','payPassword':'" + payPassword + "','items':[{'phoneNumber':'"
                    + phoneNumber + "','offerType':" + offerType + ",'flowValue':'" + flowValue
                    + "'}],'authenticationCode': '" + authenticationCode + "' }";
            System.out.println(dispacthInfo);
            dataMap.put("dispatchInfo", dispacthInfo);
            String result = "";// ToolHttp.post4sddx(linkUrl, dataMap, charset);
            if (result != null)
            {
                JSONObject resultJson = JSONObject.parseObject(result);// 返回信息
                // 结果码
                String resultCode = resultJson.get("resultCode") + "";
                // 错误码
                String errorCode = resultJson.get("errorCode") + "";
                // 失败信息
                String errorMsg = resultJson.get("errorMsg") + "";
                map.put("resultCode", errorCode + ":" + errorMsg);
                if ("0".equals(resultCode))
                {
                    flag = 1;
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
            log.error("山东电信流量充值出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
}
