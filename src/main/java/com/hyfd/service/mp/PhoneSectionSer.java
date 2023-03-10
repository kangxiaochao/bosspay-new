package com.hyfd.service.mp;

import java.io.*;
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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
	 * ????????????????????????
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
	 * ??????????????????
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
	 * ???????????????????????????????????????????????????????????????json
	 * 
	 * @param req
	 * @return
	 */
	public String phoneSectionList(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			Map<String, Object> m = getMaps(req); // ?????????????????????map
			Page p = getPage(m);// ??????????????????
			int total = getPhoneSectionCount(m);
			p.setCount(total);
			int pageNum = p.getCurrentPage();
			int pageSize = p.getPageSize();

			sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			PageHelper.startPage(pageNum, pageSize);// mybatis????????????
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
	 * ?????????????????????????????? ???????????????txt??????
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
	 * ?????????????????????????????? ???????????????txt??????(????????????)
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
	 * ?????????????????????????????? ???????????????Excel??????
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
	 * ??????????????????????????????????????? ???????????????Excel??????
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
	 * ???????????????????????????????????????????????????18880888888-10
	 * */
	public Map<String, Object> checkPhoneMessage(String[] mobileMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> phoneMeg = new ArrayList<String>();
		Set<String> faceValue = new HashSet<String>();// ??????????????????
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
	 * ?????????????????????????????????????????????  
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
	 * ???????????????????????????????????????????????????
	 * */
	public Map<String, Object> distributionByBill(List<String> phoneMeg) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<String> yidong = new ArrayList<String>();// ???????????????
		List<String> liantong = new ArrayList<String>();// ???????????????
		List<String> dianxin = new ArrayList<String>();// ???????????????
		String yiDongId="0000000001";
		String lianTongId="0000000002";
		String dianXinId="0000000003";
		for (int i = 0; i < phoneMeg.size(); i++) {
			String p = phoneMeg.get(i);
			String[] num = p.split("-");
			String phone = num[0];
			String section = (phone.length() == 13) ? phone.substring(0, 5)
					: phone.substring(0, 7);// ????????????
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
					log.error("????????????????????????????????????"+p);
				}
			} else {
				log.error("????????????????????????????????????"+p);
			}
		}
		m.put(yiDongId, yidong);
		m.put(lianTongId, liantong);
		m.put(dianXinId, dianxin);
		return m;
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????
	 * 
	 * @param
	 * @return ????????????????????????????????????????????????
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
	 * ???????????????
	 * 
	 * @param str
	 * @return ??????????????????true
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		// p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // ???????????????
		// p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$");
		// // ???????????????
