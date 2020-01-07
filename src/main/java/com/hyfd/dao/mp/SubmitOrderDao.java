package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface SubmitOrderDao extends BaseDao {

	int selectCount(Map<String, Object> param);

}