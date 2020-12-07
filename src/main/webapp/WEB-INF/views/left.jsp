<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("basePath", basePath);
out.clear();
%>
<nav class="navbar-default navbar-static-side" role="navigation">
<div class="sidebar-collapse" id = "leftMenuDiv" data-suId='${suId }'>
    <ul class="nav metismenu" id="side-menu">
        <li class="nav-header">
        <div class="dropdown profile-element"> 
            <span><img alt="image" class="img-circle" src="${basePath }ui/inspinia/img/profile_small.jpg" /></span>
            <a data-toggle="dropdown" class="dropdown-toggle" href="javascript:void(0)">
            <span class="clear"> <span class="block m-t-xs"> <strong class="font-bold">${suName }</strong> </span> 
            <!-- <span class="text-muted text-xs block">客户余额</span> -->  </span></a>
            <a data-toggle="dropdown" class="dropdown-toggle" onclick="updatePassword('${suId }')">
            <span class="clear"> <span class="block m-t-xs"> <strong class="font-bold">修改密码</strong> </span> 
            <!-- <span class="text-muted text-xs block">客户余额</span> -->  </span></a>
        </div>
        <div class="logo-element">${suName }  </div>
        </li>
        <li id="p0">
        	<a href="index.html"><i class="fa fa-diamond"></i> <span class="nav-label">总览</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p0u">
		        <li id="p01"><a href="${basePath }mainPage">主页</a></li>
	        </ul>
        </li>
        <shiro:hasPermission name="sysPage:get">
        <li>
        	<a href="index.html"><i class="fa fa-desktop"></i> <span class="nav-label">系统管理</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p1u">
		        <shiro:hasPermission name="sysUserListPage:get"> <li id="p11"><a href="${basePath }sysUserListPage">用户</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="sysPermissionListPage:get"> <li id="p12"><a href="${basePath }sysPermissionListPage">权限</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="sysRoleListPage:get"> <li id="p13"><a href="${basePath }sysRoleListPage">角色</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="sysFunctionListPage:get"> <li id="p14"><a href="${basePath }sysFunctionListPage">功能</a></li></shiro:hasPermission>
<!-- 	        <li id="p15"> <a href="${basePath }swagger-ui.html">springfox-swagger</a></li> -->
				<li id="p16"><a href="${basePath }sysNoticeListPage">公告</a></li>
				<li id="p17"><a href="${basePath }sysNewsListPage">新闻</a></li>
	        </ul>
        </li>
        </shiro:hasPermission>
        <shiro:hasPermission name="ziYuanPage:get">
        <li id="p2">
        	<a href="index.html"><i class="fa fa-database"></i> <span class="nav-label">资源配置</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p2u">
		        <shiro:hasPermission name="billPkgListPage:get"> <li id="p22"><a href="${basePath }billPkgListPage">话费包</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="providerBillGroupListPage:get"> <li id="p24"><a href="${basePath }providerBillGroupListPage">话费通道组</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="phoneSectionListPage:get"> <li id="p25"><a href="${basePath }phoneSectionListPage">号段管理</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="phonePhycicalchannelListPage:get"> <li id="p25"><a href="${basePath }phonePhycicalchannelListPage">通道号段管理</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="providerProductListPage:get"><li id="p26"><a href="${basePath }providerProductList">上家产品管理</a></li></shiro:hasPermission>
		        <shiro:hasPermission name="batchOfCharger:get"><li id="p27"><a href="${basePath }BatchOfCharger">批量充值</a></li></shiro:hasPermission>
		        <li id="p28"><a href="${basePath }cardListPage">卡密管理</a></li>
