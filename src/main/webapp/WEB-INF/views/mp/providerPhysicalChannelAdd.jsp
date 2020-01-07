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

<title>运营商物理通道增加</title>
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
				  <li class="active"><strong>运营商物理通道增加</strong></li>
			  </ol>
		  </div>
	  </div>

<div class="row white-bg animated fadeInRight">
<div class="col-md-12">

<div class="ibox float-e-margins">


    <div class="ibox-title">
    <h5><small>运营商物理通道增加</small></h5>
        <div class="ibox-tools">
            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
            <a class="close-link"><i class="fa fa-times"></i></a>
         </div>
    </div>

    <div class="ibox-content">
    <form id="fm" method="post" action="${basePath }providerPhysicalChannel" class="form-horizontal">
        <div class="form-group"><label class="col-sm-2 control-label">运营商物理通道名</label>
            <div class="col-sm-10"><input required type="text" class="form-control" name="name" id="name" required ></div>
        </div>
        <!-- <div class="form-group">
			<label class="col-md-2 control-label">运营商</label>
			<div class="col-md-10">
				<select class="chosen-select"  name="providerId" id="providerId"></select>
			</div>
		</div> -->
        <div class="form-group"><label class="col-sm-2 control-label">链接URL</label>
            <div class="col-sm-10"><input required type="text" class="form-control" name="link_url" id="link_url" ></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">回调URL</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="callback_url" id="callback_url" ></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">队列名</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="queue_name" id="queue_name" ></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">通道标识</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="provider_mark" id="provider_mark" ></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">通道类型</label>
            <div class="col-sm-10">
            	<input type="radio" checked="" value="2" id="channel_type2" name="channel_type" class="i-checks"> 话费
            	<input type="radio" value="3" id="channel_type3" name="channel_type" class="i-checks"> 物联网卡
            </div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">操作类型</label>
            <div class="col-sm-10"><input type="text" class="form-control" name="operator" id="operator" ></div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">参数列表</label>
            <div class="col-sm-10">
            <textarea rows="5" class="form-control" name="parameter_list" id="parameter_list" ></textarea>
            </div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">默认参数列表</label>
            <div class="col-sm-10">
            <textarea required rows="5" class="form-control" name="default_parameter" id="default_parameter" ></textarea>
            </div>
        </div>
        <div class="form-group"><label class="col-sm-2 control-label">优先级</label>
            <div class="col-sm-10"><input type="number" class="form-control" name="priority" id="priority" value="1" required></div>
        </div>
        
        
        <div class="form-group">
            <div class="col-sm-4 col-sm-offset-2">
            	<button type="button" class="btn btn-primary btn-outline" 
            		onclick="javascript:location.href='${basePath }providerPhysicalChannelListPage'">返回</button>
                <shiro:hasPermission name="providerPhysicalChannel:post"><button type="submit" class="btn btn-primary">增加</button></shiro:hasPermission>
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

  <script src="${basePath }js/project/mp/providerPhysicalChannelAdd.js"></script>

</body>
</html>