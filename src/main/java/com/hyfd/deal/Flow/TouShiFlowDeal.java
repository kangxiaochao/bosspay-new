package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class TouShiFlowDeal implements BaseDeal
{
    
    public static Map<String, String> errorMap = new HashMap<String, String>();
    static
    {
        errorMap.put("0", "无错误	请求已被后台接收");
        errorMap.put("1003", "用户ID或接口密码错误");
        errorMap.put("1004", "用户IP错误");
        errorMap.put("1005", "用户接口已关闭");
        errorMap.put("1006", "加密结果错误");
        errorMap.put("1007", "订单号不存在");
        errorMap.put("1011", "号码归属地未知");
        errorMap.put("1013", "手机对应的商品有误或者没有上架");
        errorMap.put("1014", "无法找到手机归属地");
        errorMap.put("1015", "余额不足");
        errorMap.put("1017", "产品未分配用户，联系商务");
        errorMap.put("1018", "订单生成失败"); // 可重新下单
        errorMap.put("1019", "充值号码与产品不匹配");
        errorMap.put("1020", "号码运营商未知");
        errorMap.put("9998", "参数有误");
        errorMap.put("9999", "系统错误");
    }
    
    Logger log = Logger.getLogger(getClass());
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phone = (String)order.get("phone");// 手机号
            String spec = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            String dataType = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String userid = paramMap.get("userid");// 账户名
            String pwd = paramMap.get("pwd");// 密码
            String key = paramMap.get("key");// 加密密钥
            // 平台订单号提交参数
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 4);
            }
            map.put("orderId", curids);
            int area = dataType.equals("2") ? 0 : 1;// 充值范围
            int effecttime = 0;// 生效日期 0 即时生效，1次日生效，2 次月生效
            int validity = 0;// 流量有效期 传入月数，0为当月有效
            String times = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmss");
            String signStr =
                "userid" + userid + "pwd" + pwd + "orderid" + curids + "account" + phone + "gprs" + spec + "area"
                    + area + "effecttime" + effecttime + "validity" + validity + "times" + times + key;// 加密串
            String userKey = MD5.ToMD5(signStr);// 加密结果
            linkUrl =
                linkUrl + "/gprsChongzhiAdvance.do?userid=" + userid + "&pwd=" + pwd + "&orderid=" + curids
                    + "&account=" + phone + "&gprs=" + spec + "&area=" + area + "&effecttime=" + effecttime
                    + "&validity=" + validity + "&times=" + times + "&userkey=" + userKey;
            String result = "";// ToolHttp.get(false, linkUrl);
            if (result != null)
            {
                Map<String, String> resultMap = XmlUtils.readXmlToMap(result);// 返回结果
                String error = resultMap.get("error");// 错误码
                String state = resultMap.get("state");// 订单状态
                String Porderid = resultMap.get("Porderid");
                map.put("providerOrderId", Porderid);
                map.put("resultCode", error + ":" + errorMap.get(error));
                if (error.equals("0") && (state.equals("0") || state.equals("8")))
                {// 提交成功
                    flag = 1;
                }
                else
                {// 提交失败
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
            log.error("投石米流量出错" + e + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
}
