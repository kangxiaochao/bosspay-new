package com.hyfd.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.service.mp.DiXinTongKeyService;

/**
 * @功能描述：	自动更新迪信通秘钥,如果失效请参考rcmp系统com.hyfd.dixintong.DixintongTestThree的testMiyao()方法
 *
 * @作者：zhangpj		@创建时间：2018年4月28日
 */
@Component
public class DixintongKeyTask {
	@Autowired
	DiXinTongKeyService diXinTongKeyService;

	@Scheduled(fixedDelay = 60000)//密钥更新时间待定
	public void saveDixintongKey() {
		diXinTongKeyService.updateDixintongKey(0);
	}
}
