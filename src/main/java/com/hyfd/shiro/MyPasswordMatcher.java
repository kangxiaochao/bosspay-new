package com.hyfd.shiro;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.subject.PrincipalCollection;

import com.hyfd.common.ToolPbkdf2;


public class MyPasswordMatcher extends SimpleCredentialsMatcher{
	
	public boolean doCredentialsMatch(AuthenticationToken token,
			AuthenticationInfo info) {
		UsernamePasswordToken upt=(UsernamePasswordToken) token;
		String inputPassword=String.valueOf(upt.getPassword());
		PrincipalCollection pc=info.getPrincipals();
		List<Map<String, Object>> pcList=pc.asList();
		Map<String, Object> userMap=pcList.get(0);
		byte[] salt=(byte[]) userMap.get("salt");
		byte[] encryptedPassword=(byte[]) userMap.get("password");
		boolean isRightPassword=false;
		try{
			isRightPassword=ToolPbkdf2.authenticate(inputPassword, encryptedPassword, salt);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isRightPassword;
	}
	

}
