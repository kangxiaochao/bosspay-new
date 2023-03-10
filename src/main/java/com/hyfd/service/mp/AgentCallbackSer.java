package com.hyfd.service.mp;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XiChengAESUtils;
import com.hyfd.common.utils.XiChengMD5Utils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.deal.Flow.XiChengFlowDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.service.BaseService;

import sun.misc.BASE64Decoder;

@Service
public class AgentCallbackSer extends BaseService
{
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    AgentDao agentDao;
    
    @Autowired
    RabbitMqProducer mqProducer;
    
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    public static String CallbackStatus_Success = "success";
    
    public static String CallbackStatus_Fail = "fail";
    
    public static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKeGmnpNwP+gLz9gZJFEf7pVf2f4BjI1KpV/1e9DVixTKlFHSS4368RY7zXvIzsUoL+qep+gqbxOHHI9d6b1yYpUlEbpX9HHFdmTf+c5vtikiTQqQX4pPvKh8GFtqLRoS8gKhdB7eIAxjjzDJOHgqN5CvbNDL5MXyNVhElumAXz3AgMBAAECgYB3M2enrrutKCV2SvhEBSF9TGijae++ueXbCmMHJDqkv2hU/QwtPs0boMxU7Bt9Zmga+QrXmlIsEsha4THKMwl7o7mI/+JPViqBFMll76+dX5G6ZBHI9KsZM26CvNbDUhjeijvOwGCH0QH9+/5uzspBY/mruNnXsz10p0LP0etXwQJBAOd4EJVyjX5ZJ6gfBgxFqkx872JTaoWtMxmieJX1rVdrHpguCsdAgQvxd9wj8ngmFUMj8qQW6X2z5ifyJIUVSTMCQQC5R7WYa9f6mKRlgHEi0Iz+EG4exwy1VYEAM/a/DYrcMk128P96QB7Y9wbg9mxfBMhOMpcEV2s1vnW4GMEYE+UtAkEAgxbgbYMe1zuu1ewcBL8/n2nyOQF9Bo/8rLvzBxrIPQYRoaYRvJ6vdxAeDbLeE2WeDRRCyuOfAO3tnxnnzOctOQJAetuLFfwE5j3LXSbzXlON+nZonu38hk2hUcIjTwKq3mJYrZMWxTi5e8MqA4hDyioArKGtNArHo4acxqtbdxzSLQJAVgfbmQcbEOfyMq45UKfYf+grSbBdX3XYFv0Q+ja4hUwS56TbecBr0s+HEmu2c6TA74e6i9u17sn6b40d2vPerQ==";
    
    public static Logger log = Logger.getLogger(AgentCallbackSer.class);
    
    public void testCallback(HttpServletRequest request, HttpServletResponse response){
    	Map<String, Object> paramMap = getMaps(request);
        String orderId = paramMap.get("id") + "";
        Map<String, Object> order = orderDao.selectByOrderId(orderId);
        callback(order, CallbackStatus_Success);
    }
    
    /**
     * ????????????
     * 
     * @author lks 2017???3???21?????????8:03:19
     * @param orderIds
     * @return
     */
    public String callbackByHand(HttpServletRequest request, HttpServletResponse response)
    {
        String message = "";
        Map<String, Object> paramMap = getMaps(request);
        String orderIds = paramMap.get("orderIds") + "";
        String suId = paramMap.get("suId") + "";
        if (orderIds != null && !orderIds.equals(""))
        {
            String[] orderIdArr = orderIds.split(",");// ???????????????
            List<String> succList = new ArrayList<String>();// ??????????????????
            List<String> failList = new ArrayList<String>();// ??????????????????
            List<String> dealList = new ArrayList<String>();// ???????????????
            int tianmaoNum = 0;
            // ???????????????????????????????????????????????????????????????
            tianmaoNum = judgeTianmaoRole(suId, orderIdArr);
            // ??????0?????????
            if (tianmaoNum > 0)
            {
                message = "???????????????????????????????????????";
                return message;
            }
            for (int i = 0, size = orderIdArr.length; i < size; i++)
            {
                String orderId = orderIdArr[i];
                Map<String, Object> order = orderDao.selectByOrderId(orderId);
                if (order != null)
                {
                    String status = order.get("status") + "";// ??????
                    if (status.equals("2") || status.equals("4"))
                    {
                        status = CallbackStatus_Fail;
                    }
                    else if (status.equals("3"))
                    {
                        status = CallbackStatus_Success;
                    }
                    else
                    {
                        dealList.add(orderId);
                    }
                    if (callback(order, status))
                    {
                        succList.add(orderId);
                    }
                    else
                    {
                        failList.add(orderId);
                    }
                }
                else
                {
                    dealList.add(orderId);
                }
            }
            if (succList.size() > 0)
            {
                message += succList.size() + "????????????????????????\n";
            }
            if (failList.size() > 0)
            {
                message += failList.size() + "????????????????????????????????????" + getListStr(failList) + "???\n";
            }
            if (dealList.size() > 0)
            {
                message += dealList.size() + "???????????????????????????????????????" + getListStr(dealList) + "???";
            }
        }
        else
        {
            message = "???????????????";
        }
        return message;
    }
    
