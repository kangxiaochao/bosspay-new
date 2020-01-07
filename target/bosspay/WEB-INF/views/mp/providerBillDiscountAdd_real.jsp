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

<title>设置话费包折扣</title>
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
				  <li><a>运营商</a></li>
				  <li><a>运营商物理通道</a></li>
				  <li class="active"><strong>设置话费包折扣</strong></li>
			  </ol>
		  </div>
		</div>

		<div class="row white-bg animated fadeInRight">
			<div class="col-md-12">

				<div class="ibox float-e-margins">


					<div class="ibox-title">
						<h5>
							<small>
					    		设置话费包折扣
					    		-
					    		<span style="color: red">${providerPhysicalChannel.name}</span>
					    	</small>
						</h5>
						<div class="ibox-tools">
				            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
				            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
				            <a class="close-link"><i class="fa fa-times"></i></a>
				        </div>
					</div>

					<div class="ibox-content">
						<div class="form-group">
				        	<div class="col-sm-12">
					        	<input type="hidden" name="providerPhysicalChannelId" id="providerPhysicalChannelId" value="${providerPhysicalChannel.id}" />
					        	<input type="hidden" name="providerPhysicalChannelName" id="providerPhysicalChannelName" value="${providerPhysicalChannel.name}"  />
				            	<input type="hidden" name="data_type" id="data_type" value="2" />
				            </div>
				            <%-- <div class="col-md-12">
								<form id="uploadForm1" name="uploadForm" method="post" action="${basePath }providerDataDiscountEx1" enctype="multipart/form-data">
									<button type="button" class="btn btn-success btn-outline" onclick="downLoadTemp()">模板下载</button>
			            			<div class="fileinput fileinput-new" data-provides="fileinput">
									    <span class="btn btn-success btn-file"><span class="fileinput-new">请选择文件</span>
									    <span class="fileinput-exists">重新选择</span><input type="file" name="providerDataDiscountFile" id="providerDataDiscountFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"/></span>
									    <span class="fileinput-filename"></span>
									    <a href="#" class="close fileinput-exists" data-dismiss="fileinput" style="float: none">×</a>
									</div>
									<button class="btn btn-primary" type="button" onclick="uploadFile()">上传</button>
			           			</form>
							</div> --%>
				        </div>
				        
						<div class="form-group">
				        	<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">运 营 商</label>
				            <div class="form-group col-sm-11">
				            	<select data-placeholder="请选择..." class="chosen-select" tabindex="2" name="providerId" id="providerId" onchange="initProvinceCode();getProviderDataPkg()"></select>
				            </div>
				        </div>
				        <div class="form-group">
				        	<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">话费类型</label>
				            <div class="form-group col-sm-11">
				            	<input type="radio" value="2" id="dataType1" name="dataType" class="i-checks" checked > 全国
				            	<input type="radio" value="1" id="dataType2" name="dataType" class="i-checks" > 省内
				            </div>
				        </div>
				        <div class="form-group">
				        	<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">话费包范围</label>
				            <div class="form-group col-sm-11">
				            	<select required data-placeholder="请选择..." class="chosen-select" name="provinceCode" id="provinceCode" onchange="getProviderDataPkg()"></select>
				            </div>
				        </div>
				        <div class="form-group">
				        	<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">是否批量操作</label>
				            <div class="form-group col-sm-11">
				            	<input id="choose" type="checkbox"  class="i-checks" checked />
				            </div>
				        </div>
				        <div class="form-group">
				        	<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">设置选中折扣</label>
				            <div class="form-group col-sm-11">
				            	<input id="dis" type="number" class="form-control" onchange="updataDiscount()" min="0.001" step="0.001"/>
				            </div>
				        </div>
						<div class="form-group">
							<label class="col-sm-1 control-label" style="padding-left: 0px;padding-right: 0px;">折扣设置</label>
							<div class="col-md-11" id="provinceDataDiscount"></div>
				        </div>
				        
				        <div class="form-group">
				            <div class="col-sm-4 col-sm-offset-4">
				            	<button type="button" class="btn btn-primary btn-outline" 
				            		onclick="javascript:location.href='${basePath }providerDataDiscountListPage/${providerPhysicalChannel.id}'">返回</button>
				            	<button type="button" class="btn btn-primary" onclick="setProvinceDataDicount();">保存</button> 
				            	<button type="button" class="btn btn-success btn-outline" style="visibility: hidden;" id="showProviderDataDiscountList"
				            		onclick="javascript:location.href='${basePath }providerDataDiscountListPage/${providerPhysicalChannel.id}'">查看详情</button>
				            </div>
				        </div>
					</div>
					<!-- end div ibox content -->
				</div>
				<!-- end div ibox -->
			</div>
			<!-- end div col 12 -->
		</div>
		<!-- end div row -->
		<c:import url="../copyright.jsp" />
	</div>
  <iframe id="downloadFrame" style="display: none"></iframe>
  <c:import url="../foot.jsp" />
  <script src="${basePath }js/lib/jquery.form.js"></script>
  <script src="${basePath }js/lib/city.js"></script>
  <script src="${basePath }js/project/mp/providerDataDiscountAdd.js"></script>

</body>
</html>