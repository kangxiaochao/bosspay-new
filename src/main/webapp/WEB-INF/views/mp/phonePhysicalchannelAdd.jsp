<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    request.setAttribute("basePath", basePath);
    out.clear();
%>
<!DOCTYPE HTML>
<html>
<head>
    <base href="${basePath }">

    <title>增加通道号段</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <c:import url="../head.jsp"/>

    <link href="${basePath }css/project/laydate.css" rel="stylesheet">
</head>

<body>
<c:import url="../left.jsp"/>

<div id="page-wrapper" class="gray-bg dashbard-1">
    <div class="row border-bottom">
        <c:import url="../topnav.jsp"/>
    </div>

    <div class="row wrapper border-bottom white-bg page-heading">
        <div class="col-lg-10">
            <br/>
            <ol class="breadcrumb">
                <li><a href="${basePath }mainPage">主页</a></li>
                <li><a>系统管理</a></li>
                <li class="active"><strong>增加通道号段</strong></li>
            </ol>
        </div>
    </div>

    <div class="row white-bg animated fadeInRight">
        <div class="col-md-12">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>
                        <small>增加通道号段</small>
                    </h5>
                    <div class="ibox-tools">
                        <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
                        <a class="close-link"><i class="fa fa-times"></i></a>
                    </div>
                </div>

                <div class="ibox-content">
                    <form id="fm" method="post" action="${basePath }phonePhysicalchannel" class="form-horizontal">
                        <div class="form-group">
                            <label class="col-md-2 control-label">号段</label>
                            <div class="col-md-8">
                                <input type="tel" class="form-control" name="section" id="section" data-mask="9999999"
                                       onchange="checkPhone(this.value)" required>
                            </div>
                            <span class="help-block m-b-none">
									<span style="color:red" id="phoneMsg">长度必须7位</span>
                            </span>
                        </div>

                        <div class="form-group">
                            <label class="col-md-2 control-label">物理通道</label>
                            <div class="col-md-8">
                                <select class="chosen-select"  name="dispatcher_provider_id" id="dispatcher_provider_id"></select>
                            </div>
                        </div>

                        <div class="form-group" hidden="hidden">
                            <label class="col-md-2 control-label">创建时间</label>
                            <div class="col-md-8">
                                <input type="tel" class="form-control" name="create_time" id="create_time">
                            </div>
                        </div>


                        <%--<div class="form-group">--%>
                        <%--<label class="col-md-2 control-label">运营商</label>--%>
                        <%--<div class="col-md-8">--%>
                        <%--<select required  data-placeholder="请选择..." class="chosen-select" tabindex="2" name="providerId" id="providerId"></select>--%>
                        <%--</div>--%>
                        <%--</div>--%>

                        <%--<!-- <div class="form-group">--%>
                        <%--<label class="col-md-2 control-label">省 份</label>--%>
                        <%--<div class="col-md-8">--%>
                        <%--<input type="text" class="form-control" name="provinceCode" id="provinceCode" maxlength="25">--%>
                        <%--</div>--%>
                        <%--</div> -->--%>

                        <%--<div class="form-group">--%>
                        <%--<label class="col-md-2 control-label">省 份</label>--%>
                        <%--<div class="col-md-8">--%>
                        <%--<select required data-placeholder="请选择..." class="chosen-select" name="provinceCode" id="provinceCode" onchange="initCity(this.value,'cityCode');initCityCodeStyle();"></select>--%>
                        <%--</div>--%>
                        <%--</div>--%>

                        <%--<div class="form-group">--%>
                        <%--<label class="col-md-2 control-label">城 市</label>--%>
                        <%--<div class="col-md-8">--%>
                        <%--<select data-placeholder="请选择..." class="chosen-select" name="cityCode" id="cityCode"></select>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="form-group"><label class="col-sm-2 control-label">号段类型</label>--%>
                        <%--<div class="col-sm-10">--%>
                        <%--<input type="radio" checked="" value="1" name="carrierType" class="i-checks"> 普卡　--%>
                        <%--<input type="radio" value="7" name="carrierType" class="i-checks"> 物联网卡--%>
                        <%--</div>--%>
                        <%--</div>--%>

                        <div class="form-group">
                            <div class="col-md-4 col-md-offset-2">
                                <button type="button" class="btn btn-primary btn-outline"
                                        onclick="javascript:location.href='${basePath }phonePhycicalchannelListPage'">
                                    返回
                                </button>
                                <button type="reset" class="btn btn-warning">重置</button>
                                <shiro:hasPermission name="phonePhysicalchannel:post">
                                    <button type="submit" id="submit" class="btn btn-primary">增加</button>
                                </shiro:hasPermission>
                            </div>
                        </div>
                    </form>
                </div><!-- end div ibox content -->
            </div><!-- end div ibox -->
        </div><!-- end div col 12 -->
    </div><!-- end div row -->

    <c:import url="../copyright.jsp"/>
</div><!-- end page wrapper -->
<c:import url="../foot.jsp"/>
<script src="${basePath }js/lib/city.js"></script>
<script src="${basePath }js/project/mp/phonePhysicalchannelAdd.js"></script>
</body>
</html>