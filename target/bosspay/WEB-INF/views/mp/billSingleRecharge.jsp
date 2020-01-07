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

<title>话费单号充值</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link rel="stylesheet"
	href="${basePath }ui/inspinia/css/jquery-labelauty.css">
<style>
ul {
	list-style-type: none;
}

li {
	display: inline-block;
}

li {
	margin: 10px 0;
}

input.labelauty+label {
	font: 12px "Microsoft Yahei";
}
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
					<h3>话费充值</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>话费单号充值</strong></li>
					</ol>
				</div>
			</div>
			
			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom">
					<form method="post" class="form-horizontal">
						<div class="form-group">
							<input type="hidden" id="carrierstype" name="carrierstype" value="" /> 
							<input type="hidden" id="provinceCode" name="provinceCode" value="" />	
							<input type="hidden" id="providerId" name="providerId" value="" />
							<input type="hidden" id="terminalName">
							<label class="col-md-2 control-label">手机号</label>
							<div class="col-md-8">
								<input type="text" class="form-control" name="mobilenumber" id="mobilenumber" 
								maxlength="13" vMin="11" oninput="changemobileExt1(this);" required />
							</div>
							
						</div>
						<div class="form-group" id="phoneMsg" style="display:none;">
							<label class="col-md-2 control-label"></label>
							<div class="col-md-8">
								<div style="float:left"><span id="phoneMessage"></span></div>
								<div style="float:left;margin-left:25%">机主姓名：<span id="phoneName"></span></div> 
								<div style="float:right">余额：<span id="phoneAmount"></span></div>
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">话费包</label>
							<div class="col-md-8">
								<ul id="billpkgids" class="dowebok">
								</ul>
							</div>
						</div>
						<div id="feeDiv" style="display:none" class="form-group">
							<label class="col-md-2 control-label">充值金额</label>
							<div class="col-md-8">
								<input type="text" id="fee" name="fee" value="" class="form-control" maxlength="4"
									vMin="1" vType="doubleZ" oninput="changeFee()">
							</div>
							<span class="help-block m-b-none">
								 <i class="fa fa-info-circle"></i>
							</span>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">原价</label>
							<div class="col-md-8">
								<input type="text" id="originalprice" name="originalprice" value=""
									readonly="readonly" class="form-control" maxlength="8"
									vMin="1" vType="doubleZ">
							</div>
							<span class="help-block m-b-none">
								 <i class="fa fa-info-circle"></i>
							</span>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">折后价</label>
							<div class="col-md-8">
								<input type="text" id="disprice" name="disprice" value=""
									readonly="readonly" class="form-control" maxlength="8"
									vMin="1" vType="doubleZ">
							</div>
							<span class="help-block m-b-none">
								 <i class="fa fa-info-circle"></i>
							</span>
						</div>
						<div class="form-group">
							<div class="col-md-12 col-md-offset-5">
								<shiro:hasPermission name="order/webSubmit:get"><button type="button" class="btn btn-primary"
									onclick="submitOrder()" id="sub">
									立即充值<i class="fa fa-arrow-circle-right"></i>
								</button></shiro:hasPermission>
							</div>
						</div>
					</form>
				</div><!-- end div row -->
			</div>
			<c:import url="../copyright.jsp" />
		</div>
		<c:import url="../foot.jsp" />
		<script type="text/javascript" charset="UTF-8"
			src="${basePath }js/lib/jquery-labelauty.js"></script>
		<script type="text/javascript" charset="UTF-8"
			src="${basePath }js/lib/shCore.js"></script>
		<script type="text/javascript" charset="UTF-8"
			src="${basePath }js/lib/verify.js"></script>
		<script type="text/javascript" charset="UTF-8"
			src="${basePath }js/project/mp/billSingleRecharge.js"></script>
</body>
</html>
