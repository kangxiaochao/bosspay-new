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

<title>修改号段</title>
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
					<li><a>系统管理</a></li>
					<li class="active"><strong>修改号段</strong></li>
				</ol>
			</div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5><small>更新号段</small></h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
							<a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a> 
							<a class="close-link"><i class="fa fa-times"></i></a>
						</div>
					</div>

					<div class="ibox-content">
						<form id="fm" action="${basePath }}phoneSection/${phoneSection.id}" class="form-horizontal">
							<div class="form-group">
								<label class="col-md-2 control-label">号段</label>
								<input type="hidden" name="id" id="id" value="${phoneSection.id}"  />
								<div class="col-md-8">
									<input type="tel" class="form-control" name="section" id="section" value="${phoneSection.section}" data-mask="9999999" readonly="readonly" required>
								</div>
								<span class="help-block m-b-none">长度必须7位</span>
							</div>

							<div class="form-group">
								<label class="col-md-2 control-label">运营商</label>
								<input type="hidden" name="oldProviderId" id="oldProviderId" value="${phoneSection.provider_id}"  />
								<input type="hidden" name="oldProviderCode" id="oldProviderCode" value="${phoneSection.province_code}"  />
								<input type="hidden" name="oldCityCode" id="oldCityCode" value="${phoneSection.city_code}"  />
								<div class="col-md-8">
									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="providerId" id="providerId"></select>
								</div>
							</div>
							
							<%-- <div class="form-group">
								<label class="col-md-2 control-label">省 份</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="provinceCode" id="provinceCode" value="${phoneSection.province_code}" maxlength="25">
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">城 市</label>
								<div class="col-md-8">
									<input type="text" class="form-control" name="cityCode" id="cityCode" value="${phoneSection.city_code}" maxlength="25">
								</div>
							</div> --%>
							
							<div class="form-group">
								<label class="col-md-2 control-label">省 份</label>
								<div class="col-md-8">
									<select data-placeholder="请选择..." class="chosen-select" name="provinceCode" id="provinceCode" onchange="initCity(this.value,'cityCode');initCityCodeStyle();"></select>
								</div>
							</div>
							
							<div class="form-group">
								<label class="col-md-2 control-label">城 市</label>
								<div class="col-md-8">
									<select data-placeholder="请选择..." class="chosen-select" name="cityCode" id="cityCode"></select>
								</div>
							</div>
							
							<%-- <div class="form-group">
								<label class="col-md-2 control-label">类 型</label>
								<input type="hidden" name="oldCarrierType" id="oldCarrierType" value="${phoneSection.carrier_type}"  />
								<div class="col-md-8">
									<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="carrierType" id="carrierType">
										<option value="1">中国移动</option>
										<option value="2">中国联通</option>
										<option value="3">中国电信</option>
										<option value="4">虚拟运营商移动</option>
										<option value="5">虚拟运营商联通</option>
										<option value="6">虚拟运营商电信</option>
										<option value="7">物联网卡</option>
										<option value="99" selected>未 知</option>
									</select>
								</div>
							</div> --%>
							
							<div class="form-group"><label class="col-sm-2 control-label">号段类型</label>
					            <div class="col-sm-10">
					            	<input type="hidden" name="myCarrierType" id="myCarrierType" value="${phoneSection.carrier_type}"  />
					            	<input type="radio" id="carrierType1" value="1" name="carrierType" class="i-checks"> 普卡　
					            	<input type="radio" id="carrierType7" value="7" name="carrierType" class="i-checks"> 物联网卡
					            </div>
					        </div>

							<div class="form-group">
								<div class="col-md-4 col-md-offset-2">
									<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }phoneSectionListPage'">返回</button>
									<button type="reset" class="btn btn-warning">重置</button>
									<shiro:hasPermission name="phoneSection:put"><button type="button" class="btn btn-primary" onclick="submitEdit();">提交</button></shiro:hasPermission> 
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
	<script src="${basePath }js/lib/city.js"></script>
	<script src="${basePath }js/project/mp/phoneSectionEdit.js"></script>
</body>
</html>