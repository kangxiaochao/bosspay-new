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
		url : basePath + 'agentProfitList?'+data,
		mtype : "GET",
		datatype : "json",
		caption : "代理商冲扣值记录列表", //设置表标题
		page : 1,
		colNames : [ '渠道专员', '笔数', '充值金额', '代理商折扣金额','供应商折扣金额', '利润', '客户状态','订单状态'],
		colModel : [ {name : 'suName',width:'80',sortable : false}, 
		             {name : 'count',width:'80',sortable : false},	
		             {name : 'rechargeAmount',sortable : false}, 	
		             {name : 'agentAmount',width:'80',sortable : false}, 
		             {name : 'supplierAmount',sortable : false}, 
		             {name : 'profit',sortable : false},
		             {name : 'customerStatus',sortable : false},
		             {name : 'orderStatus',sortable : false},
		           ],
		//width: 750,
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
	setSelectStyle($("#type"));
	setSelectStyle($("#status"));
	message();
    setMyActive(5,7); //设置激活页 
});

/*绑定日期控件*/
laydate({
	elem: '#applyDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});

/*绑定日期控件*/
laydate({
	elem: '#endDate',//目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	event: 'focus', 	//响应事件。如果没有传入event，则按照默认的click
	format: 'YYYY-MM-DD hh:mm:ss', //日期格式
	istime: true, //是否开启时间选择
});
/*查询*/
function search() {
	var data = $("#searchForm").serialize();

	$(myJqTbId).jqGrid('setGridParam', {
		url : basePath + 'agentProfitList?'+data,
		postData : {
		},  
		page : 1
	}).trigger("reloadGrid"); //重新载入
}

/*导出*/
function orderReport(){
	var data = $("#searchForm").serialize();
	location.href = basePath + 'agentProfitExport?' + data;
}