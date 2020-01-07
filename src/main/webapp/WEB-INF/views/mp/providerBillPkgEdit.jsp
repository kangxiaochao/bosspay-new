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

<title>配置话费包</title>
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

      <div class="row wrapper border-bottom white-bg page-heading">
          <div class="col-lg-10"><br/>
		      <ol class="breadcrumb">
			      <li><a href="${basePath }mainPage">主页</a></li>
				  <li><a>运营商</a></li>
				  <li class="active"><strong>配置话费包</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>设置运营商话费包-<label style="color: red">${provider.name}</label></small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>
	<input id="providerId" name="providerId" value="${provider.id}" type="hidden" />
    <input id="providerMeg" name="providerMeg" value="${providerMeg}" type="hidden" />
    <div class="ibox-content">
    <form id="fm" method="post" action="${basePath }providerBillPkgEdit/${provider.id}"  class="form-horizontal">
    	<input id ="provinceMeg" name="provinceMeg" type="hidden" value="">
    	<!-- <input id ="billPkgIdes" name="billPkgIdes" type="hidden" value=""/> -->
        <div class="form-group">
        	<label class="col-sm-2 control-label">运营商</label>
            <div class="col-sm-9">
            	<input type="text" class="form-control" value="${provider.name}" readonly >
            </div>
        </div>
        <div class="hr-line-dashed"></div>
        <div class="form-group">
			<label class="col-sm-2 control-label">省份选择</label>
			<div class="col-sm-9">
				<ul id="treeDemo" class="ztree" style="background-color: white;"></ul>
				<label style="color: red">友情提示:点击复选框可以进行全部层级操作,点击文字则只进行当前层级操作</label>
			</div>
        </div>
        <div class="form-group">
        	<label class="col-sm-2 control-label">话费包</label>
            <div class="col-sm-9" id="billPkg"></div>
        </div>
        <div class="form-group">
            <div class="col-sm-6 col-sm-offset-4">
            	<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }providerBillPkgList/${provider.id}'">返回</button>
               	<button type="submit" class="btn btn-primary" onclick="return saveProvince()">保存</button>
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
  <script src="${basePath }js/project/mp/providerBillPkgEdit.js"></script>

</body>
</html>