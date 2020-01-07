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

<title>快捷操作列表</title>
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
					<h3>快捷操作列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>其他</a></li>
						<li class="active"><strong>快捷操作列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<!-- 切换海航币账号 -->
                        <div class="btn-group float-e-margins">
                            <button type="button" id="hhb" class="btn btn-primary" onclick="initTimer(this)">切换海航币账号</button>
                        </div>
                        <!-- 刷新兔兔币cookies -->
                        <div class="btn-group float-e-margins">
                            <button type="button" id="ttb" class="btn btn-success" onclick="refreshCookies(this)">刷新兔兔币cookies</button>
                        </div>
                        <!-- 更新迪信通卡密 -->
                        <div class="btn-group float-e-margins">
                            <button type="button" id="dxtmy" class="btn btn-info" onclick="updateKey(this)">更新迪信通秘钥</button>
                        </div>
                        <!-- 刷新用友cookies -->
                        <div class="btn-group float-e-margins">
                            <button type="button" id="yy" class="btn btn-success" onclick="refreshYongYouCookies(this)">刷新用友cookies</button>
                        </div>
                        <!-- 备用二 -->
                        <!-- <div class="btn-group float-e-margins">
                            <button type="button" id="by02" class="btn btn-danger" onclick="by02(this)">备用一</button>
                        </div> -->
                    </div>
                 </div>
                 <div class="ibox-content m-b-sm border-bottom">   
                    <form id="searchForm">
	                    <div class="row">
	                    	<div class="col-md-2">
								<label class="control-label" for="">开始时间</label>
								<input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss" 
									name="startDate" id="startDate" required data-mask="9999-99-99 99:99:99" >
		                    </div>
		                    <div class="col-md-2">
	                            <label class="control-label" for="">结束时间</label>
	                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99">
		                    </div>
		                    <div class="col-md-1">
		                    	 <label class="control-label" for="">　</label>
								 <button type="button" id="newCustomerAgent" class="btn btn-primary" onclick="exportNewCustomerAgent(this)">导出新加客户 </button>
	                        </div>
		                    <div class="col-md-1">
		                    	 <label class="control-label" for="">　</label>
								 <button type="button" id="monthlyAddMoneyAgent" class="btn btn-success" onclick="exportMonthlyAddMoneyAgent(this)">导出月度加款客户</button>
	                        </div>
	                    </div>
                    </form>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/shortcutList.js"></script>
</body>
</html>
