<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
    <!-- Mainly scripts -->
    <script src="${basePath }js/lib/jquery.min.js"></script>
    
    <script type="text/javascript" charset="UTF-8" src="${basePath }js/project/head.js"></script>
    
    <script src="${basePath }ui/inspinia/js/bootstrap.min.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
    <!-- Flot -->
    <script src="${basePath }ui/inspinia/js/plugins/flot/jquery.flot.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/flot/jquery.flot.tooltip.min.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/flot/jquery.flot.resize.js"></script>
    <!-- Custom and plugin javascript -->
    <script src="${basePath }ui/inspinia/js/inspinia.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/pace/pace.min.js"></script>
    <!-- jQuery UI -->
    <script src="${basePath }ui/inspinia/js/plugins/jquery-ui/jquery-ui.min.js"></script>
    <!-- jqGrid -->
    <script src="${basePath }ui/inspinia/js/plugins/jqGrid/i18n/grid.locale-cn.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/jqGrid/jquery.jqGrid.min.js"></script>
    <script src="${basePath }ui/inspinia/js/plugins/jqGrid/grid.setcolumns.js"></script>
    
    <!-- GITTER -->
    <script src="${basePath }ui/inspinia/js/plugins/gritter/jquery.gritter.min.js"></script>
    <!-- Toastr -->
    <script src="${basePath }ui/inspinia/js/plugins/toastr/toastr.min.js"></script>
    <!-- Input Mask 格式化文本框输入格式-->
    <script src="${basePath }ui/inspinia/js/plugins/jasny/jasny-bootstrap.min.js"></script>
    <!-- laydate日期控件 -->
	<script src="${basePath }js/lib/laydate/laydate.js"></script>
	<!-- touchspin -->
    <script src="${basePath }ui/inspinia/js/plugins/touchspin/jquery.bootstrap-touchspin.min.js"></script>
	<!-- bootstrap-switch -->
	<script src="${basePath }js/lib/bootstrap-switch.min.js"></script>
    <!-- iCheck -->
    <script src="${basePath }ui/inspinia/js/plugins/iCheck/icheck.min.js"></script>
     <script src="${basePath }ui/inspinia/js/jquery.form.js"></script>
	<script src="${basePath }ui/inspinia/js/plugins/sweetalert/sweetalert.min.js"></script>
	
	<!-- Chosen -->
    <script src="${basePath }ui/inspinia/js/plugins/chosen/chosen.jquery.js"></script>
    
    <!-- layer 弹框 -->
    <script src="${basePath }js/lib/layer/layer.js"></script>
    <script src="${basePath }js/lib/layui.js"></script>
    <!-- zTree -->
    <script src="${basePath}js/lib/zTree_v3/js/jquery.ztree.core.js"></script>
	<script src="${basePath}js/lib/zTree_v3/js/jquery.ztree.excheck.js"></script>
    