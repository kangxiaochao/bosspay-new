package com.hyfd.service.mp;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.rabbitMq.MqProducer;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ExceptionOrderSer extends BaseService
{
    
    @Autowired
    MqProducer mqProducer;
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    ExceptionOrderDao exceptionOrderDao;
    
    @Autowired
    chargeOrderSer chargeOrderSer;
    
    @Autowired
    AgentDao agentDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderDao providerDao;
    
    @Autowired
    ProviderAccountSer providerAccountSer;
    
    @Autowired
    PhoneSectionDao phoneSectionDao;
    
    @Autowired
    BillPkgDao billPkgDao;
    
    @Autowired
    ProviderBillDispatcherDao providerBillDispatcherDao;
    
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    
	@Autowired
	AgentBillDiscountSer agentBillDiscountSer; // 代理商话费折扣Service
    
    Logger log = Logger.getLogger(getClass());
    
    /**
     * 批量复充
     * */
    public String batchReCharge(HttpServletRequest request)
    {
        String str = "";
        int succ = 0;
        int error = 0;
        try
        {
            String[] ids = request.getParameterValues("ids");
            for (String id : ids)
            {
                boolean flag = reCharge(id);
                if (flag)
                {
                    succ += 1;
                }
                else
                {
                    error += 1;
                }
            }
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("succ", succ);
            m.put("error", error);
            str = BaseJson.mapToJson(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * 批量复充
     * */
    public String batchReChargeOld(HttpServletRequest request)
    {
        String str = "";
        int succ = 0;
        int error = 0;
        try
        {
            String[] ids = request.getParameterValues("ids");
            for (String id : ids)
            {
                boolean flag = reChargeOld(id);
                if (flag)
                {
                    succ += 1;
                }
                else
                {
                    error += 1;
                }
            }
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("succ", succ);
            m.put("error", error);
            str = BaseJson.mapToJson(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * 批量退款
     * */
    public String batchReFund(HttpServletRequest request)
    {
        String str = "";
        int succ = 0;
        int error = 0;
        try
        {
            String[] ids = request.getParameterValues("ids");
            for (String id : ids)
            {
                boolean flag = refund(id);
                if (flag)
                {
                    succ += 1;
                }
                else
                {
                    error += 1;
                }
            }
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("succ", succ);
            m.put("error", error);
            str = BaseJson.mapToJson(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return str;
    }
    
    /**
     * 复充方法
     * 
     * @author lks 2017年3月8日下午3:18:35
     * @param request
     * @return
     */
    public boolean reCharge(String id)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);// 获取异常订单
            if (exceptionOrder != null)
            {
                if (moveOrder(exceptionOrder))
                {
                    chargeOrderSer.ReCharge(exceptionOrder);
                    flag = true;
                }
                else
                {
                    return flag;
                }
            }
        }
        catch (Exception e)
        {
            log.error("复充方法出错" + e);
        }
        return flag;
    }
    
    /**
     * 复充方法
     * 
     * @author lks 2017年3月8日下午3:18:35
     * @param request
     * @return
     */
    public boolean reChargeOld(String id)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);// 获取异常订单
            exceptionOrder.put("dealPath", "");
            if (exceptionOrder != null)
            {
                if (moveOrder(exceptionOrder))
                {
                    chargeOrderSer.ReCharge(exceptionOrder);
                    flag = true;
                }
                else
                {
                    return flag;
                }
            }
        }
        catch (Exception e)
        {
            log.error("复充方法出错" + e);
        }
        return flag;
    }
    
    /**
     * 退款方法
     * 
     * @author lks 2017年3月8日下午3:18:51
     * @param request
     * @return
     */
    public boolean refund(String id)
    {
        boolean flag = false;
        String message = "退款失败";
        Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);
        if (exceptionOrder != null)
        {
            if (moveOrder(exceptionOrder))
            {
                flag = chargeOrderSer.dealOrderFail(exceptionOrder, "4", "异常订单，人工退款处理");
                if (flag)
                {
                    message = "退款成功";
                    
                    // 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(exceptionOrder);
                    
                    Map<String, Object> callbackMap = new HashMap<String, Object>();
                    callbackMap.put("status", AgentCallbackSer.CallbackStatus_Fail);
                    callbackMap.put("order", exceptionOrder);
                    mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
                        SerializeUtil.getStrFromObj(callbackMap));
                }
            }
        }
        return flag;
    }
    
    /**
     * 异常订单改为成功
     * @author lks 2017年9月8日下午2:53:41
     * @param request
     * @param response
     * @return
     */
    public String changeSucc(HttpServletRequest request,HttpServletResponse response){
    	int succNum = 0;
    	int failNum = 0;
    	String[] ids = request.getParameterValues("ids");
    	for(String id : ids){
    		Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);
    		Map<String, Object> order = new HashMap<String, Object>();
    		order.putAll(exceptionOrder);
    		order.put("status", "3");
    		Map<String, Object> orderPathRecord = new HashMap<String, Object>();
            orderPathRecord.putAll(order);
            orderPathRecord.put("status", "3");
            // 充值流水
            chargeOrderSer.saveOrderPathRecord(orderPathRecord);
            boolean flag = providerAccountSer.Charge(order);// 扣除上家余额
            if (!flag)
            {
                log.error("扣除上家余额出错" + MapUtils.toString(order));
                exceptionOrder.put("resultCode", order.get("resultCode") + "|该笔订单扣除上家余额出现异常");
                exceptionOrderDao.updateByPrimaryKeySelective(exceptionOrder);// 保存异常订单
                failNum++;
            }
            else
            {
            	order.put("resultCode", "异常订单，人工置为成功");
                order.put("endDate", new Timestamp(System.currentTimeMillis()));
                int n = orderDao.insertSelective(order);
                if (n < 1)
                {
                    log.error("更新数据库出错" + MapUtils.toString(order));
                    exceptionOrder.put("resultCode", order.get("resultCode") + "|该笔订单更新订单状态出现异常");
                    exceptionOrderDao.updateByPrimaryKeySelective(exceptionOrder);// 保存异常订单
                    failNum++;
                }
                else
                {
                	// 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
                	
                	exceptionOrderDao.deleteByPrimaryKey((String)exceptionOrder.get("id"));
                    Map<String, Object> callbackMap = new HashMap<String, Object>();
                    callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
                    callbackMap.put("order", order);
                    mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
                        SerializeUtil.getStrFromObj(callbackMap));
                    succNum++;
                }
            }
    	}
    	JSONObject json = new JSONObject();
    	json.put("succNum", succNum);
    	json.put("failNum", failNum);
    	return json.toJSONString();
    }
    
    /**
     * 将异常订单移动到正式订单表
     * 
     * @author lks 2017年3月10日下午2:56:37
     * @param order
     * @return
     */
    @Transactional
    public boolean moveOrder(Map<String, Object> order)
    {
        boolean flag = false;
        String id = order.get("id") + "";// 获取订单id
        int a = exceptionOrderDao.deleteByPrimaryKey(id);// 将订单从异常订单删除
        if (a > 0)
        {
            int b = orderDao.insertSelective(order);
            if (b > 0)
            {
                flag = true;
            }
        }
        return flag;
    }
    
    public String exceptionOrderList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            String agentName = (String)m.get("agentName");
            String dispatcherProviderName = (String)m.get("dispatcherProviderName");
            String providerName = (String)m.get("providerName");
            if (agentName != null && agentName != "")
            {
                Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
                if (agent != null)
                {
                    String agentId = (String)agent.get("id");
                    m.put("agentId", agentId);
                }
                else
                {
                    m.put("agentId", "no");
                }
            }
            if (dispatcherProviderName != null && dispatcherProviderName != "")
            {
                String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
                if (dispatcherProviderId != null && dispatcherProviderId != "")
                {
                    m.put("dispatcherProviderId", dispatcherProviderId);
                }
                else
                {
                    m.put("dispatcherProviderId", "no");
                }
            }
            if (providerName != null && providerName != "")
            {
                String providerId = providerDao.getIdByName(providerName);
                if (providerId != null && providerId != "")
                {
                    m.put("providerId", providerId);
                }
                else
                {
                    m.put("providerId", "no");
                }
            }
            
            Page p = getPage(m);// 提取分页参数
            int total = getOrderCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> billList = exceptionOrderDao.selectAll(m);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
        
    }
    
    public int getOrderCount(Map<String, Object> m)
    {
        int orderCount = 0;
        try
        {
            orderCount = exceptionOrderDao.getOrderCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return orderCount;
    }
    
    /**
     * @功能描述： 跳转到异常订单详细参数页面
     *
     * @作者：zhangpj @创建时间：2017年5月2日
     * @param req
     */
    public void orderParamPage(HttpServletRequest req)
    {
        Map<String, Object> orderParam = selectOrderParam(req);
        req.setAttribute("orderParam", orderParam);
        req.setAttribute("orderType", "2");
    }
    
    /**
     * @功能描述： 获取异常订单详细参数信息
     *
     * @作者：zhangpj @创建时间：2017年5月2日
     * @param req
     * @return
     */
    public String getOrderParam(HttpServletRequest req)
    {
        Map<String, Object> orderParam = selectOrderParam(req);
        return JSONObject.toJSONString(orderParam);
    }
    
    /**
     * @功能描述： 查询订单详细参数信息
     *
     * @作者：zhangpj @创建时间：2017年4月26日
     * @param req
     * @return
     */
    public Map<String, Object> selectOrderParam(HttpServletRequest req)
    {
        Map<String, Object> m = getMaps(req); // 封装前台参数为map
        String phone = String.valueOf(m.get("phone"));
        String agentOrderId = String.valueOf(m.get("agentOrderId"));
        
        Map<String, Object> orderParam = null;
        if ((null != phone && !phone.equals("")) || (null != agentOrderId && !agentOrderId.equals("")))
        {
            // 根据订单编号或者电话号码获取最新一条的订单信息
            Map<String, String> orderMap = exceptionOrderDao.selectFastNewOrderByPhone(m);
            if (null != orderMap)
            {
                agentOrderId = orderMap.get("agent_order_id");
                m.put("orderId", agentOrderId);
                // 1.获取订单参数
                orderParam = exceptionOrderDao.selectOrderParam(m);
                
                // 2.获取可用流量包
                Map<String, String> param = new HashMap<String, String>();
                param.put("agentId", orderParam.get("agentId").toString());
                param.put("providerId", orderParam.get("providerId").toString());
                param.put("billType", orderParam.get("billType").toString());
                param.put("provinceCode", orderParam.get("provinceCode").toString());
                String billType = orderParam.get("billType").toString();
                if ("2".equals(billType))
                {
                    // 流量
                    param.put("value", orderParam.get("value").toString());
                }
                
                Map<String, Object> pkg = billPkgDao.selectBillPkgForAgent(param);
                if (pkg == null)
                {
                    param.put("provinceCode", "全国");
                    pkg = billPkgDao.selectBillPkgForAgent(param);
                }
                
                // 3.获取可用通道列表
                Map<String, Object> dispatcherParam = new HashMap<String, Object>();
                dispatcherParam.put("pkgId", orderParam.get("pkgId"));
                dispatcherParam.put("providerId", orderParam.get("providerId"));
                dispatcherParam.put("provinceCode", orderParam.get("provinceCode"));
                List<Map<String, Object>> dpList =
                    providerBillDispatcherDao.selectProviderPhysicalChannel(dispatcherParam);
                
                orderParam.put("result", "success");
                orderParam.put("pkg", pkg);
                orderParam.put("dispatcherParam", dpList);
            }
        }
        
        return orderParam;
    }
    
    /**
     * judgeTianmaoRoleException
     * 
     * @Description: 判断是否是天猫订单(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月7日 下午5:12:10
     * @param @param suId
     * @param @param ids
     * @param @return
     * @return int 返回类型
     * @throws
     */
    public Map<String, Object> judgeTianmaoRoleException(String suId, String[] ids)
    {
        int tianmaoSum = 0;
        List<Map<String, Object>> billList = sysUserRoleDao.getHasSysRoleList(suId);
        boolean roleFlag = false;
        for (Map<String, Object> map : billList)
        {
            String srName = map.get("srName") + "";
            if ("天猫".equals(srName) || "技术运维".equals(srName))
            {
                roleFlag = true;
            }
        }
        if (roleFlag)
        {
            
            for (int i = 0; i < ids.length; i++)
            {
                String orderId = ids[i];
                Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(orderId);// 获取异常订单
                if (exceptionOrder != null)
                {
                    String agentId = exceptionOrder.get("agentId") + "";
                    if (!"6fc1e7e29f4142f48110bc6c5286d723".equals(agentId)
                        && !"81c2390df095475f86f0e9c6dff1cfd2".equals(agentId))
                    {
                        tianmaoSum += 1;
                    }
                }
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("roleFlag", roleFlag);
        map.put("tianmaoSum", tianmaoSum);
        return map;
    }
    
    /**
     * 导出异常订单信息
     * @param req
     * @param res
     */
    public void exportException(HttpServletRequest req,HttpServletResponse res) {
    	Map<String, Object> m = getMaps(req);
    	List<Map<String, Object>> exce = exceptionOrderDao.selectAll(m);
    	
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("异常订单信息");
		HSSFRow row = sheet.createRow((int) 0);  
        HSSFCell cell = row.createCell((short) 0);  
        cell.setCellValue("平台订单号");  
        cell = row.createCell((short) 1);  
        cell.setCellValue("代理商订单号");  
        cell = row.createCell((short) 2);  
        cell.setCellValue("代理商");  
        cell = row.createCell((short) 3);  
        cell.setCellValue("物理通道");
        cell = row.createCell((short) 4);  
        cell.setCellValue("运营商"); 
        cell = row.createCell((short) 5);  
        cell.setCellValue("状态"); 
        cell = row.createCell((short) 6);  
        cell.setCellValue("手机号"); 
        cell = row.createCell((short) 7);  
        cell.setCellValue("类型"); 
        cell = row.createCell((short) 8); 
        cell.setCellValue("面值"); 
        
        for(int i=0;i<exce.size();i++) {
        	row = sheet.createRow((int) i + 1);
        	Map<String, Object> map = exce.get(i);
        	row.createCell(0).setCellValue(map.get("orderId").toString()!=null ? map.get("orderId").toString() : "平台订单号不存在");
        	row.createCell(1).setCellValue(map.get("agentOrderId").toString()!=null ? map.get("agentOrderId").toString() : "代理商订单号不存在");
        	row.createCell(2).setCellValue(map.get("agent").toString() !=null ? map.get("agent").toString() : "代理商不存在");
        	row.createCell(3).setCellValue(map.get("dispatcherProvider").toString() !=null ? map.get("dispatcherProvider").toString() : "物理通道不存在");
        	row.createCell(4).setCellValue(map.get("provider").toString() !=null ? map.get("provider").toString() : "运营商不存在");
        	row.createCell(5).setCellValue(map.get("status").toString() !=null ? map.get("status").toString() : "状态不存在");
        	row.createCell(6).setCellValue(map.get("phone").toString() !=null ? map.get("phone").toString() : "手机号该不存在");
        	row.createCell(7).setCellValue(map.get("bizType").toString() !=null ? map.get("bizType").toString() : "类型不存在");
        	row.createCell(8).setCellValue(map.get("fee").toString() !=null ? map.get("fee").toString() : "面值不存在");
        } 
        try {
        	res.setContentType("application/x-excel;charset=utf-8");
        	res.setHeader("Content-Disposition",
                "attachment;filename=exception.xlsx");
        	res.setCharacterEncoding("utf-8");
			OutputStream os = res.getOutputStream();
			wb.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
