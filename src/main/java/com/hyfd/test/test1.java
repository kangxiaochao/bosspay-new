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

	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK1DeSXuGil9je1MAaJ3/4RfAFyaXpP1RATTcjlQUbGcAENgkagp/i1ONTRe9mbGYult1BrK3/nDO1b8KzgKbHypXX+5+NR35mLwabYvxj+D8EZLtD1gx+loLDGDDkr/1iZlHnoJyd8kNyzjX+5KKMDfIM7fc/waDk7kST8+QquvAgMBAAECgYAGa0WFvjiB6QDlFbfJySBSSyK8S4vNd3y5oQvOS8d7CQiJdn5u+H9ONmOKOSpvzFHFzCfbhkCeBDmik10WimxScmjLJhgRC4T4HF0SHN6970DChEqH7dbGxkzmrNXsZgMsiHM8z474nj6RfzB3qekj2BDGTjuqEDXKqMfBSwo34QJBANRF3eEmjMRVa8uz50lVUrC5W/w+xcnOo+YEulotvMabbJ2uTt7c95KXF3s/BwC70E/QQnoX4veX1nUfAyxojsUCQQDQ9HaGPeqKBdP/eNDN48QgiCAu37lX2SdzbV9b2GmJ0/kUpFUMKTIW+6E9vy3jsy+x1SYEJoFRtrHxOuLJXPfjAkBLuVzMm+bthxd8RIJGi9SCzKz+0BnYwwl+3cLotpY4N2vI2Ey0fhknRxUOV06VOWq3fjCow2qpLpZHk7ebGPWNAkEAl1nbWBUVA3CBRDTQmbF26FFxbkJiz7zdFICOZ8pzd3/wlQELEUntnDQbcMw/gwTRcTAA31S8quF4NlD+3/fVHQJBAKsE7aLZz7vtOKQipDSnTHI6unAb62SboeMHE1ZE666+Bcmp1KRkhZ5JHYj0irWx18Gn+e5LY7NcxylWMb7/d0k=";

	// 测试用户名 -需更换正式签名
	private static String terminalName = "test";

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
		String url = "http://localhost:9001/bosspaybill_war_exploded/order/quotaOrder?" + content + "&signature=" + signature;
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
		String url = "http://localhost:9001/bosspaybill_war_exploded/order/queryOrder?customerOrderId=" + customerOrderId
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
