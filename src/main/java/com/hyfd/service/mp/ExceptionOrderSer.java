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
    AgentAccountSer agentAccountService;

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
	AgentBillDiscountSer agentBillDiscountSer; // ?????????????????????Service
    
    Logger log = Logger.getLogger(getClass());
    
    /**
     * ????????????
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
     * ????????????
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
     * ????????????
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
     * ????????????
     * 
     * @author lks 2017???3???8?????????3:18:35
     * @param id
     * @return
     */
    public boolean reCharge(String id)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);// ??????????????????
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
            log.error("??????????????????" + e);
        }
        return flag;
    }
    
    /**
     * ????????????
     * 
     * @author lks 2017???3???8?????????3:18:35
     * @param id
     * @return
     */
    public boolean reChargeOld(String id)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);// ??????????????????
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
            log.error("??????????????????" + e);
        }
        return flag;
    }
    
    /**
     * ????????????
     * 
     * @author lks 2017???3???8?????????3:18:51
     * @param id
     * @return
     */
    public boolean refund(String id)
    {
        boolean flag = false;
        String message = "????????????";
        Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(id);
        if (exceptionOrder != null)
        {
            if (moveOrder(exceptionOrder))
            {
                flag = chargeOrderSer.dealOrderFail(exceptionOrder, "4", "?????????????????????????????????");
                if (flag)
                {
                    message = "????????????";
                    
                    // ???????????????????????????????????????
					agentBillDiscountSer.addAllParentAgentOrderinfo(exceptionOrder);
                    //???????????????????????????????????????
                    chargeOrderSer.orderCallback(exceptionOrder,AgentCallbackSer.CallbackStatus_Fail);
                }
            }
        }
        return flag;
    }
    
    /**
     * ????????????????????????
     * @author lks 2017???9???8?????????2:53:41
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
            // ????????????
            chargeOrderSer.saveOrderPathRecord(orderPathRecord);
            boolean flag = providerAccountSer.Charge(order);// ??????????????????
            if (!flag)
            {
                log.error("????????????????????????" + MapUtils.toString(order));
                exceptionOrder.put("resultCode", order.get("resultCode") + "|??????????????????????????????????????????");
                exceptionOrderDao.updateByPrimaryKeySelective(exceptionOrder);// ??????????????????
                failNum++;
            }
            else
            {
            	order.put("resultCode", "?????????????????????????????????");
                order.put("endDate", new Timestamp(System.currentTimeMillis()));
                int n = orderDao.insertSelective(order);
                if (n < 1)
                {
                    log.error("?????????????????????" + MapUtils.toString(order));
                    exceptionOrder.put("resultCode", order.get("resultCode") + "|??????????????????????????????????????????");
                    exceptionOrderDao.updateByPrimaryKeySelective(exceptionOrder);// ??????????????????
                    failNum++;
                }
                else
                {
                	// ???????????????????????????????????????
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
                    //???????????????????????????????????????????????????????????????????????????????????????
                    agentAccountService.addAllParentAgentProfit(order, false);
                	exceptionOrderDao.deleteByPrimaryKey((String)exceptionOrder.get("id"));
                    //???????????????????????????????????????
                    chargeOrderSer.orderCallback(order,AgentCallbackSer.CallbackStatus_Success);
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
     * ???????????????????????????????????????
     * 
     * @author lks 2017???3???10?????????2:56:37
     * @param order
     * @return
     */
    @Transactional
    public boolean moveOrder(Map<String, Object> order)
    {
        boolean flag = false;
        String id = order.get("id") + "";// ????????????id
        int a = exceptionOrderDao.deleteByPrimaryKey(id);// ??????????????????????????????
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
            Map<String, Object> m = getMaps(req); // ?????????????????????map
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
            
            Page p = getPage(m);// ??????????????????
            int total = getOrderCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis????????????
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
     * @??????????????? ???????????????????????????????????????
     *
     * @?????????zhangpj @???????????????2017???5???2???
     * @param req
     */
    public void orderParamPage(HttpServletRequest req)
    {
        Map<String, Object> orderParam = selectOrderParam(req);
        req.setAttribute("orderParam", orderParam);
        req.setAttribute("orderType", "2");
    }
    
    /**
     * @??????????????? ????????????????????????????????????
     *
     * @?????????zhangpj @???????????????2017???5???2???
     * @param req
     * @return
     */
    public String getOrderParam(HttpServletRequest req)
    {
        Map<String, Object> orderParam = selectOrderParam(req);
        return JSONObject.toJSONString(orderParam);
    }
    
    /**
     * @??????????????? ??????????????????????????????
     *
     * @?????????zhangpj @???????????????2017???4???26???
     * @param req
     * @return
     */
    public Map<String, Object> selectOrderParam(HttpServletRequest req)
    {
        Map<String, Object> m = getMaps(req); // ?????????????????????map
        String phone = String.valueOf(m.get("phone"));
        String agentOrderId = String.valueOf(m.get("agentOrderId"));
        
        Map<String, Object> orderParam = null;
        if ((null != phone && !phone.equals("")) || (null != agentOrderId && !agentOrderId.equals("")))
        {
            // ?????????????????????????????????????????????????????????????????????
            Map<String, String> orderMap = exceptionOrderDao.selectFastNewOrderByPhone(m);
            if (null != orderMap)
            {
                agentOrderId = orderMap.get("agent_order_id");
                m.put("orderId", agentOrderId);
                // 1.??????????????????
                orderParam = exceptionOrderDao.selectOrderParam(m);
                
                // 2.?????????????????????
                Map<String, String> param = new HashMap<String, String>();
                param.put("agentId", orderParam.get("agentId").toString());
                param.put("providerId", orderParam.get("providerId").toString());
                param.put("billType", orderParam.get("billType").toString());
                param.put("provinceCode", orderParam.get("provinceCode").toString());
                String billType = orderParam.get("billType").toString();
                if ("2".equals(billType))
                {
                    // ??????
                    param.put("value", orderParam.get("value").toString());
                }
                
                Map<String, Object> pkg = billPkgDao.selectBillPkgForAgent(param);
                if (pkg == null)
                {
                    param.put("provinceCode", "??????");
                    pkg = billPkgDao.selectBillPkgForAgent(param);
                }
                
                // 3.????????????????????????
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
     * @Description: ???????????????????????????(?????????????????????????????????????????????)
     * @author CXJ
     * @date 2017???6???7??? ??????5:12:10
     * @param @param suId
     * @param @param ids
     * @param @return
     * @return int ????????????
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
            if ("??????".equals(srName) || "????????????".equals(srName))
            {
                roleFlag = true;
            }
        }
        if (roleFlag)
        {
            
            for (int i = 0; i < ids.length; i++)
            {
                String orderId = ids[i];
                Map<String, Object> exceptionOrder = exceptionOrderDao.selectByPrimaryKey(orderId);// ??????????????????
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
     * ????????????????????????
     * @param req
     * @param res
     */
    public void exportException(HttpServletRequest req,HttpServletResponse res) {
    	Map<String, Object> m = getMaps(req);
    	List<Map<String, Object>> exce = exceptionOrderDao.selectAll(m);
    	
    	HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("??????????????????");
		HSSFRow row = sheet.createRow((int) 0);
        HSSFCell cell = row.createCell((short) 0);
        cell.setCellValue("???????????????");
        cell = row.createCell((short) 1);
        cell.setCellValue("??????????????????");  
        cell = row.createCell((short) 2);  
        cell.setCellValue("?????????");  
        cell = row.createCell((short) 3);  
        cell.setCellValue("????????????");
        cell = row.createCell((short) 4);  
        cell.setCellValue("?????????"); 
        cell = row.createCell((short) 5);  
        cell.setCellValue("??????"); 
        cell = row.createCell((short) 6);  
        cell.setCellValue("?????????"); 
        cell = row.createCell((short) 7);  
        cell.setCellValue("??????"); 
        cell = row.createCell((short) 8); 
        cell.setCellValue("??????");

        for(int i=0;i<exce.size();i++) {
        	row = sheet.createRow((int) i + 1);
        	Map<String, Object> map = exce.get(i);
        	row.createCell(0).setCellValue(map.get("orderId") !=null ? map.get("orderId").toString() : "????????????????????????");
        	row.createCell(1).setCellValue(map.get("agentOrderId") !=null ? map.get("agentOrderId").toString() : "???????????????????????????");
        	row.createCell(2).setCellValue(map.get("agent") !=null ? map.get("agent").toString() : "??????????????????");
        	row.createCell(3).setCellValue(map.get("dispatcherProvider") !=null ? map.get("dispatcherProvider").toString() : "?????????????????????");
        	row.createCell(4).setCellValue(map.get("provider") !=null ? map.get("provider").toString() : "??????????????????");
        	row.createCell(5).setCellValue(map.get("status") !=null ? map.get("status").toString() : "???????????????");
        	row.createCell(6).setCellValue(map.get("phone") !=null ? map.get("phone").toString() : "?????????????????????");
        	row.createCell(7).setCellValue(map.get("bizType") !=null ? map.get("bizType").toString() : "???????????????");
        	row.createCell(8).setCellValue(map.get("fee") !=null ? map.get("fee").toString() : "???????????????");
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
