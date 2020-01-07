package com.hyfd.service.mp;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hyfd.common.BaseJson;
import com.hyfd.dao.mp.CodeDao;
import com.hyfd.service.BaseService;

@Service
public class CodeSer extends BaseService{

	public Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	CodeDao CodeDao;
	
	public String getProvinceCode(String parent){
		String str = null;
		try {
			List<Map<String, Object>> Code = CodeDao.getProvinceCode(parent);
			str = BaseJson.listToJson(Code);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
	}
	
	public String getCityCode(String parent){
		String str = null;
		try {
			List<Map<String, Object>> Code = CodeDao.getCityCode(parent);
			str = BaseJson.listToJson(Code);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
	}
}
