package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * @ClassName: YunShangFlowDeal
 * @Description: 云上流量充值接口
 * @author CXJ
 * @date 2017年4月8日 上午10:25:27
 * @version 1.0
 */
public class YunShangFlowDeal implements BaseDeal
{
    /**
     * 日志
     */
    private static final Logger log = Logger.getLogger(YunShangFlowDeal.class);
    
    /**
     * 充值成功
     */
    private static final String SUCCESS = "SUCCESS";
    
    /**
     * 充值失败
     */
    private static final String FAILED = "FAILED";
    
    /**
     * 订单充值中
     */
    private static final String UNDERWAY = "UNDERWAY";
    
    /**
     * 省内流量编码
     */
    public static Map<String, String> spuIdProvenceMap = new HashMap<String, String>();
    
    /**
     * 国内流量编码
     */
    public static Map<String, String> spuIdCountryMap = new HashMap<String, String>();
    static
    {
        spuIdProvenceMap.put("60", "642462802");
        spuIdProvenceMap.put("600", "693720663");
        spuIdProvenceMap.put("40", "647901131");
        spuIdProvenceMap.put("400", "603246980");
        spuIdProvenceMap.put("2048", "695023782");
        spuIdProvenceMap.put("200", "648372356");
        spuIdProvenceMap.put("1024", "677766872");
        spuIdProvenceMap.put("100", "612218428");
        
        spuIdCountryMap.put("50", "600917414");
        spuIdCountryMap.put("500", "628522797");
        spuIdCountryMap.put("30", "617685903");
        spuIdCountryMap.put("300", "673368122");
        spuIdCountryMap.put("20", "665648193");
        spuIdCountryMap.put("200", "602082949");
        spuIdCountryMap.put("1024", "627287152");
        spuIdCountryMap.put("100", "626170580");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            // 手机号
            String accountVal = order.get("phone") + "";
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids =
                    ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + accountVal + GenerateData.getIntData(9, 4);
            }// 渠道商订单号
            String tbOrderNo = curids;
            map.put("orderId", curids);
            // 流量值
            String spec = new Double(order.get("value") + "").intValue() + "";
            // 流量包
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            // 适用范围，1是省内，2是国内
            String dataType = (String)pkg.get("data_type");
            // 产品编号
            String spuId = null;
            if ("1".equals(dataType))// 省内
            {
                spuId = spuIdProvenceMap.get(spec);
            }
            else
            {
                // 国内
                spuId = spuIdCountryMap.get(spec);
            }
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            // 充值地址
            String linkUrl = paramMap.get("linkUrl");
            
            // 渠道商编号
            String supplierId = paramMap.get("supplierId");
            // 订单状态回调通知地址
            String notifyUrl = paramMap.get("notifyUrl");
            // 密钥
            String secretKey = paramMap.get("privateKey");
            // 客户经理编号
            String encoding = paramMap.get("encoding");
            // 活动编码
            String promotion = paramMap.get("promotion");
            
            String signStr =
                "accountVal" + accountVal + "encoding" + encoding + "promotion" + promotion + "spuId" + spuId
                    + "supplierId" + supplierId + "tbOrderNo" + tbOrderNo + secretKey;
            // 签名
            String sign = MD5.ToMD5(signStr);
            linkUrl =
                linkUrl + "?tbOrderNo=" + tbOrderNo + "&spuId=" + spuId + "&accountVal=" + accountVal + "&supplierId="
                    + supplierId + "&notifyUrl=" + notifyUrl + "&sign=" + sign + "&encoding=" + encoding
                    + "&promotion=" + promotion;
            String result = "";// ToolHttp.get(false, linkUrl);
            if (result != null)
            {
                Map<String, String> resultMap = XmlUtils.readXmlToMap(result);// 返回结果
                // 订单状态
                String coopOrderStatus = resultMap.get("coopOrderStatus");
                String errorCode = resultMap.get("errorCode");
                String errorDesc = resultMap.get("errorDesc");
                errorDesc = StringUtil.replaceBlank(errorDesc);
                String coopOrderNo = resultMap.get("coopOrderNo");
                map.put("providerOrderId", coopOrderNo);
                map.put("resultCode", errorCode + ":" + errorDesc);
                if (SUCCESS.equals(coopOrderStatus))// 实时充值成功
                {
                    flag = 3;
                }
                else if (FAILED.equals(coopOrderStatus))// 实时充值失败
                {
                    flag = 4;
                }
                else if (UNDERWAY.equals(coopOrderStatus))// 提交成功>>>>充值中
                {
                    flag = 1;
                }
                else
                {
                    flag = 0;// 提交失败
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("云上流量商城出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
}
