package com.hyfd.deal.Bill;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.deal.BaseDeal;

public class SucBillDeal implements BaseDeal{

	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		String curids = "119" + DateTimeUtils.formatDate(new Date(), "yyyyMMddhhmmss") + GenerateData.getIntData(9,8);//订单号
		map.put("orderId", curids);
		map.put("resultCode", "0:接收成功|0:");
		map.put("status", 3);
		map.put("providerOrderId", GenerateData.getIntData(9,8)+"");
		return map;
	}

}
