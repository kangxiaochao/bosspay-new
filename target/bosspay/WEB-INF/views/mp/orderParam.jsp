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

<title>index</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<c:import url="../head.jsp" />

</head>

<body>
	<div class="gray-bg wrapper animated ecommerce">
		<div class="ibox-content m-b-sm border-bottom">
			<form>
			    <div class="row">
			        <div class="col-sm-6">
			            <div class="form-group">
			                <label class="control-label" for="">代理商订单号</label>
			                <input type="text" id="agentOrderId" name="agentOrderId" value="${orderParam.agentOrderId }" placeholder="请输入平台订单号" class="form-control" maxlength="50">
			            </div>
			        </div>
			        <div class="col-sm-6" >
			            <div class="form-group">
			                <label class="control-label" for="">手机号</label>
			                <input type="tel" class="form-control" name="phone" id="phone" value="${orderParam.phone }" placeholder="请输入手机号" data-mask="999 9999 9999">
			            	<input type="hidden" id="orderType" value="${orderType }">
			            </div>
			        </div>
			    </div>
			    <div class="row">
			        <div class="col-md-12">
	                   	<div class="text-center">
	                   		<br/>
	                   		<button type="reset" class="btn btn-warning">重置</button>
	                   		<button type="button" class="btn btn-danger" onclick="orderParamQuery()">查询</button>
	                   	</div>
					</div>
				</div>
			</form>
		</div>
		<div class="ibox-content m-b-sm border-bottom">
			<div class="row">
				<div class="col-sm-12">
					<table width="100%" class="table table-striped table-bordered table-hover dataTables-example" >
					  <tr class="gradeC">
					      <td align="right" width="20%">手机号</td>
					      <td><lable id="phoneLable">${orderParam.phone }</lable></td>
					      <td align="right" width="20%">归属地</td>
					      <td><lable id="areaLable">${orderParam.provinceCode } ${orderParam.cityCode }</lable></td>
					  </tr>
					  <tr class="gradeC">
					      <td align="right">代理商</td>
					      <td><lable id="agentNameLable">${orderParam.agentName }</lable></td>
					      <td align="right">代理商折扣</td>
					      <td><lable id="agentDiscountLable">${orderParam.agentDiscount }</lable></td>
					  </tr>
					  <tr class="gradeA">
					      <td align="right">运营商</td>
					      <td><lable id="providerNameLable">${orderParam.providerName }</lable></td>
					      <td align="right">通道组</td>
					      <td><lable id="groupNameLable">${orderParam.groupName }</lable></td>
					  </tr>
					  <tr class="gradeA">
					      <td align="right">能否充值</td>
					      <td><lable id="yesOrNoLable">${orderParam.yesOrNo }</lable></td>
					      <td align="right">产品包</td>
					      <td><lable id="pkgNameLable">${orderParam.pkg.name }</lable></td>
					  </tr>
					  <tr class="gradeA">
					      <td align="right">可用通道</td>
					      <td colspan="3">
					      	<lable id="chanleNamesLable">
					      		<c:forEach items="${orderParam.dispatcherParam }" var="orderParam" varStatus="obj">
					      			[${orderParam.province_code }-${orderParam.name }-${orderParam.discount}]&nbsp;&nbsp;
					      		</c:forEach>
					      	</lable>
					      </td>
					  </tr>
					</table>
				</div>
			</div>
		</div>
	</div>
	<c:import url="../foot.jsp" />
	<script type="text/javascript" charset="UTF-8" src="${basePath }js/project/mp/orderParam.js"></script>
</body>
</html>