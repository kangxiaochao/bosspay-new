var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	setSelectStyle($("#state"));
	setSelectStyle($("#pay_type"));
	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	$(myJqTbId).jqGrid({
		url : basePath + 'queryByName',
		mtype : "GET",
		datatype : "json",
		caption : "代理商自助加款记录", //设置表标题
		page : 1,
		colNames : [ 'id', '代理商', '加款金额', '加款时间', '平台订单号', '操作IP', '状态', '商户订单号','付款方式'],
		colModel : [ {name : 'id',sortable : false,hidden : true}, 
		             {name : 'name',sortable : false,width:100,align: "center"},		
		             {name : 'add_money',sortable : false,width:80,align: "center"}, 	
		             {name : 'create_date',sortable : false,align: "center"},	
		             {name : 'transaction_id',sortable : false,align: "center"},
		             {name : 'user_ip',sortable : false,width:100,align: "center"},	
		             {name : 'state',sortable : false,width:100,align: "center"},	
		             {name : 'out_trade_no',sortable : false,align: "center"},
		             {name : 'pay_types',sortable : false,align: "center"}	
		           ],
		height : 'auto',
		multiselect:true,//多选框
		multiboxonly:true,//为true时是单选
		autowidth : true,
		shrinkToFit : true,
		hidegrid : false, //隐藏表格右上角的"展开/收缩jqGrid内容的小箭头"
		autoScroll : false,
		rowNum : myRowNum,
		rowList : myRowList,
		viewrecords : true,//显示总记录数
		rownumbers : true,
		pager : myJqPageId
	});
	
});

/**
 * 绑定日期控件
 */
laydate({
	elem : '#createDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
	event : 'focus', // 响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime : true, //是否开启时间选择
});

/**
 * 绑定日期控件
 */
laydate({
	elem : '#endDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event : 'focus', //响应事件。如果没有传入event，则按照默认的click
	format : 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime : true, //是否开启时间选择
});

//根据条件查询信息
function search() {
	var createDate = $('#createDate').val();
	var endDate = $('#endDate').val();
	var state = $('#state').val();
	var agentName = $('#agentName').val();
	var transaction_id = $('#transaction_id').val();
	var pay_type = $('#pay_type').val();
	
	
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'queryByName',
		postData : {
			'createDate' : $.trim(createDate),
			'endDate' :$.trim(endDate),
			'state' :$.trim(state),
			'agentName' :$.trim(agentName),
			'transaction_id' :$.trim(transaction_id),
			'pay_type':$.trim(pay_type)
		}, //发送话费 
		page : 1
	}).trigger("reloadGrid"); //重新载入
}

//生成付款二维码
function submit(type){
	var money = $("#money").val();
	var agentId = $("#agentId").val();
	if(money >= 100){
		$.ajax({
			type : "get",
			url : "WeChatPay",
			data:{'money':money,'agentId':agentId,'type': type},
			scriptCharset : 'utf-8',
			success : function(data) {
				$("#ds").html("<img width='200px' src='"+data+"'/>");
				$("#dt").html("<button type='button' onclick='submits()' class='btn btn-primary'>支付完成</button>");
				
			}
		});
	}else{
		outMessage('warning','加款金额低于一百,不予处理！','友情提示');
	}
}

//支付完成 提交加款请求
function submits(){
	var money = $("#money").val();
	var agentId = $("#agentId").val();
	$.ajax({
		type : "get",
		url : "addAgentMoney",
		data:{'money':money,'agentId':agentId},
		scriptCharset : 'utf-8',
		success : function(data) {
			if(data == 0){
				location.href="mainPage";
			}else{
				outMessage('warning','加款失败,如已付款成功请联系值班客服！','友情提示');
			}
		}
	});
}
