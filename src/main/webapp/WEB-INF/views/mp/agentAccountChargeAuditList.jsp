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

<title>代理商冲扣值审核列表</title>
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
					<h3>代理商列表</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>代理商冲扣值审核列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<form id="searchForm">
						<div class="row">
							<div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="product_name">代理商名称</label>
		                            <input type="text" id="agentName" name="agentName" value="" placeholder="请输入代理商名" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="product_name">渠道名称</label>
		                            <input type="text" id="channelPerson" name="channelPerson" value="" placeholder="请输入渠道名" class="form-control">
		                        </div>
		                    </div>
		                    <div class="col-md-2">
		                        <div class="form-group">
		                            <label class="control-label" for="product_name">进款账户</label>
		                            	<select class="chosen-select" id="moneyAccount" name="moneyAccount" >
											<option value="">请选择</option>
											<option value="支付宝0000">支付宝0000</option>
											<option value="支付宝6868">支付宝6868</option>
											<option value="浩百7361">浩百7361</option>
											<option value="齐鲁银行6609">齐鲁银行6609</option>
											<option value="中信5529">中信5529</option>
											<option value="中信3647">中信3647</option>
											<option value="平安商户管家">平安商户管家</option>
											<option value="其他">其他</option>
		    							</select>
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">申请时间</label>
		                             <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="applyDate" id="applyDate" required data-mask="9999-99-99 99:99:99" >
		                        </div>
		                    </div>
		                    <div class="col-md-2" >
		                        <div class="form-group">
		                            <label class="control-label" for="">结束时间</label>
		                            <input type="text" class="laydate-icon form-control" placeholder="格式:YYYY-MM-DD hh:mm:ss"
									name="confirmDate" id="confirmDate" required data-mask="9999-99-99 99:99:99">
		                        </div>
		                    </div>
		                    
		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="button" class="btn btn-success" onclick="orderReport()">导出</button>
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<button type="button" class="btn btn-primary" onclick="search()">查询</button>
		                    	</div>
		                    </div>
	                    </div>
                    </form>
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
						<button type="button" class="btn btn-primary" onclick="editEx()">修改</button>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
    <script src="${basePath }js/project/mp/agentAccountChargeAuditList.js"></script>
</body>
</html>