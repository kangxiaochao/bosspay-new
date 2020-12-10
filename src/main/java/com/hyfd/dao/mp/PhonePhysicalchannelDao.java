package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;

import java.util.List;
import java.util.Map;
public interface PhonePhysicalchannelDao extends BaseDao {

    int selectCount(Map<String, Object> maps);

    int phonePhysicalchanneladd(Map<String, Object> myBill);

    int listAddPhonePhysicalchannel(List<Map<String, Object>> sections);
}
