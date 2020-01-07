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

<title>代理商编辑</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link href="${basePath }css/project/laydate.css" rel="stylesheet">
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
					<li><a>代理商</a></li>
					<li class="active"><strong>代理商编辑</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight" id="agentRole" data-agentRole='${agentRole }'>
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>代理商编辑</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" action="${basePath }agent/${agent.id}" class="form-horizontal">
							<div class="form-group">
								<label class="col-md-2 control-label">接口编号</label>
								<input type="hidden" name="id" id="id" value="${agent.id}"  />
								<div class="col-md-8">
									<input type="text" placeholder="只能输入英文+数字" class="form-control" name="name" id="name" maxlength="25" required value="${agent.name}" readonly="readonly">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">代理商名称</label>
								<div class="col-md-8">
									<input type="text" placeholder="只能输入中文" class="form-control" name="nickname"	id="nickname" maxlength="25" value="${agent.nickname}" onkeyup="value=value.replace(/[^\u4E00-\u9FA5]/g,'')">
								</div>
								<span class="help-block m-b-none">最多输入25位字符</span>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">用 户 名</label>
								<input type="hidden" name="userId" id="userId" value="${agent.user_id}"  />
								<div class="col-md-8">
								<input type="text" class="form-control" maxlength="25" required value="${agent.suName}" readonly="readonly">
									<%-- <select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="userId" id="userId">
										<option value="${agent.user_id}">${agent.suName}</option>
									</select> --%>
								</div>
							</div>

<!-- 							<div class="form-group"> -->
<!-- 								<label class="col-md-2 control-label">上级代理商</label> -->
								
<!-- 								<div class="col-md-8"> -->
<!-- 								<input type="text" class="form-control" name="oldParentId" id="oldParentId" value="${agent.parent_id}" required readonly="readonly"/> -->
<!-- 									<input type="text" class="form-control" name="parentId" id="parentId" maxlength="25" required value="${agent.parent_id}" readonly="readonly"> -->
<!-- 									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="parentId" id="parentId"></select> -->
<!-- 								</div> -->
<!-- 							</div> -->
							<c:if test='${agentRole!="代理商" }'>
								<div class="form-group">
									<label class="col-md-2 control-label">话费通道组</label>
									<input type="hidden" name="oldBillGroupId" id="oldBillGroupId" value="${agent.bill_group_id}"  />
									<div class="col-md-8">
										<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="billGroupId" id="billGroupId"></select>
									</div>
								</div>
	
								<div class="form-group">
									<label class="col-md-2 control-label">话费折扣模板</label>
									<input type="hidden" name="oldBillModelId" id="oldBillModelId" value="${agent.bill_model_id}"  />
									<div class="col-md-8">
										<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="billModelId" id="billModelId"></select>
									</div>
								</div>
	
							</c:if>
							<c:if test='${roleAdmin=="超级管理员" }'>
							<div class="form-group">
								<label class="col-md-2 control-label">渠道人员</label>
								<input type="hidden" name="oldChannelPerson" id="oldChannelPerson" value="${agent.channel_person}"  />
								<div class="col-md-8">
									<select class="chosen-select"  name="channelPerson" id="channelPerson"></select>
								</div>
							</div>
							</c:if>
							<%-- <div class="form-group">
								<label class="col-md-2 control-label">授信额</label>
								<div class="col-md-8">
									<input type="int" name="credit" id="credit" value="${agent.credit}">
								</div>
							</div> --%>

							<div class="form-group">
								<label class="col-md-2 control-label">生效日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control"
										placeholder="格式:yyyy-mm-dd" name="startDate" id="startDate"
										required data-mask="9999-99-99"  value="${agent.start_date}">
								</div>
								<span class="help-block m-b-none">不能小于当前日期</span>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">截止日期</label>
								<div class="col-md-8">
									<input type="text" class="laydate-icon form-control"
										placeholder="格式:yyyy-mm-dd" name="endDate" id="endDate"
										required data-mask="9999-99-99"  value="${agent.end_date}">
								</div>
								<span class="help-block m-b-none">不能小于当前日期</span>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">是否生效</label>
								<input type="hidden" name="oldStatus" id="oldStatus" value="${agent.status}"  />
								<div class="col-md-8">
									<input name="status" id="status" type="checkbox" checked
										data-on-color="success" data-off-color="danger"
										data-on-text="启用" data-off-text="禁用">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">手机号</label>
								<div class="col-md-8">
									<input type="tel" class="form-control" name="phone" id="phone"
										data-mask="999 9999 9999" value="${agent.phone}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">邮 箱</label>
								<div class="col-md-8">
									<input type="email" class="form-control" name="email"
										id="email" value="${agent.email}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">公司名</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="corpName"
										id="corpName" value="${agent.corp_name}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">地 址</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="address"
										id="address" value="${agent.address}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 1</label>
								<div class="col-md-8">
									<input type="time" class="form-control" name="hotline1"
										id="hotline1" value="${agent.hotline1}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">热 线 2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="hotline2"
										id="hotline2" value="${agent.hotline2}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ1</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="serviceqq1"
										id="serviceqq1" value="${agent.serviceqq1}">
								</div>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">客服QQ2</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="serviceqq2"
										id="serviceqq2" value="${agent.serviceqq2}">
								</div>
							</div>

							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }agentListPage'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="agent:put"><button type="button" class="btn btn-primary" onclick="submitEdit();">提交</button></shiro:hasPermission> 
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
	<script src="${basePath }js/project/mp/agentEdit.js"></script>

</body>
</html>