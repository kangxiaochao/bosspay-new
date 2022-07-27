package com.hyfd.service.mp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.DefaultTransactionDefinition;
import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.GenerateData;
import com.hyfd.common.ValidatePhone;
import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.IPUtil;
import com.hyfd.common.utils.MD5;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.common.utils.ToolDateTime;
import com.hyfd.common.utils.ToolValidator;
import com.hyfd.common.utils.XiChengAESUtils;
import com.hyfd.common.utils.XmlUtils;
import com.hyfd.dao.mp.AgentAccountChargeDao;
import com.hyfd.dao.mp.AgentAccountDao;
import com.hyfd.dao.mp.AgentBillDiscountDao;
import com.hyfd.dao.mp.AgentChannelRelDao;
import com.hyfd.dao.mp.AgentDao;
import com.hyfd.dao.mp.BaseBillDiscountDetailDao;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.ExceptionOrderDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.OrderPathRecordDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderBillDiscountDao;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderBillRechargeGroupDao;
import com.hyfd.dao.mp.ProviderGroupBillRelDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.dao.mp.ProviderProductDao;
import com.hyfd.dao.mp.SubmitOrderDao;
import com.hyfd.deal.Flow.XiChengFlowDeal;
import com.hyfd.rabbitMq.RabbitMqProducer;
import com.hyfd.rabbitMq.SerializeUtil;
import com.hyfd.service.BaseService;

@Service
public class chargeOrderSer extends BaseService {

	Logger log = Logger.getLogger(getClass());

	@Autowired
	AgentDao agentDao;// 代理商

	@Autowired
	PhoneSectionDao phoneSectionDao;// 号段

	@Autowired
	ProviderGroupBillRelDao providerGroupBillRelDao;// 话费运营商组

	@Autowired
	BillPkgDao billPkgDao;// 话费产品

	@Autowired
	AgentAccountDao agentAccountDao;// 代理商余额

	@Autowired
	AgentAccountChargeDao agentAccountChargeDao;

	@Autowired
	AgentBillDiscountDao agentBillDiscountDao;// 代理商话费折扣

	@Autowired
	ProviderBillDiscountDao providerBillDiscountDao;// 运营商折扣

	@Autowired
	ProviderBillDispatcherDao providerBillDispatcherDao;// 运营商话费分销商关联表

	@Autowired
	ProviderPhysicalChannelDao providerPhysicalChannelDao;// 通道的物理参数表

	@Autowired
	BaseBillDiscountDetailDao baseBillDiscountDetailDao;// 话费折扣模板

	@Autowired
	ProviderBillRechargeGroupDao providerBillRechargeGroupDao;// 话费复充通道组

	@Autowired
	SubmitOrderDao submitOrderDao;// 提交订单

	@Autowired
	OrderDao orderDao;// 订单表

	@Autowired
	ExceptionOrderDao exOrderDao;

	@Autowired
	ProviderProductDao providerProductDao;// 上家产品表

	@Autowired
	OrderPathRecordDao orderPathRecordDao;// 复充轨迹记录表

	@Autowired
	RabbitMqProducer rabbitMqProducer;// 消息队列生产者

	@Autowired
	AgentChannelRelDao agentChannelRelDao;
	
	@Autowired
	IpSer ipSer;
	
	@Autowired
	RabbitmqPhysicalChannelSer rabbitmqPhysicalChannelSer;
	
	@Autowired
	AgentBillDiscountSer agentBillDiscountSer; // 代理商话费折扣Service

	@Autowired
	AgentAccountSer agentAccountService;

	@Autowired
	ExceptionOrderDao exceptionOrderDao;//异常订单

	@Autowired
	ProviderAccountSer providerAccountSer;//上家余额Service

	@Autowired
	AgentCallbackSer agentCallbackSer;

	@Autowired
	private DataSourceTransactionManager txManager;


	/**
	 * 查询订单状态
	 * 
	 * @author lks 2016年12月19日上午9:29:08
	 * @param request
	 * @return
	 */
	public String queryOrder(HttpServletRequest request) {
		// 根据代理商验证ip地址是否合法
		boolean flag = ipSer.validateIp(request);
		if (flag == false) {
			JSONObject json = new JSONObject();
			json.put("code", 24);
			json.put("message", "IP地址不合法");
			return json.toJSONString();
		}
		String status = "";
		Map<String, Object> param = getMaps(request);
		String agentName = (String) param.get("terminalName");// 客户名
		String agentOrderId = (String) param.get("customerOrderId");// 客户订单号
		Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
		if (agent == null) {
			JSONObject json = new JSONObject();
			json.put("code", 1001);
			json.put("message", "代理商不存在");
			return json.toJSONString();
		}
		String agentId = (String) agent.get("id");// 代理商ID
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("agentId", agentId);
		queryParam.put("agentOrderId", agentOrderId);
		List<Map<String, Object>> orders = orderDao.selectByQueryOrder(queryParam);
		if (orders.size() < 1) {
			List<Map<String, Object>> exOrderList = exOrderDao.selectByQueryOrder(queryParam);
			if (exOrderList.size() < 1) {
				JSONObject json = new JSONObject();
				json.put("code", 1002);
				json.put("message", "该笔订单不存在，请找商务确认");
				return json.toJSONString();
			} else {
				Map<String, Object> order = exOrderList.get(0);
				status = "1";
				JSONObject json = new JSONObject();
				json.put("code", 0);
				json.put("message", "success");
				JSONObject data = new JSONObject();
				data.put("orderId", order.get("orderId"));
				data.put("customerOrderId", agentOrderId);
				data.put("mobile", order.get("phone"));
				data.put("basePrice", order.get("fee"));
				data.put("actualPrice", "");// TODO 折扣价
				data.put("status", status);
				json.put("data", data);
				return json.toJSONString();
			}
		} else {
			Map<String, Object> order = orders.get(0);
			status = (String) order.get("status");// 订单状态
			JSONObject json = new JSONObject();
			json.put("code", 0);
			json.put("message", "success");
			JSONObject data = new JSONObject();
			data.put("orderId", order.get("orderId"));
			data.put("customerOrderId", agentOrderId);
			data.put("mobile", order.get("phone"));
			data.put("basePrice", order.get("fee"));
			data.put("actualPrice", "");// TODO 折扣价
			if (status.equals("3")) {
				status = "0";
			} else if (status.equals("1")||status.equals("0")) {
				status = "1";
			} else if (status.equals("2") || status.equals("4")) {
				status = "2";
			}
			data.put("status", status);
			json.put("data", data);
			return json.toJSONString();
		}
	}

	/**
	 * 提交订单
	 * 
	 * @author lks 2016年12月6日上午10:02:04
	 * @param request
	 * @return
	 */
	public String submitOrder(HttpServletRequest request) {
		int code = -1;
		Map<String, Object> param = getMaps(request);
		
		// 根据代理商验证ip地址是否合法
		boolean flag = ipSer.validateIp(request);
		if (flag) {
			if (MapUtils.checkEmptyValue(param) && checkParamIntact(param)) {// 验证参数
				if (checkSign(param)) {// 验证签名
					if(orderDao.checkAgentOrderId(param.get("customerOrderId")+"") > 0){
						code = 10;//订单号重复
					}else{
						code = createOrder(param);//创建订单
					}
				} else {
					code = 2;
				}
			} else {
				code = 1;// 参数不全
			}
		} else {
			String ipAddr = IPUtil.getIpAddr(request);
			param.put("ipAddr", ipAddr);
			code = 12;
		}
		
		JSONObject json = new JSONObject();
		String message = "";
		int resultCode = -1;
		switch (code) {
		case 0:
			resultCode = 0;
			message = "提交成功";
			break;
		case 1:
			resultCode = 1;
			message = "参数不正确";
			break;
		case 2:
			resultCode = 4;
			message = "签名错误";
			break;
		case 3:
			resultCode = 6;
			message = "该代理商不可用";
			break;
		case 4:
			resultCode = 3;
			message = "不支持该手机号段";
			break;
		case 5:
			resultCode = 18;
			message = "不允许充值该运营商号段，请联系业务人员获取权限";
			break;
		case 6:
			resultCode = 19;
			message = "无法充值该产品";
			break;
		case 7:
			resultCode = 15;
			message = "余额不足";
			break;
		case 8:
			resultCode = 20;
			message = "订单提交出现异常";
			break;
		case 9:
			resultCode = 21;
			message = "折扣已变更，请联系商务";
			break;
		case 10:
            resultCode = 22;
            message = "订单号重复，请勿重复提交";
            break;
		case 11:
            resultCode = 23;
            message = "获取不到代理商的扣款折扣";
            break;
		case 12:
			resultCode = 24;
			message = "IP地址不合法";
			break;
		case 13:
			resultCode = 25;
			message = "同一号码同一面值1分钟内不允许充值多次";
			break;
		default:
			code = -1;
			resultCode = -1;
			message = "状态返回值异常";
			break;
		}
		param.put("code", code);
		param.put("message", message);
		saveSubmitOrder(param);// 保存订单//保存下家提交的订单.
		json.put("code", resultCode);
		json.put("message", message);
		return json.toJSONString();
	}

