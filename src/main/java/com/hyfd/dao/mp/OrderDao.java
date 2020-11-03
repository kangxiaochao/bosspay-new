package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface OrderDao extends BaseDao
{
    
    public int getOrderCount(Map<String, Object> m);
    
    public List<Map<String, Object>> OrderList(Map<String, Object> m);
    
    /**
     * 根据平台订单号获取订单
     * 
     * @author lks 2016年12月14日下午1:50:16
     * @param customerOrderId
     * @return
     */
    Map<String, Object> selectByOrderId(String orderId);
    
    /**
     * 根据代理商订单号获取订单
     * 
     * @author lks 2016年12月14日下午1:50:16
     * @param customerOrderId
     * @return
     */
    Map<String, Object> selectByAgentOrderId(String agentOrderId);
    
    /**
     * @功能描述： 分页获取订单信息
     *
     * @作者：zhangpj @创建时间：2016年12月12日
     * @param record
     * @return
     */
    List<Map<String, Object>> selectAllByPage(Map<?, ?> record);
    
    /**
     * 更新订单状态，避免查询与回调冲突
     * 
     * @author lks 2016年12月23日下午3:27:54
     * @param order
     * @return
     */
    int updateOrder(Map<String, Object> order);
    
    int updateOrderByFail(Map<String, Object> order);
    
    /**
     * <h5>功能描述:</h5> 根据订单类型或者请求日期查询订单信息
     *
     * @param record
     * @return
     *
     * @作者：zhangpj @创建时间：2017年3月22日
     */
    List<Map<String, Object>> selectOrderByBizTypeAndApplyDate(Map<?, ?> record);
    
    /**
     * 根据上家订单号获取数据
     * 
     * @author lks 2017年4月1日下午3:12:01
     * @param upids
     * @return
     */
    public Map<String, Object> selectByProviderOrderId(String upids);
    
    public List<Map<String, Object>> selectByTask(Map<String, Object> param);
    
    public Map<String, Object> selectById(String orderId);
    
    /**
     * @功能描述： 获取订单参数
     *
     * @作者：zhangpj @创建时间：2017年4月22日
     * @param m
     * @return
     */
    public Map<String, Object> selectOrderParam(Map<String, Object> m);
    
    /**
     * @功能描述： 根据订单编号或者电话号码获取最新一条的订单信息
     *
     * @作者：zhangpj @创建时间：2017年4月27日
     * @param m
     * @return
     */
    public Map<String, String> selectFastNewOrderByPhone(Map<String, Object> m);
    
    /**
     * 查询利润
     * 
     * @author lks 2017年5月12日下午4:44:00
     * @param map
     * @return
     */
    public double selectProfit(Map<String, Object> map);
    
    public int countOrder(Map<String, Object> numMap);
    
    public List<Map<String, Object>> selectPhysicalChannelProfit(Map<String, Object> map);
    
    public int countProviderStatementList(Map<String, Object> param);
   
    public List<Map<String, Object>> selectProviderStatementList(Map<String, Object> param);
    
    // 查询账单列表页面数据总数新方法
    public int countProviderStatementListExt(Map<String, Object> param);
    
    // 查询账单列表页面数据新方法
    public List<Map<String, Object>> selectProviderStatementListExt(Map<String, Object> param);
    
    public int countDetailOrderList(Map<String, Object> param);
    
    public List<Map<String, Object>> selectDetailOrderList(Map<String, Object> param);
    
    // 获取话费通道账单统计详情列表总数new
    public int countDetailOrderListExt(Map<String, Object> param);
    
    // 获取话费通道账单统计详情列表new
    public List<Map<String, Object>> selectDetailOrderListExt(Map<String, Object> param);
    
    //获取导出话费订单信息的总条数
    public int selectCountCSV(Map<String, Object> param);
    
    //获取导出的话费订单信息
    public List<Map<String, Object>> selectDetailOrderListCSV(Map<String, Object> param);
    
    public int countAgentStatementList(Map<String, Object> param);
    
    public List<Map<String, Object>> selectAgentStatementList(Map<String, Object> param);
    
    public List<Map<String, Object>> selectForntAgent(Map<String, Object> agentMap);
    
    public List<String> selectForntDate(Map<String, Object> agentParam);

	public int countOrderByTask(String dispatcherProviderId);

	public int checkAgentOrderId(String agentOrderId);
    
	/**
	 * @功能描述：	获取千米供货7天以内1分钟以前充值 成功/失败 还未回调的订单
	 *
	 * @param param
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年1月15日
	 */
	public List<Map<String, Object>> selectGongHuoPingTaiOrder(Map<String, Object> param);

	public List<String> selectAgentChartsData(Map<String, Object> agentMap);
	
	/**
	 * @功能描述：	获取指定时间段内有充值成功订单的通道id
	 *
	 * @param agentMap
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月9日
	 */
	public List<Map<String, Object>> selectDispatcherProvider(Map<String, Object> param);

	public int checkPhone(Map<String, Object> param);

	public List<Map<String, Object>> selectByQueryOrder(Map<String, Object> queryParam);
	
	/**
	 * 查询充值号码是否在充值通道的指定号段中
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> specifySectionRecharge(Map<String, Object> map);
}