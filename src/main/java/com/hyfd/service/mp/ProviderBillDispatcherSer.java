package com.hyfd.service.mp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyfd.common.BaseJson;
import com.hyfd.dao.mp.ProviderBillDispatcherDao;
import com.hyfd.dao.mp.ProviderDao;
import com.hyfd.dao.mp.ProviderGroupBillRelDao;
import com.hyfd.dao.mp.ProviderPhysicalChannelDao;
import com.hyfd.service.BaseService;

@Service
@Transactional
public class ProviderBillDispatcherSer extends BaseService
{
    
    public Logger log = Logger.getLogger(this.getClass());
    
    @Autowired
    ProviderDao providerDao;
    
    @Autowired
    ProviderBillDispatcherDao providerBillDispatcherDao;
    
    @Autowired
    ProviderPhysicalChannelDao providerPhysicalChannelDao;
    
    @Autowired
    ProviderGroupBillRelDao providerGroupBillRelDao;
    
    public String providerBillDispatcherEditPage(String id)
    {
        try
        {
            Map<String, Object> provider = providerDao.getProviderById(id);
            Session session = getSession();
            session.setAttribute("provider", provider);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerBillDispatcherEdit";
    }
    
    public String providerBillDispatcherPut(HttpServletRequest req, String providerId)
    {
        try
        {
            Map<String, Object> providerBillDispatcherMap = getMaps(req);
            String providerBillDispatcherStr = (String)providerBillDispatcherMap.get("dataDispatcherList");
            JSONArray providerBillArray = JSON.parseArray(providerBillDispatcherStr);
            
            Map<String, Object> userInfoMap = getUser(); // ????????????????????????
            
            // delete data by provider_id
            providerBillDispatcherDao.providerBillDispatcherDelByProviderId(providerId);
            for (Object data : providerBillArray)
            {
                JSONObject myDt = (JSONObject)data;
                Map<String, Object> dbDt = JSON.parseObject(myDt.toString(), Map.class);
                dbDt.put("create_user", userInfoMap.get("suId"));
                providerBillDispatcherDao.providerBillDispatcherAdd(dbDt);
            }
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        
        return "providerListPage";
    }
    
    public String getProviderBillDispatcherByProviderId(HttpServletRequest req)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Map<String, Object> reqMap = getMaps(req);
            String provider_id = (String)reqMap.get("provider_id");
            List<Map<String, Object>> dataList =
                providerBillDispatcherDao.getProviderBillDispatcherByProverId(provider_id);
            String dataListJson = BaseJson.listToJson(dataList);
            sb.append(dataListJson);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return sb.toString();
    }
    
    /**
     * @??????????????? ?????? ??????????????????????????????????????????????????????
     *
     * @?????????zhangpj @???????????????2017???5???12???
     * @param req
     * @return
     */
    public String getProviderBillDispatcherToProvinceCode(HttpServletRequest req)
    {
        Map<String, Object> reqMap = getMaps(req);
        List<Map<String, Object>> provinceCodeList = providerBillDispatcherDao.selectAll(reqMap);
        return JSONObject.toJSONString(provinceCodeList);
    }
    
    /**
     * @Title:providerPhysicalChannelDispatcherEdit
     * @Description: ?????????????????????????????????(?????????????????????????????????????????????)
     * @author CXJ
     * @date 2017???5???22??? ??????11:31:14
     * @param @param id
     * @param @return
     * @return String ????????????
     * @throws
     */
    public String providerPhysicalChannelDispatcherEdit(String id)
    {
        try
        {
            Map<String, Object> providerPhysicalChannel = providerPhysicalChannelDao.getProviderPhysicalChannelById(id);
            Session session = getSession();
            session.setAttribute("providerPhysicalChannel", providerPhysicalChannel);
        }
        catch (Exception e)
        {
            getMyLog(e, log);
        }
        return "mp/providerPhysicalChannelDispatcherEdit";
    }
    
    /**
     * @Title:querySelectDispatcherByPhysicalId
     * @Description: ???????????????????????????????????????????????????(?????????????????????????????????????????????)
     * @author CXJ
     * @date 2017???5???22??? ??????11:31:45
     * @param @param physicalId
     * @param @return
     * @return List<Map<String,Object>> ????????????
     * @throws
     */
    public List<Map<String, Object>> querySelectDispatcherByPhysicalId(String physicalId)
    {
        
        return providerBillDispatcherDao.querySelectDispatcherByPhysicalId(physicalId);
    }
    
    /**
     * @Title:submitPoviderDispatcherBill
     * @Description: ???????????????????????????(?????????????????????????????????????????????)
     * @author CXJ
     * @date 2017???5???22??? ??????11:32:21
     * @param @param physicalId
     * @param @param req
     * @param @return
     * @return boolean ????????????
     * @throws
     */
    public boolean submitPoviderDispatcherBill(String physicalId, HttpServletRequest req)
    {
        Enumeration<String> e = req.getParameterNames();
        Map<String, Object> dispatcherMap = getMaps(req);
        String dispacherChannel = (String)dispatcherMap.get("dataMap");
        Map<String, Object> userInfoMap = getUser();
        String suId = userInfoMap.get("suId") + "";
        Map<String, Object> dispatcherChannelJson = JSON.parseObject(dispacherChannel);
        // ?????????????????????
        providerBillDispatcherDao.deleteByPhysicalId(physicalId);
        boolean flag = false;
        if (dispatcherChannelJson == null || dispatcherChannelJson.size() <= 0)
        {
            flag = true;
        }
        for (Map.Entry<String, Object> provider : dispatcherChannelJson.entrySet())
        {
            Map<String, Object> insMap = new HashMap<String, Object>();
            String providerId = provider.getKey();
            JSONArray province = JSON.parseArray(provider.getValue().toString());
            for (int i = 0; i < province.size(); i++)
            {
                String provinceCode = province.get(i).toString(); // ?????? jsonarray ????????????????????????????????? json ??????
                insMap.put("providerId", providerId);
                insMap.put("physicalId", physicalId);
                insMap.put("provinceCode", provinceCode);
                insMap.put("createUser", suId);
                insMap.put("updataUser", suId);
                
                int result = providerBillDispatcherDao.insertDispatcher(insMap);
                if (result == 1)
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                }
            }
        }
        return flag;
    }
    
    public String changeDispatcherStatus(HttpServletRequest req)
    {
        Map<String, Object> reqMap = getMaps(req);
        String flag = reqMap.get("flag") + "";
        String id = reqMap.get("id") + "";
        String str = "";
        Map<String, Object> map = providerBillDispatcherDao.selectByPrimaryKey(id);
        Map<String, Object> parmMap = new HashMap<String, Object>();
        parmMap.put("providerId", map.get("provider_id"));
        parmMap.put("physicalId", map.get("provider_physical_channel_id"));
        // map.put("provinceCode", map.get("province_code"));
        if ("1".equals(flag))
        {
            flag = "0";
            parmMap.put("flag", flag);
        }
        else
        {
            flag = "1";
            parmMap.put("flag", flag);
        }
        reqMap.put("flag", flag);
        // providerBillDiscountDao.updateDiscountByDispatcher(map);
        int p = providerGroupBillRelDao.updateGroupBillByDispatcher(parmMap);
        int i = providerBillDispatcherDao.updateDelFlag(parmMap);
        if (i > 0)
        {
            str = "success";
        }
        else
        {
            str = "fail";
        }
        return str;
    }
}
