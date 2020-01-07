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

<title>异常订单列表</title>
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
					<h3>异常订单列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>异常订单列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form>
						<div class="row">
<!-- 							<div class="col-md-2"> -->
<!-- 		                        <div class="form-group"> -->
<!-- 		                            <label class="control-label" for="">平台订单号</label> -->
<!-- 		                            <input type="text" id="orderId" name="orderId" value="" placeholder="请输入平台订单号" class="form-control" -->
<!-- 		                            maxlength="50"> -->
<!-- 		                        </div> -->
<!-- 		                    </div> -->
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">代理商订单号</label>
		                            <input type="text" id="agentOrderId" name="agentOrderId" value="" placeholder="请输入代理商订单号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
<!-- 		                    <div class="col-md-2" > -->
<!-- 		                        <div class="form-group"> -->
<!-- 		                            <label class="control-label" for="">上家订单号</label> -->
<!-- 		                            <input type="text" id="providerOrderId" name="providerOrderId" value="" placeholder="请输入上家订单号" class="form-control"  -->
<!-- 		                            maxlength="50"> -->
<!-- 		                        </div> -->
<!-- 		                    </div> -->
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
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">运营商名称</label>
		                            <input type="text" id="providerName" name="providerName" value="" placeholder="请输入运营商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
<!-- 		                    <div class="col-md-2" > -->
<!-- 		                    	<div class="form-group"> -->
<!-- 								<label class="control-label">订单状态</label> -->
<!-- 									<select class="form-control m-b" name="status" id="status"> -->
<!-- 										<option value="" selected="selected">所有</option>										 -->
<!-- 										<option value="1">提交成功</option> -->
<!-- 										<option value="2">提交失败</option>	 -->
<!-- 										<option value="3">充值失败</option> -->
<!-- 										<option value="4">充值失败</option>									 -->
<!-- 									</select> -->
<!-- 								</div> -->
<!-- 							</div> -->
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
							<div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">手机号</label>
		                            <input type="text" id="phone" name="phone" value="" placeholder="请输入手机号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">请求开始时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="applyDate" id="applyDate" required data-mask="9999-99-99 99:99:99" ></td>
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">请求结束时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99"></td>
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
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
							<shiro:hasPermission name="reCharge:get"><button type="button" class="btn btn-primary" onclick="selectRecharge()">复充</button></shiro:hasPermission>
							<shiro:hasPermission name="reCharge:get"><button type="button" class="btn btn-primary" onclick="selectRechargeOld()">原通道复充</button></shiro:hasPermission>
							<shiro:hasPermission name="refund:get"><button type="button" class="btn btn-success btn-outline" onclick="selectRefund()">退款</button></shiro:hasPermission>
							<shiro:hasPermission name="changeSucc:get"><button type="button" class="btn btn-success btn-outline" onclick="changeSucc()">置为成功</button></shiro:hasPermission>
							<button type="button" class="btn btn-primary btn-outline" onclick="orderParamQuery()">参数查询</button>
							<button type="button" class="btn btn-success btn-outline"onclick="showColumnDialog()" >显示/隐藏 列</button>
							<button type="button" class="btn btn-success btn-outline"onclick="exportException()" >导出异常订单</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/exceptionOrderList.js"></script>
</body>
</html>
