package com.hyfd.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

public class SanWangTongTrafficService
{
    
    public static String doOrderXML(Map<String, Object> order)
    {
        StringBuffer xml = new StringBuffer("<request><head><custInteId>");
        xml.append(order.get("custInteId") + "")
            .append("</custInteId>")
            .append("<echo>")
            .append(order.get("echo") + "")
            .append("</echo>")
            .append("<orderId>")
            .append(order.get("orderId") + "")
            .append("</orderId>")
            .append("<timestamp>")
            .append(order.get("timestamp") + "")
            .append("</timestamp>")
            .append("<orderType>")
            .append(order.get("orderType") + "")
            .append("</orderType>")
            .append("<version>")
            .append(order.get("version") + "")
            .append("</version>")
            .append("<chargeSign>")
            .append(buildChargeSgin(order))
            .append("</chargeSign></head><body>");
        List<Map<String, Object>> items = (List<Map<String, Object>>)order.get("items");
        for (Map<String, Object> item : items)
        {
            xml.append("<item><mobile>")
                .append(item.get("mobile") + "")
                .append("</mobile>")
                .append("<packCode>")
                .append(item.get("packCode") + "")
                .append("</packCode>")
                .append("<effectType>")
                .append(item.get("effectType") + "")
                .append("</effectType></item>");
        }
        xml.append("</body></request>");
        return xml.toString();
    }
    
    public static String buildChargeSgin(Map<String, Object> order)
    {
        String result = "";
        String source =
            order.get("custInteId") + "" + order.get("orderId") + "" + order.get("SecrtKey") + "" + order.get("echo")
                + "" + order.get("timestamp") + "";
        try
        {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] bts = digest.digest(source.getBytes("utf-8"));
            BASE64Encoder encoder = new BASE64Encoder();
            result = encoder.encode(bts);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String doPostOrder(String xml, String url)
    {
        String ret = "";
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(xml);
            post.setEntity(entity);
            CloseableHttpResponse resp = httpclient.execute(post);
            ret = EntityUtils.toString(resp.getEntity());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ret;
    }
    
}
