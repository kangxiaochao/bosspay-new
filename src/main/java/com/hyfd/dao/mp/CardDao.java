package com.hyfd.dao.mp;

import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface CardDao extends BaseDao {

	Map<String, Object> selectOneCard(Map<String, Object> cardParam);

	int updateState(Map<String, Object> cardParam);

	int countCard(Map<String, Object> m);

	int updateCard(Map<String, Object> card);

	Map<String, Object> selectByOrderId(Map<String, Object> cardParam);

	Map<String, Object> queryByCardId(String cardId);
}