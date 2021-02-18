package com.hyfd.service.mp;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.HttpUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.taglibs.standard.lang.jstl.NullLiteral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class QueryOrderFindallSer extends BaseService {
    public Logger log = Logger.getLogger(this.getClass());

    @Autowired
    ExceptionOrderDao exOrderDao;

    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;// 通道的物理参数表

    public String findorderbyTask(List list) {
            //判断非空
//        if ( ("".equals(orderid) && orderid == null) ||  (dispatcher_provider_id == null && "".equals(dispatcher_provider_id))){
//            return null;
//        }else {
//
//        }

        //去异常订单表去查,不是查订单表.
        //先拿到订单号和通道,判断是哪一个通道,然后把每个通道封装成一个方法,然后去调用方法.
        // String orderid = "201801291353591691703771178686";
        //如果订单号不存在或者通道出错,就给弹框显示,现在是返回null,对客户不友好.
        //多查也是查一个通道在的订单.
        //String dispatcher_provider_id = (String) o.get("dispatcher_provider_id");

        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> billList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map o = (Map) list.get(i);
            String dispatcher_provider_id = (String) o.get("dispatcher_provider_id");
            if(dispatcher_provider_id.equals("2000000039")){
                billList.add(PBSManualTask(o));
            }else{
                return null;
            }
        }
        String bill = BaseJson.listToJson(billList);
        sb.append(bill);
        return sb.toString();
    }

    /**
     * 鹏博士手动查单接口
     *
     */
    public  Map<String, Object> PBSManualTask( Map<String, Object> maps){
        String orderid = (String) maps.get("orderid");
        String dispatcher_provider_id = (String) maps.get("dispatcher_provider_id");
        Map<String, Object> stringObjectMap = exOrderDao.selectByOrderId(orderid);
            try {
                if (stringObjectMap != null) {
                    String status = stringObjectMap.get("status") + "";
                    //status = "00";
                    Map<String, Object> channel = providerPhysicalChannelDao.selectByProviderId(dispatcher_provider_id);
                    String defaultParameter = (String) channel.get("default_parameter");// 默认参数
                    Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
                    String appKey = paramMap.get("appKey");
                    String partnerId = paramMap.get("partnerId");
                    String queryUrl = paramMap.get("queryUrl");
                    String nonce_str = DateUtils.getNowTimeStamp().toString() + ((int) (Math.random() * 9000) + 1000);// 随机字符串
                    String parms = joinUrl(nonce_str, orderid, partnerId, appKey);
                    String result = HttpUtils.doGet(queryUrl + "?" + parms);
                    JSONObject resultJson = JSONObject.parseObject(result);
                    String retcode = resultJson.getString("retcode");
                    if (retcode.equals(null)) {
                        stringObjectMap.put("status", "-1");//订单异常
                        stringObjectMap.put("result_code", "订单进入异常,请联系上家核实");
                    } else if (retcode.equals("3001")) {
                        stringObjectMap.put("status", "1");//订单提交成功
                        stringObjectMap.put("result_code", "订单提交成功");
                    } else if (retcode.equals("3004")) {
                        stringObjectMap.put("status", "3");//订单充值成功
                        stringObjectMap.put("result_code", "订单充值成功,可以去异常订单表修改订单了");
                    }
                }
            } catch (Exception e) {
                getMyLog(e, log);
            }
        return stringObjectMap;
    }

    /**
     * 拼接请求信息
     *
     * @param nonce_str
     * @param order_id
     * @param partner_id
     * @param appKey
     * @return
     */
    public String  joinUrl(String nonce_str, String order_id, String partner_id, String appKey) {
        StringBuffer suBuffer = new StringBuffer();
        suBuffer.append("nonce_str" + nonce_str);
        suBuffer.append("order_id" + order_id);
        suBuffer.append("partner_id" + partner_id);
        suBuffer.append(appKey);
        String sign = DigestUtils.md5Hex(suBuffer + "");// 签名

        StringBuffer url = new StringBuffer();
        url.append("partner_id=" + partner_id);
        url.append("&nonce_str=" + nonce_str);
        url.append("&order_id=" + order_id);
        url.append("&sign=" + sign);

        return url.toString() + "";
    }

    /**
     * 解析excel内容
     * @param file
     * @param req
     */
    public ArrayList<Map> SectionAdd(MultipartFile file, HttpServletRequest req) {
        //建一个集合,把所有的map包括起来.
        int flag = -1;
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        ArrayList<Map> maps = new ArrayList<>();
        try {
            Workbook book = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = book.getSheetAt(0);
            flag = sheet.getLastRowNum();//获取行数.
            for (int i = 0; i < flag + 1; i++) {
                Row row = sheet.getRow(i);
                row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                Map<String,Object> m = new HashMap<String, Object>();
                m.put("orderid", row.getCell(0).getStringCellValue());//获取第0列的数据
                m.put("dispatcher_provider_id", row.getCell(1).getStringCellValue());
                maps.add(m);
            }
            book.close();
        }
        catch ( Exception e){
            getMyLog(e, log);
        }finally {
            fixedThreadPool.shutdown();
        }
        return maps;
    }
}



