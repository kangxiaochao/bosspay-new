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

<title>角色分配列表</title>
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
					<h3>用户列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>角色分配列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<form>
						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label class="control-label" for="product_name">角色名
									
									</label> <input
										type="text" id="srName" name="srName" value=""
										placeholder="请输入角色名" class="form-control">
								</div>
							</div>
							<div class="col-md-12">
								<div class="text-center">
									<button type="reset" class="btn btn-warning">重置</button>
									<button type="button" class="btn btn-primary"
										onclick="search()">查询</button>
								</div>
							</div>
						</div>
					</form>
				</div>

				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<div>
								<form id="userRole" name="userRole" method="post" action="${basePath }sysRoleAddOrEdit" class="form-horizontal" >
									<input id="suId" name="suId" value="${suId}" type="hidden">
									<input id="hasRoleIds" name="hasRoleIds" value="" type="hidden">
									<div class="form-group">
										<table >
											<tr>
												<td align="center">未拥有</td>
												<td width="100px" align="center">操作选项</td>
												<td align="center">已拥有</td>
											</tr>
											<tr>
												<td align="center">
													<select multiple="multiple" size="15" id="noRole" name="noRole" style="min-width: 200px;">
													</select>
												</td>
												<td align="center">
													<button type="button"
														onclick="sourceToTargetSe('noRole','hasRole',true);">&nbsp;&nbsp;>&nbsp;&nbsp;</button>
													<br> <br>
													<button type="button"
														onclick="sourceToTargetSe('hasRole','noRole',true);">&nbsp;&nbsp;<&nbsp;&nbsp;</button>
													<br> <br>
													<button type="button"
														onclick="sourceToTargetSe('noRole','hasRole',false);">&nbsp;>>&nbsp;</button>
													<br> <br>
													<button type="button"
														onclick="sourceToTargetSe('hasRole','noRole',false);">&nbsp;<<&nbsp;</button>
													<br> <br>
												</td>
												<td align="center">
													<select multiple="multiple" size="15" id="hasRole" name="hasRole" style="min-width: 200px;">
													</select>
												</td>
											</tr>
										</table>
									</div>
									<div class="form-group">
		            					<div class="col-sm-4 col-sm-offset-2">
		            						<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }sysUserListPage'">返回</button>
											<button type="reset" class="btn btn-warning">重置</button>
		                					<shiro:hasPermission name="sysRoleAddOrEdit:post"><button onclick="getArr()" class="btn btn-primary">确定</button></shiro:hasPermission>
		           					 	</div>
		       				 		</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script src="${basePath }js/project/sysRoleAddOrEdit.js"></script>
</body>
</html>