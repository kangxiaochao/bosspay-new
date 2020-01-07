<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
	request.setAttribute("basePath", basePath);
	out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${basePath }">

<title>同面值话费批号充值</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="${basePath }ui/inspinia/css/jquery-labelauty.css">
<style>
ul { list-style-type: none;}
li { display: inline-block;}
li { margin: 10px 0;} 
input.labelauty + label { font: 12px "Microsoft Yahei";}
</style>
<c:import url="../head.jsp" />
</head>

 <body>
	<div id="wrapper">
		<c:import url="../left.jsp" />
		<div id="page-wrapper" class="gray-bg dashbard-1">
			<div class="row border-bottom">
				<c:import url="../topnav.jsp" />
			</div>

			<div class="row wrapper border-bottom white-bg page-heading">
				<div class="col-lg-10">
					<h3>话费充值</h3>
					<ol class="breadcrumb">
						<li><a href="${basePath }mainPage">主页</a></li>
						<li><a>系统</a></li>
						<li class="active"><strong>同面值话费批号充值</strong></li>
					</ol>
				</div>
				
				<div style="margin-top:15px;">
						<button type="button" class="btn btn-success btn-outline" onclick="downLoadTemp()">模板下载</button>
				</div>
				<iframe id="downloadFrame" style="display: none"></iframe>
			</div>
			
			<div class="wrapper wrapper-content animated fadeInRight ecommerce">
				<div class="ibox-content m-b-sm border-bottom">
					<form id="fileForm" name="fileForm"  method="post" action="${basePath }batchFilterPhone" enctype="multipart/form-data" style="width: 1px; height: 1px;">
	          			<input name="action" type="hidden" value="importNumber">
	          			<input required  type="file" id="numberFile"  name="numberFile" onchange="submitForm.click()" style="position: absolute; filter: alpha(opacity=0); opacity: 0; width: 30px;" size="1" accept=".txt">
	          			<shiro:hasPermission name="batchFilterPhone:post">                   			
	      				<input type="submit" id="submitForm" style="display: none;">
	      				</shiro:hasPermission>
	      			</form>
	      			<form id="fileForm1" name="fileForm1"  method="post" action="${basePath }batchFilterPhoneByExcelTwo" enctype="multipart/form-data" style="width: 1px; height: 1px;">
	          			<input name="action" type="hidden" value="importNumber">
	          			<shiro:hasPermission name="batchFilterPhoneByExcel:post">
	          			<input required  type="file" id="numberFile2"  name="numberFile2" onchange="submitForm.click()" style="position: absolute; filter: alpha(opacity=0); opacity: 0; width: 30px;" size="1" accept=".xlsx">
	          			</shiro:hasPermission>               			
	      				<input type="submit" id="submitForm" style="display: none;">
	      			</form>
	      			<form id="editForm"  method="POST"  role="form" class="form-horizontal" onsubmit="return false;">
	      				<input type="hidden" id="terminalName">
						<div class="form-group">
							<label class="col-sm-2 control-label"><B>充值手机号码</B></label>
							<div class="col-sm-7">
								<textarea class="form-control" id="mobilenumbers" name="mobilenumbers" oninput="lockSubmitButton();"
					   			 placeholder="多个号码之间需要分隔符来分隔，具体可以参考以下几种分隔符：逗号，句号，空格，回车，分号" vMin="11" cols=40 rows=6></textarea>
							</div>
							
							<div class="col-sm-2">
								<shiro:hasPermission name="batchFilterPhone:post">
									<input type="button" class="btn btn-success" id="importTxt" name="importTxt" value="TXT导入"/>
								</shiro:hasPermission>
								<shiro:hasPermission name="batchFilterPhoneByExcel:post">
								<input type="button" class="btn btn-success" id="importExcel" name="importExcel" value="Excel导入"/>
								</shiro:hasPermission>
								<input type="button" class="btn btn-warning" id="checkMobileTwo" name="checkMobile" value="检测"/>
							</div>
							<span class="help-inline col-sm-2" id="phonemsg"> </span>
							
						</div>
						
						<div class="form-group">
							<label class="col-sm-2 control-label"><B>充值面值</B></label>
							<div class="col-sm-7">
								<label>
									<input type="radio" name="money" value="10" data-labelauty="10元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="20" data-labelauty="20元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="30" data-labelauty="30元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="50" data-labelauty="50元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="100" data-labelauty="100元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="200" data-labelauty="200元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="300" data-labelauty="300元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="500" data-labelauty="500元" onclick="hideMoney()">
								</label>
								<label>
									<input type="radio" name="money" value="anyAmount" data-labelauty="其他" onclick="showMoney()">
								</label>
							</div>
						</div>
						
						<div class="form-group" id="divAmount" style="display: none;">
							<label class="col-sm-2 control-label"><B>充值金额</B></label>
							<div class="col-sm-7">
								<input class="form-control" type="text" id="anyAmount" name="anyAmount" 
									placeholder="充值范围 0.01~500" maxlength="7" oninput="amountOfProcessing()"   style="width:218px;">
								<span calss="help-block m-b-none" id="span"></span>
							</div>
						</div>
						
						<div id="meg">
						</div>			
							
						<div class="form-group">
							<label class="col-sm-2 control-label"></label>
							<label id="countNumMsg" class="col-sm-4 control-label msg-label-con"></label>
						</div>
						
						
						<div class="form-group">
							<div class="col-sm-12 col-sm-offset-5">
								<shiro:hasPermission name="order/bitchWebSubmit:post"><button type="button" class="btn btn-primary" id="subs" onclick="submitTheSameValueOrder()">
									立即充值<i class="fa fa-arrow-circle-right"></i>
								</button></shiro:hasPermission>
							</div>
						</div>
					</form>
				</div>
			</div>
		<c:import url="../copyright.jsp" />
		<c:import url="../foot.jsp" />
		<script src="${basePath }js/lib/jquery.form.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery-labelauty.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/shCore.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/verify.js"></script>
		<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/billBatchRecharge.js"></script>
</body>
</html>