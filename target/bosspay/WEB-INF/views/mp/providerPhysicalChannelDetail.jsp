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

<title>运营商物理通道详情</title>
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
				  <li class="active"><strong>运营商物理通道详情</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>运营商物理通道详情</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" action="${basePath }providerPhysicalChannel/${providerPhysicalChannel.id}"  class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label">运营商物理通道名
        </label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.name}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">运营商</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.providerName}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">链接URL</label>
        	<div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.link_url}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">回调URL</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.callback_url}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">队列名</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.queue_name}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">通道标识</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.provider_mark}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">通道类型</label>
           	<div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.channel_type=='2'?'话费':'物联网卡'}"></div>
            </label>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">操作类型</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.operator}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">参数列表</label>
            <div class="col-sm-9">
            	<textarea disabled rows="5" class="form-control">${providerPhysicalChannel.parameter_list}</textarea>
            </div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">默认参数列表</label>
            <div class="col-sm-9">
            	<textarea disabled rows="5" class="form-control">${providerPhysicalChannel.default_parameter}</textarea>
            </div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">优先级</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.priority}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">创建时间</label>
            <div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.create_date}"></div>
        </div>
        
        <div class="form-group"><label class="col-sm-2 control-label">更新时间</label>
        	<div class="col-sm-9"><input disabled type="text" class="form-control" value="${providerPhysicalChannel.update_date}"></div>
        </div>
        
        <div class="form-group">
            <div class="col-sm-6 col-sm-offset-5">
                <button type="button" class="btn btn-primary btn-outline" 
            		onclick="javascript:location.href='${basePath }providerPhysicalChannelListPage'">返回</button>
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

</body>
</html>