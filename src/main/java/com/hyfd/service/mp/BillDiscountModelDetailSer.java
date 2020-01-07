package com.hyfd.service.mp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.ExcelUtil;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.BillDiscountModelDetailDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class BillDiscountModelDetailSer extends BaseService {

	@Autowired
	private BillDiscountModelDetailDao billDiscountModelDetailDao;
	@Autowired
	BillPkgDao billPkgDao;
	@Autowired
	ProviderDao providerDao;

	Logger log = Logger.getLogger(this.getClass());

	public String billDiscountModelDetailList(HttpServletRequest req,String modelId) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			m.put("modelId", modelId);
			String providerName = (String) m.get("pname");
			if(providerName!=null&&providerName!=""){
				String providerId = providerDao.getIdByName(providerName);
				if(providerId!=null&&providerId!=""){
					m.put("providerId", providerId);
				}else{
					m.remove("pname");
				}
			}
			
			Page p = getPage(m);// 提取分页参数
			int total = getBillDiscountModelDetailCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = billDiscountModelDetailDao
					.getBillDiscountModelDetailList(m);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e,log);
		}

		return sb.toString();
	}
	/**
	 * 带着模板ID跳转到模板详情修改页
	 * */
	public String billDiscountModelDetailEditPage(String modelId,HttpServletRequest req){
		try {
			req.setAttribute("modelId", modelId);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "mp/billDiscountModelDetailEdit";
	}
	/**
	 * 
	 * 设置折扣页面详情信息
	 * */
	public String billDiscountModelDetailEditPageDetail(HttpServletRequest req){
		String str="";
		try {
			Map<String, Object> m = getMaps(req);
			List<Map<String, Object>> discountModelDetailList = billDiscountModelDetailDao.getBillDiscountModelDetailList(m);
			String name="";
			String providerId="";
			String billType = "";
			Map<String, Object> bill = new HashMap<String, Object>();
			if(discountModelDetailList.size()>0){
				for(int i = 0;i<discountModelDetailList.size();i++){
					Map<String, Object> map = discountModelDetailList.get(i);
					if(map!=null){
						name = (String) map.get("name");
						providerId = (String) map.get("provider_id");
						String bill_pkg_id = (String) map.get("bill_pkg_id");
						Map<String, Object> billPkgById = billPkgDao.getBillPkgById(bill_pkg_id);
						billType = (String) billPkgById.get("bill_type");
						break;
					}
				}
			}
			bill.put("name", name);
			bill.put("providerId",providerId);
			bill.put("billType", billType);
			bill.put("discountMeg", discountModelDetailList);
			str = BaseJson.mapToJson(bill);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return str;
		
	}
	
	/**
	 * 带着模板ID跳转到模板详情添加页
	 * */
	public String billDiscountModelDetailAddPage(String modelId,HttpServletRequest req){
		try {
			
			req.setAttribute("modelId", modelId);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return "mp/billDiscountModelDetailAdd";
	}
	
	/**
	 * 设置模板折扣
	 * */
	public String billDiscountModelDetailAdd(HttpServletRequest req){
		Session session=getSession();
		Map<String, Object> map = getMaps(req);
		String modelId = (String) map.get("modelId");
		try {
			boolean flag=false;
			if(modelId!=null&&modelId!=""){
				List<Map<String, Object>> list = billDiscountModelDetailDao.getBillDiscountModelDetailList(map);
				if(list.size()>0){
					boolean del = billDiscountModelDetailDel(modelId);
					if(del){
						flag = billDIscountModelDetailAdd(map);
					}
				}else{
					flag = billDIscountModelDetailAdd(map);
				}
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag?"设置成功":"设置失败!");
		} catch (Exception e) {
			session.setAttribute(GlobalSetHyfd.backMsg, "设置折扣失败,请稍后重试!");
			getMyLog(e,log);
		}
		return "redirect:billDiscountModelDetailListPage/"+modelId;	
	}
	
	
	
	/**
	 * 获取记录数量
	 * 
	 * @param m
	 * @return
	 */
	public int getBillDiscountModelDetailCount(Map<String, Object> m) {
		int billDiscountModelCount = 0;
		try {
			billDiscountModelCount = billDiscountModelDetailDao.selectCount(m);
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return billDiscountModelCount;
	}
	
	/**
	 * 添加折扣
	 * */
	public boolean billDIscountModelDetailAdd(Map<String,Object> map){
		boolean flag=false;
		try {
			String modelId = (String) map.get("modelId");
			String name = (String) map.get("name");
			String providerId = (String) map.get("provider");
			String billType = (String) map.get("billType");
			if(name!=null&&name!=""&&providerId!=null&&providerId!=""
					&&modelId!=null&&modelId!=""&&billType!=null&&billType!=""){
						map.remove("name");
						map.remove("provider");
						map.remove("modelId");
						map.remove("billType");
						for (Map.Entry<String, Object> entry : map.entrySet()) {
							  String provinceCode = entry.getKey();
							  String discount = (String) entry.getValue();
							  if(discount!=null&&discount!=""){
								  Map<String,Object> m = new HashMap<String, Object>();
								  m.put("name", name);
								  m.put("modelId", modelId);
								  m.put("providerId", providerId);
								  m.put("provinceCode", provinceCode);
								  m.put("billType", billType);
								  m.put("discount", discount);
								  List<Map<String, Object>> billPkgByProId = billPkgDao.getBillPkgByProId(m);
								  	if(billPkgByProId.size()>0){
								  		for(Map<String, Object> billPkg:billPkgByProId){
										  String billPkgId = (String) billPkg.get("id");
										  m.put("billPkgId", billPkgId);
										  billDiscountModelDetailDao.insertSelective(m);//循环添加折扣
								  			}
								  		}else{
								  			System.err.println(provinceCode+"--没有可用流量包");
								  		}
								  }
							  }	
						flag=true;
					}else{
						System.err.println("参数不完整,设置失败");
					}
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return flag;
	}
	
	/**
	 * 删除折扣
	 * */
	public boolean billDiscountModelDetailDel(String modelId){
		boolean flag =false;
		try {
		flag = billDiscountModelDetailDao.billDiscountModelDetailDelByModelId(modelId);//删除模板下的折扣
		} catch (Exception e) {
			getMyLog(e,log);
		}
		return flag;
	}
	
	public String BillDiscountUpload(HttpServletRequest req){
		String result="";
		Map<String, Object> BillDiscountMap=new HashMap<String, Object>();
		Map<String, Object> Map=new HashMap<String, Object>();
		try{
			String resultFileName="";
			String savePath=getMyAppPath(req)+GlobalSetHyfd.savePath;
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd");
			savePath=savePath+"/"+sdf.format(new Date());
			
			createDir(savePath);
			//---------------------------------文件上传开始
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(req.getServletContext());
			if(multipartResolver.isMultipart(req)){
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)req; 
				Iterator<String> iter = multiRequest.getFileNames();
				 while(iter.hasNext()){
					 MultipartFile file = multiRequest.getFile(iter.next());  
		              if(file != null){
		            	  String myFileName = file.getOriginalFilename();
		            	  myFileName=Calendar.getInstance().getTimeInMillis()+myFileName.substring(myFileName.lastIndexOf("."),myFileName.length());
		            	  myFileName=savePath+"/"+myFileName;
		            	  resultFileName=myFileName;
		            	  File myLocalFile=new File(myFileName);
		            	  file.transferTo(myLocalFile);
		              }
				 }
			}
			//------------------------------文件上传结束
			//------------------------------读取excel文件开始
			
				Map<String, List<String[]>> mapBill=ExcelUtil.readExcel(resultFileName);
				Set<Entry<String, List<String[]>>> setBill= mapBill.entrySet();
				for(Map.Entry<String, List<String[]>> entry:setBill){
	//				String sheetName=entry.getKey();
					List<String[]> sheetBill=entry.getValue();
					int rowIndex=0;
					for(String[] col:sheetBill){
						if(rowIndex==0){
							rowIndex++;
							continue; // 不读第一行
						}
						BillDiscountMap.put(col[0], col[1]);
						rowIndex++;
					}
				}
			//------------------------------读取excel文件结束
		if(BillDiscountMap!=null){
			Map.put("bill", BillDiscountMap);
			result = BaseJson.mapToJson(Map);
		}
		}catch(Exception e){
			getMyLog(e,log);
		}
		return result;	
	}
}
