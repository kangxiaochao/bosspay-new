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

<title>代理商详情</title>
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
					<li><a>系统</a></li>
					<li class="active"><strong>代理商编辑</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>代理商编辑</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" action="${basePath }agent/${agent.id}" class="form-horizontal">
							<div class="form-group">
								<label class="col-md-2 control-label">代理商名称</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.name}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">简 称</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.nickname}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">用 户 名</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.suName}" />
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">上级代理商</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.parentName}" />
								</div>
							</div>
							<c:if test='${agentRole!="代理商" }'>
								<div class="form-group">
									<label class="col-md-2 control-label">话费通道组</label>
									<div class="col-md-8">
										<input type="text" class="form-control" readonly value="${agent.billGroupName}" />
									</div>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">话费通道组</label>
									<div class="col-md-8">
										<input type="text" class="form-control" readonly value="${agent.dataGroupName}" />
									</div>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">话费折扣模板</label>
									<div class="col-md-8">
										<input type="text" class="form-control" readonly value="${agent.billDiscountModelName}" />
									</div>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">话费折扣模板</label>
									<div class="col-md-8">
										<input type="text" class="form-control" readonly value="${agent.dataDiscountModelName}" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">渠道人员</label>
									<div class="col-md-8">
										<input type="text" class="form-control" readonly value="${agent.channelPerson}" />
									</div>
								</div>
							</c:if>
							<div class="form-group">
								<label class="col-md-2 control-label">授信额</label>
								<div class="col-md-8">
									<input type="int" class="form-control" readonly value="${agent.credit}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">生效日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control" readonly  value="${agent.start_date}">
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">截止日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control" readonly value="${agent.end_date}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">是否生效</label>
								<input type="hidden" name="oldStatus" id="oldStatus" value="${agent.status}"  />
								<div class="col-md-8">
									<input type="checkbox" checked readonly
										data-on-color="success" data-off-color="danger"
										data-on-text="启用" data-off-text="禁用">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">手机号</label>
								<div class="col-md-8">
									<input type="tel" class="form-control" readonly value="${agent.phone}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">邮 箱</label>
								<div class="col-md-8">
									<input type="email" class="form-control" readonly value="${agent.email}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">公司名</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.corp_name}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">地 址</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.address}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 1</label>
								<div class="col-md-8">
									<input type="time" class="form-control" readonly value="${agent.hotline1}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.hotline2}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ1</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.serviceqq1}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" readonly value="${agent.serviceqq2}">
								</div>
							</div>

							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }agentListPage'">返回</button>
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
	<script src="${basePath }js/project/mp/agentDetail.js"></script>

</body>
</html>