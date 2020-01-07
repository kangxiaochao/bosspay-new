<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
    <!-- Mainly scripts -->
    <script src="${basePath }js/lib/jquery.min.js"></script>
    <script src="${basePath }ui/inspinia/js/bootstrap.min.js"></script>
