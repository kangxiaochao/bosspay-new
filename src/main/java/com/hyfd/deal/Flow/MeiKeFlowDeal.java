package com.hyfd.deal.Flow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

public class MeiKeFlowDeal implements BaseDeal
{
    
    Logger log = Logger.getLogger(getClass());
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            String phone = (String)order.get("phone");// 手机号
            String value = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");
            String dataType = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            String scope = dataType.equals("1") ? "province" : "nation";
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            String callbackUrl = (String)channel.get("callback_url");// 回调地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String Userid = paramMap.get("Userid");// 用户编号
            String Uname = paramMap.get("Uname");// 用户名
            String Password = MD5.ToMD5(paramMap.get("Password"));// 密码
            String SOAPURI = paramMap.get("SOAPURI");// 链接地址
            // 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids =
                    DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 4);
            }
            map.put("orderId", curids);
            Service service = new Service();
            Call call = (Call)service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(linkUrl));// 设置地址
            call.setSOAPActionURI(SOAPURI + "getMyPhoneCharge");// 要调用方法的url
            call.setOperationName(new QName(SOAPURI, "getMyPhoneCharge"));// 设置操作的名称
            // 设置头信息
            SOAPHeaderElement soapHeaderElement = new SOAPHeaderElement(SOAPURI, "MySoapHeader");
            soapHeaderElement.setNamespaceURI(SOAPURI);
            soapHeaderElement.addChildElement("Uname").setValue(Uname);
            soapHeaderElement.addChildElement("Password").setValue(Password);
            soapHeaderElement.addChildElement("Userid").setValue(Userid);
            soapHeaderElement.addChildElement("thirdPlatOrderNumber").setValue(curids);
            call.addHeader(soapHeaderElement);
            call.setReturnType(XMLType.XSD_STRING);
            call.addParameter(new QName(SOAPURI, "phoneNumber"),
                org.apache.axis.encoding.XMLType.XSD_STRING,
                javax.xml.rpc.ParameterMode.IN);
            call.addParameter(new QName(SOAPURI, "flowSize"),
                org.apache.axis.encoding.XMLType.XSD_STRING,
                javax.xml.rpc.ParameterMode.IN);
            call.addParameter(new QName(SOAPURI, "phoneType"),
                org.apache.axis.encoding.XMLType.XSD_STRING,
                javax.xml.rpc.ParameterMode.IN);
            
            call.addParameter(new QName(SOAPURI, "thirdPlatOrderNumber"),
                org.apache.axis.encoding.XMLType.XSD_STRING,
                javax.xml.rpc.ParameterMode.IN);
            String result = "";// (String)call.invoke(new Object[] {phone, value, "1", curids});//
                               // carriertype移动是1，联通是2，电信是3
            if (!result.equals("") && result != null)
            {
                Map<String, String> resultMap = readXml(result);// 读取结果为map
                String status = resultMap.get("status");
                String msg = resultMap.get("msg");
                map.put("resultCode", status + ":" + msg);
                if ("0000".equals(status))
                {
                    String upids = resultMap.get("chargeid");// 上家订单号
                    map.put("providerOrderId", upids);
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
            log.error("美科流量充值出错" + e.getMessage() + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    public static Map<String, String> readXml(String xml)
    {
        Document doc = null;
        Map<String, String> map = new HashMap<String, String>();
        try
        {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            String status = rootElt.elementTextTrim("status");
            String msg = rootElt.elementTextTrim("msg");
            map.put("status", status);
            map.put("msg", msg);
            String chargeid = rootElt.elementTextTrim("chargeid");
            map.put("chargeid", chargeid);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    
}
