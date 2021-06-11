package com.hyfd.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.utils.*;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
public class HeMaTest {

    //下单地址
    private static final String tokenurl = "http://47.93.228.234:8081/pos/fee/flowrecharge";
    //查单地址
    private static final String taskurl = "http://47.93.228.234:8081/pos/fee/queryorder";
    private static final String  companyCode = "900101";  //企业编号(必填)
    private static final String  companyType = "1";  //企业类型(必填)
    private static final String  sign = "";         //加密串(必填)
    private static final String  amount = "1000";       //充值金额(已分为单位)
    private static final String  phoneNum = "16710577088";     //手机号码(必填)
    private static final String  customerId = UUID.randomUUID().toString();   //商户订单号，不能重复(可空)
    private static final String  orderTitle = "hfcz";   //（URLEncoder.encode(“订单名称”),"utf-8"))）(必填)




    public static void main(String[] args) throws Exception{
        //下单
     System.out.println(token());

        //查单
   //    System.out.println(task());
    }

    public static String token() throws Exception{


       String  sign = signs(companyCode,companyType,phoneNum,customerId,orderTitle,amount);
        Map<String,Object> map1 = new HashMap<String,Object>();
        Map<String,Object> map2 = new HashMap<String,Object>();
        Map<String,Object> map3 = new HashMap<String,Object>();
        map1.put("companyCode",companyCode);//必填
        map1.put("companyType",companyType);// 必填
        map1.put("sign",sign);
        map3.put("publicParams",map1);
        map2.put("orderTitle",orderTitle);
        map2.put("customerId",customerId);
        map2.put("phoneNum",phoneNum); //必填
        map2.put("amount",amount);//
        map3.put("businessParams",map2);




        String parameter = JSONObject.toJSONString(map3);

        System.out.println("parameter = " + parameter);

        //发送httppost请求
       String data = ToolHttp.post(false, tokenurl, parameter,"text/plain");
       // String s = doPost(tokenurl, maps);
        //String post = post(tokenurl, parameter, null);


        //解析返回数据
        /**
         * status 状态	0为接受订单成功，具体是否充值成功，异步通知里体现
         * message   状态描述
         * orderCode 流水号
         * customerId 商户订单号	提交的订单号，原路返回。
         */
       JSONObject response = JSON.parseObject(data);

        try {
            String status = response.get("status") + "";
            System.out.println("status = " + status);
            String message = response.get("message") + "";
            System.out.println("message = " + message);
            String orderCode = response.get("orderCode") + "";
            System.out.println("orderCode = " + orderCode);
            String customerorderId = response.get("customerId") + "";
            System.out.println("customerorderId = " + customerorderId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return "token success";
    }

    /**
     * status		String		状态	订单状态
     * 3：充值中
     * 4：充值成功
     * 5：充值失败
     * 99：订单不存在
     * else yichangdingdan
     * message		String		状态描述
     * orderCode		String		流水号
     * customerId		String		商户订单号	提交的订单号，原路返回。
     * @return
     */
    public static String task(){
        //md5Str为固定字符串
        String md5Str = "fb1e3eba-5d99-4aec-99db-00f4609d7270";

        StringBuffer str = new StringBuffer(md5Str);
        str.append("companyCode="+companyCode);
        str.append(":");
        str.append("companyType="+companyType);
        str.append(":");
        str.append("customerId="+"3e9231fa-ef75-4f3d-b2ac-27a0e4277ad5");
        str.append(":");
        str.append("orderCode="+"qmc90001623377081973vvurq"); //流水号,上家返回的.
        str.append(md5Str);
        try {
            String  sign = ToolMD5.encodeMD5Hex(str.toString()).toUpperCase();
            //发送httppost请求

            Map maps = new HashMap();
            Map maps2 = new HashMap();
            Map maps3 = new HashMap();
            maps.put("companyCode",companyCode);//必填
            maps.put("companyType",companyType);// 必填
            maps.put("sign",sign);
            maps2.put("publicParams",maps);
            //maps.put("customerId",customerId);
            maps3.put("orderCode","qmc90001623377081973vvurq");
            maps3.put("customerId","3e9231fa-ef75-4f3d-b2ac-27a0e4277ad5");
            maps2.put("businessParams",maps3);
            String redultjson = JSONObject.toJSONString(maps2);
            System.out.println("parameter = " + redultjson);

            String data = ToolHttp.post(false, taskurl, redultjson,"text/plain");

            JSONObject response = JSON.parseObject(data);

            try {
                String status = response.get("status") + "";
                System.out.println("status = " + status);
                String message = response.get("message") + "";
                System.out.println("message = " + message);
                String orderCode = response.get("orderCode") + "";
                System.out.println("orderCode = " + orderCode);
                String customerorderId = response.get("customerId") + "";
                System.out.println("customerorderId = " + customerorderId);
                System.out.println("查单成功");
            }catch (Exception e){
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



        return "task success";
    }

    //加密
    public static String signs(String companyCode,String companyType,String phoneNum,String customerId,String orderTitle,String amount) throws Exception {
        //md5Str为固定字符串
        String md5Str = "fb1e3eba-5d99-4aec-99db-00f4609d7270";
        StringBuffer str = new StringBuffer(md5Str);
        str.append("companyCode="+companyCode);
        str.append(":");
        str.append("companyType="+companyType);
        str.append(":");
        str.append("phoneNum="+phoneNum);
        str.append(":");
        str.append("customerId="+customerId);
        str.append(":");
        str.append("orderTitle="+orderTitle);
        str.append(":");
        str.append("amount="+amount);
        str.append(md5Str);
      //  String sign = (MD5Utils.getMD5(str.toString(),true,32));
        String  sign = ToolMD5.encodeMD5Hex(str.toString()).toUpperCase();


        System.out.println("加密sign"+ sign);
        return sign;
    }

    //充值结果异步通知接口
    /**
     * businessParams	platformOrderCode	String		服务流水号
     * businessParams	orderState	String		订单状态	4为充值成功，5为充值失败
     * businessParams	stateCode	String		订单响应码
     * businessParams	orderSource	String		接口类型	固定为4
     * businessParams	orderCode	String		服务订单号
     * businessParams	customerId	String		商户订单号
     * publicParams	companyCode	String		企业编号
     * publicParams	companyType	String		企业类型
     * publicParams	sign	String		加密字符串
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping("/fenxianghemacallback")
    public String callback(HttpServletRequest request, HttpServletResponse response){

        BufferedReader reader;
        StringBuilder stringBuilder;
        String inputStr = null;
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            //获取request中的json
            reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            stringBuilder = new StringBuilder();
            while ((inputStr = reader.readLine()) != null) {
                stringBuilder.append(inputStr);
            }
            JSONObject data = JSONObject.parseObject(stringBuilder.toString());
            if (data == null) {
                return "回调数据为空";
            }
            String orderState = data.get("orderState")+ "";
            String platformOrderCode = data.get("platformOrderCode")+ "";
            System.out.println(orderState);
            System.out.println("platformOrderCode = " + platformOrderCode);
        }catch (Exception e){

        }
        Map map = new HashMap();

        map.put("status","0");
        map.put("message","回调成功");
        String redultjson = JSONObject.toJSONString(map);
        return redultjson;
    }

    public static String doPost(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
            }

            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String post(String url, String data, String contentType)
    {
        CloseableHttpClient httpClient = null;
        try
        {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
           // httpPost.addHeader("sign", sign);
            // (name,
            // value);.addRequestHeader("Content-Type","text/html;charset=UTF-8");
            // httpPost.getParams().setParameter(HttpMethod.HTTP_CONTENT_CHARSET,
            // "UTF-8");
            if (null != data)
            {
                StringEntity stringEntity = new StringEntity(data, ToolString.encoding);
                stringEntity.setContentEncoding(ToolString.encoding);
                if (null != contentType)
                {
                    stringEntity.setContentType(contentType);
                }
                else
                {
                    stringEntity.setContentType("application/json");
                }
                httpPost.setEntity(stringEntity);
            }

            RequestConfig requestConfig =
                    RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    String out = EntityUtils.toString(entity, ToolString.encoding);
                    return out;
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {

            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {

            e.printStackTrace();

        }
        catch (IOException e)
        {

            e.printStackTrace();

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


    public static String postpost(){

        String rtn = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost("http://47.93.228.234:8081/pos/fee/querycompanyamount");


//	          String params = "123";
            for(int i =0;i<1;i++){

                StringBuffer params = new StringBuffer();
                JSONObject pjson = new JSONObject();
                pjson.put("companyCode", "900101");
                pjson.put("companyType", "1");

//	              JSONObject bjson = new JSONObject();
//	              bjson.put("phoneNum", "16710577077");


            //    String sign = ApiSignUtils.getSign(pjson,"fb1e3eba-5d99-4aec-99db-00f4609d7270");
                params.append("{");
                params.append("publicParams:{companyCode:900101,companyType:1,sign:\""+sign+"\"}");
                params.append("}");
                System.out.println(sign);

                StringEntity reqEntity = new StringEntity(params.toString(),"UTF-8");
                reqEntity.setContentType("text/plain; charset=utf-8;");
                reqEntity.setContentEncoding("utf-8");

                httpPost.setEntity(reqEntity);
                long start = System.currentTimeMillis();

                CloseableHttpResponse response = httpclient.execute(httpPost);

                HttpEntity entity = response.getEntity();
                rtn = EntityUtils.toString(entity);
                System.out.println("====="+rtn);

            }

        } catch (Exception ex) {
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
            }
        }
        return "";
    }






}