	/**
	 * 保存下家提交的订单
	 * 
	 * @author lks 2017年2月7日下午2:03:38
	 * @param param
	 */
	public void saveSubmitOrder(Map<String, Object> param) {
		try {
			String agentName = (String) param.get("terminalName");// 客户名
			String orderId = (String) param.get("customerOrderId");// 客户订单号
			String submitParam = MapUtils.toString(param);// 提交参数
			String phone = (String) param.get("phoneNo");// 手机号
			String bizType = (String) param.get("orderType");// 订单类型
			String submitDate = DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");// 提交时间
			String ipAddr = (String) param.get("ipAddr");// 新增:订单请求ip地址
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", UUID.randomUUID().toString().replace("-", ""));
			map.put("agentName", agentName);
			map.put("orderId", orderId);
			map.put("submitParam", submitParam);
			if (param.get("code").equals("12")) {
				map.put("resultCode", param.get("code") + ":" + param.get("message")+"["+ipAddr+"]");
			} else {
				map.put("resultCode", param.get("code") + ":" + param.get("message"));
			}
			map.put("phone", phone);
			map.put("bizType", bizType);
			map.put("submitDate", submitDate);
			submitOrderDao.insertSelective(map);
		} catch (Exception e) {
			log.error("保存提交订单出错" + ExceptionUtils.getExceptionMessage(e));
		}
	}

