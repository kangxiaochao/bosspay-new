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

    <title>异常订单查询列表</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <c:import url="../head.jsp"/>
    <link href="${basePath }css/project/laydate.css" rel="stylesheet">
</head>

<body>
    <div id="wrapper">
        <c:import url="../left.jsp"/>
        <div id="page-wrapper" class="gray-bg dashbard-1">
            <div class="row border-bottom" id="topNavDiv">
                <c:import url="../topnav.jsp"/>
            </div>

            <div class="row wrapper border-bottom white-bg page-heading" id="breadcrumbsDiv">
                <div class="col-lg-10">
                    <h3>订单查询列表</h3>
                    <ol class="breadcrumb">
                        <li><a href="${basePath }mainPage">主页</a></li>
                        <li><a>资源配置</a></li>
                        <li class="active"><strong>异常订单查询列表</strong></li>
                    </ol>
                </div>
            </div>

            <div class="wrapper wrapper-content animated fadeInRight ecommerce">
                <!-- 查询模块 -->
                <div class="ibox-content m-b-sm border-bottom" id="queryDiv">
                    <form  id="searchForm">
                        <div class="row">
                            <div class="col-md-2">
                                <div class="form-group">
                                    <label class="control-label" for="">平台订单号</label>
                                    <input type="text" id="orderId" name="orderId" value="" placeholder="请输入平台订单号" class="form-control"
                                           maxlength="50">
                                </div>
                            </div>
                            <div class="col-md-2" >
                                <div class="form-group">
                                    <label class="control-label" for="">代理商订单号</label>
                                    <input type="text" id="agentOrderId" name="agentOrderId" value="" placeholder="请输入代理商订单号" class="form-control"
                                           maxlength="50">
                                </div>
                            </div>
                            <div class="col-md-2" >
                                <div class="form-group">
                                    <label class="control-label" for="">上家订单号</label>
                                    <input type="text" id="providerOrderId" name="providerOrderId" value="" placeholder="请输入上家订单号" class="form-control"
                                           maxlength="50">
                                </div>
                            </div>
                            <div class="col-md-2" >
                                <div class="form-group">
                                    <label class="control-label">分销商</label>
                                    <select data-placeholder="请选择..." class="chosen-select" name="dispatcherProviderId" id="dispatcherProviderId">
                                        <option value="" selected="selected"></option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-12">
                                <div class="text-center">
                                    <shiro:hasPermission name="queryExceptionOrderStatus:get"><button type="reset" class="btn btn-warning">重置</button></shiro:hasPermission>
                                    <shiro:hasPermission name="queryExceptionOrderStatus:get"><button type="button" class="btn btn-primary" onclick="search()">查询</button></shiro:hasPermission>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <!-- 列表模块 -->
                <div class="ibox-content m-b-sm border-bottom">
                    <div class="row">
                        <div class="col-md-12">
                            <div id="mytbc1"></div>
                        </div>
                    </div>
                </div>
            </div>
            <c:import url="../copyright.jsp"/>
        </div>
    </div>
    <c:import url="../foot.jsp"/>
    <script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/queryExceptionOrderStatus.js"></script>
</body>
</html>
