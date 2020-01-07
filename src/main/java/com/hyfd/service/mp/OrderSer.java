package com.hyfd.service.mp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.StringUtil;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.sys.SysUserRoleDao;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.BaseService;

import net.sf.jxls.transformer.XLSTransformer;

@Service
public class OrderSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    AgentDao agentDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderDao providerDao;
    
    @Autowired
    chargeOrderSer chargeOrderSer;
    
    @Autowired
    RabbitMqProducer mqProducer;
    
    @Autowired
    ProviderAccountSer providerAccountSer;
    
    @Autowired
    ExceptionOrderDao exceptionOrderDao;
    
    @Autowired
    BillPkgDao billPkgDao;
    
    @Autowired
    ProviderBillDispatcherDao providerBillDispatcherDao;
    
    @Autowired
    SysUserRoleDao sysUserRoleDao;
    
	@Autowired
	AgentBillDiscountSer agentBillDiscountSer; // 代理商话费折扣Service
    
    public int getOrderCount(Map<String, Object> m)
    {
        int orderCount = 0;
        try
        {
            orderCount = orderDao.getOrderCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return orderCount;
    }
    
    public String orderList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
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
            List<Map<String, Object>> billList = orderDao.OrderList(m);
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
    
    public String orderListPage()
    {
        Map<String, Object> userInfoMap = getUser();
        String userid = userInfoMap.get("suId") + "";
        List<Map<String, Object>> billList = sysUserRoleDao.getHasSysRoleList(userid);
        String agentRole = null;
        for (Map<String, Object> map : billList)
        {
            String srName = map.get("srName") + "";
            if ("代理商".equals(srName))
            {
                agentRole = srName;
            }
        }
        Session session = getSession();
        session.setAttribute("agentRole", agentRole);
        return "mp/orderList";
    }
    
    /**
     * @功能描述： 根据条件获取订单列表数据并生成json
     *
     * @作者：HYJ @创建时间：2016年1月12日
     * @param req
     * @return
     */
	public String orderAllList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Map<String, Object> userInfoMap = getUser();
			String userid = userInfoMap.get("suId") + "";
			// 根据用户Id查询agent表，若不为null则代表用户为代理商登陆
			Map<String, Object> agentMap = agentDao.selectByUserId(userid);
			if (agentMap != null) {
				// 得到代理商Id，查询其自身订单和其子集订单
				String agentParentId = agentMap.get("id") + "";
				m.put("agentParentId", agentParentId);
			}

			// 根据channel_person查询agent表,若不为null则代表用户为渠道
			List<Map<String, Object>> agentList = agentDao.selectByChannelPerson(userid);
			if (null != agentList && agentList.size() > 0) {
				m.put("channelPerson", userid);
			}

			String agentName = (String) m.get("agentName");
			String dispatcherProviderName = (String) m.get("dispatcherProviderName");
			String providerName = (String) m.get("providerName");
			if (agentName != null && agentName != "") {
				Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
				if (agent != null) {
					String agentId = (String) agent.get("id");
					m.put("agentId", agentId);
				} else {
					m.put("agentId", agentName);
				}
			}
			if (dispatcherProviderName != null && dispatcherProviderName != "") {
				String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
				if (dispatcherProviderId != null && dispatcherProviderId != "") {
					m.put("dispatcherProviderId", dispatcherProviderId);
				} else {
					m.put("dispatcherProviderId", dispatcherProviderName);
				}
			}
			if (providerName != null && providerName != "") {
				String providerId = providerDao.getIdByName(providerName);
				if (providerId != null && providerId != "") {
					m.put("providerId", providerId);
				} else {
					m.put("providerId", providerName);
				}
			}

			Page p = getPage(m);// 提取分页参数
			int total = getOrderCount(m);
			p.setCount(total);
			// int pageNum = p.getCurrentPage();
			// int pageSize = p.getPageSize();
			m.put("beginNum", p.getBeginNum());
			m.put("pageSize", p.getPageSize());

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			// PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			// List<Map<String, Object>> billList = orderDao.selectAll(m);
			List<Map<String, Object>> billList = orderDao.selectAllByPage(m);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return sb.toString();
	}

    /**
     * 批量复充
     * */
    public String batchReCharge(HttpServletRequest request)
    {
        String id = request.getParameter("id");
        String suId = request.getParameter("suId");
        JSONObject json = new JSONObject();
        
        String[] ids = id.split(",");
        int succnum = 0, failnum = 0;
        int tianmaoNum = 0;
        // 判断是否天猫角色复充是否存在不是天猫的账单
        tianmaoNum = judgeTianmaoRole(suId, ids);
        // 大于0则返回
        if (tianmaoNum > 0)
        {
            json.put("succnum", succnum);
            json.put("failnum", failnum);
            json.put("tianmaoNum", tianmaoNum);
            return json.toJSONString();
        }
        for (int i = 0; i < ids.length; i++)
        {
            if (reCharge(ids[i]))
            {
                succnum++;
            }
            else
            {
                failnum++;
            }
        }
        json.put("succnum", succnum);
        json.put("failnum", failnum);
        return json.toJSONString();
    }
    
    /**
     * 批量退款
     * */
    public String batchReFund(HttpServletRequest request)
    {
        String id = request.getParameter("id");
        String suId = request.getParameter("suId");
        JSONObject json = new JSONObject();
        
        String[] ids = id.split(",");
        int succnum = 0, failnum = 0;
        int tianmaoNum = 0;
        // 判断是否天猫角色复充是否存在不是天猫的账单
        tianmaoNum = judgeTianmaoRole(suId, ids);
        // 大于0则返回
        if (tianmaoNum > 0)
        {
            json.put("succnum", succnum);
            json.put("failnum", failnum);
            json.put("tianmaoNum", tianmaoNum);
            return json.toJSONString();
        }
        for (int i = 0; i < ids.length; i++)
        {
            if (refund(ids[i]))
            {
                succnum++;
            }
            else
            {
                failnum++;
            }
        }
        
        json.put("succnum", succnum);
        json.put("failnum", failnum);
        json.put("tianmaoNum", tianmaoNum);
        return json.toJSONString();
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
            Map<String, Object> order = orderDao.selectById(id);// 获取异常订单
            if (order != null)
            {
                String orderStatus = order.get("status") + "";
                if (!orderStatus.equals("2") && !orderStatus.equals("3") && !orderStatus.equals("4"))
                {
                    chargeOrderSer.ReCharge(order);
                    flag = true;
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
        Map<String, Object> order = orderDao.selectById(id);
        if (order != null)
        {
            String orderStatus = order.get("status") + "";
            if (!orderStatus.equals("2") && !orderStatus.equals("3") && !orderStatus.equals("4"))
            {
                flag = chargeOrderSer.dealOrderFail(order, "4", "人工退款处理");
                if (flag)
                {
                	// 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
                	
                    Map<String, Object> callbackMap = new HashMap<String, Object>();
                    callbackMap.put("status", AgentCallbackSer.CallbackStatus_Fail);
                    callbackMap.put("order", order);
                    mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
                        SerializeUtil.getStrFromObj(callbackMap));
                }
            }
        }
        return flag;
    }
    
    /**
     * 修改状态
     * 
     * @author lks 2017年4月22日下午1:47:14
     * @param request
     * @return
     */
	public String updateStatus(HttpServletRequest request) {
		String id = request.getParameter("id");
		String suId = request.getParameter("suId");
		JSONObject json = new JSONObject();
		String status = request.getParameter("status");
		String[] ids = id.split(",");
		int succnum = 0, failnum = 0;
		int tianmaoSum = 0;
		tianmaoSum = judgeTianmaoRole(suId, ids);
		if (tianmaoSum > 0) {
			// JSONObject jsons = new JSONObject();
			json.put("tianmaoSum", tianmaoSum);
			json.put("succnum", succnum);
			json.put("failnum", failnum);
			return json.toJSONString();
		}

		for (int i = 0; i < ids.length; i++) {
			String orderId = ids[i];
			Map<String, Object> order = orderDao.selectById(orderId);// 查询该订单
			if (order != null) {

				String orderStatus = order.get("status") + "";
				String agentId = order.get("agentId") + "";
				if (orderStatus.equals("3") || orderStatus.equals("1")) {// 订单从正在处理状态修改为失败状态
					if (status.equals("4")) {// 置为失败
						boolean flag = chargeOrderSer.dealOrderFail(order, "4", "人工退款处理");
						if (flag) {
							succnum++;

							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);

							Map<String, Object> callbackMap = new HashMap<String, Object>();
							callbackMap.put("status", AgentCallbackSer.CallbackStatus_Fail);
							callbackMap.put("order", order);
							mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
									SerializeUtil.getStrFromObj(callbackMap));
						} else {
							failnum++;
						}
					} else if (status.equals("3")) {// 置为成功
						Map<String, Object> orderPathRecord = new HashMap<String, Object>();
						orderPathRecord.putAll(order);
						orderPathRecord.put("status", "3");
						// 充值流水
						chargeOrderSer.saveOrderPathRecord(orderPathRecord);
						boolean flag = providerAccountSer.Charge(order);// 扣除上家余额
						if (!flag) {
							log.error("扣除上家余额出错" + MapUtils.toString(order));
							orderDao.deleteByPrimaryKey((String) order.get("id"));
							order.put("resultCode", order.get("resultCode") + "|该笔订单扣除上家余额出现异常");
							exceptionOrderDao.insertSelective(order);// 保存异常订单
						} else {
							order.put("status", 3);
							order.put("endDate", new Timestamp(System.currentTimeMillis()));
							int n = orderDao.updateOrder(order);
							if (n < 1) {
								log.error("更新数据库出错" + MapUtils.toString(order));
								orderDao.deleteByPrimaryKey((String) order.get("id"));
								order.put("resultCode", order.get("resultCode") + "|该笔订单更新订单状态出现异常");
								exceptionOrderDao.insertSelective(order);// 保存异常订单
							} else {
								succnum++;
								// 添加订单所有父级代理商记录
								agentBillDiscountSer.addAllParentAgentOrderinfo(order);

								Map<String, Object> callbackMap = new HashMap<String, Object>();
								callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
								callbackMap.put("order", order);
								mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
										SerializeUtil.getStrFromObj(callbackMap));
							}
						}
					}
				} else if ("4".equals(orderStatus) && "6fc1e7e29f4142f48110bc6c5286d723".equals(agentId)) {
					if (status.equals("3")) {// 置为成功
												// Map<String, Object> orderPathRecord = new HashMap<String, Object>();
												// orderPathRecord.putAll(order);
												// orderPathRecord.put("status", "3");
												// 充值流水
												// chargeOrderSer.saveOrderPathRecord(orderPathRecord);
						order.put("status", 3);
						order.put("resultCode", "天猫订单失败状态改为成功");
						order.put("endDate", new Timestamp(System.currentTimeMillis()));
						int n = orderDao.updateOrderByFail(order);
						if (n < 1) {
							log.error("更新数据库出错" + MapUtils.toString(order));
							orderDao.deleteByPrimaryKey((String) order.get("id"));
							order.put("resultCode", order.get("resultCode") + "|该笔订单更新订单状态出现异常");
							exceptionOrderDao.insertSelective(order);// 保存异常订单
						} else {
							succnum++;
							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);

							Map<String, Object> callbackMap = new HashMap<String, Object>();
							callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
							callbackMap.put("order", order);
							mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
									SerializeUtil.getStrFromObj(callbackMap));
						}
					}
				} else {
					failnum++;
				}
			}
		}
		json.put("tianmaoSum", tianmaoSum);
		json.put("succnum", succnum);
		json.put("failnum", failnum);
		return json.toJSONString();
	}

	/**
	 * <h5>功能:批量修改订单状态</h5>
	 * 
	 * @author zhangpj	@date 2019年4月10日
	 * @param req
	 * @return 
	 */
	public String batchModifyOrderStatus(HttpServletRequest req) {
		String id = req.getParameter("agentOrderId");
		JSONObject json = new JSONObject();
		String status = req.getParameter("status");
		String[] ids = id.split(",");
		int succnum = 0, failnum = 0;
		int tianmaoSum = 0;

		for (int i = 0; i < ids.length; i++) {
			String agentOrderId = ids[i];
			Map<String, Object> order = orderDao.selectByAgentOrderId(agentOrderId);// 查询该订单
			if (order != null) {
				String orderStatus = order.get("status") + "";
				String agentId = order.get("agentId") + "";
				if (orderStatus.equals("3") || orderStatus.equals("1")) {// 订单从正在处理状态修改为失败状态
					if (status.equals("4")) {// 置为失败
						boolean flag = chargeOrderSer.dealOrderFail(order, "4", "人工退款处理");
						if (flag) {
							succnum++;

							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);

							Map<String, Object> callbackMap = new HashMap<String, Object>();
							callbackMap.put("status", AgentCallbackSer.CallbackStatus_Fail);
							callbackMap.put("order", order);
							mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
									SerializeUtil.getStrFromObj(callbackMap));
						} else {
							failnum++;
						}
					} else if (status.equals("3")) {// 置为成功
						Map<String, Object> orderPathRecord = new HashMap<String, Object>();
						orderPathRecord.putAll(order);
						orderPathRecord.put("status", "3");
						// 充值流水
						chargeOrderSer.saveOrderPathRecord(orderPathRecord);
						boolean flag = providerAccountSer.Charge(order);// 扣除上家余额
						if (!flag) {
							log.error("扣除上家余额出错" + MapUtils.toString(order));
							orderDao.deleteByPrimaryKey((String) order.get("id"));
							order.put("resultCode", order.get("resultCode") + "|该笔订单扣除上家余额出现异常");
							exceptionOrderDao.insertSelective(order);// 保存异常订单
						} else {
							order.put("status", 3);
							order.put("endDate", new Timestamp(System.currentTimeMillis()));
							int n = orderDao.updateOrder(order);
							if (n < 1) {
								log.error("更新数据库出错" + MapUtils.toString(order));
								orderDao.deleteByPrimaryKey((String) order.get("id"));
								order.put("resultCode", order.get("resultCode") + "|该笔订单更新订单状态出现异常");
								exceptionOrderDao.insertSelective(order);// 保存异常订单
							} else {
								succnum++;
								// 添加订单所有父级代理商记录
								agentBillDiscountSer.addAllParentAgentOrderinfo(order);

								Map<String, Object> callbackMap = new HashMap<String, Object>();
								callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
								callbackMap.put("order", order);
								mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
										SerializeUtil.getStrFromObj(callbackMap));
							}
						}
					}
				} else if ("4".equals(orderStatus) && "6fc1e7e29f4142f48110bc6c5286d723".equals(agentId)) {
					if (status.equals("3")) {// 置为成功
												// Map<String, Object> orderPathRecord = new HashMap<String, Object>();
												// orderPathRecord.putAll(order);
												// orderPathRecord.put("status", "3");
												// 充值流水
												// chargeOrderSer.saveOrderPathRecord(orderPathRecord);
						order.put("status", 3);
						order.put("resultCode", "天猫订单失败状态改为成功");
						order.put("endDate", new Timestamp(System.currentTimeMillis()));
						int n = orderDao.updateOrderByFail(order);
						if (n < 1) {
							log.error("更新数据库出错" + MapUtils.toString(order));
							orderDao.deleteByPrimaryKey((String) order.get("id"));
							order.put("resultCode", order.get("resultCode") + "|该笔订单更新订单状态出现异常");
							exceptionOrderDao.insertSelective(order);// 保存异常订单
						} else {
							succnum++;
							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);

							Map<String, Object> callbackMap = new HashMap<String, Object>();
							callbackMap.put("status", AgentCallbackSer.CallbackStatus_Success);
							callbackMap.put("order", order);
							mqProducer.sendDataToQueue(RabbitMqProducer.Callback_QueueKey,
									SerializeUtil.getStrFromObj(callbackMap));
						}
					}
				} else {
					failnum++;
				}
			}
		}
		json.put("tianmaoSum", tianmaoSum);
		json.put("succnum", succnum);
		json.put("failnum", failnum);
		return json.toJSONString();
	}
    
    /**
     * 导出订单
     * @param req
     * @param res
     */
    public void expOrderX(HttpServletRequest req,HttpServletResponse res){
    	Map<String,Object> param = getMaps(req);
    	Map<String,Object> userInfo = getUser();
    	String userid = userInfo.get("suId")+"";
    	Map<String,Object> agent = agentDao.selectByUserId(userid);
    	if(agent != null){
    		String agentId = agent.get("id")+"";
    		param.put("agentParentId", agentId);
    	}
    	// 根据channel_person查询agent表,若不为null则代表用户为渠道
		List<Map<String, Object>> agentList = agentDao.selectByChannelPerson(userid);
		if (null != agentList && agentList.size() > 0) {
			param.put("channelPerson", userid);
		}
    	
    }
    
    /**
	 * 用流导出excel
	 * @param filename
	 * @param list
	 * @param request
	 * @param response
	 */
	public void exportExcel(String tempName,String filename,Map<String,Object> map,HttpServletRequest request,HttpServletResponse response){
		try{
			String templatePath = request.getServletContext().getRealPath("/") + File.separator + "downloadFiles"+ File.separator+tempName+".xlsx";
			XLSTransformer former = new XLSTransformer();
	        FileInputStream in = new FileInputStream(templatePath);
	        response.setContentType("application/x-excel;charset=utf-8");
	        response.setHeader("Content-Disposition", "attachment;filename="+filename+"_"+System.currentTimeMillis()+".xlsx");
	        response.setCharacterEncoding("utf-8");
	        XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
	        OutputStream os = response.getOutputStream();
	        workbook.write(os);
	        os.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
    	  
    /**
     * <h5>功能描述:</h5> 组合导出数据
     *
     * @param sheetName excel表sheet名称
     * @param orderList 数据来源
     * @return
     *
     * @作者：zhangpj @创建时间：2017年3月22日
     */
    @SuppressWarnings("unused")
	private Map<String, String[][]> initOrderInfo(String sheetName, List<Map<String, Object>> orderList,
        Map<String, Object> agentMap)
    {
        int index = 0;
        int keyIndex = 0;
        int size = orderList.size();
        
        String[][] orderBill = new String[size + 1][50];
        if (null == agentMap)
        {
            orderBill[0][0] = "上家订单号";
            orderBill[0][1] = "平台订单号";
            orderBill[0][2] = "代理商订单号";
            orderBill[0][3] = "代理商名称";
            orderBill[0][4] = "运营商";
            orderBill[0][5] = "物理通道";
            orderBill[0][6] = "手机号";
            orderBill[0][7] = "流量包";
            orderBill[0][8] = "地区";
            orderBill[0][9] = "通道折扣价(元)";
            orderBill[0][10] = "通道折扣";
            orderBill[0][11] = "开始时间";
            orderBill[0][12] = "状态";
            orderBill[0][13] = "结束时间";
            orderBill[0][14] = "原价(元)";
            orderBill[0][15] = "代理商折扣价(元)";
            orderBill[0][16] = "代理商折扣";
            orderBill[0][17] = "利润(元)";
        }
        else
        {
            orderBill[0][0] = "代理商订单号";
            orderBill[0][1] = "代理商名称";
            orderBill[0][2] = "运营商";
            orderBill[0][3] = "手机号";
            orderBill[0][4] = "地区";
            orderBill[0][5] = "流量包";
            orderBill[0][6] = "开始时间";
            orderBill[0][7] = "状态";
            orderBill[0][8] = "结束时间";
            orderBill[0][9] = "原价(元)";
            orderBill[0][10] = "代理商折扣价(元)";
        }
        
        for (int i = 0; i < size; i++)
        {
            index++;
            keyIndex = 0;
            Map<String, Object> map = orderList.get(i);
            
            String applyDate = StringUtil.objToString(map.get("applyDate"));
            String endDate = StringUtil.objToString(map.get("endDate"));
            String orderId = StringUtil.objToString(map.get("orderId"));
            String providerOrderId = StringUtil.objToString(map.get("providerOrderId"));
            String agentOrderId = StringUtil.objToString(map.get("agentOrderId"));
            String providerName = StringUtil.objToString(map.get("providerName"));
            String providerPhysicalChannel = StringUtil.objToString(map.get("providerPhysicalChannel"));
            String providerDiscount = StringUtil.objToString(map.get("providerDiscount"));
            String phone = StringUtil.objToString(map.get("phone"));
            String provinceCode = StringUtil.objToString(map.get("provinceCode"));
            String status = StringUtil.objToString(map.get("status"));
            String fee = StringUtil.objToString(map.get("fee"));
            String value = StringUtil.objToString(map.get("value"));
            String agentName = StringUtil.objToString(map.get("agentName"));
            String agentDiscount = StringUtil.objToString(map.get("agentDiscount"));
            String pkgName = StringUtil.objToString(map.get("pkgName"));
            
            switch (status)
            {
                case "1":
                    status = "处理中";
                    break;
                case "2":
                    status = "提交失败";
                    break;
                case "3":
                    status = "充值成功";
                    break;
                case "4":
                    status = "充值失败";
                    break;
            }
            
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(3);
            double agentFee = 0;
            double providerFee = 0;
            String agentFeeStr = "";
            String providerFeeStr = "";
            // 判断客户折扣不为空
            if (null != agentDiscount && !"".equals(agentDiscount))
            {
                agentFee = Double.parseDouble(fee) * Double.parseDouble(agentDiscount);
            }
            if (agentFee != 0)
            {
                agentFeeStr = nf.format(agentFee) + "";
            }
            // 判断上家折扣不为空
            if (null != providerDiscount && !"".equals(providerDiscount))
            {
                providerFee = Double.parseDouble(fee) * Double.parseDouble(providerDiscount);
            }
            if (providerFee != 0)
            {
                providerFeeStr = nf.format(providerFee) + "";
            }
            // 利润
            double profit = agentFee - providerFee;
            String profitStr = nf.format(profit) + "";
            if (null == agentMap)
            {
                orderBill[index][keyIndex++] = providerOrderId;
                orderBill[index][keyIndex++] = orderId;
                orderBill[index][keyIndex++] = agentOrderId;
                orderBill[index][keyIndex++] = agentName;
                orderBill[index][keyIndex++] = providerName;
                orderBill[index][keyIndex++] = providerPhysicalChannel;
                orderBill[index][keyIndex++] = phone;
                orderBill[index][keyIndex++] = pkgName;
                orderBill[index][keyIndex++] = provinceCode;
                orderBill[index][keyIndex++] = providerFeeStr;
                orderBill[index][keyIndex++] = providerDiscount;
                orderBill[index][keyIndex++] = applyDate;
                orderBill[index][keyIndex++] = status;
                orderBill[index][keyIndex++] = endDate;
                orderBill[index][keyIndex++] = fee;
                orderBill[index][keyIndex++] = agentFeeStr;
                orderBill[index][keyIndex++] = agentDiscount;
                orderBill[index][keyIndex++] = profitStr;
            }
            else
            {
                orderBill[index][keyIndex++] = agentOrderId;
                orderBill[index][keyIndex++] = agentName;
                orderBill[index][keyIndex++] = providerName;
                orderBill[index][keyIndex++] = phone;
                orderBill[index][keyIndex++] = provinceCode;
                orderBill[index][keyIndex++] = pkgName;
                orderBill[index][keyIndex++] = applyDate;
                orderBill[index][keyIndex++] = status;
                orderBill[index][keyIndex++] = endDate;
                orderBill[index][keyIndex++] = fee;
                orderBill[index][keyIndex++] = agentFeeStr;
            }
            
        }
        
        Map<String, String[][]> map = new TreeMap<String, String[][]>();
        map.put(sheetName, orderBill);
        
        return map;
    }
    
    /**
     * @功能描述： 跳转到订单详细参数页面
     *
     * @作者：zhangpj @创建时间：2017年4月26日
     * @param req
     */
    public void orderParamPage(HttpServletRequest req)
    {
        Map<String, Object> orderParam = selectOrderParam(req);
        req.setAttribute("orderParam", orderParam);
        req.setAttribute("orderType", "1");
    }
    
    /**
     * @功能描述： 获取订单详细参数信息
     *
     * @作者：zhangpj @创建时间：2017年4月26日
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
        // 获取登录人Id
        Map<String, Object> userInfoMap = getUser();
        String userid = userInfoMap.get("suId") + "";
        // 根据用户Id查询agent表，若不为null则代表用户为代理商登陆
        Map<String, Object> agentMap = agentDao.selectByUserId(userid);
        if (agentMap != null)
        {
            // 得到代理商Id，查询其自身订单和其子集订单
            String agentParentId = agentMap.get("id") + "";
            m.put("agentParentId", agentParentId);
        }
        Map<String, Object> orderParam = null;
        if ((null != phone && !phone.equals("")) || (null != agentOrderId && !agentOrderId.equals("")))
        {
            // 根据订单编号或者电话号码获取最新一条的订单信息
            Map<String, String> orderMap = orderDao.selectFastNewOrderByPhone(m);
            if (null != orderMap)
            {
                agentOrderId = orderMap.get("order_id");
                m.put("orderId", agentOrderId);
                // 1.获取订单参数
                orderParam = orderDao.selectOrderParam(m);
                
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
     * 获取主页上家需要显示的数据
     * 
     * @author lks 2017年5月19日下午3:12:25
     * @param request
     * @param response
     * @return
     */
    public String countPhysicalChannelProfit(HttpServletRequest request, HttpServletResponse response)
    {
        JSONObject json = new JSONObject();
        Map<String, Object> param = getMaps(request);
        Map<String, Object> user = getUser();
        String suid = user.get("suId") + "";// 用户Id
        String agentId = "";
        List<Map<String, Object>> billList = new ArrayList<Map<String, Object>>();
        if (judgeAgentRole(suid))
        {// 如果有代理商角色
            agentId = agentDao.selectAgentIdByUserid(suid);
            if (agentId != null && !agentId.equals(""))
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("agentId", agentId);
                map.putAll(param);
                billList = orderDao.selectPhysicalChannelProfit(map);
            }
        }
        else
        {
            billList = orderDao.selectPhysicalChannelProfit(param);
        }
        json.put("data", JSONObject.toJSON(billList));
        return json.toJSONString();
    }
    
   
    
    /**
     * 判断用户是否具有代理商的角色
     * 
     * @author lks 2017年5月18日下午2:36:33
     * @param suid
     * @return
     */
    public boolean judgeAgentRole(String suid)
    {
        boolean flag = false;
        List<Map<String, Object>> list = sysUserRoleDao.getHasSysRoleList(suid);
        for (Map<String, Object> role : list)
        {
            String roleName = role.get("srName") + "";
            if (roleName.equals("代理商"))
            {
                flag = true;
            }
        }
        return flag;
    }
    
    /**
     * @Title:judgeTianmaoRole
     * @Description: 判断是否是天猫订单(这里用一句话描述这个方法的作用)
     * @author CXJ
     * @date 2017年6月7日 下午5:12:10
     * @param @param suId
     * @param @param ids
     * @param @return
     * @return int 返回类型
     * @throws
     */
    public int judgeTianmaoRole(String suId, String[] ids)
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
                Map<String, Object> order = orderDao.selectById(orderId);// 查询该订单
                if (order != null)
                {
                    String agentId = order.get("agentId") + "";
                    if (!"6fc1e7e29f4142f48110bc6c5286d723".equals(agentId)
                        && !"81c2390df095475f86f0e9c6dff1cfd2".equals(agentId))
                    {
                        tianmaoSum += 1;
                    }
                }
            }
        }
        
        return tianmaoSum;
    }
    
    public void expOrder(HttpServletResponse response, HttpServletRequest req){
		Map<String, Object> m = getMaps(req); // 封装前台参数为map

		Map<String, Object> userInfoMap = getUser();
		
		int states = 2;
		if(userInfoMap.get("suName").equals("admin")) {
			states = 1;
		}
		String userid = userInfoMap.get("suId") + "";
		// 根据用户Id查询agent表，若不为null则代表用户为代理商登陆
		Map<String, Object> agentMap = agentDao.selectByUserId(userid);
		if (agentMap != null) {
			// 得到代理商Id，查询其自身订单和其子集订单
			String agentParentId = agentMap.get("id") + "";
			m.put("agentParentId", agentParentId);
		}

		// 根据channel_person查询agent表,若不为null则代表用户为渠道
		List<Map<String, Object>> agentList = agentDao.selectByChannelPerson(userid);
		if (null != agentList && agentList.size() > 0) {
			m.put("channelPerson", userid);
		}

		String agentName = (String) m.get("agentName");
		String dispatcherProviderName = (String) m.get("dispatcherProviderName");
		String providerName = (String) m.get("providerName");
		if (agentName != null && agentName != "") {
			Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
			if (agent != null) {
				String agentId = (String) agent.get("id");
				m.put("agentId", agentId);
			} else {
				m.put("agentId", agentName);
			}
		}
		if (dispatcherProviderName != null && dispatcherProviderName != "") {
			String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
			if (dispatcherProviderId != null && dispatcherProviderId != "") {
				m.put("dispatcherProviderId", dispatcherProviderId);
			} else {
				m.put("dispatcherProviderId", dispatcherProviderName);
			}
		}
		if (providerName != null && providerName != "") {
			String providerId = providerDao.getIdByName(providerName);
			if (providerId != null && providerId != "") {
				m.put("providerId", providerId);
			} else {
				m.put("providerId", providerName);
			}
		}

		String applyDate = DateTimeUtils.formatDate(m.get("applyDate").toString());
		String endDate = DateTimeUtils.formatDate(m.get("endDate").toString());
		String sheetName = "话费充值报表";
		String fileName = applyDate + "-" + endDate + sheetName;
		
		
		String[] headers =  states == 1 
				? "代理商订单号,代理商名称,运营商,手机号,地区,话费包,开始时间,状态,原价(元),代理商折扣价(元),代理商折扣,上家订单号,平台订单号,物理通道,通道折扣价(元),通道折扣,归属渠道".split(",")
				: "代理商订单号,代理商名称,运营商,手机号,地区,话费包,开始时间,状态,原价(元),代理商折扣价(元)".split(",");
		try {
			int total = getOrderCount(m);
			File result = returnToTheResult(m,fileName,headers,states,total);
			
			outCsvStream(response, result,fileName);
			//删除临时文件
	        deleteFile(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
    /**
     * 导出大批量CSV数据
     * @param m
     * @param fileName
     * @param headers
     * @param states
     * @return
     * @throws IOException
     */
    public File returnToTheResult(Map<String, Object> m,String fileName,String[] headers,int states,int total) throws IOException{
    	File tempFile = File.createTempFile(fileName, ".csv");
		CsvWriter csvWriter = new CsvWriter(tempFile.getCanonicalPath(), ',', Charset.forName("UTF-8"));
		csvWriter.writeRecord(headers);

		int sum = total % 5000 == 0 ?  total/5000 : total/5000+1;
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
		
		for (int i = 1; i <= sum; i++) {
			int pageNum = i;
			fixedThreadPool.submit(() -> {
				PageHelper.startPage(pageNum, 5000);// mybatis分页插件
				List<Map<String, Object>> billList = orderDao.selectAllByPage(m);
				billList.forEach(bill -> {
					String status = bill.get("status") + "";
					switch (status) {
						case "1":
							status = "处理中";
							break;
						case "2":
							status = "提交失败";
							break;
						case "3":
							status = "充值成功";
							break;
						case "4":
							status = "充值失败";
							break;
					}
					try {//上家订单号,平台订单号,物理通道,通道折扣价(元),通道折扣(元),归属渠道
						csvWriter.write(bill.get("agentOrderId") + "");
						csvWriter.write(bill.get("agent") + "");
						csvWriter.write(bill.get("provider") + "");
						csvWriter.write(bill.get("phone") + "");
						csvWriter.write(bill.get("province") + "");
						csvWriter.write(bill.get("productName") + "");
						csvWriter.write("'" + bill.get("applyDate"));
						csvWriter.write(status);
						double fee = Double.parseDouble(bill.get("fee") + "");
						csvWriter.write(fee + "");
						double agentFee = fee * Double.parseDouble(bill.get("agentDiscount") + "");
						csvWriter.write(agentFee + "");
						if(states == 1) {
							csvWriter.write(bill.get("agentDiscount") + "");
							csvWriter.write("'" + bill.get("providerOrderId"));
							csvWriter.write("'" + bill.get("orderId"));
							csvWriter.write(bill.get("dispatcherProvider") + "");
							csvWriter.write(fee * Double.parseDouble(bill.get("providerDiscount") + "") + "");
							csvWriter.write(bill.get("providerDiscount") + "");
							csvWriter.write(bill.get("channelPerson") + "");
						}
						csvWriter.endRecord();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				csvWriter.close();
				return tempFile;
			}
		}
    }
    
    
    //------------------future导出Csv---------------------------------
    public class Task implements Callable<List<Map<String, Object>>> {
		int i;
		int j;
		Map<String,Object> param = new HashMap<String,Object>();

		public Task(Map<String,Object> param, int i, int j) {
			this.param = param;
			this.i = i;
			this.j = j;
		}

		@Override
		public List<Map<String, Object>> call() throws Exception {
			List<Map<String, Object>> list = getOrderList(param, i, j);
			return list;
		}

	}
    
    public List<Map<String, Object>> getOrderList(Map<String,Object> param, int i, int j) {
		Map<String, Object> userInfoMap = getUser();
		String userid = userInfoMap.get("suId") + "";
		// 根据用户Id查询agent表，若不为null则代表用户为代理商登陆
		Map<String, Object> agentMap = agentDao.selectByUserId(userid);
		if (agentMap != null) {
			// 得到代理商Id，查询其自身订单和其子集订单
			String agentParentId = agentMap.get("id") + "";
			param.put("agentParentId", agentParentId);
		}

		// 根据channel_person查询agent表,若不为null则代表用户为渠道
		List<Map<String, Object>> agentList = agentDao.selectByChannelPerson(userid);
		if (null != agentList && agentList.size() > 0) {
			param.put("channelPerson", userid);
		}

		String agentName = (String) param.get("agentName");
		String dispatcherProviderName = (String) param.get("dispatcherProviderName");
		String providerName = (String) param.get("providerName");
		if (agentName != null && agentName != "") {
			Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
			if (agent != null) {
				String agentId = (String) agent.get("id");
				param.put("agentId", agentId);
			} else {
				param.put("agentId", agentName);
			}
		}
		if (dispatcherProviderName != null && dispatcherProviderName != "") {
			String dispatcherProviderId = providerPhysicalChannelDao.getProviderIdByName(dispatcherProviderName);
			if (dispatcherProviderId != null && dispatcherProviderId != "") {
				param.put("dispatcherProviderId", dispatcherProviderId);
			} else {
				param.put("dispatcherProviderId", dispatcherProviderName);
			}
		}
		if (providerName != null && providerName != "") {
			String providerId = providerDao.getIdByName(providerName);
			if (providerId != null && providerId != "") {
				param.put("providerId", providerId);
			} else {
				param.put("providerId", providerName);
			}
		}
		param.put("beginNum", i);
		param.put("pageSize", j);
		List<Map<String, Object>> billList = orderDao.selectAllByPage(param);
		return billList;
	}
    
    /**
     * 利用future导出订单
     * @param req
     */
    public void exportCsv4Future(HttpServletRequest request,HttpServletResponse response){
    	Map<String,Object> param = getMaps(request);
    	Map<String, Object> userInfoMap = getUser();
		String userid = userInfoMap.get("suId") + "";
    	int total = getOrderCount(param);
    	List<Future<List<Map<String, Object>>>> results = new ArrayList<Future<List<Map<String, Object>>>>();
		ExecutorService es = Executors.newCachedThreadPool();
		Map<String,Integer> map = getQuotientAndMod(total);
		int quotient = map.get("quotient");
		int mod = map.get("mod");
		for(int i = 0 ; i < quotient; i++){
			if(i == (quotient -1) ){
				if(mod != 0){
					results.add(es.submit(new Task(param,i*10000,mod)));
				}
			}else{
				results.add(es.submit(new Task(param,i*10000,10000)));
			}
		}
		try {
			File tempFile = File.createTempFile("话费报表"+System.currentTimeMillis(), ".csv");
			String headers = judgeAgentRole(userid) ? "平台订单号,代理商订单号,运营商,手机号,面值,代理商名,提交时间,结束时间,"
					+ "状态(1提交成功,2提交失败,3充值成功,4充值失败),原价,代理商折扣" 
					: "平台订单号,代理商订单号,上家订单号,运营商,物理通道,手机号,面值,代理商名,提交时间,结束时间,状态,原价,代理商折扣,上家折扣";
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile),"utf-8"));
			bw.write(headers);
			bw.newLine();
			for(Future<List<Map<String, Object>>> res : results){
				try {
					List<Map<String, Object>> list = res.get();
					wirteCsv(list,bw,userid);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			outCsvStream(response, tempFile,"话费报表_"+System.currentTimeMillis());
			//删除临时文件
	        deleteFile(tempFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
    /**
     * 获取商和模
     * @param sum
     * @return
     */
    public Map<String,Integer> getQuotientAndMod(int sum){
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	int quotient = sum/10000;
    	int mod = sum%10000;
    	if(mod == 0){
    		map.put("quotient", quotient);
    		map.put("mod", mod);
    	}else{
    		map.put("quotient", quotient + 1);
    		map.put("mod", mod);
    	}
    	return map;
    }
    
    public synchronized void wirteCsv(List<Map<String, Object>> list,BufferedWriter bw,String userid) {
		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // 附加
			for (int i = 0; i < list.size(); i++) {
				String str = Map2Str(list.get(i),userid);
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String Map2Str(Map<String, Object> map,String userid) {
		String str = "";
		if(judgeAgentRole(userid)){//是代理商
			String[] arr = new String[11];
			arr[0] = map.get("orderId")+"";
			arr[1] = map.get("agentOrderId")+"";
			arr[2] = map.get("provider")+"";
			arr[3] = map.get("phone")+"";
			arr[4] = map.get("fee")+"";
			arr[5] = map.get("agent")+"";
			arr[6] = map.get("applyDate")+"";
			arr[7] = map.get("endDate")+"";
			arr[8] = map.get("status")+"";
			arr[9] = map.get("fee")+"";
			arr[10] = map.get("agentDiscount")+"";
			for (int x = 0; x < arr.length; x++) {
				if (x == arr.length - 1) {
					str += arr[x];
				} else {
					if(x == 4 || x == 9){
						str += (arr[x] + ",");
					}else{
						str += ("=\"" + arr[x] + "\"" + ",");
					}
				}
			}
		}else{
			String[] arr = new String[14];
			arr[0] = map.get("orderId")+"";
			arr[1] = map.get("agentOrderId")+"";
			arr[2] = map.get("providerOrderId")+"";
			arr[3] = map.get("provider")+"";
			arr[4] = map.get("dispatcherProvider")+"";
			arr[5] = map.get("phone")+"";
			arr[6] = map.get("fee")+"";
			arr[7] = map.get("agent")+"";
			arr[8] = map.get("applyDate")+"";
			arr[9] = map.get("endDate")+"";
			arr[10] = map.get("status")+"";
			arr[11] = map.get("fee")+"";
			arr[12] = map.get("agentDiscount")+"";
			arr[13] = map.get("providerDiscount")+"";
			for (int x = 0; x < arr.length; x++) {
				if (x == arr.length - 1) {
					str += arr[x];
				} else {
					if(x == 6 || x == 11 || x == 12){
						str += (arr[x] + ",");
					}else{
						str += ("=\"" + arr[x] + "\"" + ",");
					}

				}
			}
		}
		return str;
	}
	
    //---------------------------------------------------------------
}
