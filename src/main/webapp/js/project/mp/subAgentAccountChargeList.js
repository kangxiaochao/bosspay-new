var myTbContentId = 'mytbc1';
var myJqTbContentId = '#' + myTbContentId;
var myTbId = 'myt1';
var myJqTbId = '#' + myTbId;
var myPageId = 'myp1';
var myJqPageId = '#' + myPageId;

$(function() {
	$("#applyDate").val(laydate.now() + ' 00:00:00');
	$("#endDate").val(laydate.now(1) + ' 00:00:00');

	var t1 = $('<table></table>');
	t1.attr('id', myTbId);
	$(myJqTbContentId).append(t1);

	var p1 = $('<div></div>');
	p1.attr('id', myPageId);

	p1.insertAfter(t1);

	var data = $("#searchForm").serialize();
	$(myJqTbId).jqGrid({
		url : basePath + 'subAgentAccountChargeList?'+data,
		mtype : "GET",
		datatype : "json",
		caption : "下级代理商加款记录", //设置表标题
		page : 1,
		colNames : [ 'id', '代理商', '费用','操作前余额', '操作后余额', '状态','请求时间', '完成时间','备注'],
		colModel : [ {name : 'id',key : true,sortable : false,hidden : true},
			{name : 'name',width:'80',sortable : false},	//代理商
			{name : 'fee',width:'80',sortable : false}, 	//费用
			{name : 'balance_before',sortable : false}, 	//操作前余额
			{name : 'balance_after',sortable : false},	//操作后余额
			{name : 'status',sortable : false,width:'80',align: 'center',editoptions:{value:'1:加款;2:扣款'}},
			{name : 'apply_date',sortable : false,formatter:function(apply_date){
					var applyDate = apply_date.substring(0,23);
					return applyDate;
				}},	//请求时间
			{name : 'end_date',sortable : false,formatter:function(end_date){
					var endDate = end_date.substring(0,23);
					return endDate;
				}},//完成时间remark
			{name : 'remark',sortable : false},	//备注
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

	$('#name').focus();
	setTimeout(function() {
		$('.wrapper-content').removeClass('animated fadeInRight');
	}, 700);
	setSelectStyle($("#status"));
	message();
	setMyActive(5,9); //设置激活页
});

/**
 * 绑定日期控件
 */
laydate({
	elem : '#applyDate',// 目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式
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
	var data = $("#searchForm").serialize();
	console.log(data);
	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'subAgentAccountChargeList?'+data,
		postData : {},
		page : 1
	}).trigger("reloadGrid"); //重新载入
}

function dataReport(){
	var data = $("#searchForm").serialize();
	location.href = basePath + 'subAgentAccountChargeExport?' + data;
}