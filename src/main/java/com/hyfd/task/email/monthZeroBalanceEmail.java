package com.hyfd.task.email;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MailUtil;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.multipleReportDao;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class monthZeroBalanceEmail {

    static Logger log = Logger.getLogger(monthZeroBalanceEmail.class);

    @Autowired
    private AgentDao agentDao;

    private static String path = dailyReportEmail.class.getResource("/").getPath();

    private static String[] tos = {"1124215004@qq.com"};
//    private static String[] tos = {"15169120000@139.com"};
    private static String[] copyto = {"13964059262@139.com","15169120000@139.com"};
//	private static String[] copyto ={};


    /**
     * 每天发送每日的财报数据给财务等人
     * @author lks 2018年2月6日下午4:26:38
     */
    @Scheduled(cron = "0 0 0 1 * ? ")
    public void sendDailyReportEmail(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startformat = simpleDateFormat.format(new Date());
        log.info(startformat + "财报准备数据开始----------------------------------------");
        String date = DateTimeUtils.formatDate(DateTimeUtils.addDays(new Date(), -1),"yyyy年MM月");
//		String date = "2018年02月26日";
//		String dailyFilePath = path.replaceAll("/WEB-INF/classes/", "") + File.separator + "temp"+ File.separator+date+".xlsx";
        String dailyFilePath = path.replaceAll("/WEB-INF/classes/", "") + "/" + "temp"+"/"+date+".xlsx";
        createDailyExcel(dailyFilePath);
        log.info("财报数据模板完成准备发送-------------------------------------");
        Map<String,Object> mail = new HashMap<String,Object>();
        mail.put("tos", tos);
//        mail.put("copyto", copyto);
        mail.put("title", date+"零点余额");
        mail.put("context", date+"数据已出，内容见附件");
        mail.put("fileAddr", dailyFilePath);
        mail.put("fileName", date+"数据.xlsx");
        try {
            log.info("财报数据模板完成准备发送-------------------------------------");
            MailUtil.sendExcel(mail);
            String endformat = simpleDateFormat.format(new Date());
            log.info(endformat + "发送财报数据成功----------------------------------");
        } catch (Exception e) {
            String errformat = simpleDateFormat.format(new Date());
            log.error(errformat + "发送财报数据发生异常--------------------------:",e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 生成excel文件
     * @author lks 2018年2月6日下午4:53:48
     */
    public void createDailyExcel(String filePath){

        List<Map<String, Object>> dataList = agentDao.selectZeroBalance();


        Map<String,Object> map = new HashMap<String,Object>();
        map.put("title", "零点余额");
        map.put("agentDataList", dataList);
        log.info("获取零点余额发送的财报数据"+ map + "-----------------------------------------------------");
        exportExcel("zerobalanceTemplate",filePath,map);

    }

    public static void exportExcel(String tempName,String filePath,Map<String,Object> map){
        try{
//			String templatePath = path.replaceAll("/WEB-INF/classes/", "") + File.separator + "downloadFiles"+ File.separator+tempName+".xlsx";
            String templatePath = path.replaceAll("/WEB-INF/classes/", "") + "/" + "downloadFiles"+"/"+tempName+".xlsx";
            XLSTransformer former = new XLSTransformer();
            FileInputStream in = new FileInputStream(templatePath);
            XSSFWorkbook workbook = (XSSFWorkbook) (former.transformXLS(in, map));
            File file = new File(filePath);
            if(!file.exists()){
                File fileParent = file.getParentFile();

                if(!fileParent.exists()){
                    fileParent.mkdirs();
                }
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            workbook.write(os);
            os.close();
            log.info("把数据写到模板成功--------------------------------");
        }catch(Exception e){
            log.info("把数据写到模板出现异常--------------------------------");
            e.printStackTrace();
        }
    }
}