<%-- 		        <shiro:hasPermission name="providerDataRechargeGroupList:get"> <li id="p26"><a href="${basePath }providerDataRechargeGroupList">复充话费通道组</a></li></shiro:hasPermission>
 --%>	        
 				<shiro:hasPermission name="ipListPage:get"><li id="p29"><a href="${basePath }ipListPage">IP白名单管理</a></li></shiro:hasPermission>
 				<shiro:hasPermission name="rabbitmqPhysicalChannelListPage:get"><li id="p210"><a href="${basePath }rabbitmqPhysicalChannelListPage">消息队列通道管理</a></li></shiro:hasPermission>
 			</ul>
        </li>
        </shiro:hasPermission>
        
        <shiro:hasPermission name="orderPage:get">
        <li id="p3">
        	<a href="index.html"><i class="fa fa-table"></i> <span class="nav-label">订单</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p3u">
	        <shiro:hasPermission name="orderListPage:get"><li id="p31"> <a href="${basePath }orderListPage" target="_blank">订单列表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="exceptionOrderListPage:get"><li id="p32"> <a href="${basePath }exceptionOrderListPage" target="_blank">异常订单列表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="submitOrderPage:get"><li id="p33"> <a href="${basePath }submitOrderPage" target="_blank">订单提交记录列表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="orderPathRecordPage:get"><li id="p34"> <a href="${basePath }orderPathRecordPage" target="_blank">订单流水</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="tutubiOrderPage:get"><li id="p35"> <a href="${basePath }tutubiOrderList" target="_blank">兔兔币订单列表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="tutubiOrderList:get"><li id="p36"> <a href="${basePath }BatchOfChargerList" target="_blank">批量充值订单列表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="queryOrderInfoPage:get"><li id="p37"> <a href="${basePath }queryOrderInfoPage" target="_blank">	综合查询</a></li></shiro:hasPermission>
	        <%-- <shiro:hasPermission name="OrderPushPage:get"><li id="p35"> <a href="${basePath }OrderPushPage">订单推送</a></li></shiro:hasPermission> --%>
	        </ul>
        </li>
        </shiro:hasPermission>
        
        <shiro:hasPermission name="chargePage:get">
        <li id="p4" >
        	<a href="index.html"><i class="fa fa-shopping-cart"></i> 
        		<span class="nav-label">充值</span> <span class="fa arrow"></span>
        	</a>
	        <ul class="nav nav-second-level" id="p4u">
		       	<shiro:hasPermission name="billChargePage:get"> 	
		      	<li id="p41v">
		        	<a href="index.html"> 
			        	<span class="nav-label">话费充值</span> 
			        	<span class="fa arrow"></span>
			        </a>
		        	<ul class="nav nav-second-level" id="p41">
		        		<shiro:hasPermission name="billSingleRechargePage:get"><li id="p41v1"><a href="${basePath }billSingleRechargePage">话费单号充值</a></li></shiro:hasPermission>
		        		<shiro:hasPermission name="billBatchRechargePage:get"><li id="p41v2"><a href="${basePath }billBatchRechargePage">话费批号充值</a></li></shiro:hasPermission>	        			        
		        		<shiro:hasPermission name="billWithParValueVolumeRechargePage:get"><li id="p41v2"><a href="${basePath }billWithParValueVolumeRechargePage">同面值话费批号充值</a></li></shiro:hasPermission>	        			        
		        	</ul>
	        	</li>
	        	</shiro:hasPermission>
	        	
	        	<shiro:hasPermission name="dataChargePage:get"> 
	        	<li id="p42v">
	        		<a href="index.html"> 
	        			<span class="nav-label">流量充值</span> <span class="fa arrow"></span>
	        		</a>
		        	<ul class="nav nav-second-level" id="p42">
		        		<shiro:hasPermission name="dataSingleRechargePage:get"><li id="p42v1"> <a href="${basePath }dataSingleRechargePage">话费单号充值</a></li></shiro:hasPermission>
		        		<shiro:hasPermission name="dataBatchRechargePage:get"><li id="p42v2"> <a href="${basePath }dataBatchRechargePage">话费批号充值</a></li></shiro:hasPermission>	        
		        	</ul>
	        	</li>
	        	</shiro:hasPermission>
	        </ul>
        </li>
        </shiro:hasPermission>
		
        <shiro:hasPermission name="agentPage:get">
        <li id="p5">      
        	<a href="index.html"><i class="fa fa-users"></i> <span class="nav-label">代理商</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p5u">
			    <shiro:hasPermission name="agentListPage:get"><li id="p51"> <a href="${basePath }agentListPage">代理商列表</a></li></shiro:hasPermission>
			    <shiro:hasPermission name="agentAccountChargeListPage:get"><li id="p52"><a href="${basePath }agentAccountChargeListPage">代理商订单扣款记录</a></li></shiro:hasPermission>
			    <shiro:hasPermission name="agentAccountChargeAuditPage:get"><li id="p53"><a href="${basePath }agentAccountChargeAuditPage">代理商加款审核</a></li></shiro:hasPermission>
			    <shiro:hasPermission name="billDiscountModelListPage:get"><li id="p54"> <a href="${basePath }billDiscountModelListPage">代理商话费折扣模板</a></li></shiro:hasPermission>
			    <shiro:hasPermission name="PaymentList:get"><li id="p55"> <a href="${basePath }PaymentList">代理商自助加款记录</a></li></shiro:hasPermission>
			    <li id="p56"><a href="${basePath }agentBillDiscountSet">代理商话费折扣上传</a></li>
			    <shiro:hasPermission name="agentProfit:get"><li id="p57"> <a href="${basePath}agentProfit">代理商利润</a></li></shiro:hasPermission>
	        </ul>
        </li>
        </shiro:hasPermission>
        
        <shiro:hasPermission name="providerPage:get">
         <li id="p6">      
        <a href="index.html"><i class="fa fa-chain"></i> <span class="nav-label">运营商</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p6u">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
		       <shiro:hasPermission name="providerListPage:get"> <li id="p61"><a href="${basePath }providerListPage">运营商列表</a></li></shiro:hasPermission>
		       <shiro:hasPermission name="providerPhysicalChannelListPage:get"> <li id="p63"><a href="${basePath }providerPhysicalChannelListPage">运营商物理通道</a></li></shiro:hasPermission>
	           <li id="p64"><a href="${basePath }providerBillDiscountUpload">运营商话费折扣上传</a></li>
		       <shiro:hasPermission name="providerAccountChargePage:get"><li id="p62"><a href="${basePath }providerAccountChargePage">运营商物理通道加款记录</a></li></shiro:hasPermission>
		      
	        </ul>
        </li></shiro:hasPermission>
        <shiro:hasPermission name="statementPage:get">
        <li id="p7">
        	<a href="index.html"><i class="fa fa-pie-chart"></i> <span class="nav-label">账单统计</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p7u">
	        <shiro:hasPermission name="providerBillStatementPage:get"><li id="p71"> <a href="${basePath }providerBillStatementPage" target="_blank">通道账单统计</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="agentBillStatementPage:get"><li id="p72"> <a href="${basePath }agentBillStatementPage" target="_blank">代理账单统计</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="receiptListPage:get"><li id="p73"> <a href="${basePath }receiptListPage" target="_blank">加款收据</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="checkBalancePage:get"><li id="p74"> <a href="${basePath }checkBalancePage" target="_blank">余额核查</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="FinancialReportPage:get"><li id="p75"> <a href="${basePath }FinancialReportPage" target="_blank">财务报表</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="additionalEstimate:get"><li id="p76"> <a href="${basePath }additionalEstimate" target="_blank">加款预估统计</a></li></shiro:hasPermission>
	        <shiro:hasPermission name="chartsPage:get"><li id="p77"> <a href="${basePath }dataChartsPage" target="_blank">图表统计</a></li></shiro:hasPermission>
	        </ul>
        </li> 
        </shiro:hasPermission> 
        <shiro:hasPermission name="Payment:get">
        <li id="p8">
        	<a href="${basePath }Payment"><i class="fa fa-database"></i> <span class="nav-label">自助加款</span> <span class="fa arrow"></span></a>
        </li>
        </shiro:hasPermission> 
            
        <shiro:hasPermission name="otherPage:get"> 
        <li id="p9">
        	<a href="index.html"><i class="fa fa-pie-chart"></i> <span class="nav-label">其他</span> <span class="fa arrow"></span></a>
	        <ul class="nav nav-second-level" id="p9u">
	        	<shiro:hasPermission name="billBatchQueryPhonePage:get">
	        		<li id="p91"> <a href="${basePath }billBatchQueryPhonePage" target="_blank">批量查询余额</a></li>
	        	</shiro:hasPermission>
	        	<shiro:hasPermission name="marry:get">
	        		<li id="p92"> <a href="${basePath }marry" target="_blank">匹配不同</a></li>
	        	</shiro:hasPermission>
	        	<shiro:hasPermission name="shortcutPage:get">
	        		<li id="p93"> <a href="${basePath }shortcutPage" target="_blank">快捷操作</a></li>
	        	</shiro:hasPermission>
	        	<shiro:hasPermission name="batchModifyOrderStatus:get">
	        		<li id="p94"> <a href="${basePath }batchModifyOrderStatus" target="_blank">批量修改状态</a></li>
	        	</shiro:hasPermission>
	        </ul>
        </li> 
        </shiro:hasPermission>
    </ul><!-- end side-menu ul -->
</div><!-- end sidebar-collapse  -->
</nav>
<script>
		function updatePassword(id){
			var myBasePath=basePath+'userChangePassPage/'+id;
		    location.href=myBasePath;
		}
		
</script>