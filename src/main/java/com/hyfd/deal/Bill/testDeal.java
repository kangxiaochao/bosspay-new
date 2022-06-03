package com.hyfd.deal.Bill;

import com.hyfd.common.utils.DateUtils;
import com.hyfd.deal.BaseDeal;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class testDeal implements BaseDeal {
    Logger log = Logger.getLogger(SuNingBillDeal.class);

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Map<String, Object> deal(Map<String, Object> order) {
        log.info("testDeal ===========================================================");
        Map<String, Object> map = new HashMap<String, Object>();
        // 生成自己的id，供回调时查询数据使用,上游要求格式：代理商编码+YYYYMMDD+8位流水号
        String curids = "123456" + DateUtils.getNowTimeTo()+ UUID.randomUUID().toString().substring(0,8);
        map.put("orderId", curids);
        map.put("resultCode", "测试提单成功");
        map.put("status", 3);
        return map;
    }
}
