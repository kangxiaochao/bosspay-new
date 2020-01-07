package com.hyfd.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter;

public class RestFilter extends HttpMethodPermissionFilter{
	
	public Logger log = Logger.getLogger(this.getClass());
	
	public boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws IOException {
		HttpServletRequest req=(HttpServletRequest) request;
		String resourceFront=req.getRequestURI();
		String[] tmpArray=resourceFront.split("/");
		log.debug(resourceFront);
		
		String permissionMethod=req.getMethod().toLowerCase();
		
		String permission=tmpArray[2]+":"+permissionMethod;
		
		Subject subject = getSubject(request, response);
		
		log.debug(permission+"|"+subject.isPermitted(permission));
		
		return subject.isPermitted(permission);
	}


}
