package com.hyfd.deal.Flow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;

/**
 * 黑龙江电信业务处理
 * 
 * @author Administrator
 *
 */
public class HeiLongJiangDXFlowDeal implements BaseDeal
{
    private static Logger log = Logger.getLogger(HeiLongJiangDXFlowDeal.class);
    
    // 平台销售ID
    public static Map<String, String> platOfferIdMap = new HashMap<String, String>();
    static
    {
        // 全国流量包
        platOfferIdMap.put("10_2", "40002204");
        platOfferIdMap.put("30_2", "40002205");
        platOfferIdMap.put("50_2", "40002206");
        platOfferIdMap.put("100_2", "40002207");
        platOfferIdMap.put("200_2", "40002208");
        platOfferIdMap.put("300_2", "40002209");
        platOfferIdMap.put("500_2", "40002211");
        platOfferIdMap.put("1024_2", "40002212");
        // 省内流量包
        platOfferIdMap.put("10_1", "40002196");
        platOfferIdMap.put("30_1", "40002197");
        platOfferIdMap.put("50_1", "40002198");
        platOfferIdMap.put("100_1", "40002199");
        platOfferIdMap.put("200_1", "40002200");
        platOfferIdMap.put("300_1", "40002201");
        platOfferIdMap.put("500_1", "40002202");
        platOfferIdMap.put("1024_1", "40002203");
    }
    
