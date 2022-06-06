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

<title>代理商列表</title>
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
						<li><a>代理商</a></li>
						<li class="active"><strong>代理商列表</strong></li>
					</ol>
				</div>
			</div>

			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<!-- 查询模块 -->
				<div class="ibox-content m-b-sm border-bottom" id="queryDiv">
					<div class="row">
						<form class="form-horizontal">
							<div class="col-md-6">
		                        <div class="form-group">
		                            <label class="col-md-2 control-label" for="product_name">代理商</label>
		                            <div class="col-md-9">
		                            	<input type="text" id="name" name="name" value="" placeholder="请输入代理商" class="form-control">
		                            </div>
		                        </div>
		                    </div>
		                     <div class="col-md-6">
		                        <div class="form-group">
		                            <label class="col-md-2 control-label" for="price">代理商名称</label>
		                            <div class="col-md-9">
			                            <input type="text" id="nickname" name="nickname" value="" placeholder="请输入代理商名称" class="form-control">
		                            </div>
		                        </div>
		                    </div>

							<div class="col-md-6">
								<div class="form-group">
									<label class="col-md-2 control-label" for="price">下级代理商查询</label>
									<div class="col-md-9">
										<input type="text" id="agentParentId" name="agentParentId" value="" placeholder="请输入代理商名称" class="form-control">
									</div>
								</div>
							</div>



		                    <div class="col-md-12">
		                    	<div class="text-center">
		                    		<button type="reset" class="btn btn-warning">重置</button>
		                    		<button type="button" class="btn btn-primary" onclick="search()">查询</button>
		                    	</div>
		                    </div>
	                    </form>
                    </div>
				</div>
				
				<!-- 列表模块 -->
				<div class="ibox-content m-b-sm border-bottom">
					<div class="row">
						<div class="col-md-12">
							<shiro:hasPermission name="agentAddPage:get"><button type="button" class="btn btn-primary" onclick="add()">添加</button></shiro:hasPermission>
							<shiro:hasPermission name="agentDetail:get"><button type="button" class="btn btn-primary btn-outline" onclick="detailEx()">详情</button></shiro:hasPermission>
							<shiro:hasPermission name="agentKeyEditPage:get"><button type="button" class="btn btn-warning btn-outline" onclick="setAppKeyEx()">密钥</button></shiro:hasPermission>
							<shiro:hasPermission name="agentEditPage:get"><button type="button" class="btn btn-success btn-outline" onclick="editEx()">修改</button></shiro:hasPermission>
							<shiro:hasPermission name="agent:delete"><button type="button" class="btn btn-danger btn-outline" onclick="delEx()">删除</button></shiro:hasPermission>
							<%-- <shiro:hasPermission name="agentBillDiscountListPage:get"><button type="button" class="btn btn-primary" onclick="bill()">话费折扣</button></shiro:hasPermission>
							<shiro:hasPermission name="agentDataDiscountListPage:get"><button type="button" class="btn btn-primary" onclick="data()">话费折扣</button></shiro:hasPermission> --%>
							<shiro:hasPermission name="agentAccountEditPage:get"><button type="button" class="btn btn-primary btn-outline" onclick="agentAccountEx()">加款</button></shiro:hasPermission>
							<shiro:hasPermission name="agentChannelRelListPage:get"><button type="button" class="btn btn-primary btn-outline" onclick="agentPreferentialChannel()">设置代理商话费特惠通道</button></shiro:hasPermission>
							<shiro:hasPermission name="agentPrivateKey:get"><button type="button" class="btn btn-primary btn-outline" onclick="alertAndAddKey()">生成密钥</button></shiro:hasPermission>
							<shiro:hasPermission name="agentBalance:get"><button type="button" class="btn btn-primary btn-outline" onclick="balance()">设定限额</button></shiro:hasPermission>
							<shiro:hasPermission name="agentProfitsPlusMoney:get"><button type="button" class="btn btn-primary btn-outline" onclick="profitsPlusMoney()">利润加款</button></shiro:hasPermission>
							<div id="mytbc1"></div>
						</div>
					</div>
				</div>
			</div>
			<c:import url="../copyright.jsp" />
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/agentList.js"></script>
</body>
</html>
