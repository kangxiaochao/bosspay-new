var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid(
			{
				url : basePath + 'orderPathRecordList',
				mtype : "GET",
				datatype : "json",
				caption : "订单流水", // 设置表标题
				page : 1,
				colNames : [ 'id','平台订单号', '代理商订单号', '代理商', '物理通道',
						'运营商', '状态', '手机号', '类型', '商品名称', '面值(元)', 
						'上家折扣价(元)','客户折扣价(元)',
						'复冲次数','请求时间', '处理时间','消耗时间','结果描述' ],
				colModel : [ {
					name : 'id',
					key : true,
					sortable : false,
					hidden : true
				}, {
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
					name : 'dispatcherProvider',
					sortable : false,
					width : '100',
					index : 'dispatcherProviderId'
				}, {
					name : 'provider',
					sortable : false,
					width : '100',
					index : 'providerId'
				}, {
					name : 'status',
					sortable : false,
					index : 'status',
					width : '70',
					formatter : formatStatus
				}, {
					name : 'phone',
					sortable : false,
					index : 'phone',
					width : '90'
				}, {
					name : 'bizType',
					sortable : false,
					index : 'bizType',
					width : '50',
					formatter : formatType
				}, {
					name : 'productName',
					sortable : false,
					index : 'productName',
					width : '120',
					formatter : fromatProductName
				}, {
					name : 'fee',
					sortable : false,
					index : 'fee',
					width : '75'
				}, {
					name : 'proviceFee',
					sortable : false,
					index : 'proviceFee',
					width : '135',
					formatter : fromatProviceFee
				}, {
					name : 'agentFee',
					sortable : false,
					index : 'agentFee',
					width : '135',
					formatter : fromatAgentFee
				}, {
					name : 'dealCount',
					sortable : false,
					index : 'dealCount',
					width : '55'
				}, {
					name : 'applyDate',
					sortable : false,
					index : 'applyDate'
				}, {
					name : 'endDate',
					sortable : false,
					index : 'endDate'
				}, {
					name : 'consumedTime',
					sortable : false,
					index : 'consumedTime',
					formatter : countTime
				}, {
					name : 'resultCode',
					sortable : false,
					index : 'resultCode',
					hidden:true
				} ],
				// width: 750,
				height : 'auto',
				multiselect:false,//多选框
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
			    		  title: '流水详情',
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
	
	$('#suName').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
  setMyActive(3,4); //设置激活页
  
  setSelectStyle($("#status"));
  setSelectStyle($("#bizType"));

  $("#applyDate").val(laydate.now() + ' 00:00:00');
  $("#endDate").val(laydate.now(1) + ' 00:00:00');
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
		return "话费";
	}
	if (cellvalue == "2") {
		return "话费";
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
		return "订单异常";
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

//订单参数查询
function orderParamQuery(){
	var ids=$(myJqTbId).jqGrid("getGridParam","selarrrow");
	var phone = "";
	var orderId = "";
	if(ids.length > 0){
		var rowid=$(myJqTbId).jqGrid("getGridParam","selrow");
		var rowData = $(myJqTbId).jqGrid('getRowData',rowid);
		phone = rowData.phone;
		agentOrderId = rowData.agentOrderId;
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
		  content: 'orderPathRecordParamPage?phone='+phone+'&agentOrderId='+agentOrderId,
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
		return value.toFixed(2);
	}
}

function fromatAgentFee(cellvalue, options, rowObject) {
	var fee = rowObject['fee'];
	var agentDiscount = rowObject['agentDiscount'];
	var value = fee * agentDiscount;
	if(isNaN(value)){
		return '';
	}else{
		return value.toFixed(2);
	}
}

function search() {
	var mpOrderId = $('#mpOrderId').val();
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

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'orderPathRecordList',
		postData : {
			'mpOrderId':mpOrderId,
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
			'endDate' : endDate
		},// 发送数据
		page : 1
	}).trigger("reloadGrid"); // 重新载入
}


