package com.hyfd.common.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JsonHelper
{
    /**
     * 将Javabean转换为Map
     * 
     * @param javaBean javaBean
     * @return Map对象
     */
    public static Map toMap(Object javaBean)
    {
        
        Map result = new HashMap();
        Method[] methods = javaBean.getClass().getDeclaredMethods();
        
        for (Method method : methods)
        {
            
            try
            {
                
                if (method.getName().startsWith("get"))
                {
                    
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    
                    Object value = method.invoke(javaBean, (Object[])null);
                    result.put(field, null == value ? "" : value.toString());
                    
                }
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
        }
        
        return result;
        
    }
    
    // /**
    // * 将Json对象转换成Map
    // *
    // * @param jsonObject
    // * json对象
    // * @return Map对象
    // * @throws JSONException
    // */
    // public static Map toMap(String jsonString) throws JSONException {
    //
    // JSONObject jsonObject = new JSONObject(jsonString);
    //
    // Map result = new HashMap();
    // Iterator iterator = jsonObject.keys();
    // String key = null;
    // String value = null;
    //
    // while (iterator.hasNext()) {
    //
    // key = (String) iterator.next();
    // value = jsonObject.getString(key);
    // result.put(key, value);
    //
    // }
    // return result;
    //
    // }
    //
    // /**
    // * 将JavaBean转换成JSONObject（通过Map中转）
    // *
    // * @param bean
    // * javaBean
    // * @return json对象
    // */
    // public static JSONObject toJSON(Object bean) {
    //
    // return new JSONObject(toMap(bean));
    //
    // }
    //
    //
    // public static Map<String,String> getParams(Map requestParams) {
    // Map<String,String> params = new HashMap<String,String>();
    // //记录日志
    // //logger.info("获取  支付宝 同步 反馈信息:" + requestParams);
    // for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
    // String name = (String) iter.next();
    // String[] values = (String[]) requestParams.get(name);
    // String valueStr = "";
    // for (int i = 0; i < values.length; i++) {
    // valueStr = (i == values.length - 1) ? valueStr + values[i]
    // : valueStr + values[i] + ",";
    // }
    // params.put(name, valueStr);
    // }
    // return params;
    // }
    //
    // /*
    // * 从流中获取数据源
    // */
    // public static String readJSONString(HttpServletRequest request){
    // StringBuffer json = new StringBuffer();
    // String line = null;
    // try {
    // BufferedReader reader = request.getReader();
    // while((line = reader.readLine()) != null) {
    // json.append(line);
    // }
    // }
    // catch(Exception e) {
    // System.out.println(e.toString());
    // }
    // return json.toString();
    // }
    
    /**
     * 把数据源HashMap转换成json
     * 
     * @param map
     */
    public static String hashMapToJson(HashMap<String, Object> map)
    {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();)
        {
            Entry e = (Entry)it.next();
            string += "'" + e.getKey() + "':";
            string += "'" + e.getValue() + "',";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }
    
}
