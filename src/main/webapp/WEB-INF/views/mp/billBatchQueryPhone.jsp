<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
	request.setAttribute("basePath", basePath);
	out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath }">

<title>话费批号查询</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="${basePath }ui/inspinia/css/jquery-labelauty.css">
<style>
ul { list-style-type: none;}
li { display: inline-block;}
li { margin: 10px 0;}
input.labelauty + label { font: 12px "Microsoft Yahei";}
</style>
<c:import url="../head.jsp" />
</head>

 <body>
	<div id="wrapper">
		<c:import url="../left.jsp" />
		<div id="page-wrapper" class="gray-bg dashbard-1">
			<div class="row border-bottom">
				<c:import url="../topnav.jsp" />
			</div>

			<div class="row wrapper border-bottom white-bg page-heading">
				<div class="col-lg-10">
					<h3>话费查询</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>话费批量查询</strong></li>
					</ol>
				</div>
				
				<iframe id="downloadFrame" style="display: none"></iframe>
			</div>
			
			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom">
					<form id="fileForm" name="fileForm"  method="post" action="${basePath }batchQueryFilterPhone" enctype="multipart/form-data" style="width: 1px; height: 1px;">
	          			<input name="action" type="hidden" value="importNumber">
	          			<input required  type="file" id="numberFile"  name="numberFile" onchange="submitForm.click()" style="position: absolute; filter: alpha(opacity=0); opacity: 0; width: 30px;" size="1" accept=".txt">
	          			<shiro:hasPermission name="batchFilterPhone:post">                   			
	      				<input type="submit" id="submitForm" style="display: none;">
	      				</shiro:hasPermission>
	      			</form>
	      			<form id="editForm"  method="POST"  role="form" class="form-horizontal">
	      			<input type="hidden" id="terminalName">
						<div class="form-group">
							<label class="col-sm-2 control-label"><B>查询手机号码</B></label>
							<div class="col-sm-7">
								<textarea class="form-control" id="mobilenumbers" name="mobilenumbers" oninput="lockSubmitButton();"
					   			 placeholder="格式为11位手机号码,多条请用','分隔" vMin="11" cols=40 rows=6></textarea>
							</div>
							
							<div class="col-sm-2">
								<input type="button" class="btn btn-success" id="importTxt" name="importTxt" value="TXT导入"/>
								<input type="button" class="btn btn-warning" id="checkMobile" name="checkMobile" value="检测"/>
							</div>
							<span class="help-inline col-sm-2" id="phonemsg"> </span>
						</div>
						
						<div id="meg">
						</div>			
							
						<div class="form-group">
							<label class="col-sm-2 control-label"></label>
							<label id="countNumMsg" class="col-sm-4 control-label msg-label-con"></label>
						</div>
						<div class="form-group">
							<div class="col-sm-12 col-sm-offset-5">
								<button type="button" id="queryButtion" class="btn btn-primary" style="display: none" onclick="submitOrder()">
									立即查询<i class="fa fa-arrow-circle-right"></i>
								</button>
							</div>
						</div>
					</form>
				</div>
				<div class="ibox-content m-b-sm border-bottom" id="phoneInfo" style="display: none"></div>
			</div>
		<c:import url="../copyright.jsp" />
		<c:import url="../foot.jsp" />
		<script src="${basePath }js/lib/jquery.form.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery-labelauty.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/shCore.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/verify.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/billBatchQueryPhone.js"></script>
</body>
</html>