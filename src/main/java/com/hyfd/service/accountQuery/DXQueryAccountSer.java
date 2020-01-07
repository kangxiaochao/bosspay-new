package com.hyfd.service.accountQuery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseAccountQueryInterface;

/**
 * 鼎信账户余额查询
 * @author Administrator
 *
 */
@Service
public class DXQueryAccountSer implements BaseAccountQueryInterface{

	@Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
	
	@Override
	public String query() {
		String channelId = "2000000003";
		Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(channelId);// 获取通道的数据
        String linkUrl = "http://api.ejiaofei.net:11140/money_jkuser.do";
        String defaultParameter = (String)channel.get("default_parameter");// 默认参数
        Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
		String userId = paramMap.get("userid");// 用户编号	由鼎信商务提供，如：HR0001
		String pwd = paramMap.get("pwd");// 加密密码	由鼎信商务提供。（需大写）
		String key = paramMap.get("key");// 加密秘钥 由鼎信商务提供
		String signStr = "userid" + userId + "pwd" + pwd + key;
		String userkey = MD5.ToMD5(signStr);
		String url = linkUrl + "?userid="+userId+"&pwd="+pwd+"&userkey="+userkey;
		String result = ToolHttp.get(false, url);
		Map<String,String> map = readXmlToMap(result);
		if("0".equals(map.get("tag"))){
			return map.get("lastMoney");
		}else{
			return "查询出错：" + map.get("error");
		}
	}

	public static Map<String, String> readXmlToMap(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List<Element> l = rootElt.elements();
			for (Iterator iterator = l.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				map.put(element.getName(), element.getStringValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
