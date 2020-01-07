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

<title>用户编辑</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />
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
					<li class="active"><strong>用户编辑</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>用户编辑</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" action="${basePath }sysUser/${sysUser.suId}"
							class="form-horizontal">
							<div class="form-group">
								<label class="col-sm-2 control-label">用户名 <input
									type="hidden" name="suId" id="suId" value="${sysUser.suId}"/>
								</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="suName"
										id="suName" value="${sysUser.suName}" required>
								</div>
							</div>
							<div class="hr-line-dashed"></div>

							<div class="form-group">
								<label class="col-sm-2 control-label">手机号</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="suMobile"
										id="suMobile" value="${sysUser.suMobile}">
								</div>
							</div>

							<div class="form-group">
								<div class="col-sm-4 col-sm-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }sysUserListPage'">返回</button>
									<button type="reset" class="btn btn-warning">重写</button>
									<shiro:hasPermission name="sysUser:put">
										<button type="button" class="btn btn-primary"
											onclick="submitEdit();">提交</button>
									</shiro:hasPermission>
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

	<c:import url="../foot.jsp" />

  <script src="${basePath }js/project/sysUserEdit.js"></script>

</body>
</html>