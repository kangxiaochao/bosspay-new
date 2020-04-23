package com.hyfd.test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hyfd.common.utils.GenerateData;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.qianmi.open.api.DefaultOpenClient;
import com.qianmi.open.api.OpenClient;
import com.qianmi.open.api.request.BmOrderCustomGetRequest;
import com.qianmi.open.api.request.BmRechargeMobileGetItemInfoRequest;
import com.qianmi.open.api.request.BmRechargeMobilePayBillRequest;
import com.qianmi.open.api.response.BmOrderCustomGetResponse;
import com.qianmi.open.api.response.BmRechargeMobileGetItemInfoResponse;
import com.qianmi.open.api.response.BmRechargeMobilePayBillResponse;

/**
 * 千米话费充值 
 * 
 * @author
 *
 */
public class QianMiTwoTest{
	
	public static void main(String[] args) {
//		System.out.println(deal());
		//202004211505306611775317660780
		queryQianMiOrder();
	}

	public static Map<String, Object> deal() {
		Map<String,Object> map = new HashMap<String, Object>(); 
		int flag = -1;
		try {
			String phone = "17753176607";											//手机号	
			String fee = "10";						//充值金额  单位元  正整数，例如20、50、100等面值
			String linkUrl = "http://api.bm001.com/api";										//充值地址
			String appKey = "10002613";
			String appSecret = "BwGpSoc6ODNS7ugEzPrYTEmkOJyVXxCj";									//秘钥
			String accessToken = "d7e68627f94644e9865bedb949ea2ad5";								//接入码
			String callback = "";										//回调地址
			String curids = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 2);
			map.put("orderId",curids);
			//查询单个话费直充商品
			OpenClient client = new DefaultOpenClient(linkUrl,appKey,appSecret);
			BmRechargeMobileGetItemInfoRequest inforeq = new BmRechargeMobileGetItemInfoRequest();
			inforeq.setMobileNo(phone);
			inforeq.setRechargeAmount(fee);
			BmRechargeMobileGetItemInfoResponse inforesponse = client.execute(inforeq, accessToken);
			if(inforesponse.isSuccess()) {
				String itemId = inforesponse.getMobileItemInfo().getItemId();				// 商品编号
				// 话费订单充值
				BmRechargeMobilePayBillRequest createreq = new BmRechargeMobilePayBillRequest();
				createreq.setMobileNo(phone);
				createreq.setRechargeAmount(String.valueOf(fee));
				createreq.setItemId(itemId);	
				createreq.setOuterTid(curids);
				createreq.setCallback(callback);
				BmRechargeMobilePayBillResponse createresponse = client.execute(createreq, accessToken);
				System.out.println(createresponse);
				if (createresponse.isSuccess()){
					System.out.println(createresponse.getOrderDetailInfo().getRechargeState());
					flag = 1;		//提交成功
					map.put("resultCode","提交成功");
				}else {
					flag = 0;
					map.put("resultCode",createresponse.getErrorCode()+" : "+createresponse.getSubMsg());
				}
			}else{
            	flag = 0;
            	map.put("resultCode",inforesponse.getErrorCode()+" : "+inforesponse.getSubMsg());
            }
			
		} catch (Exception e) {

		}
		map.put("status", flag);
		return map;
	}
	
	public static void queryQianMiOrder() {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String url = "http://api.bm001.com/api";												//查询地址
			String appKey = "10002613";
			String appSecret = "BwGpSoc6ODNS7ugEzPrYTEmkOJyVXxCj";									//秘钥
			String accessToken = "d7e68627f94644e9865bedb949ea2ad5";								//接入码					//外部订单号
				
				OpenClient client = new DefaultOpenClient(url, appKey, appSecret);
				BmOrderCustomGetRequest req = new BmOrderCustomGetRequest();
				req.setOuterTid("202004211505306611775317660780");
				int flag = 2;
				BmOrderCustomGetResponse response = client.execute(req, accessToken);
				System.out.println(response.getOrderDetailInfo().getRechargeAccount());
				if(response.isSuccess()){
					// 返回的充值状态
					String status = response.getOrderDetailInfo().getRechargeState();
					if("1".equals(status)){
						/**
						 * 需要修改  判断输入状态
						 */
						// 充值成功										
						flag = 1;
						System.out.println(flag);
					}else if("0".equals(status)){
						// 
						System.out.println("充值中");
					}else if ("9".equals(status)) {
						// 
						flag = 0;
						System.out.println("充值失败");
					}else {
						System.out.println(flag);
					}
				}else {
				}
				map.put("status", flag);
		} catch (Exception e) {
		}
	}
}