	/**
	 * 创建订单
	 * 
	 * @author lks 2016年12月6日下午1:48:42
	 * @param param
	 * @return
	 */
	public int createOrder(Map<String, Object> param) {
		try {
			// 从参数map中获取参数
			String agentName = (String) param.get("terminalName");// 代理商名称
			String agentOrderId = (String) param.get("customerOrderId");// 下家订单号
			String price = (String) param.get("spec");// 充值金额
			String phoneNo = (String) param.get("phoneNo");// 手机号
			String bizType = (String) param.get("orderType");// 订单类型
			String billType = (String) param.get("scope");// 充值范围
			//判断订单号是否重复
            int caFlag = orderDao.checkAgentOrderId(agentOrderId);
            if(caFlag > 0){
            	return 10;// 订单号重复
            }
            
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("phoneNo", phoneNo);
            paramMap.put("fee", Integer.valueOf(price)/100);
            // 验证手机号码是否允许充值(60秒内不允许重复充值)
            int cpFlag = orderDao.checkPhone(paramMap);
            if(cpFlag > 0){
            	return 13;//1分钟禁止重复充值
            }
//            boolean vflag = ValidatePhone.valiPhone(phoneNo + "_" +price, 60);
//            if(!vflag){
//            	return 13;//1分钟禁止重复充值
//            }
			// 获取手机号相关信息
			String section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
			// TODO 号段从缓存中获取
			Map<String, Object> sectionMap = phoneSectionDao.selectBySection(section);
			if (sectionMap == null) {
				return 4;// 不支持该手机号段
			}
			String providerId = (String) sectionMap.get("provider_id");// 运营商ID
			param.put("providerId", providerId);
			String provinceCode = (String) sectionMap.get("province_code");// 省份代码
			String cityCode = (String) sectionMap.get("city_code");// 城市代码
			String carrierType = (String) sectionMap.get("carrier_type");	// 号段类型,1普卡 7物联网
			// 获取代理商对象
			Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
			boolean agentFlag = true;// 声明接收的flag
			if (!checkAgent(agentFlag, agent)) {
				return 3;// 该客户不可用
			}
			// 判断该代理商是否可用
			String agentId = (String) agent.get("id");// 代理商id
			param.put("agentId", agentId);
			String billGroupId = (String) agent.get("bill_group_id");// 话费运营商组Id
			if (!checkProviderForAgent(bizType, billGroupId, providerId, provinceCode, cityCode)) {
				return 5;// 不允许充值该运营商号段，请联系业务人员获取权限
			}
			Map<String, Object> pkg = getPkgForAgent(agentId, bizType, providerId, price, provinceCode);//kpg:代理商可用话费包.
			if (pkg == null) {
				return 6;// 无法充值该产品
			}
//			price = pkg.get("value") + "";// 金额
			price = Double.parseDouble(price) / 100 + "";
			if (!checkBalance4Agent(agentId, price)) {
				// TODO 缓存余额不足的订单
				return 7;// 余额不足
			}
			// 获取扣款list
			// 扣款逻辑修改为以下情况：
			//  只扣充值代理商的余额
			//  充值成功后为该代理商的上级增加利润
			//  充值失败则只需要为该代理商退款
			//  上级代理商为下级代理商加款需要将余额实际扣除
			List<Map<String, Object>> moneyList = new ArrayList<Map<String, Object>>();
			String pkgId = (String) pkg.get("id");
//			moneyList = getChargeList(0, bizType, moneyList, agent, pkgId, providerId, provinceCode, cityCode, price);
			//只计算当前代理商的扣款
			moneyList = getCurrentAgentChargeList(0, bizType, moneyList, agent, pkgId, providerId, provinceCode, cityCode, price);
			if(null == moneyList){
				log.error("获取不到代理商的扣款折扣，订单号为" + agentOrderId);
				return 11;// 订单提交出现异常
			}else if (moneyList.size() < 1) {
				log.error("扣款数据为空，订单号为" + agentOrderId);
				return 8;// 订单提交出现异常
			}
			// 生成订单对象
			Map<String, Object> order = getOrderObj(param, pkg);// 获取订单对象
			order.put("pkgId", pkgId);// 将产品Id放入order对象，为了上家扣款
			billType = billType.equals("nation") ? "2" : "1";
			order.put("billType", billType);
			order.put("carrierType", carrierType);// 号段类型,1普卡 7物联网
			// 获取代理商折扣
			Map<String, Object> discountMap = getAgentDiscount(bizType, providerId, pkgId, provinceCode, cityCode,
					agent);
			if (discountMap != null) {
				// 获取下家折扣
				Double agentDiscount = Double.parseDouble(discountMap.get("discount").toString());
				// 获取上家折扣
				Double providerDiscount = getProviceDiscount(bizType, agent, pkgId, providerId, null, provinceCode,carrierType);
				// 判断上家折扣是否大于下家折扣，大于则停止提交---损
				if (providerDiscount != null) {
					if ((agentDiscount - providerDiscount) < 0) {
						log.error("无可用产品" + MapUtils.toString(order));
						return 9;// 是否可单独定义一个code=9的
					}
					order.put("agentDiscountId", discountMap.get("id"));
					order.put("agentDiscount", discountMap.get("discount"));
				} else {
					log.error("未获取到代理商折扣获取" + MapUtils.toString(order));
					return 8;
				}
			}
			//新增Order后手动提交事务，防止出现卡单------------------------------------
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
			TransactionStatus status = txManager.getTransaction(def); // 获得事务状态
			int orderCount = orderDao.insertSelective(order);// 保存订单对象
			txManager.commit(status);
			//--------------------------------------------------------------------
			order.put("pkg", pkg);// 将产品放入order对象
			order.put("provinceCode", provinceCode);// 省份代码，为了上家扣款
			order.put("cityCode", cityCode);// 城市代码，为了上家扣款
			order.put("price", price);
			order.put("phoneNo", phoneNo);//充值号码
			//是否是批充
			if(param.containsKey("isBatch")){
				order.put("isBatch", param.get("isBatch"));
			}
			if (orderCount != 1) {
				// TODO 异常订单自动处理
				log.error("订单插入数据库失败，订单为" + MapUtils.toString(order));
				return 8;// 订单提交出现异常
			}
			Map<String, Object> msgMap = new HashMap<String, Object>();// 消息对象
			msgMap.put("order", order);
			msgMap.put("moneyList", moneyList);// 扣款的list
			// 将请求提交至扣款的消息队列
			rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Money_QueueKey, SerializeUtil.getStrFromObj(msgMap));
		} catch (Exception e) {
			log.error("订单接收出现异常，异常为》》》" + ExceptionUtils.getExceptionMessage(e));
			return 8;// 订单提交出现异常
		}
		return 0;
	}

	/**
	 * 订单处理方法
	 *
	 * @author lks 2016年12月9日上午9:30:29
	 * @param order
	 *            订单对象
	 */
	public void dealOrder(Map<String, Object> order) {
		try {
			String bizType = (String) order.get("bizType");// 获取订单类型
			String providerId = (String) order.get("providerId");// 运营商Id
			String provinceCode = (String) order.get("provinceCode");// 省份
			String pkgId = (String) order.get("pkgId");// 产品包Id
			String agentId = (String) order.get("agentId");
			String carrierType = (String) order.get("carrierType"); // 号段类型,1普卡 7物联网
			String phoneNo = (String) order.get("phoneNo"); // 手机号码
			Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
			// 获取分销商的id和权重
			List<Map<String, Object>> dpList = new ArrayList<Map<String, Object>>();
			if (bizType.equals("2")) {// 话费
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("pkgId", pkgId);
				param.put("providerId", providerId);
				param.put("provinceCode", provinceCode);
				String parentId = agent.get("parent_id") + "";
				if (!parentId.equals("0") && !parentId.trim().equals("")) {
					agentId = getParentsAgentId(parentId);
				}
				Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(agentId);
				String groupId = parentAgent.get("bill_group_id") + "";
				param.put("groupId", groupId);
				param.put("agentId", agentId);

				// carrierType是号段类型,1普卡 7物联网
				if (carrierType.equals("1")) {
					param.put("channelType", 2);	// 普通话费充值通道
				} else{
					param.put("channelType", 3);	// 物联网卡充值通道
				}

				log.info("手机号码["+phoneNo+"]获取可用充值通道信息条件["+JSONObject.toJSONString(param)+"]");
				// 获取可用通道组
				dpList = providerGroupBillRelDao.selectPhysicalChannelExt(param);
				log.info("手机号码["+phoneNo+"]获取可用充值通道信息明细["+JSONObject.toJSONString(dpList)+"]");
			}
			if (dpList.size() < 1) {// 如果没有可充值的通道
				log.error("agentOrderId为" + order.get("agentOrderId") + "的订单未找到支持该产品包的通道");
				boolean flag = dealOrderFail(order, "2", "未找到支持该产品包的通道");
				if (flag) {
					//处理回调下家及上家余额扣除
					orderCallback(order,AgentCallbackSer.CallbackStatus_Fail);
				}
				return;
			}
			// 暂时不用
			// String dispatcherProviderId = weightDeal(dpList);//获取权重处理后的分销商ID
			String channelProvinceCode = dpList.get(0).get("province_code") + "";
			String dispatcherProviderId = dpList.get(0).get("provider_physical_channel_id") + "";// 根据折扣排序去折扣最低的一个
			order.put("channelProvinceCode", channelProvinceCode);
			order.put("dispatcherProviderId", dispatcherProviderId);
			order.put("dealPath", dispatcherProviderId);
			order.put("dealCount", 1);// 处理次数
			orderDao.updateByPrimaryKeySelective(order);
			// 将确定好通道的订单加到队列中去处理
			Map<String, Object> physicalChannel = providerPhysicalChannelDao.selectByPrimaryKey(dispatcherProviderId);
			// String queueName = (String)
			// physicalChannel.get("queue_name");//队列名
			if (physicalChannel != null) {
				order.put("channel", physicalChannel);
				// 获取上家产品id
				Map<String, Object> productParam = new HashMap<String, Object>();
				productParam.put("pkgId", order.get("pkgId"));// 包ID
				productParam.put("providerId", providerId);// 运营商ID
				productParam.put("physicalChannelId", dispatcherProviderId);// 通道ID
				productParam.put("provinceCode", provinceCode);// 省份
				Map<String, Object> providerProduct = providerProductDao.selectProviderProductId(productParam);
				if (providerProduct != null) {
					order.put("providerProductId", providerProduct.get("provider_pkg_id"));
				}
				String providerMark = (String) physicalChannel.get("provider_mark");// 上家的唯一标识
				order.put("providerMark", providerMark);// 将上家的唯一标识加入order对象
				// 将订单对象放到通道处理队列
				if(order.containsKey("isBatch")){
					rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Batch_Channel_QueueKey, SerializeUtil.getStrFromObj(order));
				}else{
//					String providerPhysicalChannelId = physicalChannel.get("id").toString();
//					Map<String, Object> map = new HashMap<String, Object>();
//					map.put("providerPhysicalChannelId", providerPhysicalChannelId);
//					// 获取当前物理通道是否配置备用MQ通道(如果有,则使用备用MQ通道,否则走默认通道)
//					Map<String, Object> rabbitmqDataMap = rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelListAll(map);
//					if (null == rabbitmqDataMap) {
						// 默认通道
						rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Channel_QueueKey, SerializeUtil.getStrFromObj(order));
//					} else {
//						// 备用通道
//						rabbitMqProducer.sendDataToQueue(rabbitmqDataMap.get("mqQueueName").toString(), SerializeUtil.getStrFromObj(order));
//					}
				}
			} else {
				boolean flag = dealOrderFail(order, "2", "providerId=" + dispatcherProviderId + "的通道数据为空");
				log.error("providerId=" + dispatcherProviderId + "的通道数据为空");
				if (flag) {
					//处理回调下家及上家余额扣除
					orderCallback(order,AgentCallbackSer.CallbackStatus_Fail);
				}
				return;
			}
		} catch (Exception e) {
			log.error("处理订单的逻 辑出错" + ExceptionUtils.getExceptionMessage(e));
		}
	}

	/**
	 * 调用上家接口返回结果的处理；实时接口且会进行上家余额扣除和回调下家
	 * @param result
	 */
	public void changeStatus(Map<String,Object> result){
		try{
			@SuppressWarnings("unchecked")
			Map<String,Object> order = (Map<String, Object>) result.get("order");
			int flag = (int) result.get("status");//获取充值的状态
			String orderId = (String) result.get("orderId");//平台订单号
			if(result.containsKey("providerOrderId")){//上家订单号
				order.put("providerOrderId", (String) result.get("providerOrderId"));
			}
			if(result.containsKey("resultCode")){
				order.put("resultCode", result.get("resultCode")+"");
			}
			if(flag == 1){//提交成功
				order.put("status", "1");
				order.put("orderId", orderId);
				int i = orderDao.updateByPrimaryKeySelective(order);//更新订单状态
			}else if(flag == 0){//提交失败
				order.put("orderId", orderId);
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "2");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				saveOrderPathRecord(orderPathRecord);
				//TODO 复充
				orderDao.updateByPrimaryKeySelective(order);//更新订单状态
				ReCharge(order);
			}else if(flag == -1){//异常情况
				order.put("orderId", orderId);
				order.put("resultCode", order.get("resultCode")+"|该笔订单获取提交状态出现异常");
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("resultCode", "订单接受提交状态出现异常，进入异常订单列表");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				saveOrderPathRecord(orderPathRecord);
				//TODO 异常订单
				orderDao.deleteByPrimaryKey((String)order.get("id"));
				exceptionOrderDao.insertSelective(order);//保存异常订单
			}else if(flag == 3){//实时接口成功状态
				order.put("status", 3);
				order.put("orderId", orderId);
				order.put("endDate", new Timestamp(System.currentTimeMillis()));
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "3");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				saveOrderPathRecord(orderPathRecord);
				boolean ChargeFlag = providerAccountSer.Charge(order);//扣除上家余额
				if(!ChargeFlag){
					log.error("扣除上家余额出错"+MapUtils.toString(order));
					orderDao.deleteByPrimaryKey((String)order.get("id"));
					order.put("resultCode", order.get("resultCode")+"|该笔订单扣除上家余额出现异常");
					exceptionOrderDao.insertSelective(order);//保存异常订单
				}else{
					int n = orderDao.updateByPrimaryKeySelective(order);
					if(n<1){
						log.error("更新数据库出错"+MapUtils.toString(order));
						orderDao.deleteByPrimaryKey((String)order.get("id"));
						order.put("resultCode", order.get("resultCode")+"|该笔订单更新订单状态出现异常");
						exceptionOrderDao.insertSelective(order);//保存异常订单
					}
					// 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
					//根据订单状态新增或扣除上级代理商的利润，并生成利润变更明细
					agentAccountService.addAllParentAgentProfit(order, false);
					//处理回调下家及上家余额扣除
					orderCallback(order,AgentCallbackSer.CallbackStatus_Success);
				}
			}else if(flag == 4){//实时接口失败状态
				order.put("orderId", orderId);
				Map<String,Object> orderPathRecord = new HashMap<String,Object>();
				orderPathRecord.putAll(order);
				orderPathRecord.put("status", "4");
				orderPathRecord.put("createDate", order.get("applyDate"));
				//充值流水
				saveOrderPathRecord(orderPathRecord);
				orderDao.updateByPrimaryKeySelective(order);//更新订单状态
				ReCharge(order);
			}else if(flag == 9){//请求、响应超时
				order.put("status", 9);
				order.put("resultCode", "请求或响应超时");
				orderDao.updateByPrimaryKeySelective(order);
			}
			//流水
			//chargeOrderSer.saveOrderPathRecord(order);
		}catch(Exception e){
			//TODO 异常订单
			log.error("订单状态监听器出现异常"+ExceptionUtils.getExceptionMessage(e));
		}
	}

	/**
	 *处理回调下家及上家余额扣除
	 * @param order  订单详情
	 * @param status  success  fail
	 */
	public synchronized void orderCallback(Map<String, Object> order,String status){
		try {
			@SuppressWarnings("unchecked")
			String agentId = order.get("agentId") + "";
			Map<String, Object> agent = agentDao.selectById(agentId);
			String name = agent.get("name") + "";
			boolean flag = false;
			if ("xicheng".equals(name)) {
				flag = agentCallbackSer.xiChengCallback(order, status);
			}else if(order.containsKey("voucher")) {
				flag = agentCallbackSer.kongChongCallback(order, status);
			} else {
				String callbackUrl = (String) order.get("callbackUrl");
				if (callbackUrl.contains("baidu")) {
					flag = true;
				} else {
					flag = agentCallbackSer.callback(order, status);
				}
			}
			if (flag) {
				log.info(MapUtils.toString(order) + "回调成功");
				order.put("callbackStatus", "1");
				// 保存回调时间

			} else {
				log.error("回调失败" + MapUtils.toString(order));
			}
			Timestamp end = new Timestamp(System.currentTimeMillis());// 订单结束时间
			order.put("callbackDate", end);
			// 获取订单提交时间
			String id = order.get("id") + "";
			Map<String, Object> orderData = orderDao.selectById(id);
			Date createDate = DateTimeUtils.parseDate(orderData.get("createDate") + "", "yyyy-MM-dd HH:mm:ss");
			Timestamp start = new Timestamp(createDate.getTime());// 订单开始时间
			order.put("consumedTime", end.getTime() - start.getTime());// 保存订单消耗时间
			orderDao.updateByPrimaryKeySelective(order);
		} catch (Exception e) {
			log.error("向下家回调出错" + ExceptionUtils.getExceptionMessage(e));
			e.printStackTrace();
		}
	}

	/**
	 * 权重处理，获取权重处理后的分销商ID
	 * 
	 * @author lks 2016年12月9日上午11:24:02
	 * @param dpList
	 * @return
	 */
	public String weightDeal(List<Map<String, Object>> dpList) {
		// 权重处理
		String dispatcherProviderId = "";
		int weightSum = 0;// 权重总数
		for (int i = 0, size = dpList.size(); i < size; i++) {
			Map<String, Object> map = dpList.get(i);
			weightSum += Integer.parseInt((String) map.get("weight"));
		}
		if (weightSum <= 0) {
			log.error("权重设置出错");
		}
		Random random = new Random();
		int randomNum = random.nextInt(weightSum);// randomNum in [0,weightSum]
		int x = 0;
		for (int i = 0, size = dpList.size(); i < size; i++) {
			Map<String, Object> map = dpList.get(i);
			int weight = Integer.parseInt((String) map.get("weight"));// 权重
			if (x <= randomNum && randomNum < x + weight) {
				dispatcherProviderId = (String) map.get("provider_physical_channel_id");
				break;
			}
			x += weight;
		}
		return dispatcherProviderId;
	}

	/**
	 * 复充方法
	 * 
	 * @author lks 2016年12月13日上午10:31:54
	 * @param order
	 */
	public void ReCharge(Map<String, Object> order) {
		String pkgId = order.get("pkgId") + "";
		Map<String, Object> pkg = billPkgDao.selectByPrimaryKey(pkgId);
		order.put("pkg", pkg);
		String dealPath = order.get("dealPath") + "";
		String[] dealPathArr = {};
		if (dealPath != null && !dealPath.equals("")) {
			dealPathArr = dealPath.split(">");
		}
		String providerId = (String) order.get("providerId");// 获取运营商ID
		String bizType = (String) order.get("bizType");// 订单类型
		String provinceCode = (String) order.get("provinceCode");// 省份编码
		String cityCode = (String) order.get("cityCode");// 城市编码
		String agentId = order.get("agentId") + "";
		Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
	
		String phoneNo = order.get("phone") + "";
		// 获取手机号相关信息
		String section = (phoneNo.length() == 13) ? phoneNo.substring(0, 5) : phoneNo.substring(0, 7);// 获取号段
		// TODO 号段从缓存中获取
		Map<String, Object> sectionMap = phoneSectionDao.selectBySection(section);
		if (provinceCode == null) {
			if (sectionMap == null) {
				log.error("无可用产品" + MapUtils.toString(order));
				boolean flag = dealOrderFail(order, "4", order.get("resultCode") + "|无复充通道可走，订单失败");
				if (flag) {
					log.debug("订单置为失败成功");
					// 添加订单所有父级代理商记录
					agentBillDiscountSer.addAllParentAgentOrderinfo(order);
//					//根据订单状态新增或扣除上级代理商的利润，并生成利润变更明细
//					agentAccountService.addAllParentAgentProfit(order);
					//处理回调下家及上家余额扣除
					orderCallback(order,AgentCallbackSer.CallbackStatus_Fail);
				} else {
					log.error("订单置为失败失败");
				}
				return;
				// return 4;// 不支持该手机号段
			}
			provinceCode = (String) sectionMap.get("province_code");// 省份代码
			if (cityCode == null) {
				cityCode = (String) sectionMap.get("city_code");// 城市代码
			}
		}
		String carrierType = (String) sectionMap.get("carrier_type"); // 号段类型,1普卡 7物联网
		// 获取要复充的运营商
		Map<String, Object> rechargeProviderMap = getRechargeProviderId(bizType, dealPathArr, providerId, provinceCode,	cityCode, pkgId, agent,carrierType);
		String rechargeProviderId = null;
		String channelProvinceCode = null;
		String isRelChannel = null;	
		if (rechargeProviderMap != null) {
			channelProvinceCode = rechargeProviderMap.get("province_code") + "";
			order.put("channelProvinceCode", channelProvinceCode);
			rechargeProviderId = rechargeProviderMap.get("provider_physical_channel_id") + "";// 获取要复充的运营商ID
			isRelChannel = rechargeProviderMap.get("isRelChannel") + ""; // 是否特惠通道标识,1 是特惠通道,0不是特惠通道
		}

		if (rechargeProviderId != null && !rechargeProviderId.equals("")) {
			if ("0".equals(isRelChannel)) {
				// 获取下家折扣
				Double agentDiscount = Double.parseDouble(order.get("agentDiscount").toString());
	
				// 获取上家折扣
				Double providerDiscount = getProviceDiscount(bizType, agent, pkgId, providerId, rechargeProviderId,
						rechargeProviderMap.get("province_code") + "",carrierType);
				// 判断上家折扣是否大于下家折扣，大于则停止提交---止损
				if (providerDiscount != null) {
					if ((agentDiscount - providerDiscount) < 0) {
						log.error("无可用产品" + MapUtils.toString(order));
						boolean flag = dealOrderFail(order, "4", order.get("resultCode") + "|无复充通道可走，订单失败");
						if (flag) {
							log.debug("订单置为失败成功");
							// 添加订单所有父级代理商记录
							agentBillDiscountSer.addAllParentAgentOrderinfo(order);
//							//根据订单状态新增或扣除上级代理商的利润，并生成利润变更明细
//							agentAccountService.addAllParentAgentProfit(order);
							//处理回调下家及上家余额扣除
							orderCallback(order,AgentCallbackSer.CallbackStatus_Fail);
						} else {
							log.error("订单置为失败失败");
						}
						return;
					}
				}
			}
			
			// 复充运营商ID不为空
			order.put("dealPath", dealPath + ">" + rechargeProviderId);
			order.put("dispatcherProviderId", rechargeProviderId);
			int dealCount = (int) order.get("dealCount");// 处理次数
			order.put("dealCount", dealCount + 1);
			orderDao.updateByPrimaryKeySelective(order);
			// 将确定好通道的订单加到队列中去处理
			Map<String, Object> physicalChannel = providerPhysicalChannelDao.selectByProviderId(rechargeProviderId);
			order.put("channel", physicalChannel);
			// 获取上家产品id
			Map<String, Object> productParam = new HashMap<String, Object>();
			productParam.put("pkgId", order.get("pkgId"));// 包ID
			productParam.put("providerId", providerId);// 运营商ID
			productParam.put("physicalChannelId", rechargeProviderId);// 通道ID
			productParam.put("provinceCode", provinceCode);// 省份
			Map<String, Object> providerProduct = providerProductDao.selectProviderProductId(productParam);
			if (providerProduct != null) {
				order.put("providerProductId", providerProduct.get("provider_pkg_id"));
			}
			// /String queueName = (String)
			// physicalChannel.get("queue_name");//队列名
			String providerMark = (String) physicalChannel.get("provider_mark");// 上家的唯一标识
			order.put("providerMark", providerMark);// 将上家的唯一标识加入order对象
			
			String providerPhysicalChannelId = physicalChannel.get("id").toString();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("providerPhysicalChannelId", providerPhysicalChannelId);
			// 获取当前物理通道是否配置备用MQ通道(如果有,则使用备用MQ通道,否则走默认通道)
			Map<String, Object> rabbitmqDataMap = rabbitmqPhysicalChannelSer.rabbitmqPhysicalChannelListAll(map);
			if (null == rabbitmqDataMap) {
				// 将订单对象放到通道处理队列，默认通道
				rabbitMqProducer.sendDataToQueue(RabbitMqProducer.Channel_QueueKey, SerializeUtil.getStrFromObj(order));
			} else {
				// 备用通道
				rabbitMqProducer.sendDataToQueue(rabbitmqDataMap.get("mqQueueName").toString(), SerializeUtil.getStrFromObj(order));
			}
		} else {// 无复充通道可走
			boolean flag = dealOrderFail(order, "4", order.get("resultCode") + "|无复充通道可走:订单失败");
			if (flag) {
				log.debug("订单置为失败成功");
				// 添加订单所有父级代理商记录
				agentBillDiscountSer.addAllParentAgentOrderinfo(order);
				//根据订单状态新增或扣除上级代理商的利润，并生成利润变更明细
//				agentAccountService.addAllParentAgentProfit(order);
				//处理回调下家及上家余额扣除
				orderCallback(order,AgentCallbackSer.CallbackStatus_Fail);
			} else {
				log.error("订单置为失败失败");
			}
		}
	}

	/**
	 * 保存订单流水
	 * 
	 * @author lks 2017年4月11日下午4:03:38
	 * @return
	 * @throws ParseException
	 */
	public boolean saveOrderPathRecord(Map<String, Object> orderPathRecord) {
		boolean flag = false;
		try {
			orderPathRecord.put("mpOrderId", orderPathRecord.get("id"));
			orderPathRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
			orderPathRecord.put("endDate", new Timestamp(System.currentTimeMillis()));
			Date createDate = DateTimeUtils.parseDate(orderPathRecord.get("createDate") + "", "yyyy-MM-dd HH:mm:ss");
			Timestamp start = new Timestamp(createDate.getTime());// 订单开始时间
			orderPathRecord.put("consumedTime", System.currentTimeMillis() - start.getTime());
			int x = orderPathRecordDao.insertSelective(orderPathRecord);
			if (x > 0) {
				flag = true;
			}
			orderPathRecord.put("id", orderPathRecord.get("mpOrderId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 订单处理失败的处理方法
	 * 
	 * @author lks 2016年12月16日下午1:48:17
	 * @param order
	 */
	@Transactional
	public boolean dealOrderFail(Map<String, Object> order, String status, String resultCode) {
		boolean flag = true;
		String agentId = (String) order.get("agentId");// 代理商ID
		order.put("status", status);// 失败
		order.put("resultCode", resultCode);
		order.put("endDate", new Timestamp(System.currentTimeMillis()));
		int orderCount = orderDao.updateByPrimaryKeySelective(order);
		if (orderCount == 1) {
			List<Map<String, Object>> moneyList = new ArrayList<Map<String, Object>>();
//			Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
//			String billPkgId = (String) order.get("pkgId");// 产品ID
//			String providerId = (String) order.get("providerId");// 运营商ID
//			String phone = (String) order.get("phone");// 手机号
//			String section = (phone.length() == 13) ? phone.substring(0, 5) : phone.substring(0, 7);// 获取号段
			// TODO 号段从缓存中获取
//			Map<String, Object> sectionMap = phoneSectionDao.selectBySection(section);
//			String provinceCode = (String) sectionMap.get("province_code");// 省份代码
//			String cityCode = (String) sectionMap.get("city_code");// 城市代码
			String bizType = (String) order.get("bizType");
			String agentOrderId = order.get("agentOrderId")+"";
//			Map<String, Object> pkg = new HashMap<String, Object>();
//			if (bizType.equals("1")) {// 话费
//				pkg = billPkgDao.selectByPrimaryKey(billPkgId);
//			} else {// 话费
//				pkg = billPkgDao.selectByPrimaryKey(billPkgId);
//			}
//			String price = pkg.get("value") + "";
//			moneyList = getChargeList(1, bizType, moneyList, agent, billPkgId, providerId, provinceCode, cityCode,
//					price);
			moneyList = getChargeList(agentOrderId);
			if(moneyList != null){
				for (int i = 0, size = moneyList.size(); i < size; i++) {
					Map<String, Object> moneyMap = moneyList.get(i);
					double money = Double.parseDouble(moneyMap.get("money") + "");
					agentId = moneyMap.get("agentId")+"";
					moneyMap.put("money", money);
					double beforeBalance = agentAccountDao.selectBalanceByAgentid(agentId);
					moneyMap.put("beforeBalance", beforeBalance);
					int chargeCount = agentAccountDao.addMoney(moneyMap);
					if (chargeCount != 1) {
						flag = false;
						log.error("订单置为失败，为代理商退款出错" + MapUtils.toString(order));
					} else {
						double afterBalance = beforeBalance + money;// 扣除当前钱数之后的余额
						Map<String, Object> aacMap = new HashMap<String, Object>();
						String id = UUID.randomUUID().toString().replace("-", "");// 生成id
						aacMap.put("id", id);
						aacMap.put("agentId", agentId);
						aacMap.put("orderId", order.get("id") + "");
						aacMap.put("agentOrderId", order.get("agentOrderId") + "");
						aacMap.put("fee", money);
						aacMap.put("balanceBefore", beforeBalance);
						aacMap.put("balanceAfter", afterBalance);
						aacMap.put("type", bizType);
						if (money < 0) {
							aacMap.put("status", "1");
						} else {
							aacMap.put("status", "2");
						}
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		                String times = timestamp.toString();
		                aacMap.put("applyDate",times);
						int aacFlag = agentAccountChargeDao.insertSelective(aacMap);// 插入扣款记录表
						if (aacFlag != 1) {
							log.error("扣款记录表未插入成功");
						}
					}
				}
			}
		} else {
			flag = false;
			log.error("【订单置为失败】修改数据库出错" + MapUtils.toString(order));
		}
		return flag;
	}

	public List<Map<String,Object>> getChargeList(String agentOrderId){
		List<Map<String,Object>> list = agentAccountChargeDao.selectChargeList(agentOrderId);
		return list;
	}
	
	/**
	 * 获取要复充的运营商ID
	 * 
	 * @author lks 2016年12月15日下午3:48:44
	 * @param
	 * @return
	 */
	public Map<String, Object> getRechargeProviderId(String bizType, String[] alreadyDealArr, String providerId,
			String provinceCode, String cityCode, String pkgId, Map<String, Object> agent,String carrierType) {
		String rechargeId = "";
		String agentId = agent.get("id") + "";
		String parentId = agent.get("parent_id") + "";
		Map<String, Object> reMap = null;
		List<Map<String, Object>> rechargeList = new ArrayList<Map<String, Object>>();
		if (bizType.equals("2")) {// 话费复充通道组
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("providerId", providerId);
			param.put("provinceCode", provinceCode);
			param.put("pkgId", pkgId);
			if (!parentId.equals("0") && !parentId.trim().equals("")) {
				agentId = getParentsAgentId(parentId);
			}
			Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(agentId);
			String groupId = parentAgent.get("bill_group_id") + "";
			param.put("groupId", groupId);
//			rechargeList = providerGroupBillRelDao.selectPhysicalChannel(param);
			param.put("agentId", agentId);
			
			// carrierType是号段类型,1普卡 7物联网
			if (carrierType.equals("1")) {
				param.put("channelType", 2);	// 普通话费充值通道
			} else{
				param.put("channelType", 3);	// 物联网卡充值通道
			}
            rechargeList = providerGroupBillRelDao.selectPhysicalChannelExt(param);
			

			// dpList =
			// providerBillDispatcherDao.selectDispatcherByProviderId(param);

			// rechargeList =
			// providerBillRechargeGroupDao.selectByProviderId(param);
			// // 不允许省网话费跑全国
			// if (rechargeList.size() < 1)
			// {
			// param.put("provinceCode", "全国");
			// rechargeList =
			// providerBillRechargeGroupDao.selectByProviderId(param);
			// }
		}
		if (rechargeList != null) {
			for (int i = 0, size = alreadyDealArr.length; i < size; i++) {
				String alreadyDealId = alreadyDealArr[i];// 已经充值过的ID
				int j = 0;
				int Size = rechargeList.size();
				while (j < Size) {
					Map<String, Object> dispatcherProviderMap = rechargeList.get(j);// 分销商ID
					if (alreadyDealId.equals(dispatcherProviderMap.get("provider_physical_channel_id") + "")) {
						rechargeList.remove(j);
						Size--;
						continue;
					}
					j++;
				}
			}
			// for (int i = 0, size = alreadyDealArr.length; i < size; i++)
			// {
			// String alreadyDealId = alreadyDealArr[i];// 已经充值过的ID
			// for (int j = 0, Size = rechargeList.size(); j < Size; j++)
			// {
			// Map<String, Object> dispatcherProviderMap =
			// rechargeList.get(j);// 分销商ID
			// if
			// (alreadyDealId.equals(dispatcherProviderMap.get("provider_physical_channel_id")
			// + ""))
			// {
			// rechargeList.remove(j);
			// Size--;
			// }
			// }
			// }
		}
		if (rechargeList.size() > 0) {
			reMap = rechargeList.get(0);
			// rechargeId = reMap.get("provider_physical_channel_id")+"";

		}
		return reMap;
	}

	/**
	 * 判断参数是否完整
	 * 
	 * @author lks 2016年12月26日下午2:03:36
	 * @param param
	 * @return
	 */
	public boolean checkParamIntact(Map<String, Object> param) {
		boolean flag = true;
		// 校验参数不为空
		if (param.get("terminalName") == null || param.get("customerOrderId") == null || param.get("phoneNo") == null
				|| param.get("orderType") == null || param.get("orderType") == null || param.get("spec") == null
				|| param.get("scope") == null || param.get("callbackUrl") == null || param.get("timeStamp") == null
				|| param.get("signature") == null) {
			flag = false;
		} else {
			// 校验参数符合规则
			if (32 < ((String) param.get("customerOrderId")).length()
					|| (11 != ((String) param.get("phoneNo")).length()
							&& 13 != ((String) param.get("phoneNo")).length())
					|| (!"1".equals(param.get("orderType")) && !"2".equals(param.get("orderType")))
					|| 256 < ((String) param.get("callbackUrl")).length()
					|| ((!ToolValidator.isMobile((String) param.get("phoneNo")))
							&& (!ToolValidator.isWLWMobile((String) param.get("phoneNo"))))) {
				flag = false;
			}
		}
		if(Double.parseDouble(param.get("spec")+"") == 0){
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证签名是否正确
	 * 
	 * @author lks 2017年2月5日下午1:50:15
	 * @param param
	 * @return
	 */
	public boolean checkSign(Map<String, Object> param) {
		boolean flag = false;
		try {
			String signature = (String) param.get("signature");// 获取客户提交的签名
			param.remove("signature");// 删除签名
			String agentName = (String) param.get("terminalName");// 客户名
			Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
			String publicKeyStr = (String) agent.get("app_key");// 获取该客户的公钥
			byte[] keyByteArray = new BASE64Decoder().decodeBuffer(publicKeyStr);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByteArray);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
			// 设置请求参数排列顺序
			String content = setParamStr(param);
			// MD5加密
			String md5 = MD5.ToMD5(content);

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] rsa = cipher.doFinal(Hex.decodeHex(signature.toCharArray()));
			flag = new String(rsa).equals(md5);
		} catch (Exception e) {
			log.error("签名验证过程出现错误" + ExceptionUtils.getExceptionMessage(e));
		}
		return flag;
	}

	/**
	 * 获取排序后的字符串
	 * 
	 * @author lks 2017年2月5日下午2:46:33
	 * @param paramMap
	 * @return
	 */
	public static String setParamStr(Map<String, Object> paramMap) {
		// 转换
		List<String> paramNames = new ArrayList<String>();
		for (String paramKey : paramMap.keySet()) {
			paramNames.add(paramKey);
		}
		// 排序
		Collections.sort(paramNames);
		// 拼接请求参数字符串
		StringBuilder paramUrl = new StringBuilder();
		for (String paramName : paramNames) {
			paramUrl.append(paramName).append("=").append(paramMap.get(paramName)).append("&");
		}
		paramUrl.deleteCharAt(paramUrl.length() - 1);

		String content = paramUrl.toString();
		return content;
	}

	/**
	 * 判断代理商是否可用
	 * 
	 * @author lks 2016年12月7日下午4:14:34
	 * @param flag
	 *            接收状态 ，agent 代理商对象
	 * @return
	 */
	public boolean checkAgent(boolean flag, Map<String, Object> agent) {
		if (agent != null) {
			String status = (String) agent.get("status");// 获取状态
			if (!status.equals("1")) {
				flag = false;
			}
			String parentId = (String) agent.get("parent_id");// 获取父级Id
			if (!parentId.equals("0")) {// 0代表最高级
				Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(parentId);// 获取父级代理商
				flag = checkAgent(flag, parentAgent);
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断该代理商是否能充值该运营商号码
	 * 
	 * @author lks 2016年12月6日下午3:28:39
	 * @param billGroupId
	 *            运营商组ID
	 * @param providerId
	 *            运营商ID
	 * @return true 可以充值，false 不可以充值
	 */
	public boolean checkProviderForAgent(String bizType, String billGroupId, String providerId, String provinceCode,
			String cityCode) {
		Map<String, String> param = new HashMap<String, String>();
		int count = 0;
		if (bizType.equals("2")){//话费
			param.put("billGroupId", billGroupId);
			param.put("providerId", providerId);
			param.put("provinceCode", provinceCode);
			count = providerGroupBillRelDao.countByBillGroupIdAndProviderId(param);
		}
		return count > 0 ? true : false;
	}

	/**
	 * 获取代理商可以充值的产品
	 * 
	 * @author lks 2016年12月6日下午4:30:03
	 * @param providerId
	 *            运营商id
	 * @param price
	 *            充值金额
	 * @return billPkg对象
	 */
	public Map<String, Object> getPkgForAgent(String agentId, String bizType, String providerId, String price,
			String provinceCode) {
		Map<String, String> param = new HashMap<String, String>();
		Map<String, Object> pkg = new HashMap<String, Object>();
		if (bizType.equals("2")) {// 话费
			param.put("agentId", agentId);
			param.put("provinceCode", provinceCode);
			param.put("providerId", providerId);
			param.put("price", Double.parseDouble(price) / 100 + "");
			pkg = billPkgDao.selectBillPkgForAgent(param);
		}
		if(pkg == null){
			param.put("agentId", agentId);
			param.put("provinceCode", provinceCode);
			param.put("providerId", providerId);
			pkg = billPkgDao.selectBillPkgForAgentRYC(param);
		}
		return pkg;
	}

	/**
	 * 获取代理商折扣
	 * 
	 * @author lks 2017年3月1日上午11:25:53
	 * @return
	 */
	public Map<String, Object> getAgentDiscount(String bizType, String providerId, String pkgId, String provinceCode,
			String cityCode, Map<String, Object> agent) {
		Map<String, Object> discount = new HashMap<String, Object>();
		String agentId = (String) agent.get("id");// 获取代理商id
		String parentId = (String) agent.get("parent_id");// 获取父id
		String billModelId = (String) agent.get("bill_model_id");// 获取话费折扣模板id
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("agentId", agentId);
		param.put("providerId", providerId);
		param.put("provinceCode", provinceCode);
		param.put("cityCode", cityCode);

		param.remove("cityCode");
		param.put("billPkgId", pkgId);
		// String billGroupId = agent.get("bill_group_id")+"";
		// //判断代理商能否取省网折扣
		// if(checkProvince(provinceCode,providerId,billGroupId)){
		// discount = agentBillDiscountDao.selectDiscountMap(param);// 获取折扣
		// }else{
		// param.put("provinceCode", "全国");
		discount = agentBillDiscountDao.selectDiscountMap(param);// 获取折扣
		/**
		 * baseBillDiscountDetailDao 表已不存在，此处后续优化掉     XieXiaoZhen
		 */
		if (discount == null) {// 如果折扣获取不到，去折扣模板中去获取
			if (billModelId != null && !billModelId.equals("")) {
				param.remove("agentId");
				param.put("billModelId", billModelId);
				discount = baseBillDiscountDetailDao.selectDiscountMap(param);
			} else {
				return null;
			}
		}

		return discount;
	}

	/**
	 * 检查省份是否可用，为了下家折扣查询
	 * 
	 * @author lks 2017年5月27日上午9:54:20
	 * @param provinceCode
	 * @param providerId
	 * @param groupId
	 * @return
	 */
	public boolean checkProvince(String provinceCode, String providerId, String groupId) {
		boolean flag = false;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("provinceCode", provinceCode);
		map.put("providerId", providerId);
		map.put("groupId", groupId);
		int x = agentBillDiscountDao.checkProvince(map);
		if (x > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断代理商余额是否充足
	 * 
	 * @author lks 2016年12月6日下午4:41:56
	 * @param agentId
	 *            代理商id
	 * @param price
	 *            充值金额
	 * @return true 余额充足，false 余额不足
	 */
	public boolean checkBalance4Agent(String agentId, String price) {
		double balance = agentAccountDao.selectBalanceByAgentid(agentId);
		/**
			//代理商加款由冻结上级代理商加款的金额，下级代理商提单时扣除上级代理商冻结金额逻辑变更为：
			//上级代理商给下级代理商加款直接扣除加款金额
			double childBalance = agentAccountDao.selectChildBalanceByAgentid(agentId);
			// TODO 资金预警
			return (balance - childBalance) >= Double.parseDouble(price) ? true : false;
		 **/
		return balance >= Double.parseDouble(price) ? true : false;
	}

	/**
	 * 获取订单对象
	 * 
	 * @author lks 2016年12月8日上午9:28:03
	 * @param param
	 * @return
	 */
	public Map<String, Object> getOrderObj(Map<String, Object> param, Map<String, Object> pkg) {
		Map<String, Object> order = new HashMap<String, Object>();
		String id = UUID.randomUUID().toString().replace("-", "");// 生成id
		String agentOrderId = (String) param.get("customerOrderId");// 下家订单号
		String agentId = (String) param.get("agentId");// 代理商Id
		String providerId = (String) param.get("providerId");// 运营商Id
		String phone = (String) param.get("phoneNo");// 手机号
		String bizType = (String) param.get("orderType");// 业务类型 2 话费 1 话费
		String orderId = (String) param.get("orderId");
		double fee = Double.parseDouble((String) param.get("spec"));// 费用 分为单位
		String callbcakUrl = (String) param.get("callbackUrl");// 回调地址
		Timestamp applyDate = DateTimeUtils.getOrderTimestamp((String) param.get("timeStamp"));// 时间戳，格式：yyyyMMddHHmmssSSS
		order.put("id", id);
		order.put("agentOrderId", agentOrderId);
		order.put("agentId", agentId);
		order.put("providerId", providerId);
		order.put("phone", phone);
		order.put("bizType", bizType);
		// 判断是都存在订单号
		if (null != orderId && !"".equals(orderId) && !"null".equals(orderId)) {
			order.put("orderId", orderId);
		}
		if (bizType.equals("2")) {// 话费订单
			order.put("fee", fee / 100);
		}
		order.put("callbackUrl", callbcakUrl);
		order.put("callbackStatus", "0");
		order.put("applyDate", applyDate);
		order.put("status", "0");//订单处理中状态
		return order;
	}

	/**
	 * 获取分销商的折扣
	 * 
	 * @author lks 2016年12月9日上午9:46:55
	 * @param providerId
	 *            运营商Id
	 * @param billPkgId
	 *            产品ID
	 * @param provinceCode
	 *            省份
	 * @param cityCode
	 *            城市
	 * @return
	 */
	public double getProviderDiscount(String providerId, String billPkgId, String provinceCode, String cityCode) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("providerId", providerId);
		param.put("billPkgId", billPkgId);
		param.put("provinceCode", provinceCode);
		param.put("cityCode", cityCode);
		double discount = providerBillDiscountDao.selectDiscount(param);// 获取该产品的运营商折扣
		return discount;
	}

	//新版本新增
	public List<Map<String, Object>> getCurrentAgentChargeList(int type, String bizType, List<Map<String, Object>> list,
												   Map<String, Object> agent, String pkgId, String providerId, String provinceCode, String cityCode,
												   String price) {
		String agentId = (String) agent.get("id");// 获取代理商id
		double discount = 0.0;
		Map<String, String> param = new HashMap<String, String>();
		param.put("agentId", agentId);
		param.put("providerId", providerId);
		param.put("provinceCode", provinceCode);
		param.put("cityCode", cityCode);
		if (agent != null) {
			if (bizType.equals("2")) {// 话费
				param.put("billPkgId", pkgId);
				discount = agentBillDiscountDao.selectDiscount(param);// 获取折扣
				if (discount == 0.0) {// 如果折扣获取不到，去折扣模板中去获取
					return null;
				}
			}
			Map<String, Object> discountMap = new HashMap<String, Object>();
			discountMap.put("type", 1); //扣款
			discountMap.put("agentId", agentId);
			BigDecimal multiply = BigDecimal.valueOf(Double.parseDouble(price)).multiply(BigDecimal.valueOf(discount));
			if (type == 0) {
				//扣款
				discountMap.put("money", multiply.negate().doubleValue()); //negate转为负数 doubleValue转为Double类型
			} else if (type == 1) {
				//加款
				discountMap.put("money", multiply.doubleValue());
			}
			list.add(discountMap);
		}
		return list;
	}


	/**
	 * 递归获取代理商扣款数据
	 * 
	 * @author lks 2016年12月7日上午8:39:38
	 * @param type
	 *            0=扣款|1=退款
	 * @param list
	 *            接收数据的List<Map<String,Object>>
	 * @param agent
	 *            代理商对象
	 * @param pkgId
	 *            产品ID
	 * @param providerId
	 *            运营商ID
	 * @param provinceCode
	 *            省份
	 * @param cityCode
	 *            城市
	 * @return list<Map<String,Object>> map中包含agentId(代理商Id),money(应扣钱数)
	 */
	public List<Map<String, Object>> getChargeList(int type, String bizType, List<Map<String, Object>> list,
			Map<String, Object> agent, String pkgId, String providerId, String provinceCode, String cityCode,
			String price) {
		String agentId = (String) agent.get("id");// 获取代理商id
		String parentId = (String) agent.get("parent_id");// 获取父id
//		String billModelId = (String) agent.get("bill_model_id");// 获取话费折扣模板id
		double discount = 0.0;
		Map<String, String> param = new HashMap<String, String>();
		param.put("agentId", agentId);
		param.put("providerId", providerId);
		param.put("provinceCode", provinceCode);
		param.put("cityCode", cityCode);
		if (agent != null) {
			if (bizType.equals("2")) {// 话费
				param.put("billPkgId", pkgId);
				discount = agentBillDiscountDao.selectDiscount(param);// 获取折扣
				if (discount == 0.0) {// 如果折扣获取不到，去折扣模板中去获取
//					param.remove("agentId");
//					param.put("billModelId", billModelId);
//					discount = baseBillDiscountDetailDao.selectDiscount(param);
					return null;
				}
			}
			Map<String, Object> discountMap = new HashMap<String, Object>();
			discountMap.put("type", 1); //扣款
			discountMap.put("agentId", agentId);
			BigDecimal multiply = BigDecimal.valueOf(Double.parseDouble(price)).multiply(BigDecimal.valueOf(discount));
			if (type == 0) {
				//扣款
				discountMap.put("money", multiply.negate().doubleValue()); //negate转为负数 doubleValue转为Double类型
			} else if (type == 1) {
				//加款
				discountMap.put("money", multiply.doubleValue());
			}
			list.add(discountMap);
			if (!parentId.equals("0") && !parentId.trim().equals("")) {
				Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(parentId);
				//list = getChargeList(type, bizType, list, parentAgent, pkgId, providerId, provinceCode, cityCode, price); //按照各级代理商的折扣生成扣款记录
				//按照提单代理商的折扣生成上级代理的扣款记录
				list = getPrentChargeList(type, list, parentAgent, discount, price);
			}
		}
		return list;
	}

	/**
	 * 根据提单代理商的折扣生成上级代理商的扣款记录，上下级代理商折扣之间的差价在充值成功后生成订单时进行统计并添加到利润中
	 * 递归获取
	 * @author xxz 2022年05月28日下午15:36:30
	 * @param type   0=扣款|1=退款
	 * @param list   接收数据的List<Map<String,Object>>
	 * @param agent  代理商对象
	 * @param discount  提单代理商折扣
	 * @return
	 */
	public List<Map<String, Object>> getPrentChargeList(int type,List<Map<String, Object>> list, Map<String, Object> agent,
														 double discount,String price){
		String agentId = (String) agent.get("id");// 获取代理商id
		String parentId = (String) agent.get("parent_id");// 获取父id
		Map<String, Object> discountMap = new HashMap<String, Object>();
		discountMap.put("agentId", agentId);
		if (agent != null) {
			BigDecimal multiply = BigDecimal.valueOf(Double.parseDouble(price)).multiply(BigDecimal.valueOf(discount));
			if (type == 0) {
				//扣款
				discountMap.put("money", multiply.negate().doubleValue()); //negate转为负数 doubleValue转为Double类型
			} else if (type == 1) {
				//加款
				discountMap.put("money", multiply.doubleValue());
			}
		}
		list.add(discountMap);
		if (!parentId.equals("0") && !parentId.trim().equals("")) {
			Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(parentId);
			list = getPrentChargeList(type, list, parentAgent,discount, price);
		}
		return list;
	}

	/**
	 * @Title:getProviceDiscount @Description:获取上家折扣 @author CXJ @date
	 * 2017年4月12日 上午10:20:02 @param @param bizType @param @param
	 * agentName @param @param pkgId @param @param providerId @param @param
	 * provinceCode @param @return @return Double 返回类型 @throws
	 */
	public Double getProviceDiscount(String bizType, Map<String, Object> agent, String pkgId, String providerId,
			String providerPhysicalChannelId, String provinceCode,String carrierType) {
		String agentId = (String) agent.get("id");// 获取代理商id
		String parentId = (String) agent.get("parent_id");// 获取父id
		Double providerDiscount = null;
//		String channelProvinceCode = provinceCode;
		String isRelChannel = "";	// 是否特惠通道标识,1 是特惠通道,0不是特惠通道
		// 获取渠道id
		if (providerPhysicalChannelId == null) {
			List<Map<String, Object>> dpList = new ArrayList<Map<String, Object>>();
			Map<String, Object> phyparam = new HashMap<String, Object>();
			phyparam.put("pkgId", pkgId);
			phyparam.put("providerId", providerId);
			phyparam.put("provinceCode", provinceCode);
			if (!parentId.equals("0") && !parentId.trim().equals("")) {
				agentId = getParentsAgentId(parentId);
			}
			Map<String, Object> parentAgent = agentDao.selectByPrimaryKeyForOrder(agentId);
			String groupId = parentAgent.get("bill_group_id") + "";
			phyparam.put("groupId", groupId);
			//dpList = providerGroupBillRelDao.selectPhysicalChannel(phyparam);
			phyparam.put("agentId", agentId);
			
			// carrierType是号段类型,1普卡 7物联网
			if (carrierType.equals("1")) {
				phyparam.put("channelType", 2);	// 普通话费充值通道
			} else{
				phyparam.put("channelType", 3);	// 物联网卡充值通道
			}
			dpList = providerGroupBillRelDao.selectPhysicalChannelExt(phyparam);
			if (dpList.size() > 0) {
				Map<String, Object> providerPhysicalChannel = dpList.get(0);
//				channelProvinceCode = providerPhysicalChannel.get("province_code") + "";
				providerPhysicalChannelId = providerPhysicalChannel.get("provider_physical_channel_id") + "";// 根据折扣排序取折扣最低的一个
				isRelChannel = providerPhysicalChannel.get("isRelChannel") + "";
			}
		}

		// 查看是否允许特惠
		if (providerPhysicalChannelId != null) {
//			Map<String, Object> agentChannelMap = new HashMap<String, Object>();
//			agentChannelMap.put("provinceCode", channelProvinceCode);
//			agentChannelMap.put("providerPhysicalChannelId", providerPhysicalChannelId);
//			agentChannelMap.put("agentId", agentId);
//			agentChannelMap.put("providerId", providerId);
//			Map<String, Object> resultMap = agentChannelRelDao.queryAgentChannelRel(agentChannelMap);
//			if (resultMap != null) {
//				return providerDiscount;
//			}
			
			if ("1".equals(isRelChannel)) {
        		return providerDiscount;
			}
			
			// 获取上家折扣
			Map<String, Object> providerDisParam = new HashMap<String, Object>();
			providerDisParam.put("providerId", providerId);
			providerDisParam.put("providerPhysicalChannelId", providerPhysicalChannelId);
			providerDisParam.put("provinceCode", provinceCode);
			providerDisParam.put("billPkgId", pkgId);
			List<Map<String, Object>> providerDiscountList = new ArrayList<Map<String, Object>>();

			providerDiscountList = providerBillDiscountDao.selectDiscountMap(providerDisParam);// 获取话费运营商折扣
			if (providerDiscountList.size() > 0) {
				Map<String, Object> providerDiscountMap = providerDiscountList.get(0);
				providerDiscount = Double.parseDouble(providerDiscountMap.get("real_discount") + "");
			}
			// else
			// {
			// providerDisParam.put("provinceCode", "全国");
			// providerDiscountMap =
			// providerBillDiscountDao.selectDiscountMap(providerDisParam);//
			// 获取话费运营商折扣
			// if (providerDiscountMap != null)
			// {
			// providerDiscount =
			// Double.parseDouble(providerDiscountMap.get("discount") + "");
			// }
			// }
		}

		return providerDiscount;
	}

	public String getParentsAgentId(String agentId) {
		Map<String, Object> agent = agentDao.selectByPrimaryKeyForOrder(agentId);
		String parentId = (String) agent.get("parent_id");// 获取父id
		String id = null;
		if (!parentId.equals("0") && !parentId.trim().equals("")) {
			id = getParentsAgentId(parentId);
		} else {
			id = agentId;
		}
		return id;
	}

	/**
	 * @Title:submitXiCheng @Description: 西城订单提交处理 TODO(这里用一句话描述这个方法的作用) @author
	 * CXJ @date 2017年6月23日 下午3:09:55 @param @param
	 * request @param @return @return String 返回类型 @throws
	 */
	public String submitXiCheng(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String jsonStr = getRequestContext(request);
		JSONObject js = JSON.parseObject(jsonStr);
		String partyId = js.getString("partyId") + "";
		String bill = js.getString("bill") + "";
		String time = js.getString("time") + "";
		String sign = js.getString("sign") + "";
		log.error("西城回调参数为partyId=" + partyId + "|partyId=" + partyId + "|bill=" + bill + "|time=" + time + "|sign="
				+ sign);
		if (null == partyId || null == bill || null == time || null == sign) {
			log.error("西城回调的参数有空值");
			json.put("status", "0");
			json.put("resultCode", "50001");
			json.put("resultDesc", "请求失败,非法请求参数");
			return json.toJSONString();
		} else {
			try {
				// 获取西城配置参数
				Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey("0000000075");
				String defaultParameter = (String) channel.get("default_parameter");// 默认参数
				Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
				String securitKey = paramMap.get("securitKey");// 拿到加密key
				// 参数解密
				String decryptBill = XiChengAESUtils.decrypt(bill, securitKey);
				// 转成json对象
				JSONObject jsonBill = JSON.parseObject(decryptBill);
				String orderId = jsonBill.getString("orderId");// 订单号
				String orderTime = jsonBill.getString("orderTime");// 订单请求时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 精确到秒
				Date date = sdf.parse(orderTime);
				orderTime = ToolDateTime.format(date, "yyyyMMddHHmmssSSS");// 发起请求的时间
				String prodId = jsonBill.getString("prodId");// 产品ID
				String phoneNumber = jsonBill.getString("phoneNumber");// 电话号码
				String notifyUrl = jsonBill.getString("notifyUrl");// 回调地址
				int spec = Integer.parseInt(prodId.substring(2, prodId.length()));
				String terminalName = "xicheng";
				String orderType = "1";
				String scope = "nation";
				// 组装请求参数
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("terminalName", terminalName);
				param.put("customerOrderId", orderId);
				param.put("phoneNo", phoneNumber);
				param.put("orderType", orderType);
				param.put("spec", String.valueOf(spec));
				param.put("timeStamp", orderTime);
				param.put("scope", scope);
				param.put("callbackUrl", notifyUrl);
				// 生成平台订单号
				String channelOrderId = ToolDateTime.format(new Date(), "yyyyMMddHHmmssSSS") + phoneNumber
						+ GenerateData.getIntData(9, 2);
				param.put("orderId", channelOrderId);
				// 生成签名
				String signature = sign(param,
						"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANP91zKDLCQQoSLn7KeilW7/48vvYrdNkNIioGy1XJtpoebFBpFKPBY4KqV0nu6wVLOqjY2QKJov69rerg7vrSBsQ7rg3lctvHWVkr4fc5rmyMD6W5Hl/ZfIdSQQGS4CyUF5wHmte5S52BlkKUHhJEq51GSoehEk306mH34HHDjJAgMBAAECgYB/WKiSfnvgumCiAGKInUFZabylAIvzg1Pw974ZL6WO8pjAcTEtzENmMZ1kwTbMOf7X4yefl67cWNc9JrG25UHA9PsYObIFjxpj0YF7q4yo9SlbqAKEefCIt4T+Z3CM7qqo8Im50DscPIS15hnvSBYJgjAXhOQxaR7dkB/M23VItQJBAPfiR42rL3Wami24sexCmU+hKgw6DGP5w1owUhobU/Y6JwTATtjL52YkpGDh4f4MjV6j8r9wQfKcCAkBXi6UJecCQQDa7re4cYAOVKzauW349xdIwexGLmS5s/X700kf1FmR2FGFAx4AYlHA6YKjDgNIoooy7zMzFcWrA41471n6KXXPAkEA43KnQSCADaLsMWO1Lhn13pD8qfdhgyKb01dfHkFRCy9UuhFSx65hBuI8SgE5ggg2d05r4Ki6ekgdP1YX+xlIpQJAEMzCslebUP40aXBTPQiQ89dVryj+N7XUiWd0NAoSeXuU/dT4Z2UkCt6gVKzNmbRHUJNEZYYlLfNO1tnfGLA3xwJAJJ0tV2ZSOoJmvkRVs3h3ZQNAmiNgGC1ZgOtuJwp/tFkbR8yGd/HQVJqShoZeXQ0ffHOssarrbYBGW0Ou4cWSrA==");
				param.put("signature", signature);
				int code = -1;
				if (MapUtils.checkEmptyValue(param) && checkParamIntact(param)) {// 验证参数
					if (checkSign(param)) {// 验证签名
						code = createOrder(param);
					} else {
						code = 2;
					}
				} else {
					code = 1;// 参数不全
				}
				// JSONObject resultJson = new JSONObject();
				String status = "0";
				String message = "";
				int resultCode = -1;
				switch (code) {
				case 0:
					resultCode = 0;
					message = "提交成功";
					status = "1";
					break;
				case 1:
					resultCode = 1;
					message = "参数不正确";
					break;
				case 2:
					resultCode = 4;
					message = "签名错误";
					break;
				case 3:
					resultCode = 6;
					message = "该代理商不可用";
					break;
				case 4:
					resultCode = 3;
					message = "不支持该手机号段";
					break;
				case 5:
					resultCode = 18;
					message = "不允许充值该运营商号段，请联系业务人员获取权限";
					break;
				case 6:
					resultCode = 19;
					message = "无法充值该产品";
					break;
				case 7:
					resultCode = 15;
					message = "余额不足";
					break;
				case 8:
					resultCode = 20;
					message = "订单提交出现异常";
					break;
				case 9:
					resultCode = 21;
					message = "折扣已变更，请联系商务";
					break;
				default:
					code = -1;
					resultCode = -1;
					message = "状态返回值异常";
					break;
				}
				param.put("code", code);
				param.put("message", message);
				saveSubmitOrder(param);// 保存订单
				json.put("status", status);
				json.put("resultCode", String.valueOf(resultCode));
				json.put("resultDesc", message);
				JSONObject resultBill = new JSONObject();
				resultBill.put("orderId", orderId);
				resultBill.put("channelOrderId", channelOrderId);
				json.put("bill", resultBill);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return json.toJSONString();
	}

	/**
	 * @Title:getRequestContext @Description: 获取请求数据
	 * TODO(这里用一句话描述这个方法的作用) @author CXJ @date 2017年7月4日 上午8:44:22 @param @param
	 * request @param @return @return String 返回类型 @throws
	 */
	public static String getRequestContext(HttpServletRequest request) {
		String str = "";
		try {
			StringBuilder sb = new StringBuilder();
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			str = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * @Title:queryXiChengOrder @Description: 西城订单查询处理
	 * TODO(这里用一句话描述这个方法的作用) @author CXJ @date 2017年6月23日
	 * 下午3:10:17 @param @param request @param @return @return String
	 * 返回类型 @throws
	 */
	public String queryXiChengOrder(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		String jsonStr = getRequestContext(request);
		JSONObject js = JSON.parseObject(jsonStr);
		String partyId = js.getString("partyId") + "";
		String bill = js.getString("bill") + "";
		String time = js.getString("time") + "";
		String sign = js.getString("sign") + "";
		log.error("西城回调参数为partyId=" + partyId + "|partyId=" + partyId + "|bill=" + bill + "|time=" + time + "|sign="
				+ sign);
		if (partyId.equals("") || bill.equals("") || time.equals("") || sign.equals("")) {
			log.error("西城回调的参数有空值");
			json.put("status", "0");
			json.put("resultCode", "50001");
			json.put("resultDesc", "请求失败,非法请求参数");
			return json.toJSONString();
		} else {
			try {
				// 获取西城配置参数
				Map<String, Object> channel = providerPhysicalChannelDao.selectByPrimaryKey("0000000075");
				String defaultParameter = (String) channel.get("default_parameter");// 默认参数
				Map<String, String> paramMap = XmlUtils.readXmlToMap(defaultParameter);
				String securitKey = paramMap.get("securitKey");// 拿到加密key
				// 参数解密
				String decryptBill = XiChengAESUtils.decrypt(bill, securitKey);
				// 转成json对象
				JSONObject jsonBill = JSON.parseObject(decryptBill);
				// String customerOrderId = jsonBill.getString("orderId");//
				// 客户订单号
				// String orderTime = jsonBill.getString("orderTime");// 订单请求时间
				String orderId = jsonBill.getString("channelOrderId");// 新平台订单号
				String agentName = "xicheng";

				// String agentName = (String)param.get("terminalName");// 客户名
				// String agentOrderId = (String)param.get("customerOrderId");//
				// 客户订单号
				Map<String, Object> agent = agentDao.selectByAgentNameForOrder(agentName);
				if (agent == null) {
					json.put("status", "0");
					json.put("resultCode", "50002");
					json.put("resultDesc", "请求失败,非法的身份ID");
					return json.toJSONString();
				}
				String agentId = (String) agent.get("id");// 代理商ID
				Map<String, Object> queryParam = new HashMap<String, Object>();
				queryParam.put("agentId", agentId);
				queryParam.put("orderId", orderId);
				List<Map<String, Object>> orders = orderDao.selectAll(queryParam);
				if (orders.size() < 1) {
					json.put("status", "0");
					json.put("resultCode", "10003");
					json.put("resultDesc", "请求失败,订单号为空");
					return json.toJSONString();
				}
				Map<String, Object> order = orders.get(0);
				String status = (String) order.get("status");// 订单状态
				// JSONObject json = new JSONObject();
				json.put("status", "1");
				json.put("resultCode", "00000");
				json.put("resultDesc", "请求成功");
				JSONObject resultBill = new JSONObject();
				resultBill.put("orderId", orderId);
				resultBill.put("customerOrderId", order.get("agentOrderId"));
				resultBill.put("orderTime", order.get("applyDate"));
				resultBill.put("phoneNumber", order.get("phone"));
				String spec = new Double(order.get("value") + "").intValue() + "";
				String providerId = order.get("providerId") + "";
				// 产品ID;
				String prodId = XiChengFlowDeal.getProdId(spec, providerId);
				resultBill.put("prodId", prodId);
				double fee = Double.parseDouble(order.get("fee") + "");
				double agentDiscount = Double.parseDouble(order.get("agentDiscount") + "");
				double payAmount = fee * agentDiscount * 1000;
				resultBill.put("payAmount", payAmount);

				if (status.equals("3")) {
					status = "1";
				} else if (status.equals("1")) {
					status = "2";
				} else if (status.equals("2") || status.equals("4")) {
					status = "0";
				}
				resultBill.put("orderStatus", status);
				json.put("bill", resultBill);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json.toJSONString();
	}

	/**
	 * 签名
	 * 
	 * @param paramMap
	 * @param privateKeyStr
	 * @return
	 * @throws Exception
	 */
	public static String sign(Map<String, Object> paramMap, String privateKeyStr) throws Exception {
		byte[] keyByteArray = new BASE64Decoder().decodeBuffer(privateKeyStr);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 设置请求参数排列顺序
		String content = setParamStr(paramMap);
		System.out.println("加密参数顺序如下[" + content + "]");
		// MD5加密
		String md5 = MD5.ToMD5(content);

		byte[] rsa = encodeBytePrivate(md5.getBytes(), privateKey);
		return Hex.encodeHexString(rsa);
	}

	/**
	 * RSA
	 * 
	 * @param content
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeBytePrivate(byte[] content, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(content);
	}

}
