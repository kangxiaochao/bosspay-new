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

<title>用户详情</title>
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
				  <li><a>系统</a></li>
				  <li class="active"><strong>用户详情</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>用户详情</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
        <div class="form-group"><label class="col-sm-2 control-label">用户编号</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suId}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">用户名</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suName}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">密码</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suPass == null || sysUser.suPass == ''?'&nbsp;':sysUser.suPass}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">授权</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suCredits == null || sysUser.suPass == ''?'&nbsp;':sysUser.suCredits}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">手机号</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suMobile == null || sysUser.suMobile == '' ?'&nbsp;':sysUser.suMobile}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">别名</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suNick == null || sysUser.suPass == ''?'&nbsp;':sysUser.suNick}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">QQ</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suQq == null || sysUser.suPass == ''?'&nbsp;':sysUser.suQq}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">邮箱</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suEmail == null || sysUser.suPass == ''?'&nbsp;':sysUser.suEmail}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">注册时间</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suRegTime == null || sysUser.suPass == ''?'&nbsp;':sysUser.suRegTime}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">最后访问IP</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suLastIp == null || sysUser.suPass == ''?'&nbsp;':sysUser.suLastIp}</p></label>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">最后访问时间</label>
            <label class="col-sm-10 control-label"><p>${sysUser.suLastVisit == null || sysUser.suPass == ''?'&nbsp;':sysUser.suLastVisit}</p></label>
        </div>
        
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
				<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }sysUserListPage'">返回</button>
            </div>
        </div>
    </div><!-- end div ibox content -->

</div><!-- end div ibox -->

</div><!-- end div col 12 -->
</div><!-- end div row -->

  <c:import url="../copyright.jsp" />

  </div><!-- end page wrapper -->

  <c:import url="../foot.jsp" />

  <script src="${basePath }js/project/sysUserAdd.js"></script>

</body>
</html>