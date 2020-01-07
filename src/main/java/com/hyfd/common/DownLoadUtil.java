package com.hyfd.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownLoadUtil {
	
	public static final String NAMESUFFIX_HTML  = ".html"; //HTML
	public static final String NAMESUFFIX_PDF   = ".pdf"; //PDF
	public static final String NAMESUFFIX_EXCEL = ".xlsx"; //EXCEL
	public static final String NAMESUFFIX_WORD  = ".doc"; //WORD
	public static final String NAMESUFFIX_ZIP   = ".zip"; //ZIP
	public static final String NAMESUFFIX_TXT   = ".txt"; //HTML	
	
	public static final  String DOWNLOAD_PDF   ="1";//PDF
	public static final  String DOWNLOAD_EXCEL ="2";//EXCEL
	public static final  String DOWNLOAD_WORD  ="3";//WORD
	public static final  String DOWNLOAD_ZIP   ="4";//ZIP
	public static final  String DOWNLOAD_TXT   ="5";//TXT
	public static final  String DOWNLOAD_HTML  ="6";//HTML
	
	/**
	 * 
	 * @param response
	 * @param request
	 * @param filePath
	 * @param fileName
	 * @param type
	 * @throws IOException
	 */
 public static void downLoad(HttpServletResponse response,HttpServletRequest request,String filePath ,String fileName,String type) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		try {
			File file = null;
			if(DOWNLOAD_PDF.equals(type)){
				file = new File(filePath+fileName+NAMESUFFIX_PDF);
			}else if(DOWNLOAD_EXCEL.equals(type)){
				file = new File(filePath+fileName+NAMESUFFIX_EXCEL);
			}else if(DOWNLOAD_WORD.equals(type)){
				file = new File(filePath+fileName+NAMESUFFIX_WORD);
			}else if(DOWNLOAD_ZIP.equals(type)){
				file = new File(filePath+fileName+NAMESUFFIX_ZIP);
			}else if(DOWNLOAD_TXT.equals(type)){
				file = new File(filePath+fileName+NAMESUFFIX_TXT);
			}else{
				file = new File(filePath+fileName+NAMESUFFIX_HTML);
			}
			long fileLength = file.length();
			response.setContentType("application/x-msdownload;");	//设置为下载类型
			response.setHeader("Content-disposition", "attachment; filename="+ new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
			response.setHeader("Content-Length", String.valueOf(fileLength));
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
}
}
