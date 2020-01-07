package com.hyfd.service.mp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.service.BaseService;
import com.hyfd.service.query.AiShiDeQuerySer;
import com.hyfd.service.query.DiXinTongQuerySer;
import com.hyfd.service.query.FenXiangQuerySer;
import com.hyfd.service.query.HaiHangQuerySer;
import com.hyfd.service.query.HuaJiQuerySer;
import com.hyfd.service.query.LanMaoQuerySer;
import com.hyfd.service.query.MingShengQuerySer;
import com.hyfd.service.query.TianYinQuerySer;
import com.hyfd.service.query.YongYouQuerySer;
import com.hyfd.service.query.YuanTeQuerySer;
import com.hyfd.service.query.ZhongYouQuerySer;

@Service
public class PhoneSectionSer extends BaseService {

	public Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private PhoneSectionDao phoneSectionDao;
	@Autowired
	BillPkgDao billPkgDao;
	@Autowired
	ProviderDao providerDao;
	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;

	@Autowired
	HuaJiQuerySer huaJiQuerySer;

	@Autowired
	YuanTeQuerySer yuanTeQuerySer;

	@Autowired
	DiXinTongQuerySer diXinTongQuerySer;

	@Autowired
	HaiHangQuerySer haiHangQuerySer;

	@Autowired
	FenXiangQuerySer fenXiangQuerySer;

	@Autowired
	TianYinQuerySer tianYinQuerySer;

	@Autowired
	AiShiDeQuerySer aiShiDeQuerySer;

	@Autowired
	LanMaoQuerySer lanMaoQuerySer;

	@Autowired
	ZhongYouQuerySer zhongYouQuerySer;
	
	@Autowired
	MingShengQuerySer mingShengQuerySer;
	
