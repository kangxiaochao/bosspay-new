package com.hyfd.service.accountQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseAccountQueryInterface;

@Service
public class YSQueryAccountSer implements BaseAccountQueryInterface{

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	public String query() {
		
		return null;
	}
	
}
