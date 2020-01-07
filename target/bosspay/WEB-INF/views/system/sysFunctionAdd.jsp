<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
	request.setAttribute("basePath", basePath);
	out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath }">

<title>添加功能</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />
</head>

<body>
	<div id="wrapper">
		<c:import url="../left.jsp" />
		<div id="page-wrapper" class="gray-bg dashbard-1">
			<div class="row border-bottom">
				<c:import url="../topnav.jsp" />
			</div>

			<div class="row wrapper border-bottom white-bg page-heading">
				<div class="col-lg-10">
					<h3>功能列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>添加功能</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom">
					<form id="function" name="function" method="post" action="${basePath }sysFunctionAdd" class="form-horizontal" > 
						<input id="hasSrIds" name="srId" value="" type="hidden" />
						<input id="hasSpIds" name="spId" value="" type="hidden" />
						<!-- 添加功能 -->
						<table style="width:50%">
							<tr>
								<td>
									<h5>功能地址</h5>
									<div class="col-md-6">
										<div class="form-group">
											<input type="text" id="sfValue" name="sfValue" value=""
												placeholder="请输入功能地址" class="form-control" required>
										</div>
									</div>
								</td>
							</tr>
							<tr id="selectPermission">
								<td>
									<h5>选择权限</h5> 
									<select required id="Permission" name="Permission" data-placeholder="请选择..." class="chosen-select" tabindex="2">
									</select>
								</td>
							</tr>
							<tr id="type">
								<td>
									<h5>授权类型</h5> 
									<input type="radio" name="sfType" value="cusperm" checked="checked"/>授权访问
									<input type="radio" name="sfType" value="naon"/>全部访问
								</td>
							</tr>
						</table>
						<br>
						<div class="row">
							<div class="col-md-12">
								<div class="text-left">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }sysFunctionListPage'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="sysFunctionAdd:post"><button onclick="getArr()" class="btn btn-primary">确定</button></shiro:hasPermission>
								</div>
							</div>
						</div>
					</form>
				</div>

				<!-- 列表模块 -->
				<c:import url="../copyright.jsp" />
			</div>
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/project/sysFunctionAdd.js"></script>
</body>
</html>

