package com.hyfd.service.accountQuery;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseAccountQueryInterface;

/**
 * 远特查询账户余额
 * @author Administrator
 *
 */
@Service
public class YTQueryAccountSer implements BaseAccountQueryInterface{

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	@Override
	public String query() {
		String id = "2000000021";// 物理通道ID ~~~~~
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
		String defaultParameter = (String) channel.get("default_parameter");// 默认参数
		Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String desUrl = paramMap.get("desUrl"); // 加密地址
		String privateKey = paramMap.get("privateKey");// 加密秘钥
		String userName = paramMap.get("userName"); // Boss系统分配，定值
		String userPwd = paramMap.get("userPwd"); // Boss系统分配，定值
		String systemId = paramMap.get("systemId");
		String intfType = paramMap.get("ddanType");// 接口类型：1异步接口,定值
		String userId = paramMap.get("userId");// 用户标识, 填写0,定值
		String dealerId = paramMap.get("dealerId");
		String serviceKind = paramMap.get("serviceKind"); // 业务类型，定值
		String url = paramMap.get("linkUrl"); // 请求地址,定值
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
