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

<title>物理通道对应的消息队列通道编辑</title>
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
				  <li class="active"><strong>物理通道对应的消息队列通道编辑</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>物理通道对应的消息队列通道编辑</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" action="${basePath }rabbitmqPhysicalChannel" class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label">物理通道</label>
            <div class="col-sm-9">
            	<input type="hidden" name="id" id="id" value="${rabbitmqPhysicalChannel.id}">
            	<input type="text" class="form-control" name="agentName" id="agentName" value="${rabbitmqPhysicalChannel.name }" readonly required>
            </div>
        </div>
        
        <div class="form-group">
			<label class="col-md-2 control-label">消息队列通道</label>
			<div class="col-md-9">
				<select class="chosen-select"  name="mqqueuename" id="mqqueuename">
					<option value="bill_channel_queue_one">备用通道1</option>
					<option value="bill_channel_queue_two">备用通道2</option>
					<option value="bill_channel_queue_three">备用通道3</option>
					<option value="bill_channel_queue_four">备用通道4</option>
					<option value="bill_channel_queue_five">备用通道5</option>
					<option value="bill_channel_queue_six">备用通道6</option>
					<option value="bill_channel_queue_seven">备用通道7</option>
					<option value="bill_channel_queue_Eight">备用通道8</option>
					<option value="bill_channel_queue_nine">备用通道9</option>
				</select>
				<input type="hidden" id="oldMqqueuename" name="oldMqqueuename" value="${rabbitmqPhysicalChannel.mqQueueName }">
				<input type="hidden" id="mqqueuedisplayname" name="mqqueuedisplayname" value="${rabbitmqPhysicalChannel.mqQueueDisplayName }">
			</div>
		</div>
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-3">
            	<button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }rabbitmqPhysicalChannelListPage'">返回</button>
				<button type="button" class="btn btn-primary" onclick="submitEdit()">更新</button>
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
  <script src="${basePath }js/project/mp/rabbitmqPhysicalChannelEdit.js"></script>

</body>
</html>