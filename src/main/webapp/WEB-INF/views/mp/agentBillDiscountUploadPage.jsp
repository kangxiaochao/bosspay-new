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

<title>代理商话费折扣上传</title>
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
					<h3>代理商话费折扣上传</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>代理商</a></li>
						<li class="active"><strong>代理商话费折扣上传</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce" id="selectId" data-id="${1 }">
				<!-- 查询模块 -->
				<div id="queryDiv"></div>
			
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<div class="col-xs-1">
								<button type="button" class="btn btn-success btn-outline" onclick="downLoadTemp()">模板下载</button>
							</div>
							<div class="col-xs-1">
								<button type="button" class="btn btn-primary btn-outline" onclick="uploadProviderPkg()">折扣上传</button>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="col-md-3 control-label">代理商</label>
									<div class="col-md-8">
										<select class="chosen-select"  name="agentId" id="agentId"></select>
									</div>
								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
									<label class="col-md-3 control-label">运营商</label>
									<div class="col-md-8">
										<select class="chosen-select"  name="providerId" id="providerId"></select>
									</div>
								</div>
							</div>
							<div class="col-xs-1">
								<button type="button" class="btn btn-primary" onclick="search()">查询</button>
							</div>
						</div>
					</div>
					<div id="mytbc1"></div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<form method="post" action="" name="depEditForm" id="depEditForm" class="form-horizontal"
          enctype="multipart/form-data" style="display:none;">
        <input name="file" type="file" onchange="commitBoxChange()"
               accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
               id="commitBox" data-id="${1 }"/>
    </form>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/agentBillDiscountUpload.js"></script>
<iframe id="downloadFrame" style="display: none"></iframe>
</body>
</html>
