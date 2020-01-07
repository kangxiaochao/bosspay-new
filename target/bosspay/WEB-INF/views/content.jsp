<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>

	<%-- <div class="wrapper wrapper-content">
	<div class="text-center">
		<img alt="image" class="" src="${basePath }ui/inspinia/img/logo_hyfd1.png" />
		<font size="6" style="font-weight:600">好亚飞达充值管理平台</font>
	</div>
	    <div class="middle-box text-center animated flipInY"
	         style="margin-top: 0px;">
	        <div class="widget blue-bg p-lg text-center">
	            <div class="m-b-md">
	                <h3 style="color:#FFF">您好，${suName }</h3>
	                <h3  style="margin: 20px auto;color:#FFF">欢迎登陆好亚飞达充值管理平台</h3>
	                <h3  style="margin: 20px auto;color:#FFF">如需操作，请查看左侧菜单栏选择所需功能！</h3>
	                <a href="${basePath }swagger-ui.html" style="color:#FFF">swagger2 [springfox-swagger] </a>
	            </div>
	        </div>
	    </div>
	</div> --%>
<%-- 
//-----------------------------------
 <br/>
  你已经登陆成功,可以为所欲为了
 <br/>
 <shiro:hasPermission name="user:list"><a href="${basePath }sysUserListPage">用户列表</a></shiro:hasPermission>
 <a href="${basePath }sysPermissionListPage">权限列表</a>|
 <a href="${basePath }sysRoleListPage">角色列表</a>|
 <a href="${basePath }sysFunctionListPage">功能列表</a>|
 <a href="${basePath }swagger-ui.html">swagger2 [springfox-swagger] </a>| --%>
 <!-- 
<shiro:hasPermission name="user:list">user:list权限用户显示此内容</shiro:hasPermission><br/>
<shiro:lacksPermission name="user:list">不具有user:list权限用户显示此内容</shiro:lacksPermission><br/>

<shiro:hasPermission name="user:add">user:add权限用户显示此内容</shiro:hasPermission><br/>
<shiro:lacksPermission name="user:add">不具有user:add权限用户显示此内容</shiro:lacksPermission><br/>

<shiro:hasPermission name="user:edit">user:edit权限用户显示此内容</shiro:hasPermission><br/>
<shiro:lacksPermission name="user:edit">不具有user:edit权限用户显示此内容</shiro:lacksPermission><br/>

<shiro:hasPermission name="user:del">user:del权限用户显示此内容</shiro:hasPermission><br/>
<shiro:lacksPermission name="user:del">不具有user:del权限用户显示此内容</shiro:lacksPermission><br/>
 -->
<!--  
  <br/>
//----------------------------------- -->