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

<title>增加代理商</title>
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
					<li class="active"><strong>增加代理商</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight" id="agentRole" data-agentRole="${agentRole }">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5><small>增加代理商</small></h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
							<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a> 
							<a class="close-link"><i class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" method="post" action="${basePath }agent" class="form-horizontal">
							<div class="form-group">
								<label class="col-md-2 control-label">接口编号</label>
								<div class="col-md-8">
									<input placeholder="只能输入英文+数字" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" type="text" class="form-control" name="name" id="name" onchange="checkAgentName(this.value)" maxlength="25" required>
								</div>
								<span class="help-block m-b-none">
									<span style="color:red" id="agentNameMsg"></span>
								</span>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">代理商名称</label>
								<div class="col-md-8">
									<input type="text" placeholder="只能输入中文" class="form-control" name="nickname" id="nickname" maxlength="25" onkeyup="value=value.replace(/[^\u4E00-\u9FA5]/g,'')">
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">用户名</label>
								<div class="col-md-8">
									<input type="text" placeholder="只能输入英文+数字" class="form-control" name="suName" id="suName" onblur="checkSuName();" required onkeyup="value=value.replace(/[^\w\.\/]/ig,'')">
								</div>
								<span class="help-block m-b-none">
									<span style="color:red" id="suNameMsg"></span>
								</span>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">密　码</label>
								<div class="col-md-8">
									<input type="password" class="form-control" name="suPass" id="suPass" onblur="checkSuPass();" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 control-label">确认密码</label>
								<div class="col-md-8">
									<input type="password" class="form-control" name="suPass1" id="suPass1" onblur="checkSuPass();" required>
								</div>
								<span class="help-block m-b-none">
									<span style="color:red" id="suPass1Msg"></span>
								</span>
							</div>
							<%-- <c:if test='${agentRole=="代理商" }'>
								<div class="form-group">
									<label class="col-md-2 control-label">上级代理商</label>
									<div class="col-md-8">
										<select  class="chosen-select"  name="parentId" id="parentId"></select>
									</div>
								</div>
							</c:if> --%>
							<c:if test='${agentRole!="代理商" }'>
								<div class="form-group">
									<label class="col-md-2 control-label">话费通道组</label>
									<div class="col-md-8">
										<select class="chosen-select"  name="billGroupId" id="billGroupId"></select>
									</div>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">话费折扣模板</label>
									<div class="col-md-8">
										<select  class="chosen-select"  name="billModelId" id="billModelId"></select>
									</div>
								</div>
<!-- 							
<div class="form-group">
									<label class="col-md-2 control-label">渠道人员</label>
									<div class="col-md-8">
										<select class="chosen-select" required="required" name="channelPerson" id="channelPerson"></select>
									</div>
								</div>
 -->							</c:if>
							<!-- <div class="form-group">
								<label class="col-md-2 control-label">授信额</label>
								<div class="col-md-8">
									<input type="int" name="credit" id="credit" value="0">
								</div>
							</div> -->

							<div class="form-group">
								<label class="col-md-2 control-label">生效日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control" placeholder="格式:yyyy-mm-dd"
									name="startDate" id="startDate" required data-mask="9999-99-99">
								</div>
								<span class="help-block m-b-none">不能小于当前日期</span>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">截止日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control" placeholder="格式:yyyy-mm-dd"
									name="endDate" id="endDate" required data-mask="9999-99-99">
								</div>
								<span class="help-block m-b-none">不能小于当前日期</span>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">是否生效</label>
								<div class="col-md-8">
									<input name="status" id="status" type="checkbox" checked 
										data-on-color="success" data-off-color="danger" data-on-text="启用" data-off-text="禁用">
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">手机号</label>
								<div class="col-md-8">
									<input type="tel" class="form-control" name="phone" id="phone" data-mask="999 9999 9999">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">邮　箱</label>
								<div class="col-md-8">
									<input type="email" class="form-control" name="email" id="email">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">公司名</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="corpName" id="corpName">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">地　址</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="address" id="address">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 1</label>
								<div class="col-md-8">
									<input type="time" class="form-control" name="hotline1" id="hotline1">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="hotline2" id="hotline2">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ1</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="serviceqq1" id="serviceqq1">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="serviceqq2" id="serviceqq2">
								</div>
							</div>

							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }agentListPage'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="agent:post"><button type="submit" class="btn btn-primary" onclick="return check()">增加</button></shiro:hasPermission>
								</div>
							</div>
						</form>
					</div><!-- end div ibox content -->
				</div><!-- end div ibox -->
			</div><!-- end div col 12 -->
		</div><!-- end div row -->

		<c:import url="../copyright.jsp" />
	</div><!-- end page wrapper -->
	<c:import url="../foot.jsp" />
	
	<script src="${basePath }js/project/mp/agentAdd.js"></script>
</body>
</html>