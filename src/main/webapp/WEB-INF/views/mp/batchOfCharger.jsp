<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath }">

<title>小面额话费批量充值</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />

<link href="${basePath }css/project/laydate.css" rel="stylesheet">
</head>

<body>
	<c:import url="../left.jsp" />

	<div id="page-wrapper" class="gray-bg dashbard-1">
		<div class="row border-bottom">
			<c:import url="../topnav.jsp" />
		</div>

		<div class="row wrapper border-bottom white-bg page-heading">
			<div class="col-lg-10">
				<br />
				<ol class="breadcrumb">
					<li><a href="${basePath }mainPage">主页</a></li>
					<li><a>系统管理</a></li>
					<li class="active"><strong>小面额话费批量充值</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5><small>批量充值</small></h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
							<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a> 
							<a class="close-link"><i class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="upload" method="post" action="" enctype="multipart/form-data"  class="form-horizontal">
							<div class="form-group">
								<label class="col-md-2 control-label">文&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;件：&nbsp;</label>
								<div class="col-md-8">
									<input type="file" name="file" />
								</div>
								<span class="help-block m-b-none">文件格式必须为.xls</span>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">充值密码：&nbsp;</label>
								<div class="col-md-8">
									<input type="password" class="form-control" name="password" onblur="sub()"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">扣款账号：</label>
								<div class="col-md-8">
									<select name="agentId" id="agentId" class="chosen-select">
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">选择通道：</label>
								<div class="col-md-8">
									<select name="state" onchange="gradeChange()" id="state" class="form-control">
							   			<option value="0">国美</option>
							   			<option value="1">远特</option>	
							   			<option value="2">兔兔币</option>	
							   		</select>
								</div>
							</div>
							<div id="yan" style="display: none;">
								<div class="form-group">
								<label class="col-md-2 control-label">充值工号：&nbsp;</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="account" value="">
								</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">工号密码：&nbsp;</label>
									<div class="col-md-8">
										<input type="password" class="form-control" name="pass" value="">
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">验&nbsp;证&nbsp;&nbsp;码：&nbsp;</label>
									<div class="col-md-8">
										<input type="text" class="form-control" name="msgVerify">
									</div>
									<span class="help-block m-b-none"> &nbsp;&nbsp;<a id="sd"
										href="javascript:login4Cookie()">发送验证码</a> <span id="msg"
										style="color: red;"></span></span>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">复充次数：&nbsp;</label>
									<div class="col-md-8">
										<input type="number" class="form-control" name="sum" max="10" min="2" value="2">
									</div>
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="addbatchOfCharger:post"><button type="button" onclick="submits()" id="submit" class="btn btn-primary">充值</button></shiro:hasPermission>
								</div>
							</div>
						</form>
					</div><!-- end div ibox content -->
				</div><!-- end div ibox -->
			</div><!-- end div col 12 -->
		</div><!-- end div row -->

		<c:import url="../copyright.jsp" />
	</div><!-- end page wrapper -->
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/lib/city.js"></script>
	<script src="${basePath }js/project/mp/batchOfCharger.js"></script>
</body>
</html>