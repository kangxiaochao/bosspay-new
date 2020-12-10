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

    <title>修改通道号段</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <c:import url="../head.jsp" />

    <link href="${basePath }css/project/laydate.css" rel="stylesheet">
</head>

<body>
<c:import url="../left.jsp" />

<div id="page-wrapper" class="gray-bg dashbard-1">
    <div class="row border-bottom">
        <c:import url="../topnav.jsp" />
    </div>

    <div class="row wrapper border-bottom white-bg page-heading">
        <div class="col-lg-10">
            <br />
            <ol class="breadcrumb">
                <li><a href="${basePath }mainPage">主页</a></li>
                <li><a>系统管理</a></li>
                <li class="active"><strong>通道号段编辑</strong></li>
            </ol>
        </div>
    </div>

    <div class="row white-bg animated fadeInRight">
        <div class="col-md-12">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5><small>通道号段更新</small></h5>
                    <div class="ibox-tools">
                        <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
                        <a class="close-link"><i class="fa fa-times"></i></a>
                    </div>
                </div>

                <div class="ibox-content">
                    <form id="fm" class="form-horizontal">
                        <div class="form-group">
                            <label class="col-md-2 control-label">号段</label>
                            <div class="col-md-8">
                                <input type="tel" id="section" name="section" class="form-control" value="${phonePhysicalchannel.section }">
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-2 control-label">物理通道id</label>
                            <div class="col-md-8">
                                <input type="text" id="dispatcher_provider_id" name="dispatcher_provider_id" class="form-control" value="${phonePhysicalchannel.dispatcher_provider_id }" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-2 control-label">创建时间</label>
                            <div class="col-md-8">
                                <input type="text" id="create_time"  name="create_time" class="form-control" value="${phonePhysicalchannel.create_time }" >
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-4 col-md-offset-2">
                                <button type="button" class="btn btn-primary btn-outline" onclick="javascript:location.href='${basePath }phonePhycicalchannelListPage'">返回</button>
                                <button type="reset" class="btn btn-warning">重置</button>
                                <shiro:hasPermission name="phonePhycicalchannel:put"><button type="button" class="btn btn-primary" onclick="submitEdit();">提交</button></shiro:hasPermission>
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
<script>
    function submitEdit() {
        var id = ${phonePhysicalchannel.id }
        alert(id)
        var myFormActionUrl = basePath + 'phonePhysicalchannel/' + id;
        var data = $("form").serialize();
        console.log(data)
        $.ajax({
            type: 'PUT',
            url: myFormActionUrl,
            data: data,
            success: function (dt) {
                alert(dt)
                location.href = basePath + dt;
            },
            dataType: 'html'
        });
    }
</script>
<script src="${basePath }js/project/mp/PhonePhysicalchannelEdit.js"></script>
</body>
</html>