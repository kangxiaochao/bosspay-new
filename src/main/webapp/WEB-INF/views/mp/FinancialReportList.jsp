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

<title>index</title>
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

			<div class="row wrapper border-bottom white-bg page-heading"
				id="breadcrumbsDiv">
				<div class="col-lg-10">
					<h3>财务报表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>财务报表</strong></li>
					</ol>
				</div>
			</div>

			<div class="tabs-container">
				<ul class="nav nav-tabs">
					<li class="active"><a data-toggle="tab" href="#tab-1">上游数据报表</a></li>
					<li onclick="agentList()"><a data-toggle="tab" href="#tab-2">下游数据报表</a></li>
					<li onclick="BusinessList()"><a data-toggle="tab" href="#tab-3">经营分析数据报表</a></li>
				</ul>
				<div class="tab-content ">
					<div id="tab-1" class="tab-pane active">
						<div class="panel-body">

							<div
								class="wrapper wrapper-content animated fadeInRight ecommerce">
								<!-- 查询模块 -->
								<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
									<form id="searchForm">
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="nickName">通道名称</label> <input
														type="text" id="channelName" name="channelName" value=""
														placeholder="请输入通道名称" class="form-control">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交开始时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="startDate"
														id="startDate" required data-mask="9999-99-99 99:99:99">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交结束时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="endDate"
														id="endDate" required data-mask="9999-99-99 99:99:99">
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
											<button type="button" class="btn btn-primary"
												onclick="exportFinancialChannelReport()">导出报表</button>
											<div id="mytbc1"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<div id="tab-2" class="tab-pane">
						<div class="panel-body">

							<div
								class="wrapper wrapper-content animated fadeInRight ecommerce">
								<!-- 查询模块 -->
								<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
									<form id="a_searchForm">
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="nickName">代理商名称</label> <input
														type="text" id="agentName" name="agentName" value=""
														placeholder="请输入代理商名称" class="form-control">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交开始时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="startDate"
														id="a_startDate" required data-mask="9999-99-99 99:99:99">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交结束时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="endDate"
														id="a_endDate" required data-mask="9999-99-99 99:99:99">
												</div>
											</div>
											<div class="col-md-12">
												<div class="text-center">
													<button type="reset" class="btn btn-warning">重置</button>
													<button type="button" class="btn btn-primary"
														onclick="a_search()">查询</button>
												</div>
											</div>
										</div>
									</form>
								</div>

								<!-- 列表模块 -->
								<div class="ibox-content m-b-sm border-bottom">
									<div class="row">
										<div class="col-md-12">
											<button type="button" class="btn btn-primary"
												onclick="exportFinancialAgentReport()">导出报表</button>
											<div id="a_mytbc1"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					
					<div id="tab-3" class="tab-pane">
						<div class="panel-body">

							<div
								class="wrapper wrapper-content animated fadeInRight ecommerce">
								<!-- 查询模块 -->
								<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
									<form id="b_searchForm">
										<div class="row">
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="nickName">通道名称</label> <input
														type="text" id="b_providerName" name="providerName" value=""
														placeholder="请输入运营商名称" class="form-control">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="nickName">通道名称</label> <input
														type="text" id="b_channelName" name="channelName" value=""
														placeholder="请输入通道名称" class="form-control">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="nickName">代理商名称</label> <input
														type="text" id="b_agentName" name="agentName" value=""
														placeholder="请输入代理商名称" class="form-control">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交开始时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="startDate"
														id="b_startDate" required data-mask="9999-99-99 99:99:99">
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<label class="control-label" for="">提交结束时间</label> <input
														type="text" class="laydate-icon form-control"
														placeholder="格式:YYYY-MM-DD hh:mm:ss" name="endDate"
														id="b_endDate" required data-mask="9999-99-99 99:99:99">
												</div>
											</div>
											<div class="col-md-12">
												<div class="text-center">
													<button type="reset" class="btn btn-warning">重置</button>
													<button type="button" class="btn btn-primary"
														onclick="b_search()">查询</button>
												</div>
											</div>
										</div>
									</form>
								</div>

								<!-- 列表模块 -->
								<div class="ibox-content m-b-sm border-bottom">
									<div class="row">
										<div class="col-md-12">
											<button type="button" class="btn btn-primary"
												onclick="exportBusinessReport()">导出报表</button>
											<div id="b_mytbc1"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					
				</div>
			</div>

			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/project/mp/FinancialReportList.js"></script>
</body>
</html>