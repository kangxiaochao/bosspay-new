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

<title>增加代理商话费折扣</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />

<link href="${basePath }css/project/laydate.css" rel="stylesheet">
</head>

<body>
	<c:import url="../left.jsp" />

	<div id="page-wrapper" class="gray-bg dashbard-1">
		<div class="row border-bottom">
			<c:import url="../topnav.jsp" />
		</div>

		<div class="row wrapper border-bottom white-bg page-heading">
			<div class="col-lg-10">
				<br />
				<ol class="breadcrumb">
					<li><a href="${basePath }mainPage">主页</a></li>
					<li><a>系统</a></li>
					<li class="active"><strong>增加代理商话费折扣</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
           			<div class="form-group">
						<div class="col-md-12">
							<form id="uploadForm1" name="uploadForm1" method="post" action="${basePath }DataDiscountModelEx1" enctype="multipart/form-data">
								<button type="button" class="btn btn-success btn-outline" onclick="downLoadTemp()">模板下载</button>
		            			<div class="fileinput fileinput-new" data-provides="fileinput">
								    <span class="btn btn-success btn-file"><span class="fileinput-new">请选择文件</span>
								    <span class="fileinput-exists">重新选择</span><input type="file" name="DataDiscountFile" id="DataDiscountFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/></span>
								    <span class="fileinput-filename"></span>
								    <a href="#" class="close fileinput-exists" data-dismiss="fileinput" style="float: none">×</a>
								</div>
								<button class="btn btn-primary" type="button" onclick="uploadFile()">上传</button>
		           			</form>
						</div>
					</div>
					<div class="ibox-content">
						<form id="fm" method="post" action="${basePath }agentDataDiscount"
							class="form-horizontal">
							<input id="agentId" name="agentId" value="${agentId}" type="hidden">
							<div class="form-group">
								<label class="col-sm-2 control-label">代理商话费折扣名称</label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="name" id="name"
										maxlength="25" >
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">运营商</label>
								<div class="col-sm-9">
									<select class="chosen-select" onchange="checkProvince()" tabindex="2" name="providerId" id="providerId"></select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">话费类型</label>
								<div class="col-sm-9">
									<select class="chosen-select" onchange="checkProvince()" tabindex="2" name="dataType"
										id="dataType" >
										<option value="2" selected="selected">全国</option>
										<option value="1">省内</option>
									</select>
								</div>
							</div>
							<div class="form-group" id="provinceDiscount">
								<label class="col-sm-2 control-label">折扣设置</label>
									<div class="col-sm-10" id="provinceDataDiscount">
		            					
		           					</div>
        					</div>
							<div class="form-group">
								<div class="col-sm-12 col-sm-offset-5">
									<button type="button" class="btn btn-primary btn-outline"
										onclick="javascript:location.href='${basePath }agentDataDiscountListPage/${agentId}'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="agentDataDiscount:post"><button type="submit" class="btn btn-primary" onclick="return checkDis()">增加</button></shiro:hasPermission>
								</div>
							</div>
						</form>
					</div>
					<!-- end div ibox content -->
				</div>
				<!-- end div ibox -->
			</div>
			<!-- end div col 12 -->
		</div>
		<!-- end div row -->

		<c:import url="../copyright.jsp" />
	</div>
	<!-- end page wrapper -->
	<iframe id="downloadFrame" style="display: none"></iframe>
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/lib/city.js"></script>
	<script src="${basePath }js/project/mp/agentDataDiscountAdd.js"></script>
</body>
</html>