    /**
     * 黑龙江电信业务处理
     * 
     * @param obj
     */
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try
        {
            // 获取订单参数
            String phoneNo = (String)order.get("phone");// 手机号
            String spec = new Double(order.get("value") + "").intValue() + "";// 流量包大小
            Map<String, Object> pkg = (Map<String, Object>)order.get("pkg");// 获取产品包
            String scope = (String)pkg.get("data_type");// 适用范围，1是省内，2是国内
            
            // 生成自己的id，供回调时查询数据使用
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
                curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + GenerateData.getStrData(10);
            }
            // 获取物理通道信息
            Map<String, Object> channel = (Map<String, Object>)order.get("channel"); // 获取物理通道
            String linkUrl = channel.get("link_url") + "";// 充值地址
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
            // String rechargeFlowUrl = paramMap.get("rechargeFlowUrl").toString(); // 充值地址,这是正式环境,没有测试环境
            String queryRechargeUrl = paramMap.get("queryRechargeUrl").toString();// 查询订单受理详情地址,这是正式环境,没有测试环境
            String staffCode = paramMap.get("staffCode").toString(); // 渠道ID
            String signKey = paramMap.get("signKey").toString(); // 密钥,现在是原接口的密钥
            String ivstr = paramMap.get("ivstr").toString(); // 加密向量,,现在是原接口的加密向量
            String pricePlanCd = platOfferIdMap.get(spec + "_" + scope); // 销售品ID
            String transactionId = curids; // 业务流水号
            map.put("orderId", curids);
            boolean isOk = true; // 提交状态是否正常
            String errorMessage = "";
            // 1.验证是否有对应的销售品ID
            if (null == pricePlanCd)
            {
                errorMessage = "黑龙江电信:没有流量包为[" + spec + "MB]的销售品ID,充值号码[" + phoneNo + "],curids[" + curids + "]!";
                log.error(errorMessage);
                
                isOk = false;
            }
            else
            {
                String sign = Encrypt(md5Encode((phoneNo + staffCode + signKey).toUpperCase()), signKey, ivstr); // AES加密
                JSONObject reqJson = new JSONObject();
                reqJson.put("reqTime", DateUtils.getNowTime()); // 请求时间
                reqJson.put("staffCode", staffCode);
                reqJson.put("transactionId", transactionId);
                reqJson.put("userid", phoneNo);
                reqJson.put("pricePlanCd", pricePlanCd);
                reqJson.put("sign", sign);
                
                // 2.发起充值请求
                String data = "";// ToolHttp.post(false, linkUrl, reqJson.toString(), null);
                if (data == null)
                {
                    errorMessage = "黑龙江电信[流量充值]请求:超时,充值号码[" + phoneNo + "],curids[" + curids + "]!";
                    log.error(errorMessage);
                    
                    isOk = false;
                }
                else
                {
                    // 3.解析充值请求数据
                    JSONObject jsonObject = JSON.parseObject(data);
                    String result = jsonObject.getString("result"); // 返回状态码(接口为实时):0成功 其它 失败
                    String resultMsg = jsonObject.getString("resultMsg"); // 响应结果描述
                    map.put("resultCode", result + ":" + resultMsg);
                    resultMsg = StringUtil.getNewColumnValueForSql(resultMsg);
                    if (result.equals("0"))
                    {
                        errorMessage =
                            "黑龙江电信[流量充值]请求:提交成功,充值号码[" + phoneNo + "],curids[" + curids + "],result[" + result
                                + "],resultMsg[" + resultMsg + "]!";
                        log.error(errorMessage);
                        flag = 3; // 提交成功
                    }
                    else
                    {
                        errorMessage =
                            "黑龙江电信[流量充值]请求:提交失败,充值号码[" + phoneNo + "],curids[" + curids + "],result[" + result
                                + "],resultMsg[" + resultMsg + "]!";
                        log.error(errorMessage);
                        flag = 4; // 提交失败
                    }
                }
            }
            if (isOk == false)
            {
                int queryCount = 0; // 查询次数
                boolean isRechargeFlowOk = false; // 充值是否成功
                
                String sign = Encrypt(md5Encode((staffCode + signKey).toUpperCase()), signKey, ivstr); // AES加密
                JSONObject reqJson = new JSONObject();
                reqJson.put("reqTime", DateUtils.getNowTime());
                reqJson.put("staffCode", staffCode);
                reqJson.put("transactionId", transactionId);
                reqJson.put("sign", sign);
                
                while (queryCount < 3)
                {
                    // 订单查询接口,如果返回值是null则代表没有查询到订单
                    String data = excute(queryRechargeUrl, reqJson.toString());
                    
                    log.error("黑龙江电信[流量订单]查询,充值号码[" + phoneNo + "],curids[" + curids + "]返回信息[" + data + "]");
                    if (null == data)
                    {
                        queryCount++;
                        try
                        {
                            TimeUnit.SECONDS.sleep(2);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try
                        {
                            // 解析数据
                            JSONObject jsonObject = JSON.parseObject(data);
                            System.err.println("jsonObject:" + jsonObject);
                            String result = jsonObject.getString("result"); // 0成功 其它 失败
                            if ("0".equals(result))
                            {
                                // 获取json数组
                                JSONArray bodysArray = jsonObject.getJSONArray("bodys");
                                ; // 成功返回的消息体
                                JSONObject bodys = (JSONObject)bodysArray.get(0);
                                
                                String recharge_resultMsg = bodys.getString("recharge_resultMsg"); // 充值结果描述
                                String recharge_result = bodys.getString("recharge_result"); // 充值结果状态
                                System.out.println("黑龙江电信[流量订单]查询成功,充值结果状态[" + recharge_result + "],充值结果描述["
                                    + recharge_resultMsg + "]");
                                isRechargeFlowOk = true; // 充值成功
                                map.put("resultCode", recharge_result + ":" + recharge_resultMsg);
                                break;
                            }
                            else
                            {
                                queryCount++;
                                TimeUnit.SECONDS.sleep(2);
                            }
                        }
                        catch (Exception e)
                        {
                            try
                            {
                                TimeUnit.SECONDS.sleep(2);
                            }
                            catch (InterruptedException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                // 如果查询三次都没有获取到信息,则默认为此订单提交失败
                if (!isRechargeFlowOk)
                {
                    flag = 3; // 充值成功
                }
                else
                {
                    flag = 4; // 充值失败
                }
            }
        }
        // catch (ConnectTimeoutException | SocketTimeoutException e)
        // {// 请求、响应超时
        // flag = -1;
        // }
        catch (Exception e)
        {
            log.error("黑龙江电信[流量充值]出错" + e.getMessage() + "||" + MapUtils.toString(order));
        }
        map.put("status", 3);
        return map;
    }
    
    /***
     * @param 进行md5加密
     * */
    public static String md5Encode(String str)
    {
        StringBuffer buf = new StringBuffer();
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte bytes[] = md5.digest();
            for (int i = 0; i < bytes.length; i++)
            {
                String s = Integer.toHexString(bytes[i] & 0xff);
                if (s.length() == 1)
                {
                    buf.append("0");
                }
                buf.append(s);
            }
        }
        catch (Exception ex)
        {
        }
        return buf.toString().toLowerCase();// 转换成小写字母
    }
    
    // -----------------------↓↓↓↓↓↓AES加密解密处理↓↓↓↓↓↓-----------------------
    
    /**
     * <h5>功能:</h5>AES加密
     * 
     * @param sSrc 加密内容
     * @param Skey 加密密钥
     * @param ivstr 加密向量
     * @return String 加密信息
     *
     * @author zhangpj @date 2016年8月26日
     */
    public static String Encrypt(String sSrc, String Skey, String ivstr)
    {
        try
        {
            if (sSrc == null || sSrc.length() < 2)
            {
                return null;
            }
            if (Skey == null)
            {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (Skey.length() != 16)
            {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = Skey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
            IvParameterSpec iv = new IvParameterSpec(ivstr.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes());
            
            return encodeBytes(encrypted);
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    private static String encodeBytes(byte[] bytes)
    {
        StringBuffer strBuf = new StringBuffer();
        
        for (int i = 0; i < bytes.length; i++)
        {
            strBuf.append((char)(((bytes[i] >> 4) & 0xF) + ((int)'a')));
            strBuf.append((char)(((bytes[i]) & 0xF) + ((int)'a')));
        }
        return strBuf.toString();
    }
    
    // -----------------------↑↑↑↑↑↑AES加密解密处理↑↑↑↑↑↑-----------------------
    
    public static String excute(String url, String requestData)
    {
        String encoding = "UTF-8";
        int timeOut = 1000;
        
        HttpClientParams httpClientParams = new HttpClientParams();
        String contentType = "text/xml";
        
        httpClientParams.setContentCharset(encoding);
        httpClientParams.setHttpElementCharset(encoding);
        
        HttpClient httpClient = new HttpClient(httpClientParams);
        httpClient.getHostConfiguration().setHost(url);
        HttpConnectionManagerParams httpConnectionManagerParams = new HttpConnectionManagerParams();
        httpConnectionManagerParams.setConnectionTimeout(timeOut * 60);
        httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(1);
        httpConnectionManagerParams.setMaxTotalConnections(60);
        httpConnectionManagerParams.setSoTimeout(timeOut * 2);
        
        // httpClient.getHttpConnectionManager().setParams(httpConnectionManagerParams);
        String responseBody = null;
        PostMethod postMethod = null;
        try
        {
            postMethod = new PostMethod(url);
            // postMethod.setRequestHeader("Connection", "close");
            RequestEntity requestEntity = new StringRequestEntity(requestData, contentType, encoding);
            postMethod.setRequestEntity(requestEntity);
            httpClient.executeMethod(postMethod);
            // responseBody = postMethod.getResponseBodyAsString();
            InputStream inputStream = postMethod.getResponseBodyAsStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null)
            {
                stringBuffer.append(new String(str.getBytes(), "utf-8"));
            }
            responseBody = stringBuffer.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("编码异常:" + e.getMessage());
        }
        catch (HttpException e)
        {
            log.error("网络异常:" + e.getMessage());
        }
        catch (IOException e)
        {
            log.error("IO流异常:" + e.getMessage());
        }
        catch (Exception e)
        {
            log.error("异常:" + e.getMessage());
        }
        finally
        {
            try
            {
                postMethod.releaseConnection();
            }
            catch (Exception e2)
            {
                // TODO: handle exception
            }
        }
        return responseBody;
    }
}
