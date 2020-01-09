package com.hyfd.common.utils;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

/**
 * 邮件发送工具类
 * 
 * @author Administrator
 * @create 2017-11-28
 * yfuqzgldwwhdbceb
 */
public class MailUtil {

	protected final Logger logger = Logger.getLogger(getClass());

	private static Properties props;
	// 发件人的邮箱地址
	private static String userName = "1310993083@qq.com";
	// 发件人的密码
	private static String password = "buhhzyjpdcshbaef";

	static {
		props = new Properties();
		// 指定协议
		props.put("mail.transport.protocol", "smtp");
		// 主机 smtp.qq.com
		props.put("mail.smtp.host", "smtp.qq.com");
		// 端口
		props.put("mail.smtp.port", 587);
		// 用户密码认证
		props.put("mail.smtp.auth", "true");
		// 调试模式
//		props.put("mail.debug", "true");
	}

	public static void send(Map<String, String> user) {
		try {
			// 创建邮件会话
			Session session = Session.getInstance(props);
			// 创建邮件对象
			MimeMessage msg = new MimeMessage(session);
			// 设置发件人
			msg.setFrom(new InternetAddress(userName));
			// 设置邮件收件人
			msg.setRecipients(Message.RecipientType.TO, user.get("recipients"));
			// 标题
			msg.setSubject(user.get("title"));
			// 发送时间
			msg.setSentDate(new Date());
			// 发送内容
			msg.setText(user.get("count"));

			// 5. 发送
			Transport trans = session.getTransport();
			trans.connect(userName, password);
			trans.sendMessage(msg, msg.getAllRecipients());
			trans.close();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送带excel附件的邮件
	 * @author lks 2018年2月6日下午3:37:10
	 * tos String[] 接受者地址
	 * copyto String[] 抄送者地址
	 * title String 主题
	 * context String 内容
	 * fileAddr String 文件地址
	 * fileName String 文件名
	 */
	 public static void sendExcel(Map<String,Object> mail) throws Exception {
	        // 1.创建Session，包含邮件服务器网络连接信息
	        // 指定邮件的传输协议，smtp;同时通过验证
	        Session session = Session.getDefaultInstance(props);
	        // 开启调试模式
	        session.setDebug(true);

	        // 2.编辑邮件内容
	        Message message = new MimeMessage(session);
	        // 设置邮件消息头
	        message.setFrom(new InternetAddress(userName));
	        // 创建邮件接收者地址
	        String[] tos = (String[]) mail.get("tos");
	        if (tos != null && tos.length != 0) {
	            InternetAddress[] to = new InternetAddress[tos.length];
	            // 设置邮件消息的发送者
	            for (int i = 0; i < tos.length; i++) {
	                to[i] = new InternetAddress(tos[i]);
	            }
	            message.setRecipients(Message.RecipientType.TO, to);
	        }
	        // 设置邮件抄送者地址
	        String[] copyto = (String[]) mail.get("copyto");
	        if (copyto != null && copyto.length != 0) {
	            InternetAddress[] toCC = new InternetAddress[copyto.length];
	            // 设置邮件消息的发送者
	            for (int i = 0; i < copyto.length; i++) {
	                toCC[i] = new InternetAddress(copyto[i]);
	            }
	            message.addRecipients(Message.RecipientType.CC, toCC);
	        }

	        // 设置主题
	        message.setSubject(mail.get("title")+"");
	        // 设置邮件消息内容、包含附件
	        Multipart msgPart = new MimeMultipart();
	        message.setContent(msgPart);

	        MimeBodyPart body = new MimeBodyPart();   // 正文
	        MimeBodyPart attach = new MimeBodyPart(); // 附件

	        msgPart.addBodyPart(body);
	        msgPart.addBodyPart(attach);

	        // 设置正文内容
	        body.setContent(mail.get("context"), "text/html;charset=utf-8");
	        // 设置附件内容
	        attach.setDataHandler(new DataHandler(new FileDataSource(mail.get("fileAddr")+"")));
	        attach.setFileName((MimeUtility.encodeText(mail.get("fileName")+"")));

	        message.saveChanges();

	        // 3.发送邮件
	        Transport trans = session.getTransport();
	        trans.connect(userName, password);
	        trans.sendMessage(message, message.getAllRecipients());
	        trans.close();
	    }
	
}