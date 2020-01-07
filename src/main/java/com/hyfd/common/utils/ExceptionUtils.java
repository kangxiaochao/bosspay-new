package com.hyfd.common.utils;

public class ExceptionUtils {

	public static String getExceptionMessage(Exception e){
		StackTraceElement ste = e.getStackTrace()[e.getStackTrace().length-1];
		String fileName = ste.getFileName();//文件名
		String className = ste.getClassName();//类名
		String methodName = ste.getMethodName();//方法名
		int lineNumber = ste.getLineNumber();//行号
		String message = fileName+"文件出现异常，类为"+className+"，异常信息为"+e.getLocalizedMessage()+"，出现在"+methodName+"方法，"+lineNumber+"行";
		return message;
	}
	
}