	@Autowired
    YongYouQuerySer yongYouQuerySer;
	/**
	 * 根据主键获取记录
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getPhoneSectionById(String id) {
		Map<String, Object> m = new HashMap<String, Object>();
		try {
			m = phoneSectionDao.selectByPrimaryKey(id);
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
	public int getPhoneSectionCount(Map<String, Object> m) {
		int phoneSectionCount = 0;
		try {
			phoneSectionCount = phoneSectionDao.selectCount(m);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return phoneSectionCount;
	}

	/**
	 * 根据条件分页获取号段折扣模板列表数据并生成json
	 * 
	 * @param req
	 * @return
	 */
	public String phoneSectionList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			Page p = getPage(m);// 提取分页参数
			int total = getPhoneSectionCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = phoneSectionDao.selectAll(m);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		} catch (Exception e) {
			getMyLog(e, log);
		}

		return sb.toString();
	}

	/**
	 * 批量手机号有效性校验 解析上传的txt文件
	 */

	public String numberBatchFilter(HttpServletRequest req) {
		String txtStr = null;
		String numberMessage = null;
		StringBuilder sBuilder = new StringBuilder();
		MultipartHttpServletRequest re = (MultipartHttpServletRequest) req;
		MultipartFile fileM = re.getFile("numberFile");
		CommonsMultipartFile cf = (CommonsMultipartFile) fileM;
		try {
			InputStream inputStream = cf.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			while ((txtStr = bufferedReader.readLine()) != null) {
				sBuilder.append(txtStr.trim()).append(",");
			}
			reader.close();
			numberMessage = sBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberMessage;
	}
	
	/**
	 * 批量手机号有效性校验 解析上传的txt文件(余额查询)
	 */
	
	public String numberBatchQueryFilter(HttpServletRequest req) {
		String txtStr = null;
		String numberMessage = null;
		StringBuilder sBuilder = new StringBuilder();
		MultipartHttpServletRequest re = (MultipartHttpServletRequest) req;
		MultipartFile fileM = re.getFile("numberFile");
		CommonsMultipartFile cf = (CommonsMultipartFile) fileM;
		try {
			InputStream inputStream = cf.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			while ((txtStr = bufferedReader.readLine()) != null) {
				sBuilder.append(txtStr.trim());
			}
			reader.close();
			numberMessage = sBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numberMessage;
	}

	
	/**
	 * 批量手机号有效性校验 解析上传的Excel文件
	 */

	public String numberBatchFilterByExcel(HttpServletRequest req) {
		String numberMessage = null;
		StringBuilder sBuilder = new StringBuilder();
		MultipartHttpServletRequest re = (MultipartHttpServletRequest) req;
		MultipartFile fileM = re.getFile("numberFile2");
		try {
			Workbook book = new XSSFWorkbook(fileM.getInputStream());
			Sheet sheet = book.getSheetAt(0);
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				Row row = sheet.getRow(i);
				row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
				String phone = row.getCell(0).getStringCellValue();
				String money = row.getCell(1).getStringCellValue();
				sBuilder.append(phone+"-"+money+"+");
			}
			book.close();
			numberMessage = sBuilder.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return numberMessage;
	}
	/**
	 * 同面值批量手机号有效性校验 解析上传的Excel文件
	 */
	
	public String numberBatchFilterByExcelTwo(HttpServletRequest req) {
		String numberMessage = null;
		StringBuilder sBuilder = new StringBuilder();
		MultipartHttpServletRequest re = (MultipartHttpServletRequest) req;
		MultipartFile fileM = re.getFile("numberFile2");
		try {
			Workbook book = new XSSFWorkbook(fileM.getInputStream());
			Sheet sheet = book.getSheetAt(0);
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				Row row = sheet.getRow(i);
				row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
				String phone = row.getCell(0).getStringCellValue();
				sBuilder.append(phone+",");
			}
			book.close();
			numberMessage = sBuilder.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return numberMessage;
	}
	
	/**
	 * 检测手机号有效性，传入手机号类型：18880888888-10
	 * */
	public Map<String, Object> checkPhoneMessage(String[] mobileMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> phoneMeg = new ArrayList<String>();
		Set<String> faceValue = new HashSet<String>();// 所有充值面值
		for (int i = 0; i < mobileMeg.length; i++) {
			String p = mobileMeg[i];
			String[] num = p.split("-");
			if (num.length > 1) {
				String phone = num[0];
				String value = num[1];
				if (isMobile(phone) || isWLWMobile(phone)) {
					phoneMeg.add(p);
					faceValue.add(value);
				}
			}
		}
		m.put("phoneMessage", phoneMeg);
		m.put("faceValue", faceValue);
		return m;
	}
	
	/**
	 * 检测手机号有效性，只传入手机号  
	 * */
	public Map<String, Object> checkPhoneMessageTwo(String[] mobileMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> phoneMeg = new ArrayList<String>();
		for (int i = 0; i < mobileMeg.length; i++) {
			String phone = mobileMeg[i];
			if (isMobile(phone) || isWLWMobile(phone)) {
				phoneMeg.add(phone);
			}
		}
		m.put("phoneMessage", phoneMeg);
		return m;
	}
	
	
	/**
	 * 批充流量时手机号信息放入各自运营商
	 * */
	public Map<String, Object> distributionByBill(List<String> phoneMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> yidong = new ArrayList<String>();// 移动的号码
		List<String> liantong = new ArrayList<String>();// 联通的号码
		List<String> dianxin = new ArrayList<String>();// 电信的号码
		String yiDongId="0000000001";
		String lianTongId="0000000002";
		String dianXinId="0000000003";
		for (int i = 0; i < phoneMeg.size(); i++) {
			String p = phoneMeg.get(i);
			String[] num = p.split("-");
			String phone = num[0];
			String section = (phone.length() == 13) ? phone.substring(0, 5)
					: phone.substring(0, 7);// 获取号段
			Map<String, Object> sectionMessage = phoneSectionDao
					.selectBySection(section);
			if (sectionMessage != null) {
				String provider_type = (String) sectionMessage.get("provider_type");
				String province_code = (String) sectionMessage.get("province_code");
				if (provider_type.equals("1")){
					p=p+"-"+province_code;
					yidong.add(p);
				}else if(provider_type.equals("2")){
					p=p+"-"+province_code;
					liantong.add(p);
				}else if(provider_type.equals("3")){
					p=p+"-"+province_code;
					dianxin.add(p);
				}else{
					log.error("没有查询到号段，手机号为"+p);
				}
			} else {
				log.error("没有查询到号段，手机号为"+p);
			}
		}
		m.put(yiDongId, yidong);
		m.put(lianTongId, liantong);
		m.put(dianXinId, dianxin);
		return m;
	}

	/**
	 * 逗号分隔的电话号码字符串，分割校验后拼接，过滤无效
	 * 
	 * @param str
	 * @return 过滤后的电话号码字符串，逗号拼接
	 */
	public static String mobileFilter(String mobileContent) {
		StringBuilder mobileB = new StringBuilder();
		String[] mobileArr = mobileContent.split(",");
		for (String mobile : mobileArr) {
			if (isMobile(mobile) || isWLWMobile(mobile)) {
				mobileB.append(mobile);
				mobileB.append(",");
			}
		}
		mobileContent = mobileB.toString();
		if (StringUtils.endsWith(mobileContent, ",")) {
			mobileContent = mobileContent.substring(0,
					mobileContent.length() - 1);
		}
		return mobileContent;
	}

	/**
	 * 手机号验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		// p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
		// p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$");
		// // 验证手机号
		p = Pattern.compile("^((13[0-9])|(15[0-9])|(14[0-9])|(16[0-9])|(17[0-9])|(18[0-9]))\\d{8}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 物联网卡验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isWLWMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		// p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
		// p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$");
		// // 验证手机号
		p = Pattern.compile("^((10648)|(10649))\\d{8}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 是否为正整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber1(String str) {
		Pattern pattern = Pattern.compile("\\d+");
		return pattern.matcher(str).matches();
	}

	/**
	 * 是否为正整数或负整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber2(String str) {
		Pattern pattern = Pattern.compile("-?\\d+");
		return pattern.matcher(str).matches();
	}

	/**
	 * 电话号码验证
	 * 
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // 验证没有区号的
		if (str.length() > 9) {
			m = p1.matcher(str);
			b = m.matches();
		} else {
			m = p2.matcher(str);
			b = m.matches();
		}
		return b;
	}

	/**
	 * @功能描述： 根据条件获取全部号段折扣模板列表数据并生成json
	 *
	 * @作者：zhangpj @创建时间：2016年12月16日
	 * @param req
	 * @return
	 */
	public String phoneSectionAllList(HttpServletRequest req) {
		String str = null;
		try {
			Map<String, Object> m = getMaps(req); // 封装前台参数为map
			List<Map<String, Object>> billList = phoneSectionDao.selectAll(m);
			str = BaseJson.listToJson(billList);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	/**
	 * 添加号段折扣模板信息
	 * 
	 * @param req
	 * @return
	 */
	public String phoneSectionAdd(HttpServletRequest req) {
		Session session = getSession();
		boolean flag = false;
		try {
			Map<String, Object> myBill = getMaps(req);
			Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
			String provider = myBill.get("providerId")+"";
			String providerType = "2";
			if("0000000001".equals(provider) || "0000000002".equals(provider) || "0000000003".equals(provider)){
				providerType = "1";
			}
			myBill.put("providerType", providerType);
			myBill.put("createUser", userInfoMap.get("suName"));// 放入用户
			int rows = phoneSectionDao.insert(myBill);
			if (rows > 0) {
				flag = true;
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "添加成功" : "添加失败");
		} catch (Exception e) {
			session.setAttribute(GlobalSetHyfd.backMsg,
					"小Y不小心感冒了,号段添加失败,请稍后重试!");
			getMyLog(e, log);
		}
		return "redirect:/phoneSectionListPage";
	}

	/**
	 * 修改号段折扣模板信息
	 * 
	 * @param req
	 * @param id
	 * @return
	 */
	public String phoneSectionEdit(HttpServletRequest req, String id) {

		try {
			boolean flag = false;
			Map<String, Object> myBill = getMaps(req);
			Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
			myBill.put("updateUser", userInfoMap.get("suName"));// 放入用户
			int rows = phoneSectionDao.updateByPrimaryKeySelective(myBill);
			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "phoneSectionListPage";
	}

	/**
	 * 跳转到号段折扣模板编辑页
	 * 
	 * @param id
	 * @return
	 */
	public String phoneSectionEditPage(String id) {
		try {
			Map<String, Object> phoneSection = getPhoneSectionById(id);
			Session session = getSession();
			session.setAttribute("phoneSection", phoneSection);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/phoneSectionEdit";
	}

	/**
	 * 带着模板Id跳转到号段折扣模板详情页
	 * 
	 * @param id
	 * @return
	 */
	public String phoneSectionDetail(String id) {
		try {
			Map<String, Object> m = getPhoneSectionById(id);
			Session session = getSession();
			session.setAttribute("phoneSection", m);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "mp/phoneSectionDetail";
	}

	/**
	 * 删除号段折扣模板信息
	 * 
	 * @param id
	 * @return
	 */
	public String phoneSectionDel(String id) {

		try {
			boolean flag = false;
			int rows = phoneSectionDao.deleteByPrimaryKey(id);
			if (rows > 0) {
				flag = true;
			}

			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "删除成功" : "删除失败");

		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "phoneSectionListPage";
	}
	
	public Map<String, String> queryPhoneBalanceCharge(String phone,String providerName){
    	
    	Map<String, String> phoneExtInfo = new HashMap<String, String>();
    	Map<String, String> result = new HashMap<String, String>();
		// 金额单位修复 使用元
		String unKnownName = "未知";
		String myUnit = "元";
		try {
	        
	        if(providerName.contains("话机")){
	        	result = huaJiQuerySer.getChargeAmountInfo(phone);
	        }
//	        else if (providerName.contains("远特")) {
//	        	result = yuanTeQuerySer.getChargeAmountInfo(phone);
//			}
	        else if (providerName.contains("迪信通")) {
				result = diXinTongQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("海航")) {
				result.put("status", "1");
				//result = haiHangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("分享")) {
				result = fenXiangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("天音")) {
				result = tianYinQuerySer.getChargeAmountInfo(phone);
//			}else if (providerName.contains("爱施德")) {
//				result = aiShiDeQuerySer.getChargeAmountInfo(phone);
//				result.put("status", "1");
//				result.put("amount", "0");
			}else if (providerName.contains("蓝猫")) {
				result = lanMaoQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("中邮普泰")) {
				result = zhongYouQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("民生")) {
				result = mingShengQuerySer.getChargeAmountInfo(phone);
			}else if(providerName.contains("用友")){
				result = yongYouQuerySer.getChargeAmountInfo(phone);
			}else {
				result.put("status", "1");
				result.put("amount", "0");
			}
	        
	        if ("0".equals(result.get("status"))) {// 查询成功
				phoneExtInfo.put("amount", result.get("amount") + myUnit);
				phoneExtInfo.put("phoneownername",result.get("phoneownername"));
			} else {
				phoneExtInfo.put("phoneownername", unKnownName);
				if (providerName.contains("分享")) {
					phoneExtInfo.put("amount", result.get("amount"));
				}else{
					phoneExtInfo.put("amount", "获取"+providerName+"余额信息失败");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			phoneExtInfo.put("phoneownername", unKnownName);
			phoneExtInfo.put("amount", "未查询到余额信息");
			log.error(e.getMessage());
		}
        return phoneExtInfo;
        
    }

	
	/**
	 * @功能描述：	验证号段是否已存在
	 *
	 * @param section
	 * @return 
	 *
	 * @作者：zhangpj		@创建时间：2018年4月9日
	 */
	public String phoneCheck(String section) {
		String str = "";
		try {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("section", section);
			
			List<Map<String, Object>> billList = phoneSectionDao.selectAll(m);
			
			if (billList.size() > 0) {
				str = "true";
			} else {
				str = "false";
			}
		} catch (Exception e) {
			getMyLog(e, log);
		}
	return str;
	}
	
	/**
	 * 检测批量查询余额手机号有效性
	 * */
	public Map<String, Object> checkPhone(String[] mobileMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> phoneMeg = new ArrayList<String>();
		for (int i = 0; i < mobileMeg.length; i++) {
			String phone = mobileMeg[i];
			if (isMobile(phone) || isWLWMobile(phone)) {
				phoneMeg.add(phone);
			}
		}
		m.put("phoneMessage", phoneMeg);
		return m;
	}
	
	/**
	 * 批量添加号段
	 * @param file
	 * @param req
	 * @return
	 */
	public String phoneSectionAdd(MultipartFile file, HttpServletRequest req) {
		int flag = -1;
		Map<String, Object> map = getMaps(req);
		Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
		String provider = map.get("providerId")+"";
		String providerType = "2";
		if("0000000001".equals(provider) || "0000000002".equals(provider) || "0000000003".equals(provider)){
			providerType = "1";
		}
		List<Map<String, Object>> sections = new ArrayList<>();
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
		try {
			Workbook book = new XSSFWorkbook(file.getInputStream());
			Sheet sheet = book.getSheetAt(0);
			flag = sheet.getLastRowNum();
			for (int i = 0; i < flag + 1; i++) {
				Row row = sheet.getRow(i);
				//判断获取的数据是否为‘手机号码段’
				Pattern pattern = Pattern.compile("[0-9]*");
		        Matcher isNum = pattern.matcher(row.getCell(0).getStringCellValue());
		        if( !isNum.matches() ){
		        	log.error("批量添加号段时，获取的参数有误！     section："+row.getCell(0).getStringCellValue());
		        	return "-1";
		        }
				
				Map<String, Object> phone = new HashMap<>();
				phone.put("section", row.getCell(0).getStringCellValue());
				phone.put("provinceCode", row.getCell(1).getStringCellValue());
				phone.put("cityCode", row.getCell(2).getStringCellValue());
				phone.put("providerType", providerType);
				phone.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
				phone.put("carrierType", "1");
				phone.put("createUser", userInfoMap.get("suName"));// 放入用户
				phone.put("providerId", map.get("providerId"));
				sections.add(phone);
			}
			int sum = phoneSectionDao.batchinsert(sections);
			System.out.println(sum+"------------------------------充值结果");
			book.close();
		} catch (IOException e) {
			getMyLog(e, log);
		}finally {
			fixedThreadPool.shutdown();
		}
		return flag+"";
	}
	
}
