package com.hyfd.dao.mp;

import java.util.List;
import java.util.Map;

import com.hyfd.dao.BaseDao;

public interface AgentEmailTask2Dao extends BaseDao{
    
	//修改所有代理商发送Email文件状态
    int updateAll(String id);
    
    //查询所有余额低于限额的代理商
    List<Map<String, String>>selectAllAgent();
    
    //查询所有需要发送的邮件
    List<Map<String, Object>> selectAllEmail();
}