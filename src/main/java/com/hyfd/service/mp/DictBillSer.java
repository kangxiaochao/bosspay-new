package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.BaseJson;
import com.hyfd.dao.mp.DictBillDao;
import com.hyfd.service.BaseService;

@Service
public class DictBillSer extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	DictBillDao dictBillDao;
	
	public String getProvinceCode(String name){
		String str = null;
		try {
			List<Map<String, Object>> provinceCode = dictBillDao.getProvinceCode(name);
			str = BaseJson.listToJson(provinceCode);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
	}
}
