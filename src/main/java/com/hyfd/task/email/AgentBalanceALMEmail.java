package com.hyfd.task.email;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hyfd.common.utils.DateTimeUtils;
import com.hyfd.common.utils.MailUtil;
import com.hyfd.dao.mp.AgentEmailTask2Dao;

/**
 * 定时查询代理商余额 达到限额自动报警
 * @author Administrator
 * @center 2017-11-30
 */
@Component
public class AgentBalanceALMEmail {

	@Autowired
	AgentEmailTask2Dao emailDao;
	
	/**
	 * 定时查询代理商功能余额  进行email发送提醒
	 */
	@Scheduled(fixedDelay = 1800000)
	public void Email(){
		List<Map<String, Object>> email = AwaitEmail();
		if(email.size() > 0) {
			for(int i=0;i<email.size();i++){
				Map<String, Object> mail = email.get(i);
				Map<String, String> user = new HashMap<String, String>();
				user.put("recipients", mail.get("mail")+"");
				user.put("title", "济南浩百充值平台余额提醒");
				String count =	"尊敬的  "+mail.get("name").toString()+" 用户：\n"
									+ "       您当前的余额为"+new Double(mail.get("balance")+"").toString()+",已不足"+new Double(mail.get("quota")+"").toString()+",请及时充值。";
				user.put("count", count);
				MailUtil.send(user);
				emailDao.updateAll(mail.get("id").toString());
		 	}
		}
	}
	
	/**
	 * 查询所有代理余额不足的，并返回所有需要发送的email
	 * @return
	 */
	public List<Map<String, Object>> AwaitEmail(){
		List<Map<String, String>> task2 = emailDao.selectAllAgent();
		for (int i = 0; i < task2.size(); i++) {
			Map<String, String> mail = task2.get(i);
			mail.put("id", UUID.randomUUID().toString().replace("-", ""));
			mail.put("state", "0");
			mail.put("currentTime", DateTimeUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
			emailDao.insertSelective(mail);
		}
		List<Map<String, Object>> email = emailDao.selectAllEmail();
		return email;
	}
	//{currentTime=2017-12-01 09:54:31, agentId=1450c364fea34cccb19f1ea598e65e91, id=8541e72f07a6489e8c422275cd931bd4, state=0, email=wanyefu@163.com}
}
