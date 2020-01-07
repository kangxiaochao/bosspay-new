package com.hyfd.deal.Flow;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hyfd.common.GenerateData;
import com.hyfd.common.https.HttpsUtils;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolSHA2_Codec;
import com.hyfd.deal.BaseDeal;

public class SdllptFlowDeal implements BaseDeal {

	Logger log = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> deal(Map<String, Object> order) {
		Map<String,Object> map = new HashMap<String,Object>();
		int flag = -1;
		try{
			String phone = (String) order.get("phone");//手机号
			String spec = new Double(order.get("value")+"").intValue() + "";//流量包大小
			
			Map<String,Object> channelMap = (Map<String, Object>) order.get("channel");//获取通道参数
			String appKey = String.valueOf(channelMap.get("appKey")); // appKey
			String appSecret = String.valueOf(channelMap.get("appSecret")); // appSecret
			String authInterfaceUrl = String.valueOf(channelMap.get("authInterfaceUrl")); // 认证地址
			String productsInterfaceUrl = String.valueOf(channelMap.get("productsInterfaceUrl"));// 获取产品列表地址
			String chargeInterfaceUrl = String.valueOf(channelMap.get("chargeInterfaceUrl")); // 流量充值地址
			
			// 生成自己的id，供回调时查询数据使用
			// 平台订单号
            String curids = order.get("orderId") + "";
            if (null == curids || "".equals(curids) || "null".equals(curids))
            {
            	curids = DateTimeUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS") + phone + GenerateData.getIntData(9, 4); // 充值流水号
            }
			map.put("orderId", curids);
			
			String token = "";
			String productId = ""; // 充值产品编码
			String errorMessage = "";
			
			boolean isOk = true; // 请求是否正常
			
			// 1.进行认证
			String authRequestReturnXml = authInterfaceRequest(appKey, appSecret,authInterfaceUrl);
			if (null == authRequestReturnXml) {
				errorMessage = "山东统付流量[认证]请求:超时,curids[" + curids + "]!";
				log.error(errorMessage);
				isOk = false;
			} else {
				// 2.解析xml文件,获取Token信息
				token = getToken(authRequestReturnXml);
			}
			
			// 3.如果[认证]请求不超时
			if (isOk) {
				// 4.产品查询接口
				String productsRequestReturnXml = productsInterfaceRequest(
						appSecret, token, productsInterfaceUrl);
				if (null == productsRequestReturnXml) {
					errorMessage = "山东统付流量[产品查询]请求:超时,curids[" + curids + "]!";
					log.error(errorMessage);
					isOk = false;
				} else {
					// 5.根据充值的流量大小获取充值产品编码
					productId = getProductCodeByOrderSize(productsRequestReturnXml,	spec);
					if ("".equals(productId)) {
						errorMessage = "山东统付流量[产品查询]请求：获取产品列表发生异常,curids[" + curids	+ "]!";
						log.error(errorMessage);
						isOk = false;
					}
				}
			}
			
			// 3.如果[认证]请求不超时
			if (isOk) {
				// 4.产品查询接口
				String productsRequestReturnXml = productsInterfaceRequest(appSecret, token, productsInterfaceUrl);
				if (null == productsRequestReturnXml) {
					errorMessage = "山东统付流量[产品查询]请求:超时,curids[" + curids + "]!";
					log.error(errorMessage);
					isOk = false;
				} else {
					// 5.根据充值的流量大小获取充值产品编码
					productId = getProductCodeByOrderSize(productsRequestReturnXml,
							spec);
					if ("".equals(productId)) {
						errorMessage = "山东统付流量[产品查询]请求：获取产品列表发生异常,curids[" + curids	+ "]!";
						log.error(errorMessage);
						isOk = false;
					}
				}
			}
			
			// 6.如果[产品查询]请求不超时
			if (isOk) {
				// 7.充值请求
				String chargeRequestReturnXml = chargeInterfaceRequest(appSecret,token, chargeInterfaceUrl, phone, productId, curids);
				log.error("山东统付流量[充值]请求,curids[" + curids + "]返回信息["	+ chargeRequestReturnXml + "]");
				Map<String, String> chargeResultMap = null;
				if (null == chargeRequestReturnXml) {
					errorMessage = "山东统付流量[充值请求]:超时,curids[" + curids + "]!";
					log.error(errorMessage);
				} else {
					// 8.获取充值结果
					chargeResultMap = getChargeResult(chargeRequestReturnXml);
					String upids = chargeResultMap.get("systemNum");

					if (null != upids) {
						flag = 1;
						map.put("resultCode", "success");
						map.put("providerOrderId", upids);
					} else {
						map.put("resultCode", "error");
						flag = 0;
					}
				}
			}
		} catch(Exception e){
			log.error("山东统付流量充值出错"+e.getMessage()+"||"+MapUtils.toString(order));
		}
		map.put("status", flag);
		return map;
	}
	
