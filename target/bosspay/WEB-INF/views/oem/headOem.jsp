<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
    <link href="${basePath }ui/inspinia/css/bootstrap.min.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/animate.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/style.css" rel="stylesheet"> 