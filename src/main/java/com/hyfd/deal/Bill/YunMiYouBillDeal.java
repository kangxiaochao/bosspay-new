package com.hyfd.deal.Bill;

import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.BmRechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.request.BmRechargeMobilePayBillRequest;
import com.qianmi.open.api.response.BmRechargeMobileGetItemInfoResponse;
import com.qianmi.open.api.response.BmRechargeMobilePayBillResponse;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YunMiYouBillDeal implements BaseDeal {
    private static Logger log = Logger.getLogger(YunMiYouBillDeal.class);
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order) {
        Map<String,Object> map = new HashMap<String, Object>();
        int flag = -1;
        try {
            String phone = order.get("phone")+"";											//手机号
            String fee = new Double(order.get("fee")+"").intValue()+"";						//充值金额  单位元  正整数，例如20、50、100等面值
            Map<String,Object> channel = (Map<String, Object>) order.get("channel");		//获取通道参数
            String linkUrl =channel.get("link_url")+"";										//充值地址
            String defaultParameter = channel.get("default_parameter")+"";					//默认参数
            Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            String appKey = paramMap.get("appKey");
            String appSecret = paramMap.get("appSecret");									//秘钥
            String accessToken = paramMap.get("accessToken");								//接入码
            String callback = paramMap.get("callback");										//回调地址
            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
            map.put("orderId",curids);
            //查询单个话费直充商品
            OpenClient client = new DefaultOpenClient(linkUrl, appSecret);
            BmRechargeMobileGetItemInfoRequest inforeq = new BmRechargeMobileGetItemInfoRequest();
            inforeq.setMobileNo(phone);
            inforeq.setRechargeAmount(fee);
            BmRechargeMobileGetItemInfoResponse inforesponse = client.execute(inforeq, accessToken);
            if (inforesponse.isSuccess()){
                String itemId = inforesponse.getMobileItemInfo().getItemId();				// 商品编号
                // 话费订单充值
                BmRechargeMobilePayBillRequest createreq = new BmRechargeMobilePayBillRequest();
                createreq.setMobileNo(phone);
                createreq.setRechargeAmount(String.valueOf(fee));
                createreq.setItemId(itemId);
                createreq.setOuterTid(curids);
                createreq.setCallback(callback);
                BmRechargeMobilePayBillResponse createresponse = client.execute(createreq, accessToken);
                if (createresponse.isSuccess()){
                    flag = 1;		//提交成功
                    map.put("resultCode","提交成功");
                }else {
                    flag = 0;
                    map.put("resultCode",createresponse.getErrorCode()+" : "+createresponse.getSubMsg());
                }
            }else {
                flag = 0;
                map.put("resultCode",inforesponse.getErrorCode()+" : "+inforesponse.getSubMsg());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("云米优话费充值逻辑出错" + e + MapUtils.toString(order));
        }
        map.put("status", flag);
        return map;
    }
}
