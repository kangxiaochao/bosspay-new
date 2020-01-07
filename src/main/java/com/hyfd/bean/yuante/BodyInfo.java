package com.hyfd.bean.yuante;


public class BodyInfo {
	
	private String cityCode;//受理地市,填写0
	private String accountId;//帐户标识，填写0
	private String userId;//用户标识, 填写0
	private String dealerId;//营业厅代码
	private String ifContinue;//缴费方式30代理商缴费
	private String notifyDate;//缴费时间YYYYMMDD hh24miss如：20150331185555
	private String operator;//操作员账号，同UserName
	private String payFee;//充值金额
	private String serviceId;//业务号码
	private String serviceKind;//业务类型： 8
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDealerId() {
		return dealerId;
	}
	public void setDealerId(String dealerId) {
		this.dealerId = dealerId;
	}
	public String getIfContinue() {
		return ifContinue;
	}
	public void setIfContinue(String ifContinue) {
		this.ifContinue = ifContinue;
	}
	public String getNotifyDate() {
		return notifyDate;
	}
	public void setNotifyDate(String notifyDate) {
		this.notifyDate = notifyDate;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getPayFee() {
		return payFee;
	}
	public void setPayFee(String payFee) {
		this.payFee = payFee;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceKind() {
		return serviceKind;
	}
	public void setServiceKind(String serviceKind) {
		this.serviceKind = serviceKind;
	}
	
}
