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

<title>代理商冲扣值记录列表</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />
<link href="${basePath }css/project/laydate.css" rel="stylesheet">
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
					<h3>代理商列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>代理商冲扣值记录列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form id="searchForm">
						<div class="row">
							<div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="">订单号</label>
		                            <input type="text" id="agentOrderId" name="agentOrderId" value="" placeholder="请输入订单号" class="form-control"
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">代理商名称</label>
		                            <input type="text" id="agentName" name="agentName" value="" placeholder="请输入代理商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <!-- <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">类型</label>
		                            <select class="chosen-select" id="type" name="type" >
            							<option value="">请选择</option>
			                            <option value="1">话费</option>
			                            <option value="2">话费</option>
		    						</select>
		                        </div>
		                    </div> -->
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">状态</label>
		                            <select class="chosen-select" id="status" name="status" >
            							<option value="">请选择</option>
			                            <option value="1">扣款</option>
			                            <option value="2">退款</option>
			                            <option value="3">加款</option>
			                            <option value="4">调账</option>
		    						</select>
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">开始时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="applyDate" id="applyDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">完成时间</label>
									<input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="button" class="btn btn-success" onclick="orderReport()">导出</button>
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<shiro:hasPermission name="agentAccountCharge:get"><button type="button" class="btn btn-primary" onclick="search()">查询</button></shiro:hasPermission>
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
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/agentAccountChargeList.js"></script>
</body>
</html>

