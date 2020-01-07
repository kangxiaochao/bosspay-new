package com.hyfd.dao.mp;

import java.util.Map;

public interface MpKeytDao {
	
    int insert(Map<String, Object> record);

    //添加密钥信息
    int insertSelective(Map<String, Object> record);
    
}