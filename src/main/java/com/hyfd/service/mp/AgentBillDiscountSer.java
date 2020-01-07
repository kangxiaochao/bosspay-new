package com.hyfd.service.mp;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.OrderAllAgentDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderBillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class AgentBillDiscountSer extends BaseService {

	public Logger log = Logger.getLogger(this.getClass());

	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;

	@Autowired
	AgentDao agentDao;

	@Autowired
	BillPkgDao billPkgDao;

	@Autowired
	ProviderDao providerDao;
	
	@Autowired
	ProviderBillPkgDao pbpDao;
	
	@Autowired
	PhoneSectionDao phoneSectionDao;
	
	@Autowired
	OrderAllAgentDao orderAllAgentDao;

	/**
	 * 根据主键获取记录
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getagentBillDiscountById(String id) {
		Map<String, Object> m = new HashMap<String, Object>();
		try {
			m = agentBillDiscountDao.selectByPrimaryKey(id);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return m;
	}

	/**
	 * 获取记录数量
	 * 
	 * @param m
	 * @return
	 */
	public int getAgentBillDiscountCount(Map<String, Object> m) {
		int agentBillDiscountCount = 0;
		try {
			agentBillDiscountCount = agentBillDiscountDao.selectCount(m);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return agentBillDiscountCount;
	}

	public String getColModel(String agentId,String providerId,HttpServletRequest requset){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("agentId", agentId);
		param.put("providerId", providerId);
		List<String> valueList = agentBillDiscountDao.getColModel(param);
		List<String> colNameList = new ArrayList<String>();
		colNameList.add("地区");
		List<Map<String,Object>> colModelList = new ArrayList<Map<String,Object>>();
		Map<String,Object> modelP = new HashMap<String,Object>();
		modelP.put("name", "provinceCode");
		modelP.put("index", "provinceCode");
		modelP.put("sortable", false);
		modelP.put("align","center");
		colModelList.add(modelP);
		for(int i = 0 ; i < valueList.size() ; i++){
			double value = Double.parseDouble(valueList.get(i));
			BigDecimal bd = new BigDecimal(value);
			value = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			colNameList.add(value+"");
			Map<String,Object> model = new HashMap<String,Object>();
			int index = new Double(value*100).intValue();
			model.put("name", "fee"+index);
			model.put("index", "fee"+index);
//			model.put("width", "180");
			model.put("sortable", false);
			model.put("align","center");
			model.put("editable", true);
			colModelList.add(model);
		}
		JSONObject json = new JSONObject();
		json.put("colNameList", colNameList);
		json.put("colModelList", colModelList);
		return json.toJSONString();
	}
	
	/**
	 * 单元格修改折扣数据
	 * @author lks 2017年12月15日下午2:58:42
	 * @param req
	 * @return
	 */
	public String editCellDiscount(HttpServletRequest req){
		Map<String,Object> map = getMaps(req);
		String feeKey = getFeeKey(map);
		if(!"".equals(feeKey)&&feeKey!=null){
			String fee = feeKey.replaceAll("fee", "");
			double value = Double.parseDouble(fee);
			String pkgId = billPkgDao.selectIdByValue(value/100);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("discount", map.get(feeKey));
			param.put("agentId", map.get("agentId"));
			param.put("providerId", map.get("providerId"));
			param.put("provinceCode", map.get("province"));
			param.put("billPkgId", pkgId);
			int flag = agentBillDiscountDao.updateDiscount(param);
			if(flag == 1){
				return "success";
			}else{
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
		}
		return "fail";
	}
	
	public static String getFeeKey(Map<String,Object> map){
		String feeKey = "";
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if(key.contains("fee")){
				feeKey = key;
			}
		}
		return feeKey;
	}
	
	/**
	 * 根据条件分页获取代理商流量折扣列表数据并生成json
	 * 
	 * @param req
	 * @return
	 */
	public String agentBillDiscountList(String agentId, String providerId, HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			m.put("agentId", agentId);
			m.put("providerId", providerId);
			Page p = getPage(m);// 提取分页参数
			int total = agentBillDiscountDao.getAgentBillDiscountListCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			String sql = agentBillDiscountDao.getAllAgentBillDiscountSql(m);
			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = agentBillDiscountDao.getAllAgentBillDiscountForSql(sql);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}

	public String agentBillDiscountAdd(String agentId, HttpServletRequest req) {
		try {
			req.setAttribute("agentId", agentId);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/agentBillDiscountAdd";
	}

	/**
	 * @功能描述： 根据条件获取全部代理商流量折扣列表数据并生成json
	 *
	 * @作者：HYJ @创建时间：2016年1月2日
	 * @param req
	 * @return
	 */
	public String agentBillDiscountAllList(HttpServletRequest req) {
		String str = null;
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			List<Map<String, Object>> billList = agentBillDiscountDao.selectAll(m);
			str = BaseJson.listToJson(billList);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	/**
	 * 根据流量包、代理商、运营商查询折扣
	 */
	public String agentBillDiscountGet(HttpServletRequest req) {
		String str = "0";// 0代表没有折扣
		try {
			Map<String, Object> m = getMaps(req);
			String price = (String) m.get("price");
			Map<String, Object> agent = agentGetByUserId();
			String agentId = (String) agent.get("id");
			m.put("agentId", agentId);
			Map<String, Object> agentBillDiscount = agentBillDiscountDao.agentBillDiscountGet(m);
			if (agentBillDiscount != null) {
				// 计算折扣后价格
				String discount = agentBillDiscount.get("discount") + "";
				double pri = Double.parseDouble(price);
				double dis = Double.parseDouble(discount);
				BigDecimal b1 = new BigDecimal(pri);
				BigDecimal b2 = new BigDecimal(dis);
				Double disprice = new Double(b1.multiply(b2).doubleValue());
				NumberFormat nf = NumberFormat.getNumberInstance();
				// 保留两位小数
				nf.setMaximumFractionDigits(2);
				str = nf.format(disprice);
			}

		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}
	
	/**
	 * @功能描述：保存指定代理商所属的所有上级代理商订单信息
	 * 
	 *
	 * @作者：zhangpj		@创建时间：2018年3月26日
	 */
	public void addAllParentAgentOrderinfo(Map<String,Object> orderMap){
		try {
			String agentId = orderMap.get("agentId").toString();
			// 1.获取代理商父级id
			Map<String, Object> agentMap = agentDao.selectById(agentId);
			String[] parentAgentId = agentMap.get("parent_path").toString().split(">");
			
			// 2.根据号段获取归属地
			String phoneNo = orderMap.get("phone").toString().substring(0, 7);
			Map<String, Object> sectionMap = phoneSectionDao.selectBySection(phoneNo);
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("parentAgentId", parentAgentId);
			param.put("providerId", orderMap.get("providerId"));
			param.put("provinceCode", sectionMap.get("province_code"));
			param.put("billPkgId", orderMap.get("pkgId"));
			// 3.获取指定代理商的所有上级代理商满足条件的折扣信息
			List<Map<String, Object>> agentBillDiscountList = agentBillDiscountDao.getAllParentAgentBillDiscount(param);
			if (agentBillDiscountList.size() > 0) {
				orderAllAgentDao.deleteByOrderId(orderMap.get("agentOrderId").toString());
				int listSize = agentBillDiscountList.size();
				int count = 0;
				Map<String,Object> agentOrderMap;
				for (int i = 0; i < listSize; i++) {
					Map<String, Object> agentBillDiscountMap = agentBillDiscountList.get(i);
					agentOrderMap = new HashMap<String, Object>();
					agentOrderMap.putAll(orderMap);
					
					agentOrderMap.put("id", UUID.randomUUID().toString().replace("-", ""));
					agentOrderMap.put("agentId", agentBillDiscountMap.get("agent_id"));
					agentOrderMap.put("agentDiscountId", agentBillDiscountMap.get("id"));
					agentOrderMap.put("agentDiscount", agentBillDiscountMap.get("discount"));
					orderAllAgentDao.insert(agentOrderMap);
					count++;
				}
				
				if (listSize > count) {
					log.error("保存订单[" + orderMap.get("orderId").toString() + "]的所有父级代理商订单发生异常");
				}
			}
		} catch (Exception e) {
			log.error("保存订单[" + orderMap.get("orderId").toString() + "]的指定代理商所属的所有上级代理商订单信息发生异常");
		}
	}

	/**
	 * 添加代理商流量折扣信息
	 * 
	 * @param req
	 * @return
	 */
	public String agentBillDiscountAdd(HttpServletRequest req) {
		Session session = getSession();
		Map<String, Object> map = getMaps(req);
		String agentId = (String) map.get("agentId");
		boolean flag = false;
		try {
			String name = (String) map.get("name");
			String providerId = (String) map.get("providerId");
			String billType = (String) map.get("billType");
			if (providerId != null && providerId != "" && agentId != null && agentId != "" && billType != null
					&& billType != "") {
				map.remove("name");
				List<Map<String, Object>> allList = agentBillDiscountDao.selectAll(map);
				if (allList.size() > 0) {
					int i = agentBillDiscountDao.delByAgentAndProvider(map);
					map.remove("billType");
					map.remove("providerId");
					map.remove("agentId");
					flag = agentBillDiscount(name, agentId, providerId, billType, map);
				} else {
					map.remove("billType");
					map.remove("providerId");
					map.remove("agentId");
					flag = agentBillDiscount(name, agentId, providerId, billType, map);
				}
			} else {
				session.setAttribute(GlobalSetHyfd.backMsg, "参数不完整,添加失败");
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败!");
		} catch (Exception e) {
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败!");
			getMyLog(e, log);
		}
		return "redirect:agentBillDiscountListPage/" + agentId;
	}

	public boolean agentBillDiscount(String name, String agentId, String providerId, String billType,
			Map<String, Object> map) {
		boolean flag = false;
		try {
			Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String provinceCode = entry.getKey();
				String discount = (String) entry.getValue();
				if (discount != null && discount != "") {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("name", name);
					m.put("agentId", agentId);
					m.put("providerId", providerId);
					m.put("provinceCode", provinceCode);
					m.put("billType", billType);
					m.put("discount", discount);
					List<String> billPkgByProId = billPkgDao.getPkgId(m);
					if (billPkgByProId.size() > 0) {
						for (String billPkgId : billPkgByProId) {
							m.put("createUser", userInfoMap.get("suName"));// 放入用户
							m.put("billPkgId", billPkgId);
							int i = agentBillDiscountDao.insertSelective(m);
							if (i > 0) {
								flag = true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			flag = false;
			getMyLog(e, log);
		}

		return flag;
	}

	// /**
	// * 修改代理商流量折扣信息
	// * @param req
	// * @param id
	// * @return
	// */
	// public String agentBillDiscountEdit(HttpServletRequest req){
	// String agentId =null;
	// boolean flag=false;
	// Session session=getSession();
	// try{
	// Map<String, Object> myBill=getMaps(req);
	// agentId = (String) myBill.get("agentId");
	// Map<String, Object> userInfoMap=getUser(); //取到当前用户信息
	// myBill.put("updateUser", userInfoMap.get("suName"));//放入用户
	// int rows=agentBillDiscountDao.updateByPrimaryKeySelective(myBill);
	// if(rows>0){
	// flag=true;
	// }
	// session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
	// }catch(Exception e){
	// session.setAttribute(GlobalSetHyfd.backMsg, flag?"修改成功":"修改失败");
	// getMyLog(e,log);
	// }
	// return "agentBillDiscountListPage/"+agentId;
	// }
	//
	/**
	 * 修改代理商流量折扣
	 * 
	 * @param req
	 * @param id
	 * @return
	 */
	public String agentBillDiscountUpdate(HttpServletRequest req) {
		String flag = "";
		try {
			Map<String, Object> myBill = getMaps(req);
			Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
			String agentId = (String) myBill.get("agentId");
			String providerId = (String) myBill.get("providerId");
			String provinceCode = (String) myBill.get("provinceCode");
			String discountMeg = (String) myBill.get("discountMeg");
			if (agentId != null && agentId != "" && providerId != null && providerId != "" && provinceCode != null
					&& provinceCode != "" && discountMeg != null && discountMeg != "") {
				String[] bill = discountMeg.split(",");
				for (String billMeg : bill) {
					Map<String, Object> param = new HashMap<String, Object>();
					String[] split = billMeg.split("-");
					String billPkgId = split[0];
					String discount = "";
					if (split.length > 1) {
						discount = split[1];
					}
					param.put("agentId", agentId);
					param.put("providerId", providerId);
					param.put("provinceCode", provinceCode);
					param.put("billPkgId", billPkgId);
					// 根据代理商ID运营商ID省份和包查询是否有折扣
					Map<String, Object> agentDiscount = agentBillDiscountDao.agentBillDiscountGet(param);
					if (agentDiscount != null) {// 有折扣的操作
						if (discount != "") {// 如果前台传来的折扣不为空就修改
							param.put("discount", discount);
							param.put("updateUser", userInfoMap.get("suName"));// 放入修改用户
							int i = agentBillDiscountDao.updateDiscount(param);
							if (i > 0) {
								flag = "更新成功";
							} else {
								flag = "更新失败";
								break;
							}
						} else {// 前台传来折扣为空，则删除
							int i = agentBillDiscountDao.deleteDiscount(param);
							if (i > 0) {
								flag = "更新成功";
							} else {
								flag = "更新失败";
								break;
							}
						}
					} else {// 没有折扣
						if (discount != "") {// 如果折扣不为空就添加
							param.put("discount", discount);
							param.put("createUser", userInfoMap.get("suName"));// 放入添加用户
							int i = agentBillDiscountDao.insertSelective(param);
							if (i > 0) {
								flag = "更新成功";
							} else {
								flag = "更新失败";
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return flag;
	}

	/**
	 * 带着代理商Id跳转到代理商流量折扣页
	 * 
	 * @param id
	 * @return
	 */
	public String agentBillDiscountListPage(String id, HttpServletRequest req) {
		try {
			req.setAttribute("agentId", id);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/agentBillDiscountList";
	}

	/**
	 * 跳转到代理商流量折扣编辑页
	 * 
	 * @param id
	 * @return
	 */
	public String agentBillDiscountPage(String id, HttpServletRequest req) {
		try {
			String[] ids = req.getParameterValues("ids");
			req.setAttribute("agentId", id);
			req.setAttribute("ids", ids);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "agentBillDiscountEditPage";
	}

	public String agentBillDiscountEditPage(HttpServletRequest req) {
		return "mp/agentBillDiscountEdit";
	}

	/**
	 * 带着agentId跳转到代理商话费折扣详情页
	 * 
	 * @param id
	 * @return
	 */
	public String agentBillDiscountDetailPage(String id, HttpServletRequest req) {
		try {
			Map<String, Object> agent = agentDao.selectById(id);
			req.setAttribute("agent", agent);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/agentBillDiscountDetail";
	}

	/**
	 * 详情页数据
	 */
	public String agentBillDiscountDetail(String agentId, HttpServletRequest req) {
		String str = "";
		try {
			Map<String, Object> map = getMaps(req);
			String providerId = (String) map.get("providerId");
			String province = (String) map.get("provinceCode");
			String billType = (String) map.get("billType");
			if (providerId != null && providerId != "" && province != null && province != "" && billType != null
					&& billType != "") {
				map.put("agentId", agentId);
				List<Map<String, Object>> selectListByBillType = agentBillDiscountDao.selectByBillType(map);
				if (selectListByBillType.size() > 0) {
					str = BaseJson.listToJson(selectListByBillType);
				}
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	/**
	 * 查询该代理商所在通道的流量包
	 */
	public String agentBillDiscountPkgList(String agentId, HttpServletRequest req) {
		String str = "false";
		try {
			Map<String, Object> map = getMaps(req);
			map.put("agentId", agentId);
			List<Map<String, Object>> billPkgList = billPkgDao.getBillPkgByProId(map);
			if (billPkgList.size() > 0) {
				str = BaseJson.listToJson(billPkgList);
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	// /**
	// * 删除代理商流量折扣信息
	// * @param id
	// * @return
	// */
	// public String agentBillDiscountDel(String agentId,HttpServletRequest
	// req){
	// try{
	// boolean flag=false;
	// String[] ids = req.getParameterValues("ids");
	// for(String id:ids){
	// int rows=agentBillDiscountDao.deleteByPrimaryKey(id);
	// if(rows>0){
	// flag=true;
	// }else{
	// flag=false;
	// break;
	// }
	// }
	// Session session=getSession();
	// session.setAttribute(GlobalSetHyfd.backMsg, flag?"删除成功":"删除失败");
	//
	// }catch(Exception e){
	// getMyLog(e,log);
	// }
	// return "agentBillDiscountListPage/"+agentId;
	// }

	public String queryAgentDiscountBySuId(String suId, HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> agentMap = agentDao.selectByUserId(suId);
			Map<String, Object> m = getMaps(req);
			m.put("agentId", agentMap.get("id"));
			Page p = getPage(m);// 提取分页参数
			int total = agentBillDiscountDao.selectCountByAgentId(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = agentBillDiscountDao.getAgentBillDiscountPkgListBySuId(m);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}

	public List<String> commitAgentBillDiscount(String agentId, MultipartFile mFile) throws Exception {
		InputStream is = mFile.getInputStream();
		List<String> errorList = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		try{
			Workbook workbook = new XSSFWorkbook(is);
			for(int i = 0 ; i < workbook.getNumberOfSheets(); i++){
				Sheet sheet = workbook.getSheetAt(i);
				String sheetName = sheet.getSheetName();
				String providerId = providerDao.getIdByName(sheetName);
				if("".equals(providerId) || providerId == null){
					errorList.add(sheetName + "名字不匹配！");
				}else{
					map.put("agentId", agentId);
					map.put("providerId", providerId);
					agentBillDiscountDao.discountDel(map);//删除指定代理商的运营商所有的折扣信息
					Row fristRow = sheet.getRow(0);
					List<String> moneyList = getListForFristRow(fristRow);
					for(int x = 1 ; x < sheet.getPhysicalNumberOfRows() ; x++){
						Row row = sheet.getRow(x);
						if(row.getCell(0) != null){
							String provinceCode = row.getCell(0).getStringCellValue();//省份
							if(!"".equals(provinceCode) || provinceCode != null){
								for(int m = 0 ; m < moneyList.size() ; m++){
									String discount = row.getCell(m+1).getStringCellValue();
									if(!"".equals(discount) || discount != null){
										Map<String,Object> discountMap = new HashMap<String,Object>();
										String billPkgId = billPkgDao.getBillPkgByPrice(moneyList.get(m));
										if(!"".equals(billPkgId) || billPkgId != null){
											discountMap.put("provinceCode", provinceCode);
											discountMap.put("providerId", providerId);
											discountMap.put("agentId", agentId);
											discountMap.put("discount", discount);
											discountMap.put("billPkgId", billPkgId);
											agentBillDiscountDao.insertSelective(discountMap);
										}
									}
								}
							}
						}
					}
				}
			}
			workbook.close();
		}catch(Exception e){
			e.printStackTrace();
			errorList.add("上传表格格式不匹配！");
		}
		return errorList;
	}
	
	/**
	 * 获取第一行的金额值
	 * @author lks 2017年12月12日下午2:23:31
	 * @param row
	 * @return
	 */
	public List<String> getListForFristRow(Row row){
		List<String> list = new ArrayList<String>();
		for(int i = 1; i < row.getPhysicalNumberOfCells(); i++){
			String money = row.getCell(i).getStringCellValue().replaceAll("元", "");
			list.add(money);
		}
		return list;
	}

	/**
	 * 设置虚商折扣
	 * @author lks 2017年12月18日上午10:08:18
	 * @param request
	 * @return
	 */
	public String saveVpd(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		boolean flag = false;
		String msg = "提交失败";
		int sum = 0;
		Map<String, Object> userInfoMap = getUser();
        String userid = userInfoMap.get("suId") + "";
		Map<String,Object> param = getMaps(request);
//		List<String> pkgIdList = pbpDao.selectPkgIdByProviderId(param);
//		if (pkgIdList.size() > 0) {
//			agentBillDiscountDao.updateDiscountDel(param);
//			for(String pkgId : pkgIdList){
//				Map<String,Object> discountMap = new HashMap<String,Object>();
//				discountMap.put("provinceCode", "全国");
//				discountMap.put("providerId", param.get("providerId"));
//				discountMap.put("agentId", param.get("agentId"));
//				discountMap.put("discount", param.get("discount"));
//				discountMap.put("billPkgId", pkgId);
//				sum += agentBillDiscountDao.insertSelective(discountMap);
//			}
//			if(sum == pkgIdList.size()){
//				flag = true;
//				msg = "提交成功";
//			}else{
//				msg = "代理商话费折扣设置的数量与话费包折扣的数量不一致,请稍后重试或联系技术人员查看";
//				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//			}
//		} else {
//			msg = "提交失败,请先设置运营商话费包再进行此操作";
//		}
		String discountInfoStr= param.get("discountInfoAarry").toString();
		if ("".equals(discountInfoStr)) {
			// 删除指定代理商的指定运营商的折扣信息
			agentBillDiscountDao.discountDel(param);
			flag = true;
			msg = "折扣[清除]成功";
		}else{
			// 删除指定代理商的指定运营商的折扣信息
			agentBillDiscountDao.discountDel(param);
			String[] discountInfoAarry = discountInfoStr.split(",");
			int discountInfoAarryLength = discountInfoAarry.length;
			String[] discountInfo = null;
			for (int i = 0; i < discountInfoAarryLength; i++) {
				discountInfo = discountInfoAarry[i].split("-");
				Map<String,Object> discountMap = new HashMap<String,Object>();
				discountMap.put("provinceCode", discountInfo[0]);
				discountMap.put("providerId", param.get("providerId"));
				discountMap.put("agentId", param.get("agentId"));
				discountMap.put("discount", discountInfo[2]);
				discountMap.put("billPkgId", discountInfo[1]);
				discountMap.put("createUser", userid);
				sum += agentBillDiscountDao.insertSelective(discountMap);
			}
			if(sum == discountInfoAarryLength){
				flag = true;
				msg = "折扣[设置]成功";
			}else{
				msg = "折扣设置成功数量与提交数量不一致,请稍后重试或联系技术人员查看";
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
		}
		
		jsonObject.put("state", flag);
		jsonObject.put("msg", msg);

		return jsonObject.toJSONString();
	}

	/**
	 * 查询虚商折扣
	 * @author lks 2017年12月18日上午11:32:48
	 * @param request
	 * @return
	 */
	public String selectVpd(HttpServletRequest request) {
		Map<String,Object> param = getMaps(request);
		double discount = agentBillDiscountDao.selectVpd(param);
		NumberFormat nf = NumberFormat.getNumberInstance();
		// 保留两位小数
		nf.setMaximumFractionDigits(4);
		return nf.format(discount);
	}
	
	/**
	 * @功能描述：	复制代理商折扣
	 *
	 * @param request
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年3月1日
	 */
	public String agentBillDiscountCopy(HttpServletRequest request){
		boolean flag =false;
		String msg = "";
		Session session = getSession();
		
		Map<String,Object> param = getMaps(request);
		String agentId = param.get("agentId").toString();
		String agentIdSource = param.get("agentIdSource").toString();
		
		Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
        String createUser = userInfoMap.get("suId").toString();// 放入创建用户
        
        try {
			if (null != agentId && !agentId.equals("") && null != agentIdSource && !agentIdSource.equals("")) {
				List<Map<String, Object>> discountList = agentBillDiscountDao.selectAgentBillDiscountListByAgentid(agentIdSource);
				Map<String, Object> map = null;
				Map<String,Object> discountMap = null;
				
				// 删除指定代理商的所有折扣信息
				agentBillDiscountDao.discountDelByAgentId(param);
				
				int listSize = discountList.size();
				int countSum = 0;
				int resultSum = 0;
				for (int i = 0; i < listSize; i++) {
					map = discountList.get(i);					
					discountMap = new HashMap<String,Object>();
					discountMap.put("provinceCode",  map.get("province_code"));
					discountMap.put("providerId",map.get("provider_id"));
					discountMap.put("agentId", agentId);
					discountMap.put("discount", map.get("discount"));
					discountMap.put("billPkgId", map.get("bill_pkg_id"));
					discountMap.put("createUser", createUser);
					
					resultSum = agentBillDiscountDao.insertSelective(discountMap);
					if (resultSum > 0) {
						countSum++;
					}
				}
				if (listSize == countSum) {
					flag = true;
					msg = "折扣复制成功";
					session.setAttribute(GlobalSetHyfd.backMsg, msg);
				} else {
					msg = "折扣复制成功数量与折扣来源数量不一致,请重新操作";
					session.setAttribute(GlobalSetHyfd.backMsg, msg);
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				}
			}
        } catch (Exception e) {
        	flag = false;
        	msg = "折扣复制发生异常,请重新操作";
        	session.setAttribute(GlobalSetHyfd.backMsg, msg);
        	getMyLog(e, log);
		}
		
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("state", flag);
        jsonObj.put("msg", msg);
        
		return jsonObj.toJSONString();
	}
}
