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

<title>设置代理商特惠通道</title>
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
				  <li class="active"><strong>设置代理商特惠通道</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>设置</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" method="post" action="" class="form-horizontal">
        <div class="form-group" id="agentId" data-id="${agentMap.id }"><label class="col-sm-2 control-label">代理商</label>
             <div class="col-sm-8">
            	<input type="text" class="form-control" id="" value="${agentMap.name }" readonly >
            </div>
        </div>
        <div class="form-group" id="groupId" data-id="${agentMap.bill_group_id }"><label class="col-sm-2 control-label">设置代理商特惠通道</label>
            <div class="col-sm-6">
            	<c:forEach items="${providerList }" var="provider">
            		<div>
            			<h4><input type="checkbox" class="i-checks" id="provider${provider.providerId }" name="provider" 
            						onclick="clickProvider('${provider.providerId }')" value="${provider.providerId }"/>
            				<span style="padding-left:10px;">${provider.name }</span>
            			</h4>
            			<div style="padding-left:20px;" id="physical${provider.providerId }" class="">
            			
            			</div>
            		</div>
            	</c:forEach>
          	</div>
        </div>
        
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
            	<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }agentListPage'">返回</button>
                <button type="button" onclick="submitData()" class="btn btn-primary">确定</button>
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
  <script src="${basePath }js/project/mp/agentChannelRelEdit.js"></script>

</body>
</html>