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

<title>自助加款</title>
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
					<li class="active"><strong>自助加款</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>批量充值</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>
					<div class="ibox-content" style="margin-left: 249px;">
						<div style ="float:left;">
							<table>
								<tr>
									<td width='100px' valign="top"><h2>充值金额：</h2></td>
									<td>
										<input type="text" class="form-control" name="money" id="money"/>
										<br/><span style='color:red;'>注：加款金额不得低于100元</span>
									</td>
								</tr>
								<tr>
									<td></td>
									<td valign="middle"><br/>
									<button type="button" onclick="submit(0)" class='btn btn-primary'>微信支付</button>&nbsp;&nbsp;
									<button type="button" onclick="submit(1)" class="btn btn-warning" style="background-color:#33ccff;">支付宝支付</button>&nbsp;&nbsp;
								</tr>
								<tr>
									<td></td>
									<td width='200px' valign="middle" id="ds"></td>
								</tr>
								<tr>
									<td></td>
									<td align="center" id="dt"></td>
								</tr>
							</table>							
						</div>
						<div style="margin-left:100px;float:left;font-size:20px;">
							<h1>支付教程</h1><br/>
							<strong>
							(1)加款金额，不得低于一百元<br/>
							(2)选择支付方式微信或支付宝<br/>
							(3)支付完成后点击完成支付<br/>
							(4)加款成功后会自动跳转到平台主页，反之弹出提示框提示加款失败<br/>
							<span style='color:red;'>如由客户自己操作造成的失误，资金出现问题浩百充值平台概不负责，谢谢配合！</span>
							</strong><br/><br/>
						</div>
					</div>
				</div>
			</div>
		</div>
		<c:import url="../copyright.jsp" />
	</div>
	<!-- end page wrapper -->
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/lib/city.js"></script>
	<script src="${basePath }js/project/mp/Payment.js"></script>
</body>
</html>