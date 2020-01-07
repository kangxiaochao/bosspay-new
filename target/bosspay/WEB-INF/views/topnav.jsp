<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
        <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
        <div class="navbar-header">
            <a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="javascript:setJqGridWidthTwo()"><i class="fa fa-bars"></i> </a>
        </div>
         <ul class="nav navbar-top-links navbar-right">
             <li>
                 <span class="m-r-sm text-muted welcome-message">&nbsp;&nbsp;</span>
             </li>
             <shiro:hasPermission name="logout:delete"><li>
                 <a href="javascript:void(0);" onclick="deleteMethod('${basePath }logout');" >
                     <i class="fa fa-sign-out"></i> 退&nbsp;出
                 </a>
             </li></shiro:hasPermission>
         </ul>
        </nav>
        <script type="text/javascript"src="https://cdn.goeasy.io/goeasy.js"></script>
        <script type="text/javascript">
	      window.onload=function(){
	    		var goEasy = new GoEasy({appkey: 'BS-5b50b68bae0d48d883a5e1f59cf130e2'}); 
		    	goEasy.subscribe({
					channel: '系统提示',
					onMessage: function(message){
					 	layer.alert(message.content, {
							skin: 'layui-layer-molv', //样式类名
							title:'系统提示',
							closeBtn: 0
				    	});
					}
		    	});
		    }
		</script>