    /**
     * @Title:judgeTianmaoRole
     * @Description: ???????????????????????????(?????????????????????????????????????????????)
     * @author CXJ
     * @date 2017???6???7??? ??????5:12:10
     * @param @param suId
     * @param @param ids
     * @param @return
     * @return int ????????????
     * @throws
     */
    public int judgeTianmaoRole(String suId, String[] ids)
    {
        int tianmaoSum = 0;
        List<Map<String, Object>> billList = sysUserRoleDao.getHasSysRoleList(suId);
        boolean roleFlag = false;
        for (Map<String, Object> map : billList)
        {
            String srName = map.get("srName") + "";
            if ("??????".equals(srName) || "????????????".equals(srName))
            {
                roleFlag = true;
            }
        }
        if (roleFlag)
        {
            
            for (int i = 0; i < ids.length; i++)
            {
                String orderId = ids[i];
                Map<String, Object> order = orderDao.selectByOrderId(orderId);
                if (order != null)
                {
                    String agentId = order.get("agentId") + "";
                    if (!"6fc1e7e29f4142f48110bc6c5286d723".equals(agentId)
                        && !"81c2390df095475f86f0e9c6dff1cfd2".equals(agentId))
                    {
                        tianmaoSum += 1;
                    }
                }
            }
        }
        
        return tianmaoSum;
    }
    
    /**
     * ??????
     * 
     * @author lks 2017???3???21?????????8:03:34
     * @param order
     * @param status
     * @return
     */
    public boolean callback(Map<String, Object> order, String status)
    {
        boolean flag = false;
        String callbackUrl = (String)order.get("callbackUrl");// ????????????
        String orderId = (String)order.get("orderId");// ???????????????
        if(order.get("orderId") == null || "".equals(order.get("orderId"))){
        	orderId = (String)order.get("agentOrderId");
        }
        String customerOrderId = (String)order.get("agentOrderId");// ??????????????????
        String phoneNo = (String)order.get("phone");// ?????????
        String bizType = (String)order.get("bizType");// ????????????
        String agentId = (String) order.get("agentId");//?????????ID
        int spec = 0;
        if (bizType.equals("1"))
        {// ??????
            spec = (int)Double.parseDouble(order.get("value") + "");
        }
        else if (bizType.equals("2"))
        {// ??????
            spec = new Double(order.get("fee") + "").intValue();
        }
        String scope = "nation";
        Map<String,String> signMap = new HashMap<String,String>();
        signMap.put("orderId", orderId);
        signMap.put("customerOrderId", customerOrderId);
        signMap.put("phoneNo", phoneNo);
        signMap.put("spec", spec*100+"");
        signMap.put("scope", scope);
        signMap.put("status", status);
        String sign = getSignStr(agentId,signMap);
        String param =
            "orderId=" + orderId + "&customerOrderId=" + customerOrderId + "&phoneNo=" + phoneNo + "&spec=" + spec*100
                + "&scope=" + scope + "&status=" + status+"&signature="+sign;
        String response = get(callbackUrl + "?" + param);
        
        if ("success".equals(response))
        {
            flag = true;
        }
        return flag;
    }
    
    public boolean kongChongCallback(Map<String, Object> order, String status)
    {
        boolean flag = false;
        String callbackUrl = (String)order.get("callbackUrl");// ????????????
        String orderId = (String)order.get("orderId");// ???????????????
        if(order.get("orderId") == null || "".equals(order.get("orderId"))){
        	orderId = (String)order.get("agentOrderId");
        }
        String customerOrderId = (String)order.get("agentOrderId");// ??????????????????
        String phoneNo = (String)order.get("phone");// ?????????
        String bizType = (String)order.get("bizType");// ????????????
        String agentId = (String)order.get("agentId");//?????????ID
        String voucher = (String)order.get("voucher");//0034?????????
        int spec = 0;
        if (bizType.equals("1"))
        {// ??????
            spec = (int)Double.parseDouble(order.get("value") + "");
        }
        else if (bizType.equals("2"))
        {// ??????
            spec = new Double(order.get("fee") + "").intValue();
        }
        String scope = "nation";
        Map<String,String> signMap = new HashMap<String,String>();
        signMap.put("orderId", orderId);
        signMap.put("customerOrderId", customerOrderId);
        signMap.put("phoneNo", phoneNo);
        signMap.put("spec", spec*100+"");
        signMap.put("scope", scope);
        signMap.put("status", status);
        signMap.put("voucher", voucher);
        String sign = getSignStr(agentId,signMap);
        String param =
            "orderId=" + orderId + "&customerOrderId=" + customerOrderId + "&phoneNo=" + phoneNo + "&spec=" + spec*100
                + "&scope=" + scope + "&status=" + status + "&voucher=" + voucher +"&signature="+sign;
        String response = get(callbackUrl + "?" + param);
        
        if ("success".equals(response))
        {
            flag = true;
        }
        return flag;
    }
    
