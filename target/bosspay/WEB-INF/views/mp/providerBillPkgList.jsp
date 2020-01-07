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

<title>运营商话费包列表</title>
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
					<h3>运营商话费包列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>运营商</a></li>
						<li class="active"><strong>运营商话费包列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce" id="selectId" data-id="${provider.id }">
				<div class="ibox-title">
					<h5>
						<small>
				    		运营商话费包列表
				    		-
				    		<span style="color: red">${provider.name}</span>
				    	</small>
					</h5>
				</div>
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<div class="row">
						<form class="form-horizontal">
						
		                    <div class="col-md-6">
		                        <div class="form-group">
		                            <label class="col-md-2 control-label" for="product_name">省份</label>
		                            <div class="col-md-9">
		                            	<input type="text" id="provinceCode" name="provinceCode" value="" placeholder="请输入省份名称" class="form-control">
		                            </div>
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<button type="button" class="btn btn-primary" onclick="search()">查询</button>
		                    	</div>
		                    </div>
	                    </form>
                    </div>
				</div>
				<input type="hidden" name="providerId" id="providerId" value="${provider.id}">
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<button type="button" class="btn btn-success btn-outline" onclick="editEx()">设置</button>
							<button type="button" class="btn btn-primary btn-outline" 
	          						onclick="javascript:location.href='${basePath }providerListPage'">返回</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/providerBillPkgList.js"></script>
<iframe id="downloadFrame" style="display: none"></iframe>
</body>
</html>