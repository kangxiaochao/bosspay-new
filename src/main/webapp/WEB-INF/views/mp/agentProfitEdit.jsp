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

<title>代理商利润加款</title>
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
				  <li class="active"><strong>代理商利润加款</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>代理商冲扣值</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" action="${basePath }agentAccountChargeAudit"  class="form-horizontal" method="post">
        <div class="form-group">
            <label class="col-sm-2 control-label">代理商名
                <input type="hidden" name="agentId" id="agentId" value="${agent.id}"  />
            </label>
            <div class="col-sm-10">${agent.name}</div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">当前利润</label>
            <div class="col-sm-10">${agent.agentProfit}</div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">可用余额</label>
            <div class="col-sm-10">${agent.agentBalance}</div>
        </div>
        <div class="hr-line-dashed"></div>
        <div class="form-group"><label class="col-sm-2 control-label">金额</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="fee" id="fee" onkeyup="value=value.replace(/[^\-?\d.]/g,'')" required="required"></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">备注</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="remark" id="remark" value=""></div>
        </div>
     
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
                <button id="submit" onclick="submitForm()" class="btn btn-primary">提交</button> 
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

  <script src="${basePath }js/project/mp/agentProfitEdit.js"></script>

</body>
</html>