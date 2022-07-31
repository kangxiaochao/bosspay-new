package com.hyfd.service.mp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyfd.common.GlobalSetHyfd;
import com.hyfd.common.utils.ExceptionUtils;
import com.hyfd.common.utils.MapUtils;
import com.hyfd.dao.mp.BillPkgDao;
import com.hyfd.dao.mp.OrderDao;
import com.hyfd.dao.mp.PhoneSectionDao;
import com.hyfd.dao.mp.ProviderAccountChargeDao;
import com.hyfd.dao.mp.ProviderAccountDao;
import com.hyfd.dao.mp.ProviderBillDiscountDao;
import com.hyfd.dao.mp.ProviderChargeRecordDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
public class ProviderAccountSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    ProviderDao providerDao; // 运营商
    
    @Autowired
    ProviderBillDiscountDao providerBillDiscountDao;// 分销商折扣
    
    @Autowired
    ProviderAccountDao providerAccountDao;// 代理商余额
    
    @Autowired
    ProviderAccountChargeDao providerAccountChargeDao;// 代理商加款记录
    
    @Autowired
    ProviderChargeRecordDao providerChargeRecordDao;// 代理商扣款记录
    
    @Autowired
    BillPkgDao billPkgDao;// 流量包
    
    @Autowired
    PhoneSectionDao phoneSectionDao;// 号段
    
    @Autowired
    OrderDao orderDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;

    public String providerAccountEditPage(String id)
    {
        try
        {
            Map<String, Object> physicalProvider = providerPhysicalChannelDao.selectByPrimaryKey(id);
            // String providerId = (String)provider.get("provider_id");
            String balance = providerAccountDao.getBalanceByProviderId(id);
            physicalProvider.put("balance", balance);
            Session session = getSession();
            session.setAttribute("provider", physicalProvider);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerAccountEdit";
    }
    
    public Map<String, Object> getProviderAccountByProviderId(String providerId)
    {
        Map<String, Object> providerAccount = null;
        try
        {
            providerAccount = providerAccountDao.getProviderAccountByProviderId(providerId);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return providerAccount;
    }
    
    public String providerAccountEdit(HttpServletRequest req, String id)
    {
        try
        {
            boolean flag = false;
            Map<String, Object> myBill = getMaps(req);
            String providerId = (String)myBill.get("providerId");
            String f = (String)myBill.get("fee");
            String remarks = (String) myBill.get("remarks");
            double fee = 0.0;
            if (f != null && f != "")
            {
                fee = Double.parseDouble(f);
            }
            Map<String, Object> providerAccount = getProviderAccountByProviderId(providerId);
            boolean updateFlag = false;
            double balance = 0.0;
            double bala = 0.0;
            int rows = 0;
            if (providerAccount != null)
            {
                String ba = providerAccount.get("balance") + "";
                if (ba != null && ba != "")
                {
                    balance = Double.parseDouble(ba);
                    updateFlag = true;
                }
                if (!updateFlag)
                {
                    // 没有这个余额就添加
                    rows = providerAccountDao.providerAccountAdd(myBill);
                }
                else
                {
                    // 有这个余额就更新
                    bala = balance + fee;
                    myBill.put("balance", bala);
                    rows = providerAccountDao.providerAccountEdit(myBill);
                }
            }
            else
            {
                rows = providerAccountDao.providerAccountAdd(myBill);
                bala = balance + fee;
            }
            if (rows > 0)
            {
                flag = true;
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("providerId", providerId);
                m.put("fee", fee);
                m.put("balanceBefore", balance);
                m.put("balanceAfter", bala);
                m.put("remarks", remarks);
                providerAccountChargeDao.providerAccountChargeAdd(m);
            }
            Session session = getSession();
            session.setAttribute(GlobalSetHyfd.backMsg, flag ? "修改成功" : "修改失败");
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "providerPhysicalChannelListPage";
    }
    
    /**
     * 上家扣款方法
     * 
     * @author lks 2016年12月16日上午10:34:10
     * @param order
     * @return
     */
    public boolean Charge(Map<String, Object> order)
    {
        boolean flag = false;
        try
        {
            String dispatcherProviderId = (String)order.get("dispatcherProviderId");// 获取分销商ID
            String providerId = order.get("providerId") + "";// 运营商ID
            String orderId = (String)order.get("orderId");// 获取平台订单号
            String billPkgId = (String)order.get("pkgId");// 获取产品ID
            String bizType = (String)order.get("bizType");// 订单类型
            String phone = (String)order.get("phone");// 手机号
            String section = (phone.length() == 13) ? phone.substring(0, 5) : phone.substring(0, 7);// 获取号段
            // TODO 号段从缓存中获取
            Map<String, Object> sectionMap = phoneSectionDao.selectBySection(section);
            String provinceCode = (String)sectionMap.get("province_code");// 省份代码
            String cityCode = (String)sectionMap.get("city_code");// 城市代码
//            Map<String, Object> pkg = new HashMap<String, Object>();
//            if (bizType.equals("2"))
//            {
//                pkg = billPkgDao.selectByPrimaryKey(billPkgId);// 获取流量包
//            }
            //String price = pkg.get("price") + "";
            String price = order.get("fee") + "";
            String balance = providerAccountDao.getBalanceByProviderId(dispatcherProviderId);
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("providerId", providerId);
            param.put("providerPhysicalChannelId", dispatcherProviderId);
            param.put("billPkgId", billPkgId);
            param.put("provinceCode", provinceCode);
            param.put("cityCode", cityCode);
            List<Map<String, Object>> discountList = new ArrayList<Map<String, Object>>();
            Double discount = null;
            Double realDiscount = null;
            String providerDiscountId = "";
            if (bizType.equals("2"))
            {
                param.remove("cityCode");
                param.remove("billPkgId");
                param.put("billPkgId", billPkgId);
                discountList = providerBillDiscountDao.selectDiscountMap(param);// 获取流量运营商折扣
                if (discountList.size() > 0)
                {
                    Map<String, Object> discountMap = discountList.get(0);
                    providerDiscountId = discountMap.get("id") + "";
                    discount = Double.parseDouble(discountMap.get("discount") + "");
                    realDiscount = Double.parseDouble(discountMap.get("real_discount") + "");
                }
                // else
                // {
                // param.put("provinceCode", "全国");
                // discountMap = providerBillDiscountDao.selectDiscountMap(param);// 获取流量运营商折扣
                // if (discountMap != null)
                // {
                // providerDiscountId = discountMap.get("id") + "";
                // discount = Double.parseDouble(discountMap.get("discount") + "");
                // }
                // }
            }
            // 保存上家折扣数据
            order.put("providerDiscountId", providerDiscountId);
            order.put("providerDiscount", realDiscount);
            orderDao.updateByPrimaryKeySelective(order);
            Map<String, Object> chargeParam = new HashMap<String, Object>();
            chargeParam.put("providerId", dispatcherProviderId);
            chargeParam.put("discount", Double.parseDouble(price) * discount);
            int chargeFlag = providerAccountDao.charge(chargeParam);
            if (chargeFlag > 0)
            {
                Map<String, Object> providerChargeRecord = new HashMap<String, Object>();
                providerChargeRecord.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
                providerChargeRecord.put("orderId", order.get("id"));
                providerChargeRecord.put("providerId", dispatcherProviderId);
                providerChargeRecord.put("balanceBefore", balance);
                providerChargeRecord.put("fee", Double.parseDouble(price) * discount);
                providerChargeRecord.put("balanceAfter", Double.parseDouble(balance) - Double.parseDouble(price) * discount);
                providerChargeRecord.put("applyDate", new Timestamp(System.currentTimeMillis()));
                int count = providerChargeRecordDao.insertSelective(providerChargeRecord);
                if (count > 0)
                {
                    flag = true;
                }
            }
        }
        catch (Exception e)
        {
            log.error(MapUtils.toString(order) + "为上家扣款出错" + ExceptionUtils.getExceptionMessage(e));
        }
        return flag;
    }
    
}
