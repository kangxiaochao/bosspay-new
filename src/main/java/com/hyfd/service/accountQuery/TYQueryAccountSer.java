package com.hyfd.service.accountQuery;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.deal.Bill.TianYinBillDeal;
import com.hyfd.service.BaseAccountQueryInterface;

@Service
public class TYQueryAccountSer implements BaseAccountQueryInterface{

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 物理通道信息
	
	@Override
	public String query() {
		String id = "2000000015";
        Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);// 获取通道的数据
        String defaultParameter = (String)channel.get("default_parameter");// 默认参数
        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
        String searchUrl = paramMap.get("searchUrl") + "";// 查询地址
        String platyformId = paramMap.get("platyformId"); // 平台编号
        String orgcode = paramMap.get("orgcode"); // 机构代码
        String requestid = platyformId + ToolDateTime.format(new Date(), "yyyyMMddHHmmss") + TianYinBillDeal.getFourSquece();
        String requesttime = ToolDateTime.format(new Date(), "yyyyMMddHHmmss");
        
		return null;
	}

}
