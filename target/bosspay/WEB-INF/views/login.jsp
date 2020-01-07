<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
<!DOCTYPE html>
<html>
<style>
ul li{list-style:none;color:#ffa042;font-weight: bold;line-height: 23px;}
#FontScroll{width:520px;height:90px;line-height:30px;overflow:Hidden;margin:0 -25%;}
#FontScroll .line{text-align:center;width:100%;}
</style>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>INSPINIA | Login 2</title>

    <link href="${basePath }ui/inspinia/img/logo_hyfd1.png" rel="icon" />

    <link href="${basePath }ui/inspinia/css/bootstrap.min.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/animate.css" rel="stylesheet">
    <link href="${basePath }ui/inspinia/css/style.css" rel="stylesheet">
    
</head>

<body class="gray-bg" style="background-image: url(${basePath }ui/inspinia/img/2.jpg)">

    <div class="loginColumns animated fadeInDown" id="loginWin">
        <div class="row">

            <div class="col-md-6" style="color: #ffffff">
                <h2 class="font-bold">浩百话费管理充值平台&nbsp;&nbsp;&nbsp;欢迎您</h2>

				<p><br></p>
                <p>
                   	 济南浩百信息技术有限公司主要经营范围涵盖话费充值、后向统付流量营销，是国内领先的综合缴费服务提供商。
                </p>

                <p>
                  	基于虚拟运营商快速发展的趋势，济南浩百信息技术有限公司聚焦于整合各虚拟运营商话费资源，形成了以虚拟运营商资源为主，三大运营商为基础的特殊产品，专注为第三方缴费平台、服务商家、营业厅网点等提供优质的缴费服务。
                </p>

                <p>
                    <small>截止目前，已签约的虚拟运营商包括迪信通、远特、蜗牛、国美等，能够实现缴费的虚拟运营商达32家，基本实现了虚拟运营商缴费全覆盖。</small>
                </p>
                <div id="FontScroll">
                	<ul>
                	</ul>
				</div>
            </div>
            <div class="col-md-6">
                <div class="ibox-content">
                    <form class="m-t" role="form" id="loginForm" method="POST" action="${basePath }login">
                   
                        <div class="form-group" id="backMsg" style="color: red;">
                        <c:if test="${!empty backMsg}"> <c:out value="${backMsg}"/>  </c:if>
                        </div>
                        <div class="form-group">
                            <input type="text" class="form-control" id="suName" name="suName" placeholder="请输入您的用户名！" required>
                        </div>
                        <div class="form-group">
                            <input type="password" class="form-control" id="suPass" name="suPass" placeholder="请输入您的密码！" required>
                        </div>
                        <div class="form-group">
                            <input type="text" style="width:215px;" class="form-control" id="verification" name="verification" onblur="auths()" placeholder="请输入您的验证码！" required>
                            <div id="authCode" style="width: 100px;height: 30px;margin-left: 225px;margin-top: -32px; font-size: 20px;"></div>
                        </div>
  							<%-- <input type="text" id="captcha" name="captcha" class="text captcha" maxlength="6" autocomplete="off" placeholder="请输入验证码！" required/>
  							<img id="captchaImage" src="captcha" onclick="change()" title="点击更换验证码"/>
                        	<input type="hidden" id="randomString" name="randomString"  value="${randomString}"/> --%>
                        <button type="button" class="btn btn-primary block full-width m-b" onclick="toLogin()">登录</button>

                        <a href="#">
                            <small>忘记密码?</small>
                        </a>

                    </form>
                    <p class="m-t">
                        <small>Copyright&nbsp;&nbsp;©&nbsp;2012-2018&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;济南浩百信息  版权所有</small>
                    </p>
                </div>
            </div>
        </div>
        <hr/>
        <div class="row">
            <div class="col-md-6">
                Copyright 济南浩百信息技术有限公司
            </div>
            <div class="col-md-6 text-right">
               <small>© 2012-2018</small>
            </div>
        </div>
    </div>
   	<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery.min.js"></script>
  	<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/md5.js"></script>
 	<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery-easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/gVerify.js" ></script>
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/login.js" ></script>
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/fontscroll.js" ></script>
	<script type="text/javascript">
		$(function(){
			$('#FontScroll').FontScroll({time: 3000,num: 0});
		});
	</script>
</body>
  
</html>

