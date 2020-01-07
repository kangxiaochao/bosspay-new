package com.hyfd.service.mp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.csvreader.CsvWriter;
import com.github.pagehelper.PageHelper;
import com.hyfd.common.BaseJson;
import com.hyfd.common.Page;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.SubmitOrderDao;
import com.hyfd.service.BaseService;

@Service
public class SubmitOrderSer extends BaseService{

	Logger log = Logger.getLogger(SubmitOrderSer.class);
	@Autowired
	AgentDao agentDao;
	@Autowired
	SubmitOrderDao submitOrderDao;
	
	/**
	 * 根据条件查询数据列表
	 * @author lks 2017年5月4日上午8:53:16
	 * @param request
	 * @return
	 */
	public String submitOrderList(HttpServletRequest request){
		Map<String,Object> param = getMaps(request);
		StringBuilder sb=new StringBuilder();
		try{
			Page p=getPage(param);//提取分页参数
			int total=submitOrderDao.selectCount(param);
			p.setCount(total);
			int pageNum=p.getCurrentPage();
			int pageSize=p.getPageSize();
			sb.append("{");
			sb.append(""+getKey("page")+":"+p.getCurrentPage()+",");
			sb.append(""+getKey("total")+":"+p.getNumCount()+",");
			sb.append(""+getKey("records")+":"+p.getCount()+",");
			sb.append(""+getKey("rows")+":"+"");
			PageHelper.startPage(pageNum, pageSize);//mybatis分页插件
			List<Map<String,Object>> billList = submitOrderDao.selectAll(param);
			String billListJson=BaseJson.listToJson(billList);
			sb.append(billListJson);
			sb.append("}");
		}catch(Exception e){
			log.error("订单提交记录列表查询方法出错"+e.getMessage());
		}
		return sb.toString();
	}
	
	/**
	 * 导出订单提交记录
	 * @param rep
	 * @param req
	 */
	public void expsubmitOrder(HttpServletResponse resp,HttpServletRequest req) {
		Map<String,Object> m = getMaps(req);
		String applyDate = DateTimeUtils.formatDate(m.get("startDate").toString());
		String sheetName = "话费提交记录报表";
		String fileName = applyDate + "-" + sheetName;
		String[] headers = {"代理商名称","代理商订单号","手机号","提交参数","提交结果","提交时间"};
		try {
			int total = submitOrderDao.selectCount(m);
			File result = returnToTheResult(m,fileName,headers,total);
			outCsvStream(resp, result,fileName);
			//删除临时文件
			deleteFile(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File returnToTheResult(Map<String, Object> m,String fileName,String[] headers,int total) throws IOException{
    	File tempFile = File.createTempFile(fileName, ".csv");
		CsvWriter csvWriter = new CsvWriter(tempFile.getCanonicalPath(), ',', Charset.forName("UTF-8"));
		csvWriter.writeRecord(headers);

		int sum = total % 5000 == 0 ?  total/5000 : total/5000+1;
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
		
		for (int i = 1; i <= sum; i++) {
			int pageNum = i;
			fixedThreadPool.submit(() -> {
				PageHelper.startPage(pageNum, 5000);// mybatis分页插件
				List<Map<String, Object>> billList = submitOrderDao.selectAll(m);
				billList.forEach(bill -> {
					try {
						csvWriter.write(bill.get("agent_name") + "");
						csvWriter.write(bill.get("order_id") + "");
						csvWriter.write(bill.get("phone") + "");
						csvWriter.write(bill.get("submit_param") + "");
						csvWriter.write(bill.get("result_code") + "");
						csvWriter.write("'" + bill.get("submit_date"));
						csvWriter.endRecord();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
		}
		fixedThreadPool.shutdown();
		while (true) {
			if (fixedThreadPool.isTerminated()) {
				csvWriter.close();
				return tempFile;
			}
		}
    }
}