    public boolean xiChengCallback(Map<String, Object> order, String status)
    {
        boolean flag = false;
        try
        {
            
            String callbackUrl = (String)order.get("callbackUrl");// ????????????
            String resultStatus = "";
            String resultCode = "";
            if (CallbackStatus_Success.equals(status))
            {
                resultStatus = "1";
                resultCode = "00000";
            }
            else
            {
                resultStatus = "0";
                resultCode = "51000";
            }
            String orderId = (String)order.get("agentOrderId");// ??????????????????;
            String channelOrderId = (String)order.get("orderId");// ???????????????
            String phoneNumber = (String)order.get("phone");// ?????????
            String spec = new Double(order.get("value") + "").intValue() + "";
            String providerId = order.get("providerId") + "";
            // ??????ID;
            String prodId = XiChengFlowDeal.getProdId(spec, providerId);
            JSONObject json = new JSONObject();
            json.put("status", resultStatus);
            json.put("resultCode", resultCode);
            json.put("orderId", orderId);
            json.put("channelOrderId", channelOrderId);
            json.put("phoneNumber", phoneNumber);
            json.put("prodId", prodId);
            json.put("prodName", "");
            json.put("channel", "");
            json.put("province", "");
            json.put("city", "");
            json.put("validBeginDate", "");
            json.put("validEndDate", "");
            json.put("validBeginTime", "");
            json.put("validEndTime", "");
            json.put("payAmount", "");
            json.put("ext", "");
            json.put("extDesc", "");
            JSONArray jsonArr = new JSONArray();
            jsonArr.add(json);
            JSONObject paramJson = new JSONObject();
            String time = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS");// ?????????????????????
            Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey("0000000075");
            String defaultParameter = (String)channel.get("default_parameter");// ????????????
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            String securitKey = paramMap.get("securitKey");// ????????????key
            String partyId = paramMap.get("partyId");
            String bill = XiChengAESUtils.encrypt(jsonArr.toString(), securitKey);
            String sign = XiChengMD5Utils.getSignAndMD5(partyId, bill, time);
            paramJson.put("partyId", partyId);
            paramJson.put("bill", bill);
            paramJson.put("time", time);
            paramJson.put("sign", sign);
            
            String contentType = "application/x-www-form-urlencoded";
            // post??????
            String result = ToolHttp.post(false, callbackUrl, paramJson.toString(), contentType);
            if (result != null)
            {
                JSONObject jObject = JSONObject.parseObject(result);
                
                String respStatus = jObject.getString("status");
                if ("1".equals(respStatus))
                {
                    flag = true;
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flag;
    }
    
    public static String get(String url)
    {
        CloseableHttpClient httpClient = null;
        try
        {
            httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig =
                RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// ?????????????????????????????????
            httpget.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    String out = EntityUtils.toString(entity, "UTF-8");
                    return out;
                }
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                if (null != httpClient)
                {
                    httpClient.close();
                }
            }
            catch (IOException e)
            {
            }
        }
        return null;
    }
    
    public static String getListStr(List<String> list)
    {
        String str = "";
        for (int i = 0, size = list.size(); i < size; i++)
        {
            if (i == size - 1)
            {
                str += list.get(i);
            }
            else
            {
                str += list.get(i) + ",";
            }
        }
        return str;
    }
    
    public String getSignStr(String agentId,Map<String,String> map){
    	if(agentId != null && !agentId.equals("")){
    		Map<String,Object> agent = agentDao.selectById(agentId);
    		if(agent != null){
    			try {
//    				String app_key = (String) agent.get("app_key");
    				String app_key = privateKey;
					return sign(map,app_key);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
					return "";
				}
    		}
    	}
    	return "";
    }
    
    /**
     * ??????
     * @param paramMap
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String sign(Map<String, String> paramMap,String privateKeyStr) throws Exception {
    	byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        
        // ??????????????????????????????
        String content = setParamStr(paramMap);
        log.debug("????????????????????????["+content+"]");
        // MD5??????
        String md5 = MD5.ToMD5(content);
        
        byte[] rsa = encodeBytePrivate(md5.getBytes(),privateKey);
        return Hex.encodeHexString(rsa);
    }
    
    /**
     * <h5>??????:</h5>??????????????????????????????
     * @param paramMap
     * @return 
     *
     * @author zhangpj	@date 2016???9???9???
     */
    public static String setParamStr(Map<String, String> paramMap) {
        // ??????
        List<String> paramNames = new ArrayList<String>();
        for (String paramKey : paramMap.keySet()) {
            paramNames.add(paramKey);
        }

        // ??????
        Collections.sort(paramNames);

        // ???????????????????????????
        StringBuilder paramUrl = new StringBuilder();
        for (String paramName : paramNames) {
            paramUrl.append(paramName).append("=").append(paramMap.get(paramName)).append("&");
        }
        paramUrl.deleteCharAt(paramUrl.length() - 1);

        String content = paramUrl.toString();
        return content;
    }
    
    /**
     * RSA
     * 
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }
    
}
