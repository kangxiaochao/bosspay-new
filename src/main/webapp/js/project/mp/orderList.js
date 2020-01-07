﻿var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
  $("#applyDate").val(laydate.now() + ' 00:00:00');
  $("#endDate").val(laydate.now(1) + ' 00:00:00');
var userId = $('#leftMenuDiv').attr('data-suId');
	$.ajax({
		type:'get',
		url :basePath + 'sysGetHasRole/'+userId,
		async: false,
		data:{},
		dataType: 'json',
		success:function (dt){
			var roleName;
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "批量"){
					break;
				}
			}
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "代理商"){
					break;
				}
			}
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "渠道"){
					break;
				}
			}
			for(var i in dt){
				roleName = dt[i]["srName"];
				if(roleName == "超级管理员"){
					break;
				}
			}
			if(roleName == "代理商" || roleName == "批量"){
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid(
						{
							url : basePath + 'orderList',
							mtype : "GET",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : [ 'id', '代理商订单号', '代理商名称',
									'运营商', '手机号',  '地区', '话费包','开始时间','状态','结束时间' ,'类型', '原价(元)', 
									'代理商折扣价(元)'],
							colModel : [ {
								name : 'id',
								key : true,
								sortable : false,
								hidden : true
							},{
								name : 'agentOrderId',
								sortable : false,
								index : 'agentOrderId'
							}, {
								name : 'agent',
								sortable : false,
								width : '100',
								index : 'agentId'
							}, {
								name : 'provider',
								sortable : false,
								width : '60',
								index : 'providerId'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone',
								width : '90'
							}, {
								name : 'province',
								sortable : false,
								index : 'province',
								width : '45'
							}, {
								name : 'productName',
								sortable : false,
								index : 'productName',
								width : '40',
								formatter : fromatProductName
							}, {
								name : 'applyDate',
								sortable : false,
								width : '140',
								index : 'applyDate'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '60',
								formatter : formatStatus
							}, {
								name : 'endDate',
								sortable : false,
								width : '140',
								index : 'endDate'
							}, {
								name : 'bizType',
								sortable : false,
								index : 'bizType',
								width : '35',
								formatter : formatType
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '55'
							}, {
								name : 'agentFee',
								sortable : false,
								index : 'agentFee',
								width : '100',
								formatter : fromatAgentFee
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
							pager : myJqPageId
						});
			}else if(roleName == "渠道") {
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid(
						{
							url : basePath + 'orderList',
							mtype : "GET",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : [ 'id', '上家订单号','平台订单号', '代理商订单号', '代理商名称','运营商','物理通道', '手机号',
							             '话费包', '地区','开始时间','入库时间', '状态', '结束时间','消耗时间', 
							             '原价(元)','代理商折扣价(元)','代理商折扣','归属渠道' , '类型', '复冲次数','结果描述'],
							colModel : [ {
								name : 'id',
								key : true,
								sortable : false,
								hidden : true
							}, {
								name:"providerOrderId",
								sortable:false,
								index:"providerOrderId"
							},{
								name : 'orderId',
								sortable : false,
								index : 'orderId'
							}, {
								name : 'agentOrderId',
								sortable : false,
								index : 'agentOrderId'
							}, {
								name : 'agent',
								sortable : false,
								width : '100',
								index : 'agentId'
							}, {
								name : 'provider',
								sortable : false,
								width : '60',
								index : 'providerId'
							}, {
								name : 'dispatcherProvider',
								sortable : false,
								width : '60',
								index : 'dispatcherProviderId'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone',
								width : '90'
							}, {
								name : 'productName',
								sortable : false,
								index : 'productName',
								width : '40',
								formatter : fromatProductName
							}, {
								name : 'province',
								sortable : false,
								index : 'province',
								width : '45'
							}, {
								name : 'applyDate',
								sortable : false,
								width : '140',
								index : 'applyDate'
							}, {
								name : 'createDate',
								sortable : false,
								width : '140',
								index : 'createDate'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '60',
								formatter : formatStatus
							}, {
								name : 'endDate',
								sortable : false,
								width : '140',
								index : 'endDate'
							}, {
								name : 'consumedTime',
								sortable : false,
								index : 'consumedTime',
								width : '70',
								formatter : countTime
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '55'
							}, {
								name : 'agentFee',
								sortable : false,
								index : 'agentFee',
								width : '100',
								formatter : fromatAgentFee
							}, {
								name : 'agentDiscount',
								sortable : false,
								index : 'agentDiscount',
								width : '50'
							},{
								name : 'channelPerson',
								sortable : false,
								width : '90',
								index : 'channelPerson'
							}, {
								name : 'bizType',
								sortable : false,
								index : 'bizType',
								width : '35',
								formatter : formatType
							}, {
								name : 'dealCount',
								sortable : false,
								index : 'dealCount',
								width : '55'
							}, {
								name : 'resultCode',
								sortable : false,
								index : 'resultCode',
								hidden:true
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
							pager : myJqPageId,
							ondblClickRow:function (rowid,iRow,iCol,e){
						    	  var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
						    	  layer.open({
						    		  skin: 'layui-layer-molv',
						    		  type: 0,
						    		  title: '订单详情',
						    		  closeBtn: false,
						    		  area: '800px;',
						    		  shade: 0.8,
						    		  id: 'LAY_layuipro', //设定一个id，防止重复弹出
						    		  resize: false,	//是否允许拉伸
						    		  btn: ['关闭'],
						    		  btnAlign: 'c',
						    		  moveType: 1, //拖拽模式，0或者1
						    		  content: showOrderDetail(rowData),
						    	  });
						      }
						});
			}else if(roleName == "超级管理员") {
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid(
						{
							url : basePath + 'orderList',
							mtype : "GET",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : [ 'id', '上家订单号','平台订单号', '代理商订单号', '代理商名称','运营商','物理通道', '手机号',
							             '话费包', '地区','通道折扣价(元)','通道折扣','开始时间','入库时间', '状态', '结束时间','消耗时间', 
							             '原价(元)','代理商折扣价(元)','代理商折扣','归属渠道' , '类型', '复冲次数','结果描述'],
							colModel : [ {
								name : 'id',
								key : true,
								sortable : false,
								hidden : true
							}, {
								name:"providerOrderId",
								sortable:false,
								index:"providerOrderId"
							},{
								name : 'orderId',
								sortable : false,
								index : 'orderId'
							}, {
								name : 'agentOrderId',
								sortable : false,
								index : 'agentOrderId'
							}, {
								name : 'agent',
								sortable : false,
								width : '100',
								index : 'agentId'
							}, {
								name : 'provider',
								sortable : false,
								width : '60',
								index : 'providerId'
							}, {
								name : 'dispatcherProvider',
								sortable : false,
								width : '60',
								index : 'dispatcherProviderId'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone',
								width : '90'
							}, {
								name : 'productName',
								sortable : false,
								index : 'productName',
								width : '40',
								formatter : fromatProductName
							}, {
								name : 'province',
								sortable : false,
								index : 'province',
								width : '45'
							}, {
								name : 'proviceFee',
								sortable : false,
								index : 'proviceFee',
								width : '90',
								formatter : fromatProviceFee
							}, {
								name : 'providerDiscount',
								sortable : false,
								index : 'providerDiscount',
								width : '50'
							}, {
								name : 'applyDate',
								sortable : false,
								width : '140',
								index : 'applyDate'
							}, {
								name : 'createDate',
								sortable : false,
								width : '140',
								index : 'createDate'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '60',
								formatter : formatStatus
							}, {
								name : 'endDate',
								sortable : false,
								width : '140',
								index : 'endDate'
							}, {
								name : 'consumedTime',
								sortable : false,
								index : 'consumedTime',
								width : '70',
								formatter : countTime
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '55'
							}, {
								name : 'agentFee',
								sortable : false,
								index : 'agentFee',
								width : '100',
								formatter : fromatAgentFee
							}, {
								name : 'agentDiscount',
								sortable : false,
								index : 'agentDiscount',
								width : '50'
							},{
								name : 'channelPerson',
								sortable : false,
								width : '90',
								index : 'channelPerson'
							}, {
								name : 'bizType',
								sortable : false,
								index : 'bizType',
								width : '35',
								formatter : formatType
							}, {
								name : 'dealCount',
								sortable : false,
								index : 'dealCount',
								width : '55'
							}, {
								name : 'resultCode',
								sortable : false,
								index : 'resultCode',
								hidden:true
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
							pager : myJqPageId,
							ondblClickRow:function (rowid,iRow,iCol,e){
						    	  var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
						    	  layer.open({
						    		  skin: 'layui-layer-molv',
						    		  type: 0,
						    		  title: '订单详情',
						    		  closeBtn: false,
						    		  area: '800px;',
						    		  shade: 0.8,
						    		  id: 'LAY_layuipro', //设定一个id，防止重复弹出
						    		  resize: false,	//是否允许拉伸
						    		  btn: ['关闭'],
						    		  btnAlign: 'c',
						    		  moveType: 1, //拖拽模式，0或者1
						    		  content: showOrderDetail(rowData),
						    	  });
						      }
						});
			} else {
				var t1 = $('<table></table>');
				t1.attr('id', myTbId);
				$(myJqTbContentId).append(t1);

				var p1 = $('<div></div>');
				p1.attr('id', myPageId);

				p1.insertAfter(t1);

				$(myJqTbId).jqGrid(
						{
							url : basePath + 'orderList',
							mtype : "GET",
							datatype : "json",
							caption : "订单列表", // 设置表标题
							page : 1,
							colNames : [ 'id', '上家订单号','平台订单号', '代理商订单号', '代理商名称','运营商','物理通道', '手机号',
							             '话费包', '地区','开始时间','入库时间', '状态', '结束时间','消耗时间', 
							             '原价(元)','归属渠道' , '类型', '复冲次数','结果描述'],
							colModel : [ {
								name : 'id',
								key : true,
								sortable : false,
								hidden : true
							}, {
								name:"providerOrderId",
								sortable:false,
								index:"providerOrderId"
							},{
								name : 'orderId',
								sortable : false,
								index : 'orderId'
							}, {
								name : 'agentOrderId',
								sortable : false,
								index : 'agentOrderId'
							}, {
								name : 'agent',
								sortable : false,
								width : '100',
								index : 'agentId'
							}, {
								name : 'provider',
								sortable : false,
								width : '60',
								index : 'providerId'
							}, {
								name : 'dispatcherProvider',
								sortable : false,
								width : '60',
								index : 'dispatcherProviderId'
							}, {
								name : 'phone',
								sortable : false,
								index : 'phone',
								width : '90'
							}, {
								name : 'productName',
								sortable : false,
								index : 'productName',
								width : '40',
								formatter : fromatProductName
							}, {
								name : 'province',
								sortable : false,
								index : 'province',
								width : '45'
							}, {
								name : 'applyDate',
								sortable : false,
								width : '140',
								index : 'applyDate'
							}, {
								name : 'createDate',
								sortable : false,
								width : '140',
								index : 'createDate'
							}, {
								name : 'status',
								sortable : false,
								index : 'status',
								width : '60',
								formatter : formatStatus
							}, {
								name : 'endDate',
								sortable : false,
								width : '140',
								index : 'endDate'
							}, {
								name : 'consumedTime',
								sortable : false,
								index : 'consumedTime',
								width : '70',
								formatter : countTime
							}, {
								name : 'fee',
								sortable : false,
								index : 'fee',
								width : '55'
							}, {
								name : 'channelPerson',
								sortable : false,
								width : '90',
								index : 'channelPerson'
							}, {
								name : 'bizType',
								sortable : false,
								index : 'bizType',
								width : '35',
								formatter : formatType
							}, {
								name : 'dealCount',
								sortable : false,
								index : 'dealCount',
								width : '55'
							}, {
								name : 'resultCode',
								sortable : false,
								index : 'resultCode',
								hidden:true
							} ],
							// width: 750,
							height : 'auto',
							multiselect:true,//多选框
							multiboxonly:false,//为true时是单选
							autowidth : true,
							shrinkToFit : false,
							hidegrid : false, // 隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
							autoScroll : false,
							rowNum : myRowNum,
							rowList : myRowList,
							viewrecords : true,//显示总记录数
							rownumbers : true,
							pager : myJqPageId,
							ondblClickRow:function (rowid,iRow,iCol,e){
						    	  var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
						    	  layer.open({
						    		  skin: 'layui-layer-molv',
						    		  type: 0,
						    		  title: '订单详情',
						    		  closeBtn: false,
						    		  area: '800px;',
						    		  shade: 0.8,
						    		  id: 'LAY_layuipro', //设定一个id，防止重复弹出
						    		  resize: false,	//是否允许拉伸
						    		  btn: ['关闭'],
						    		  btnAlign: 'c',
						    		  moveType: 1, //拖拽模式，0或者1
						    		  content: showOrderDetail(rowData),
						    	  });
						      }
						});
			}
		}
	});
	

	$('#suName').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
  setMyActive(3,1); //设置激活页
  
  setSelectStyle($("#status"));
  setSelectStyle($("#bizType"));
});


/* 绑定日期控件 */
laydate({
	elem : '#applyDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
							// '#id .class'
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime : true, // 是否开启时间选择
});

/* 绑定日期控件 */
laydate({
	elem : '#endDate', // 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
						// '#id .class'
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', // 日期格式
	istime : true, // 是否开启时间选择
});


function formatType(cellvalue) {
	if (cellvalue == "1") {
		return "流量";
	}
	if (cellvalue == "2") {
		return "话费";
	}
}

function showOrderDetail(rowData){
	var orderDetailHtml='<table width="100%" class="table table-striped table-bordered table-hover dataTables-example" >'+
						'    <tr class="gradeX">'+
						'        <td width="150px;" align="right">平台订单号</td>'+
						'        <td>'+rowData.orderId+'</td>'+
						'        <td width="150px;" align="right">代理商订单号</td>'+
						'        <td>'+rowData.agentOrderId+'</td>'+
						'  </tr>'+
						'    <tr class="gradeC">'+
						'        <td align="right">代理商</td>'+
						'        <td>'+rowData.agent+'</td>'+
						'        <td align="right">物理通道</td>'+
						'        <td>'+rowData.dispatcherProvider+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">运营商</td>'+
						'        <td>'+rowData.provider+'</td>'+
						'        <td align="right">状态</td>'+
						'        <td style="color:red">'+rowData.status+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">手机号</td>'+
						'        <td>'+rowData.phone+'</td>'+
						'        <td align="right">类型</td>'+
						'        <td>'+rowData.bizType+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">商品名称</td>'+
						'        <td>'+rowData.productName+'</td>'+
						'        <td align="right">面值(元)</td>'+
						'        <td>'+rowData.fee+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">上家折扣价(元)</td>'+
						'        <td>'+rowData.proviceFee+'</td>'+
						'        <td align="right">客户折扣价(元)</td>'+
						'        <td>'+rowData.agentFee+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">复冲次数</td>'+
						'        <td>'+rowData.dealCount+'</td>'+
						'        <td align="right">请求时间</td>'+
						'        <td>'+rowData.applyDate+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">处理时间</td>'+
						'        <td>'+rowData.endDate+'</td>'+
						'        <td align="right">消耗时间</td>'+
						'        <td>'+rowData.consumedTime+'</td>'+
						'    </tr>'+
						'    <tr class="gradeA">'+
						'        <td align="right">结果描述</td>'+
						'        <td colspan="3" >'+rowData.resultCode+'</td>'+
						'    </tr>'+
						'</table>';
	
	return orderDetailHtml;
}

// 订单参数查询
function orderParamQuery(){
	var ids=$(myJqTbId).jqGrid("getGridParam","selarrrow");
	var phone = "";
	var orderId = "";
	if(ids.length > 0){
		var rowid=$(myJqTbId).jqGrid("getGridParam","selrow");
		var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
		phone = rowData.phone;
		orderId = rowData.orderId;
	}
	layer.open({
		  skin: 'layui-layer-molv',
		  type: 2,
		  title: '订单参数查询',
		  closeBtn: false,
		  area: ['800px', '500px'],
		  shade: 0.8,
		  id: 'LAY_layuipro', //设定一个id，防止重复弹出
		  resize: false,	//是否允许拉伸
		  btn: ['关闭'],
		  btnAlign: 'c',
		  moveType: 1, //拖拽模式，0或者1
		  content: 'orderParamPage?phone='+phone+'&orderId='+orderId,
	  });
}

function fromatProductName(cellvalue, options, rowObject) {
	var bizType = rowObject['bizType'];
	var fee = rowObject['fee'];
	var value = rowObject['value'];
	if (bizType == "1") {
		return fee+'元'+value+'MB';
	}else{
		return fee+'元';
	}
}

function fromatProviceFee(cellvalue, options, rowObject) {
	var fee = rowObject['fee'];
	var providerDiscount = rowObject['providerDiscount'];
	var value = fee * providerDiscount;
	if(isNaN(value)){
		return '';
	}else{
		return value.toFixed(3);
	}
}

function fromatAgentFee(cellvalue, options, rowObject) {
	var fee = rowObject['fee'];
	var agentDiscount = rowObject['agentDiscount'];
	var value = fee * agentDiscount;
	if(isNaN(value)){
		return '';
	}else{
		return value.toFixed(3);
	}
}

function formatStatus(cellvalue) {
	if (cellvalue == "1") {
		return "提交成功";
	} else if (cellvalue == "2") {
		return "提交失败";
	} else if (cellvalue == "3") {
		return "充值成功";
	} else if (cellvalue == "4") {
		return "充值失败";
	} else {
		return "处理中";
	}
}

function search() {
	var orderId = $('#orderId').val();
	var agentOrderId = $('#agentOrderId').val();
	var providerOrderId = $('#providerOrderId').val();
	var agentName = $('#agentName').val();
	var phone = $('#phone').val();
	var dispatcherProviderName = $('#dispatcherProviderName').val();
	var providerName = $('#providerName').val();
	var status = $('#status').val();
	var bizType = $('#bizType').val();
	var applyDate = $('#applyDate').val();
	var endDate = $('#endDate').val();
	var channelPerson = $("#channelPerson").val();
	var provinceCode = $("#provinceCode").val();
	
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'orderList',
		postData : {
			'orderId' : orderId,
			'agentName' : agentName,
			'providerOrderId' : providerOrderId,
			'agentOrderId' : agentOrderId,
			'phone' : phone,
			'dispatcherProviderName' : dispatcherProviderName,
			'providerName' : providerName,
			'status' : status,
			'bizType' : bizType,
			'applyDate' : applyDate,
			'endDate' : endDate,
			'channelPerson':channelPerson,
			'provinceCode':provinceCode
		},// 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}

// 导出订单报表
function orderReport() {
	var bizType = $('#bizType').val();
	var applyDate = $('#applyDate').val();
	var endDate = $('#endDate').val();
	if(applyDate == ""){
		outMessage('warning','请选择开始时间！','友情提示');
		return false;
	}
	if(endDate == ""){
		outMessage('warning','请选择结束时间！','友情提示');
		return false;
	}
	if(bizType == ""){
		outMessage('warning','请选择业务类型！','友情提示');
		return false;
	}
	outMessage('info','数据量较大,请耐心等候！','友情提示');
	var myDelUrl = basePath + 'orderReport?'+$("#orderForm").serialize();
	location.href= myDelUrl;
}

//——————————————————————————————订单状态修改按钮功能—————————————————————————————————————
function changeStatus(operation){
	var id = getSelectedIds();
	var suId = $('#leftMenuDiv').attr('data-suId'); 
	$.ajax({
		type:"GET",
		url:basePath+operation,
		data:{
			"id":id,
			"suId":suId
		},
		dataType:"json",
		success:function(data){
			var succnum = data.succnum;
			var failnum = data.failnum;
			var tianmaoNum = data.tianmaoNum;
			if(tianmaoNum>0){
				layer.alert('存在非天猫用户的订单，无法修改！');
//				outMessage('warning',,'友情提示');
			}else{
				var message = succnum+"条请求处理成功，"+failnum+"条请求处理失败";
				search();
				layer.msg(message,{icon: 1});
			}
			
		}
	});
}

function handCallback(){
	var id = getSelectedOrderIds();
	var suId = $('#leftMenuDiv').attr('data-suId'); 
	$.ajax({
		url :basePath+"agentCallback",
		type:'post',
		data:{
			orderIds:id,
			"suId":suId
		},
		success:function (dt){
			layer.alert(dt);
	    }
	});
}

//弹出框提示
function openDialog(type){
	layer.confirm("确定执行此项操作？", {
		  btn: ["确定","取消"] //按钮
		}, function(){
			var operation = "";
			if(type == 1){
				operation = "changeStatus";
			}else if(type == 2){
				operation = "refundForOrder";
			}else if(type == 3){
				operation = "reChargeForOrder";
			}else if(type == 4){
				handCallback();
			}
			if(operation != ""){
				changeStatus(operation);
			}
		}, function(){
			
		});
}

function openChangeStatusDialog(){
	layer.open({
		  type: 1,
		  title:false,
		  skin: 'layui-layer-rim', //加上边框
		  area: ['300px', '200px'], //宽高
		  content:'<form id="statusForm" action="changeStatus">'+
		  			'<br/><h4>&nbsp;&nbsp;&nbsp;订单需要置为的状态：</h4><br/>'+
		  			'<h1><input id="succ" type="radio" name="status" value="3"\><label for="succ" style="color:green">充值成功</label></h1>'+
		  			'<h1><input id="fail" type="radio" name="status" value="4"\><label for="fail" style="color:red">充值失败</label></h1>'+
		  		  '</from>',
		  btn: ["确定","取消"],
		  yes:function(){
			  var id = getSelectedIds();
			  var suId = $('#leftMenuDiv').attr('data-suId'); 
				$.ajax({
					type:"GET",
					url:basePath+"changeStatus?"+$("#statusForm").serialize(),
					data:{
						"id":id,
						"suId":suId
					},
					dataType:"json",
					success:function(data){
						var succnum = data.succnum;
						var failnum = data.failnum;
						var tianmaoSum = data.tianmaoSum;
						if(tianmaoSum>0){
							layer.alert('存在非天猫用户的订单，无法修改！');
//							outMessage('warning','存在非天猫用户的订单，无法修改！','友情提示');
						}else{
							var message = succnum+"条请求处理成功，"+failnum+"条请求处理失败";
							search();
							layer.msg(message,{icon: 1});
						}
						
					}
				});
		  },
		  btn2:function(){
			  
		  }
		});
}

//获取选中行的id
function getSelectedIds(){
	var ids = jQuery(myJqTbId).jqGrid("getGridParam", "selarrrow");
	var id = "";
	for(var i = 0 ; i < ids.length ; i++){
		id += ids[i]+",";
	}
	return id;
}

//获取选中行的orderId
function getSelectedOrderIds(){
	var ids = jQuery(myJqTbId).jqGrid("getGridParam", "selarrrow");
	var id = "";
	for(var i = 0 ; i < ids.length ; i++){
		var rowData = $(myJqTbId).jqGrid('getRowData',ids[i]);
		id += rowData.orderId+",";
	}
	return id;
}
//—————————————————————————————————————————————————————————————————————————————————