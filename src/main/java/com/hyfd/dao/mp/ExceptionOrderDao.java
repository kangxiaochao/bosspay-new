package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface ExceptionOrderDao extends BaseDao {

	public int getOrderCount(Map<String, Object> m);
	
	/**
	 * @功能描述：	获取异常订单参数
	 *
	 * @作者：zhangpj		@创建时间：2017年5月2日
	 * @param m
	 * @return
	 */
	public Map<String, Object> selectOrderParam(Map<String, Object> m);
	
	/**
	 * @功能描述：	根据订单编号或者电话号码获取最新一条的异常订单信息
	 *
	 * @作者：zhangpj		@创建时间：2017年5月2日
	 * @param m
	 * @return
	 */
	public Map<String, String> selectFastNewOrderByPhone(Map<String, Object> m);

	public List<Map<String, Object>> selectByTask(Map<String, Object> param);

	public List<Map<String, Object>> selectByQueryOrder(Map<String, Object> queryParam);

	public Map<String, Object> selectByOrderId(String orderId);

}