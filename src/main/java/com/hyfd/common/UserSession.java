package com.hyfd.common;

import java.util.Date;
import java.util.List;

public class UserSession {
	private int userId;
	private int usCredits;
	private String username;
	private String loginid;
	private Date lastChangePwd;
	private Date lastLoginTime;
	private String departId;
	private String departName;
	private List roles;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUsCredits() {
		return usCredits;
	}
	public void setUsCredits(int usCredits) {
		this.usCredits = usCredits;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getLastChangePwd() {
		return lastChangePwd;
	}
	public void setLastChangePwd(Date lastChangePwd) {
		this.lastChangePwd = lastChangePwd;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getDepartId() {
		return departId;
	}
	public void setDepartId(String departId) {
		this.departId = departId;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public List getRoles() {
		return roles;
	}
	public void setRoles(List roles) {
		this.roles = roles;
	}	
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
}
