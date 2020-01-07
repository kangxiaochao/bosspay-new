package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.RechargeBasePayBillRequest;
import com.qianmi.open.api.request.RechargeMobileCreateBillRequest;
import com.qianmi.open.api.request.RechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.response.RechargeBasePayBillResponse;
import com.qianmi.open.api.response.RechargeMobileCreateBillResponse;
import com.qianmi.open.api.response.RechargeMobileGetItemInfoResponse;

/**
 * 千米话费充值
 * 
 * @author zhangjun
 *
 */
public class QianMiBillDeal implements BaseDeal
{
    private static Logger log = Logger.getLogger(QianMiBillDeal.class);
    
    public static Map<String, String> rltMap = new HashMap<String, String>();
    
    static
    {
        rltMap.put("国美-500", "1808302");
        rltMap.put("国美-300", "1808303");
        rltMap.put("国美-100", "1808305");
        rltMap.put("国美-200", "1808304");
        rltMap.put("国美-20", "1808308");
        rltMap.put("国美-10", "1808309");
        rltMap.put("国美-30", "1808307");
        rltMap.put("国美-50", "1808306");
        rltMap.put("分享-20", "1807008");
        rltMap.put("分享-10", "1807009");
        rltMap.put("分享-100", "1807005");
        rltMap.put("分享-50", "1807006");
        rltMap.put("分享-30", "1807007");
        rltMap.put("分享-500", "1807002");
        rltMap.put("分享-300", "1807003");
        rltMap.put("分享-200", "1807004");
        rltMap.put("普泰-10", "1811705");
        rltMap.put("普泰-20", "1811704");
        rltMap.put("普泰-500", "1811706");
        rltMap.put("普泰-100", "1811701");
        rltMap.put("普泰-50", "1811702");
        rltMap.put("普泰-200", "1811708");
        rltMap.put("普泰-300", "1811707");
        rltMap.put("普泰-30", "1811703");
        rltMap.put("苏宁-30", "1808506");
        rltMap.put("苏宁-20", "1808507");
        rltMap.put("苏宁-10", "1808508");
        rltMap.put("苏宁-300", "1808502");
        rltMap.put("苏宁-100", "1808504");
        rltMap.put("苏宁-50", "1808505");
        rltMap.put("苏宁-200", "1808503");
        rltMap.put("苏宁-500", "1808501");
        rltMap.put("中邮-10", "1810409");
        rltMap.put("中邮-30", "1810407");
        rltMap.put("中邮-20", "1810408");
        rltMap.put("中邮-300", "1810403");
        rltMap.put("中邮-200", "1810404");
        rltMap.put("中邮-100", "1810405");
        rltMap.put("中邮-50", "1810406");
        rltMap.put("中邮-500", "1810402");
        rltMap.put("华翔-20", "1810508");
        rltMap.put("华翔-10", "1810509");
        rltMap.put("华翔-100", "1810505");
        rltMap.put("华翔-30", "1810507");
        rltMap.put("华翔-50", "1810506");
        rltMap.put("华翔-500", "1810502");
        rltMap.put("华翔-200", "1810504");
        rltMap.put("华翔-300", "1810503");
        rltMap.put("凤凰-500", "1811301");
        rltMap.put("凤凰-300", "1811302");
        rltMap.put("凤凰-50", "1811305");
        rltMap.put("凤凰-30", "1811306");
        rltMap.put("凤凰-10", "1811308");
        rltMap.put("凤凰-20", "1811307");
        rltMap.put("凤凰-200", "1811303");
        rltMap.put("凤凰-100", "1811304");
        rltMap.put("星美-20", "1810608");
        rltMap.put("星美-10", "1810609");
        rltMap.put("星美-200", "1810604");
        rltMap.put("星美-100", "1810605");
        rltMap.put("星美-50", "1810606");
        rltMap.put("星美-30", "1810607");
        rltMap.put("星美-500", "1810602");
        rltMap.put("星美-300", "1810603");
        rltMap.put("红豆-30", "1810207");
        rltMap.put("红豆-20", "1810208");
        rltMap.put("红豆-200", "1810204");
        rltMap.put("红豆-100", "1810205");
        rltMap.put("红豆-50", "1810206");
        rltMap.put("红豆-500", "1810202");
        rltMap.put("红豆-10", "1810209");
        rltMap.put("红豆-300", "1810203");
        rltMap.put("中麦-500", "1806308");
        rltMap.put("中麦-300", "1806309");
        rltMap.put("中麦-30", "1806304");
        rltMap.put("中麦-20", "1806305");
        rltMap.put("中麦-10", "1806306");
        rltMap.put("中麦-100", "1806302");
        rltMap.put("中麦-200", "1806307");
        rltMap.put("中麦-50", "1806303");
        rltMap.put("乐语-20", "1807108");
        rltMap.put("乐语-10", "1807109");
        rltMap.put("乐语-200", "1807104");
        rltMap.put("乐语-50", "1807106");
        rltMap.put("乐语-30", "1807107");
        rltMap.put("乐语-100", "1807105");
        rltMap.put("乐语-300", "1807103");
        rltMap.put("乐语-500", "1807102");
        rltMap.put("朗玛-50", "1808005");
        rltMap.put("朗玛-20", "1808007");
        rltMap.put("朗玛-10", "1808008");
        rltMap.put("朗玛-30", "1808006");
        rltMap.put("朗玛-300", "1808002");
        rltMap.put("朗玛-100", "1808004");
        rltMap.put("朗玛-200", "1808003");
        rltMap.put("朗玛-500", "1808001");
        rltMap.put("青牛-50", "1814905");
        rltMap.put("青牛-20", "1814907");
        rltMap.put("青牛-30", "1814906");
        rltMap.put("青牛-10", "1814908");
        rltMap.put("青牛-300", "1814902");
        rltMap.put("青牛-200", "1814903");
        rltMap.put("青牛-100", "1814904");
        rltMap.put("青牛-500", "1814901");
        rltMap.put("海航-20", "1809308");
        rltMap.put("海航-10", "1809309");
        rltMap.put("海航-200", "1809304");
        rltMap.put("海航-100", "1809305");
        rltMap.put("海航-30", "1809307");
        rltMap.put("海航-50", "1809306");
        rltMap.put("海航-500", "1809302");
        rltMap.put("海航-300", "1809303");
        rltMap.put("天音-300", "1805909");
        rltMap.put("天音-500", "1805908");
        rltMap.put("天音-10", "1805902");
        rltMap.put("天音-100", "1805906");
        rltMap.put("天音-20", "1805903");
        rltMap.put("天音-50", "1805905");
        rltMap.put("天音-30", "1805904");
        rltMap.put("天音-200", "1805907");
        rltMap.put("爱施德-10", "1800307");
        rltMap.put("爱施德-100", "1800303");
        rltMap.put("爱施德-50", "1800304");
        rltMap.put("爱施德-30", "1800305");
        rltMap.put("爱施德-20", "1800306");
        rltMap.put("爱施德-200", "1800302");
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> deal(Map<String, Object> order)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        int flag = -1;
        
        String itemId = "";
        String billId = "";
        try
        {
        	String phone = (String)order.get("phone");// 手机号
        	int fee = new Double(order.get("fee")+"").intValue();
            Map<String, Object> channel = (Map<String, Object>)order.get("channel");// 获取通道参数
            String linkUrl = (String)channel.get("link_url");// 充值地址
            
            String defaultParameter = (String)channel.get("default_parameter");// 默认参数
            Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
            
            String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
            map.put("orderId", curids);
            String appKey = paramMap.get("appKey");
            String appSecret = paramMap.get("appSecret");
            // String url = paramMap.get("url");
            String accessToken = paramMap.get("AccessToken");
            
            // 查询单个话费直充商品
            OpenClient client = new DefaultOpenClient(linkUrl, appKey, appSecret);
            RechargeMobileGetItemInfoRequest inforeq = new RechargeMobileGetItemInfoRequest();
            inforeq.setMobileNo(phone);// 手机号
            inforeq.setRechargeAmount(String.valueOf(fee));// 充值金额
            // 获取商品信息
            RechargeMobileGetItemInfoResponse inforesponse = client.execute(inforeq, accessToken);
            if (inforesponse.isSuccess())
            {
                itemId = inforesponse.getMobileItemInfo().getItemId();// 商品编号
                
                RechargeMobileCreateBillRequest creatreq = new RechargeMobileCreateBillRequest();
                creatreq.setMobileNo(phone);
                creatreq.setRechargeAmount(String.valueOf(fee));
                creatreq.setItemId(itemId);
                creatreq.setOuterTid(curids);
                RechargeMobileCreateBillResponse creatresponse = client.execute(creatreq, accessToken);
                if (creatresponse.isSuccess())
                {
                    billId = creatresponse.getOrderDetailInfo().getBillId();// 订单号
                    map.put("providerOrderId", billId);
                    RechargeBasePayBillRequest payreq = new RechargeBasePayBillRequest();
                    payreq.setBillId(billId);
                    RechargeBasePayBillResponse payresponse = client.execute(payreq, accessToken);
                    
                    if (payresponse.isSuccess())
                    {// 充值请求成功
                        flag = 1;
                    }
                    else
                    {
                        flag = 0;
                    }
                }
            }else{
            	flag = 0;
            }
        }
        catch (Exception e)
        {
            log.error("千米话费充值逻辑出错" + e + MapUtils.toString(order));
        }
        
        map.put("status", flag);
        return map;
    }
}
