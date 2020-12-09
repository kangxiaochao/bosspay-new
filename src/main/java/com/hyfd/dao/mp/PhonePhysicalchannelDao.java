package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;
import java.util.Map;
public interface PhonePhysicalchannelDao extends BaseDao {

    int selectCount(Map<String, Object> maps);

    int phonePhysicalchanneladd(Map<String, Object> myBill);
}
