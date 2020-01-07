<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
        <div class="row" style="width:100%;position:fixed;bottom:0px;">
            <div class="col-lg-12">
                <div class="wrapper wrapper-content">
					<input type="hidden" id="backMsg" value="${backMsg }">
					<c:remove var="backMsg" scope="session" />
                </div>
                <div class="footer">
                    <div class="pull-right">
                        10GB of <strong>250GB</strong> Free.
                    </div>
                    <div id="copyRight">
                        <strong>Copyright</strong> 话费充值管理平台 &copy; 2018
                    </div>
                </div>
            </div>
        </div>
