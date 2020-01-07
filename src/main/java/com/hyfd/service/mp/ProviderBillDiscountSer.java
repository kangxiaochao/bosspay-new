package com.hyfd.service.mp;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.ExcelUtil;
import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.Page;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ProviderBillDiscountDao;
import com.hyfd.dao.mp.ProviderBillPkgDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ProviderBillDiscountSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    BillPkgDao billPkgDao;
    
    @Autowired
    ProviderBillDiscountDao providerBillDiscountDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderDao providerDao;
    
    @Autowired
	ProviderBillPkgDao pbpDao;
    
    public String providerBillDiscountEditPage(HttpServletRequest req, String id)
    {
        try
        {
            Map<String, Object> m = providerPhysicalChannelDao.getProviderPhysicalChannelById(id);
            req.setAttribute("providerPhysicalChannel", m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerBillDiscountEdit";
    }
    
    public String getProviderBillDiscountByBillPkgId(String bill_pkg_id)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            List<Map<String, Object>> billList =
                providerBillDiscountDao.getProviderBillDiscountByBillPkgId(bill_pkg_id);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
        
    }
    
    public String getProviderBillDiscountByBillPkgIdAndProvinceCode(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> reqParMap = getMaps(req);
            List<Map<String, Object>> billList =
                providerBillDiscountDao.getProviderBillDiscountByBillPkgIdAndProvinceCode(reqParMap);
            String billListJson = BaseJson.listToJson(billList);
            sb.append(billListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
    }
    
    public String providerBillDiscountAdd(HttpServletRequest req)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("create_user", userInfoMap.get("suId"));// 放入创建用户
            
            int rows = providerBillDiscountDao.providerBillDiscountAdd(myBill);
            if (rows > 0)
            {
                flag = true;
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return flag + "";
    }
    
    public String providerBillDiscountEdit(HttpServletRequest req, String id)
    {
        boolean flag = false;
        try
        {
            Map<String, Object> myBill = getMaps(req);
            
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myBill.put("update_user", userInfoMap.get("suId"));// 放入创建用户
            int rows = providerBillDiscountDao.providerBillDiscountEdit(myBill);
            if (rows > 0)
            {
                flag = true;
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return flag + "";
    }
    
    public String providerBillDiscountUpload(HttpServletRequest req)
    {
        String result = "";
        try
        {
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            String provider_id = req.getParameter("provider_id");
            String bill_pkg_id = req.getParameter("bill_pkg_id");
            String discountType = req.getParameter("discountType");
            
            String resultFileName = "";
            String savePath = getMyAppPath(req) + GlobalSetHyfd.savePath;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
            savePath = savePath + "/" + sdf.format(new Date());
            
            createDir(savePath);
            // ---------------------------------文件上传开始
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(req.getServletContext());
            if (multipartResolver.isMultipart(req))
            {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)req;
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext())
                {
                    MultipartFile file = multiRequest.getFile(iter.next());
                    if (file != null)
                    {
                        String myFileName = file.getOriginalFilename();
                        myFileName =
                            Calendar.getInstance().getTimeInMillis()
                                + myFileName.substring(myFileName.lastIndexOf("."), myFileName.length());
                        myFileName = savePath + "/" + myFileName;
                        resultFileName = myFileName;
                        File myLocalFile = new File(myFileName);
                        file.transferTo(myLocalFile);
                    }
                }
            }
            // ------------------------------文件上传结束
            // 删除所有折扣数据
            providerBillDiscountDao.deleteProviderBillDiscountByBillPkgId(bill_pkg_id);
            // ------------------------------读取excel文件开始
            Map<String, List<String[]>> mapBill = ExcelUtil.readExcel(resultFileName);
            Set<Entry<String, List<String[]>>> setBill = mapBill.entrySet();
            for (Map.Entry<String, List<String[]>> entry : setBill)
            {
                // String sheetName=entry.getKey();
                List<String[]> sheetData = entry.getValue();
                int rowIndex = 0;
                for (String[] col : sheetData)
                {
                    if (rowIndex == 0)
                    {
                        rowIndex++;
                        continue; // 不读第一行
                    }
                    Map<String, Object> providerBillDiscountMap = new HashMap<String, Object>();
                    providerBillDiscountMap.put("provider_id", provider_id);
                    providerBillDiscountMap.put("bill_pkg_id", bill_pkg_id);
                    
                    providerBillDiscountMap.put("province_code", col[0]);
                    if ("1".equals(discountType))
                    {
                        providerBillDiscountMap.put("discount", col[1]);
                    }
                    else if ("2".equals(discountType))
                    {
                        providerBillDiscountMap.put("city_code", col[1]);
                        providerBillDiscountMap.put("discount", col[2]);
                    }
                    
                    providerBillDiscountMap.put("create_user", userInfoMap.get("suId"));
                    // 将折扣数据导入数据库
                    providerBillDiscountDao.providerBillDiscountAdd(providerBillDiscountMap);
                    rowIndex++;
                }
            }
            // ------------------------------读取excel文件结束
            
            result = "true";
        }
        catch (Exception e)
        {
            getMyLog(e, log);
            result = e.getMessage();
        }
        return result + "";
    }
    
    /**
     * 获取记录数量
     * 
     * @param m
     * @return
     */
    public int getAgentCount(Map<String, Object> m)
    {
        int agentCount = 0;
        try
        {
            agentCount = providerBillDiscountDao.selectCount(m);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return agentCount;
    }
    
    /**
     * <h5>功能描述:</h5> 跳转到运营商折扣包折扣页面
     *
     * @param req
     * @return
     *
     * @作者：zhangpj @创建时间：2017年5月30日
     */
    public String providerBillDiscountListPage(String id, HttpServletRequest req)
    {
        Map<String, Object> providerPhysicalChannel = providerPhysicalChannelDao.selectByPrimaryKey(id);
        req.setAttribute("providerPhysicalChannel", providerPhysicalChannel);
        return "mp/providerBillDiscountList";
    }
    
    /**
     * <h5>功能描述:</h5> 分页获取运营商物理通道折扣信息
     *
     * @param req
     * @return
     *
     * @作者：zhangpj @创建时间：2017年5月10日
     */
    public String providerBillDiscountList(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            Page p = getPage(m);// 提取分页参数
            int total = getAgentCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
            sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
            sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
            sb.append("" + getKey("records") + ":" + p.getCount() + ",");
            sb.append("" + getKey("rows") + ":" + "");
            
            PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
            List<Map<String, Object>> dataList = providerBillDiscountDao.selectAll(m);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
            sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    /**
     * @功能描述： 设置物理运营商通道话费折扣
     *
     * @作者：zhangpj @创建时间：2017年5月15日
     * @param req
     * @return
     */
    public String providerBillDiscountEditExt(HttpServletRequest req)
    {
        boolean flag = true;
        try
        {
            Map<String, Object> myData = getMaps(req);
            Map<String, Object> userInfoMap = getUser(); // 取到当前用户信息
            
            myData.put("createUser", userInfoMap.get("suId"));// 放入创建用户
            String[] billPkgDiscount = myData.get("billPkgDiscount").toString().split(",");
            
            // 删除指定流量包折扣旧数据
            providerBillDiscountDao.deleteProviderBillDiscount(myData);
            
            for (int i = 0; i < billPkgDiscount.length; i++)
            {
                String[] discountInfo = billPkgDiscount[i].split("-");
                myData.put("name", discountInfo[0]);
                myData.put("billPkgId", discountInfo[1]);
                myData.put("discount", discountInfo[2]);
                
                // 添加运营商流量折扣信息
                int rows = providerBillDiscountDao.providerBillDiscountAdd(myData);
                if (rows < 1)
                {
                    flag = false;
                    throw new Exception("插入话费包折扣异常");
                }
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return flag + "";
    }
    
    /**
     * @功能描述： 折扣设置页面获取满足条件的话费包折扣信息
     *
     * @作者：zhangpj @创建时间：2017年5月16日
     * @param req
     * @return
     */
    public String selectProviderBillDiscount(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> maps = getMaps(req);
            List<Map<String, Object>> dataList = providerBillDiscountDao.selectAll(maps);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
    }
    
    public String getColModel(String physicalId,String providerId,HttpServletRequest requset){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("physicalId", physicalId);
		param.put("providerId", providerId);
		List<String> valueList = providerBillDiscountDao.getColModel(param);
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
			colNameList.add(value+"(扣款折扣)");
			Map<String,Object> model = new HashMap<String,Object>();
			int index = new Double(value*100).intValue();
			model.put("name", "fee"+index);
			model.put("index", "fee"+index);
			model.put("sortable", false);
			model.put("width", "100");
			model.put("align","center");
			model.put("editable", true);
			colModelList.add(model);
			colNameList.add(value+"(返利折扣)");
			Map<String,Object> models = new HashMap<String,Object>();
			int indexs = new Double(value*1700).intValue();
			models.put("name", "fee"+indexs);
			models.put("index", "fee"+indexs);
			models.put("sortable", false);
			models.put("width", "100");
			models.put("align","center");
			models.put("editable", true);
			colModelList.add(models);
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
			String pkgId = "";
			Map<String,Object> param = new HashMap<String,Object>();
			
		    String regEx = "^fee[1-9]0{3,9}$";
		    Pattern pattern = Pattern.compile(regEx);
		    Matcher matcher = pattern.matcher(feeKey);
		    boolean rs = matcher.matches();
			if(rs) {
				pkgId = billPkgDao.selectIdByValue(value/100);
				param.put("discount", map.get(feeKey));
			}else {
				pkgId = billPkgDao.selectIdByValue(value/1700);
				param.put("realDiscount", map.get(feeKey));
			}
			param.put("physicalId", map.get("physicalId"));
			param.put("providerId", map.get("providerId"));
			param.put("provinceCode", map.get("province"));
			param.put("billPkgId", pkgId);
			int flag = providerBillDiscountDao.updateDiscount(param);
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
    public String providerBillDiscountList(String physicalId, String providerId, HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> m = getMaps(req); // 封装前台参数为map
            m.put("physicalId", physicalId);
            m.put("providerId", providerId);
            Page p = getPage(m);// 提取分页参数
            int total = providerBillDiscountDao.getProviderBillDiscountListCount(m);
            p.setCount(total);
            int pageNum = p.getCurrentPage();
            int pageSize = p.getPageSize();
            
            sb.append("{");
			sb.append("" + getKey("page") + ":" + p.getCurrentPage() + ",");
			sb.append("" + getKey("total") + ":" + p.getNumCount() + ",");
			sb.append("" + getKey("records") + ":" + p.getCount() + ",");
			sb.append("" + getKey("rows") + ":" + "");

			String sql = providerBillDiscountDao.getAllAgentBillDiscountSql(m);
			PageHelper.startPage(pageNum, pageSize);// mybatis分页插件
			List<Map<String, Object>> billList = providerBillDiscountDao.getAllAgentBillDiscountForSql(sql);
			String billListJson = BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return sb.toString();
    }
    
    /**
     * excel文件上传解析
     * 
     * @param id
     * @param mFile
     * @return
     * @throws Exception
     */
    public List<String> commitProviderBillDiscount(String physicalId, MultipartFile mFile)
        throws Exception
    {
        
        InputStream is = mFile.getInputStream();
        List<String> errorList = new ArrayList<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        
        try
        {
            @SuppressWarnings("resource")
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            // List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++)
            {
                XSSFSheet normalSheet = workbook.getSheetAt(i);
                String sheetName = normalSheet.getSheetName();
                String providerId = providerDao.getIdByName(sheetName);
                if ("".equals(providerId) || null == providerId)
                {
                    errorList.add(sheetName + "名字不匹配！");
                }
                else
                {
                    map.put("physicalId", physicalId);
                    map.put("providerId", providerId);
                    // 根据物理运营商通道和运营商类型删除折扣
                    providerBillDiscountDao.deleteDiscountDel(map);
                    for (int r = 1; r < normalSheet.getPhysicalNumberOfRows(); r++)
                    {
                        XSSFRow itemSheetRow = normalSheet.getRow(r);
                        if (itemSheetRow.getCell(0) != null)
                        {
                            String provinceCode = itemSheetRow.getCell(0).getStringCellValue();
                            if (!"".equals(provinceCode) && null != provinceCode)
                            {
                                List<Map<String, Object>> insList = new ArrayList<Map<String, Object>>();
                                // 获取包ID，组装折扣map
                                if (itemSheetRow.getCell(1) != null)
                                {
                                    if (itemSheetRow.getCell(2) != null)
                                    {
                                        String fee = itemSheetRow.getCell(1).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(2).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("10");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(3) != null)
                                {
                                    if (itemSheetRow.getCell(4) != null)
                                    {
                                        String fee = itemSheetRow.getCell(3).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(4).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("20");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(5) != null)
                                {
                                    if (itemSheetRow.getCell(6) != null)
                                    {
                                        String fee = itemSheetRow.getCell(5).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(6).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("30");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(7) != null)
                                {
                                    if (itemSheetRow.getCell(8) != null)
                                    {
                                        String fee = itemSheetRow.getCell(7).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(8).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("50");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(9) != null)
                                {
                                    if (itemSheetRow.getCell(10) != null)
                                    {
                                        String fee = itemSheetRow.getCell(9).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(10).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("100");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(11) != null)
                                {
                                    if (itemSheetRow.getCell(12) != null)
                                    {
                                        String fee = itemSheetRow.getCell(11).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(12).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("200");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                    
                                }
                                
                                if (itemSheetRow.getCell(13) != null)
                                {
                                    if (itemSheetRow.getCell(14) != null)
                                    {
                                        String fee = itemSheetRow.getCell(13).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(14).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("300");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                
                                if (itemSheetRow.getCell(15) != null)
                                {
                                    if (itemSheetRow.getCell(16) != null)
                                    {
                                        String fee = itemSheetRow.getCell(15).getStringCellValue();
                                        String feeRel = itemSheetRow.getCell(16).getStringCellValue();
                                        if (!"".equals(fee) && null != fee && !"".equals(feeRel) && null != feeRel)
                                        {
                                            Map<String, Object> mapBill = new HashMap<String, Object>();
                                            String billPkgId = billPkgDao.getBillPkgByPrice("500");
                                            mapBill.put("provinceCode", provinceCode);
                                            mapBill.put("providerId", providerId);
                                            mapBill.put("physicalId", physicalId);
                                            mapBill.put("discount", fee);
                                            mapBill.put("realDiscount", feeRel);
                                            mapBill.put("billPkgId", billPkgId);
                                            insList.add(mapBill);
                                        }
                                    }
                                }
                                // 更新原先数据为删除，插入最新数据
                                for (Map<String, Object> insMap : insList)
                                {
                                    providerBillDiscountDao.insertSelective(insMap);
                                }
                            }
                        }
                    }
                }
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            errorList.add("上传表格格式不匹配！");
            getMyLog(e, log);
        }
        return errorList;
    }

	/**
	 * 获取通道折扣
	 * @author lks 2017年12月18日下午2:13:04
	 * @param request
	 * @return
	 */
	public String selectChannelVpd(HttpServletRequest request) {
		Map<String,Object> param = getMaps(request);
		Map<String,BigDecimal> discountMap = providerBillDiscountDao.selectChannelVpd(param);
		JSONObject json = new JSONObject();
		if(discountMap != null){
			Double discount = discountMap.get("discount").doubleValue();
			Double realDiscount = discountMap.get("real_discount").doubleValue();
			NumberFormat nf = NumberFormat.getNumberInstance();
			// 保留两位小数
			nf.setMaximumFractionDigits(4);
			if(discount != null){
				json.put("discount", nf.format(discount));
			}
			if(realDiscount != null){
				json.put("realDiscount", nf.format(realDiscount));
			}
		}
		return json.toJSONString();
	}
	
	/**
	 * 保存通道虚商折扣
	 * @author lks 2017年12月18日下午2:14:05
	 * @param request
	 * @return
	 */
	public String saveChannelVpd(HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		boolean flag = false;
		String msg = "提交失败";
		int sum = 0;
		Map<String, Object> userInfoMap = getUser();
        String userid = userInfoMap.get("suId") + "";
		Map<String,Object> param = getMaps(request);
//		List<String> pkgIdList = pbpDao.selectPkgIdByProviderId(param);
//		providerBillDiscountDao.deleteDiscountDel(param);
//		for(String pkgId : pkgIdList){
//			Map<String,Object> discountMap = new HashMap<String,Object>();
//			discountMap.put("provinceCode", "全国");
//			discountMap.put("providerId", param.get("providerId"));
//			discountMap.put("physicalId", param.get("physicalId"));
//			discountMap.put("discount", param.get("discount"));
//			discountMap.put("realDiscount", param.get("realDiscount"));
//			discountMap.put("billPkgId", pkgId);
//			sum += providerBillDiscountDao.insertSelective(discountMap);
//		}
//		if(sum == pkgIdList.size()){
//			msg = "提交成功";
//		}else{
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//		}
		String discountStr= param.get("discount").toString();
		if ("".equals(discountStr)) {
			// 删除物理通道的指定运营商的折扣信息
			providerBillDiscountDao.deleteDiscountDel(param);
			flag = true;
			msg = "折扣[清除]成功";
		} else {
			// 删除物理通道的指定运营商的折扣信息
			providerBillDiscountDao.deleteDiscountDel(param);
			String[] discountAarry = discountStr.split(",");
			int discountAarryLength = discountAarry.length;
			// 删除指定代理商的指定运营商的折扣信息
			String[] discountInfo = null;
			
			for (int i = 0; i < discountAarryLength; i++) {
				discountInfo = discountAarry[i].split("-");
				Map<String,Object> discountMap = new HashMap<String,Object>();
				discountMap.put("provinceCode", discountInfo[0]);
				discountMap.put("providerId", param.get("providerId"));
				discountMap.put("physicalId", param.get("physicalId"));
//				discountMap.put("discount", discount);
//				discountMap.put("realDiscount", realDiscount);
				discountMap.put("billPkgId", discountInfo[1]);
				discountMap.put("createUser", userid);
				
				if (!"".equals(discountInfo[2])) {
					discountMap.put("discount", discountInfo[2]);
				}
				if (!"".equals(discountInfo[3])) {
					discountMap.put("realDiscount", discountInfo[3]);
				}
				
				sum += providerBillDiscountDao.insertSelective(discountMap);
			}
			if(sum == discountAarryLength){
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
}
