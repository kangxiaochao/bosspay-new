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

<title>代理商自助加款记录</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />
</head>

<body>
	<div id="wrapper">
		<c:import url="../left.jsp" />
		<div id="page-wrapper" class="gray-bg dashbard-1">
			<div class="row border-bottom" id="topNavDiv">
				<c:import url="../topnav.jsp" />
			</div>

			<div class="row wrapper border-bottom white-bg page-heading" id="breadcrumbsDiv">
				<div class="col-lg-10">
					<h3>代理商自助加款记录</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统管理</a></li>
						<li class="active"><strong>代理商自助加款记录</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form>
						<div class="row">
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label" for="">开始时间</label> <input
										type="text" class="laydate-icon form-control"
										placeholder="格式:YYYY-MM-DD hh:mm:ss" name="createDate"
										id="createDate" required data-mask="9999-99-99 99:99:99">
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label" for="">结束时间</label> <input
										type="text" class="laydate-icon form-control"
										placeholder="格式:YYYY-MM-DD hh:mm:ss" name="endDate"
										id="endDate" required data-mask="9999-99-99 99:99:99">
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">选择加款状态</label> 
									<select class="chosen-select" name="state" id="state">
										<option value="" selected="selected">所有</option>
										<option value="0">支付成功</option>
										<option value="1">待支付</option>
										<option value="2">加款成功</option>
										<option value="3">加款失败</option>
										<option value="-1">异常</option>
		 						</select>
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">选择付款方式</label> 
									<select class="chosen-select" name="pay_type" id="pay_type">
										<option value="" selected="selected">所有</option>
										<option value="1">微信支付</option>
										<option value="2">支付宝支付</option>
		 						</select>
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label">代理商名称</label> 
									<input type="text" class="form-control" name="agentName" id="agentName" >
								</div>
							</div>
							<div class="col-md-2">
								<div class="form-group">
									<label class="control-label" for="">平台订单号</label> 
									<input type="text" class="form-control" name="transaction_id" id="transaction_id" >
								</div>
							</div>
							<div class="col-md-12">
								<div class="text-center">
									<button type="reset" class="btn btn-warning">重置</button>
									<button type="button" class="btn btn-primary"
										onclick="search()">查询</button>
								</div>
							</div>
						</div>
					</form>
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/Payment.js"></script>
</body>
</html>