	/**
	 * <h5>功能:</h5>进行认证,获取认证请求返回信息 post
	 * 
	 * @author zhangpj @date 2016年8月24日
	 * @param appKey
	 * @param appSecret
	 * @param url
	 *            认证地址
	 * @return
	 */
	public String authInterfaceRequest(String appKey, String appSecret,
			String url) {
		// 1.1获取认证接口的请求信息主体
		String bodyXml = getAuthBodyXml(appKey, appSecret);
		// System.out.println("1.1发起认证请求信息\r["+bodyXml+"]");
		String returnInfo = null;
		// 1.2发起请求认证
		try {
			returnInfo = HttpsUtils.post(url, bodyXml, "application/xml",
					"utf-8", 10000, 10000);
			log.error("山东统付流量请求:" + url + "\n" + "bodyXml:" + bodyXml);
		} catch (ConnectTimeoutException e) {
			log.error("山东统付流量请求,连接超时:" + url + "\n:bodyXml:" + bodyXml);
		} catch (SocketTimeoutException e) {
			log.error("山东统付流量请求,Socket连接超时:" + url + "\n:bodyXml:" + bodyXml);
		} catch (Exception e) {
			log.error("山东统付流量请求,其他异常:" + url + "\n:bodyXml:" + bodyXml);
		}
		// System.out.println("1.4发起认证请求返回信息\r["+bodyXml+"]");
		return returnInfo;
	}

