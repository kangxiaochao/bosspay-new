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

<title>index</title>
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
				  <li><a>资源配置</a></li>
				  <li class="active"><strong>天猫产品话费配置</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>天猫产品话费</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" action="${basePath }tmallProductEdit/${tmallProduct.ids}"  class="form-horizontal">
    <input type="hidden" name="ids" id="ids" value="${tmallProduct.ids}"  />
        <div class="form-group"><label class="col-sm-2 control-label">天猫产品ID</label>
            <div class="col-sm-10">
            	<input type="text" value="${tmallProduct.spuid }" class="form-control" placeholder="请输入天猫提供的产品ID" name="spuid" id="spuid" placeholder="请输入产品ID" required >
            </div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">省份</label>
            <div class="col-sm-10"><input type="text" value="${tmallProduct.province }" class="form-control" placeholder="请输入话费包所适用的省份" name="province" id="province" required></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">话费包大小</label>
            <div class="col-sm-10"><input type="text" value="${tmallProduct.pkgs }" class="form-control" placeholder="单位M" name="pkgs" id="pkgs" required></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">基础售价</label>
            <div class="col-sm-10"><input type="text" value="${tmallProduct.price }" class="form-control" placeholder="单位元" name="price" id="price" required></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">运营商</label>
            <div class="col-sm-10">
            <input type="text" class="form-control" value="${tmallProduct.carr }" placeholder="请输入移动、联通或电信" name="carr" id="carr" required>
            	<%-- <select id='carr' name='carr' class="form-control"  value="${tmallProduct.carr }" required>
            		<option value="">请选择</option>
            		<option value="移动">移动</option>
            		<option value="联通">联通</option>
            		<option value="电信">电信</option>
            	</select> --%>
            </div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">话费类型</label>
            <div class="col-sm-10">	
            <input type="text" class="form-control" value="${tmallProduct.flowtype }" placeholder="请输入nation(全国)或province(省份)" name="flowtype" id="flowtype" required>												
				<%-- <select id='flowtype' name='flowtype' class="form-control" value="${tmallProduct.flowtype }" required>
					<option value="">请选择</option>
            		<option value="nation">nation(全国)</option>
            		<option value="province">province(省份)</option>
            	</select> --%>
			</div>
        </div>
        
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
            	<button type="button" class="btn btn-primary btn-outline" 
            		onclick="javascript:location.href='${basePath }providerListPage'">返回</button>
                <button type="button" class="btn btn-primary" onclick="submitEdit();">提交</button>
				<button type="reset" class="btn btn-white">重写</button>
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

  <script src="${basePath }js/project/mp/tmallProductEdit.js"></script>

</body>
</html>