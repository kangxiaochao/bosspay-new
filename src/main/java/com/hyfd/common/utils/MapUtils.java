package com.hyfd.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapUtils {

	/**
	 * 判断map中是否有空值
	 * @author lks 2016年12月6日上午10:19:33
	 * @param map
	 * @return true map中不存在空值，false map中存在空值或者map为空
	 */
	public static boolean checkEmptyValue(Map<?, ?> map){
		boolean flag = true;
		if(map!=null){
			Set<?> set=map.keySet();  
	        Iterator<?> ite=set.iterator();
	        while(ite.hasNext()){
	        	Object key = ite.next();
	        	if(map.get(key)==null||map.get(key)==""){
	        		flag = false;
	        	}
	        }
		}else{
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 获取Map的key与value的对应字符串
	 * @author lks 2016年12月15日下午4:25:14
	 * @param map
	 * @return
	 */
	public static String toString(Map<?, ?> map){
		String str = "";
		if(map!=null){
			Set<?> set=map.keySet();  
	        Iterator<?> ite=set.iterator();
	        while(ite.hasNext()){
	        	Object key = ite.next();
	        	str += (key+"="+map.get(key)+"|");
	        }
		}else{
			str = "该Map为空";
		}
		return str;
	}
	
	/**
	 * 去除map中的空值
	 * @author lks 2017年5月10日上午9:41:57
	 * @param map
	 * @return
	 */
	public static Map<String,Object> removeSpaceValueMap(Map<String,Object> map){
		Set<?> set=map.keySet();  
        Iterator<?> ite=set.iterator();
        while(ite.hasNext()){
        	Object key = ite.next();
        	if(map.get(key)==null||map.get(key).equals("")){
        		map.remove(key);
        	}
        }
		return map;
	}
}