	/**
	 * <h5>功能:</h5>产品查询接口 get
	 * 
	 * @author zhangpj @date 2016年8月24日
	 * @param appSecret
	 * @param token
	 *            进行认证后获取的认证信息
	 * @param url
	 *            查询地址
	 * @return
	 */
	public String productsInterfaceRequest(String appSecret,
			String token, String url) {
		// 2.1获取HTTP-X-4GGOGO-Signature的头信息,签名内容为SHA256Hex(appSecret)
		String signatrue = getSHA256HexSignature(appSecret);
		String returnInfo = null;
		try {
			returnInfo = HttpsUtils.doGet(url, null, token, signatrue,
					"application/xml", "utf-8");
			log.error("山东统付流量请求:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (ConnectTimeoutException e) {
			log.error("山东统付流量请求,连接超时:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (SocketTimeoutException e) {
			log.error("山东统付流量请求,Socket连接超时:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (Exception e) {
			log.error("山东统付流量请求,其他异常:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		}
		return returnInfo;
	}

	/**
	 * <h5>功能:</h5>充值请求 post
	 * 
	 * @param appSecret
	 * @param token
	 * @param url
	 *            充值请求地址
	 * @param mobile
	 *            充值号码
	 * @param productId
	 *            充值产品编码
	 * @param serialNum
	 *            平台充值请求流水号
	 * @return String 请求信息主体
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	public String chargeInterfaceRequest(String appSecret,
			String token, String url, String mobile, String productId,
			String serialNum) {
		// 3.1获取认证接口的请求信息主体
		String bodyXml = getChargeBodyXml(mobile, productId, serialNum);
		// log.error("3.1发起认证请求信息\r["+bodyXml+"]");

		// 3.2获取HTTP-X-4GGOGO-Signature的头信息,post请求签名内容为SHA256Hex(bodyXml+appSecret)
		String signatrue = getSHA256HexSignature(bodyXml + appSecret);
		// 3.3发起充值请求
		String returnInfo = null;
		try {
			returnInfo = HttpsUtils.post(url, bodyXml, token, signatrue,
					"application/xml", "utf-8", 10000, 10000);
			log.error("山东统付流量请求:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (ConnectTimeoutException e) {
			log.error("山东统付流量请求,连接超时:" + url + "\n:bodyXml:" + bodyXml + "\n"
					+ "token:" + token + "\nsignatrue:" + signatrue);
		} catch (SocketTimeoutException e) {
			log.error("山东统付流量请求,Socket连接超时:" + url + "\n:bodyXml:" + bodyXml
					+ "\n" + "token:" + token + "\nsignatrue:" + signatrue);
		} catch (Exception e) {
			log.error("山东统付流量请求,其他异常:" + url + "\n:bodyXml:" + bodyXml + "\n"
					+ "token:" + token + "\nsignatrue:" + signatrue);
		}
		return returnInfo;
	}

	/**
	 * <h5>功能:</h5>订单查询接口,先根据平台流水号查询,如果平台流水号不存在再根据上游流水号查询
	 * 
	 * @param appSecret
	 * @param token
	 * @param url01
	 *            根据上游流水号查询订单地址
	 * @param url02
	 *            根据平台流水号查询订单地址
	 * @param systemNun
	 *            上游流水号
	 * @param serialNum
	 *            平台流水号
	 * @return String
	 * 
	 * @author zhangpj @date 2016年8月25日
	 */
	public String chargeQueryRequest(String appSecret, String token,
			String url01, String url02, String systemNun, String serialNum) {
		// 2.1获取HTTP-X-4GGOGO-Signature的头信息,签名内容为SHA256Hex(appSecret)
		String returnInfo = "";
		String url = "";
		if (null == serialNum || "".equals(serialNum)) {
			url = url01 + systemNun + ".html";
		} else {
			url += url02 + serialNum + ".html";
		}
		String signatrue = getSHA256HexSignature(appSecret);
		try {
			returnInfo = HttpsUtils.doGet(url, null, token, signatrue,
					"application/xml", "utf-8");
			log.error("山东统付流量请求:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (ConnectTimeoutException e) {
			log.error("山东统付流量请求,连接超时:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (SocketTimeoutException e) {
			log.error("山东统付流量请求,Socket连接超时:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		} catch (Exception e) {
			log.error("山东统付流量请求,其他异常:" + url + "\n" + "token:" + token
					+ "\nsignatrue:" + signatrue);
		}
		return returnInfo;
	}

	/**
	 * <h5>功能:</h5>获取认证接口的请求信息主体
	 * 
	 * @author zhangpj @date 2016年8月23日
	 * @param appKey
	 * @param appSecret
	 * @return String
	 */
	public String getAuthBodyXml(String appKey, String appSecret) {
		String requestTime = getUTCTime();

		// 签名内容为 SHA256Hex(appKey+requestTime+appSecret)
		String sign = getSHA256HexSignature(appKey + requestTime + appSecret);

		StringBuffer bodyXml = new StringBuffer();
		bodyXml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		bodyXml.append("<Request>");
		bodyXml.append("<Datetime>").append(requestTime).append("</Datetime>");
		bodyXml.append("<Authorization>");
		bodyXml.append("<AppKey>").append(appKey).append("</AppKey>");
		bodyXml.append("<Sign>").append(sign).append("</Sign>");
		bodyXml.append("</Authorization>");
		bodyXml.append("</Request>");

		return bodyXml.toString();
	}

	/**
	 * <h5>功能:</h5>获取充值请求接口的请求信息主体
	 * 
	 * @param mobile
	 *            充值号码
	 * @param productId
	 *            充值产品编码
	 * @param serialNum
	 *            平台充值请求流水号
	 * @return String 请求信息主体
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	private String getChargeBodyXml(String mobile, String productId,
			String serialNum) {
		String requestTime = getUTCTime();

		StringBuffer bodyXml = new StringBuffer();
		bodyXml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		bodyXml.append("<Request>");
		bodyXml.append("<Datetime>").append(requestTime).append("</Datetime>");
		bodyXml.append("<ChargeData>");
		bodyXml.append("<Mobile>").append(mobile).append("</Mobile>");
		bodyXml.append("<ProductId>").append(productId).append("</ProductId>");
		bodyXml.append("<SerialNum>").append(serialNum).append("</SerialNum>");
		bodyXml.append("</ChargeData>");
		bodyXml.append("</Request>");

		return bodyXml.toString();
	}

	/**
	 * <h5>功能:</h5>进行SHA256Hex加密
	 * 
	 * @author zhangpj @date 2016年8月23日
	 * @param str
	 * @return
	 */
	private String getSHA256HexSignature(String str) {
		String signature = "";
		try {
			signature = ToolSHA2_Codec.encodeSHA256Hex(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signature;
	}

	/**
	 * <h5>功能:</h5>获取UTC时间 格式如:2016-08-23T10:02:16.748+08:00
	 * 
	 * @author zhangpj @date 2016年8月23日
	 * @return
	 */
	public String getUTCTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String utcTime = sdf.format(new Date());
		return utcTime;
	}

	/**
	 * <h5>功能:</h5>解析xml文件,获取Token信息
	 * 
	 * @author zhangpj @date 2016年8月23日
	 * @param xml
	 * @return
	 */
	public String getToken(String xml) {
		Document doc = null;
		String Token = "";
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element authorizationElement = rootElt.element("Authorization"); // 获取Authorization枝节点
			Token = authorizationElement.elementTextTrim("Token"); // 获取Token叶子节点
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Token;
	}

	/**
	 * <h5>功能:</h5>根据充值的流量大小获取充值产品编码
	 * 
	 * @param xml
	 * @param orderSize
	 * @return String 充值产品编码
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	private String getProductCodeByOrderSize(String xml, String orderSize) {
		String productId = "";
		List<Map<String, String>> productList = getProductList(xml);
		for (Map<String, String> map : productList) {
			String size = (String) map.get("size");
			String name = map.get("productName");
			if (size.equals(orderSize) && name.contains("全省")) {
				productId = map.get("productId");
				break;
			}
		}
		return productId;
	}

	/**
	 * <h5>功能:</h5>根据产品查询接口返回的xml获取产品列表集合
	 * 
	 * @param xml
	 * @return List<Map<String, String>> 产品列表集合
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	private List<Map<String, String>> getProductList(String xml) {
		Document doc = null;
		List<Map<String, String>> productList = new ArrayList<Map<String, String>>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element productsElement = rootElt.element("Products"); // 获取Products枝节点
			List<Element> productElementList = productsElement
					.elements("Product"); // 获取Product枝节点集合

			// 循环处理枝节点集合
			for (Element element : productElementList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("productId", element.elementText("ProductId"));
				map.put("productName", element.elementText("ProductName"));
				map.put("cost", element.elementText("Cost"));
				map.put("size", element.elementText("Size"));
				productList.add(map);
			}
			return productList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("山东流量解析产品列表xml出錯");
		return null;
	}

	/**
	 * <h5>功能:</h5>获取充值结果
	 * 
	 * @param xml
	 * @return Map<String, String>
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	private Map<String, String> getChargeResult(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element authorizationElement = rootElt.element("ChargeData"); // 获取ChargeData枝节点

			String datetime = rootElt.elementTextTrim("Datetime");
			String serialNum = authorizationElement
					.elementTextTrim("SerialNum"); // 获取SerialNum叶子节点
			String systemNum = authorizationElement
					.elementTextTrim("SystemNum"); // 获取SystemNum叶子节点

			map.put("datetime", datetime); // 接收时间
			map.put("serialNum", serialNum);// EC 平台充值请求流水号
			map.put("systemNum", systemNum);// 流量平台充值业务流水号
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * <h5>功能:</h5>获取充值查询结果,如果返回值是null则代表没有查询到订单
	 * 
	 * @param xml
	 * @return Map<String, String>
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	public Map<String, String> getChargeQueryResult(String xml) {
		Document doc = null;
		Map<String, String> map = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			String datetime = rootElt.elementTextTrim("Datetime"); // 获取datetime叶子节点
			Element recordElement = rootElt.element("Records")
					.element("Record"); // 获取Record枝节点
			if (null != recordElement) {
				map = new HashMap<String, String>();
				String enterpriseId = recordElement
						.elementTextTrim("EnterpriseId"); // 获取SerialNum叶子节点
				String productId = recordElement.elementTextTrim("ProductId"); // 获取SystemNum叶子节点
				String mobile = recordElement.elementTextTrim("Mobile"); // 获取SystemNum叶子节点
				String status = recordElement.elementTextTrim("Status"); // 获取SystemNum叶子节点
				String description = recordElement
						.elementTextTrim("Description"); // 获取SystemNum叶子节点
				String chargeTime = recordElement.elementTextTrim("ChargeTime"); // 获取SystemNum叶子节点

				map.put("datetime", datetime);
				map.put("enterpriseId", enterpriseId);// 企业 ID
				map.put("productId", productId);// 产品编码
				map.put("mobile", mobile);// 充值电话号码
				map.put("status", status);// 充值结果状态,充值结果状态 1:订单已创建 2:已发送充值请求
											// 3:充值成功 4:充值失败
				map.put("description", description);// 充值结果描述信息
				map.put("chargeTime", chargeTime);// 充值时间
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * <h5>功能:</h5>获取上游回调信息处理结果
	 * 
	 * @param xml
	 * @return
	 * 
	 * @author zhangpj @date 2016年8月24日
	 */
	public Map<String, String> getBackRequestResult(String xml) {
		Document doc = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element recordElement = rootElt.element("Record"); // 获取Record枝节点

			String serialNum = recordElement.elementTextTrim("SerialNum"); // 获取SerialNum叶子节点
			String systemNum = recordElement.elementTextTrim("SystemNum"); // 获取SystemNum叶子节点
			String mobile = recordElement.elementTextTrim("Mobile"); // 获取SystemNum叶子节点
			String status = recordElement.elementTextTrim("Status"); // 获取SystemNum叶子节点
			String description = recordElement.elementTextTrim("Description"); // 获取SystemNum叶子节点
			String chargeTime = recordElement.elementTextTrim("ChargeTime"); // 获取SystemNum叶子节点

			map.put("serialNum", serialNum);// 企业 ID
			map.put("systemNum", systemNum);// 产品编码
			map.put("mobile", mobile);// 充值电话号码
			map.put("status", status);// 充值结果状态,充值结果状态 1:订单已创建 2:已发送充值请求 3:充值成功
										// 4:充值失败
			map.put("description", description);// 充值结果描述信息
			map.put("chargeTime", chargeTime);// 充值时间
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
