package com.hyfd.deal.Bill;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.AESUtil;
import com.hyfd.common.utils.*;
import com.hyfd.deal.BaseDeal;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.operasale.AgentrechargeAddRequest;
import com.suning.api.entity.operasale.AgentrechargeAddResponse;
import com.suning.api.entity.operasale.AgentrechargedAddRequest;
import com.suning.api.entity.operasale.AgentrechargedAddResponse;
import com.suning.api.exception.SuningApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SuNingbujiaoBillDeal implements BaseDeal {

    Logger log = Logger.getLogger(SuNingBillDeal.class);

    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phoneNo = (String) order.get("phone");	// 手机号
            Double fee = (Double)order.get("fee");//金额，以分为单位
            String agentOrderId = (String)order.get("agentOrderId");
            Double ibillsize = Double.parseDouble(fee+"")*100;
            String spec = ibillsize.intValue() + "";

            Map<String, Object> channel = (Map<String, Object>) order.get("channel");	// 获取通道参数
            String linkUrl = channel.get("link_url").toString(); 						// 充值地址
            String defaultParameter = (String) channel.get("default_parameter");//默认参数
            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = paramMap.get("AppKey") + ""; 		// 苏宁提供 平台appKay
            String appSecret = paramMap.get("AppSecret") + ""; 	// 苏宁提供 平台appSecret
            String key = paramMap.get("Key") + ""; 				// 苏宁提供 接口签名使用的key
            String channelId = paramMap.get("ChannelId") + ""; 	// 苏宁提供 代理商充值渠道ID

            // 生成自己的id，供回调时查询数据使用,上游要求格式：代理商编码+YYYYMMDD+8位流水号（我感觉后续还要和提交过来的唯一标识做绑定，插入数据库）
            String curids = channelId + DateUtils.getNowTimeToMS().substring(0, 16);
            map.put("orderId", curids);

            String resultStr = rechargeAddRequest(linkUrl, appKey, appSecret, key, channelId, curids, phoneNo, spec,agentOrderId);
            log.info("苏宁补交接口提单响应结果："+resultStr);
            if (null == resultStr || resultStr.equals("")) {
                // 请求超时,未获取到返回数据
                flag = -1;
                String msg = "苏宁话费充值补交接口,号码[" + phoneNo + "],金额[" + spec + "(分)],请求超时,未接收到返回数据";
                map.put("resultCode", msg);
                log.error(msg);
            } else {

                JSONObject resultJson = valiResult(resultStr);
                boolean state = resultJson.getBoolean("state");
                if (state) {
                    String status = resultJson.getString("respCode");
                    String respMsg = resultJson.getString("respMsg");
                    if (status.equals("0000")) {
                        map.put("resultCode", respMsg);
                        flag = 1;	// 提交成功
                    } else {
                        map.put("resultCode", respMsg);
                        flag = -1;	// 提交异常
                    }
                } else {
                    String errorCode = resultJson.getString("error_code");
                    String errorMsg = resultJson.getString("error_msg");

                    map.put("resultCode", errorCode+":" + errorMsg);
                    flag = 0;	// 提交失败
                }

            }


        }catch (Exception e){
            log.error("苏宁视通补交话费充值出错" + e.getMessage() + MapUtils.toString(order));
        }

        map.put("status", flag);
        return map;

    }


    public String rechargeAddRequest(String serverUrl, String appKey, String appSecret, String key, String channelId, String reqSerial, String serialNumber, String feeAmount,String agentOrderId) {
        String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
        String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
        String resultStr = "";
        try {
            AESUtil util = new AESUtil(key); // 密钥
            String bujiaoNumber = util.encryptData(serialNumber);
            AgentrechargedAddRequest request = new AgentrechargedAddRequest();
            request.setChannelId(channelId);
            request.setFeeAmount(feeAmount);
            request.setPermission(agentOrderId);
            request.setReqSerial(reqSerial);
            request.setReqSign(sign);
            request.setReqTime(reqTime);
            request.setSerialNumber(bujiaoNumber);
            //api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
            request.setCheckParam(true);
            log.info("唯一标识："+agentOrderId+"加密手机号"+bujiaoNumber+"补交金额"+feeAmount+"业务签名"+sign);
            DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
            AgentrechargedAddResponse response = client.excute(request);
            resultStr = response.getBody();
            log.info("苏宁话费充值补交接口返回json/xml格式数据 :" + response.getBody());


        }catch (Exception e){
            e.printStackTrace();
        }


        return resultStr;
    }


    /**
     * <h5>功能:处理充值请求返回结果</h5>
     *
     * @author zhangpj	@date 2018年11月13日
     * @param
     * @return 成功state返回true,失败state返回false
     */
    public JSONObject valiResult(String resultStr){
        JSONObject jsonObj = JSONObject.parseObject(resultStr);
        JSONObject resultJson = null;
        try {
            JSONObject snBodyJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_body");
            if (null != snBodyJson) {
                resultJson = snBodyJson.getJSONObject("addAgentrecharge");
                resultJson.put("state", true);
            } else {
                resultJson = jsonObj.getJSONObject("sn_responseContent").getJSONObject("sn_error");
                resultJson.put("state", false);
            }
        } catch (Exception e) {
            log.error("苏宁充值返回信息解析发生异常,返回数据为[" + resultStr + "]");
        }
        return resultJson;
    }


    public static void main(String[] args) {
//        String serverUrl = "https://open.suning.com/api/http/sopRequest";
       String serverUrl = "https://openpre.cnsuning.com/api/http/sopRequest";
        String appKey = "9101c76f7736f9ce84686ab63b735585";
        String appSecret = "188a97a559c1ac72b5ac42d824349779";
        String key = "D30SDz0rxZ5P9tSu";
        String channelId = "10160383";
        String reqSerial = channelId + DateUtils.getNowTimeToMS().substring(0, 16);
        String serialNumber = "17091739064";
        String feeAmount = "1";
        String agentOrderId = "d2ac487986dee2cee5c9e5f50b4ac640";



        String reqTime = DateUtils.getNowTimeToSec();								// 请求时间，格式：YYYYMMDDHH24MISS
        String sign = MD5.MD5(channelId + reqSerial + reqTime + key).toLowerCase();	// 业务签名
        String resultStr = "";
        try {

            AESUtil util = new AESUtil(key); // 密钥
            String bujiaoNumber = util.encryptData(serialNumber);
            AgentrechargedAddRequest request = new AgentrechargedAddRequest();
            request.setChannelId(channelId);
            request.setFeeAmount(feeAmount);
            request.setPermission(agentOrderId);
            request.setReqSerial(reqSerial);
            request.setReqSign(sign);
            request.setReqTime(reqTime);
            request.setSerialNumber(bujiaoNumber);
            //api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
            request.setCheckParam(true);
            System.out.println(request);
            System.out.println("唯一标识："+agentOrderId+"加密手机号"+bujiaoNumber+"补交金额"+feeAmount+"业务签名"+sign);
            DefaultSuningClient client = new DefaultSuningClient(serverUrl, appKey,appSecret, "json");
            AgentrechargedAddResponse response = client.excute(request);
            resultStr = response.getBody();
            System.out.println("苏宁话费充值补交接口返回json/xml格式数据 :" + response.getBody());

        }catch (Exception e){
            e.printStackTrace();
        }




    }
}
