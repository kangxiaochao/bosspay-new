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

<title>功能列表</title>
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
					<h3>功能列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>功能列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form>
						<div class="row">
							<div class="col-md-6">
		                        <div class="form-group">
		                            <label class="control-label" for="product_name">功能地址</label>
		                            <input type="text" id="sfValue" name="sfValue" value="" placeholder="请输入功能地址" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<button type="button" class="btn btn-primary" onclick="search()">查询</button>
		                    	</div>
		                    </div>
	                    </div>
                    </form>
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<shiro:hasPermission name="sysFunctionAddPage:get"> <button type="button" class="btn btn-primary" onclick="add()">添加</button></shiro:hasPermission>
							<shiro:hasPermission name="sysFunctionEditPage:get"> <button type="button" class="btn btn-success btn-outline" onclick="editEx()">修改</button></shiro:hasPermission>
							<shiro:hasPermission name="sysFunction:delete"> <button type="button" class="btn btn-danger btn-outline" onclick="delEx()">删除</button></shiro:hasPermission>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
    <script src="${basePath }js/project/sysFunctionList.js"></script>
</body>
</html>