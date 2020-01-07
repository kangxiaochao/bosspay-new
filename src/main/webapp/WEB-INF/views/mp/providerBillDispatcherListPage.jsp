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

<title>运营商物理通道话费拆分通道配置列表</title>
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
					<h3>运营商物理通道话费拆分通道配置列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>运营商物理通道</a></li>
						<li class="active"><strong>运营商物理通道话费拆分通道配置列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv" data-physicalId="${physicalId}">
					<form>
						<div class="row">
							<div class="col-md-6">
		                        <div class="form-group">
		                            <label class="control-label" for="name">运营商</label>
		                            <input type="text" id="providerName" name="providerName" value="" placeholder="请输入运营商" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-6">
		                        <div class="form-group">
		                            <label class="control-label" for="name">省份</label>
		                            <input type="text" id="provinceCode" name="provinceCode" value="" placeholder="请输入省份" class="form-control">
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
				<input id="groupId" name="physicalId" value="${physicalId}" type="hidden"/>
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
								<button type="button" class="btn btn-success btn-outline" onclick="editEx()">设置</button>
								<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }providerPhysicalChannelListPage'">返回</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/providerBillDispatcherList.js"></script>
</body>
</html>
