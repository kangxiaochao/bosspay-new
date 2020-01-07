package com.hyfd.task;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolHttp;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ProductDao;
import com.hyfd.dao.mp.ProviderBillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.ProviderProductDao;

@Component
public class E19ProductTask {

	private static Logger log = Logger.getLogger(E19ProductTask.class);
	
	private static String productListFile="G:/19eProductList.txt";
	
	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;//物理通道信息
	@Autowired
	ProviderProductDao productDao;//19e产品列表
	@Autowired
	BillPkgDao billPkgDao;
	@Autowired
	ProviderDao providerDao;
	
	@Scheduled(cron="0 0 1 * * ?")//每天凌晨1点更新
//	@Scheduled(fixedDelay = 60000)
	public void getE19Product(){
		try{
			String id = "2000000006";//物理通道表里的id
			Map<String,Object> channel = providerPhysicalChannelDao.selectByPrimaryKey(id);//获取19e的通道信息
			String defaultParameter = (String) channel.get("default_parameter");//默认参数
			Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
			List<Map<String, String>> productList = getProductList(paramMap);
//			{prodIsptype=移动, prodDelaytimes=5分钟, prodContent=30, prodProvinceid=北京, prodPrice=29.94, prodId=10003, prodType=移动电话}
			for(Map<String, String> product : productList){
//				Thread.sleep(1000);
				String prodId = product.get("prodId");//产品ID
				String prodIsptype = product.get("prodIsptype");//运营商
				String prodContent = product.get("prodContent");
				String prodProvinceid = product.get("prodProvinceid");
				Map<String,Object> pkgParam = new HashMap<String,Object>();
				pkgParam.put("value", prodContent);
				Map<String,Object> pkg = billPkgDao.selectPkgs(pkgParam);
				if(pkg != null){
					String pkgid = (String) pkg.get("id");
					Map<String,Object> productMap = new HashMap<String,Object>();
					productMap.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
					productMap.put("pkgId", pkgid);
					productMap.put("providerId", providerDao.getIdByShortName(prodIsptype));
					productMap.put("physicalChannelId", "2000000006");
					productMap.put("providerPkgId", prodId);
					productMap.put("provinceCode", prodProvinceid);
					Map<String, Object> pMap = new HashMap<String,Object>();
					pMap.put("providerPkgId", prodId);
					List<Map<String,Object>> t = productDao.selectAll(pMap);
					if(t.size() == 0){
						productDao.insertSelective(productMap);
					}
				}
				
			}
		}catch(Exception e){
			log.error("更新19e的产品列表出错"+e.getMessage());
		}
	}
	
	/**
	 * 获取19e的产品列表
	 * @author lks 2017年1月12日下午2:56:18
	 * @return
	 */
	public static List<Map<String, String>> getProductList(Map<String,String> paramMap) {
		List<Map<String, String>> productList=new ArrayList<Map<String,String>>();
		String xml="";
		try{
			xml=getProductXmlData(paramMap);
		}catch(Exception e){
			log.error("19e get product list exception|"+e.getMessage());
			xml=getProductFromFile();
		}
		try{
		Document doc = DocumentHelper.parseText(xml);
		Element rootElt = doc.getRootElement(); // 获取根节点
		Iterator<?> it = rootElt.elementIterator();
		while (it.hasNext()) {
			Element recordEle = (Element) it.next();
			List<?> elList=recordEle.elements();
			Map<String, String> product=new HashMap<String, String>();
			for(int i=0;i<elList.size();i++){
				Element el=(Element) elList.get(i);
				product.put(el.attributeValue("name"), URLDecoder.decode(el.attributeValue("value"),"utf-8"));
			}
			productList.add(product);
		}
		}catch(Exception e){
			log.error("19e product error|"+e.getMessage());
		}
		return productList;		
	}
	
	/**
	 * 
	 * @param productList
	 * @param prodContent 产品面值
	 * @param prodIsptype 运营商名称
	 * @param prodProvincedid 省份名称
	 * @param prodType 充值类型座机,移动电话
	 * @return
	 */
	public static Map<String, String> getProduct(List<Map<String, String>> productList,String prodContent,String prodIsptype,String prodProvincedid,String prodType){
		Map<String, String> product=new HashMap<String, String>();
		for(Map<String, String> m:productList){
			if(prodContent.equals(m.get("prodContent")) && prodIsptype.equals(m.get("prodIsptype")) && prodProvincedid.equals(m.get("prodProvinceid")) && prodType.equals(m.get("prodType"))){
				return m;
			}
		}
		return product;
	}
	
	public static String getProductXmlData(Map<String,String> paramMap){
		String productData="";
		String directProductUrl=paramMap.get("directProductUrl");//产品列表
		String verifystring="";
		String agentid=paramMap.get("agentId");
		String source=paramMap.get("source"); //固定值
		String merchantKey=paramMap.get("merchantKey");
		verifystring=getDirectProductVerify(agentid,source,merchantKey);
		directProductUrl+="?agentid="+agentid;
		directProductUrl+="&source="+source;
		directProductUrl+="&verifystring="+verifystring;
		productData=ToolHttp.get(false, directProductUrl);
		return productData;
	}
	
	public static Map<String,String> getAccegmentReturnData(String xml) throws Exception{
		Map<String, String> rm=new HashMap<String, String>();
		Document doc = DocumentHelper.parseText(xml);
		Element rootElt = doc.getRootElement(); 
		Iterator<?> it = rootElt.elementIterator();
		while (it.hasNext()) {
			Element recordEle = (Element) it.next();
			List<?> elList=recordEle.elements();
			for(int i=0;i<elList.size();i++){
				Element el=(Element) elList.get(i);
				rm.put(el.attributeValue("name"), URLDecoder.decode(el.attributeValue("value"),"utf-8"));
			}
		}
		return rm;
	}
	
	public static String getDirectProductVerify(String agentid,String source,String merchantKey){
		StringBuilder sb=new StringBuilder();
		sb.append("agentid="+agentid);
		sb.append("&source="+source);
		sb.append("&merchantKey="+merchantKey);
		return getKeyedDigest(sb.toString(),"");
	}
	
	public static String getAccegmentVerify(String agentid,String source,String mobilenum,String merchantKey){
		StringBuilder sb=new StringBuilder();
		sb.append("agentid="+agentid);
		sb.append("&source="+source);
		sb.append("&mobilenum="+mobilenum);
		sb.append("&merchantKey="+merchantKey);
		return getKeyedDigest(sb.toString(),"");
	}
	
	/**
	 * MD5加密
	 * @author lks 2017年1月12日下午3:00:56
	 * @param strSrc
	 * @param key
	 * @return
	 */
	public static String getKeyedDigest(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            
            String result="";
            byte[] temp;
            temp=md5.digest(key.getBytes("UTF8"));
    		for (int i=0; i<temp.length; i++){
    			result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
    		}
    		
    		return result;
    		
        } catch (NoSuchAlgorithmException e) {
        	
        	e.printStackTrace();
        	
        }catch(Exception e)
        {
          e.printStackTrace();
        }
        return null;
    }
	
	public static String getProductFromFile(){
		String result="";
		try{
			result=FileUtils.readFileToString(new File(productListFile),"UTF-8");
		}catch(Exception e){
			log.error("19E产品列表文件异常!请检查c:/19eProductList.txt"+e.getMessage());
		}
		return result;	
	}
}
