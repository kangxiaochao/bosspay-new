package com.hyfd.test;

import com.hyfd.common.utils.DateUtils;
import com.hyfd.common.utils.ToolDateTime;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test1 {

	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALw76MUVJcFoAnP1u5oLX1d/SnxLlT5lS9ja0AGyQYZ+Zkv8Aze/ag3hdAv3uHuV+NOp4JtZd0rf4hDF8t2bfK7cdw4ast8ZQ8nx7u0XL/oLfR+aHL0ENFIz+llJ121+Ln7F61awzIk8fzgUyNf1+Gdd9THNZcqa71Ywt0u5a4CHAgMBAAECgYAVFazcLqM9B9aGgLcT6UQzu9lvFNRR2ezmDJSSLBJ060GwLzl5DCLoDSH/mS78AO+PXg7/t8TKCUv8TwAGXL1Zc5XR5VRMwWJvEzYXenJW9gygzmBPLFkRPPmAdADrX9aAI4rx5rpTjcPpEa96jVTd2j1ZZmbOTHWJOhjeTSK7yQJBAO5w+WWUD49m706wu7IcYwEL+P53wkRVMLGlKTtNp8V9GZ0Hvs0ZUhd2kZqfo62yTSxhgfgwi3FN0waF/E9ZrkUCQQDKGHEmT2Kkbdajwp17MS7CD4wfP+8vCYP860yrasrSQkqwnENni6j3OtZ53OUGAFDV2tbvCpuituZ1wh9TKDZbAkAsO+0oeQVB9ziiAotviXCcexogKxtxDdTQv/EcPVXIf2LHDO7Koc3m6Gouozr8OD205An2fxr/VA64ARdDqUF5AkEAlIT/6pqesJ50BidJGrbbWQyZ+oE+4v+IO0AlOTijU4Aaac59DsSpaP+ZjTdULGPhBDjuif/uvtbRaSs/YBqGTwJBANXEVfdNgZvKyGzdCqgBqrW8MxTTz4EwFZekqz+CMo2/YF/xLCfRdnXV/zaElpXxRDEYVRn52m3lw12Wxe1teAA=";

	// 测试用户名 -需更换正式签名
	private static String terminalName = "yihaodian";

	public static void main(String[] args) {
		/**第一步-私钥生成**/
		//私钥生成：生成的私钥和公钥都放在设置的目录里面,请将公钥和私钥发送给商务经理
		HaoBaiKey.genKeyPair("d:\\");
		//设置线程数
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++) {
			//多线程
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					/**第二步-参数封装**/
					String orderId = "asdfghjklq"+ToolDateTime.format(new Date(),"yyyyMMddHHmmss")+(RandomUtils.nextInt(9999999) + 10000000); //订单编号必须32位
					String phone = "1700000"+String.format("%04d",new Random().nextInt(9999)); // 充值号码
					String orderType = "2"; // 1:流量 2:话费
					String fee = ""+new Random().nextInt(100)*100; // 充值金额 流量:填写流量值单位M,话费:填写充值金额,单位分
					String callBack = "http://www.baidu.com"; // 订单状态回调地址
					String type = "nation"; // 充值类型 nation:全国 province:省内
					String timeMillis = DateUtils.getNowTimeToMS(); //请求时间戳
					String url = test1.quotaOrder(orderId, phone, orderType, type, fee, callBack, timeMillis);
					// 创建Httpclient对象
					CloseableHttpClient httpclient = HttpClients.createDefault();
					HttpGet httpGet = new HttpGet(url);
					//response 对象
					CloseableHttpResponse response = null;
					String resultString="0";
					try {
						// 执行http get请求
						response = httpclient.execute(httpGet);
					}catch (Exception e){
						e.printStackTrace();
					}
					/**第三步-生成URL地址**/
					System.out.println();
				}
			});
		}
		executorService.shutdown(); //关闭线程
//		System.out.println(test1.queryOrder(orderId, orderType));
//		System.out.println(test1.queryBalance());

		/**第四步-执行生成的url**/
	}

	/**
	 * @功能描述：流量包订购封装
	 */
	public static String quotaOrder(String customerOrderId, String phoneNo, String orderType, String scope, String spec,
									String callbackUrl, String timeStamp) {
		// 加密后产生的密钥
		String signature = "";
		try {
			signature = HaoBaiSign.sign(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec, terminalName,
					timeStamp, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String content = HaoBaiSign.getCustSignature(callbackUrl, customerOrderId, orderType, phoneNo, scope, spec,
				terminalName, timeStamp);
		String url = "http://localhost:8080/bosspaybill_war/order/quotaOrder?" + content + "&signature=" + signature;
		return url;
	}

	/**
	 * @功能描述： 订单查询
	 */
	public static String queryOrder(String customerOrderId, String orderType) {
		// 加密后产生的密钥
		String signature = "";
		try {
			signature = HaoBaiSign.sign(terminalName, customerOrderId, orderType, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = "http://localhost:8080/bosspaybill_war/order/queryOrder?customerOrderId=" + customerOrderId
				+ "&orderType=1&terminalName=" + terminalName + "&signature=" + signature;

		return url;
	}

	/**
	 * @功能描述： 余额查询
	 */
	public static String queryBalance() {
		// 加密后产生的密钥
		String signature = "";
		try {
			signature = HaoBaiSign.sign(terminalName, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = "http://localhost:9001/bosspaybill_war_exploded/agent/queryBalance?terminalName=" + terminalName + "&signature="
				+ signature;

		return url;
	}
	
}
