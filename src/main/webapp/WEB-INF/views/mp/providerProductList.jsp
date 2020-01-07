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

<title>上家产品列表</title>
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
			<input type="hidden" value="${backMsg}" id="backMsg">
			<div class="row wrapper border-bottom white-bg page-heading" id="breadcrumbsDiv">
				<div class="col-lg-10">
					<h3>上家产品列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>资源配置</a></li>
						<li class="active"><strong>上家产品列表</strong></li>
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
		                            <label class="control-label">请选择流量包：</label>
									<input type="text" id="pkgName" name="pkgName" value="" placeholder="请输入流量包" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label">运营商物理通道：</label>
									<input type="text" id="physicalChannelName" name="physicalChannelName" value="" placeholder="请输入运营商物理通道" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label">省份：</label>
									<input type="text" id="provinceCodes" name="provinceCodes" value="" placeholder="请输入省份" class="form-control">
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
							<shiro:hasPermission name="providerProductAddPage:get"><button type="button" class="btn btn-primary" onclick="add()">添加上家产品</button></shiro:hasPermission>
							<shiro:hasPermission name="providerProductEditPage:get"><button type="button" class="btn btn-success btn-outline" onclick="editEx()">编辑上家产品</button></shiro:hasPermission>
							<shiro:hasPermission name="providerProductDel:delete"><button type="button" class="btn btn-danger btn-outline" onclick="delEx()">删除</button></shiro:hasPermission>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/ProviderProduct.js"></script>
</body>
</html>
