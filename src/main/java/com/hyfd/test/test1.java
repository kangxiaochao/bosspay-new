package com.hyfd.test;

public class test1 {

	// 测试密钥-需更换正式密钥
//	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALLXxOWFLgJ/6KCVzxKb2WHDDk9l+Iau7G2Z+9RC5Ia/x2gdZUY1z5a0NLOaVlumjj9eBxUkE8kKCwIPVB8xPJCVhYe6+GGQT9Ar2c8qmbRo4YqFBrnB0pGG8LXXI8/+LViOc6z/hPCUjYgFQKynmmep1Tg+IJSSG0v2UhXB/sSZAgMBAAECgYEAlX2wkt4RCHvceqbesUJeoc5G3u1woTwEWtUU4GeN2GjkCM5Rgi+mtuUpDFvdBb0iOCujpfNDKo/fhbhEa9JfOWDT17QSXFXyb4pZwag9H1zu0SJfMCqRZCsufVVhHn6Pfy+tqOCNgjXBAkXraxwq7pV3md13bPj2EqUkXaAof5ECQQDqtDGT5If94HSzDeWyUWdovgdU2bR40pKtYATBgFDPjNvWygT3aiJEB0D9gLRwqIh+9cnpQ9aAlT2jpzCqTk7dAkEAwxICr5R9lF22wOlHLjzCjHyXfzAil3WpfMRvZjhr42AKeNedPro2g+oGmhKKFZjUpXZ2CFQk5j2ehqzOqxWq7QJAdClC8GOHOwvANm6GZL8NYzXKAyDMxY/SsKR5Nhv/4vlgROovkxSgaPL+I3lz1N5U0CFuEVlV7MXwf/Lbjy66sQJAB4fQdrrCxlF/1p6q0swhro64wn43N6WhL2Hd8xJGh/aTMOsR9PP3WsxUCOqiTgciXmjeUZ/99K14Szasm8owWQJBALriHVbH5J/vdadsdfR5yC0ULPDiLqQnkcUABMd0CkCXI8HPISiaYa6PKCf2U+UXfN29udQkNyIycynD1bjWH4s=";
	private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK1DeSXuGil9je1MAaJ3/4RfAFyaXpP1RATTcjlQUbGcAENgkagp/i1ONTRe9mbGYult1BrK3/nDO1b8KzgKbHypXX+5+NR35mLwabYvxj+D8EZLtD1gx+loLDGDDkr/1iZlHnoJyd8kNyzjX+5KKMDfIM7fc/waDk7kST8+QquvAgMBAAECgYAGa0WFvjiB6QDlFbfJySBSSyK8S4vNd3y5oQvOS8d7CQiJdn5u+H9ONmOKOSpvzFHFzCfbhkCeBDmik10WimxScmjLJhgRC4T4HF0SHN6970DChEqH7dbGxkzmrNXsZgMsiHM8z474nj6RfzB3qekj2BDGTjuqEDXKqMfBSwo34QJBANRF3eEmjMRVa8uz50lVUrC5W/w+xcnOo+YEulotvMabbJ2uTt7c95KXF3s/BwC70E/QQnoX4veX1nUfAyxojsUCQQDQ9HaGPeqKBdP/eNDN48QgiCAu37lX2SdzbV9b2GmJ0/kUpFUMKTIW+6E9vy3jsy+x1SYEJoFRtrHxOuLJXPfjAkBLuVzMm+bthxd8RIJGi9SCzKz+0BnYwwl+3cLotpY4N2vI2Ey0fhknRxUOV06VOWq3fjCow2qpLpZHk7ebGPWNAkEAl1nbWBUVA3CBRDTQmbF26FFxbkJiz7zdFICOZ8pzd3/wlQELEUntnDQbcMw/gwTRcTAA31S8quF4NlD+3/fVHQJBAKsE7aLZz7vtOKQipDSnTHI6unAb62SboeMHE1ZE666+Bcmp1KRkhZ5JHYj0irWx18Gn+e5LY7NcxylWMb7/d0k=";

	// 测试用户名 -需更换正式签名
	private static String terminalName = "test";

	public static void main(String[] args) {
		/**第一步-私钥生成**/
		//私钥生成：生成的私钥和公钥都放在设置的目录里面,请将公钥和私钥发送给商务经理
		HaoBaiKey.genKeyPair("d:\\");

		/**第二步-参数封装**/
		String orderId = "047d4dcdw2sfazc350039b98883d6cb0"; //订单编号必须32位
		String phone = "17000000000"; // 充值号码
		String orderType = "2"; // 1:流量 2:话费
		String fee = "10"; // 充值金额 流量:填写流量值单位M,话费:填写充值金额,单位分
		String callBack = "http://www.baidu.com"; // 订单状态回调地址
		String type = "nation"; // 充值类型 nation:全国 province:省内
		String timeMillis = "20220508180756000"; //请求时间戳

		/**第三步-生成URL地址**/
		System.out.println(test1.quotaOrder(orderId, phone, orderType, type, fee, callBack, timeMillis));
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
