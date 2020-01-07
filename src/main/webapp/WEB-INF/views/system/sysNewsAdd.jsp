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

<title>新闻添加</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link href="${basePath }js/lib/umeditor1.2.3/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
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
				  <li><a>系统管理</a></li>
				  <li class="active"><strong>新闻添加</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>新闻添加</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" method="post" action="${basePath }sysNews" class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label">标题</label>
            <div class="col-sm-9"><input type="text" class="form-control" name="title" id="title" required></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">副标题</label>
            <div class="col-sm-9"><input type="text" class="form-control" name="subtitle" id="subtitle"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">新闻内容</label>
            <div class="col-sm-9">
            	<input type="hidden" class="form-control" name="content" id="content" >
            	<script type="text/plain" id="myEditor" style="width:100%;min-width:400px;;height:140px;">
					
				</script>
            </div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">显示顺序</label>
            <div class="col-sm-9"><input type="number" class="form-control" name="order" id="order" value="99" required step="1"></div>
        </div>
        
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
            	<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }sysNewsListPage'">返回</button>
				<button type="reset" class="btn btn-warning" onclick="resetCotent()">重置</button>
				<button type="submit" class="btn btn-primary" onclick="setContent()">增加</button>
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
  <script type="text/javascript" src="${basePath }js/lib/umeditor1.2.3/third-party/template.min.js"></script>
  <script type="text/javascript" src="${basePath }js/lib/umeditor1.2.3/umeditor.config.js" charset="utf-8" ></script>
  <script type="text/javascript" src="${basePath }js/lib/umeditor1.2.3/umeditor.min.js" charset="utf-8" ></script>
  <script type="text/javascript" src="${basePath }js/lib/umeditor1.2.3/lang/zh-cn/zh-cn.js"></script>

  <script src="${basePath }js/project/sysNewsAdd.js"></script>

</body>
</html>