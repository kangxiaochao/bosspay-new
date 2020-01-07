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

<title>订单推送</title>
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
					<h3>订单</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>订单</a></li>
						<li class="active"><strong>订单推送</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">						
						<div class="panel-body">
							<form id="fm" method="post" action="${basePath }agentCallback" class="form-horizontal">
								<div class="form-group">
									<label class="col-sm-2 control-label"><B>订单号</B></label>
									<div class="col-sm-7">
									<textarea class="form-control" id="orderIds" name="orderIds" placeholder="多条订单请用逗号分隔(英文模式下逗号)"
							   			 maxlength="1024" vMin="11" cols=40 rows=6 required></textarea>
									</div>
								</div>
								<div id="dialog">
								<tbody id="Data"></tbody>
								</div>	
						<div class="form-group">
							<div class="col-sm-2 col-sm-offset-3">
								<button type="button" class="btn btn-primary" onclick="submit1()">
									订单推送<i class="fa fa-arrow-circle-right"></i>
								</button>
							</div>
						</div>
						</form>
					</div>
				</div>
			</div>
		</div>
		<c:import url="../copyright.jsp" />
		<c:import url="../foot.jsp" />
		<script src="${basePath }js/lib/jquery.form.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery-labelauty.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/shCore.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/verify.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/OrderPush.js"></script>
</body>
</html>
