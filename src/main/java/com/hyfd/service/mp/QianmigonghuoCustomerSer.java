package com.hyfd.service.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyfd.dao.mp.QianmigonghuoCustomerDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class QianmigonghuoCustomerSer extends BaseService {
	
	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	QianmigonghuoCustomerDao qianmigonghuoCustomer;

	public List<Map<String, Object>> getQianmigonghuoCustomerList()
    {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "1");	// 启用状态
		map.put("del_flag", 1); // 未删除状态
        List<Map<String, Object>> customerList = qianmigonghuoCustomer.selectAll(map);
        
        return customerList;
    }
}
