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

<title>话费管理充值平台</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="head.jsp" />
</head>

<body>

  <c:import url="left.jsp" />

  <div id="page-wrapper" class="white-bg gray-bg dashbard-1">
      <div class="row border-bottom">
  <c:import url="topnav.jsp" />
      </div>

      <%-- <div class="row wrapper border-bottom white-bg page-heading">
          <div class="col-lg-10"><br/>
		      <ol class="breadcrumb">
			      <li><a href="${basePath }mainPage">主页</a></li>
			  </ol>
		  </div>
	  </div> --%>

<div class="row animated fadeInRight">
<div class="col-md-12">
<c:import url="content.jsp" />
<div class="ibox float-e-margins">
	<!--  利润   --- -->
	<shiro:hasPermission name="countProfit:get">
	<div class="col-sm-4">
        <div class="row m-t-xs">
            <div class="col-xs-6">
                <h5 class="m-b-xs">当天充值金额</h5>
                <h1 id="todayAmount" class="no-margins"></h1>
            </div>
            <div class="col-xs-6">
                <h5 class="m-b-xs">总充值金额</h5>
                <h1 id="amount" class="no-margins"></h1>
            </div>
        </div>
        <div class="row m-t-xs">
        	 <div class="col-xs-6">
                <h5 class="m-b-xs">当天利润</h5>
                <h1 id="todayProfit" class="no-margins"></h1>
            </div>
            <div class="col-xs-6">
                <h5 class="m-b-xs">总利润</h5>
                <h1 id="profit" class="no-margins"></h1>
            </div>
        </div>
        <table class="table small m-t-sm">
            <tbody>
            <tr>
                <td>
                    <strong id="todayRate"></strong> &nbsp;成功率
                </td>
                <td>
                    <strong id="rate"></strong> &nbsp;成功率
                </td>
            </tr>
            <tr>
                <td>
                    <strong id="todaySumNum"></strong> &nbsp;订单

                </td>
                <td>
                    <strong id="sumNum"></strong> &nbsp;订单
                </td>
            </tr>
            <tr>
                <td>
                    <strong id="todayFailNum"></strong> &nbsp;<font color = "red">失败</font>
                </td>
                <td>
                    <strong id="failNum"></strong> &nbsp;<font color = "red">失败</font>
                </td>
            </tr>
            <tr>
                <td>
                    <strong id="todaySuccNum"></strong> &nbsp;<font color = "green">成功</font>
                </td>
                <td>
                    <strong id="succNum"></strong> &nbsp;<font color = "green">成功</font>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    </shiro:hasPermission>
<%--     <shiro:hasPermission name="getChartsData:get"> --%>
    <div id="charts" style="width:60%;height:300px;float:left"></div>
<%--     </shiro:hasPermission> --%>
       <shiro:hasPermission name="countPhysicalChannelProfit:get">
	       <div class="ibox-content">
		       <form id="dateForm">
					<div class="col-md-2">
						<div class="form-group">
							<input type="text" class="laydate-icon form-control"
								placeholder="格式:YYYY-MM-DD hh:mm:ss" name="startDate"
								id="startDate" required data-mask="9999-99-99 99:99:99">
						</div>
					</div>
					<div class="col-md-2">
						<div class="form-group">
							<input type="text" class="laydate-icon form-control"
								placeholder="格式:YYYY-MM-DD hh:mm:ss" name="endDate"
								id="endDate" required data-mask="9999-99-99 99:99:99">
						</div>
					</div>
					<button type="button" class="btn btn-primary" onclick="countPhysicalChannelProfit()">查询</button>
				</form>
				<table class="table table-striped">
	                <thead>
	                <tr>
	                    <th>通道编号</th>
	                    <th>通道名</th>
	                    <th>占比</th>
	                    <th>总订单数</th>
	                    <th>成功订单数</th>
	                    <th>充值金额</th>
	                    <th>成功率</th>
	                    <th>利润</th>
	                </tr>
	                </thead>
	                <tbody id="channelProfit">
	                </tbody>
	            </table>
	        </div>
        </shiro:hasPermission>
<!--     <table id = "physicalChannelProfit"> -->
<!--     	<tr> -->
<!--     		<th>通道名</th> -->
<!--     		<th></th> -->
<!--     	</tr> -->
<!--     </table> -->
<shiro:hasPermission name="getAgentAccount:get">
<div class="row m-t-xs">
            <div class="col-xs-12 text-center">
                <h1 class="m-b-xs">账户余额</h1>
                <h1 id="todayAmount" class="no-margins">￥: &nbsp;<span id="agentAccount" style='color:red'></span> &nbsp;元</h1>
            </div>
  </div>
</shiro:hasPermission>
<shiro:hasPermission name="queryAgentDiscountBySuId:get">
    <div class="row white-bg animated fadeInRight" style="margin-bottom:0px;">
		<div class="col-md-12">
		
			<div class="ibox float-e-margins">
			
			
			    <div class="ibox-title">
			    <h3>话费充值折扣信息</h3>
			        <div class="ibox-tools">
			            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
			            <a class="close-link"><i class="fa fa-times"></i></a>
			         </div>
			    </div>
			
			    <div class="ibox-content">
			    	<div id="mytbc1"></div>
			    </div>
			
			</div>
		
		</div>
	</div>
	</shiro:hasPermission>
	<shiro:hasPermission name="queryAgentBillDiscountBySuId:get">  
	<div class="row white-bg animated fadeInRight">
		<div class="col-md-12">
		
			<div class="ibox float-e-margins">
			    <div class="ibox-title">
			    <h3>话费充值折扣信息</h3>
			        <div class="ibox-tools">
			            <a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
			            <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
			            <a class="close-link"><i class="fa fa-times"></i></a>
			         </div>
			    </div>
			
			    <div class="ibox-content">
			    	<div id="mytbc2"></div>
			    </div>
			
			</div>
		
		</div>
	</div>
	</shiro:hasPermission>	 
	</div><!-- end div ibox -->
	
	</div><!-- end div col 12 -->
	</div><!-- end div row -->

  <c:import url="copyright.jsp" />

  </div><!-- end page wrapper -->

  <c:import url="foot.jsp" />
<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/main.js"></script>
<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/echarts-all.js"></script>
<script type="text/javascript" charset="UTF-8" src="${basePath }js/lib/jquery.peity.min.js"></script>
</body>
</html>