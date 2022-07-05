package com.hyfd.service.mp;

import com.hyfd.common.BaseJson;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;
import com.hyfd.exceptionTask.BaseTask;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryExceptionOrderStatusSer extends BaseService {
    public Logger log = Logger.getLogger(this.getClass());

    @Autowired
    OrderDao orderDao;

    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 通道的物理参数表

    public String qeuryOrderStatus(HttpServletRequest req) {
        String result = "";
        Map<String, Object> m = getMaps(req); //获取接口传参
        try {
            Session session = getSession();
            //获取异常订单信息
            Map<String, Object> order = null;
            Object orderId = m.get("orderId");
            Object agentOrderId =m.get("agentOrderId");
            Object providerOrderId =m.get("providerOrderId");
            Object dispatcherProviderId =m.get("dispatcherProviderId");
            Object startDate =m.get("startDate");
            Object endDate =m.get("endDate");
            if((orderId != null && orderId.toString().length() > 0)
                    || (agentOrderId != null && agentOrderId.toString().length() > 0)
                    || (providerOrderId != null && providerOrderId.toString().length() > 0) ){
                Map<String, Object> map = new HashMap<>();
                map.put("orderId",orderId);
                map.put("agentOrderId",agentOrderId);
                map.put("providerOrderId",providerOrderId);
                map.put("applyDate",startDate);
                map.put("endDate",endDate);
                List<Map<String,Object>> mapList = orderDao.selectAll(map);
                if(mapList != null && mapList.size() > 0){
                    order = mapList.get(0);
                }
            }
            if(order == null){
                return result;
            }
            //获取查询接口标识
            Map<String,Object> channelMap = new HashMap<>();
            if(dispatcherProviderId != null && dispatcherProviderId.toString().length() > 0){
                channelMap = providerPhysicalChannelDao.getProviderPhysicalChannelById(dispatcherProviderId+"");
            }else{
                return result;
            }
            String providerTaskMark = channelMap.get("provider_task_mark")+"";
            //调用查询接口
            BaseTask task = (BaseTask) Class.forName("com.hyfd.exceptionTask."+providerTaskMark).newInstance();
            Map<String,Object> taskMap = task.task(order,channelMap);
            StringBuilder sb = new StringBuilder();
            List<Map<String, Object>> billList = new ArrayList<>();
            billList.add(taskMap);
            sb.append("{");
            sb.append("" + getKey("rows") + ":" + "");
            String bill = BaseJson.listToJson(billList);
            sb.append(bill);
            sb.append("}");
            result = sb.toString();
        }catch (Exception e){
            log.error("通道选择出错"+ ExceptionUtils.getExceptionMessage(e)+"||"+ MapUtils.toString(m));
            e.printStackTrace();
        }
        return result;
    }
}



