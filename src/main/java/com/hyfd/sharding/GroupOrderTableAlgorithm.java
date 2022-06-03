package com.hyfd.sharding;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.DateTimeUtils;

@Component
public class GroupOrderTableAlgorithm implements StandardShardingAlgorithm<String> {

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "GROUP_ORDER";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
		// TODO Auto-generated method stub
		String dateStr = shardingValue.getValue();
		Date date = DateTimeUtils.parse(dateStr, "yyyy-MM-dd hh:mm:ss");
		String tableSuffix = DateTimeUtils.formatDate(date, "yyyyMM");
//		for (String s : availableTargetNames) {
//            System.out.println("节点配置表名为: "+s);
//        }
		return shardingValue.getLogicTableName() + "_" + tableSuffix;
	}

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames,
			RangeShardingValue<String> shardingValue) {
		throw new UnsupportedOperationException("暂时不支持区间查询");
	}

}
