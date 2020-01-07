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

<title>代理商折扣详情</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />
</head>

<body>

  <c:import url="../left.jsp" />
  <!-- --------------------------------------------- -->
	<div id="page-wrapper" class="gray-bg dashbard-1">
		<div class="row border-bottom">
			<c:import url="../topnav.jsp" />
		</div>

		<div class="row wrapper border-bottom white-bg page-heading">
          <div class="col-lg-10"><br/>
		      <ol class="breadcrumb">
			      <li><a href="${basePath }mainPage">主页</a></li>
				  <li><a>代理商</a></li>
				  <li><a>代理商折扣</a></li>
				  <li class="active"><strong>折扣详情</strong></li>
			  </ol>
		  </div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">

				<div class="ibox float-e-margins">


					<div class="ibox-title">
						<h5>
							<small>
					    		代理商名称
					    		-
					    		<span style="color: red">${agent.name}</span>
					    	</small>
						</h5>
						<div class="ibox-tools">
				            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
				            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
				            <a class="close-link"><i class="fa fa-times"></i></a>
				        </div>
					</div>
					<form id="fm" action="${basePath }agentDataDiscountUpdate"class="form-horizontal">
					<div class="ibox-content">
						<div class="form-group col-md-12">
				        	<label class="col-sm-2 control-label">运营商</label>
				            <div class="form-group col-sm-10" id="providerCheck">
				            </div>
				        </div>
				         <div class="form-group col-md-12">
				        	<label class="col-sm-2 control-label">话费类型</label>
				            <div class="form-group col-sm-10" >
				            <input name="dataType"  class="i-checks" type="radio" value="2"  checked="checked"/>全国
				            <input name="dataType"  class="i-checks" type="radio" value="1" />省内
				            </div>
				        </div>
				        <input type="hidden" id="agentId" value="${agent.id}">
				        <div class="form-group col-md-12">
				        	<label class="col-sm-2 control-label">可用通道</label>
				            <div class="form-group col-sm-10" id="province">
				            </div>
				        </div>
				        <div class="form-group col-md-12" id="choose">
				        	<label class="col-sm-2 control-label">批量设置</label>
				            <div class="form-group col-sm-10" >
				            <input type="number" id="dis"/>
				            <input type="button" onclick="selectAllItem()" value="设置"/>
				            </div>
				        </div>
						<div class="form-group col-md-12">
							<label class="col-sm-2 control-label">折扣详情</label>
							<div class="col-md-10" id="agentDataDiscount">
							</div>
				        </div>
				        <div class="form-group">
				            <div class="col-sm-4 col-sm-offset-4">
				            	<button type="button" class="btn btn-primary btn-outline" 
				            		onclick="javascript:location.href='${basePath }agentListPage'">返回</button>
				            	<button type="button" class="btn btn-primary" onclick="checkUpdata()">保存</button> 
				            </div>
				        </div>
					</div>
					</form>
					<!-- end div ibox content -->
				</div>
				<!-- end div ibox -->
			</div>
			<!-- end div col 12 -->
		</div>
		<!-- end div row -->
		<c:import url="../copyright.jsp" />
	</div>
  <c:import url="../foot.jsp" />
  <script src="${basePath }js/lib/jquery.form.js"></script>
  <script src="${basePath }js/project/mp/agentDataDiscountDetail.js"></script>

</body>
</html>