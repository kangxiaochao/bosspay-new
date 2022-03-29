package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface PhoneSectionDao extends BaseDao {

	/**
	 * 跟据号段获取号段信息
	 * @author lks 2016年12月7日上午11:41:18
	 * @param section
	 * @return
	 */
	Map<String, Object> selectBySection(String section);
	
	/**
	 * @功能描述：	根据条件获取号段记录总数
	 *
	 * @作者：zhangpj		@创建时间：2016年12月29日
	 * @param Map
	 * @return
	 */
	Integer selectCount(Map<String,Object> Map);
	
	/**
	 * @功能描述：	根据号段查询号码信息
	 *
	 * @param section
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月16日
	 */
	Map<String, Object> selectBySectionExt(String section);
	
	/**
	 * 批量添加号段
	 * @param record
	 * @return
	 */
	int batchinsert(List<Map<String, Object>> record);



	Integer selectSectionbyunique(String phone);
}