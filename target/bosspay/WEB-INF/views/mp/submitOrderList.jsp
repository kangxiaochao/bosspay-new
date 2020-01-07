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

<title>订单提交记录列表</title>
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
					<h3>订单提交记录列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>订单提交记录列表</strong></li>
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
		                            <label class="control-label" for="">代理商名称</label>
		                            <input type="text" id="agentName" name="agentName" value="" placeholder="请输入代理商名" class="form-control"
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">代理商订单号</label>
		                            <input type="text" id="orderId" name="orderId" value="" placeholder="请输入代理商订单号" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">手机号</label>
		                            <input type="text" id="phone" name="phone" value="" placeholder="请输入分销运营商名称" class="form-control" 
		                            maxlength="50">
		                        </div>
		                    </div>
							<div class="col-md-2" >
		                    	<div class="form-group">
								<label class="control-label">返回信息</label>
									<select id="resultCode" name="resultCode" class="form-control" >
										<option value="">全部</option>
										<option value="-1:状态返回值异常">状态返回值异常</option>
										<option value="0:提交成功">提交成功</option>
										<option value="1:参数不正确">参数不正确</option>
										<option value="2:签名错误">签名错误</option>
										<option value="4:不支持该手机号段">不支持该手机号段</option>
										<option value="5:不允许充值该运营商号段，请联系业务人员获取权限">不允许充值该运营商号段</option>
										<option value="6:无法充值该产品">无法充值该产品</option>
										<option value="7:余额不足">余额不足</option>
										<option value="8:订单提交出现异常">订单提交出现异常</option>
										<option value="9:折扣已变更，请联系商务">折扣已变更，请联系商务</option>
										<option value="10:订单号重复，请勿重复提交">订单号重复，请勿重复提交</option>
										<option value="11:获取不到代理商的扣款折扣">获取不到代理商的扣款折扣</option>
										<option value="12:IP地址不合法">IP地址不合法</option>
									</select>
								</div>
							</div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">提交开始时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="startDate" id="startDate" required data-mask="9999-99-99 99:99:99" >
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">提交失败时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="button" class="btn btn-success" onclick="orderReport()">导出</button>
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
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/submitOrderList.js"></script>
</body>
</html>
