package com.hyfd.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date> {    
public Date convert(String source) {    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");    
    dateFormat.setLenient(false);    
    try {  
    	if(source==null||"".equals(source)) return null;
        return dateFormat.parse(source);    
    }  catch (java.text.ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}           
    return null;    
} 
}
