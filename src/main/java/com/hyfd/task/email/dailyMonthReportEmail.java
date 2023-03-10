package com.hyfd.task.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MailUtil;
import com.hyfd.dao.mp.multipleReportDao;

import net.sf.jxls.transformer.XLSTransformer;

@Component
public class dailyMonthReportEmail {

	Logger log = Logger.getLogger(getClass());

	@Autowired
	multipleReportDao mRDao;
	
	private static String path = dailyMonthReportEmail.class.getResource("/").getPath();
	
	private static String[] tos = {"15169120000@139.com"};
	private static String[] copyto = {"15908082988@139.com","2607931601@qq.com","13964059262@139.com"};
//	private static String[] copyto ={};
	
	/**
	 * 每天发送每日的财报数据给财务等人
	 * @author lks 2018年2月6日下午4:26:38
	 */
	@Scheduled(cron = "0 30 3 * * ? ")
	public void sendMonthReportEmail(){
		String date = DateTimeUtils.formatDate(DateTimeUtils.addDays(new Date(), -1),"yyyy年MM月dd日");
//		String date = "2018年02月26日";
//		String monthFilePath = path.replaceAll("/WEB-INF/classes/", "") + File.separator + "temp"+ File.separator+"当月累计数据("+date+").xlsx";
		String monthFilePath = path.replaceAll("/WEB-INF/classes/", "")+ "/" + "temp"+"/"+"当月累计数据("+date+").xlsx";
		createMonthExcel(monthFilePath);
		Map<String,Object> mail = new HashMap<String,Object>();
		mail.put("tos", tos);
		mail.put("copyto", copyto);
		mail.put("title", date+"当月累计数据");
		mail.put("context", date+"数据已出，内容见附件");
		mail.put("fileAddr", monthFilePath);
		mail.put("fileName", date+"当月累计数据.xlsx");
		try {
			MailUtil.sendExcel(mail);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成excel文件
	 * @author lks 2018年2月6日下午4:53:48
	 */
	public void createMonthExcel(String filePath){
		//下家数据
		List<Map<String,Object>> agentList = mRDao.selectAgentMonthData();
		//上家数据
		List<Map<String,Object>> providerList = mRDao.selectProviderMonthData();
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("title", "当月累计数据");
		map.put("agentDataList", agentList);
		map.put("providerDataList", providerList);
		
		exportExcel("dailyReportTemp",filePath,map);
		
 	}
	
	public static void exportExcel(String tempName,String filePath,Map<String,Object> map){
		try{
//			String templatePath = path.replaceAll("/WEB-INF/classes/", "") + File.separator + "downloadFiles"+ File.separator+tempName+".xlsx";
			String templatePath = path.replaceAll("/WEB-INF/classes/", "")+ "/" + "downloadFiles"+"/"+tempName+".xlsx";
			XLSTransformer former = new XLSTransformer();
	        FileInputStream in = new FileInputStream(templatePath);
	        XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
	        File file = new File(filePath);
	        if(!file.exists()){
	        	file.createNewFile();
	        }
	        OutputStream os = new FileOutputStream(file);
	        workbook.write(os);
	        os.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
}
