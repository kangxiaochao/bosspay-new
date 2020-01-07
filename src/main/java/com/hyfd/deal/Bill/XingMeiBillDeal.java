package com.hyfd.deal.Bill;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.RSAEncrypt;
import com.hyfd.common.utils.ToolHttps;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 星美话费充值
 * 
 * @author zhangjun
 *
 */
public class XingMeiBillDeal implements BaseDeal
{
    private static Logger log = Logger.getLogger(XingMeiBillDeal.class);
    
    private static RSAEncrypt rsaEncrypt = new RSAEncrypt();
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        
        try
        {
            String phone = (String)order.get("phone");// 手机号
            double fee = (double)order.get("fee");// 金额，以元为单位
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            
            // String url = paramMap.get("url");
            String type = paramMap.get("type");
            String ServiceCode = paramMap.get("ServiceCode");
            String SYS_ID = paramMap.get("SYS_ID");
            String PayAcct = paramMap.get("PayAcct");
            String Pwd = paramMap.get("Pwd");
            String RequestId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            String RequestTime = format.format(new Date());
            String RequestUser = "haobai";
            String RequestSource = paramMap.get("RequestSource");
            String ObjType = paramMap.get("ObjType");
            String BalanceType = paramMap.get("BalanceType");
            String RechargeUnit = paramMap.get("RechargeUnit");
            String RechargeType = paramMap.get("RechargeType");
            String TransactionID = paramMap.get("TransactionID");
            String RechargeAmount = String.valueOf(fee);
            String StaffId = "";
            map.put("orderId", RequestId);
            String response =
                sendPost(linkUrl,
                    type,
                    PayAcct,
                    Pwd,
                    RequestSource,
                    RequestUser,
                    RequestId,
                    RequestTime,
                    phone,
                    ObjType,
                    BalanceType,
                    RechargeUnit,
                    RechargeAmount,
                    RechargeType,
                    TransactionID,
                    ServiceCode,
                    SYS_ID,
                    StaffId);
            
            String result = "";
            String responseId = "";
            if (null == response)
            {
                log.error("星美话费充值请求超时" + MapUtils.toString(order));
            }
            else
            {
                JSONObject responseJson = readXmlToJson(response);
                responseId =
                    responseJson.getJSONObject("SvcCont")
                        .getJSONArray("SOO")
                        .getJSONObject(0)
                        .getJSONObject("RESP")
                        .getString("ResponseId");
                result =
                    responseJson.getJSONObject("SvcCont")
                        .getJSONArray("SOO")
                        .getJSONObject(0)
                        .getJSONObject("RESP")
                        .getString("Result");
                map.put("providerOrderId", responseId);
                if (!"0".equals(result))
                {
                    flag = 4;
                }
                else
                {
                    flag = 3;
                }
            }
            
        }
        catch (Exception e)
        {
            log.error("星美话费充值逻辑出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
    
    /**
     * 发送充值请求
     * 
     * @param url
     * @param type
     * @param PayAcct
     * @param Pwd
     * @param RequestSource
     * @param RequestUser
     * @param RequestId
     * @param RequestTime
     * @param DestinationId
     * @param ObjType
     * @param BalanceType
     * @param RechargeUnit
     * @param RechargeAmount
     * @param RechargeType
     * @param TransactionID
     * @param ServiceCode
     * @param SYS_ID
     * @param StaffId
     * @return
     */
    public static String sendPost(String url, String type, String PayAcct, String Pwd, String RequestSource,
        String RequestUser, String RequestId, String RequestTime, String DestinationId, String ObjType,
        String BalanceType, String RechargeUnit, String RechargeAmount, String RechargeType, String TransactionID,
        String ServiceCode, String SYS_ID, String StaffId)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:ns1=\"http://www.tydic.com/\"><SOAP-ENV:Header/><SOAP-ENV:Body><Business>");
        JSONObject jsonObject = new JSONObject(true);
        
        JSONObject tcpCont = new JSONObject();
        JSONObject svcCont = new JSONObject();
        
        JSONArray SOOArray = new JSONArray();
        
        JSONObject SOO = new JSONObject();
        
        Map<String, Object> map = new TreeMap<String, Object>(new Comparator<String>()
        {
            public int compare(String obj1, String obj2)
            {
                // 降序排序
                return obj1.compareTo(obj2);
            }
        });
        
        // JSONObject RECHARGE_REQ = new JSONObject(true);
        map.put("PayAcct", PayAcct);
        map.put("Pwd", Pwd);
        map.put("RequestSource", RequestSource);
        map.put("RequestUser", RequestUser);
        map.put("RequestId", RequestId);
        map.put("RequestTime", RequestTime);
        map.put("DestinationId", DestinationId);
        map.put("DestinationAttr", "8");
        map.put("DestinationAttrDetail", "9");
        map.put("ObjType", ObjType);
        map.put("BalanceType", BalanceType);
        map.put("RechargeUnit", RechargeUnit);
        map.put("RechargeAmount", RechargeAmount);
        map.put("RechargeType", RechargeType);
        map.put("RechargeType", RechargeType);
        map.put("StaffId", StaffId);
        SOO.put("RechargeReq", map);
        SOOArray.add(SOO);
        svcCont.put("SOO", SOOArray);
        String code = rsaEncrypt.MD5(svcCont.toString());
        // 把MD5加密内容通过私钥签名
        String signatureInfo = rsaEncrypt.enc(code);
        // 把MD5加密内容通过私钥签名
        tcpCont.put("TransactionID", TransactionID);
        tcpCont.put("ReqTime", RequestTime);
        tcpCont.put("SignatureInfo", signatureInfo);
        tcpCont.put("ServiceCode", ServiceCode);
        tcpCont.put("SYS_ID", SYS_ID);
        jsonObject.put("TcpCont", tcpCont);
        jsonObject.put("SvcCont", svcCont);
        
        xml.append(jsonObject);
        xml.append("</Business></SOAP-ENV:Body></SOAP-ENV:Envelope>");
        
        String ret = ToolHttps.post(true, url, xml.toString(), "text/html;charset=UTF-8");
        log.info("充值请求响应信息|" + ret + "|");
        return ret;
    }
    
    /**
     * xml文件解析
     * 
     * @param xml
     * @return
     */
    public static JSONObject readXmlToJson(String xml)
    {
        Document doc = null;
        try
        {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            Element body = rootElt.element("Body");
            String jsonStr = body.elementTextTrim("Business");
            JSONObject json = JSONObject.parseObject(jsonStr);
            return json;
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("解析出錯");
        return null;
    }
    
    public static void main(String[] args)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fee", 11.01);
        map.put("phone", "125635263 52");
        XingMeiBillDeal xingMeiBillDeal = new XingMeiBillDeal();
        xingMeiBillDeal.deal(map);
        
    }
    
}
