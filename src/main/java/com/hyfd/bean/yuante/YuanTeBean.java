package com.hyfd.bean.yuante;

import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
public class YuanTeBean { 
	
	private String streamNo;//消息流水 （3位系统标识 + yyyymmddhhmiss + 8位流水）
	private String systemId;//需要统计接入boss系统的外系统个数，由boss系统统一分配。编码从101开始，依次为102,103…
	private String userName;//登陆系统的用户名，boss系统分配
	private String userPwd;//用户对应的密码，boss系统分配 
	private String intfType;//接口类型：1异步接口    0同步接口
	private String rechargeType;//充值类型：0 代理商充值  2工单状态查询  1 余额查询
	private String notifyURL;//异步通知接口地址   RechargeType=1
	private List<BodyInfo> bodys;//请求消息体
	
	public String getStreamNo() {
		return streamNo;
	}
	public void setStreamNo(String streamNo) {
		this.streamNo = streamNo;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPwd() {
		return userPwd;
	}
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}
	public String getIntfType() {
		return intfType;
	}
	public void setIntfType(String intfType) {
		this.intfType = intfType;
	}
	public String getRechargeType() {
		return rechargeType;
	}
	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public List<BodyInfo> getBodys() {
		return bodys;
	}
	public void setBodys(List<BodyInfo> bodys) {
		this.bodys = bodys;
	}
	
	
	
	
	
	
	
}
