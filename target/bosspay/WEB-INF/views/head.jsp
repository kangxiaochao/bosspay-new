<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>



<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);

String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
	<link href="${basePath }ui/inspinia/img/logo_hyfd1.png" rel="icon" />
	
    <link href="${basePath }ui/inspinia/css/bootstrap.min.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/animate.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/toastr/toastr.min.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/js/plugins/gritter/jquery.gritter.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/jQueryUI/jquery-ui-1.10.4.custom.min.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/jqGrid/ui.jqgrid.css" rel="stylesheet">
	<link href="${basePath }ui/inspinia/css/plugins/chosen/bootstrap-chosen.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/style.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/iCheck/custom.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/touchspin/jquery.bootstrap-touchspin.min.css" rel="stylesheet">
    <link href="${basePath }css/project/bootstrap-switch.css" rel="stylesheet">
    <link href="${basePath }css/project/layui.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    <!-- 上传css -->
    <link href="${basePath }ui/inspinia/css/plugins/jasny/jasny-bootstrap.min.css" rel="stylesheet">
    <!-- zTree -->
    <link href="${basePath }js/lib/zTree_v3/css/demo.css" rel="stylesheet">
	<link href="${basePath }js/lib/zTree_v3/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">