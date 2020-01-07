<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
	request.setAttribute("basePath", basePath);
	out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath }">

<title>图表统计</title>
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
					<h3>图表统计</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>账单统计</a></li>
						<li class="active"><strong>图表统计</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<div class="row">
						<form id="searchForm">
							<div class="col-md-3" >
		                        <div class="form-group">
		                            <label class="control-label" for="">开始时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD"
									name="startDate" id="startDate" required data-mask="9999-99-99" >
		                        </div>
		                    </div>
		                    <div class="col-md-3" >
		                        <div class="form-group">
		                            <label class="control-label" for="">结束时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD"
									name="endDate" id="endDate" required data-mask="9999-99-99">
		                        </div>
		                    </div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="control-label" for="agent">代理商</label>
									<input type="text" id="agent" name="agent" value=""
										placeholder="请输入代理商名" class="form-control">	
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="control-label" for="channel">物理通道</label>
									<input type="text" id="channel" name="channel" value=""
										placeholder="请输入物理通道名称" class="form-control">
								</div>
							</div>
							<div class="col-md-12">
								<div class="text-center">
									<button type="reset" class="btn btn-warning">重置</button>
									<button type="button" class="btn btn-primary"
										onclick="paintCharts()">生成代理商图表</button>
									<button type="button" class="btn btn-primary"
										onclick="paintChannelCharts()">生成物理通道图表</button>
								</div>
							</div>
						</form>
					</div>
				</div>

				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div id="content" class="row">
						<div id="charts" style="width:100%;height:500px;"></div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8"
		src="${basePath }js/project/mp/dataCharts.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/echarts-all.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery.peity.min.js"></script>
</body>
</html>
