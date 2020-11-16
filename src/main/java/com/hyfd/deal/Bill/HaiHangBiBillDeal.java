package com.hyfd.deal.Bill;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.deal.BaseDeal;
import com.hyfd.deal.interfaces.JavaScriptInterface;

public class HaiHangBiBillDeal implements BaseDeal {
	private static Logger log = Logger.getLogger(HaiHangBiBillDeal.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String, Object> map = new HashMap<String, Object>();
		int flag = -1;
		String phoneNo = (String) order.get("phone");							// 手机号
		Double fee = (Double) order.get("fee");									//金额，以元为单位
		String spec = fee.intValue() + ""; 										//只取整数部分
		Map<String, Object> channel = (Map<String, Object>) order.get("channel");// 获取通道参数
		String defaultParameter = (String) channel.get("default_parameter");	//默认参数
		Map<String,String> paramMap = XmlUtils.readXmlToMap(defaultParameter.trim());
		Map<String,Object> cookie = (Map<String, Object>) order.get("cookie"); 	//cookie
		String payPwd = paramMap.get("payPwd");									//操作密码
		String agentAirRechargeUrl = paramMap.get("agentAirRechargeUrl");		//下单地址
		String serviceUrl = paramMap.get("serviceUrl")+""+new Date().getTime();	//下单时跟参数一起提交的地址
		// 生成自己的id，供回调时查询数据使用
		String orderId = ToolDateTime.format(new Date(),"yyyyMMddHHmmssSSS") + GenerateData.getStrData(3);
		map.put("orderId", orderId);
		try {
			// 海航币充值
			String result = recharge(cookie.get("cookies")+"",payPwd, phoneNo, spec,agentAirRechargeUrl,serviceUrl);
			log.info("号码[" + phoneNo + "]海航币[话费充值]请求返回信息[" + result + "]");
			if (!result.equals("")) {
				//{"Result":"0","Msg":"未知异常"}
				JSONObject jsonObj = JSONObject.parseObject(result);
				String code = jsonObj.getString("Result");
				String Msg = jsonObj.getString("Msg");
				map.put("resultCode", code + ":" + Msg);
				if (code.equals("0")) {
					flag = 3;
					map.put("resultCode", code + ":充值成功");
					log.debug("海航币[话费充值]请求:提交成功!手机号["+phoneNo+"],充值金额["+spec+"]元");
				}else {
					log.debug("海航币[话费充值]请求:提交失败!手机号[" + phoneNo + "],充值金额["	+ spec + "]元," + jsonObj.toString());
					flag = 4;
				}
			} else{
				log.debug("海航币[话费充值]请求:充值请求发生异常,请人工核实处理!手机号["+phoneNo+"],充值金额["+spec+"]元");
				flag = -1;
			}
		} catch (Exception e) {
			flag = -1;
			if (map.get("resultCode") == null) {
				map.put("resultCode", "充值请求发生异常,请人工核实处理");
			}
			log.error("号码[" + phoneNo + "]海航币[话费充值]发生异常:" + e.getMessage() + ","+ MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	/**
	 * 余额充值功能
	 * @param cookies cookie
	 * @param payPwd  支付密码
	 * @param phoneNO 充值手机号
	 * @param rechargeAmount  支付金额
	 * @param agentAirRechargeUrl  充值地址
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String recharge(String cookie,String payPwd,String phoneNO,String rechargeAmount,
			String agentAirRechargeUrl,String serviceUrl) throws ClientProtocolException, IOException {
		log.info("号码[" + phoneNO + "]海航币[话费充值]下单开始:"+"cookie:"+cookie+"payPwd:"+payPwd+"rechargeAmount:"+rechargeAmount+"agentAirRechargeUrl:"+agentAirRechargeUrl+"serviceUrl:"+serviceUrl);
		HttpClient httpClient = new HttpClient();
//		serviceUrl =  "https://www.10044.cn/SaleWeb/www/html/index.html?time="+new Date().getTime();  //请求地址
		JSONObject rechargeJson = new JSONObject();
		rechargeJson.put("payPwd", rsaEncrypt(payPwd));
		rechargeJson.put("phoneNO", rsaEncrypt(phoneNO));
		rechargeJson.put("rechargeAmount", rsaEncrypt(rechargeAmount));
		rechargeJson.put("serviceUrl", serviceUrl);
		PostMethod transferMethod = new PostMethod(agentAirRechargeUrl);
		NameValuePair[] transferParam = {
                new NameValuePair("serviceUrl", serviceUrl),
                new NameValuePair("payPwd", rsaEncrypt(payPwd)),
                new NameValuePair("phoneNO", rsaEncrypt(phoneNO)),
                new NameValuePair("rechargeAmount", rsaEncrypt(rechargeAmount)),
        };
		transferMethod.setRequestBody(transferParam);
		transferMethod.setRequestHeader("cookie",cookie);
		httpClient.executeMethod(transferMethod);
		BufferedReader reader = new BufferedReader(new InputStreamReader(transferMethod.getResponseBodyAsStream()));  
		StringBuffer stringBuffer = new StringBuffer();  
		String str = "";  
		while((str = reader.readLine())!=null){  
		    stringBuffer.append(str);  
		}  
		String transferResult = stringBuffer.toString();
		return transferResult;
	}
	
	
	/**
	 * <h5>功能:javaScript的RSA加密</h5>
	 * 
	 * @author zhangpj	@date 2018年9月12日
	 * @param value
	 * @return 
	 */
	private static String rsaEncrypt(String value){
		ScriptEngineManager manager = new ScriptEngineManager();
		// 获取一个JavaScript 引擎实例
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		String str="";
		try {
			// 获取文件路径
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//            System.out.println(path);
            // FileReader的参数为所要执行的js文件的路径
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/BigInt.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/Barrett.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/RSA.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/base64.js"));
            engine.eval(new FileReader(path+ "../../js/project/haihangbi/rsaEncrypt.js"));
            
            if (engine instanceof Invocable) {
                Invocable invocable = (Invocable) engine;
                // 从脚本引擎中获取JavaScriptInterface接口对象（实例）. 该接口方法由具有相匹配名称的脚本函数实现。
                JavaScriptInterface executeMethod = invocable.getInterface(JavaScriptInterface.class);
                // 调用这个js接口
                str = executeMethod.rsaEncrypt(value);
//                System.out.println(str);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("海航币加密参数："+str);
		return str;
	}

}
