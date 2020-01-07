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

<title>卡密列表</title>
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
					<h3>卡密列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>卡密列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form id="searchForm">
						<div class="row">
							<div class="col-md-3">
		                        <div class="form-group">
		                            <label class="control-label" for="nickName">卡号</label>
		                            <input type="text" id="nickName" name="cardId" value="" placeholder="请输入卡号" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-3">
		                        <div class="form-group">
		                            <label class="control-label" for="payName">密码</label>
		                            <input type="text" id="payName" name="cardPass" value="" placeholder="请输入密码" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-3">
		                        <div class="form-group">
		                            <label class="control-label" for="moneyAccount">类型</label>
		                            <input type="text" id="type" name="type" value="" placeholder="请输入类型" class="form-control">
		                        </div>
		                    </div>
		                     <div class="col-md-3">
		                        <div class="form-group">
		                            <label class="control-label" for="moneyAccount">使用标识</label>
		                            <input type="text" id="useState" name="useState" value="" placeholder="请输入使用标识" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-4">
		                        <div class="form-group">
		                            <label class="control-label" for="moneyAccount">订单号</label>
		                            <input type="text" id="moneyAccount" name="orderId" value="" placeholder="请输入订单号" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-4" >
		                        <div class="form-group">
		                            <label class="control-label" for="">使用开始时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="startDate" id="startDate" required data-mask="9999-99-99 99:99:99" >
		                        </div>
		                    </div>
		                    <div class="col-md-4" >
		                        <div class="form-group">
		                            <label class="control-label" for="">使用结束时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="endDate" id="endDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<button type="button" class="btn btn-primary" onclick="search()">查询</button>
		                    	</div>
		                    </div>
	                    </div>
                    </form>`
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<button type="button" class="btn btn-primary" onclick="location.href='exportCardTemp'">下载模板</button>
							<button type="button" class="btn btn-primary" onclick="toImportCard()">导入卡密</button>
							<button type="button" class="btn btn-primary" onclick="updateCard()">修改卡密状态</button>
							<button type="button" class="btn btn-primary" onclick="exportCard()">导出卡密信息</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
    <script src="${basePath }js/project/mp/cardList.js"></script>
</body>
</html>