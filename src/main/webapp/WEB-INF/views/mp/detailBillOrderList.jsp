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

<title>话费通道账单统计</title>
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
					<h3>话费通道账单统计</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>话费通道账单统计</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form id="searchForm">
						<input type = "hidden" name = "physicalChannelName" value = "${param.physicalChannelName }"/>
						<input type = "hidden" name = "channelName" value = "${param.channelName }"/>
						<input type = "hidden" name = "agentName" value = "${param.agentName }"/>
						<input type = "hidden" name = "startDate" value = "${param.startDate }"/>
						<input type = "hidden" name = "endDate" value = "${param.endDate }"/>
						<input type = "hidden" name = "province" value = "${param.province }"/>
						<input type = "hidden" name = "agentId" value = "${param.agentId }"/>
						<div class="row">
							<div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">手机号</label>
		                            <input type="text" id="phone" name="phone" value="" placeholder="请输入手机号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
							<div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="">平台订单号</label>
		                            <input type="text" id="orderId" name="orderId" value="" placeholder="请输入平台订单号" class="form-control"
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                    	<div class="form-group">
								<label class="control-label">订单状态</label>
									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="status" id="status">
										<option value="" selected="selected">所有</option>										
										<option value="1">提交成功</option>
										<option value="2">提交失败</option>	
										<option value="3">充值成功</option>
										<option value="4">充值失败</option>									
									</select>
								</div>
							</div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="button" class="btn btn-primary btn-outline" onclick="javascript:history.go(-1)">返回</button>
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
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/detailBillOrderList.js"></script>
</body>
</html>
