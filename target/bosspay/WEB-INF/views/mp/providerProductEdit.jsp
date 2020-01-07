<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
	request.setAttribute("basePath", basePath);
	out.clear();
%>

<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath}">

<title></title>
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
		<input type="hidden" value="${backMsg}" id="backMsg">
		<div class="row wrapper border-bottom white-bg page-heading">
			<div class="col-lg-10">
				<br />
				<ol class="breadcrumb">
					<li><a href="${basePath }mainPage">主页</a></li>
					<li><a>资源配置</a></li>
					<li class="active"><strong>天猫上家产品管理</strong></li>
				</ol>
			</div>
		</div>
		<input type="hidden" value="${provider.pcId}" id="pcid">
		<input type="hidden" value="${provider.pkId}" id="pkid">
		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">
				<div class="ibox float-e-margins">
					<div class="ibox-title">
						<h5>
							<small>天猫上家产品修改</small>
						</h5>
						<div class="ibox-tools">
							<a class="collapse-link"><i class="fa fa-chevron-up"></i></a> <a
								class="dropdown-toggle" data-toggle="dropdown" href="#"><i
								class="fa fa-wrench"></i></a> <a class="close-link"><i
								class="fa fa-times"></i></a>
						</div>
					</div>
					<div class="ibox-content">
						<form method="post" action="updateProvider" class="form-horizontal">
							<input type="hidden" name="id" value="${provider.id}">
							<div class="form-group">
								<label class="col-sm-2 control-label">请选择花费包：</label>
								<div class="col-sm-10">
									<select class="chosen-select" tabindex="2" id="pkgId" name="pkgId" >
		                            </select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">通道选择：</label>
								<div class="col-sm-10">
									<select class="form-control" name="providerId">
										<option value="0000000001" selected='<c:if test="${provider.provider='0000000001'}">selected</c:if>'>中国移动</option>
										<option value="0000000002" selected='<c:if test="${provider.provider='0000000002'}">selected</c:if>'>中国联通</option>
										<option value="0000000003" selected='<c:if test="${provider.provider='0000000003'}">selected</c:if>'>中国电信</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">运营商物理通道：</label>
								<div class="col-sm-10">
									<select class="chosen-select" tabindex="2" id="physicalChannelId" name="physicalChannelId">
		                            </select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">上家产品ID：</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" placeholder="上家产品ID"
										name="providerPkgId" id="providerPkgId" value="${provider.pId}" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-2 control-label">省份：</label>
								<div class="col-sm-10">
									<select class="chosen-select" tabindex="2" id="provinceCode" name="provinceCode">
										<option value="北京" selected='<c:if test="${provider.code='北京'}">selected</c:if>'>北京</option>
										<option value="上海" selected='<c:if test="${provider.code='上海'}">selected</c:if>'>上海</option>
										<option value="天津" selected='<c:if test="${provider.code='天津'}">selected</c:if>'>天津</option>
										<option value="重庆" selected='<c:if test="${provider.code='重庆'}">selected</c:if>'>重庆</option>
										<option value="吉林" selected='<c:if test="${provider.code='吉林'}">selected</c:if>'>吉林</option>
										<option value="辽宁" selected='<c:if test="${provider.code='辽宁'}">selected</c:if>'>辽宁</option>
										<option value="江苏" selected='<c:if test="${provider.code='江苏'}">selected</c:if>'>江苏</option>
										<option value="山东" selected='<c:if test="${provider.code='山东'}">selected</c:if>'>山东</option>
										<option value="安徽" selected='<c:if test="${provider.code='安徽'}">selected</c:if>'>安徽</option>
										<option value="河北" selected='<c:if test="${provider.code='河北'}">selected</c:if>'>河北</option>
										<option value="河南" selected='<c:if test="${provider.code='河南'}">selected</c:if>'>河南</option>
										<option value="湖北" selected='<c:if test="${provider.code='湖北'}">selected</c:if>'>湖北</option>
										<option value="湖南" selected='<c:if test="${provider.code='湖南'}">selected</c:if>'>湖南</option>
										<option value="江西" selected='<c:if test="${provider.code='江西'}">selected</c:if>'>江西</option>
										<option value="陕西" selected='<c:if test="${provider.code='陕西'}">selected</c:if>'>陕西</option>
										<option value="山西" selected='<c:if test="${provider.code='山西'}">selected</c:if>'>山西</option>
										<option value="四川" selected='<c:if test="${provider.code='四川'}">selected</c:if>'>四川</option>
										<option value="青海" selected='<c:if test="${provider.code='青海'}">selected</c:if>'>青海</option>
										<option value="海南" selected='<c:if test="${provider.code='海南'}">selected</c:if>'>海南</option>
										<option value="广东" selected='<c:if test="${provider.code='广东'}">selected</c:if>'>广东</option>
										<option value="贵州" selected='<c:if test="${provider.code='贵州'}">selected</c:if>'>贵州</option>
										<option value="浙江" selected='<c:if test="${provider.code='浙江'}">selected</c:if>'>浙江</option>
										<option value="福建" selected='<c:if test="${provider.code='福建'}">selected</c:if>'>福建</option>
										<option value="台湾" selected='<c:if test="${provider.code='台湾'}">selected</c:if>'>台湾</option>
										<option value="甘肃" selected='<c:if test="${provider.code='甘肃'}">selected</c:if>'>甘肃</option>
										<option value="云南" selected='<c:if test="${provider.code='云南'}">selected</c:if>'>云南</option>
										<option value="宁夏" selected='<c:if test="${provider.code='宁夏'}">selected</c:if>'>宁夏</option>
										<option value="西藏" selected='<c:if test="${provider.code='西藏'}">selected</c:if>'>西藏</option>
										<option value="广西" selected='<c:if test="${provider.code='广西'}">selected</c:if>'>广西</option>
										<option value="香港" selected='<c:if test="${provider.code='香港'}">selected</c:if>'>香港</option>
										<option value="澳门" selected='<c:if test="${provider.code='澳门'}">selected</c:if>'>澳门</option>
										<option value="黑龙江" selected='<c:if test="${provider.code='黑龙江'}">selected</c:if>'>黑龙江</option>
										<option value="内蒙古" selected='<c:if test="${provider.code='内蒙古'}">selected</c:if>'>内蒙古</option>
										<option value="新疆维吾尔" selected='<c:if test="${provider.code='新疆维吾尔'}">selected</c:if>'>新疆维吾尔</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<div class="col-sm-4 col-sm-offset-2">
									<button type="button" class="btn btn-primary btn-outline"
										onclick="javascript:location.href='${basePath }providerProductList'">返回</button>
									<input type="submit" value="修改" class="btn btn-primary">
									<button type="reset" class="btn btn-white">重写</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>

		<c:import url="../copyright.jsp" />

	</div>
	<!-- end page wrapper -->

	<c:import url="../foot.jsp" />

	<script src="${basePath }js/project/mp/ProviderProduct.js"></script>

</body>
</html>
