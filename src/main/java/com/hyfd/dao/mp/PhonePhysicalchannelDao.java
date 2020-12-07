package com.hyfd.dao.mp;

import com.hyfd.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface PhonePhysicalchannelDao extends BaseDao {

    int selectCount(@Param("map") Map<String, Object> maps);
}
