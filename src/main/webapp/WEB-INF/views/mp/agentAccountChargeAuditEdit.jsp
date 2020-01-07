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

<title>代理商加款修改</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link href="${basePath }css/project/laydate.css" rel="stylesheet">
<c:import url="../head.jsp" />
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
					<li><a>代理商</a></li>
					<li class="active"><strong>代理商加款修改</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight" id="agentRole" data-agentRole='${agentRole }'>
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>代理商加款修改</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" class="form-horizontal">
						<input type="hidden" name="id" id="id" value="${agentAccountChargeAudit.id}"  />
							<div class="form-group">
								<label class="col-md-2 control-label">代理商名称</label>
								<div class="col-md-8">
									<input type="text" class="form-control"  maxlength="25" value="${agentAccountChargeAudit.agentName}" readonly="readonly">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">打款人姓名</label>
								<div class="col-md-8">
								<input type="text" class="form-control" name="paymoneyName" id="paymoneyName" maxlength="25" required value="${agentAccountChargeAudit.playmoney_name}">
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">打款账户</label>
								<div class="col-md-8">
								<input type="text" class="form-control" name="moneyAccount" id="moneyAccount" maxlength="25" required value="${agentAccountChargeAudit.money_account}">
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">备注</label>
								<div class="col-md-8">
								<input type="text" class="form-control" name="remark" id="remark" maxlength="25" required value="${agentAccountChargeAudit.remark}">
								</div>
							</div>
							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }agentAccountChargeAuditPage'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<button type="button" class="btn btn-primary" onclick="submitEdit();">提交</button>
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
	<script src="${basePath }js/project/mp/agentAccountChargeAuditEdit.js"></script>

</body>
</html>