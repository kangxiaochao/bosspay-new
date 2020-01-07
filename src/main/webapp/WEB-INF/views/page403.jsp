<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>

<!DOCTYPE HTML>
<html>
  <head>
    <base href="${basePath }">
    
    <title>index</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<c:import url="head.jsp" />
  </head>
  
  <body>
	<div class="wrapper wrapper-content">
	    <div class="middle-box text-center animated flipInY"
	         style="margin-top: 100px;">
	        <div class="widget blue-bg p-lg text-center">
	            <div class="m-b-md">
	                <h3>403</h3>
	                <h3 class="font-bold m-sm" style="margin: 20px auto;">无权访问</h3>
	                <small>没有请求该资源的权限。</small>
	            </div>
	        </div>
	    </div>
	</div>
  </body>
</html>
