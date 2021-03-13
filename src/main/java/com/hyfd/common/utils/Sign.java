package com.hyfd.common.utils;

import java.util.*;

/**
 * 接口参数传递过程中的签名生成算法
 * 
 */
public class Sign {

	private static final String CHARSET = "UTF-8";



	public static synchronized String signAES(String key, Map<String, String> parameters) {

		Map<String, String> filterParameters = filterParameters(parameters);
		String link = linkParameters(filterParameters);
		String mysign = MD5.signAES(link, key, CHARSET);
		return mysign;
	}


	/**
	 * sign不参与生成签名
	 * 
	 * @param parameters
	 * @return
	 */
	private static Map<String, String> filterParameters(Map<String, String> parameters) {
		Map<String, String> result = new HashMap<String, String>();
		if (parameters == null || parameters.size() <= 0) {
			return result;
		}

		for (String key : parameters.keySet()) {
			String value = String.valueOf(parameters.get(key));
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
				continue;
			}
			result.put(key, value);
		}
		return result;
	}

	private static String linkParameters(Map<String, String> parameters) {
		List<String> keys = new ArrayList<String>(parameters.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = parameters.get(key);
			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr;
	}
}

