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

<title>订单列表</title>
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
					<h3>订单列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>订单列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form  id="orderForm">
						<div class="row">
							<div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">请求开始时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="applyDate" id="applyDate" required data-mask="9999-99-99 99:99:99" >
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">请求结束时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">手机号</label>
		                            <input type="text" id="phone" name="phone" value="" placeholder="请输入手机号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <c:if test='${agentRole!="代理商" }'>
							<div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="">平台订单号</label>
		                            <input type="text" id="orderId" name="orderId" value="" placeholder="请输入平台订单号" class="form-control"
		                            maxlength="50">
		                        </div>
		                    </div>
		                    </c:if>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">代理商订单号</label>
		                            <input type="text" id="agentOrderId" name="agentOrderId" value="" placeholder="请输入代理商订单号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <c:if test='${agentRole!="代理商" }'>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">上家订单号</label>
		                            <input type="text" id="providerOrderId" name="providerOrderId" value="" placeholder="请输入上家订单号" class="form-control" 
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
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">分销运营商名称</label>
		                            <input type="text" id="dispatcherProviderName" name="dispatcherProviderName" value="" placeholder="请输入分销运营商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    </c:if>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">运营商名称</label>
		                            <input type="text" id="providerName" name="providerName" value="" placeholder="请输入运营商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                    	<div class="form-group">
								<label class="control-label">订单状态</label>
									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="status" id="status">
										<option value="" selected="selected">所有</option>
										<option value="0">处理中</option>										
										<option value="1">提交成功</option>
										<option value="2">提交失败</option>	
										<option value="3">充值成功</option>
										<option value="4">充值失败</option>									
									</select>
								</div>
							</div>
							<!-- <div class="col-md-2" >
		                    	<div class="form-group">
								<label class="control-label">业务类型</label>
									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="bizType" id="bizType">
										<option value="" selected="selected">所有</option>										
										<option value="1">话费</option>
										<option value="2">话费</option>									
									</select>
								</div>
							</div> -->
							<c:if test='${agentRole!="代理商" }'>
							<div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">渠道名称</label>
		                            <input type="text" id="channelPerson" name="channelPerson" value="" placeholder="请输入代理商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    </c:if>
		                    <div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="">归属地</label>
		                            <input type="text" id="provinceCode" name="provinceCode" value="" placeholder="请输入归属地" class="form-control"
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<shiro:hasPermission name="orderReport:get"><button type="button" class="btn btn-success" onclick="orderReport()">导出</button></shiro:hasPermission>
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<shiro:hasPermission name="orderListAll:get"><button type="button" class="btn btn-primary" onclick="search()">查询</button></shiro:hasPermission>
		                    	</div>
		                    </div>
	                    </div>
                    </form>
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
<%-- 							<button type="button" class="btn btn-warning" onclick="openDialog(1)">修改状态</button></shiro:hasPermission> --%>
							<shiro:hasPermission name="changeStatus:get"><button type="button" class="btn btn-warning" onclick="openChangeStatusDialog()">修改状态</button></shiro:hasPermission>
							<shiro:hasPermission name="	refundForOrder:get"><button type="button" class="btn btn-success" onclick="openDialog(2)">退款</button></shiro:hasPermission>
							<shiro:hasPermission name="reChargeForOrder:get"><button type="button" class="btn btn-primary" onclick="openDialog(3)">复充</button></shiro:hasPermission>
							<shiro:hasPermission name="agentCallback:post"><button type="button" class="btn btn-info" onclick="openDialog(4)">订单推送</button></shiro:hasPermission>
							<shiro:hasPermission name="orderParamPage:get"><button type="button" class="btn btn-primary btn-outline" onclick="orderParamQuery()">参数查询</button></shiro:hasPermission>
							<button type="button" class="btn btn-success btn-outline"onclick="showColumnDialog()">显示/隐藏 列</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/orderList.js"></script>
</body>
</html>