//		p = Pattern.compile("^((13[0-9])|(15[0-9])|(14[0-9])|(16[0-9])|(17[0-9])|(18[0-9]))\\d{8}$"); // ???????????????
		p = Pattern.compile("^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$"); // ???????????????
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * ??????????????????
	 * 
	 * @param str
	 * @return ??????????????????true
	 */
	public static boolean isWLWMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		// p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // ???????????????
		// p =
		// Pattern.compile("^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(18[0,0-9]))\\d{8}$");
		// // ???????????????
		p = Pattern.compile("^((10648)|(10649))\\d{8}$"); // ???????????????
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * ??????????????????
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber1(String str) {
		Pattern pattern = Pattern.compile("\\d+");
		return pattern.matcher(str).matches();
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber2(String str) {
		Pattern pattern = Pattern.compile("-?\\d+");
		return pattern.matcher(str).matches();
	}

	/**
	 * ??????????????????
	 * 
	 * @param str
	 * @return ??????????????????true
	 */
	public static boolean isPhone(String str) {
		Pattern p1 = null, p2 = null;
		Matcher m = null;
		boolean b = false;
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // ??????????????????
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$"); // ?????????????????????
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
	 * @??????????????? ???????????????????????????????????????????????????????????????json
	 *
	 * @?????????zhangpj @???????????????2016???12???16???
	 * @param req
	 * @return
	 */
	public String phoneSectionAllList(HttpServletRequest req) {
		String str = null;
		try {
			Map<String, Object> m = getMaps(req); // ?????????????????????map
			List<Map<String, Object>> billList = phoneSectionDao.selectAll(m);
			str = BaseJson.listToJson(billList);
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return str;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param req
	 * @return
	 */
	public String phoneSectionAdd(HttpServletRequest req) {
		Session session = getSession();
		boolean flag = false;
		try {
			Map<String, Object> myBill = getMaps(req);
			Map<String, Object> userInfoMap = getUser(); // ????????????????????????
			String provider = myBill.get("providerId")+"";
			String providerType = "2";
			if("0000000001".equals(provider) || "0000000002".equals(provider) || "0000000003".equals(provider)){
				providerType = "1";
			}
			myBill.put("providerType", providerType);
			myBill.put("createUser", userInfoMap.get("suName"));// ????????????
			int rows = phoneSectionDao.insert(myBill);
			if (rows > 0) {
				flag = true;
			}
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "????????????" : "????????????");
		} catch (Exception e) {
			session.setAttribute(GlobalSetHyfd.backMsg,
					"???Y??????????????????,??????????????????,???????????????!");
			getMyLog(e, log);
		}
		return "redirect:/phoneSectionListPage";
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param req
	 * @param id
	 * @return
	 */
	public String phoneSectionEdit(HttpServletRequest req, String id) {

		try {
			boolean flag = false;
			Map<String, Object> myBill = getMaps(req);
			Map<String, Object> userInfoMap = getUser(); // ????????????????????????
			myBill.put("updateUser", userInfoMap.get("suName"));// ????????????
			int rows = phoneSectionDao.updateByPrimaryKeySelective(myBill);
			if (rows > 0) {
				flag = true;
			}
			Session session = getSession();
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "????????????" : "????????????");
		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "phoneSectionListPage";
	}

	/**
	 * ????????????????????????????????????
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
	 * ????????????Id????????????????????????????????????
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
	 * ??????????????????????????????
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
			session.setAttribute(GlobalSetHyfd.backMsg, flag ? "????????????" : "????????????");

		} catch (Exception e) {
			getMyLog(e, log);
		}
		return "phoneSectionListPage";
	}
	
	public Map<String, String> queryPhoneBalanceCharge(String phone,String providerName){
    	
    	Map<String, String> phoneExtInfo = new HashMap<String, String>();
    	Map<String, String> result = new HashMap<String, String>();
		// ?????????????????? ?????????
		String unKnownName = "??????";
		String myUnit = "???";
		try {
	        
	        if(providerName.contains("??????")){
	        	result = huaJiQuerySer.getChargeAmountInfo(phone);
	        }
//	        else if (providerName.contains("??????")) {
//	        	result = yuanTeQuerySer.getChargeAmountInfo(phone);
//			}
	        else if (providerName.contains("?????????")) {
				result = diXinTongQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("??????")) {
				result.put("status", "1");
				//result = haiHangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("??????")) {
				result = fenXiangQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("??????")) {
				result = tianYinQuerySer.getChargeAmountInfo(phone);
//			}else if (providerName.contains("?????????")) {
//				result = aiShiDeQuerySer.getChargeAmountInfo(phone);
//				result.put("status", "1");
//				result.put("amount", "0");
			}else if (providerName.contains("??????")) {
				result = lanMaoQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("????????????")) {
				result = zhongYouQuerySer.getChargeAmountInfo(phone);
			}else if (providerName.contains("??????")) {
				result = mingShengQuerySer.getChargeAmountInfo(phone);
			}else if(providerName.contains("??????")){
				result = yongYouQuerySer.getChargeAmountInfo(phone);
			}else {
				result.put("status", "1");
				result.put("amount", "0");
			}
	        
	        if ("0".equals(result.get("status"))) {// ????????????
				phoneExtInfo.put("amount", result.get("amount") + myUnit);
				phoneExtInfo.put("phoneownername",result.get("phoneownername"));
			} else {
				phoneExtInfo.put("phoneownername", unKnownName);
				if (providerName.contains("??????")) {
					phoneExtInfo.put("amount", result.get("amount"));
				}else{
					phoneExtInfo.put("amount", "??????"+providerName+"??????????????????");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			phoneExtInfo.put("phoneownername", unKnownName);
			phoneExtInfo.put("amount", "????????????????????????");
			log.error(e.getMessage());
		}
        return phoneExtInfo;
        
    }

	
	/**
	 * @???????????????	???????????????????????????
	 *
	 * @param section
	 * @return 
	 *
	 * @?????????zhangpj		@???????????????2018???4???9???
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
	 * ??????????????????????????????????????????
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
	 * ??????????????????
	 * @param file
	 * @param req
	 * @return
	 */
	public String phoneSectionAdd(MultipartFile file, HttpServletRequest req) {
		int flag = -1;
		int sum = 0;
		Map<String, Object> map = getMaps(req);
		Map<String, Object> userInfoMap = getUser(); // ????????????????????????
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
			for (int i = 1; i < flag + 1; i++) {
				Row row = sheet.getRow(i);
				//???????????????????????????????????????????????????
				Pattern pattern = Pattern.compile("[0-9]*");
				row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
		        Matcher isNum = pattern.matcher(row.getCell(0).getStringCellValue());
		        if( !isNum.matches() ){
		        	log.error("????????????????????????????????????????????????     section???"+row.getCell(0).getStringCellValue());
		        	return "-1";
		        }
				
				Map<String, Object> phone = new HashMap<>();
				phone.put("section", row.getCell(0).getStringCellValue());
				phone.put("provinceCode", row.getCell(1).getStringCellValue());
				phone.put("cityCode", row.getCell(2).getStringCellValue());
				phone.put("providerType", providerType);
				phone.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
				phone.put("carrierType", "1");
				phone.put("createUser", userInfoMap.get("suName"));// ????????????
				phone.put("providerId", map.get("providerId"));
				sections.add(phone);
			}
			sum = phoneSectionDao.batchinsert(sections);
			System.out.println(sum+"------------------------------????????????");
			book.close();
		} catch (IOException e) {
			getMyLog(e, log);
		}finally {
			fixedThreadPool.shutdown();
		}
		return sum+"";
	}

	public void exportphonePage(HttpServletRequest req, HttpServletResponse res) {
		Map<String, Object> m = getMaps(req);

		List<Map<String, Object>> exce = phoneSectionDao.selectAll(m);

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("????????????");
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("??????");
		cell = row.createCell((short) 1);
		cell.setCellValue("???????????????");
		cell = row.createCell((short) 2);
		cell.setCellValue("???");
		cell = row.createCell((short) 3);
		cell.setCellValue("???");

		//a.id, a.section, a.provider_id, a.province_code, a.city_code, a.update_date, a.update_user, a.create_date,
		//    a.create_user, a.provider_type, a.carrier_type, b.name as provider_name

		for(int i=0;i<exce.size();i++) {
			row = sheet.createRow((int) i + 1);
			Map<String, Object> map = exce.get(i);
			row.createCell(0).setCellValue(map.get("section").toString()!=null ? map.get("section").toString() : "???????????????");
			row.createCell(1).setCellValue(map.get("provider_name").toString()!=null ? map.get("provider_name").toString() : "????????????????????????");
			row.createCell(2).setCellValue(map.get("province_code").toString() !=null ? map.get("province_code").toString() : "????????????");
			row.createCell(3).setCellValue(map.get("city_code").toString() !=null ? map.get("city_code").toString() : "????????????");
		}

		try {
			res.setContentType("application/x-excel;charset=utf-8");
			res.setHeader("Content-Disposition",
					"attachment;filename=exception.xlsx");
			res.setCharacterEncoding("utf-8");
			OutputStream os = res.getOutputStream();
			wb.write(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
