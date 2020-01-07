package com.hyfd.task;

import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;


@Component
public class HaiHangTask
{
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
    
    @Autowired
    OrderDao orderDao;// 订单
    
    @Autowired
    RabbitMqProducer mqProducer;// 消息队列生产者
    
    private static Logger log = Logger.getLogger(HaiHangTask.class);
    
    @Scheduled(fixedDelay = 60000)
    public void queryMinShengOrder()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        
        try
        {
            String id = "2000000008";// 海航物理通道ID ~~~~~
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("dispatcherProviderId", id);
            param.put("status", "1");
            List<Map<String, Object>> orderList = orderDao.selectByTask(param);
            for (Map<String, Object> order : orderList)
            {
            	int flag = 2;
                String orderId = order.get("orderId") + "";
                map.put("orderId", orderId);
                
                String queryResultJson=orderQuery(paramMap, orderId);
//                log.debug("海航查询结果：" + queryResultJson);
                
                JSONObject jsonObject = JSONObject.parseObject(queryResultJson).getJSONObject("msgResponse");
                JSONObject responseInfoJson = jsonObject.getJSONObject("responseInfo");
    			String result = responseInfoJson.getString("result");	// 响应信息结果，1代表成功返回数据，0表示没有数据返回，-1表示出现异常
    			if ("1".equals(result)) {
    				JSONObject dataJson = jsonObject.getJSONObject("data");
    				String resultCode = dataJson.getString("ResultCode");	// 返回错误码 ,0:操作成功
    				if ("0".equals(resultCode)) {
    					JSONObject orderItemJson = dataJson.getJSONObject("ORDER_ITEM");
    					String statCode = orderItemJson.getString("STAT_CODE");	// 交易状态码(-1：初始状态，0：充值成功，1充值失败，2：充值中，3：手工充值成功)
    					if("0".equals(statCode) || "3".equals(statCode)){
    						flag = 1;
    					}else if("1".equals(statCode)){
    						flag = 0;
    					}
    				}
    			}
    			
                map.put("status", flag);
                mqProducer.sendDataToQueue(RabbitMqProducer.Result_QueueKey, SerializeUtil.getStrFromObj(map));
            }
        }
        catch (Exception e)
        {
            log.error("海航查询Task出错" + e);
        }
    }
    
	private	String orderQuery(Map<String, String> map,String curids){
		String result="";
		try {
			String appSecret = map.get("appSecret");
			String appToken = map.get("appToken");
			String method = map.get("queryOrderMethod"); //	capability-package_recharge_queryOrderInfo
			String timestamp="";
			String format ="json";
			StringBuffer source = new StringBuffer();
			
			String sign = "";
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			timestamp = sdf.format(now);
			
			JSONObject appJson = new JSONObject();
			
			//封装应用请求包体
			appJson.put("hpno", map.get("hpno"));	// 平台定义200047
			appJson.put("orderNumber", curids);	// 订单号(30位以内，不能重复)
			
//			System.out.println(appJson.toString());
			//
			source.append(appSecret).append("AccessToken"+appToken).append("Format"+format).append("Method"+method)
			.append("Parameter"+appJson.toString()).append("Timestamp"+timestamp).append(appSecret);
			sign = md5Encode(source.toString()); // MD5加密大写
			
			// 封装http请求
			String urlString = "https://api.hnagroup.com";
			
			HttpPost httpRequst = new HttpPost(urlString);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("Method", method));
			nvps.add(new BasicNameValuePair("AccessToken", appToken));
			nvps.add(new BasicNameValuePair("Sign", sign));
			nvps.add(new BasicNameValuePair("Timestamp", timestamp));
			nvps.add(new BasicNameValuePair("Format", format));
			nvps.add(new BasicNameValuePair("Parameter", appJson.toString()));
			
			httpRequst.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
//			System.out.println("url="+httpRequst.getURI());	
			CloseableHttpClient httpclient = createSSLInsecureClient();
			HttpResponse httpResponse = httpclient.execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);// 取出应答字符串
			}else{
				httpclient.close();
			}
//			System.out.println("result="+result);	
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		
		return result;
	}

	private String md5Encode(String str)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			byte bytes[] = md5.digest();
			for(int i = 0; i < bytes.length; i++)
			{
				String s = Integer.toHexString(bytes[i] & 0xff);
				if(s.length()==1){
					buf.append("0");
				}
				buf.append(s);
			}
		}
		catch(Exception ex){	
		}
		return buf.toString().toUpperCase();//转换成大写字母
	}
	
	/**
	 * HTTPS访问对象，信任所有证书
	 * 
	 * @return
	 */
	public CloseableHttpClient createSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("haiHang createSSLInsecureClient error|" + e.getMessage());
		}
		return HttpClients.createDefault();
	}
}
